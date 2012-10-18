/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoPoint;

import java.util.ArrayList;

/**
 * Path mover for locus
 */
public class PathMoverLocus extends PathMoverGeneric {

	private ArrayList<MyPoint> myPointList;
	private boolean noLineToSet, lastNoLineToSet;

	/**
	 * Creates new path mover for given locus
	 * 
	 * @param locus locus
	 */
	public PathMoverLocus(GeoLocus locus) {
		super(locus);
		myPointList = locus.getPoints();
	}

	@Override
	public void init(GeoPoint p, int min_steps) {
		if (p.getPath() instanceof GeoLocus) {
			myPointList = ((GeoLocus) p.getPath()).getPoints();
		}
		lastNoLineToSet = noLineToSet = false;
		super.init(p, min_steps);
	}

	@Override
	public void init(GeoPoint p) {
		if (p.getPath() instanceof GeoLocus) {
			myPointList = ((GeoLocus) p.getPath()).getPoints();
		}
		lastNoLineToSet = noLineToSet = false;
		super.init(p);
	}

	@Override
	public void resetStartParameter() {
		super.resetStartParameter();
		noLineToSet = lastNoLineToSet = false;
	}

	@Override
	protected void calcPoint(GeoPoint p) {
		// curr_param is between 0 and myPointList.size()-1 now
		double param = curr_param;
		PathParameter pp = p.getPathParameter();
		pp.t = param;

		// PATH MOVER CHANGED PARAMETER (see PathMoverGeneric.calcPoint())
		// get points left and right of path parameter
		int leftIndex = (int) Math.max(0, Math.floor(param));
		int rightIndex = (int) Math.min(myPointList.size() - 1,
				Math.ceil(param));
		MyPoint leftPoint = myPointList.get(leftIndex);
		MyPoint rightPoint = myPointList.get(rightIndex);

		// interpolate between leftPoint and rightPoint
		double param1 = (param - leftIndex);
		double param2 = 1.0 - param1;
		p.x = param2 * leftPoint.x + param1 * rightPoint.x;
		p.y = param2 * leftPoint.y + param1 * rightPoint.y;
		p.z = 1.0;

		p.updateCoords();
	}

	@Override
	public boolean getNext(GeoPoint p) {
		// check if we are in our interval
		boolean lineTo = true;
		last_param = curr_param;
		lastMaxBorderSet = maxBorderSet;
		lastMinBorderSet = minBorderSet;
		lastNoLineToSet = noLineToSet;

		// in the last step we got outside a border and stopped there
		// now continue at the other border
		if (maxBorderSet) {
			curr_param = min_param;
			lineTo = path.isClosedPath();
			maxBorderSet = false;
		} else if (minBorderSet) {
			curr_param = max_param;
			lineTo = path.isClosedPath();
			minBorderSet = false;
		} else if (noLineToSet) {
			curr_param = borderParam(curr_param);
			lineTo = false;
			noLineToSet = false;
		}

		// STANDARD CASE
		else {
			double new_param = curr_param + step_width;

			// new_param too big
			if (new_param >= max_param) {
				// slow down by making smaller steps
				while ((new_param >= max_param || noLineTo(new_param))
						&& smallerStep()) {
					new_param = curr_param + step_width;
				}

				// max border reached
				if (new_param >= max_param) {
					new_param = max_param;
					maxBorderSet = true;
				} else if (noLineTo(new_param)) {
					new_param = borderParam(new_param);
					noLineToSet = true;
				}
			}

			// new_param too small
			else if (new_param <= min_param) {
				// slow down by making smaller steps
				while ((new_param <= min_param || noLineTo(new_param))
						&& smallerStep()) {
					new_param = curr_param + step_width;
				}

				// min border reached
				if (new_param <= min_param) {
					new_param = min_param;
					minBorderSet = true;
				} else if (noLineTo(new_param)) {
					new_param = borderParam(new_param);
					noLineToSet = true;
				}
			} else if (noLineTo(new_param)) {
				while (noLineTo(new_param) && smallerStep()) {
					new_param = curr_param + step_width;
				}

				if (noLineTo(new_param)) {
					new_param = borderParam(new_param);
					noLineToSet = true;
				}
			}

			// set parameter
			curr_param = new_param;
		}

		// calculate point for current parameter
		calcPoint(p);

		return lineTo;
	}

	@Override
	public void stepBack() {
		super.stepBack();
		noLineToSet = lastNoLineToSet;
	}

