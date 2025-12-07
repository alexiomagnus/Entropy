package entropy;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Obstacle extends Entity{
    private static final Random random = new Random();

    public Obstacle(int x, int y, int size) {
        super(x, y, size, size);
    }

    public Obstacle(int size) {
        super(-1, -1, size, size);
    }

    public void spawn(int panelWidth, int panelHeight, int scoreAreaY, Player player, ArrayList<Obstacle> obstacles) {
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
                if (currentObstacle == this) continue; // Salta il confronto con sé stesso

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


    public  void drawGif(Graphics g, Gif gif) {
            int imageSize = 50;

            int drawX = x - (imageSize - 25) / 2; // Sposto indietro la X di metà differenza
            int drawY = y - (imageSize - 25) / 2; // Sposto indietro la Y di metà differenza

            g.drawImage(gif.gifImage, drawX, drawY, imageSize, imageSize, null);
    }

    public void checkCollisions(Player player, GameManager game) {
        if (player.x < x + width &&
                player.x + player.width > x &&
                player.y < y + height &&
                player.y + player.height > y) {

            game.isGameOver = true;
            game.timerReset();
            game.updateButtonVisibility();
            player.incrementDeaths();
            game.playDeathSound();
        }
    }
}
