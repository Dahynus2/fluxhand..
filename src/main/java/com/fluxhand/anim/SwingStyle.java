package com.fluxhand.anim;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;

/**
 * Estilos de golpe. Filas: {tiempo, dx, dy, dz, rx, ry, rz}
 * dx = lateral (positivo = hacia afuera del brazo), dy = arriba, dz = adelante es NEGATIVO.
 * rx/ry/rz en grados. dx, ry y rz se invierten automáticamente para zurdos.
 */
public enum SwingStyle {
    /** Tajo diagonal. */
    SWORD(new float[][]{
        {0.00f,  0.00f,  0.00f,  0.00f,   0f,   0f,   0f},
        {0.18f,  0.14f,  0.10f,  0.06f,  18f, -10f, -22f},
        {0.42f, -0.20f, -0.10f, -0.14f, -38f,  38f,  20f},
        {0.62f, -0.10f, -0.05f, -0.06f, -14f,  16f,   8f},
        {1.00f,  0.00f,  0.00f,  0.00f,   0f,   0f,   0f}}),

    /** Golpe cenital: hachas, picos, azadas, maza. */
    CHOP(new float[][]{
        {0.00f,  0.00f,  0.00f,  0.00f,   0f, 0f,  0f},
        {0.22f,  0.04f,  0.16f,  0.10f,  38f, 0f, -8f},
        {0.45f, -0.02f, -0.14f, -0.20f, -62f, 0f,  4f},
        {0.65f,  0.00f, -0.05f, -0.08f, -20f, 0f,  0f},
        {1.00f,  0.00f,  0.00f,  0.00f,   0f, 0f,  0f}}),

    /** Estocada hacia abajo: palas. */
    SHOVEL(new float[][]{
        {0.00f,  0.00f,  0.00f,  0.00f,   0f,  0f,  0f},
        {0.20f,  0.03f,  0.08f,  0.08f,  20f,  0f, -6f},
        {0.45f, -0.04f, -0.12f, -0.18f, -45f,  6f,  8f},
        {0.70f, -0.01f, -0.04f, -0.06f, -12f,  2f,  2f},
        {1.00f,  0.00f,  0.00f,  0.00f,   0f,  0f,  0f}}),

    /** Puñetazo con la mano vacía. */
    FIST(new float[][]{
        {0.00f,  0.00f,  0.00f,  0.00f,   0f,   0f,   0f},
        {0.15f,  0.03f,  0.02f,  0.10f,   8f,  -6f,  -6f},
        {0.38f, -0.10f,  0.06f, -0.40f, -14f,  14f,  10f},
        {0.60f, -0.04f,  0.02f, -0.12f,  -4f,   4f,   3f},
        {1.00f,  0.00f,  0.00f,  0.00f,   0f,   0f,   0f}}),

    /** Golpe corto para cualquier otro ítem. */
    GENERIC(new float[][]{
        {0.00f,  0.00f,  0.00f,  0.00f,   0f, 0f,  0f},
        {0.20f,  0.02f,  0.04f,  0.05f,  10f, 0f, -4f},
        {0.42f, -0.04f, -0.04f, -0.16f, -28f, 8f,  5f},
        {1.00f,  0.00f,  0.00f,  0.00f,   0f, 0f,  0f}});

    public final Keyframes frames;

    SwingStyle(float[][] rows) {
        this.frames = new Keyframes(rows);
    }

    public static SwingStyle of(ItemStack stack) {
        if (stack.isEmpty()) return FIST;
        if (stack.isIn(ItemTags.SWORDS)) return SWORD;
        if (stack.isIn(ItemTags.AXES) || stack.isIn(ItemTags.PICKAXES)
                || stack.isIn(ItemTags.HOES) || stack.isOf(Items.MACE)) return CHOP;
        if (stack.isIn(ItemTags.SHOVELS)) return SHOVEL;
        return GENERIC;
    }
}
