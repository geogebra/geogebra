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

package org.geogebra.common.gui.view.table;

import java.util.HashMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDependentListExpression;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Operation;

/**
 * Implementation of TableValuesPoints
 */
public class TableValuesPointsImpl implements TableValuesPoints {

	private final HashMap<GeoEvaluatable, GeoList> points;
	private final Kernel kernel;
	private final Construction construction;
	private final TableValues view;

	/**
	 * Construct a new object of Table Values Points.
	 * @param kernel kernel
	 * @param construction the construction to add points to
	 * @param view table view
	 */
	public TableValuesPointsImpl(Kernel kernel, Construction construction,
			TableValues view) {
		this.kernel = kernel;
		this.points = new HashMap<>(2);
		this.construction = construction;
		this.view = view;
	}

	/**
	 * Construct and register a new object of Table Values Points.
	 * @param kernel kernel
	 * @param construction the construction to add points to
	 * @param view table view
	 * @return points model
	 */
	public static TableValuesPointsImpl create(Kernel kernel, Construction construction,
			TableValues view) {
		TableValuesPointsImpl instance = new TableValuesPointsImpl(kernel, construction,
				view);
		view.setTableValuePoints(instance);
		return instance;
	}

	/**
	 * @return points list size, if not initialized -1
	 */
	public int getPointsSize() {
		return points.size();
	}

	@Override
	public void createPointsIfNeeded(GeoEvaluatable evaluatable) {
		if (points.get(evaluatable) == null) {
			createAndAddPoints(evaluatable);
		}
	}

	@Override
	public boolean arePointsVisible(int column) {
		return points.get(view.getEvaluatable(column)) != null;
	}

	@Override
	public void setPointsVisible(int column, boolean visible) {
		GeoEvaluatable geoEvaluatable = view.getEvaluatable(column);
		geoEvaluatable.setPointsVisible(visible);
		if (visible && points.get(geoEvaluatable) == null) {
			createAndAddPoints(geoEvaluatable);
		} else if (!visible && points.get(geoEvaluatable) != null) {
			removePoints(geoEvaluatable);
		}
		construction.getKernel().getApplication().storeUndoInfo();
	}

	@Override
	public void clear() {
		points.clear();
	}

	@Override
	public void notifyPointsAdded(AlgoDependentListExpression listExpression) {
		ExpressionValue expr = listExpression.getExpression().unwrap();
		if (expr instanceof MyVecNode && ((MyVecNode) expr).getX().unwrap() == view.getValues()) {
			for (GeoElementND inp : listExpression.getInput()) {
				if (inp instanceof GeoEvaluatable && ((GeoEvaluatable) inp).getTableColumn() >= 0) {
					points.put((GeoEvaluatable) inp, listExpression.getList());
					listExpression.getList().setTableOrigin(true);
					return;
				}
			}
		}
	}

	@Override
	public void removeList(GeoElement element) {
		points.entrySet().removeIf(
				entry -> entry.getKey() == element || entry.getValue() == element);
	}

	// Expression construction utility methods

	/** For testing purposes only. */
	GeoList getPointListForColumn(int index) {
		return points.get(view.getEvaluatable(index));
	}

	private void createAndAddPoints(GeoEvaluatable evaluatable) {
		GeoList list = createPoints(evaluatable);
		points.put(evaluatable, list);
	}

	private GeoList createPoints(GeoEvaluatable evaluatable) {
		GeoList result;

		ExpressionNode xValues = view.getValues().wrap();
		ExpressionValue yValues;
		if (evaluatable.unwrapSymbolic() instanceof GeoList) {
			// Construct (xColumn, listColumn) dependent list
			result = buildPointList(xValues, evaluatable);
		} else {
			// Construct (xColumn, f(yColumn)) dependent list
			yValues = new ExpressionNode(kernel, evaluatable, Operation.FUNCTION, xValues);
			result = buildPointList(xValues, yValues);
		}
		stylePoints(result, evaluatable);
		return result;
	}

	private GeoList buildPointList(ExpressionValue xValues, ExpressionValue yValues) {
		GeoList result = null;
		ExpressionNode vecNode = new MyVecNode(kernel, xValues,
				yValues).wrap();
		try {
			result = (GeoList) kernel.getAlgebraProcessor().processValidExpression(vecNode)[0];
		} catch (CircularDefinitionException ex) {
			//
		}
		return result;
	}

	private void stylePoints(GeoList points, GeoEvaluatable evaluatable) {
		points.setPointStyle(EuclidianStyleConstants.POINT_STYLE_NO_OUTLINE);
		points.setAlgebraVisible(false);
		points.setAuxiliaryObject(true);
		points.setEuclidianVisible(true);
		points.setLabelVisible(false);
		points.setHasPreviewPopup(true);
		points.setFixed(true);
		points.setTableOrigin(true);
		points.setLabel("TableValuesPoints");
		//points.setLabelSet(true);
		maybeSetPointColor(points, evaluatable);
	}

	private static void maybeSetPointColor(GeoList points,
			GeoEvaluatable evaluatable) {
		GColor color = evaluatable instanceof GeoFunctionable ? evaluatable.getObjectColor()
				: GColor.Y_POINT_COLOR;
		points.setObjColor(color);
	}

	private void removePoints(GeoEvaluatable column) {
		GeoElement list = points.get(column);
		if (list != null) {
			list.remove();
		}
	}
}
