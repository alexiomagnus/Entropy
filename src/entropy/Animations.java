package entropy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Animations {
    private JPanel panel;
    private Timer timer;
    GameManager game;

    // Stato del giocatore
    protected boolean canMove; // Indica se il giocatore può muoversi
    protected boolean isPlayerVisible; // Indica se il giocatore è visibile
    protected boolean deathVisibility; // Stato della visibilità del giocatore dopo la morte

    // Stato della mela
    protected boolean isCollected; // Indica se la mela è stata raccolta
    protected boolean isAnimating; // Indica se la mela è in animazione
    protected long collectedTime; // Tempo in cui la mela è stata raccolta
    protected int collectedAppleX; // Posizione X della mela raccolta
    protected int collectedAppleY; // Posizione Y della mela raccolta

    public Animations(JPanel panel, GameManager game) {
        this.panel = panel;
        this.game = game;
        isPlayerVisible = true;
        deathVisibility = true;
        isCollected = false;
        isAnimating = false;
    }


    // Animazione lampeggiante del giocatore dopo lo start
    public void playerAnimation() {
        canMove = false; // Il giocatore non può muoversi durante l'animazione
        Timer timer = new Timer(150, new ActionListener() {
            int blinkCount = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                isPlayerVisible = !isPlayerVisible; // Alterna la visibilità del giocatore
                panel.repaint(); // Aggiorna il pannello per mostrare il cambiamento

                if (isPlayerVisible) {
                    canMove = true; // Permette il movimento quando visibile
                }
                blinkCount++;
                if (blinkCount >= 4) {
                    ((Timer)e.getSource()).stop(); // Termina l'animazione dopo 4 lampeggi
                }
            }
        });
        timer.start();
    }


     //Animazione di dissolvenza alla morte del giocatore.
    public void deathAnimation() {
        Timer timer = new Timer(250, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deathVisibility = !deathVisibility; // Alterna la visibilità dopo la morte
                panel.repaint();
            }
        });
        timer.start();
    }

    // Nasconde il giocatore dalla schermata.
    public void deletePlayer() {
        isPlayerVisible = false;
        panel.repaint();
    }

    /**
     * Anima la mela raccolta.
     * Se la mela non è raccolta, viene disegnata normalmente.
     * Se è in animazione, si riduce progressivamente fino a scomparire.
     */
    public void animateStar(Graphics g, Star star, Player player, ArrayList<Obstacle> obstacles, GifFrames gif) {
        final int ANIMATION_DURATION = 250; // Durata dell'animazione della mela

        if (!isCollected) { // Se la mela non è stata raccolta, viene disegnata normalmente

            int drawX = star.x - (game.imageSize - star.width) / 2;  // Sposto indietro la X di metà differenza
            int drawY = star.y - (game.imageSize - star.height) / 2;  // Sposto indietro la Y di metà differenza


            g.drawImage(gif.getCurrentFrame(), drawX, drawY, game.imageSize, game.imageSize, null);
            return;
        }

        long elapsedTime = System.currentTimeMillis() - collectedTime; // Tempo trascorso dall'inizio dell'animazione

        if (elapsedTime < ANIMATION_DURATION) {
            double scaleFactor = 1.0 - (elapsedTime / (double)ANIMATION_DURATION); // Fattore di riduzione della dimensione
            int scaledSize = (int)(star.width * scaleFactor); // Calcolo della nuova dimensione scalata
            int centerX = collectedAppleX + (star.width - scaledSize) / 2; // Posizionamento X per la scala
            int centerY = collectedAppleY + (star.height - scaledSize) / 2; // Posizionamento Y per la scala

            g.drawImage(gif.getCurrentFrame(), centerX, centerY, scaledSize, scaledSize, null); // Disegna la mela scalata
        } else {
            // Quando l'animazione termina, la mela viene riposizionata
            isCollected = false;
            isAnimating = false;
            star.respawn(game.panelWidth, game.panelHeight, game.scoreAreaY, player, obstacles);
        }
    }
}
