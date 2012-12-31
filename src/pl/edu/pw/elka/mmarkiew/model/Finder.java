package pl.edu.pw.elka.mmarkiew.model;

import java.util.ArrayList;
import pl.edu.pw.elka.mmarkiew.view.ImagePainter;

/**
 * Class responsible for finding logo from segments
 * Compare sizes and position
 * 
 * @author Mikolaj Markiewicz
 */
public class Finder {

	/** List of segments */
	private ArrayList<Segment> segments = new ArrayList<>();
	
	/** List of segments which possible made logo */
	private ArrayList<Segment> possibleSegments = new ArrayList<>();
	
	/** List of found AMD segments */
	private ArrayList<AMDSegment> amdSegments = new ArrayList<>();

	/**
	 * C-tor
	 * 
	 * @param segments Segments to find in
	 */
	public Finder(ArrayList<Segment> segments) {
		this.segments = segments;
	}

	/**
	 * Find logo from given segments
	 */
	public void find() {
		// Check whether segment is possible part of logo
		for (Segment segment : segments)
			findIfPossibleSegments(segment);

		// Find whole logo from possible segments
		findAMD();

		/*
		 * Mark found logos and show them in new window
		 */
		Pixel[][] pixels = new Pixel[segments.get(0).imageWidth][segments.get(0).imageHeight];
		for (int x = 0; x < pixels.length; ++x)
			for (int y = 0; y < pixels[0].length; ++y)
				pixels[x][y] = new Pixel(0);
		
		for (Segment segment : possibleSegments)
			for (Point point : segment.getPoints()) {
				pixels[point.x][point.y].r = (segment.type == SegmentType.LETTER_A ||
												segment.type == SegmentType.BOTTOM_LEFT_BOXY ? 255 : 0);
				pixels[point.x][point.y].g = (segment.type == SegmentType.LETTER_M ||
												segment.type == SegmentType.BOTTOM_LEFT_BOXY ||
												segment.type == SegmentType.TOP_RIGHT_BOXY ? 255 : 0);
				pixels[point.x][point.y].b = (segment.type == SegmentType.LETTER_D ||
												segment.type == SegmentType.TOP_RIGHT_BOXY ? 255 : 0);
			}
		
		new ImagePainter(Utilities.createImageFromPixels(pixels), "Marked segments");
	}
	
	/**
	 * Check whether segment is possible search segment
	 * 
	 * @param segment Segment to check
	 */
	private void findIfPossibleSegments(Segment segment) {
		/*
		 * Get probability of assigning to each searching segment type
		 */
		double aProbability = checkIfA(segment);
		double mProbability = checkIfM(segment);
		double dProbability = checkIfD(segment);
		double blbProbability = checkIfBottomLeftBoxy(segment);
		double trbProbability = checkIfTopRightBoxy(segment);

		/*
		 * If found probability as any segment add to possible
		 */
		if (aProbability > 0 || mProbability > 0 || dProbability > 0 || blbProbability > 0 || trbProbability > 0) {
			aProbability = (aProbability == 0 ? 9999 : aProbability);
			mProbability = (mProbability == 0 ? 9999 : mProbability);
			dProbability = (dProbability == 0 ? 9999 : dProbability);
			blbProbability = (blbProbability == 0 ? 9999 : blbProbability);
			trbProbability = (trbProbability == 0 ? 9999 : trbProbability);

			possibleSegments.add(segment);
			
			double minimum = Math.min(aProbability, Math.min(mProbability, Math.min(dProbability,
																		Math.min(blbProbability, trbProbability))));

			if (minimum == aProbability) {
				segment.type = SegmentType.LETTER_A;
			} else if (minimum == mProbability) {
				segment.type = SegmentType.LETTER_M;
			} else if (minimum == dProbability) {
				segment.type = SegmentType.LETTER_D;
			} else if (minimum == blbProbability) {
				segment.type = SegmentType.BOTTOM_LEFT_BOXY;
			} else {// if (minimum == trbProbability) {
				segment.type = SegmentType.TOP_RIGHT_BOXY;
			}

			segment.typeProbability = minimum;

			/*
			 * Less probability, means better adaptation to segment
			 * Mark segments what they are => set its type
			 */
//			if (aProbability < mProbability) {
//				if (aProbability < dProbability)
//					segment.type = SegmentType.LETTER_A;
//				else if (mProbability < dProbability)
//					segment.type = SegmentType.LETTER_M;
//				else
//					segment.type = SegmentType.LETTER_D;
//			} else if (mProbability < dProbability)
//				segment.type = SegmentType.LETTER_M;
//			else
//				segment.type = SegmentType.LETTER_D;
//			
//			segment.typeProbability = Math.min(aProbability, Math.min(mProbability, dProbability));
		}
	}

