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

public class TransformacionVideoScreen extends JPanel {

    private MainFrame mainFrame;
    private JFXPanel jfxPanel;
    private MediaPlayer mediaPlayer;

    public TransformacionVideoScreen(MainFrame frame) {
        this.mainFrame = frame;

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);

        Platform.setImplicitExit(false);
    }

    public void playVideo() {
        Platform.runLater(() -> {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                }

                File archivoVideo = new File("ilerntale/src/main/resources/vid/transformacion.mp4");
                String uriVideo = archivoVideo.toURI().toString();

                Media media = new Media(uriVideo);
                mediaPlayer = new MediaPlayer(media);
                MediaView mediaView = new MediaView(mediaPlayer);

                StackPane root = new StackPane();
                root.setStyle("-fx-background-color: black;");
                root.getChildren().add(mediaView);

                Scene scene = new Scene(root);
                jfxPanel.setScene(scene);

                mediaView.fitWidthProperty().bind(scene.widthProperty());
                mediaView.fitHeightProperty().bind(scene.heightProperty());
                mediaView.setPreserveRatio(true);

                mediaPlayer.play();

                mediaPlayer.setOnEndOfMedia(() -> {
                    SwingUtilities.invokeLater(() -> {
                        mainFrame.triggerPhase2();
                    });
                });

            } catch (Exception ex) {
                System.err.println("Error al cargar el video de transformación: " + ex.getMessage());
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    mainFrame.triggerPhase2();
                });
            }
        });
    }
}
