# Fluxhand (Minecraft 1.21.8, Fabric)

Mod cliente original con animaciones de primera persona:
- Golpe distinto por ítem: espada (tajo), hacha/pico/azada/maza (golpe cenital), pala (estocada), mano vacía (puñetazo), resto (golpe corto).
- Balanceo de manos al caminar/correr, pose al correr y al agacharse.
- Animación al equipar un ítem.
- Tecla J (configurable en Controles) para activar/desactivar.
- Config en `.minecraft/config/fluxhand.json` (activar/desactivar cada efecto e `intensity`).

## Compilar
**Opción recomendada (la más segura):**
1. Entra a https://fabricmc.net/develop/template, elige Minecraft 1.21.8, mappings Yarn, y descarga el proyecto plantilla.
2. Copia mi carpeta `src/` dentro de la plantilla (reemplaza `src/main/resources/fabric.mod.json` por el mío y borra los ejemplos de la plantilla: su mixin, su entrypoint y su `*.mixins.json`).
3. Ejecuta `./gradlew build` (Windows: `gradlew.bat build`) con JDK 21.
4. El jar queda en `build/libs/fluxhand-1.0.0.jar` (no uses el `-sources`). Ponlo en `mods/` junto con Fabric API.

**Opción alternativa:** usa los archivos de Gradle incluidos y un Gradle 8.14+ instalado (`gradle build`).

## Si algo falla
- Crash al iniciar con un error de Mixin sobre `renderFirstPersonItem`: pégame el log completo.
- Se ve algún movimiento raro: ajusta los números de `SwingStyle.java` (filas {tiempo, dx, dy, dz, rx, ry, rz}) o baja `intensity` en el JSON.
