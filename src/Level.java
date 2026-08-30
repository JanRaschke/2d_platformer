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
    public Player player;
    Vec2 lvlSize;
    float offsetX;

    public static ArrayList<BufferedImage> tileImages = new ArrayList<>();
    public ArrayList<Tile> tiles = new ArrayList<>();

    public int tileSize = 70;

    public Level(String levelMapPath) {
        try {
            lvlSize = new Vec2(0, 0);
            offsetX = 0.0f;

            try {
                levelImg = ImageIO.read(new File(levelMapPath));

                if (tileImages.isEmpty()) {
                    tileImages.add(ImageIO.read(new File(Platformer.BasePath + "Tiles/grassMid.png")));
                    tileImages.add(ImageIO.read(new File(Platformer.BasePath + "Tiles/liquidWaterTop_mid.png")));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            initLevel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (player == null) return;

        float diff = player.pos.x - 500 - offsetX;
        int noMoveZone = 100;

        if (Math.abs(diff) > noMoveZone) {
            if (diff < 0)
                diff += noMoveZone;
            else
                diff -= noMoveZone;
            offsetX += diff;
        }

        if (offsetX < 0) offsetX = 0;
        if (resultingLevelImg != null && offsetX > resultingLevelImg.getWidth() - 1000)
            offsetX = resultingLevelImg.getWidth() - 1000;
    }

    public void initLevel() {
        if (levelImg == null) return;

        lvlSize.x = tileSize * levelImg.getWidth(null);
        lvlSize.y = tileSize * levelImg.getHeight(null);

        resultingLevelImg = new BufferedImage((int) lvlSize.x, (int) lvlSize.y, BufferedImage.TYPE_INT_RGB);
        tiles.clear();

        Graphics2D g2d = (Graphics2D) resultingLevelImg.getGraphics();

        for (int y = 0; y < levelImg.getHeight(null); y++) {
            for (int x = 0; x < levelImg.getWidth(null); x++) {

                int rgb = levelImg.getRGB(x, y);
                Color color = new Color(rgb, true);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                int tileIndex = -1;

                if (color.equals(Color.BLACK) || (red < 30 && green < 30 && blue < 30)) {
                    tileIndex = 0;
                } else if (color.equals(Color.BLUE) || (blue > 150 && red < 100 && green < 100)) {
                    tileIndex = 1;
                }

                if (tileIndex < 0) continue;

                Tile tile = new Tile(x * tileSize, y * tileSize, tileIndex, tileSize);
                tiles.add(tile);
                g2d.drawImage(tileImages.get(tileIndex), null, x * tileSize, y * tileSize);
            }
        }
        g2d.dispose();
    }

    public Image getResultingImage() {
        return resultingLevelImg;
    }
}