package equipoilerntale.view.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Cursor;
import java.io.InputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import equipoilerntale.view.MainFrame;

/**
 * Panel de selección de personajes.
 * Permite al usuario elegir entre diferentes personajes (Antonio, Baku, Migue) 
 * antes de comenzar la partida.
 */
public class CharacterSelector extends JPanel {

    /** Referencia al marco principal para gestionar el cambio de pantallas. */
    private MainFrame mainFrame;
    /** Nombre del personaje actualmente seleccionado. */
    private String selectedCharacter = "";

    /**
     * Constructor del selector de personajes.
     * Configura la interfaz visual, carga la fuente personalizada y crea los botones de selección.
     * 
     * @param frame Referencia al MainFrame de la aplicación.
     */
    public CharacterSelector(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("SELECCIONA A TU PERSONAJE", SwingConstants.CENTER);

        try {
            InputStream fontStream = getClass().getResourceAsStream("/font/deltarune.ttf");
            if (fontStream != null) {
                Font deltaruneFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(52f);
                titleLabel.setFont(deltaruneFont);
            } else {
                throw new IOException("No se encontró el archivo de la fuente.");
            }
        } catch (FontFormatException | IOException e) {
            titleLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
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

    /**
     * Crea un botón personalizado para un personaje específico.
     * Configura el icono, el color de fondo y los eventos de ratón y acción.
     * 
     * @param characterName Nombre del personaje para el cual se crea el botón.
     * @return Un objeto JButton configurado para el personaje.
     */
    private JButton createCharacterButton(String characterName) {
        JButton button = new JButton();

        Color colorNormal = Color.DARK_GRAY;
        Color colorHover = Color.GRAY;

        button.setBackground(colorNormal);
        button.setFocusPainted(false);
        button.setBorder(javax.swing.BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setOpaque(true);

        try (InputStream is = getClass()
                .getResourceAsStream("/player/" + characterName + "/abajo1" + characterName + ".png")) {
            if (is != null) {
                button.setIcon(new ImageIcon(ImageIO.read(is)));
            } else {
                button.setText(characterName.toUpperCase());
                button.setForeground(Color.WHITE);
            }
        } catch (IOException e) {
            button.setText(characterName.toUpperCase());
            button.setForeground(Color.WHITE);
        }

        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(colorHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(colorNormal);
            }
        });

        button.addActionListener(e -> {
            selectedCharacter = characterName;
            equipoilerntale.service.SoundService.getInstance().playSFX("/sound/mouse_click.wav");
            mainFrame.setPersonajeSeleccionado(selectedCharacter);
            mainFrame.cambiarPantalla(MainFrame.SCREEN_GAME);
        });

        return button;
    }

    public String getSelectedCharacter() {
        return selectedCharacter;
    }
}