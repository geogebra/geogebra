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

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Creates one or more points from a numeric list passed to the {@code Point} command.
 * <p>
 * Each point is created from a numeric row. Rows with one or two values create a 2D point, using
 * {@code 0} for missing coordinates. Rows with three or more values create a 3D point; values
 * after the z-coordinate are ignored.
 * <p>
 * A flat numeric list is interpreted as one row and creates a single point. A nested list creates
 * one point from each numeric row. Each row is interpreted independently, so the result may contain
 * both 2D and 3D points.
 * <p>
 * Examples:
 * <ul>
 * <li>{@code Point({1})} creates {@code A = (1, 0)}</li>
 * <li>{@code Point({1, 2})} creates {@code A = (1, 2)}</li>
 * <li>{@code Point({1, 2, 3, 4})} creates {@code A = (1, 2, 3)}</li>
 * <li>{@code Point({{1}, {2, 3}, {4, 5, 6}, {7, 8, 9, 10}})} creates {@code A = (1, 0)},
 * {@code B = (2, 3)}, {@code C = (4, 5, 6)}, and {@code D = (7, 8, 9)}</li>
 * </ul>
 * <p>
 * When the source list changes, existing output points keep the dimension they were created with.
 * A 3D output can be updated from a 2D row, in which case z is set to {@code 0}. A 2D output
 * cannot be updated from a 3D row and becomes undefined until the corresponding row is 2D again.
 * If the list grows, new outputs are appended using the dimensions of the new rows; if it shrinks
 * or becomes unsupported, surplus outputs are kept but marked undefined.
 */
public class AlgoPointsFromList extends AlgoElement {
	private final GeoList inputList;
	private final List<GeoPointND> outputPoints;

	/**
	 * @param cons construction
	 * @param labels output labels
	 * @param setLabels whether to set point labels
	 * @param inputList list of numbers
	 */
	public AlgoPointsFromList(@Nonnull Construction cons, @CheckForNull String[] labels,
			boolean setLabels, @Nonnull GeoList inputList) {
		super(cons);
		List<ParsedPoint> parsedPoints = parsePoints(inputList);
		this.inputList = inputList;
		this.outputPoints = new ArrayList<>();
		addOutputPoints(parsedPoints, false);
		setInputOutput();
		compute();
		if (setLabels) {
			LabelManager.setLabels(labels, geoElementArrayOf(outputPoints));
		}
	}

	/**
	 * @return whether the list can initialize point outputs
	 */
	public static boolean isSupportedList(@Nonnull GeoList list) {
		return !parsePoints(list).isEmpty();
	}

	public List<GeoPointND> getOutputPoints() {
		return outputPoints;
	}

	@Override
	public Commands getClassName() {
		return Commands.Point;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[] {inputList};
		super.setOutput(geoElementArrayOf(outputPoints));
		for (int outputIndex = 1; outputIndex < outputPoints.size(); outputIndex++) {
			outputPoints.get(outputIndex).showUndefinedInAlgebraView(false);
		}
		setDependencies();
	}

	@Override
	public void compute() {
		// Parse the new input list
		List<ParsedPoint> parsedPoints = parsePoints(inputList);

		// Add new points to the output if necessary
		addOutputPoints(parsedPoints, true);

		// Update existing points
		int updatePointCount = Math.min(parsedPoints.size(), outputPoints.size());
		for (int index = 0; index < updatePointCount; index++) {
			// Update coordinates if they are compatible
			if (outputPoints.get(index).isGeoElement3D()
					|| parsedPoints.get(index).dimension() == 2) {
				if (outputPoints.get(index).isGeoElement3D()) {
					outputPoints.get(index).setCoords(parsedPoints.get(index).x(),
							parsedPoints.get(index).y(), parsedPoints.get(index).z(), 1);
				} else {
					outputPoints.get(index).setCoords(parsedPoints.get(index).x(),
							parsedPoints.get(index).y(), 1);
				}
			}
			// Otherwise mark them as undefined
			else {
				outputPoints.get(index).setUndefined();
			}
		}

		// Mark the rest (if any) as undefined
		for (int index = updatePointCount; index < outputPoints.size(); index++) {
			outputPoints.get(index).setUndefined();
		}
	}

	/**
	 * Removes one output element. Outputs with dependent objects are kept undefined.
	 */
	@Override
	public void remove(@Nonnull GeoElement output) {
		if (output == inputList) {
			super.remove();
			return;
		}
		if (output.getAlgoUpdateSet().isEmpty()) {
			output.doRemove();
			outputPoints.removeIf(point -> point == output);
			super.setOutput(geoElementArrayOf(outputPoints));
			if (outputPoints.isEmpty()) {
				super.remove();
			}
		} else {
			output.notifyRemove(); // Notify UI to remove the Algebra view item
			output.setUndefined();
			((GeoPointND) output).showUndefinedInAlgebraView(false);
			output.updateRepaint();
		}
	}

	private void addOutputPoints(@Nonnull List<ParsedPoint> parsedPoints, boolean setLabels) {
		int oldSize = outputPoints.size();
		for (int index = oldSize; index < parsedPoints.size(); index++) {
			GeoPointND point = parsedPoints.get(index).dimension() == 2 ? new GeoPoint(cons)
					: cons.getKernel().getGeoFactory().newPoint(3, cons);
			point.setCoords(0, 0, 1);
			outputPoints.add(point);
			setOutputDependencies(point);
			if (setLabels) {
				point.setLabel(null);
			}
			if (outputPoints.size() > 1) {
				point.showUndefinedInAlgebraView(false);
			}
		}
		if (oldSize < outputPoints.size()) {
			super.setOutput(geoElementArrayOf(outputPoints));
		}
	}

	private static List<ParsedPoint> parsePoints(@Nonnull GeoList list) {
		if (!list.isDefined() || list.size() == 0) {
			return List.of();
		}
		if (list.get(0).isGeoNumeric()) {
			return parseFlatList(list);
		}
		if (list.get(0).isGeoList()) {
			return parseNestedList(list);
		}
		return List.of();
	}

	private static List<ParsedPoint> parseFlatList(@Nonnull GeoList list) {
		if (!list.elements().allMatch(GeoElement::isGeoNumeric)) {
			return List.of();
		}
		return List.of(parsedPointOf(list, list.size() > 2 ? 3 : 2));
	}

	private static List<ParsedPoint> parseNestedList(@Nonnull GeoList list) {
		if (!list.elements().allMatch(row -> row instanceof GeoList
				&& ((GeoList) row).elements().allMatch(GeoElement::isGeoNumeric))) {
			return List.of();
		}
		return list.elements()
				.map(element -> (GeoList) element)
				.map(row -> parsedPointOf(row, row.size() > 2 ? 3 : 2))
				.toList();
	}

	private static ParsedPoint parsedPointOf(@Nonnull GeoList row, int dimension) {
		return new ParsedPoint(coordinateAt(0, row), coordinateAt(1, row),
				dimension == 3 ? coordinateAt(2, row) : 0, dimension);
	}

	private static double coordinateAt(int index, @Nonnull GeoList row) {
		return index < row.size() ? ((GeoNumeric) row.get(index)).getDouble() : 0;
	}

	private static GeoElement[] geoElementArrayOf(List<GeoPointND> geoPointNDs) {
		return geoPointNDs.stream().map(GeoPointND::toGeoElement).toArray(GeoElement[]::new);
	}

	private record ParsedPoint(double x, double y, double z, int dimension) {
	}
}
