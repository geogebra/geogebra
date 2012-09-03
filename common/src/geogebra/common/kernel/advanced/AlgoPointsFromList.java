/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;


/**
 * Converts a list into a Point (or Points)
 * adapted from AlgoRootsPolynomial
 * 
 * @author Michael
 */
public class AlgoPointsFromList extends AlgoElement {

	private GeoList list; // input 
	private GeoPoint[] points; // output

	private String[] labels;
	private boolean initLabels, setLabels;


	public AlgoPointsFromList(
			Construction cons,
			String[] labels,
			boolean setLabels,
			GeoList list) {
		super(cons);
		this.list = list;

		this.labels = labels;
		this.setLabels = setLabels; // should labels be used?


				//  make sure root points is not null
		int number = labels == null ? 1 : Math.max(1, labels.length);
		points = new GeoPoint[0];
		initPoints(number);
		initLabels = true;  

		setInputOutput(); // for AlgoElement    
		compute();        

		// show at least one root point in algebra view
		// this is enforced here:
		if (!points[0].isDefined()) {
			points[0].setCoords(0,0,1);
			points[0].update();
			points[0].setUndefined();
			points[0].update();
		}
	}

	/**
	 * The given labels will be used for the resulting points.   
	 */
	public void setLabels(String[] labels) {
		this.labels = labels;
		setLabels = true;

		// make sure that there are at least as many
		// points as labels
		if (labels != null)
			initPoints(labels.length);

		update();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoPointsFromList;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		input = new GeoElement[1];
		input[0] = list;

		super.setOutput(points);
		for (int i=1; i < points.length; i++) {
			points[i].showUndefinedInAlgebraView(false);
		}
		setDependencies();
	}

	public GeoPoint[] getPoints() {
		return points;
	}

	@Override
	public void compute() {
		int n;
		if (!list.isDefined() || (n = list.size()) == 0) {
			setPoints(null, null, 0);
			return;
		}
		
		int length = -1;
		double[] x = new double[n];
		double[] y = new double[n];
		
		// handle Point[ {1,2} ] case
		if (list.size() == 2) {
			GeoElement arg0, arg1;	
			if ((arg0 = list.get(0)).isGeoNumeric() && (arg1 = list.get(1)).isGeoNumeric()) {
				x[0] = ((GeoNumeric)arg0).getDouble();
				y[0] = ((GeoNumeric)arg1).getDouble();
				length = 1;
			}
		}
		
		if (length == -1) {			
			// handle Point[ { {1,2}, {3,4} } ] case
			for (int i = 0 ; i < n ; i ++) {
				GeoElement geo = list.get(i);
				if (geo.isGeoList()) {
					GeoList geoList = ((GeoList)geo);
					GeoElement geoX = geoList.get(0);
					GeoElement geoY = geoList.get(1);
					x[i] = ((GeoNumeric)geoX).getDouble();
					y[i] = ((GeoNumeric)geoY).getDouble();
				}
			}
			length = x.length;
			
		}

		if (length > 0) setPoints(x, y, length);
		
	}

	// roots array and number of roots
	final void setPoints(double[] x, double[] y, int number) {
		initPoints(number);

		// now set the new values of the roots
		for (int i = 0; i < number; i++) {
			points[i].setCoords(x[i], y[i], 1.0);
		}

		// all other roots are undefined
		for (int i = number; i < points.length; i++) {
			points[i].setUndefined();
		}

		if (setLabels)
			updateLabels(number);
	}

	// number is the number of current roots
	private void updateLabels(int number) {  
		if (initLabels) {
			GeoElement.setLabels(labels, points);
			initLabels = false;
		} else {	    
			for (int i = 0; i < number; i++) {
				//  check labeling      
				if (!points[i].isLabelSet()) {
					// use user specified label if we have one
					String newLabel = (labels != null && i < labels.length) ? labels[i] : null;	            	
					points[i].setLabel(newLabel);	                
				}
			}
		}

		// all other roots are undefined
		for (int i = number; i < points.length; i++) {
			points[i].setUndefined();
		}
	}

	/**
	 * Removes only one single output element if possible. 
	 * If this is not possible the whole algorithm is removed.
	 */
	@Override
	public void remove(GeoElement output) {
		// only single undefined points may be removed       
		for (int i = 0; i < points.length; i++) {
			if (points[i] == output && !points[i].isDefined()) {
				removeRootPoint(i);      		
				return;
			}            
		}

		// if we get here removing output was not possible
		// so we remove the whole algorithm
		super.remove();
	}

	private void initPoints(int number) {
		// make sure that there are enough points   
		if (points.length < number) {
			GeoPoint[] temp = new GeoPoint[number];
			for (int i = 0; i < points.length; i++) {
				temp[i] = points[i];
				temp[i].setCoords(0, 0, 1); // init as defined
			}
			for (int i = points.length; i < temp.length; i++) {
				temp[i] = new GeoPoint(cons);
				temp[i].setCoords(0, 0, 1); // init as defined
				temp[i].setParentAlgorithm(this);
			}
			points = temp;
			super.setOutput(points);
		}
	}

	private void removeRootPoint(int pos) {
		points[pos].doRemove();

		// build new rootPoints array without the removed point
		GeoPoint[] temp = new GeoPoint[points.length - 1];
		int i;
		for (i=0; i < pos; i++) 
			temp[i] = points[i];        		
		for (i=pos+1; i < points.length; i++) 
			temp[i-1] = points[i];
		points = temp;
	}

	// TODO Consider locusequability


}
