package pl.edu.pw.elka.mmarkiew.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

//////import java.util.HashMap;

public class Segmentator {

	private int width;
	private int height;
	private boolean[][] binaryPixels;
	private Point[][] pointsAssignment;
	private HashMap<Integer, Segment> segments = new HashMap<>();
//////	private HashMap<Integer, Segment> segments = new HashMap<>();
//////	private int[][] pixelAssignmentToSegment;
	
	public Segmentator(final boolean[][] binaryPixels) {
		this.width = binaryPixels.length;
		this.height = binaryPixels[0].length;

		this.binaryPixels = binaryPixels;
		this.pointsAssignment = new Point[width][height];

		for (int x = 0; x < width; ++x)
			for (int y = 0; y < height; ++y)
				pointsAssignment[x][y] = new Point(x, y, -1);
//////		this.pixelAssignmentToSegment = new int[this.width][this.height];
	}
	
	public ArrayList<Segment> getSegments() {
		return new ArrayList<Segment>(this.segments.values());
	}

	public void segmentation() {
		int segmentId = 0;
		ArrayList<Integer> removeSegmentsAfter = new ArrayList<>();

		for (int y = 0; y < height; ++y)
			for (int x = 0; x < width; ++x) {
//				if (binaryPixels[x][y]) {
					Point c = new Point(x, y, -1);

					if (y == 0) {
						if (x == 0 || binaryPixels[x][y] != binaryPixels[x - 1][y]) {
							Segment s = new Segment(segmentId++, width, height);
							c.segmentId = segmentId - 1;
							s.addPoint(c);
							segments.put(segmentId - 1, s);
							pointsAssignment[x][y].segmentId = segmentId - 1;
						} else {
							c.segmentId = pointsAssignment[x - 1][y].segmentId;
							segments.get(c.segmentId).addPoint(c);
							pointsAssignment[x][y].segmentId = c.segmentId;
						}
					} else {
						if (x == 0) {
							if (binaryPixels[x][y] != binaryPixels[x][y - 1]) {
								Segment s = new Segment(segmentId++, width, height);
								c.segmentId = segmentId - 1;
								s.addPoint(c);
								segments.put(segmentId - 1, s);
								pointsAssignment[x][y].segmentId = segmentId - 1;
							} else {
								c.segmentId = pointsAssignment[x][y - 1].segmentId;
								segments.get(c.segmentId).addPoint(c);
								pointsAssignment[x][y].segmentId = c.segmentId;
							}
						} else {
							if (binaryPixels[x][y] == binaryPixels[x - 1][y]) {
								c.segmentId = pointsAssignment[x - 1][y].segmentId;
								pointsAssignment[x][y].segmentId = c.segmentId;
								
								// Check top
								if (binaryPixels[x][y] == binaryPixels[x][y - 1] &&
											pointsAssignment[x][y].segmentId != pointsAssignment[x][y - 1].segmentId) {
									
									int upVal = pointsAssignment[x][y - 1].segmentId;
									int leftVal = pointsAssignment[x - 1][y].segmentId;
									int addToThisId = Math.min(upVal, leftVal);
		                            int removeThisId = Math.max(upVal, leftVal);

									c.segmentId = addToThisId;
									pointsAssignment[x][y].segmentId = c.segmentId;
									Segment addItemsTo = segments.get(addToThisId);
									addItemsTo.addPoint(c);

									Segment toBeRemoved = segments.get(removeThisId);
									LinkedList<Point> pointses = toBeRemoved.getPoints();
									for (int i = 0; i < pointses.size(); ++i) {
										Point ic = pointses.get(i);
										ic.segmentId = addToThisId;
										pointsAssignment[ic.x][ic.y].segmentId = addToThisId;
										addItemsTo.addPoint(ic);
									}
									
									segments.remove(removeThisId);
									
								} else {
									segments.get(c.segmentId).addPoint(c);
								}
							} else {
								if (binaryPixels[x][y] == binaryPixels[x][y - 1]) {
									c.segmentId = pointsAssignment[x][y - 1].segmentId;
									segments.get(c.segmentId).addPoint(c);
									pointsAssignment[x][y].segmentId = c.segmentId;
								} else {
									Segment s = new Segment(segmentId++, width, height);
									c.segmentId = segmentId - 1;
									s.addPoint(c);
									segments.put(segmentId - 1, s);
									pointsAssignment[x][y].segmentId = segmentId - 1;
								}
							}
						}
//					}
//					if (x > 0 && pointsAssignment[x - 1][y].segmentId >= 0) {
//						if (y > 0
//								&& pointsAssignment[x][y - 1].segmentId >= 0
//								&& pointsAssignment[x][y - 1].segmentId != pointsAssignment[x - 1][y].segmentId) {
//
//							int upVal = pointsAssignment[x][y - 1].segmentId;
//							int leftVal = pointsAssignment[x - 1][y].segmentId;
//							int addToThisId = Math.min(upVal, leftVal);
//							int removeThisId = Math.max(upVal, leftVal);
//							
//							c.segmentId = addToThisId;
//							Segment addItemsTo = segments.get(addToThisId);
//							addItemsTo.addPoint(c);
//
//							Segment toBeRemoved = segments.get(removeThisId);
//							for (int i = 0; i < toBeRemoved.getPoints().size(); ++i) {
//								Point ic = toBeRemoved.getPoints().get(i);
//								ic.segmentId = c.segmentId;
//								pointsAssignment[ic.x][ic.y].segmentId = c.segmentId;
//								addItemsTo.addPoint(ic);
//							}
//							
//							removeSegmentsAfter.add(removeThisId);
//						} else {
//							c.segmentId = pointsAssignment[x - 1][y].segmentId;
//							segments.get(c.segmentId).addPoint(c);
//						}
//					} else if (y > 0
//							&& pointsAssignment[x][y - 1].segmentId >= 0) {
//						c.segmentId = pointsAssignment[x][y - 1].segmentId;
//						segments.get(c.segmentId).addPoint(c);
//					} else {
//						Segment s = new Segment(segmentId++, width, height);
//						c.segmentId = segmentId - 1;
//						s.addPoint(c);
//						segments.add(segmentId - 1, s);
//					}

					pointsAssignment[x][y] = c;
				}
			}

//		Collections.sort(removeSegmentsAfter);
//		Collections.reverse(removeSegmentsAfter);
//		for (int i = 0; i < removeSegmentsAfter.size(); ++i)
////			if (removeSegmentsAfter.get(i) < segments.size())
//				segments.remove((int) removeSegmentsAfter.get(i));
//////		pixelAssignmentToSegment[0][0] = segmentId++;
//////
//////		for (int y = 0; y < height; ++y)
//////			for (int x = 1; x < width; ++x) {
//////
//////				if (y == 0) {
//////					if (binaryPixels[x][y] != binaryPixels[x - 1][y])
//////						pixelAssignmentToSegment[x][y] = segmentId++;
//////					else
//////						pixelAssignmentToSegment[x][y] = pixelAssignmentToSegment[x - 1][y];
//////				} else {
//////					if (x == 0) {
//////						if (binaryPixels[x][y] != binaryPixels[x][y - 1])
//////							pixelAssignmentToSegment[x][y] = segmentId++;
//////						else
//////							pixelAssignmentToSegment[x][y] = pixelAssignmentToSegment[x][y - 1];
//////					} else {
//////						if (binaryPixels[x][y] == binaryPixels[x - 1][y]) {
//////							pixelAssignmentToSegment[x][y] = pixelAssignmentToSegment[x - 1][y];
//////							
//////							// Check top
//////							if (binaryPixels[x][y] == binaryPixels[x][y - 1] &&
//////												pixelAssignmentToSegment[x][y] != pixelAssignmentToSegment[x][y - 1])
//////								replaceAssignment(pixelAssignmentToSegment[x][y], pixelAssignmentToSegment[x][y - 1],
//////																												x, y);
//////						} else {
//////							if (binaryPixels[x][y] == binaryPixels[x][y - 1])
//////								pixelAssignmentToSegment[x][y] = pixelAssignmentToSegment[x][y - 1];
//////							else
//////								pixelAssignmentToSegment[x][y] = segmentId++;
//////						}
//////					}
//////				}
//////			}
//////
//////		for (int y = 0; y < pixelAssignmentToSegment[0].length; ++y) {
//////			for (int x = 0; x < pixelAssignmentToSegment.length; ++x) {
//////				int id = pixelAssignmentToSegment[x][y];
//////
//////				if (segments.get(id) == null)
//////					segments.put(id, new Segment(id, width, height));
//////				
//////				segments.get(id).lightPixel(x, y);
//////			}
//////		}
	}
	
//////	public void replaceAssignment(final int from, final int to, final int xEnd, final int yEnd) {
//////		for (int y = 0; y < yEnd + 1; ++y)
//////			for (int x = 0; x < xEnd + 1; ++x)
//////				if (pixelAssignmentToSegment[x][y] == from)
//////					pixelAssignmentToSegment[x][y] = to;
//////	}
//////
//////	public HashMap<Integer, Segment> getSegments() {
//////		return this.segments;
//////	}
	
}
