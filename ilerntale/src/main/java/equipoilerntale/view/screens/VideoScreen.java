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

public class VideoScreen extends JPanel {

    private MainFrame mainFrame;
    private JFXPanel jfxPanel;

    public VideoScreen(MainFrame frame) {
        this.mainFrame = frame;

        // Usamos BorderLayout para que el panel de video ocupe todo el espacio
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Inicializamos el panel puente de JavaFX y lo añadimos al centro
        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);
    }

    // Este método ahora es llamado por el MainFrame
    public void playVideo() {
        Platform.runLater(() -> {
            try {
                // Ruta del video
                File archivoVideo = new File("ilerntale/src/main/resources/vid/intro.mp4");
                String uriVideo = archivoVideo.toURI().toString();

                Media media = new Media(uriVideo);
                MediaPlayer mediaPlayer = new MediaPlayer(media);
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

                // Cuando termine el video, cambiamos a la pantalla de PERSONAJES
                mediaPlayer.setOnEndOfMedia(() -> {
                    SwingUtilities.invokeLater(() -> {
                        mainFrame.cambiarPantalla("PERSONAJES");
                    });
                });

            } catch (Exception ex) {
                System.err.println("Error al cargar el video: " + ex.getMessage());
                ex.printStackTrace();
                // Si el video falla, saltamos de pantalla para no bloquear el juego
                SwingUtilities.invokeLater(() -> {
                    mainFrame.cambiarPantalla("PERSONAJES");
                });
            }
        });
    }
}