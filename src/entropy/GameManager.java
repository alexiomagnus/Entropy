package entropy;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.Timer;

public class GameManager extends JPanel implements KeyListener {

    // Oggetti di gioco e risorse multimediali
    Gif victoryGif = new Gif("media/confetti.gif");
    Gif obstacleGif = new Gif("media/netherStar.gif");

    GifFrames playerGifFrames = new GifFrames("media/sphere.gif", 120);
    GifFrames starGifFrames = new GifFrames("media/star.gif", 160);
    GifFrames backgroundGifFrames = new GifFrames("media/sky.gif", 160);

    Palette palette = new Palette();
    Player player = new Player(100, 100, 50, 4, 0);
    Star star = new Star(0, 0, 25);
    Animations animations = new Animations(this, this);

    Music backgroudMusic = new Music();
    Music starSound = new Music();
    Music deathSound = new Music();
    Music victorySound = new Music();

    ArrayList<Obstacle> obstacles = new ArrayList<>();

    // Dimensioni del pannello
    int panelWidth = 640;
    int panelHeight = 480;

    // Dimensioni immagine
    int imageSize = 50;

    // Area punteggio e vari punteggi
    int scoreAreaY = 35;
    int score = 0;
    int victoryCounter = 0;

    // Variabili per il boost di velocità
    boolean boost = false;
    double boostTime;
    int lastBoostScore = -1;

    // Stato del gioco
    boolean isPaused = false;
    boolean isGameOver = false;
    boolean isPlayable = false;
    boolean isGameStarted = true;
    boolean isVictory = false;
    boolean isPlayingMusic = true;
    boolean soundEnabled = true;

    // Pulsanti
    JButton exitButton;
    JButton musicButton;
    JButton tryAgainButton;
    JButton soundButton;

    // Stato della musica
    long musicPosition = 0;

    // Tempo
    Timer timer;
    double elapsedSeconds;
    int elapsedCentiseconds = 9000; // 90 second = 9000 centiseconds
    int minimumTime = 0;

    public void CountdownTimer() {
        // 10 ms = 1 centisecond
        timer = new Timer(10, e -> {
            elapsedCentiseconds--;
            elapsedSeconds = elapsedCentiseconds / 100.0;

            if (elapsedSeconds == minimumTime) {
                isGameOver = true;
                timerReset();
                updateButtonVisibility();
                playDeathSound();
            }
        });
    }

    public void timerReset() {
        timer.stop();
        elapsedCentiseconds = 9000;
    }

    public void createObstacles() {
        Obstacle obstacle = new Obstacle(25);
        obstacle.spawn(panelWidth, panelHeight, scoreAreaY, player, obstacles);
        obstacles.add(obstacle);
    }

