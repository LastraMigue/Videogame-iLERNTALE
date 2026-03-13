package equipoilerntale.view.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;

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

public class FinalVideoScreen extends JPanel {

    private MainFrame mainFrame;
    private JFXPanel jfxPanel;
    private MediaPlayer mediaPlayer;

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
    public void playVideo() {
        Platform.runLater(() -> {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                }

                // Ruta del video final
                File archivoVideo = new File("ilerntale/src/main/resources/vid/final.mp4");
                String uriVideo = archivoVideo.toURI().toString();

                Media media = new Media(uriVideo);
                mediaPlayer = new MediaPlayer(media);
                MediaView mediaView = new MediaView(mediaPlayer);

                // Creamos la Escena
                StackPane root = new StackPane();
                root.setStyle("-fx-background-color: black;"); // Fondo negro con CSS integrado
                root.getChildren().add(mediaView);

                Scene scene = new Scene(root);
                jfxPanel.setScene(scene);

                // Vinculamos el video al tamaño de la escena de JavaFX
                mediaView.fitWidthProperty().bind(scene.widthProperty());
                mediaView.fitHeightProperty().bind(scene.heightProperty());
                mediaView.setPreserveRatio(true);

                mediaPlayer.play();

                // Cuando termine el video, cambiamos a la pantalla del MENU
                mediaPlayer.setOnEndOfMedia(() -> {
                    SwingUtilities.invokeLater(() -> {
                        mainFrame.reiniciarJuego();
                        mainFrame.cambiarPantalla("MENU");
                    });
                });

            } catch (Exception ex) {
                System.err.println("Error al cargar el video final: " + ex.getMessage());
                ex.printStackTrace();
                // Si el video falla, saltamos al menú
                SwingUtilities.invokeLater(() -> {
                    mainFrame.reiniciarJuego();
                    mainFrame.cambiarPantalla("MENU");
                });
            }
        });
    }
}
