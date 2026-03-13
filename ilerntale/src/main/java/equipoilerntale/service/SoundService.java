package equipoilerntale.service;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * SERVICIO CENTRALIZADO PARA LA GESTIÓN DE SONIDO.
 * Permite reproducir música de fondo (BGM) en bucle y efectos de sonido (SFX).
 */
public class SoundService {
    private static final Logger LOG = Logger.getLogger(SoundService.class.getName());
    private static SoundService instance;

    private Clip bgmClip;
    private String currentBGMPath;
    private float volume = 0.5f; // Volumen predeterminado al 50%

    private SoundService() {
    }

    public static SoundService getInstance() {
        if (instance == null) {
            instance = new SoundService();
        }
        return instance;
    }

    /**
     * AJUSTA EL VOLUMEN GLOBAL (0.0 a 1.0).
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        if (bgmClip != null) {
            applyVolume(bgmClip);
        }
    }

    public float getVolume() {
        return volume;
    }

    private void applyVolume(Clip clip) {
        try {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl.Type.MASTER_GAIN).equals(null) ? null
                        : (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                if (gainControl != null) {
                    // Mapeo lineal de 0.0-1.0 a decibelios (-80dB a 6dB aprox)
                    float dB = (float) (Math.log(volume <= 0.0001 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
                    gainControl.setValue(Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB)));
                }
            }
        } catch (Exception e) {
            LOG.warning("No se pudo ajustar el volumen del clip: " + e.getMessage());
        }
    }

    /**
     * REPRODUCE MÚSICA DE FONDO EN BUCLE.
     * Si ya está sonando la misma música, no hace nada.
     * Si es una música diferente, detiene la anterior y empieza la nueva.
     */
    public void playBGM(String path) {
        if (path == null || path.equals(currentBGMPath)) {
            return;
        }

        stopBGM();

        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                LOG.warning("No se encontró el archivo de sonido: " + path);
                return;
            }

            // Usar BufferedInputStream para soporte de mark/reset requerido por
            // AudioInputStream
            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);
            applyVolume(bgmClip); // Aplicar volumen actual
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();

            currentBGMPath = path;
            LOG.info("Reproduciendo BGM: " + path);
        } catch (Exception e) {
            LOG.severe("Error al reproducir BGM " + path + ": " + e.getMessage());
        }
    }

    /**
     * DETIENE LA MÚSICA DE FONDO ACTUAL.
     */
    public void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
        bgmClip = null;
        currentBGMPath = null;
    }

    /**
     * REPRODUCE UN EFECTO DE SONIDO (UNA SOLA VEZ).
     */
    public void playSFX(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                LOG.warning("No se encontró el archivo SFX: " + path);
                return;
            }

            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip sfxClip = AudioSystem.getClip();
            sfxClip.open(audioStream);
            applyVolume(sfxClip); // Aplicar volumen actual
            sfxClip.start();

            // Cerrar el clip automáticamente cuando termine
            sfxClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    sfxClip.close();
                }
            });
        } catch (Exception e) {
            LOG.severe("Error al reproducir SFX " + path + ": " + e.getMessage());
        }
    }
}