    // Costruttore
    public GameManager() {
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);
        addExitButton();
        addMusicButton();
        addTryAgainButton();
        addSoundButton();
        setBackground(palette.black);
        backgroudMusic.playMusic("media/arcade.wav");
        starSound.loadSound("media/blink.wav");
        deathSound.loadSound("media/death.wav");
        victorySound.loadSound("media/trumpet.wav");
        animations.playerAnimation();
        animations.deathAnimation();
        star.respawn(panelWidth, panelHeight, scoreAreaY, player, obstacles);
        CountdownTimer();
        startGameLoop();
    }


    // Disegna gli elementi del gioco
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Pagina iniziale
        if (isGameStarted) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(palette.white);

            // Titolo "Entropy"
            Font titleFont = new Font("Arial", Font.BOLD, 50);
            g.setFont(titleFont);
            FontMetrics titleMetrics = g.getFontMetrics(titleFont);
            String title = "Entropy";
            int titleX = (panelWidth - titleMetrics.stringWidth(title)) / 2;
            int titleY = panelHeight / 2 - 10;
            g.drawString(title, titleX, titleY);

            // Sottotitolo "Press Enter to play"
            Font subFont = new Font("Arial", Font.PLAIN, 14);
            g.setFont(subFont);
            FontMetrics subMetrics = g.getFontMetrics(subFont);
            String subtitle = "Press Enter to play";
            int subX = (panelWidth - subMetrics.stringWidth(subtitle)) / 2;
            int subY = panelHeight / 2 + 70;
            g.drawString(subtitle, subX, subY);

            // Cerchi colorati
            int ovalSize = 75;

            g.setColor(palette.celeste);
            g.fillOval(50, 60, ovalSize, ovalSize);      // in alto a sinistra

            g.setColor(palette.yellow);
            g.fillOval(panelWidth - 50 - ovalSize, panelHeight - 60 - ovalSize, ovalSize, ovalSize);    // in basso a destra

            g.setColor(palette.purple);
            g.fillOval(panelWidth - 50 - ovalSize, 60, ovalSize, ovalSize);     // in alto a destra

            g.setColor(palette.gray);
            g.fillOval(50, panelHeight - 60 - ovalSize, ovalSize, ovalSize);     // in basso a sinistra

            return;
        }

        // Gameplay
        if (isPlayable) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (backgroundGifFrames.getCurrentFrame() != null) {
                g.drawImage(backgroundGifFrames.getCurrentFrame(), 0, 0, panelWidth, panelHeight, this);
            }

            // Disegna il giocatore
            player.drawGif(g, animations.isPlayerVisible, playerGifFrames);

            // Disegna la stella animata
            animations.animateStar(g, star, player, obstacles, starGifFrames);

            // Disegna gli ostacoli
            for (Obstacle obstacle : obstacles) {
                obstacle.drawGif(g, obstacleGif);
            }

            // FONT E COLORI
            g.setColor(palette.white);
            Font statsFont = new Font("Arial", Font.BOLD, 22);
            g.setFont(statsFont);
            FontMetrics fm = g.getFontMetrics();

            // STRINGHE
            String scoreText = "Score: " + score;
            String trophiesText = "Trophies: " + victoryCounter;
            String deathsText = "Deaths: " + player.getDeaths();
            String timeText = new DecimalFormat("0.00").format(elapsedSeconds);

            // Y FISSA PER TUTTE LE STATISTICHE
            int centerY = 25;

            // POSIZIONAMENTO ORIZZONTALE
            int spacing = 60;

            int scoreX = 35;
            int trophiesX = scoreX + fm.stringWidth(scoreText) + spacing;
            int deathsX = trophiesX + fm.stringWidth(trophiesText) + spacing;
            int timeX = deathsX + fm.stringWidth(deathsText) + spacing + 20;

            // DISEGNA LE STATISTICHE PRINCIPALI
            g.drawString(scoreText, scoreX, centerY);
            g.drawString(trophiesText, trophiesX, centerY);
            g.drawString(deathsText, deathsX, centerY);

            // LINEA DI SEPARAZIONE
            g.fillRect(0, 35, panelWidth, 1);

            // TEMPO
            if (elapsedSeconds == minimumTime)
                g.setColor(palette.yellow);
            else
                g.setColor(palette.white);

            g.drawString(timeText, timeX, centerY);

            // BOOST
            if (boost) {
                g.setColor(palette.yellow);
                Font boostFont = new Font("Arial", Font.BOLD, 18);
                g.setFont(boostFont);
                FontMetrics boostMetrics = g.getFontMetrics();

                String boostText = "BOOST: " + (int) Math.ceil(boostTime);

                // Calcola la larghezza del testo
                int textWidth = boostMetrics.stringWidth(boostText);

                // Centra orizzontalmente il testo
                int x = (640 - textWidth) / 2;

                g.drawString(boostText, x, 60);
            }
        }


        // Schermata di pausa
        if (isPaused) {
            g.setColor(palette.overlay);
            g.fillRect(0, 0, panelWidth, panelHeight);

            // Scritta "PAUSE"
            g.setColor(palette.white);
            Font pauseFont = new Font("Arial", Font.BOLD, 40);
            g.setFont(pauseFont);
            FontMetrics pauseMetrics = g.getFontMetrics(pauseFont);
            String pauseText = "PAUSE";
            int pauseX = (panelWidth - pauseMetrics.stringWidth(pauseText)) / 2;
            int pauseY = panelHeight / 2 - 60;
            g.drawString(pauseText, pauseX, pauseY);

            // Righe di controllo
            Font infoFont = new Font("Arial", Font.PLAIN, 13);
            g.setFont(infoFont);
            FontMetrics infoMetrics = g.getFontMetrics(infoFont);

            // LINEA DI SEPARAZIONE
            g.fillRect(0, 400, panelWidth, 1);

            // Testi informativi (centrati rispetto alla posizione)
            String exitText = "Push E to exit";
            String musicText = "Push P to play/mute music";
            String moveText = "Use arrow keys to move";
            String soundsText = "Push M to play/mute sounds";
            String info = "suggestions:".toUpperCase();

            int exitX = (panelWidth - infoMetrics.stringWidth(exitText)) / 2 - 244;
            int musicX = (panelWidth - infoMetrics.stringWidth(musicText)) / 2 - 205;
            int moveX = (panelWidth - infoMetrics.stringWidth(moveText)) / 2 + 180;
            int soundsX = (panelWidth - infoMetrics.stringWidth(soundsText)) / 2 + 195;

            g.drawString(info, 10, 380);

            g.drawString(exitText, exitX, 430);
            g.drawString(musicText, musicX, 460);
            g.drawString(moveText, moveX, 430);
            g.drawString(soundsText, soundsX, 460);
        }


        // Schermata di game over
        if (isGameOver) {
            g.setColor(palette.overlay);
            g.fillRect(0, 0, panelWidth, panelHeight);
            animations.deletePlayer();

            // Mostra il giocatore grigio durante l'animazione di morte
            if (animations.deathVisibility) {
                g.setColor(palette.gray);
                g.fillOval(player.x, player.y, player.width, player.height);
            }

            // Visualizza "GAME OVER"
            g.setColor(palette.yellow);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("GAME OVER", panelWidth / 2 - 110, panelHeight / 2 - 90);
        }

        // Schermata di vittoria
        if (isVictory) {
            g.setColor(palette.overlay);
            g.fillRect(0, 0, panelWidth, panelHeight);

            // Visualizza "GAME OVER"
            g.setColor(palette.celeste);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("VICTORY", panelWidth / 2 - 80, panelHeight / 2 - 90);

            g.drawImage(victoryGif.gifImage, 0, 0, panelWidth, panelHeight, this);
            updateButtonVisibility();
        }
    }

    // Aggiorna gioco
    public void startGameLoop() {
        Thread gameThread = new Thread(new GameLoop(), "GameLoop");
        gameThread.start();
    }

    private class GameLoop implements Runnable {
        private static final int TARGET_FPS = 140;
        private static final long TARGET_FRAME_TIME_NS = 1_000_000_000 / TARGET_FPS;

        @Override
        public void run() {
            long previousTime = System.nanoTime();
            long accumulatedTime = 0;

            while (!Thread.currentThread().isInterrupted()) {
                long currentTime = System.nanoTime();
                long elapsed = currentTime - previousTime;
                previousTime = currentTime;
                accumulatedTime += elapsed;

                // Aggiorna il gioco più volte se necessario
                while (accumulatedTime >= TARGET_FRAME_TIME_NS && isGameRunning()) {
                    double deltaSeconds = TARGET_FRAME_TIME_NS / 1_000_000_000.0;
                    update(deltaSeconds);
                    accumulatedTime -= TARGET_FRAME_TIME_NS;
                }

                // Renderizza se il gioco è attivo
                if (isGameRunning()) {
                    repaint();
                    Toolkit.getDefaultToolkit().sync(); // evita tearing su Linux
                }

                long frameTime = System.nanoTime() - currentTime;
                long sleepTime = TARGET_FRAME_TIME_NS - frameTime;

                try {
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime / 1_000_000, (int)(sleepTime % 1_000_000));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        private boolean isGameRunning() {
            return !isGameOver && !isVictory;
        }

        private void update(double delta) {
            if (isPaused) {
                player.move(0);
                return;
            }
            if (boost) {
                boostTime -= delta;
                if (boostTime <= 0) {
                    boost = false;
                    player.setSpeed(4); // velocità normale
                }
            }

            player.move(delta);
            checkBoost();
            checkCollisions();
        }
    }



    // Metodo chiamato quando un tasto viene premuto
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Se il gioco è iniziato e il tasto non è Enter, esce
        if (isGameStarted && key != KeyEvent.VK_ENTER) return;

        // Se il tasto Enter viene premuto e il gioco è partito
        if (key == KeyEvent.VK_ENTER && isGameStarted) {
            timer.start();
            isGameStarted = false;
            isPlayable = true;
            return;
        }

        // Tasto ESC per mettere in pausa o riprendere il gioco
        if (key == KeyEvent.VK_ESCAPE && !isVictory && !isGameOver) {
            isPaused = !isPaused;
            updateButtonVisibility();

            if (isPaused) {
                timer.stop();
                animations.canMove = false;  // Blocca movimento durante la pausa
            } else {
                timer.start();
                animations.canMove = true;   // Riattiva movimento
                requestFocusInWindow();
                repaint();
            }
            return; // Importante: evita che altri comandi vengano processati durante ESC
        }

        // Se il gioco è in pausa, accetta solo alcuni tasti
        if (isPaused) {
            if (key == KeyEvent.VK_P) toggleMusic();
            if (key == KeyEvent.VK_M) toggleSound();
            if (key == KeyEvent.VK_E) System.exit(0);
            return;
        }

        // Se il gioco è finito, si accettano solo alcuni tasti
        if (isGameOver || isVictory) {
            if (key == KeyEvent.VK_P) toggleMusic();
            if (key == KeyEvent.VK_M) toggleSound();
            if (key == KeyEvent.VK_E) System.exit(0);
            if (key == KeyEvent.VK_ENTER) tryAgain();
            return;
        }

        // Movimento del giocatore
        if (isPlayable && animations.canMove) {
            int dx = 0;
            int dy = 0;

            switch (key) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    dx = -1;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    dx = 1;
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    dy = -1;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    dy = 1;
                    break;
                default:
                    return;
            }

            player.setDirX(dx);
            player.setDirY(dy);

            // Solo se si muove, controlla collisioni e boost
            checkCollisions();
            checkBoost();
            repaint();
        }
    }


    // Metodo non usato per il rilascio di un tasto
    @Override public void keyReleased(KeyEvent e) {}

    // Metodo non usato per la digitazione di un tasto
    @Override public void keyTyped(KeyEvent e) {}



    /**
     * Verifica le collisioni tra il giocatore e la stella, e controlla
     * se il giocatore esce dall'area di gioco.
     */
    public void checkCollisions() {
        // Controlla se il rettangolo del giocatore interseca quello della stella.
        if (player.x < star.x + star.width &&
                player.x + player.width > star.x &&
                player.y < star.y + star.height &&
                player.y + player.height > star.y) {

            // Aggiorna punteggio e record
            score++;

            // Spawn ostacolo
            if (score == 10) {
                createObstacles();
                createObstacles();
            }

            if (score == 25) {
                if (!obstacles.isEmpty()) {
                    obstacles.get(0).spawn(panelWidth, panelHeight, scoreAreaY, player, obstacles);
                    obstacles.get(1).spawn(panelWidth, panelHeight, scoreAreaY, player, obstacles);
                }
            }

            if (score == 35) {
                createObstacles();
                createObstacles();
            }

            if (score == 45) {
                if (!obstacles.isEmpty()) {
                    obstacles.get(2).spawn(panelWidth, panelHeight, scoreAreaY, player, obstacles);
                    obstacles.get(3).spawn(panelWidth, panelHeight, scoreAreaY, player, obstacles);
                }
            }

            if (score == 50) {
                isVictory = true;
                timerReset();
                playVictorySound();
            }


            // Imposta lo stato per l'animazione della mela raccolta.
            animations.isCollected = true;
            animations.isAnimating = true;
            animations.collectedTime = System.currentTimeMillis();
            animations.collectedAppleX = star.x;
            animations.collectedAppleY = star.y;

            // Riposiziona la mela e riproduce il suono di raccolta.
            star.respawn(panelWidth, panelHeight, scoreAreaY, player, obstacles);
            playStarSound();
        }

        for (Obstacle obstacle : obstacles) {
            obstacle.checkCollisions(player, this);
        }

        // Controlla i bordi orizzontali.
        if (player.x < 0) {
            player.x = 0; // Impedisce di uscire a sinistra.
            GameOver();
        } else if (player.x + player.width > panelWidth) {
            player.x = panelWidth - player.width; // Impedisce di uscire a destra.
            GameOver();
        }

        // Controlla i bordi verticali.
        if (player.y < scoreAreaY) {
            player.y = scoreAreaY; // Impedisce di uscire in alto.
            GameOver();
        } else if (player.y + player.height > panelHeight) {
            player.y = panelHeight - player.height; // Impedisce di uscire in basso.
            GameOver();
        }
    }

    public void GameOver() {
        isGameOver = true;
        timerReset();
        updateButtonVisibility();
        playDeathSound();
        player.incrementDeaths();
    }


    // Attiva un boost per il giocatore al raggiungimento di una soglia di punteggio.
    public void checkBoost() {
        if (score % 15 == 0 && score != 0 && !boost && score != lastBoostScore) {
            player.setSpeed(6);    // Aumenta la velocità del giocatore.
            boost = true;     // Attiva il boost.
            boostTime = 10.0;   // Imposta la durata del boost.
            lastBoostScore = score;
        }
    }



    // Suono raccolta mela
    public void playStarSound() {
        if (soundEnabled && starSound.music != null) {
            if (starSound.music.isRunning()) {
                starSound.music.stop();
            }

            starSound.music.setFramePosition(0);
            starSound.music.start();
        }
    }


    // Suono morte
    public void playDeathSound() {
        if (soundEnabled && deathSound.music != null) {
            if (deathSound.music.isRunning()) {
                deathSound.music.stop();
            }

            deathSound.music.setFramePosition(0);
            deathSound.music.start();
        }
    }


    // Suono vittoria
    public void playVictorySound() {
        if (soundEnabled && victorySound.music != null)
            victorySound.music.start();
    }


    // Attiva/disattiva suoni
    public void toggleSound() {
        if (soundEnabled) {
            soundButton.setText("Sound");
            soundEnabled = false;
        } else  {
            soundButton.setText("Mute");
            soundEnabled = true;
        }
    }


    // Attiva/disattiva la musica
    public void toggleMusic() {
        if (backgroudMusic.music != null) {
            if (isPlayingMusic) {
                musicPosition = backgroudMusic.music.getMicrosecondPosition();
                backgroudMusic.music.stop();
                isPlayingMusic = false;
                musicButton.setText("Play Music");
            } else {
                backgroudMusic.music.setMicrosecondPosition(musicPosition);
                backgroudMusic.music.start();
                backgroudMusic.music.loop(Clip.LOOP_CONTINUOUSLY);
                isPlayingMusic = true;
                musicButton.setText("Pause Music");
            }
        }
    }


    // Reset del gioco
    public void tryAgain() {
        if (isGameOver) {
            isGameOver = false;
            reset();
        }

        if (isVictory) {
            victoryCounter++;
            isVictory = false;
            reset();
        }
    }

    public void reset() {
        score = 0;
        lastBoostScore = -1;
        boost = false;
        player.setSpeed(4);
        player.setDirX(0);
        player.setDirY(0);
        player.posX = player.x;
        player.posY = player.y;
        animations.isPlayerVisible = true;
        star.respawn(panelWidth, panelHeight, scoreAreaY, player, obstacles);
        animations.playerAnimation();
        obstacles.clear();
        timer.start();
        updateButtonVisibility();
        requestFocusInWindow();
        repaint();
    }


    // Aggiorna la visibilità dei bottoni
    public void updateButtonVisibility() {
        tryAgainButton.setVisible(isGameOver || isVictory);
        musicButton.setVisible(isPaused || isGameOver || isVictory);
        soundButton.setVisible(isPaused || isGameOver || isVictory);
        exitButton.setVisible(isPaused || isGameOver || isVictory);
    }


    // Bottone uscita
    public void addExitButton() {
        exitButton = new JButton("Exit");
        exitButton.setBounds(panelWidth / 2 - 50, panelHeight / 2 + 70, 100, 30);
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setForeground(palette.white);
        exitButton.setFont(new Font("Arial", Font.PLAIN, 16));

        exitButton.setVisible(false);
        add(exitButton);

        exitButton.addActionListener(e -> System.exit(0));
    }


    // Bottone musica
    public void addMusicButton() {
        musicButton = new JButton("Pause Music");
        musicButton.setBounds(panelWidth / 2 - 80, panelHeight / 2 - 10, 160, 30);
        musicButton.setFocusPainted(false);
        musicButton.setContentAreaFilled(false);
        musicButton.setBorderPainted(false);
        musicButton.setForeground(palette.white);
        musicButton.setFont(new Font("Arial", Font.PLAIN, 16));

        musicButton.setFocusable(false);
        musicButton.setVisible(false);
        add(musicButton);

        musicButton.addActionListener(e -> {
            toggleMusic();
            requestFocusInWindow();
        });
    }


    // Bottone suono mela
    public void addSoundButton() {
        soundButton = new JButton("Mute");
        soundButton.setBounds(panelWidth / 2 - 70, panelHeight / 2 + 30, 140, 30);
        soundButton.setFocusPainted(false);
        soundButton.setContentAreaFilled(false);
        soundButton.setBorderPainted(false);
        soundButton.setForeground(palette.white);
        soundButton.setFont(new Font("Arial", Font.PLAIN, 16));

        soundButton.setFocusable(false);
        soundButton.setVisible(false);
        add(soundButton);

        soundButton.addActionListener(e -> {
            toggleSound();
            requestFocusInWindow();
        });
    }



    // Bottone "Try Again"
    public void addTryAgainButton() {
        tryAgainButton = new JButton("Try Again");
        tryAgainButton.setBounds(panelWidth / 2 - 55, panelHeight / 2 - 50, 110, 30);
        tryAgainButton.setFocusPainted(false);
        tryAgainButton.setContentAreaFilled(false);
        tryAgainButton.setBorderPainted(false);
        tryAgainButton.setForeground(palette.white);
        tryAgainButton.setFont(new Font("Arial", Font.PLAIN, 16));

        tryAgainButton.setFocusable(false);
        tryAgainButton.setVisible(false);
        add(tryAgainButton);

        tryAgainButton.addActionListener(e -> {
            tryAgain();
            requestFocusInWindow();
        });
    }
}
