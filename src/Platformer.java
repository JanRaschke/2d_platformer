import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serial;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Platformer extends JFrame {
	@Serial
	private static final long serialVersionUID = 5736902251450559962L;
	private Level level;
	private int cameraX = 0;

	BufferedImage levelImg;

	public Platformer() {
		// exit program when window is closed
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		this.level = new Level();
		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (level == null || level.getRenderedLevel() == null) {
					return;
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					cameraX = Math.max(0, cameraX - 20);
					repaint();
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					int maxScroll = level.getRenderedLevel().getWidth() - 1000;
					cameraX = Math.min(maxScroll, cameraX + 20);
					repaint();
				}

			}

		});

		this.setTitle("Platformer");

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("./"));
		fc.setDialogTitle("Select input image");
		FileFilter filter = new FileNameExtensionFilter("Level image (.bmp)", "bmp");
		fc.setFileFilter(filter);
		int result = fc.showOpenDialog(this);
		File selectedFile = new File("");

		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fc.getSelectedFile();
			System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		} else {
			dispose();
			System.exit(0);
		}

		try {
			levelImg = ImageIO.read(selectedFile);

			this.setBounds(0, 0, 1000 + 16, 350 + 39);
			this.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if (level != null && level.getRenderedLevel() != null) {
			BufferedImage visibleLevel = level.getRenderedLevel().getSubimage(cameraX, 0, 1000, 350);
			g2d.drawImage(visibleLevel, 8, 31, this);

		}

	}
}
