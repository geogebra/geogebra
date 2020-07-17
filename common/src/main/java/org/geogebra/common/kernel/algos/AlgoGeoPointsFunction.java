/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.Arrays;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.LabelManager;

/**
 * Abstract class with all the label methods needed to update labels of commands
 * on functions, where the command returns a varying number of GeoPoints. This
 * is to avoid a lot of duplicated label-updating code. Most of the code is
 * copied from AlgoRootsPolynomial. (Where it might be eliminated later...)
 * @author Hans-Petter Ulven
 * @version 06.03.11
 */
public abstract class AlgoGeoPointsFunction extends AlgoElement {
	private static final int PIXELS_BETWEEN_SAMPLES = 5; // Open for empirical
	// adjustments
	/**
	 * Max number of samples; (covers a screen up to // 2000 pxs if
	 * 5-pix-convention)
	 **/
	protected static final int MAX_SAMPLES = 400;
	private static final int MIN_SAMPLES = 50; // -"- (covers up to 50 in a 250

	protected GeoPoint[] points; // output in subclass

	private String[] labels;
	private boolean initLabels;
	protected final boolean setLabels;

	/** left interval bound */
	protected GeoNumberValue left;
	/** right interval bound */
	protected GeoNumberValue right;
	/** whether min and max should be synced with EV */
	protected boolean intervalDefinedByEV = false;

	/**
	 * Computes all roots of f
	 * @param cons construction
	 * @param labels output labels
	 * @param setLabels whether to set labels
	 */
	public AlgoGeoPointsFunction(Construction cons, String[] labels,
			boolean setLabels) {
		super(cons);
		this.labels = labels;
		initLabels = true;
		updateLabelsFromOld();

		this.setLabels = setLabels;
		// make sure root points is not null
		int number = this.labels == null ? 1 : Math.max(1, this.labels.length);
		points = new GeoPoint[0];
		initPoints(number);

		// setInputOutput, compute(), show at least one point: must be done in
		// subclass.
	}

	private void updateLabelsFromOld() {
		if (labels != null && labels.length == 1) {
			GeoElement old = kernel.lookupLabel(labels[0]);
			if (old != null && old
					.getParentAlgorithm() instanceof AlgoGeoPointsFunction) {
				this.labels = new String[old.getParentAlgorithm()
						.getOutputLength()];
				for (int i = 0; i < this.labels.length; i++) {
					this.labels[i] = old.getParentAlgorithm().getOutput(i)
							.getLabelSimple();
				}
			}
		}
	}

	/**
	 * @param cons construction
	 */
	public AlgoGeoPointsFunction(Construction cons) {
		super(cons);
		setLabels = false;
		// make sure root points is not null
		int number = 1;
		points = new GeoPoint[0];
		initPoints(number);
		// setInputOutput, compute(), show at least one point: must be done in
		// subclass.
	}

	/**
	 * @return resulting points
	 */
	public GeoPoint[] getPoints() {
		return points;
	}

	// Show at least one root point in algebra view
	// Copied from AlgoRootsPolynomial...
	protected final void showOneRootInAlgebraView() {
		if (!points[0].isDefined()) {
			points[0].setCoords(0, 0, 1);
			points[0].update();
			points[0].setUndefined();
			points[0].update();
		} // if list not defined
	}

	protected static void removeDuplicates(double[] tab) {
		Arrays.sort(tab);
		int maxIndex = 0;
		double max = tab[0];
		for (int i = 1; i < tab.length; i++) {
			if ((tab[i] - max) > Kernel.MIN_PRECISION) {
				max = tab[i];
				maxIndex++;
				tab[maxIndex] = max;
			} // if greater
		} // for
	}

	protected final void setPoints(UnivariateFunction evaluatable, double[] curXValues,
			int number) {
		setPoints(curXValues, getYs(evaluatable, curXValues), number);
	}

	// roots array and number of roots
	protected final void setPoints(double[] curXValues, double[] curYValues, int number) {
		initPoints(number);

		// now set the new values of the roots
		for (int i = 0; i < number; i++) {
			points[i].setCoords(curXValues[i], curYValues[i], 1.0);
		}

		// all other roots are undefined
		for (int i = number; i < points.length; i++) {
			points[i].setUndefined();
		}

		if (setLabels) {
			updateLabels(number);
		}
		noUndefinedPointsInAlgebraView(points); // **** experiment****
	}

	// number is the number of current roots
	protected void updateLabels(int number) {
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
	}

	protected void noUndefinedPointsInAlgebraView(GeoPoint[] gpts) {
		for (int i = 1; i < gpts.length; i++) {
			gpts[i].showUndefinedInAlgebraView(false);
		}
	}

	/**
	 * Removes only one single output element if possible. If this is not
	 * possible the whole algorithm is removed.
	 */
	@Override
	public void remove(GeoElement output) {
		// only single undefined points may be removed
		for (int i = 0; i < points.length; i++) {
			if (points[i] == output && !points[i].isDefined()) {
				removePoint(i);

				// make sure that the function is removed after the last
				// undefined point was removed
				if (points.length == 0) {
					super.remove();
				}

				return;
			}
		}

		// if we get here removing output was not possible
		// so we remove the whole algorithm
		super.remove();
	}

	protected void initPoints(int number) {
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

	protected void removePoint(int pos) {
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
	}

	protected void updateInterval() {
		EuclidianViewInterfaceCommon ev = this.kernel.getApplication()
				.getActiveEuclidianView();

		left = ev.getXminObject();
		right = ev.getXmaxObject();
	}

	@Override
	protected int getInputLengthForXML() {
		if (intervalDefinedByEV) {
			return 1;
		}
		return super.getInputLengthForXML();
	}

	@Override
	public void resetLabels(String oldGeoLabel) {
		for (int i = 0; i < labels.length; i++) {
			if (oldGeoLabel.equals(labels[i])) {
				String swap = labels[i];
				labels[i] = labels[0];
				labels[0] = swap;
			}
		}
		updateLabels(getOutputLength());
	}

	/**
	 * @param l left bound
	 * @param r right bound
	 * @return number of samples based on zoom
	 */
	public final int findNumberOfSamples(double l, double r) {
		// Find visible area of graphic screen: xmin,xmax,ymin,ymax
		// pixels_in_visible_interval=...
		// n=pixels_in_visible_interval/PIXELS_BETWEEN_SAMPLES;

		// EuclidianView ev = app.getEuclidianView();
		double visiblemax = kernel.getViewsXMax(points[0]);
		double visiblemin = kernel.getViewsXMin(points[0]);
		double visiblepixs = kernel.getApplication().countPixels(visiblemin,
				visiblemax);
		// debug("Visible pixels: "+visiblepixs);
		double pixsininterval = visiblepixs * (r - l)
				/ (visiblemax - visiblemin);
		// debug("Pixels in interval: "+pixsininterval);
		double screenSamples = Math.min(pixsininterval / PIXELS_BETWEEN_SAMPLES,
				MAX_SAMPLES);
		if (Double.isNaN(screenSamples)) {
			return MIN_SAMPLES;
		}
		return (int) Math.round(Math.max(screenSamples, MIN_SAMPLES));
	}

	/**
	 * Helper method to get the y values for specific x values.
	 * @param evaluatable function
	 * @param xs x values
	 * @return y values
	 */
	protected double[] getYs(UnivariateFunction evaluatable, double[] xs) {
		double[] ys = new double[xs.length];
		for (int i = 0; i < xs.length; i++) {
			ys[i] = evaluatable.value(xs[i]);
		}
		return ys;
	}
}