	/**
	 * @param new_param param of next point
	 * @return true if there is not continous line from cur_param to new_param
	 */
	protected boolean noLineTo(double new_param) {

		if (new_param >= max_param || new_param <= min_param) {
			// not right use case
			return false;
		}
		if (curr_param < new_param) {
			int leftIndexCurr = (int) Math.max(0, Math.floor(curr_param));
			int rightIndexNew = (int) Math.min(myPointList.size() - 1,
					Math.ceil(new_param));
			for (int i = leftIndexCurr + 1; i <= rightIndexNew; i++) {
				if (myPointList.get(i).lineTo == false)
					return true;
			}
		} else if (curr_param > new_param) {
			int leftIndexNew = (int) Math.max(0, Math.floor(new_param));
			int rightIndexCurr = (int) Math.min(myPointList.size() - 1,
					Math.ceil(curr_param));
			for (int i = leftIndexNew + 1; i <= rightIndexCurr; i++) {
				if (myPointList.get(i).lineTo == false)
					return true;
			}
		}
		return false;
	}

	/**
	 * @param param initial parameter
	 * @return parameter of nearest point with line to / move to
	 *         or min/max param if not found 
	 */
	protected double borderParam(double param) {

		if (curr_param < param) {
			return Math.min(myPointList.size() - 1, Math.ceil(curr_param));
		} else if (curr_param > param) {
			return Math.max(0, Math.floor(curr_param));
		}

		// from this, suppose param is already an index
		int paramindex = (int) Math.round(param);
		paramindex = Math.max(0, paramindex);
		paramindex = Math.min(paramindex, myPointList.size() - 1);
		if (posOrientation) {
			for (int i = paramindex + 2; i <= myPointList.size() - 1; i++) {
				// lineTo at i == paramindex + 1 cannot happen
				if (myPointList.get(i).lineTo == true)
					return i - 1;
			}
			maxBorderSet = true;
			return max_param;
		}
		for (int i = paramindex - 1; i >= 1; i--) {
			// lineTo at i == paramindex cannot happen
			if (myPointList.get(i).lineTo == true) {
				return i;
			}
		}
		minBorderSet = true;
		return min_param;
	}

	@Override
	public boolean hasNext() {
		// check if we pass the start parameter
		// from last_param to the next parameter curr_param
		boolean hasNext;

		double next_param = curr_param + step_width;

		if (posOrientation) {
			if (next_param < max_param) {
				int rightIndexNext = (int) Math.min(myPointList.size() - 1,
						Math.ceil(next_param));
				if (myPointList.get(rightIndexNext).lineTo == false) {
					next_param = max_param;
					for (int i = rightIndexNext + 1; i <= myPointList.size() - 1; i++) {
						if (myPointList.get(i).lineTo == true) {
							next_param = i - 1;
							break;
						}
					}
				}
			} else if (next_param > max_param) {
				int rightIndexNext = (int) Math.min(myPointList.size() - 1,
						Math.ceil(next_param - myPointList.size() + 1));
				double next_param_little = next_param - myPointList.size() + 1;
				if (myPointList.get(rightIndexNext).lineTo == false) {
					next_param_little = max_param;
					for (int i = rightIndexNext + 1; i <= myPointList.size() - 1; i++) {
						if (myPointList.get(i).lineTo == true) {
							next_param_little = i - 1;
							break;
						}
					}
				}
				next_param = next_param_little + myPointList.size() - 1;
			}

			hasNext = !(curr_param < start_param && next_param >= start_param || curr_param < start_paramUP
					&& next_param >= start_paramUP);
		} else {
			if (next_param > min_param) {
				int rightIndexNext = (int) Math.min(myPointList.size() - 1,
						Math.ceil(next_param));
				if (myPointList.get(rightIndexNext).lineTo == false) {
					next_param = min_param;
					for (int i = rightIndexNext - 1; i >= 1; i--) {
						if (myPointList.get(i).lineTo == true) {
							next_param = i;
							break;
						}
					}
				}
			} else if (next_param < min_param) {
				int rightIndexNext = (int) Math.min(myPointList.size() - 1,
						Math.ceil(next_param + myPointList.size() - 1));
				double next_param_big = next_param + myPointList.size() - 1;
				if (myPointList.get(rightIndexNext).lineTo == false) {
					next_param_big = min_param;
					for (int i = rightIndexNext - 1; i >= 1; i--) {
						if (myPointList.get(i).lineTo == true) {
							next_param_big = i;
							break;
						}
					}
				}
				next_param = next_param_big - myPointList.size() + 1;
			}

			hasNext = !(curr_param > start_param && next_param <= start_param || curr_param > start_paramDOWN
					&& next_param <= start_paramDOWN);
		}

		return hasNext;
	}
}
