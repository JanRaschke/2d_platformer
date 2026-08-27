import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serial;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Platformer extends JFrame {
	@Serial
	private static final long serialVersionUID = 5736902251450559962L;

	private static final int VIEW_WIDTH = 1000;
	private static final int VIEW_HEIGHT = 5 * 70; // 350

	private Level l = null;
	private Player player = null;
	private BufferStrategy bufferStrategy;

	public Platformer() {
		// Exit program when window is closed
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("./"));
		fc.setDialogTitle("Select input image");
		FileFilter filter = new FileNameExtensionFilter("Level image (.bmp)", "bmp", "png");
		fc.setFileFilter(filter);
		int result = fc.showOpenDialog(this);
		File selectedFile = new File("");
		addKeyListener(new AL(this));

		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fc.getSelectedFile();
			System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		} else {
			dispose();
			System.exit(0);
		}

		try {
			l = new Level(selectedFile.getAbsolutePath());
			player = new Player();

			this.setBounds(0, 0, VIEW_WIDTH, l.getHeight());
			this.setResizable(false);
			this.setVisible(true);

			// DoubleBuffering einrichten
			createBufferStrategy(2);
			bufferStrategy = this.getBufferStrategy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Player getPlayer() {
		return player;
	}

	private void updateGameStateAndRepaint() {
		if (player != null) {
			player.move();

			// Spieler innerhalb des Levels halten
			int maxX = (l != null) ? l.getWidth() - 70 : VIEW_WIDTH;
			int maxY = (l != null) ? l.getHeight() - 70 : VIEW_HEIGHT;

			player.x = Math.max(0, Math.min(player.x, maxX));
			player.y = Math.max(0, Math.min(player.y, maxY));
		}

		if (l != null) {
			// Kamera an der Spielfigur orientieren
			l.offsetX = (float) (player.x - (VIEW_WIDTH / 2.0));
			l.update();
		}

		repaint();
	}

	@Override
	public void update(Graphics g) {
		// Standard-Hintergrundlöschen des JFrame verhindern (kein Flackern)
		paint(g);
	}

	@Override
	public void paint(Graphics g) {
		if (bufferStrategy == null) {
			return;
		}

		Graphics2D g2 = null;
		try {
			g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
			draw(g2);
		} finally {
			if (g2 != null) {
				g2.dispose();
			}
		}
		bufferStrategy.show();
	}

	private void draw(Graphics2D g2) {
		if (l == null)
			return;

		// 1. Sichtbaren Ausschnitt des Levels zeichnen
		BufferedImage visibleLevel = l.getSubimage((int) l.offsetX, 0, VIEW_WIDTH, l.getHeight());
		if (visibleLevel != null) {
			g2.drawImage(visibleLevel, 0, 0, this);
		}

		// 2. Spielfigur relativ zum sichtbaren Ausschnitt zeichnen
		if (player != null) {
			int playerScreenX = (int) player.x - (int) l.offsetX;
			int playerScreenY = (int) player.y;
			g2.drawImage(player.getImage(), playerScreenX, playerScreenY, this);
		}
	}

	public class AL extends KeyAdapter {
		Platformer p;

		public AL(Platformer p) {
			super();
			this.p = p;
		}

		@Override
		public void keyPressed(KeyEvent event) {
			int keyCode = event.getKeyCode();

			if (keyCode == KeyEvent.VK_ESCAPE) {
				p.dispose();
				System.exit(0);
			}

			// Steuerung des Spielers in X- und Y-Richtung
			if (p.player != null) {
				if (keyCode == KeyEvent.VK_LEFT) {
					p.player.speedX = -10;
				}
				if (keyCode == KeyEvent.VK_RIGHT) {
					p.player.speedX = 10;
				}
				if (keyCode == KeyEvent.VK_UP) {
					p.player.speedY = -10;
				}
				if (keyCode == KeyEvent.VK_DOWN) {
					p.player.speedY = 10;
				}
			}

			p.updateGameStateAndRepaint();
		}

		@Override
		public void keyReleased(KeyEvent event) {
			int keyCode = event.getKeyCode();

			if (p.player != null) {
				if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
					p.player.speedX = 0;
				}
				if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN) {
					p.player.speedY = 0;
				}
			}

			p.updateGameStateAndRepaint();
		}
	}
}