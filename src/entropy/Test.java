package entropy;

import javax.swing.*;

public class Test {
    public static void main(String[] args) {
        // Rileva il sistema operativo
        String os = System.getProperty("os.name").toLowerCase();

        // Imposta lo scaling in base al sistema
        if (os.contains("linux")) {
            System.setProperty("sun.java2d.uiScale", "2"); // o 1.75 se troppo grande
        } else {
            System.setProperty("sun.java2d.uiScale", "1"); // nessuno scaling su Windows
        }

        // Avvia tutto nel thread grafico Swing
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Entropy");
            GameManager entropy = new GameManager();
            Icon icon = new Icon(frame);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(entropy);
            frame.pack();
            frame.setResizable(false);
            icon.load();
            frame.setVisible(true);
        });
    }
}
