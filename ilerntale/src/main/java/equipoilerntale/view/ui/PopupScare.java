package equipoilerntale.view.ui;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class PopupScare extends JWindow {
    private static Font customFont;

    public PopupScare(int x, int y, int width, int height) {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);
        
        loadFont();
        
        JLabel label = new JLabel("MUERE!", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        if (customFont != null) {
            label.setFont(customFont.deriveFont(24f));
        } else {
            label.setFont(new Font("Monospaced", Font.BOLD, 24));
        }
        
        add(label, BorderLayout.CENTER);
        
        setBounds(x, y, width, height);
        setAlwaysOnTop(true);
    }

    private void loadFont() {
        if (customFont == null) {
            try (InputStream is = getClass().getResourceAsStream("/font/deltarune.ttf")) {
                if (is != null) {
                    customFont = Font.createFont(Font.TRUETYPE_FONT, is);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showFor(int millis) {
        setVisible(true);
        Timer timer = new Timer(millis, e -> {
            setVisible(false);
            dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
