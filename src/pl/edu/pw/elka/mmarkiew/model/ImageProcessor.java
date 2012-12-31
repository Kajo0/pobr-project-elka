package pl.edu.pw.elka.mmarkiew.model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import pl.edu.pw.elka.mmarkiew.view.ImagePainter;

/**
 * Class responsible for processing of image
 * 
 * @author Mikolaj Markiewicz
 */
public class ImageProcessor {

	/** Minimum area of segment which is consider as proper segment after segmentation */
	public static final int MINIMUM_SGEMENT_AREA = 30;


	/** Debug string to get data from iteration calc */
	public static String debugString = "-1";

	/** Debug int to get data from iteration calc */
	public static int debugInt = -1;


	/** Image to process */
	private BufferedImage image;
	
	/** Width of image */
	private int width;
	
	/** Height of image */
	private int height;


	/** Raw pixels read as integer values */
	private int[] rawPixels;
	
	/** Image as pixels */
	private Pixel[][] pixels;
	
	/** Temporary helpful image as pixels */
	private Pixel[][] tmpPixels;
	
	/** Binarized image as pixels */
	private boolean[][] binPixels;
	
	
	/** List of segmented objects from image */
	private ArrayList<Segment> segments = new ArrayList<>();
	
	/**
	 * C-tor
	 * 
	 * @param image Image to process
	 */
	public ImageProcessor(final BufferedImage image) {
		initProcessor(image);
	}

	/**
	 * Initialize processor as new one
	 * 
	 * @param image Image to process
	 */
	public void initProcessor(final BufferedImage image) {
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();

		/*
		 * Grab pixels from image
		 */
		PixelGrabber grabber = new PixelGrabber((Image) this.image, 0, 0, width, height, false);

		try {
			if (!grabber.grabPixels())
				throw new Exception("Unable to grab pixels.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.rawPixels = (int[]) grabber.getPixels();
		grabber.abortGrabbing();
		grabber = null;	// info 4 GC

		this.pixels = new Pixel[width][height];
		this.tmpPixels = new Pixel[width][height];
		this.binPixels = new boolean[width][height]; 

		/*
		 * Copy as pixels
		 */
		for (int y = 0; y < height; ++y)
			for (int x = 0; x < width; ++x)
				tmpPixels[x][y] = pixels[x][y] = new Pixel(rawPixels[x + y * width]);
	}

	/**
	 * Do full object recognition on image by operations on image
	 */
	public void process() {
//		Integer.parseInt(new JOptionPane().showInputDialog("Input value"))	// TODO remove

		/*
		 * Possible image filters
		 */
//		pixels = Utilities.blurPixels(pixels);
//		pixels = Utilities.binarizePixels(pixels, 172);
//		pixels = Utilities.rankinkgFilterPixels(pixels, 3, 3);
//		pixels = Utilities.unsharpPixels(pixels, 0.7f, 2, 2);
//		pixels = Utilities.increaseContrastPixels(pixels, 1.5);

		/*
		 * Sharpen image
		 */
		pixels = Utilities.unsharpPixels(pixels, 0.7f, 2, 2);
		/*
		 * Binarize image
		 */
		pixels = Utilities.binarizePixels(pixels, 142);


		/*
		 * Create binary image as pixels
		 */
		for (int x = 0; x < width; ++x)
			for (int y = 0; y < height; ++y)
				binPixels[x][y] = pixels[x][y].isWhite();

		/*
		 * Do segmentation
		 */
		Segmentator segmentator = new Segmentator(binPixels);
		segmentator.segmentation();

		/*
		 * Set pixels before computation
		 */
		Segment.pixels = binPixels;

		
		/*
		 * Select appropriate segments by size, calculate on them sth.
		 */
		for (Segment segment : segmentator.getSegments()) {
				segment.calculateSizes();
		
				if (segment.getS() > MINIMUM_SGEMENT_AREA) {
					segment.calculateMoments();
					segments.add(segment);
				}
		}


		/*
		 * Do proper recognition - find logos
		 */
		Finder finder = new Finder(segments);
		finder.find();

		
		/*
		 * For each AMD segment mark(bound box) them on picute
		 */
		for (AMDSegment amd : finder.getAMDSegments())
			markAMDs(tmpPixels, amd.getMinX(), amd.getMinY(), amd.getMaxX(), amd.getMaxY());
		
		
		/*
		 * Show segmented binary image
		 */
		image = Utilities.createImageFromPixels(pixels);
		new ImagePainter(image, "Segmented image");

		/*
		 * Show final recognized image
		 */
		new ImagePainter(Utilities.createImageFromPixels(tmpPixels), "Found AMD logos");
	}

	/**
	 * Function to iterate get proper moments range
	 * At the end, text data about computation is given as static fields of processor
	 * 
	 * @note only simple, well formated parts of images are appropriate
	 */
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
		
				if (segment.getS() > width * height / 6 && segment.getMinX() != 0 && segment.getMinY() != 0) {
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
		new ImagePainter(image, "Segmented iteraration picture");
	}

	/**
	 * Calculate ranges of moments by segmented objects
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void calculateSegmentsMoments() {
		if (segments.size() == 0)
			return;

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

		Iterator<Entry<String, ArrayList<Double>>> it = map.entrySet().iterator();
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
			
			System.out.print("double " + key + " = " + sum / ((ArrayList<Double>) pair.getValue()).size() + ",\t");
			System.out.print(key.toLowerCase() + "l = " + min + ",\t");
			System.out.print(key.toLowerCase() + "r = " + max + ";");
			System.out.println();

			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	/**
	 * Create bounding box around AMD segment
	 * 
	 * @param pixels Image to mark on
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
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
