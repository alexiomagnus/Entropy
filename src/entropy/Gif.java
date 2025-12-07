package entropy;

import javax.swing.*;
import java.awt.*;

public class Gif {
    ImageIcon gifIcon;
    Image gifImage;

    public Gif(String resourcePath) {
        loadGif(resourcePath);
    }

    // Metodo per caricare una GIF da una risorsa nel jar
    public void loadGif(String resourcePath) {
        try {
            // Usa getClassLoader per ottenere la risorsa come URL
            java.net.URL gifUrl = getClass().getClassLoader().getResource(resourcePath);
            if (gifUrl != null) {
                gifIcon = new ImageIcon(gifUrl);
                gifImage = gifIcon.getImage();
            } else {
                System.err.println("GIF non trovata: " + resourcePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
