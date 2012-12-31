package pl.edu.pw.elka.mmarkiew.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.ScrollPane;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImagePainter extends JFrame {

	private JPanel panel;

	public ImagePainter(final BufferedImage image) {
		super("Image");

		setBounds(600, 100, 500, 500);
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
