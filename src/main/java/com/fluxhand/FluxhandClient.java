package com.fluxhand;

import com.fluxhand.config.FluxhandConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class FluxhandClient implements ClientModInitializer {
    public static final String MOD_ID = "fluxhand";

    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        FluxhandConfig.load();

        // En 1.21.8 la categoría de la tecla es un String (cambia en 1.21.9+).
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fluxhand.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.fluxhand"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                FluxhandConfig cfg = FluxhandConfig.get();
                cfg.enabled = !cfg.enabled;
                FluxhandConfig.save();
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("Fluxhand: " + (cfg.enabled ? "ON" : "OFF")), true);
                }
            }
        });
    }
}
