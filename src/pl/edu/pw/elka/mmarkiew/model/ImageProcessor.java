package pl.edu.pw.elka.mmarkiew.model;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pl.edu.pw.elka.mmarkiew.view.ImagePainter;

public class ImageProcessor {

	public static String debugString = "-1";
	public static int debugInt = -1;

	private BufferedImage image;
	private int width;
	private int height;

	private int[] rawPixels;
	private Pixel[][] pixels;
	private Pixel[][] tmpPixels;
	private boolean[][] binPixels;
	
	private ArrayList<Segment> segments = new ArrayList<>();

	public ImageProcessor(final BufferedImage image) {
		initProcessor(image);
	}

	public void initProcessor(final BufferedImage image) {
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();

		PixelGrabber grabber = new PixelGrabber((Image) this.image, 0, 0, width, height, false);

		try {
			if (!grabber.grabPixels())
				throw new Exception("Unable to grab pixels");
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.rawPixels = (int[]) grabber.getPixels();
		grabber.abortGrabbing();
		grabber = null;

		this.pixels = new Pixel[width][height];
		this.tmpPixels = new Pixel[width][height];
		this.binPixels = new boolean[width][height]; 

		for (int y = 0; y < height; ++y)
			for (int x = 0; x < width; ++x)
				tmpPixels[x][y] = pixels[x][y] = new Pixel(rawPixels[x + y * width]);
	}

	public void process() {
//		pixels = Utilities.blurPixels(pixels);
//		pixels = Utilities.binarizePixels(pixels, Integer.parseInt(new JOptionPane().showInputDialog("Podaj threshold")));
//		pixels = Utilities.binarizePixels(pixels, 172);
//		pixels = Utilities.rankinkgFilterPixels(pixels, 3);
//		pixels = Utilities.unsharpPixels(pixels, 0.7f, 2, 2);
		
//		pixels = Utilities.increaseContrastPixels(pixels, 1.5);
		pixels = Utilities.unsharpPixels(pixels, 0.7f, 2, 2);
		pixels = Utilities.binarizePixels(pixels, 142);
//		pixels = Utilities.rankinkgFilterPixels(pixels, 3, 3);

		boolean[][] binaryPixels = new boolean[width][height];
		for (int x = 0; x < width; ++x)
			for (int y = 0; y < height; ++y)
				binaryPixels[x][y] = pixels[x][y].isWhite();

		Segmentator segmentator = new Segmentator(binaryPixels);
		segmentator.segmentation();

		Segment.pixels = binaryPixels;

		for (Segment segment : segmentator.getSegments()) {
				segment.calculateSizes();
		
				if (segment.getS() > 30 /*&& segment.getS() > width * height / 4 && segment.getMinX() != 0 && segment.getMinY() != 0*/) {
					segment.calculateMoments();
					segments.add(segment);
				}
		}

		Finder finder = new Finder(segments);
		finder.find();
		
		for (AMDSegment amd : finder.getAMDSegments()) {
			markAMDs(tmpPixels, amd.getMinX(), amd.getMinY(), amd.getMaxX(), amd.getMaxY());
		}
		
//////		ArrayList<Segment> segments = new ArrayList<>();
//////		for (Map.Entry<Integer, Segment> it : segmentator.getSegments().entrySet()) {
//////			Segment segment = it.getValue();
//////
//////			segment.calculateSizes();
//////
//////			if (segment.getS() > 50 /*&& segment.getS() > width * height / 4 && segment.getMinX() != 0 && segment.getMinY() != 0*/) {
//////				segment.calculateMoments();
//////				segments.add(segment);
//////			}
//////		}



		image = Utilities.createImageFromPixels(pixels);
		new ImagePainter(image);
		new ImagePainter(Utilities.createImageFromPixels(tmpPixels));
	}

	public void processComputation() {
		pixels = Utilities.unsharpPixels(pixels, 0.7f, 2, 2);
		pixels = Utilities.binarizePixels(pixels, 142);

		boolean[][] binaryPixels = new boolean[width][height];
		for (int x = 0; x < width; ++x)
			for (int y = 0; y < height; ++y)
				binaryPixels[x][y] = pixels[x][y].isWhite();

		Segmentator segmentator = new Segmentator(binaryPixels);
		segmentator.segmentation();

		Segment.pixels = binaryPixels;

		debugString = "-1";
		debugInt = -1;

		int i = 0;
		StringBuilder s = new StringBuilder();
		for (Segment segment : segmentator.getSegments()) {
				segment.calculateSizes();
		
				if (/*segment.getS() > 30 && */segment.getS() > width * height / 6 && segment.getMinX() != 0 && segment.getMinY() != 0) {
					segment.calculateMoments();
					segments.add(segment);
					++i;
					s.append(segment);
					s.append("\n\n\n");
				}
		}
		s.append(i);

		debugString = s.toString();
		debugInt = i;

		image = Utilities.createImageFromPixels(pixels);
		new ImagePainter(image);
	}

	public void calculateSegmentsMoments() {
		if (segments.size() == 0)
			return;

//		M7 -> M3 -> W7 -> W8 -> W9 -> M1
		HashMap<String, ArrayList<Double>> map = new HashMap<>();
		map.put("M7", new ArrayList<Double>());
		map.put("M3", new ArrayList<Double>());
		map.put("W7", new ArrayList<Double>());
		map.put("W8", new ArrayList<Double>());
		map.put("W9", new ArrayList<Double>());
		map.put("M1", new ArrayList<Double>());

		for (Segment segment : this.segments) {
			map.get("M7").add(segment.getMoment(7));
			map.get("M3").add(segment.getMoment(3));
			map.get("W7").add(segment.getWoment(7));
			map.get("W8").add(segment.getWoment(8));
			map.get("W9").add(segment.getWoment(9));
			map.get("M1").add(segment.getMoment(1));
		}

		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			double sum = 0, min = 9999, max = -9999;
			Map.Entry pair = (Map.Entry) it.next();

			String key = (String) pair.getKey();
			ArrayList<Double> value = (ArrayList<Double>) pair.getValue();
			
			for (double d : value) {
				sum += d;
				min = (d < min ? d : min);
				max = (d > max ? d : max);
			}
			
//			double m7l = 0.011,	m7r = 0.017,	M7 = 0.01529928011767842575;
			System.out.print("double " + key + " = " + sum / ((ArrayList<Double>) pair.getValue()).size() + ",\t");
			System.out.print(key.toLowerCase() + "l = " + min + ",\t");
			System.out.print(key.toLowerCase() + "r = " + max + ";");
			System.out.println();

			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	private void markAMDs(Pixel[][] pixels, int x1, int y1, int x2, int y2) {
		int xp = Math.min(x1, x2);
		int xk = Math.max(x1, x2);
		int yp = Math.min(y1, y2);
		int yk = Math.max(y1, y2);

		for (int x = xp; x <= xk; ++x)
			for (int y = yp; y <= yk; ++y)
				if (x == xp || x == xk || y == yp || y == yk)
					pixels[x][y] = new Pixel(255, 0, 0);
	}

}
