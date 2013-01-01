package pl.edu.pw.elka.mmarkiew.model;

import java.util.LinkedList;

/**
 * Gather segments made AMD logos
 * 
 * @author Mikolaj Markiewicz
 */
public class AMDSegment {

	/** Id generator */
	public static int i = 0;
	
	/** Id used to combine pair of AMDSegments (AMD + Boxies) */
	public int id = i++;

	/** List of segments */
	private LinkedList<Segment> segments = new LinkedList<>();

	/**
	 * Default empty C-tor
	 */
	public AMDSegment() {

	}

	/**
	 * C-tor
	 * 
	 * @param a A segment
	 * @param m M segment
	 * @param d D segment
	 */
	public AMDSegment(final Segment a, final Segment m, final Segment d) {
		segments.add(a);
		segments.add(m);
		segments.add(d);
	}

	/**
	 * C-tor
	 * 
	 * @param blb BOTTOM LEFT BOXY segment
	 * @param trb TOR RIGHT BOXY segment
	 */
	public AMDSegment(final Segment blb, final Segment trb) {
		segments.add(blb);
		segments.add(trb);
	}
	
	/**
	 * Add segment to list
	 * 
	 * @param segment Segment to add
	 */
	public void addSegment(final Segment segment) {
		this.segments.add(segment);
	}

	/**
	 * Get segments making logo
	 * 
	 * @return List of segments
	 */
	public LinkedList<Segment> getSegments() {
		return this.segments;
	}

	/**
	 * Get minimum x coordinate of logo bounding box
	 * 
	 * @return Minimum x coordinate
	 */
	public int getMinX() {
		int tmp = 999999;
		
		for (Segment seg : segments)
			tmp = Math.min(tmp, seg.getMinX());
		
		return tmp;
	}

	/**
	 * Get minimum y coordinate of logo bounding box
	 * 
	 * @return Minimum y coordinate
	 */
	public int getMinY() {
		int tmp = 999999;
		
		for (Segment seg : segments)
			tmp = Math.min(tmp, seg.getMinY());
		
		return tmp;
	}

	/**
	 * Get maximum x coordinate of logo bounding box
	 * 
	 * @return Maximum x coordinate
	 */
	public int getMaxX() {
		int tmp = 0;
		
		for (Segment seg : segments)
			tmp = Math.max(tmp, seg.getMaxX());
		
		return tmp;
	}

	/**
	 * Get maximum y coordinate of logo bounding box
	 * 
	 * @return Maximum x coordinate
	 */
	public int getMaxY() {
		int tmp = 0;
		
		for (Segment seg : segments)
			tmp = Math.max(tmp, seg.getMaxY());
		
		return tmp;
	}

}
