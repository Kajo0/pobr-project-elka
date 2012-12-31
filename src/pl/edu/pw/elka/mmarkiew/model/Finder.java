package pl.edu.pw.elka.mmarkiew.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import pl.edu.pw.elka.mmarkiew.view.ImagePainter;

public class Finder {

	private ArrayList<Segment> segments = new ArrayList<>();
	private ArrayList<Segment> possibleSegments = new ArrayList<>();
	private ArrayList<AMDSegment> amdSegments = new ArrayList<>();
	
	public Finder(ArrayList<Segment> segments) {
		this.segments = segments;
	}

	public void find() {
		for (Segment segment : segments)
			findIfPossibleSegments(segment);

		findAMD();

		Pixel[][] pixels = new Pixel[segments.get(0).imageWidth][segments.get(0).imageHeight];
		for (int x = 0; x < pixels.length; ++x)
			for (int y = 0; y < pixels[0].length; ++y)
				pixels[x][y] = new Pixel(0);
		
		for (Segment segment : possibleSegments)
			for (Point point : segment.getPoints()) {
				pixels[point.x][point.y].r = (segment.type == SegmentType.LETTER_A ? 255 : 0);
				pixels[point.x][point.y].g = (segment.type == SegmentType.LETTER_M ? 255 : 0);
				pixels[point.x][point.y].b = (segment.type == SegmentType.LETTER_D ? 255 : 0);
			}
		
		new ImagePainter(Utilities.createImageFromPixels(pixels));
	}
	
	private void findIfPossibleSegments(Segment segment) {
//							M7 -> M3 -> W7 -> W8 -> W9 -> M1
		double aProbability = checkIfA(segment);
		double mProbability = checkIfM(segment);
		double dProbability = checkIfD(segment);
		
		if (aProbability > 0 || mProbability > 0 || dProbability > 0) {
			aProbability = (aProbability == 0 ? 9999 : aProbability);
			mProbability = (mProbability == 0 ? 9999 : mProbability);
			dProbability = (dProbability == 0 ? 9999 : dProbability);

			possibleSegments.add(segment);
//			System.out.println(segment);
//			SegmentType[] t = {SegmentType.LETTER_A, SegmentType.LETTER_M, SegmentType.LETTER_D};
//			segment.type = t[new Random().nextInt(3)];
			
			if (aProbability < mProbability) {
				if (aProbability < dProbability)
					segment.type = SegmentType.LETTER_A;
				else if (mProbability < dProbability)
					segment.type = SegmentType.LETTER_M;
				else
					segment.type = SegmentType.LETTER_D;
			} else if (mProbability < dProbability)
				segment.type = SegmentType.LETTER_M;
			else
				segment.type = SegmentType.LETTER_D;
		}
	}

	private double checkIfA(Segment segment) {
		double M7 = 0.015235764667050448,	m7l = 0.01167020015456442,	m7r = 0.018087107026053546;
		double W8 = 0.21031196473853922,	w8l = 0.16399082568807338,	w8r = 0.3018867924528302;
		double W7 = 0.09618590622537318,	w7l = 0.05555555555555555,	w7r = 0.2222222222222222;
		double W9 = 0.4312677287886182,	w9l = 0.37632973406571524,	w9r = 0.5350804078205331;
		double M3 = 0.010209099157830432,	m3l = 0.0044628508989242545,	m3r = 0.020645000937991043;
		double M1 = 0.2818378848836348,	m1l = 0.22242647058823528,	m1r = 0.33691269979583216;

//		double lr = 0.5, rl = 1.5;
		double lr = 0.7, rl = 1.3;
		
		m7l *= lr;
		m7r *= rl;
		w8l *= lr;
		w8r *= rl;
		w7l *= lr;
		w7r *= rl;
		w9l *= lr;
		w9r *= rl;
		m3l *= lr;
		m3r *= rl;
		m1l *= lr;
		m1r *= rl;
		
		double prob = 0;

		if (segment.getMoment(7) >= m7l && segment.getMoment(7) <= m7r) {
			prob += Math.abs(M7 - segment.getMoment(7));// / (m7r - m7l);

			if (segment.getMoment(3) >= m3l && segment.getMoment(3) <= m3r) {
				prob += Math.abs(M3 - segment.getMoment(3));// / (m3r - m3l);
				
				if (segment.getWoment(7) >= w7l && segment.getWoment(7) <= w7r) {
					prob += Math.abs(W7 - segment.getWoment(7));// / (w7r - w7l);

					if (segment.getWoment(8) >= w8l && segment.getWoment(8) <= w8r) {
						prob += Math.abs(W8 - segment.getWoment(8));// / (w8r - w8l);

						if (segment.getWoment(9) >= w9l && segment.getWoment(9) <= w9r) {
							prob += Math.abs(W9 - segment.getWoment(9));// / (w9r - w9l);

							if (segment.getMoment(1) >= m1l && segment.getMoment(1) <= m1r) {
								prob += Math.abs(M1 - segment.getMoment(1));// / (m1r - m1l);
								
								return prob;
							}
						}
					}
				}
			}
		}

		return 0;
	}

