package com.fluxhand.mixin.client;

import com.fluxhand.anim.HandAnimator;
import com.fluxhand.config.FluxhandConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    /**
     * Envuelve el render de la mano/ítem en primera persona (Minecraft 1.21.8, mappings Yarn).
     * push/pop garantiza que nuestra transformación no "se filtre" a la otra mano.
     */
    @WrapMethod(method = "renderFirstPersonItem")
    private void fluxhand$renderFirstPersonItem(
            AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand,
            float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, Operation<Void> original) {

        if (!FluxhandConfig.get().enabled) {
            original.call(player, tickProgress, pitch, hand, swingProgress, item, equipProgress,
                    matrices, vertexConsumers, light);
            return;
        }

        matrices.push();
        float swing = HandAnimator.apply(player, tickProgress, hand, swingProgress, item,
                equipProgress, matrices);
        original.call(player, tickProgress, pitch, hand, swing, item, equipProgress,
                matrices, vertexConsumers, light);
        matrices.pop();
    }
}
