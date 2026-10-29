package com.fluxhand.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Configuración simple en JSON: .minecraft/config/fluxhand.json */
public final class FluxhandConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static FluxhandConfig instance = new FluxhandConfig();

    public boolean enabled = true;
    public boolean swingAnimations = true;
    public boolean walkSway = true;
    public boolean sprintPose = true;
    public boolean sneakPose = true;
    public boolean equipFlourish = true;
    /** Multiplicador general de la intensidad (0.0 = nada, 1.0 = normal, 2.0 = exagerado). */
    public float intensity = 1.0f;

    public static FluxhandConfig get() {
        return instance;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("fluxhand.json");
    }

    public static void load() {
        Path p = path();
        if (Files.exists(p)) {
            try {
                FluxhandConfig loaded = GSON.fromJson(Files.readString(p), FluxhandConfig.class);
                if (loaded != null) instance = loaded;
            } catch (Exception e) {
                System.err.println("[Fluxhand] No se pudo leer la config, usando valores por defecto: " + e);
            }
        }
        save(); // escribe el archivo con los campos nuevos si faltaban
    }

    public static void save() {
        try {
            Files.writeString(path(), GSON.toJson(instance));
        } catch (IOException e) {
            System.err.println("[Fluxhand] No se pudo guardar la config: " + e);
        }
    }
}
