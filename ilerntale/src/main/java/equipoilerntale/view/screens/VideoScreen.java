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

public class VideoScreen extends JPanel {

    private MainFrame mainFrame;
    private JFXPanel jfxPanel;
    private MediaPlayer mediaPlayer;

    public VideoScreen(MainFrame frame) {
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

    // Este método ahora es llamado por el MainFrame
    public void playVideo() {
        Platform.runLater(() -> {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                }

                // Ruta del video (JAR compatible)
                java.net.URL resource = getClass().getResource("/vid/intro.mp4");
                if (resource == null) {
                    throw new java.io.FileNotFoundException("No se encontró el video de intro en resources");
                }
                String uriVideo = resource.toExternalForm();

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

                // Cuando termine el video, volvemos al panel de MENU (como solicitó el usuario)
                mediaPlayer.setOnEndOfMedia(() -> {
                    SwingUtilities.invokeLater(() -> {
                        mainFrame.cambiarPantalla("MENU");
                    });
                });

            } catch (Exception ex) {
                System.err.println("Error al cargar el video: " + ex.getMessage());
                ex.printStackTrace();
                // Si el video falla, volvemos al MENU para evitar bloqueos
                SwingUtilities.invokeLater(() -> {
                    mainFrame.cambiarPantalla("MENU");
                });
            }
        });
    }
}