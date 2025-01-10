/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Find asymptotes
 * 
 * @author Michael Borcherds
 */
public class AlgoAsymptoteFunction extends AlgoElement implements UsesCAS {

	private final GeoFunction f; // input
	private final GeoList g; // output

	private final StringBuilder sb = new StringBuilder();

	// make sure Asymptote() gives undefined in Graphing/Geometry, not {}
	private boolean enabled = false;

	/**
	 * Asymptotes for function
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 */
	public AlgoAsymptoteFunction(Construction cons, String label,
			GeoFunction f) {
		super(cons);
		this.f = f;

		g = new GeoList(cons);
		g.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		g.setTypeStringForXML("line");
		setInputOutput(); // for AlgoElement
		compute();
		g.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Asymptote;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = f;

		setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return list of asymptotes
	 */
	public GeoList getResult() {
		return g;
	}

	@Override
	public final void compute() {

		if (!enabled) {
			// check again, CAS might now be loaded
			enabled = kernel.getApplication().getSettings().getCasSettings()
					.isEnabled();
		}

		if (!enabled || !f.isDefined() || !containsValidFunctionVariable()) {
			g.setUndefined();
			return;
		}

		try {
			sb.setLength(0);
			sb.append("{");
			boolean posHorizontal = f.getHorizontalPositiveAsymptote(sb);
			boolean negHorizontal = f.getHorizontalNegativeAsymptote(sb);
			if (!posHorizontal) {
				f.getDiagonalPositiveAsymptote(sb);
			}
			if (!negHorizontal) {
				f.getDiagonalNegativeAsymptote(sb);
			}

			f.getVerticalAsymptotes(sb);

			sb.append("}");
			g.set(kernel.getAlgebraProcessor().evaluateToList(sb.toString()));
		} catch (Throwable th) {
			g.setUndefined();
		}
	}

	/**
	 * @return True if the input function contains only one function variable that is x/y/z/t,
	 * false else
	 */
	private boolean containsValidFunctionVariable() {
		if (f.getFunctionVariables().length == 1) {
			String var = f.getFunctionVariables()[0].getSetVarString();
			return var.equals("x") || var.equals("y") || var.equals("z") || var.equals("t");
		}
		return false;
	}

}