	private double checkIfM(Segment segment) {
		double M7 = 0.017500501180688226,	m7l = 0.010851376029452724,	m7r = 0.022203063652607036;
		double W8 = 0.19308847931575132,	w8l = 0.15454545454545454,	w8r = 0.29850746268656714;
		double W7 = 0.09638430564907972,	w7l = 0.0,	w7r = 0.1724137931034483;
		double W9 = 0.4109670811052067,	w9l = 0.34915257312687775,	w9r = 0.4961196399632683;
		double M3 = 1.9592520533750948E-4,	m3l = 2.0053774453794264E-6,	m3r = 8.394500479036301E-4;
		double M1 = 0.30006058856181006,	m1l = 0.2145311470740088,	m1r = 0.426053517064573;

//		double lr = 0.7, rl = 1.3;
		double lr = 0.8, rl = 1.2;
		
		m7l *= lr;
		m7r *= rl;
		w8l *= lr;
		w8r *= rl;
		w7l *= lr;
		w7r *= rl;
		w9l *= lr;
		w9r *= rl;
		m3l *= lr;
		m3r *= rl;
		m1l *= lr;
		m1r *= rl;
		
		double prob = 0;

		if (segment.getMoment(7) >= m7l && segment.getMoment(7) <= m7r) {
			prob += Math.abs(M7 - segment.getMoment(7));// / (m7r - m7l);

			if (segment.getMoment(3) >= m3l && segment.getMoment(3) <= m3r) {
				prob += Math.abs(M3 - segment.getMoment(3));// / (m3r - m3l);

				if (segment.getWoment(7) >= w7l && segment.getWoment(7) <= w7r) {
					prob += Math.abs(W7 - segment.getWoment(7));// / (w7r - w7l);

					if (segment.getWoment(8) >= w8l && segment.getWoment(8) <= w8r) {
						prob += Math.abs(W8 - segment.getWoment(8));// / (w8r - w8l);

						if (segment.getWoment(9) >= w9l && segment.getWoment(9) <= w9r) {
							prob += Math.abs(W9 - segment.getWoment(9));// / (w9r - w9l);

							if (segment.getMoment(1) >= m1l && segment.getMoment(1) <= m1r) {
								prob += Math.abs(M1 - segment.getMoment(1));// / (m1r - m1l);
								
								return prob;
							}
						}
					}
				}
			}
		}

		return 0;
	}

	private double checkIfD(Segment segment) {
		double M7 = 0.020365120630589456,	m7l = 0.012886541408450096,	m7r = 0.023036918303589227;
		double W8 = 0.19332569667910052,	w8l = 0.15995260663507108,	w8r = 0.26666666666666666;
		double W7 = 0.2233540174872819,	w7l = 0.11764705882352941,	w7r = 0.2962962962962963;
		double W9 = 0.4274388630670708,	w9l = 0.37533107757367445,	w9r = 0.5202923494351547;
		double M3 = 0.0014564929351270149,	m3l = 3.817088406910104E-4,	m3r = 0.0030419047356112423;
		double M1 = 0.3277085894576783,	m1l = 0.23766447368421054,	m1r = 0.40262575472343864;

//		double lr = 0.8, rl = 1.3;
		double lr = 0.8, rl = 1.4;
		
		m7l *= lr;
		m7r *= rl;
		w8l *= lr;
		w8r *= rl;
		w7l *= lr;
		w7r *= rl;
		w9l *= lr;
		w9r *= rl;
		m3l *= lr;
		m3r *= rl;
		m1l *= lr;
		m1r *= rl;
		
		double prob = 0;

		if (segment.getMoment(7) >= m7l && segment.getMoment(7) <= m7r) {
			prob += Math.abs(M7 - segment.getMoment(7));// / (m7r - m7l);

			if (segment.getMoment(3) >= m3l && segment.getMoment(3) <= m3r) {
				prob += Math.abs(M3 - segment.getMoment(3));// / (m3r - m3l);

				if (segment.getWoment(7) >= w7l && segment.getWoment(7) <= w7r) {
					prob += Math.abs(W7 - segment.getWoment(7));// / (w7r - w7l);

					if (segment.getWoment(8) >= w8l && segment.getWoment(8) <= w8r) {
						prob += Math.abs(W8 - segment.getWoment(8));// / (w8r - w8l);

						if (segment.getWoment(9) >= w9l && segment.getWoment(9) <= w9r) {
							prob += Math.abs(W9 - segment.getWoment(9));// / (w9r - w9l);

							if (segment.getMoment(1) >= m1l && segment.getMoment(1) <= m1r) {
								prob += Math.abs(M1 - segment.getMoment(1));// / (m1r - m1l);
								
								return prob;
							}
						}
					}
				}
			}
		}

		return 0;
	}

