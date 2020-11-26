/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.LabelManager;

/**
 * Converts a list into a Point (or Points) adapted from AlgoRootsPolynomial
 * 
 * @author Michael
 */
public class AlgoPointsFromList extends AlgoElement {

	private GeoList list; // input
	private GeoPoint[] points; // output
	private GeoPoint3D[] points3D; // output for 3D

	private String[] labels;
	private boolean initLabels;
	private boolean setLabels;

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param setLabels
	 *            whether to set point labels
	 * @param list
	 *            list of numbers
	 */
	public AlgoPointsFromList(Construction cons, String[] labels,
			boolean setLabels, GeoList list) {
		super(cons);
		this.list = list;

		this.labels = labels;
		this.setLabels = setLabels; // should labels be used?

		// make sure root points is not null
		int number = labels == null ? 1 : Math.max(1, labels.length);
		if ((list.get(0).isGeoNumeric() && list.size() == 2)
				|| (list.get(0).isGeoList()
						&& ((GeoList) list.get(0)).size() == 2)) {
			points = new GeoPoint[0];
			initPoints(number);
			initLabels = true;

			setInputOutput(); // for AlgoElement
			compute();

			// show at least one root point in algebra view
			// this is enforced here:
			if (!points[0].isDefined()) {
				points[0].setCoords(0, 0, 1);
				points[0].update();
				points[0].setUndefined();
				points[0].update();
			}
		} else if ((list.get(0).isGeoNumeric() && list.size() == 3)
				|| (list.get(0).isGeoList()
						&& ((GeoList) list.get(0)).size() == 3)) {
			points3D = new GeoPoint3D[0];
			initPoints3D(number);
			initLabels = true;

			setInputOutput();
			compute();
			// show at least one root point in algebra view
			// this is enforced here:
			if (!points3D[0].isDefined()) {
				points3D[0].setCoords(0, 0, 1);
				points3D[0].update();
				points3D[0].setUndefined();
				points3D[0].update();
			}
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
		if (labels != null) {
			initPoints(labels.length);
		}

		update();
	}

	@Override
	public Commands getClassName() {
		return Commands.Point;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		input = new GeoElement[1];
		input[0] = list;
		if ((list.get(0).isGeoNumeric() && list.size() == 2)
				|| (list.get(0).isGeoList()
						&& ((GeoList) list.get(0)).size() == 2)) {
			super.setOutput(points);
			for (int i = 1; i < points.length; i++) {
				points[i].showUndefinedInAlgebraView(false);
			}
			setDependencies();
		} else if ((list.get(0).isGeoNumeric() && list.size() == 3)
				|| (list.get(0).isGeoList()
						&& ((GeoList) list.get(0)).size() == 3)) {
			super.setOutput(points3D);
			for (int i = 1; i < points3D.length; i++) {
				points3D[i].showUndefinedInAlgebraView(false);
			}
			setDependencies();
		}
	}

	public GeoPoint[] getPoints() {
		return points;
	}

	public GeoPoint3D[] getPoints3D() {
		return points3D;
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
		double[] z = new double[n];

		// handle Point[ {1,2} ] case
		if (list.get(0).isGeoNumeric() && list.size() == 2) {
			GeoElement arg0, arg1;
			if ((arg0 = list.get(0)).isGeoNumeric()
					&& (arg1 = list.get(1)).isGeoNumeric()) {
				x[0] = ((GeoNumeric) arg0).getDouble();
				y[0] = ((GeoNumeric) arg1).getDouble();
				length = 1;
			}
		}
		// handle Point[ {1,2,3} ] case
		if (list.get(0).isGeoNumeric() && list.size() == 3) {
			GeoElement arg0, arg1, arg2;
			if ((arg0 = list.get(0)).isGeoNumeric()
					&& (arg1 = list.get(1)).isGeoNumeric()
					&& (arg2 = list.get(2)).isGeoNumeric()) {
				x[0] = ((GeoNumeric) arg0).getDouble();
				y[0] = ((GeoNumeric) arg1).getDouble();
				z[0] = ((GeoNumeric) arg2).getDouble();
				length = 1;
			}
		}

		if (length == -1) {
			if (list.get(0).isGeoList()
					&& ((GeoList) list.get(0)).size() == 2) {
				// handle Point[ { {1,2}, {3,4} } ] case
				for (int i = 0; i < n; i++) {
					GeoElement geo = list.get(i);
					if (geo.isGeoList()) {
						GeoList geoList = ((GeoList) geo);
						if (geoList.size() < 2) {
							x[i] = Double.NaN;
							y[i] = Double.NaN;
						} else {
							GeoElement geoX = geoList.get(0);
							GeoElement geoY = geoList.get(1);
							x[i] = ((GeoNumeric) geoX).getDouble();
							y[i] = ((GeoNumeric) geoY).getDouble();
						}
					}
				}
				length = x.length;
			} else if (list.get(0).isGeoList()
					&& ((GeoList) list.get(0)).size() == 3) {
				// handle Point[ { {1,2,3}, {4,5,6} } ] case
				for (int i = 0; i < n; i++) {
					GeoElement geo = list.get(i);
					if (geo.isGeoList()) {
						GeoList geoList = ((GeoList) geo);
						if (geoList.size() < 3) {
							x[i] = Double.NaN;
							y[i] = Double.NaN;
							z[i] = Double.NaN;
						} else {
							GeoElement geoX = geoList.get(0);
							GeoElement geoY = geoList.get(1);
							GeoElement geoZ = geoList.get(2);
							x[i] = ((GeoNumeric) geoX).getDouble();
							y[i] = ((GeoNumeric) geoY).getDouble();
							z[i] = ((GeoNumeric) geoZ).getDouble();
						}
					}
				}
				length = x.length;
			}

		}

		if (length > 0) {
			if ((list.get(0).isGeoNumeric() && list.size() == 2)
					|| (list.get(0).isGeoList()
							&& ((GeoList) list.get(0)).size() == 2)) {
				setPoints(x, y, length);
			} else if ((list.get(0).isGeoNumeric() && list.size() == 3)
					|| (list.get(0).isGeoList()
							&& ((GeoList) list.get(0)).size() == 3)) {
				setPoints3D(x, y, z, length);
			}
		}
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

		if (setLabels) {
			updateLabels(number);
		}
	}

	// roots array and number of roots
	final void setPoints3D(double[] x, double[] y, double[] z, int number) {
		initPoints3D(number);

		// now set the new values of the roots
		for (int i = 0; i < number; i++) {
			points3D[i].setCoords(x[i], y[i], z[i], 1);
		}

		// all other roots are undefined
		for (int i = number; i < points3D.length; i++) {
			points3D[i].setUndefined();
		}

		if (setLabels) {
			updateLabels(number);
		}
	}

	// number is the number of current roots
	private void updateLabels(int number) {
		if (list == null || list.size() == 0) {
			return;
		}

		if ((list.get(0).isGeoNumeric() && list.size() == 2)
				|| (list.get(0).isGeoList()
						&& ((GeoList) list.get(0)).size() == 2)) {
			if (initLabels) {
				LabelManager.setLabels(labels, points);
				initLabels = false;
			} else {
				for (int i = 0; i < number; i++) {
					// check labeling
					if (!points[i].isLabelSet()) {
						// use user specified label if we have one
						String newLabel = (labels != null && i < labels.length)
								? labels[i] : null;
						points[i].setLabel(newLabel);
					}
				}
			}

			// all other roots are undefined
			for (int i = number; i < points.length; i++) {
				points[i].setUndefined();
			}
		} else if ((list.get(0).isGeoNumeric() && list.size() == 3)
				|| (list.get(0).isGeoList()
						&& ((GeoList) list.get(0)).size() == 3)) {
			if (initLabels) {
				LabelManager.setLabels(labels, points3D);
				initLabels = false;
			} else {
				for (int i = 0; i < number; i++) {
					// check labeling
					if (!points3D[i].isLabelSet()) {
						// use user specified label if we have one
						String newLabel = (labels != null && i < labels.length)
								? labels[i] : null;
						points3D[i].setLabel(newLabel);
					}
				}
			}

			// all other roots are undefined
			for (int i = number; i < points3D.length; i++) {
				points3D[i].setUndefined();
			}
		}
	}

	/**
	 * Removes only one single output element if possible. If this is not
	 * possible the whole algorithm is removed.
	 */
	@Override
	public void remove(GeoElement output) {
		// only single undefined points may be removed
		if ((list.get(0).isGeoNumeric() && list.size() == 2)
				|| (list.get(0).isGeoList()
						&& ((GeoList) list.get(0)).size() == 2)) {
			for (int i = 0; i < points.length; i++) {
				if (points[i] == output && !points[i].isDefined()) {
					removeRootPoint(i);
					return;
				}
			}
		} else if ((list.get(0).isGeoNumeric() && list.size() == 3)
				|| (list.get(0).isGeoList()
						&& ((GeoList) list.get(0)).size() == 3)) {
			for (int i = 0; i < points3D.length; i++) {
				if (points3D[i] == output && !points3D[i].isDefined()) {
					removeRootPoint(i);
					return;
				}
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

	private void initPoints3D(int number) {
		// make sure that there are enough points
		if (points3D.length < number) {
			GeoPoint3D[] temp = new GeoPoint3D[number];
			for (int i = 0; i < points3D.length; i++) {
				temp[i] = points3D[i];
				temp[i].setCoords(0, 0, 1); // init as defined
			}
			for (int i = points3D.length; i < temp.length; i++) {
				temp[i] = (GeoPoint3D) cons.getKernel().getGeoFactory().newPoint(3, cons);
				temp[i].setCoords(0, 0, 1); // init as defined
				temp[i].setParentAlgorithm(this);
			}
			points3D = temp;
			super.setOutput(points3D);
		}
	}

	private void removeRootPoint(int pos) {
		if ((list.get(0).isGeoNumeric() && list.size() == 2)
				|| (list.get(0).isGeoList()
						&& ((GeoList) list.get(0)).size() == 2)) {
			points[pos].doRemove();

			// build new rootPoints array without the removed point
			GeoPoint[] temp = new GeoPoint[points.length - 1];
			int i;
			for (i = 0; i < pos; i++) {
				temp[i] = points[i];
			}
			for (i = pos + 1; i < points.length; i++) {
				temp[i - 1] = points[i];
			}
			points = temp;
		} else if ((list.get(0).isGeoNumeric() && list.size() == 3)
				|| (list.get(0).isGeoList()
						&& ((GeoList) list.get(0)).size() == 3)) {
			points3D[pos].doRemove();

			// build new rootPoints array without the removed point
			GeoPoint3D[] temp = new GeoPoint3D[points3D.length - 1];
			int i;
			for (i = 0; i < pos; i++) {
				temp[i] = points3D[i];
			}
			for (i = pos + 1; i < points3D.length; i++) {
				temp[i - 1] = points3D[i];
			}
			points3D = temp;
		}
	}

}