	/**
	 * Check probability of assigning to A letter
	 * 
	 * @param segment Segment to check
	 * @return Probability of match or 0 if doesn't match at all
	 */
	private double checkIfA(Segment segment) {
		/*
		 * Computed ranges
		 */
		double M7 = 0.015235764667050448,	m7l = 0.01167020015456442,		m7r = 0.018087107026053546;
		double W8 = 0.21031196473853922,	w8l = 0.16399082568807338,		w8r = 0.3018867924528302;
		double W7 = 0.09618590622537318,	w7l = 0.05555555555555555,		w7r = 0.2222222222222222;
		double W9 = 0.4312677287886182,		w9l = 0.37632973406571524,		w9r = 0.5350804078205331;
		double M3 = 0.010209099157830432,	m3l = 0.0044628508989242545,	m3r = 0.020645000937991043;
		double M1 = 0.2818378848836348,		m1l = 0.22242647058823528,		m1r = 0.33691269979583216;

		/*
		 * Left, right expand
		 */
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

		/*
		 * Check if match in range
		 */
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

	/**
	 * Check probability of assigning to M letter
	 * 
	 * @param segment Segment to check
	 * @return Probability of match or 0 if doesn't match at all
	 */
	private double checkIfM(Segment segment) {
		/*
		 * Computed ranges
		 */
		double M7 = 0.017500501180688226,	m7l = 0.010851376029452724,	m7r = 0.022203063652607036;
		double W8 = 0.19308847931575132,	w8l = 0.15454545454545454,	w8r = 0.29850746268656714;
		double W7 = 0.09638430564907972,	w7l = 0.0,	w7r = 0.1724137931034483;
		double W9 = 0.4109670811052067,	w9l = 0.34915257312687775,	w9r = 0.4961196399632683;
		double M3 = 1.9592520533750948E-4,	m3l = 2.0053774453794264E-6,	m3r = 8.394500479036301E-4;
		double M1 = 0.30006058856181006,	m1l = 0.2145311470740088,	m1r = 0.426053517064573;

		/*
		 * Left, right expand
		 */
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

		/*
		 * Check if match in range
		 */
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

	/**
	 * Check probability of assigning to D letter
	 * 
	 * @param segment Segment to check
	 * @return Probability of match or 0 if doesn't match at all
	 */
	private double checkIfD(Segment segment) {
		/*
		 * Computed ranges
		 */
		double M7 = 0.020365120630589456,	m7l = 0.012886541408450096,	m7r = 0.023036918303589227;
		double W8 = 0.19332569667910052,	w8l = 0.15995260663507108,	w8r = 0.26666666666666666;
		double W7 = 0.2233540174872819,	w7l = 0.11764705882352941,	w7r = 0.2962962962962963;
		double W9 = 0.4274388630670708,	w9l = 0.37533107757367445,	w9r = 0.5202923494351547;
		double M3 = 0.0014564929351270149,	m3l = 3.817088406910104E-4,	m3r = 0.0030419047356112423;
		double M1 = 0.3277085894576783,	m1l = 0.23766447368421054,	m1r = 0.40262575472343864;

		/*
		 * Left, right expand
		 */
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

		/*
		 * Check if match in range
		 */
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

	/**
	 * Check corresponding sizes, distances and directions to find logos
	 * Add found logos into amdSemgnets list
	 */
	private void findAMD() {
		/*
		 * List of each segments type
		 */
		ArrayList<Segment> aSegments = new ArrayList<>();
		ArrayList<Segment> mSegments = new ArrayList<>();
		ArrayList<Segment> dSegments = new ArrayList<>();

		/*
		 * Assign them onto proper list
		 */
		for (Segment segment : possibleSegments)
			if (segment.type == SegmentType.LETTER_A)
				aSegments.add(segment);
			else if (segment.type == SegmentType.LETTER_M)
				mSegments.add(segment);
			else if (segment.type == SegmentType.LETTER_D)
				dSegments.add(segment);

		/*
		 * Go through each segments
		 */
		for (Segment aSeg : aSegments) {
			int aMaxSize = Math.max(aSeg.getSegmentWidth(), aSeg.getSegmentHeight());

			/*
			 * With next one
			 */
			for (Segment mSeg : mSegments) {
				// Calculate distance between weight centers of considered segments
				double ddd = pointToPointDistance(aSeg.xWeightCenter(), aSeg.yWeightCenter(), mSeg.xWeightCenter(),
																								mSeg.yWeightCenter());

				int mMaxSize = Math.max(mSeg.getSegmentWidth(), mSeg.getSegmentHeight());

				/*
				 * If distance is not to big
				 */
				if (ddd < aMaxSize / 2 + mMaxSize / 2 +
													Math.min(aSeg.getSegmentWidth(), aSeg.getSegmentHeight()) * 0.4)
					/*
					 * Go through next one
					 */
					for (Segment dSeg : dSegments) {
						// Calculate next distance
						double dd = pointToPointDistance(mSeg.xWeightCenter(), mSeg.yWeightCenter(),
																		dSeg.xWeightCenter(), dSeg.yWeightCenter());

						int dMaxSize = Math.max(dSeg.getSegmentWidth(), dSeg.getSegmentHeight());

						/*
						 * If distance is appropriate
						 */
						if (dd < mMaxSize / 2 + dMaxSize / 2 +
													Math.min(dSeg.getSegmentWidth(), dSeg.getSegmentHeight()) * 0.4) {
							// Calc distance between first and third segmnet
							double d = pointToPointDistance(aSeg.xWeightCenter(), aSeg.yWeightCenter(),
																dSeg.xWeightCenter(), dSeg.yWeightCenter());

							/*
							 * If there has proper ratio
							 */
							if (d > ddd && d > dd)
								/*
								 * And if there are in similar size
								 */
								if (Math.max(aMaxSize, mMaxSize) / Math.min(aMaxSize, mMaxSize) < 4 &&
										Math.max(mMaxSize, dMaxSize) / Math.min(mMaxSize, dMaxSize) < 4 &&
										Math.max(aMaxSize, dMaxSize) / Math.min(aMaxSize, dMaxSize) < 4) {

									/*
									 * Calc direction
									 */
									double amAngle = Math.abs(Math.atan2(aSeg.yWeightCenter() - mSeg.yWeightCenter(),
																			aSeg.xWeightCenter() - mSeg.xWeightCenter()));
									double mdAngle = Math.abs(Math.atan2(mSeg.yWeightCenter() - dSeg.yWeightCenter(),
																			mSeg.xWeightCenter() - dSeg.xWeightCenter()));
									double adAngle = Math.abs(Math.atan2(aSeg.yWeightCenter() - dSeg.yWeightCenter(),
																			aSeg.xWeightCenter() - dSeg.xWeightCenter()));
	
									/*
									 * Get average direction and set accept differ
									 */
									double aver = (amAngle + mdAngle + adAngle) / 3;
									double dt = 0.3;
									
									/*
									 * If they are in the same line
									 */
									if (amAngle > aver - dt && amAngle < aver + dt &&
											mdAngle > aver - dt && mdAngle < aver + dt &&
											adAngle > aver - dt && adAngle < aver + dt)
										/*
										 * Add AMD logo into list as found logos!
										 */
										amdSegments.add(new AMDSegment(aSeg, mSeg, dSeg));
								}
						}
					}
			}
		}
	}
	/**
	 * Check probability of assigning to BOTTOM_LEFT_BOXY letter
	 * 
	 * @param segment Segment to check
	 * @return Probability of match or 0 if doesn't match at all
	 */
	private double checkIfBottomLeftBoxy(Segment segment) {
		/*
		 * Computed ranges
		 */
		double M7 = 0.01006087606634447,	m7l = 0.00893095564700503,	m7r = 0.010612771903258118;
		double W8 = 0.27841615088534183,	w8l = 0.22748815165876776,	w8r = 0.3584905660377358;
		double W7 = 0.09584026292864498,	w7l = 0.0,	w7r = 0.2;
		double W9 = 0.6191299122404113,	w9l = 0.5570199036889057,	w9r = 0.6812722500195435;
		double M3 = 0.0032730671570117787,	m3l = 0.0021232435303287973,	m3r = 0.007387269873406151;
		double M1 = 0.2277520247833174,	m1l = 0.20527777777777778,	m1r = 0.3011525620554507;

		/*
		 * Left, right expand
		 */
		double lr = 0.9, rl = 1.1;
		
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

		/*
		 * Check if match in range
		 */
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

	/**
	 * Check probability of assigning to TOP_RIGHT_BOXY letter
	 * 
	 * @param segment Segment to check
	 * @return Probability of match or 0 if doesn't match at all
	 */
	private double checkIfTopRightBoxy(Segment segment) {
		/*
		 * Computed ranges
		 */
		double M7 = 0.016750583364079735,	m7l = 0.01460895109865161,	m7r = 0.01862475224489796;
		double W8 = 0.28129323266771716,	w8l = 0.24170616113744076,	w8r = 0.3440366972477064;
		double W7 = 0.034393487345838504,	w7l = 0.0,	w7r = 0.0625;
		double W9 = 0.5355766842288621,	w9l = 0.43022679814997716,	w9r = 0.61744742344133;
		double M3 = 0.030138853380037044,	m3l = 0.017385017354562094,	m3r = 0.08499255340048821;
		double M1 = 0.3512951154473476,	m1l = 0.2863000524692911,	m1r = 0.5873122448979592;


		/*
		 * Left, right expand
		 */
		double lr = 0.9, rl = 1.1;
		
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

		/*
		 * Check if match in range
		 */
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

	/**
	 * Calculate distance between points
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return Distance between points
	 */
	private double pointToPointDistance(int x1, int y1, int x2, int y2) {
		int x = x2 - x1;
		int y = y2 - y1;

		return Math.sqrt(x * x + y * y);
	}

	/**
	 * Get found AMD segments
	 * 
	 * @return List of found segments
	 */
	public ArrayList<AMDSegment> getAMDSegments() {
		return this.amdSegments;
	}

}
