package entropy;

import java.awt.*;

public class Player extends Entity {
    private int dirX, dirY;  // Direzione del movimento lungo gli assi X e Y
    private int speed;       // Velocità del giocatore
    private int deaths;      // Numero di morti del giocatore
    public double posX, posY;

    public Player(int x, int y, int size, int speed, int deaths) {
        super(x, y, size, size);
        this.speed = speed;
        this.deaths = deaths;

        // Inizializza le coordinate double per il movimento fluido
        this.posX = x;
        this.posY = y;
    }

    // Metodi getter per ottenere la direzione, velocità e numero di morti
    public int getDirX() {
        return dirX;
    }

    public int getDirY() {
        return dirY;
    }

    public int getSpeed() {
        return speed;
    }

    public int getDeaths() {
        return deaths;
    }

    // Metodi setter per impostare la direzione, velocità e numero di morti
    public void setDirX(int dirX) {
        this.dirX = dirX;
    }

    public void setDirY(int dirY) {
        this.dirY = dirY;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    // Metodo per aggiornare la posizione del giocatore in base alla direzione e velocità
    public void move(double delta) {
        posX += dirX * speed * delta * 60;
        posY += dirY * speed * delta * 60;

        // Cast a int solo alla fine, per non perdere precisione
        this.x = (int) posX;
        this.y = (int) posY;
    }

    // Metodo per incrementare il numero di morti del giocatore
    public void incrementDeaths() {
        deaths++;
    }

    // Metodo per disegnare il giocatore sulla schermata
    public void draw(Graphics g, boolean isVisible, Palette palette) {
        if (isVisible) {
            g.setColor(palette.celeste);
            g.fillRect(x, y, width, height);
        }
    }

    public void drawGif(Graphics g, boolean isVisible, GifFrames gif) {
        if (isVisible) {

            int imageSize = 70;

            int drawX = x - (imageSize - 50) / 2;  // Sposto indietro la X di metà differenza
            int drawY = y - (imageSize - 50) / 2;  // Sposto indietro la Y di metà differenza

            g.drawImage(gif.getCurrentFrame(), drawX, drawY, imageSize, imageSize, null);
        }
    }
}
