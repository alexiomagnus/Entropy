package entropy;

import javax.swing.*;
import java.util.Objects;

public class Icon {
    private ImageIcon icon;
    private JFrame frame;

    public Icon(JFrame frame) {
        this.frame = frame;
    }

    public void load() {
        try {
            icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/media/icon.png")));
            frame.setIconImage(icon.getImage());
        } catch (Exception e) {
            System.out.println("Non è stato possibile caricare l'icona");
        }
    }
}
