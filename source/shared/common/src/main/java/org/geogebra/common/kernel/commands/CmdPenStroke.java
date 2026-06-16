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

package org.geogebra.common.kernel.commands;

import java.util.ArrayList;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoLocusStroke;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

public class CmdPenStroke extends CommandProcessor {
	private final boolean bezier;

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdPenStroke(Kernel kernel, boolean bezier) {
		super(kernel);
		this.bezier = bezier;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		int size = c.getArgumentNumber();
		ArrayList<MyPoint> myPoints = new ArrayList<>();
		double[] flat = new double[size];
		for (int i = 0; i < size; i++) {
			ExpressionValue current = c.getArgument(i).unwrap();
			if (bezier && current instanceof MyDouble) {
				flat[i] = current.evaluateDouble();
			} else if (current instanceof MyDouble && i < size - 1) {
				ExpressionValue next = c.getArgument(i + 1).unwrap();
				myPoints.add(new MyPoint(current.evaluateDouble(),
						next.evaluateDouble()));
				i++;
			} else if (current instanceof MyVecNode vec) {
				myPoints.add(new MyPoint(vec.getX().evaluateDouble(),
						vec.getY().evaluateDouble()));
			} else {
				throw argErr(c, current);
			}
		}
		AlgoLocusStroke algo;
		if (bezier) {
			algo = new AlgoLocusStroke(cons, new ArrayList<>());
			algo.getPenStroke().setBezierCoords(flat);
		} else {
			algo = new AlgoLocusStroke(cons, myPoints);
		}
		algo.getOutput(0).setLabel(c.getLabel());
		return algo.getOutput();
	}
}
