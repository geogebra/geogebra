/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoNumeric;

/**
 * Slider mover for GeoNumeric and AlgoLocusSlider
 */
public class SliderMover extends PathMoverGeneric{

	private static final int BOUNDS_FIXED = 1;
	private static final int BOUNDS_INFINITE = 2;
	private static final int BOUNDS_FIXED_INFINITE = 3;
	private static final int BOUNDS_INFINITE_FIXED = 4;
	/** slider */
	private GeoNumeric slider;

	/**
	 * Creates new path mover for given path
	 * 
	 * @param path slider
	 */
	public SliderMover(GeoNumeric path) {
		this.slider = path;
	}

	/**
	 * @param p initial value
	 */
	public void init(GeoNumeric p) {
		init(p.getValue());
	}

	private void init(double param) {
		start_param = param;

		min_param = slider.getIntervalMin();
		max_param = slider.getIntervalMax();

		// make sure start_param is between min and max
		if (start_param < min_param || start_param > max_param) {
			param_extent = max_param - min_param;
			start_param = (start_param - min_param) % param_extent;
			if (start_param < min_param)
				start_param += param_extent;
		}

		if (min_param == Double.NEGATIVE_INFINITY) {
			if (max_param == Double.POSITIVE_INFINITY) {
				// (-infinite, +infinite)
				mode = BOUNDS_INFINITE;

				// infFunction(-1, 1)
				min_param = -1 + OPEN_BORDER_OFFSET;
				max_param = 1 - OPEN_BORDER_OFFSET;

				// transform start parameter to be in (-1, 1)
				start_param = PathNormalizer.inverseInfFunction(start_param);
			} else {
				// (-infinite, max_param]
				mode = BOUNDS_INFINITE_FIXED;
				start_param = 0;

				// max_param + infFunction(-1 ... 0)
				offset = max_param;
				min_param = -1 + OPEN_BORDER_OFFSET;
				max_param = 0;
			}
		} else {
			if (max_param == Double.POSITIVE_INFINITY) {
				// [min_param, +infinite)
				mode = BOUNDS_FIXED_INFINITE;
				start_param = 0;

				// min_param + infFunction(0 ... 1)
				offset = min_param;
				min_param = 0;
				max_param = 1 - OPEN_BORDER_OFFSET;
			} else {
				// [min_param, max_param]
				mode = BOUNDS_FIXED;
			}
		}

		param_extent = max_param - min_param;
		start_paramUP = start_param + param_extent;
		start_paramDOWN = start_param - param_extent;

		max_step_width = param_extent / MIN_STEPS;
		posOrientation = true;
		resetStartParameter();

		// System.out.println("init Path mover");
		// System.out.println("  min_param : " + min_param);
		// System.out.println("  max_param : " + max_param);
		// System.out.println("  start_param : " + start_param);
		// System.out.println("  max_step_width : " + max_step_width);
	}


	/**
	 * @param p store current position to p
	 */
	public void getCurrentPosition(GeoNumeric p) {
		calcPoint(p);
	}

	/**
	 * @param p number to store result
	 * @return true if it was possible
	 */
	public boolean getNext(GeoNumeric p) {
		// check if we are in our interval
		boolean lineTo = true;
		last_param = curr_param;
		lastMaxBorderSet = maxBorderSet;
		lastMinBorderSet = minBorderSet;

		// in the last step we got outside a border and stopped there
		// now continue at the other border
		if (maxBorderSet) {
			curr_param = min_param;
			lineTo = false; // path.isClosedPath();
			maxBorderSet = false;
		} else if (minBorderSet) {
			curr_param = max_param;
			lineTo = false; // path.isClosedPath();
			minBorderSet = false;
		}

		// STANDARD CASE
		else {
			double new_param = curr_param + step_width;

			// new_param too big
			if (new_param >= max_param) {
				// slow down by making smaller steps
				while (new_param >= max_param && smallerStep()) {
					new_param = curr_param + step_width;
				}

				// max border reached
				if (new_param >= max_param) {
					new_param = max_param;
					maxBorderSet = true;
				}
			}

			// new_param too small
			else if (new_param <= min_param) {
				// slow down by making smaller steps
				while (new_param <= min_param && smallerStep()) {
					new_param = curr_param + step_width;
				}

				// min border reached
				if (new_param <= min_param) {
					new_param = min_param;
					minBorderSet = true;
				}
			}

			// set parameter
			curr_param = new_param;
		}

		// calculate point for current parameter
		calcPoint(p);

		return lineTo;
	}

	/**
	 * Updates path parameter of point p from curr_param
	 * 
	 * @param p point to store result
	 */
	protected void calcPoint(GeoNumeric p) {
		double param;
		switch (mode) {
		case BOUNDS_FIXED:
			param = curr_param;
			break;

		case BOUNDS_INFINITE:
			param = PathNormalizer.infFunction(curr_param);
			break;

		case BOUNDS_FIXED_INFINITE:
		case BOUNDS_INFINITE_FIXED:
			param = offset + PathNormalizer.infFunction(curr_param);
			break;

		default:
			param = Double.NaN;
		}

		// PathParameter pp = p.getPathParameter();
		// pp.t = param;
		// path.pathChanged(p);
		// p.updateCoords();
		p.setValue(param);
	}

}
