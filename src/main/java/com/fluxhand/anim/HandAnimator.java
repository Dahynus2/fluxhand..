package com.fluxhand.anim;

import com.fluxhand.config.FluxhandConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

/**
 * Calcula una "pose extra" (desplazamiento + rotación) para la mano en primera persona
 * y la aplica a la MatrixStack justo antes de que vanilla dibuje la mano/ítem.
 * Solo se usa desde el hilo de render, por eso los buffers son estáticos.
 */
public final class HandAnimator {
    // Posición base de la mano en vanilla (en el espacio de la cámara). Se usa como pivote.
    private static final float PIVOT_X = 0.56f;
    private static final float PIVOT_Y = -0.52f;
    private static final float PIVOT_Z = -0.72f;

    private static final float[] POSE = new float[Keyframes.CHANNELS]; // dx dy dz rx ry rz
    private static final float[] TMP = new float[Keyframes.CHANNELS];

    private static float sprintBlend = 0f;
    private static float sneakBlend = 0f;
    private static long lastNanos = System.nanoTime();

    private HandAnimator() {}

    /**
     * Aplica la animación a la pila de matrices.
     *
     * @return el progreso de swing que debe recibir el renderizado vanilla
     *         (0 si nosotros reemplazamos el swing de vanilla, o el original si no).
     */
    public static float apply(AbstractClientPlayerEntity player, float tickProgress, Hand hand,
                              float swing, ItemStack item, float equip, MatrixStack matrices) {
        if (item.isOf(Items.FILLED_MAP)) return swing; // los mapas usan su propio render

        FluxhandConfig cfg = FluxhandConfig.get();
        float k = cfg.intensity;

        Arm arm = hand == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        float side = arm == Arm.RIGHT ? 1f : -1f;

        if (hand == Hand.MAIN_HAND) updateBlends(player);

        java.util.Arrays.fill(POSE, 0f);
        float passSwing = swing;

        // --- Golpe / minado ---
        boolean usingThisHand = player.isUsingItem() && player.getActiveHand() == hand;
        if (cfg.swingAnimations && swing > 0f && !usingThisHand) {
            SwingStyle.of(item).frames.sample(swing, TMP);
            add(TMP, side, k);
            passSwing = 0f; // anulamos el swing de vanilla para que no se sume
        }

        // --- Balanceo al caminar / correr ---
        float speed = MathHelper.clamp(player.limbAnimator.getSpeed(tickProgress), 0f, 1f);
        float phase = player.limbAnimator.getPos(tickProgress) * 0.6662f
                + (hand == Hand.MAIN_HAND ? 0f : MathHelper.PI);
        if (cfg.walkSway && speed > 0.001f) {
            float walk = speed * k * (1f - sprintBlend * 0.4f);
            POSE[1] += 0.010f * MathHelper.sin(phase * 2f) * walk;                 // sube/baja
            POSE[2] += 0.012f * MathHelper.cos(phase) * walk;                      // adelante/atrás
            POSE[3] += 1.2f * MathHelper.cos(phase * 2f) * walk;                   // cabeceo
            POSE[5] += side * 2.0f * MathHelper.sin(phase) * walk;                 // balanceo lateral
        }

        // --- Pose de correr ---
        if (cfg.sprintPose && sprintBlend > 0.001f) {
            float b = sprintBlend * k;
            POSE[0] += side * 0.02f * b;
            POSE[1] += -0.04f * b;
            POSE[2] += 0.03f * b + 0.025f * MathHelper.sin(phase) * b;
            POSE[3] += -12f * b;
            POSE[5] += side * 6f * b;
        }

        // --- Pose al agacharse ---
        if (cfg.sneakPose && sneakBlend > 0.001f) {
            float b = sneakBlend * k;
            POSE[1] += -0.03f * b;
            POSE[2] += 0.02f * b;
            POSE[3] += 5f * b;
        }

        // --- Animación al equipar (equip: 0 = listo, 1 = completamente bajado) ---
        if (cfg.equipFlourish && equip > 0.001f) {
            float b = equip * k;
            POSE[0] += side * 0.08f * b;
            POSE[3] += 35f * b;
            POSE[5] += side * -25f * b;
        }

        // Aplicar: rotar alrededor de la posición de la mano, no alrededor de la cámara.
        float px = side * PIVOT_X;
        matrices.translate(px + POSE[0], PIVOT_Y + POSE[1], PIVOT_Z + POSE[2]);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(POSE[4]));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(POSE[3]));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(POSE[5]));
        matrices.translate(-px, -PIVOT_Y, -PIVOT_Z);

        return passSwing;
    }

    /** Suma una muestra de keyframes a la pose, invirtiendo lateral/rotaciones según el brazo. */
    private static void add(float[] s, float side, float k) {
        POSE[0] += s[0] * side * k;
        POSE[1] += s[1] * k;
        POSE[2] += s[2] * k;
        POSE[3] += s[3] * k;
        POSE[4] += s[4] * side * k;
        POSE[5] += s[5] * side * k;
    }

    /** Suaviza las transiciones de correr/agacharse con un seguimiento exponencial. */
    private static void updateBlends(AbstractClientPlayerEntity player) {
        long now = System.nanoTime();
        float dt = Math.min(0.1f, (now - lastNanos) / 1.0e9f);
        lastNanos = now;
        float rate = 1f - (float) Math.exp(-12f * dt);
        sprintBlend += ((player.isSprinting() ? 1f : 0f) - sprintBlend) * rate;
        sneakBlend += ((player.isSneaking() ? 1f : 0f) - sneakBlend) * rate;
    }
}
