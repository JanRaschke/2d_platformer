import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Player {

    // 1. Aktuelle Position
    public double x = 100;
    public double y = 200;

    // 2. Position aus dem letzten Frame
    public double lastX = 100;
    public double lastY = 200;

    // 3. Geschwindigkeit (Pixel pro Frame/Schritt)
    public double speedX = 0;
    public double speedY = 0;

    // Animations-Bilder (p1_walk01.png bis p1_walk11.png)
    private BufferedImage[] walkFrames;
    private BufferedImage standFrame;
    private int currentFrame = 0;
    private int animationDelay = 0;

    public Player() {
        loadSprites();
    }

    private void loadSprites() {
        try {
            // Standbild laden
            standFrame = ImageIO.read(new File("assets/Player/p1_front.png"));

            // 11 Lauf-Frames laden (p1_walk01.png bis p1_walk11.png)
            walkFrames = new BufferedImage[11];
            for (int i = 0; i < 11; i++) {
                String filename = String.format("assets/Player/p1_walk%02d.png", i + 1);
                walkFrames[i] = ImageIO.read(new File(filename));
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Spieler-Sprites: " + e.getMessage());
        }
    }

    /**
     * Aktualisiert Position und Frame-Index der Laufanimation.
     */
    public void move() {
        // Letzte Position sichern
        lastX = x;
        lastY = y;

        // Neue Position anwenden
        x += speedX;
        y += speedY;

        // Animation nur abspielen, wenn Bewegung vorliegt
        if (speedX != 0 || speedY != 0) {
            animationDelay++;
            // Alle 3 Ticks zum nächsten Bild schalten (verhindert zu schnelles Durchrattern)
            if (animationDelay >= 3) {
                currentFrame = (currentFrame + 1) % walkFrames.length;
                animationDelay = 0;
            }
        } else {
            currentFrame = 0;
        }
    }

    /**
     * Gibt das aktuelle Animationsbild zurück (oder Standbild im Ruhezustand).
     */
    public BufferedImage getImage() {
        if (speedX == 0 && speedY == 0) {
            return (standFrame != null) ? standFrame : walkFrames[0];
        }
        return walkFrames[currentFrame];
    }
}