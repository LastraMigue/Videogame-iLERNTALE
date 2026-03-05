package equipoilerntale.view.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import equipoilerntale.view.MainFrame;

public class CharacterSelector extends JPanel {

    private MainFrame mainFrame;
    private String selectedCharacter = "";

    public CharacterSelector(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("SELECCIONA A TU PERSONAJE", SwingConstants.CENTER);

        try {
            String fontPath = "ilerntale/src/main/resources/font/deltarune.ttf";
            File fontFile = new File(fontPath);
            if (!fontFile.exists()) {
                fontFile = new File("src/main/resources/font/deltarune.ttf");
            }

            Font deltaruneFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(52f);
            titleLabel.setFont(deltaruneFont);

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

        String imagePath = "ilerntale/src/main/resources/player/" + characterName + "/abajo1" + characterName + ".png";
        File file = new File(imagePath);
        if (!file.exists()) {
            imagePath = "src/main/resources/player/" + characterName + "/abajo1" + characterName + ".png";
        }

        ImageIcon icon = new ImageIcon(imagePath);
        button.setIcon(icon);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedCharacter = characterName;
                System.out.println("Personaje seleccionado: " + selectedCharacter);
                mainFrame.cambiarPantalla("MAPA");
            }
        });

        return button;
    }
}