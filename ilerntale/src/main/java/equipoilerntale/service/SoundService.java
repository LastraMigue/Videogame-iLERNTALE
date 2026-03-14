package equipoilerntale.service;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Servicio centralizado para la gestión de audio y sonido.
 * Proporciona soporte para música de fondo (BGM) en bucle y efectos de sonido (SFX).
 * Implementa el patrón Singleton.
 */
public class SoundService {
    /** Registrador de eventos. */
    private static final Logger LOG = Logger.getLogger(SoundService.class.getName());
    /** Instancia única del servicio. */
    private static SoundService instance;

    /** Clip actual utilizado para la música de fondo. */
    private Clip bgmClip;
    /** Ruta del archivo de audio cargado actualmente como BGM. */
    private String currentBGMPath;
    /** Nivel de volumen global (rango 0.0 a 1.0). */
    private float volume = 0.5f;

    /**
     * Constructor privado.
     */
    private SoundService() {
    }

    /**
     * Obtiene la instancia única de SoundService.
     * 
     * @return La instancia Singleton.
     */
    public static SoundService getInstance() {
        if (instance == null) {
            instance = new SoundService();
        }
        return instance;
    }

    /**
     * Ajusta el volumen maestro de todos los sonidos y lo aplica al clip de música actual.
     * 
     * @param volume Nivel de volumen entre 0.0 (silencio) y 1.0 (máximo).
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        if (bgmClip != null) {
            applyVolume(bgmClip);
        }
    }

    /**
     * Recupera el volumen global configurado.
     * 
     * @return Valor entre 0.0 y 1.0.
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Aplica el volumen actual a un clip de audio específico mediante su control maestro de ganancia.
     * 
     * @param clip El clip al que aplicar el cambio de volumen.
     */
    private void applyVolume(Clip clip) {
        try {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                if (gainControl != null) {
                    float dB = (float) (Math.log(volume <= 0.0001 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
                    gainControl.setValue(Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB)));
                }
            }
        } catch (Exception e) {
            LOG.warning("No se pudo ajustar el volumen del clip: " + e.getMessage());
        }
    }

    /**
     * Inicia la reproducción de una pista de música de fondo en bucle.
     * Si la pista solicitada ya está sonando, no se interrumpe.
     * 
     * @param path Ruta absoluta al recurso de audio.
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

            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);
            applyVolume(bgmClip);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();

            currentBGMPath = path;
            LOG.info("Reproduciendo BGM: " + path);
        } catch (Exception e) {
            LOG.severe("Error al reproducir BGM " + path + ": " + e.getMessage());
        }
    }

    /**
     * Detiene por completo la música de fondo y libera los recursos asociados.
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
     * Reproduce un efecto de sonido de corta duración una sola vez.
     * 
     * @param path Ruta al recurso de audio SFX.
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
            applyVolume(sfxClip);
            sfxClip.start();

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
