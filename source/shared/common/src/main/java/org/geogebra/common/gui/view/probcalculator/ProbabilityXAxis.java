/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.view.probcalculator;

import java.util.List;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoAxis;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Operation;

/**
 * The x axis of the probability calculator with low and high points.
 */
public class ProbabilityXAxis {
	private final Construction construction;
	private final Kernel kernel;
	private GeoPoint lowPoint;
	private GeoPoint highPoint;

	/**
	 * @param kernel the Kernel
	 */
	public ProbabilityXAxis(Kernel kernel) {
		this.kernel = kernel;
		this.construction = kernel.getConstruction();
		GeoAxis xAxis = construction.getXAxis();
		this.lowPoint = createAxisPoint(xAxis);
		this.highPoint = createAxisPoint(xAxis);
	}

	private GeoPoint createAxisPoint(GeoAxis axis) {
		AlgoPointOnPath algo = new AlgoPointOnPath(construction, axis, 0, 0);
		construction.removeFromConstructionList(algo);

		GeoPoint p = (GeoPoint) algo.getOutput(0);
		p.setObjColor(ProbabilityCalculatorView.COLOR_POINT);
		p.setPointSize(4);
		p.setPointStyle(
				EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH);
		p.setLayer(5);
		return p;
	}

	/**
	 *
	 * @return the lower point of the axis.
	 */
	public GeoPoint lowPoint() {
		return lowPoint;
	}

	/**
	 *
	 * @return the higher point of the axis.
	 */
	public GeoPoint highPoint() {
		return highPoint;
	}

	/**
	 * Add axis points to a list
	 * @param list to add to.
	 */
	public void addToList(List<GeoElement> list) {
		list.add(lowPoint);
		list.add(highPoint);
	}

	/**
	 *
	 * @return the expression of the low point.
	 */
	public ExpressionNode getLowExpression() {
		return getExpression(lowPoint);
	}

	private ExpressionNode getExpression(GeoPoint point) {
		return new ExpressionNode(kernel, point, Operation.XCOORD, null);
	}

	/**
	 *
	 * @return the expression of the high point.
	 */
	public ExpressionNode getHighExpression() {
		return getExpression(highPoint);
	}

	/**
	 * Show/hide both points together.
	 * @param b whether to show or not.
	 */
	public void showBothPoints(boolean b) {
		lowPoint.setEuclidianVisible(b);
		highPoint.setEuclidianVisible(b);
	}

	/**
	 * Show/hide low point only.
	 * @param b whether to show or not.
	 */
	public void showLowOnly(boolean b) {
		lowPoint.setEuclidianVisible(b);
		highPoint.setEuclidianVisible(false);
	}

	/**
	 * Show/hide high point only.
	 * @param b whether to show or not.
	 */
	public void showHighOnly(boolean b) {
		lowPoint.setEuclidianVisible(false);
		highPoint.setEuclidianVisible(b);
	}

	/**
	 * Sets animation step of the axis points.
	 * @param step to set.
	 */
	public void setAnimationStep(double step) {
		lowPoint.setAnimationStep(step);
		highPoint.setAnimationStep(step);
	}
}