package pl.edu.pw.elka.mmarkiew.model;

/**
 * Class represent Point in image used in segmentation
 * 
 * @author Mikolaj Markiewicz
 */
public class Point {

	/** X coordinate */
	public int x;
	
	/** Y coordinate */
	public int y;
	
	/** Id of segment to which point belongs to */
	public int segmentId;

	/**
	 * C-tor
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param segmentId Id of containing segment
	 */
	public Point(final int x, final int y, final int segmentId) {
		this.x = x;
		this.y = y;
		this.segmentId = segmentId;
	}

}
