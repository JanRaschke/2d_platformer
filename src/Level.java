import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Level {

    private BufferedImage levelImg;
    private BufferedImage grassTile;
    private BufferedImage waterTile;
    private BufferedImage renderedLevel;

    public Level() {
        loadImages();
        this.renderedLevel = createLevelImage();
    }

    /**
     * Lädt die Rohbilder (Kacheln und Level-Map).
     */
    private void loadImages() {
        try {
            levelImg = ImageIO.read(new File("level1.bmp"));
            grassTile = ImageIO.read(new File("assets/Tiles/grassMid.png"));
            waterTile = ImageIO.read(new File("assets/Tiles/liquidWaterTop_mid.png"));
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Bilddateien: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Erzeugt das zusammengesetzte Ausgabebild basierend auf den Pixel-Farben von level1.bmp.
     */
    public BufferedImage createLevelImage() {
        if (levelImg == null || grassTile == null || waterTile == null) {
            return null;
        }

        int tileWidth = grassTile.getWidth();
        int tileHeight = grassTile.getHeight();

        int totalWidth = levelImg.getWidth() * tileWidth;
        int totalHeight = levelImg.getHeight() * tileHeight;

        BufferedImage output = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = output.createGraphics();

        try {
            for (int y = 0; y < levelImg.getHeight(); y++) {
                for (int x = 0; x < levelImg.getWidth(); x++) {
                    Color color = new Color(levelImg.getRGB(x, y));

                    int drawX = x * tileWidth;
                    int drawY = y * tileHeight;

                    if (color.equals(Color.BLUE)) {
                        g2d.drawImage(waterTile, drawX, drawY, null);
                    } else if (color.equals(Color.BLACK)) {
                        g2d.drawImage(grassTile, drawX, drawY, null);
                    }
                }
            }
        } finally {
            g2d.dispose();
        }

        return output;
    }

    public BufferedImage getRenderedLevel() {
        return renderedLevel;
    }
}