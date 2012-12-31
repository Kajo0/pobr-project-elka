package pl.edu.pw.elka.mmarkiew.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.ScrollPane;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Image painter to paint images in new window
 * 
 * @author Mikolaj Markiewicz
 */
@SuppressWarnings("serial")
public class ImagePainter extends JFrame {

	/** Panel to paint on it */
	private JPanel panel;
	
	/** Image to paint */
	private BufferedImage image;

	/**
	 * C-tor
	 * Paints image in new window
	 * 
	 * @param image Image to paint
	 */
	public ImagePainter(final BufferedImage image) {
		super("Painted image");

		this.image = image;
		
		initFrame();
	}

	/**
	 * C-tor
	 * Paints image in new window with given title
	 * 
	 * @param image Image to paint
	 * @param title Title of window
	 */
	public ImagePainter(final BufferedImage image, String title) {
		super(title);

		this.image = image;
		
		initFrame();
	}

	/**
	 * Init window parameters and paint image
	 */
	private void initFrame() {
		setBounds(500, 100, 900, 700);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		panel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);

				g.drawImage(image, 0, 0, null);
			}
		};

		Container pane = getContentPane();
		panel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

		ScrollPane scroll = new ScrollPane();
		scroll.add(panel);

		pane.add(scroll);
	}

}
