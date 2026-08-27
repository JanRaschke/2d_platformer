import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Level {
    BufferedImage levelImg, resultingLevelImg;
    Vec2 lvlSize;
    public float offsetX;
    public static ArrayList<BufferedImage> tileImages = new ArrayList<>();
    public int tileSize = 70;

    public Level(String levelMapPath) {
        try {
            lvlSize = new Vec2(0, 0);
            offsetX = 0.0f;

            try {
                // Level image
                levelImg = ImageIO.read(new File(levelMapPath));

                // Tile images laden (Liste leeren, falls static mehrmals aufgerufen wird)
                tileImages.clear();
                tileImages.add(ImageIO.read(new File("./assets/Tiles/grassMid.png")));
                tileImages.add(ImageIO.read(new File("./assets/Tiles/liquidWaterTop_mid.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            initLevel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (resultingLevelImg == null) return;

        if (offsetX < 0)
            offsetX = 0;

        if (offsetX > resultingLevelImg.getWidth() - 1000)
            offsetX = Math.max(0, resultingLevelImg.getWidth() - 1000);
    }

    public void initLevel() {
        if (levelImg == null) return;

        lvlSize.x = tileSize * levelImg.getWidth(null);
        lvlSize.y = tileSize * levelImg.getHeight(null);

        resultingLevelImg = new BufferedImage((int) lvlSize.x, (int) lvlSize.y, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = (Graphics2D) resultingLevelImg.getGraphics();

        try {
            // Hintergrund initialisieren
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, (int) lvlSize.x, (int) lvlSize.y);

            for (int y = 0; y < levelImg.getHeight(null); y++) {
                for (int x = 0; x < levelImg.getWidth(null); x++) {

                    Color color = new Color(levelImg.getRGB(x, y));
                    int tileIndex = -1;

                    // Compare color of pixels in order to select the corresponding tiles
                    if (color.equals(Color.BLACK))
                        tileIndex = 0;
                    if (color.equals(Color.BLUE))
                        tileIndex = 1;

                    if (tileIndex < 0 || tileIndex >= tileImages.size())
                        continue;

                    // Korrekte drawImage-Signatur: (Image, int x, int y, ImageObserver)
                    g2d.drawImage(tileImages.get(tileIndex), x * tileSize, y * tileSize, null);
                }
            }
        } finally {
            g2d.dispose();
        }
    }

    // --- Bestehende Schnittstellen beibehalten ---

    public Image getResultingImage() {
        return resultingLevelImg;
    }

    // --- Ergänzungen für Schritt 3 (Player, Kamera, getSubimage) ---

    public BufferedImage getImage() {
        return resultingLevelImg;
    }

    public int getWidth() {
        return (resultingLevelImg != null) ? resultingLevelImg.getWidth() : (int) lvlSize.x;
    }

    public int getHeight() {
        return (resultingLevelImg != null) ? resultingLevelImg.getHeight() : (int) lvlSize.y;
    }

    /**
     * Schneidet einen sicheren Bildausschnitt ohne IndexOutOfBounds-Gefahr aus.
     */
    public BufferedImage getSubimage(int x, int y, int width, int height) {
        if (resultingLevelImg == null) return null;

        int safeWidth = Math.min(width, resultingLevelImg.getWidth());
        int safeHeight = Math.min(height, resultingLevelImg.getHeight());

        int safeX = Math.max(0, Math.min(x, resultingLevelImg.getWidth() - safeWidth));
        int safeY = Math.max(0, Math.min(y, resultingLevelImg.getHeight() - safeHeight));

        return resultingLevelImg.getSubimage(safeX, safeY, safeWidth, safeHeight);
    }
}