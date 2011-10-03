/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


/**
 * Path mover for most common paths
 */
public class PathMoverGeneric implements PathMover {		
	
	private static final int BOUNDS_FIXED = 1;
	private static final int BOUNDS_INFINITE = 2;
	private static final int BOUNDS_FIXED_INFINITE = 3;
	private static final int BOUNDS_INFINITE_FIXED = 4;
	
	protected Path path;
	protected double start_param, start_paramUP, start_paramDOWN;
	protected double curr_param, last_param, param_extent, min_param, max_param,
					max_step_width, step_width, offset;
	protected int  mode;	
	protected boolean posOrientation;
	protected boolean maxBorderSet, minBorderSet, lastMaxBorderSet, lastMinBorderSet;
	
	/**
	 * Creates new path mover for given path
	 * @param path
	 */
	public PathMoverGeneric(Path path) {		
		this.path = path;
	}
	
	public void init(GeoPoint p) {
		PathParameter pp = p.getPathParameter();
		init(pp.t); 	
	}
	
	private void init(double param) {
		start_param = param; 

		min_param = path.getMinParameter();
		max_param = path.getMaxParameter();		
		
		// make sure start_param is between min and max
		if (start_param < min_param || start_param > max_param) {
			param_extent = max_param - min_param;	
			start_param = (start_param - min_param) % param_extent;
			if (start_param < min_param)
				start_param += param_extent;		
		}				
		
		if (min_param == Double.NEGATIVE_INFINITY) { 
			if (max_param == Double.POSITIVE_INFINITY){
				// (-infinite, +infinite)
				mode = BOUNDS_INFINITE;		
				
				// infFunction(-1, 1)
				min_param  = -1 + OPEN_BORDER_OFFSET;
				max_param  =  1 - OPEN_BORDER_OFFSET;
				
				// transform start parameter to be in (-1, 1)
				start_param = PathNormalizer.inverseInfFunction(start_param);										
			}
			else { 
				// (-infinite, max_param]
				mode = BOUNDS_INFINITE_FIXED;				
				start_param = 0;
			
				// max_param +  infFunction(-1 ... 0)				
				offset = max_param;
				min_param = -1 + OPEN_BORDER_OFFSET;
				max_param = 0;				
			}
		} 
		else {
			if (max_param == Double.POSITIVE_INFINITY){
				// [min_param, +infinite)
				mode = BOUNDS_FIXED_INFINITE;		
				start_param = 0;
				
				// min_param + infFunction(0 ... 1)	
				offset = min_param;
				min_param = 0;
				max_param = 1 - OPEN_BORDER_OFFSET;				
			}
			else { 
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
		
//		System.out.println("init Path mover");
//		System.out.println("  min_param : " + min_param);
//		System.out.println("  max_param : " + max_param);
//		System.out.println("  start_param : " + start_param);
//		System.out.println("  max_step_width : " + max_step_width);	
	}		
	
	public void resetStartParameter() {		
		curr_param = start_param;	
		last_param = start_param;
		maxBorderSet = lastMaxBorderSet = minBorderSet = lastMinBorderSet = false;
		step_width = max_step_width;		
	}
	
	public void getCurrentPosition(GeoPoint p) {
		calcPoint(p);
	}
	
	public boolean getNext(GeoPoint p) {													
		//  check if we are in our interval
		boolean lineTo = true;				
		last_param = curr_param;
		lastMaxBorderSet = maxBorderSet;
		lastMinBorderSet = minBorderSet;
		
		// in the last step we got outside a border and stopped there
		// now continue at the other border
		if (maxBorderSet) {			
			curr_param = min_param;	
			lineTo = path.isClosedPath();
			maxBorderSet = false;
		} 
		else if (minBorderSet) {
			curr_param = max_param;	
			lineTo = path.isClosedPath();
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
	 * @param p
	 */
	protected void calcPoint(GeoPoint p) {
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
		
		PathParameter pp = p.getPathParameter();
		pp.t = param;
		path.pathChanged(p);
		p.updateCoords();			
	}
	
	public boolean hasNext() {		
		// check if we pass the start parameter
		// from last_param to the next parameter curr_param										
		boolean hasNext;
		
		double next_param = curr_param + step_width;	
		if (posOrientation) {
			hasNext = !(curr_param < start_param && next_param >= start_param
				|| curr_param < start_paramUP && next_param >= start_paramUP);
		} else {
			hasNext = !(curr_param > start_param && next_param <= start_param
				|| curr_param > start_paramDOWN && next_param <= start_paramDOWN);
		}
							
		return hasNext;
	}
	
	public double getCurrentParameter() {
		return curr_param;
	}
	
	public void changeOrientation() {
		posOrientation = !posOrientation;
		step_width = -step_width;
	}


	public boolean hasPositiveOrientation() {		
		return posOrientation;
	}

	final public boolean smallerStep() {		
		return changeStep(step_width * STEP_DECREASE_FACTOR);		
	}

	final public boolean biggerStep() {
		return changeStep(step_width * STEP_INCREASE_FACTOR);
	}
	
	final public boolean setStep(double step) {
		return changeStep(step);
	}

	final public double getStep() {
		return step_width;
	}	
	
	private boolean changeStep(double new_step) {
		double abs_new_step = Math.abs(new_step);		
		
		if (abs_new_step > max_step_width) {
			if (new_step >= 0) {
				if (step_width == max_step_width)
					return false;
				step_width = max_step_width;
				return true;
			} else {
				if (step_width == -max_step_width)
					return false;
				step_width = -max_step_width;
				return true;
			}
		} 
		else if (abs_new_step < MIN_STEP_WIDTH) {
			if (new_step >= 0) {
				if (step_width == MIN_STEP_WIDTH)
					return false;
				step_width = MIN_STEP_WIDTH;
				return true;
			} else {
				if (step_width == -MIN_STEP_WIDTH)
					return false;
				step_width = -MIN_STEP_WIDTH;
				return true;
			}
		} 
		else {
			step_width = new_step;
			return true;
		}	
	}

	public void stepBack() {			
		curr_param = last_param;
		maxBorderSet = lastMaxBorderSet;
		minBorderSet = lastMinBorderSet;
	}
	

}
