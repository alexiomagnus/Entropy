package entropy;

import java.util.ArrayList;
import java.util.Random;

public class Star extends Entity {
    private static final Random random = new Random();

    public Star(int x, int y, int size) { super(x, y, size, size);}

    //Riposiziona la stela in modo casuale all'interno del pannello
    public void respawn(int panelWidth, int panelHeight, int scoreAreaY, Player player, ArrayList<Obstacle> obstacles) {
        int distancePlayer;
        int minimumDistance = 120; // Distanza minima da player e ostacoli
        int margin = 20; // Margine dai bordi dello schermo
        boolean tooClose;

        do {
            // Genera coordinate tenendo conto del margine e della dimensione dell'immagine
            x = random.nextInt(panelWidth - width - 2 * margin) + margin;
            y = random.nextInt(panelHeight - height - 2 * margin) + margin;

            // Calcola la distanza dal player
            distancePlayer = (int) Math.sqrt(Math.pow(x - player.x, 2) + Math.pow(y - player.y, 2));

            // Controlla la distanza dagli ostacoli
            tooClose = false;
            for (Obstacle currentObstacle : obstacles) {
                int distanceObstacles = (int) Math.sqrt(Math.pow(x - currentObstacle.x, 2) + Math.pow(y - currentObstacle.y, 2));
                if (distanceObstacles < minimumDistance) {
                    tooClose = true;
                    break;
                }
            }

            // Aggiunti controlli per stare dentro i bordi e fuori dalla zona punteggio
        } while (
                x < margin || x + width > panelWidth - margin ||
                        y < scoreAreaY + margin || y + height > panelHeight - margin ||
                        distancePlayer < minimumDistance || tooClose
        );
    }
}