	private void findAMD() {
		ArrayList<Segment> aSegments = new ArrayList<>();
		ArrayList<Segment> mSegments = new ArrayList<>();
		ArrayList<Segment> dSegments = new ArrayList<>();

		for (Segment segment : possibleSegments)
			if (segment.type == SegmentType.LETTER_A)
				aSegments.add(segment);
			else if (segment.type == SegmentType.LETTER_M)
				mSegments.add(segment);
			else if (segment.type == SegmentType.LETTER_D)
				dSegments.add(segment);

		for (Segment aSeg : aSegments) {
			int aMaxSize = Math.max(aSeg.getSegmentWidth(), aSeg.getSegmentHeight());

			for (Segment mSeg : mSegments) {
				double ddd = pointToPointDistance(aSeg.xWeightCenter(), aSeg.yWeightCenter(), mSeg.xWeightCenter(),
																								mSeg.yWeightCenter());

				int mMaxSize = Math.max(mSeg.getSegmentWidth(), mSeg.getSegmentHeight());

				if (ddd < aMaxSize / 2 + mMaxSize / 2 +
													Math.min(aSeg.getSegmentWidth(), aSeg.getSegmentHeight()) * 0.4)
					for (Segment dSeg : dSegments) {
						double dd = pointToPointDistance(mSeg.xWeightCenter(), mSeg.yWeightCenter(),
																		dSeg.xWeightCenter(), dSeg.yWeightCenter());

						int dMaxSize = Math.max(dSeg.getSegmentWidth(), dSeg.getSegmentHeight());

						if (dd < mMaxSize / 2 + dMaxSize / 2 +
													Math.min(dSeg.getSegmentWidth(), dSeg.getSegmentHeight()) * 0.4) {
							double d = pointToPointDistance(aSeg.xWeightCenter(), aSeg.yWeightCenter(),
																dSeg.xWeightCenter(), dSeg.yWeightCenter());

							if (d > ddd && d > dd)
								if (Math.max(aMaxSize, mMaxSize) / Math.min(aMaxSize, mMaxSize) < 4 &&
										Math.max(mMaxSize, dMaxSize) / Math.min(mMaxSize, dMaxSize) < 4 &&
										Math.max(aMaxSize, dMaxSize) / Math.min(aMaxSize, dMaxSize) < 4) {

									double amAngle = Math.abs(Math.atan2(aSeg.yWeightCenter() - mSeg.yWeightCenter(),
																			aSeg.xWeightCenter() - mSeg.xWeightCenter()));
									double mdAngle = Math.abs(Math.atan2(mSeg.yWeightCenter() - dSeg.yWeightCenter(),
																			mSeg.xWeightCenter() - dSeg.xWeightCenter()));
									double adAngle = Math.abs(Math.atan2(aSeg.yWeightCenter() - dSeg.yWeightCenter(),
																			aSeg.xWeightCenter() - dSeg.xWeightCenter()));
	
									double aver = (amAngle + mdAngle + adAngle) / 3;
									double dt = 0.3;
									
									if (amAngle > aver - dt && amAngle < aver + dt &&
											mdAngle > aver - dt && mdAngle < aver + dt &&
											adAngle > aver - dt && adAngle < aver + dt)
										amdSegments.add(new AMDSegment(aSeg, mSeg, dSeg));
								}
						}
					}
			}
		}
	}
	
	private double pointToPointDistance(int x1, int y1, int x2, int y2) {
		int x = x2 - x1;
		int y = y2 - y1;

		return Math.sqrt(x * x + y * y);
	}
	
	public ArrayList<AMDSegment> getAMDSegments() {
		return this.amdSegments;
	}

}
