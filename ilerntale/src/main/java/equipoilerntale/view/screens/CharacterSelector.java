package equipoilerntale.view.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import equipoilerntale.view.MainFrame;

public class CharacterSelector extends JPanel {

    private MainFrame mainFrame;
    private String selectedCharacter = "";

    /**
     * CONSTRUCTOR DEL SELECTOR DE PERSONAJES.
     */
    public CharacterSelector(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("SELECCIONA A TU PERSONAJE", SwingConstants.CENTER);

        try {
            URL fontUrl = getClass().getResource("/font/deltarune.ttf");
            if (fontUrl != null) {
                Font deltaruneFont = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream()).deriveFont(52f);
                titleLabel.setFont(deltaruneFont);
            } else {
                titleLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
                System.out.println("No se pudo cargar la fuente Deltarune, usando Monospaced.");
            }
        } catch (FontFormatException | IOException e) {
            titleLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
            System.out.println("No se pudo cargar la fuente Deltarune, usando Monospaced.");
        }

        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(50, 0, 50, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel charactersPanel = new JPanel();
        charactersPanel.setLayout(new GridLayout(1, 3, 20, 20));
        charactersPanel.setBackground(Color.BLACK);
        charactersPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 50, 100, 50));

        JButton btnAntonio = createCharacterButton("antonio");
        JButton btnBaku = createCharacterButton("baku");
        JButton btnMigue = createCharacterButton("migue");

        charactersPanel.add(btnAntonio);
        charactersPanel.add(btnBaku);
        charactersPanel.add(btnMigue);

        add(charactersPanel, BorderLayout.CENTER);
    }

    private JButton createCharacterButton(String characterName) {
        JButton button = new JButton();
        button.setBackground(Color.DARK_GRAY);
        button.setFocusPainted(false);
        button.setBorder(javax.swing.BorderFactory.createLineBorder(Color.WHITE, 2));

        String imagePath = "/player/" + characterName + "/abajo1" + characterName + ".png";
        URL imageUrl = getClass().getResource(imagePath);

        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT);
            button.setIcon(new ImageIcon(img));
        } else {
            System.err.println("No se encontró la imagen: " + imagePath);
        }

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedCharacter = characterName;
                System.out.println("Personaje seleccionado: " + selectedCharacter);
                mainFrame.setPersonajeSeleccionado(selectedCharacter);
                mainFrame.cambiarPantalla("MAPA");
            }
        });

        return button;
    }

    public String getSelectedCharacter() {
        return selectedCharacter;
    }
}