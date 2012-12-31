package pl.edu.pw.elka.mmarkiew.model;

import java.util.LinkedList;

public class AMDSegment {

	private LinkedList<Segment> segments = new LinkedList<>();

	public AMDSegment(Segment a, Segment m, Segment d) {
		segments.add(a);
		segments.add(m);
		segments.add(d);
	}
	
	public void addSegment(Segment segment) {
		this.segments.add(segment);
	}

	public LinkedList<Segment> getSegments() {
		return this.segments;
	}

	public int getMinX() {
		int tmp = 999999;
		
		for (Segment seg : segments)
			tmp = Math.min(tmp, seg.getMinX());
		
		return tmp;
	}
	
	public int getMinY() {
		int tmp = 999999;
		
		for (Segment seg : segments)
			tmp = Math.min(tmp, seg.getMinY());
		
		return tmp;
	}

	public int getMaxX() {
		int tmp = 0;
		
		for (Segment seg : segments)
			tmp = Math.max(tmp, seg.getMaxX());
		
		return tmp;
	}
	
	public int getMaxY() {
		int tmp = 0;
		
		for (Segment seg : segments)
			tmp = Math.max(tmp, seg.getMaxY());
		
		return tmp;
	}

}
