import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Platformer extends JFrame {
    public static final String BasePath = "./assets/";
    @Serial
    private static final long serialVersionUID = 5736902251450559962L;

    private Player p = null;
    private Level l = null;
    BufferStrategy bufferStrategy;

    public Platformer() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("./"));
        fc.setDialogTitle("Select input image");
        FileFilter filter = new FileNameExtensionFilter("Level image (.bmp)", "bmp");
        fc.setFileFilter(filter);
        int result = fc.showOpenDialog(this);
        File selectedFile = new File("");

        this.setBounds(0, 0, 1000, 12 * 70);
        this.setVisible(true);

        addKeyListener(new AL());
        createBufferStrategy(2);
        bufferStrategy = this.getBufferStrategy();

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fc.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        } else {
            dispose();
            System.exit(0);
        }

        try {
            l = new Level(selectedFile.getAbsolutePath());
            p = new Player(l);
            l.player = p;

            // Timer mit 10ms Intervall starten
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (l != null && p != null) {
                        l.update();
                        p.update();
                        checkCollision();
                        repaint();
                    }
                }
            }, 0, 10);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkCollision() {
        for (Tile tile : l.tiles) {
            if (p.boundingBox.intersect(tile.boundingBox)) {
                Vec2 overlap = p.boundingBox.overlapSize(tile.boundingBox);

                if (overlap.y < overlap.x) {
                    // Kollision Vertikal
                    if (p.posLastFrame.y < tile.boundingBox.min.y) {
                        // Von oben (Spieler landet auf Kachel)
                        p.pos.y -= overlap.y;
                        p.speed.y = 0; // Fallgeschwindigkeit stoppen
                    } else {
                        // Von unten (Spieler stößt gegen Decke)
                        p.pos.y += overlap.y;
                        p.speed.y = 0;
                    }
                } else {
                    // Kollision Horizontal
                    if (p.posLastFrame.x < tile.boundingBox.min.x) {
                        // Von links (Spieler stößt an rechte Wand)
                        p.pos.x -= overlap.x;
                        p.speed.x = 0;
                    } else {
                        // Von rechts (Spieler stößt an linke Wand)
                        p.pos.x += overlap.x;
                        p.speed.x = 0;
                    }
                }

                // BoundingBox des Spielers sofort nach der Korrektur aktualisieren,
                // damit Folgekollisionen im selben Frame stimmen
                p.boundingBox.set(
                        p.pos.x, p.pos.y,
                        p.pos.x + p.tilesWalk.get(0).getWidth(),
                        p.pos.y + p.tilesWalk.get(0).getHeight()
                );
            }
        }
    }

    private void restart() throws IOException {
        p.pos.x = 0;
        p.pos.y = 0;
        p.speed.x = 0;
        p.speed.y = 0;
        l.offsetX = 0;
        l.initLevel();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = null;
        try {
            g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
            draw(g2);
        } finally {
            if (g2 != null) g2.dispose();
        }
        bufferStrategy.show();
    }

    private void draw(Graphics2D g2d) {
        if (l == null || p == null) return;

        BufferedImage level = (BufferedImage) l.getResultingImage();
        if (level == null) return;

        if (l.offsetX > level.getWidth() - 1000) {
            l.offsetX = level.getWidth() - 1000;
        }
        BufferedImage bi = level.getSubimage((int) l.offsetX, 0, 1000, level.getHeight());
        g2d.drawImage(bi, 0, 0, this);
        g2d.drawImage(p.getPlayerImage(), (int) (p.pos.x - l.offsetX), (int) p.pos.y, this);
    }

    public class AL extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            int keyCode = event.getKeyCode();
            if (p == null) return;

            if (keyCode == KeyEvent.VK_ESCAPE) dispose();
            if (keyCode == KeyEvent.VK_UP) p.jumping = true;
            if (keyCode == KeyEvent.VK_LEFT) p.walkingLeft = true;
            if (keyCode == KeyEvent.VK_RIGHT) p.walkingRight = true;

            if (keyCode == KeyEvent.VK_R) {
                try { restart(); } catch (IOException e) { e.printStackTrace(); }
            }
        }

        @Override
        public void keyReleased(KeyEvent event) {
            int keyCode = event.getKeyCode();
            if (p == null) return;

            if (keyCode == KeyEvent.VK_UP) p.jumping = false;
            if (keyCode == KeyEvent.VK_LEFT) p.walkingLeft = false;
            if (keyCode == KeyEvent.VK_RIGHT) p.walkingRight = false;
        }
    }
}