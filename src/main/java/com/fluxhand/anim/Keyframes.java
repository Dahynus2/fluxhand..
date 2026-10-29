package com.fluxhand.anim;

/**
 * Pista de keyframes con 6 canales: dx, dy, dz, rx, ry, rz.
 * Cada fila: {tiempo(0..1), dx, dy, dz, rx, ry, rz}.
 * Interpola con splines de Hermite (tangentes por diferencias finitas),
 * así el movimiento es suave y no "arranca y frena" en cada keyframe.
 */
public final class Keyframes {
    public static final int CHANNELS = 6;

    private final float[] times;
    private final float[][] values;
    private final float[][] tangents;

    public Keyframes(float[][] rows) {
        int n = rows.length;
        times = new float[n];
        values = new float[n][CHANNELS];
        tangents = new float[n][CHANNELS];
        for (int i = 0; i < n; i++) {
            times[i] = rows[i][0];
            System.arraycopy(rows[i], 1, values[i], 0, CHANNELS);
        }
        for (int i = 1; i < n - 1; i++) {
            float dt = times[i + 1] - times[i - 1];
            for (int c = 0; c < CHANNELS; c++) {
                tangents[i][c] = (values[i + 1][c] - values[i - 1][c]) / dt;
            }
        }
        // tangentes de los extremos = 0 (empieza y termina en reposo)
    }

    /** Escribe en out[0..5] el valor interpolado en t (0..1). */
    public void sample(float t, float[] out) {
        if (t <= times[0]) {
            System.arraycopy(values[0], 0, out, 0, CHANNELS);
            return;
        }
        int last = times.length - 1;
        if (t >= times[last]) {
            System.arraycopy(values[last], 0, out, 0, CHANNELS);
            return;
        }
        int i = 0;
        while (i < last - 1 && t > times[i + 1]) i++;

        float t0 = times[i], t1 = times[i + 1];
        float dt = t1 - t0;
        float u = (t - t0) / dt;
        float u2 = u * u, u3 = u2 * u;
        float h00 = 2 * u3 - 3 * u2 + 1;
        float h10 = u3 - 2 * u2 + u;
        float h01 = -2 * u3 + 3 * u2;
        float h11 = u3 - u2;
        for (int c = 0; c < CHANNELS; c++) {
            out[c] = h00 * values[i][c] + h10 * dt * tangents[i][c]
                   + h01 * values[i + 1][c] + h11 * dt * tangents[i + 1][c];
        }
    }
}
