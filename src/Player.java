import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Player {
    boolean facingLeft = false;

    // Position & Geschwindigkeit
    Vec2 pos;
    Vec2 posLastFrame;
    Vec2 speed;

    BoundingBox boundingBox;

    // Zustände der Tasten
    public boolean jumping = false;
    public boolean walkingLeft = false;
    public boolean walkingRight = false;

    int numberAnimationStates = 0;
    int displayedAnimationState = 0;
    int moveCounter = 0;

    protected ArrayList<BufferedImage> tilesWalk;
    Level l;

    public Player(Level l) {
        this.pos = new Vec2(0, 0);
        this.posLastFrame = new Vec2(0, 0);
        this.speed = new Vec2(0, 0);

        this.l = l;
        tilesWalk = new ArrayList<BufferedImage>();
        try {
            tilesWalk.add(ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk01.png")));
            tilesWalk.add(ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk02.png")));
            tilesWalk.add(ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk03.png")));
            tilesWalk.add(ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk04.png")));
            tilesWalk.add(ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk05.png")));
            tilesWalk.add(ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk06.png")));
            tilesWalk.add(ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk07.png")));
            tilesWalk.add(ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk08.png")));
            tilesWalk.add(ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk09.png")));
            tilesWalk.add(ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk10.png")));
            tilesWalk.add(ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk11.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        numberAnimationStates = tilesWalk.size();

        boundingBox = new BoundingBox(
                pos.x, pos.y,
                pos.x + tilesWalk.get(0).getWidth(),
                pos.y + tilesWalk.get(0).getHeight()
        );
    }

    public void update() {
        posLastFrame.x = pos.x;
        posLastFrame.y = pos.y;

        float acceleration = 1.2f;
        float maxSpeed = 7.5f;
        float gravity = 0.5f;
        float friction = 0.8f;
        float jumpStrength = -12.0f;

        // Beschleunigung durch Tasten
        if (walkingLeft) {
            speed.x -= acceleration;
            facingLeft = true;
        }
        if (walkingRight) {
            speed.x += acceleration;
            facingLeft = false;
        }

        // Springen (nur wenn vertikale Geschwindigkeit fast 0 ist -> erfordert Bodenkontakt)
        if (jumping && Math.abs(speed.y) < 0.1f) {
            speed.y = jumpStrength;
        }

        // Reibung (Luft/Boden) auf der X-Achse
        speed.x *= friction;

        // Schwerkraft auf der Y-Achse
        speed.y += gravity;

        // Maximalgeschwindigkeit begrenzen
        if (speed.x > maxSpeed) speed.x = maxSpeed;
        if (speed.x < -maxSpeed) speed.x = -maxSpeed;

        // Position updaten
        pos.x += speed.x;
        pos.y += speed.y;

        // Sicherstellen, dass das Level nicht verlassen wird
        int playerWidth = tilesWalk.get(0).getWidth();
        int playerHeight = tilesWalk.get(0).getHeight();

        if (pos.x < 0) {
            pos.x = 0;
            speed.x = 0;
        }
        if (pos.y < 0) {
            pos.y = 0;
            speed.y = 0;
        }
        if (l != null && l.getResultingImage() != null) {
            float levelW = l.getResultingImage().getWidth(null);
            float levelH = l.getResultingImage().getHeight(null);

            if (pos.x > levelW - playerWidth) {
                pos.x = levelW - playerWidth;
                speed.x = 0;
            }
            if (pos.y > levelH - playerHeight) {
                pos.y = levelH - playerHeight;
                speed.y = 0;
            }
        }

        // BoundingBox aktualisieren
        boundingBox.set(
                pos.x, pos.y,
                pos.x + playerWidth,
                pos.y + playerHeight
        );

        if (Math.abs(speed.x) > 0.5f) {
            getNextImage();
        }
    }

    public BufferedImage getPlayerImage() {
        BufferedImage b = tilesWalk.get(displayedAnimationState);
        if (facingLeft) {
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-b.getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            b = op.filter(b, null);
        }
        return b;
    }

    private void getNextImage() {
        moveCounter++;
        if (moveCounter >= 3) {
            displayedAnimationState++;
            moveCounter = 0;
        }
        if (displayedAnimationState > numberAnimationStates - 1) {
            displayedAnimationState = 0;
        }
    }
}