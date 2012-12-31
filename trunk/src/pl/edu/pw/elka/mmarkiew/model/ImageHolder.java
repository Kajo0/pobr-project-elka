package pl.edu.pw.elka.mmarkiew.model;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ImageHolder {

	ArrayList<BufferedImage> images = new ArrayList<>();
	ArrayList<String> names = new ArrayList<>();

	public ImageHolder(final File[] files) {
		try {
			for (File file : files) {
				images.add(ImageIO.read(file));
				names.add(file.getName());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BufferedImage getImage() {
		return images.size() == 0 ? null : images.get(0);
	}
	
	public ArrayList<BufferedImage> getImages() {
		return this.images;
	}
	
	public String getName(final int nr) {
		if (nr < 0 || nr > this.images.size())
			throw new IllegalArgumentException("Bad name number.");
		
		return this.names.get(nr);
	}

	public int getAmount() {
		return this.images.size();
	}

}
