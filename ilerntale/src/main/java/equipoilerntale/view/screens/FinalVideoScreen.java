package equipoilerntale.view.screens;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import equipoilerntale.view.MainFrame;

/**
 * Pantalla que reproduce el vídeo final del juego tras la victoria.
 * Utiliza JavaFX para la reproducción multimedia dentro de un entorno Swing.
 */
public class FinalVideoScreen extends JPanel {

    /** Referencia al marco principal para navegación tras el vídeo. */
    private MainFrame mainFrame;
    /** Panel puente para integrar contenido de JavaFX en Swing. */
    private JFXPanel jfxPanel;
    /** Reproductor de medios para el vídeo final. */
    private MediaPlayer mediaPlayer;

    /**
     * Constructor de la pantalla de vídeo final.
     * 
     * @param frame Referencia al marco principal.
     */
    public FinalVideoScreen(MainFrame frame) {
        this.mainFrame = frame;

        // Usamos BorderLayout para que el panel de video ocupe todo el espacio
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Inicializamos el panel puente de JavaFX y lo añadimos al centro
        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);

        // Previene que JavaFX se cierre cuando termina el video
        Platform.setImplicitExit(false);
    }

    // Este método es llamado por el MainFrame o donde se necesite
    /**
     * Inicia la reproducción del vídeo final en el hilo de JavaFX.
     * Configura la finalización para reiniciar el juego y volver al menú.
     */
    public void playVideo() {
        Platform.runLater(() -> {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                }

                java.net.URL resource = getClass().getResource("/vid/final.mp4");
                if (resource == null) {
                    throw new java.io.FileNotFoundException("No se encontró el video final en resources");
                }
                String uriVideo = resource.toExternalForm();

                Media media = new Media(uriVideo);
                mediaPlayer = new MediaPlayer(media);
                MediaView mediaView = new MediaView(mediaPlayer);

                // Creamos la Escena
                StackPane root = new StackPane();
                root.setStyle("-fx-background-color: black;");
                root.getChildren().add(mediaView);

                Scene scene = new Scene(root);
                jfxPanel.setScene(scene);

                // Vinculamos el video al tamaño de la escena de JavaFX
                mediaView.fitWidthProperty().bind(scene.widthProperty());
                mediaView.fitHeightProperty().bind(scene.heightProperty());
                mediaView.setPreserveRatio(true);

                mediaPlayer.play();

                mediaPlayer.setOnEndOfMedia(() -> {
                    SwingUtilities.invokeLater(() -> {
                        mainFrame.reiniciarJuego();
                        mainFrame.cambiarPantalla("MENU");
                    });
                });

            } catch (Exception ex) {
                System.err.println("Error al cargar el video final: " + ex.getMessage());
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    mainFrame.reiniciarJuego();
                    mainFrame.cambiarPantalla("MENU");
                });
            }
        });
    }
}
