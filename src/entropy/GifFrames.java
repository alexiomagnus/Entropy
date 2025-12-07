package entropy;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class GifFrames {
    private final BufferedImage[] frames;
    private int currentFrame = 0;
    private long lastFrameTime = System.currentTimeMillis();
    private final int frameDelay;

    // Frame vuoto per gestire errori di caricamento
    private static final BufferedImage EMPTY_FRAME = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    public GifFrames(String resourcePath, int frameDelay) {
        this.frameDelay = frameDelay;
        this.frames = loadFrames(resourcePath);
    }

    private BufferedImage[] loadFrames(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path);
             ImageInputStream iis = ImageIO.createImageInputStream(is)) {

            if (is == null) {
                System.err.println("Risorsa non trovata: " + path);
                return new BufferedImage[]{EMPTY_FRAME};
            }

            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            reader.setInput(iis);

            int count = reader.getNumImages(true);
            BufferedImage[] result = new BufferedImage[count];

            for (int i = 0; i < count; i++) {
                result[i] = reader.read(i);
            }

            reader.dispose();
            return result;

        } catch (Exception e) {
            System.err.println("Errore caricamento GIF: " + e.getMessage());
            return new BufferedImage[]{EMPTY_FRAME};
        }
    }

    public BufferedImage getCurrentFrame() {
        long now = System.currentTimeMillis();
        if (now - lastFrameTime >= frameDelay) {
            currentFrame = (currentFrame + 1) % frames.length;
            lastFrameTime = now;
        }
        return frames[currentFrame];
    }

    // Metodi di utilità
    public void reset() {
        currentFrame = 0;
        lastFrameTime = System.currentTimeMillis();
    }

    public int getFrameCount() {
        return frames.length;
    }

    public boolean isValid() {
        return frames.length > 0 && frames[0] != EMPTY_FRAME;
    }
}