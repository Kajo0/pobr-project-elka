package pl.edu.pw.elka.mmarkiew.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * Class to gather loaded images as read and buffered
 * 
 * @author Mikolaj Markiewicz
 */
public class ImageHolder {

	/** List of read & buffered images */
	ArrayList<BufferedImage> images = new ArrayList<>();
	
	/** List of names of files of buffered images */
	ArrayList<String> names = new ArrayList<>();

	/**
	 * C-tor
	 * Read image into buffered images
	 * 
	 * @param files Chosen images
	 */
	public ImageHolder(final File[] files) {
		try {
			for (File file : files) {
				images.add(ImageIO.read(file));
				names.add(file.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get first image from list
	 * 
	 * @return Image if loaded, null otherwise
	 */
	public BufferedImage getImage() {
		return images.size() == 0 ? null : images.get(0);
	}

	/**
	 * Get list of images
	 * 
	 * @return List of images
	 */
	public ArrayList<BufferedImage> getImages() {
		return this.images;
	}

	/**
	 * Get name of file of wanted image
	 * 
	 * @param nr Number on images list to get name of
	 * @return Name of file of wanted image
	 */
	public String getName(final int nr) {
		if (nr < 0 || nr > this.images.size())
			throw new IllegalArgumentException("Bad name number.");
		
		return this.names.get(nr);
	}

	/**
	 * Get amount of loaded images
	 * 
	 * @return Amount of loaded images
	 */
	public int getAmount() {
		return this.images.size();
	}

}
