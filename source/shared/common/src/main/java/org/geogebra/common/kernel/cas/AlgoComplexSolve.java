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

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.Operation;

/**
 * Algo for CAS commands CSolve and CSolutions
 */
public class AlgoComplexSolve extends AlgoSolve {

	/**
	 * @param c Construction
	 * @param eq Equation or list thereof
	 * @param hint Variables or variable = initial value
	 * @param type Whether to use CSolve/CSolutions
	 */
	public AlgoComplexSolve(Construction c, GeoElement eq, GeoElement hint, Commands type) {
		super(c, eq, hint, type);
	}

	@Override
	protected void convertOutputToSameType(GeoList raw) {
		raw.replaceAll(geo -> {
			if (geo instanceof GeoList) {
				convertOutputToSameType((GeoList) geo);
				return geo;
			}
			String var;
			GeoSymbolicPoint pt = new GeoSymbolicPoint(geo.getConstruction());
			ExpressionNode definition = geo.getDefinition();
			if (geo instanceof GeoNumeric) {
				pt.setCoords(geo.evaluateDouble(), 0 , 1);
				var = geo.getLabelSimple();
				definition = addImaginaryPart(definition);
			} else if (geo instanceof GeoPlaneND) {
				Coords equationVector = ((GeoPlaneND) geo).getCoordSys().getEquationVector();
				pt.setCoords(equationVector.getW() / equationVector.getZ(), 0 , 1);
				var = "z";
			} else if (geo instanceof GeoLine) {
				var = ((EquationValue) geo).getEquationVariables()[0];
				pt.setCoords(((GeoLine) geo).getZ(), 0 , 1);
			} else {
				pt.set(geo);
				var = geo.getLabelSimple();
			}
			pt.setComplex();
			if (getClassName() == Commands.CSolve && var != null) {
				pt.setComplexSolutionVar(var);
			}
			if (definition.unwrap() instanceof Equation) {
				definition = addImaginaryPart(((Equation) definition.unwrap()).getRHS());
			}
			pt.setDefinition(definition);
			pt.setSymbolicMode(solutions.isSymbolicMode(), false);
			return pt;
		});
	}

	private ExpressionNode addImaginaryPart(ExpressionNode definition) {
		return definition.plus(new ExpressionNode(kernel, new MyDouble(kernel, 0),
				Operation.MULTIPLY, kernel.getImaginaryUnit()));
	}

	private static class GeoSymbolicPoint extends GeoPoint implements HasSymbolicMode {
		private boolean symbolicMode;

		public GeoSymbolicPoint(Construction construction) {
			super(construction);
		}

		@Override
		public void initSymbolicMode() {
			symbolicMode = true;
		}

		@Override
		public void setSymbolicMode(boolean symbolicMode, boolean updateParent) {
			this.symbolicMode = symbolicMode;
		}

		@Override
		public boolean isSymbolicMode() {
			return symbolicMode;
		}

		@Override
		public String toValueString(StringTemplate tpl) {
			ExpressionNode definition = getDefinition();
			return symbolicMode && definition != null
					? prependCSolveVar(definition.toValueString(tpl), tpl)
					: super.toValueString(tpl);
		}

		private String prependCSolveVar(String defString, StringTemplate tpl) {
			return complexSolutionVar == null ? defString
					: complexSolutionVar + tpl.getEqualsWithSpace() + defString;
		}

		@Override
		public boolean supportsEngineeringNotation() {
			return false;
		}

		@Override
		public void setEngineeringNotationMode(boolean mode) {
			// no engineering
		}

		@Override
		public boolean isEngineeringNotationMode() {
			return false;
		}

		@Override
		public GeoSymbolicPoint copy() {
			GeoSymbolicPoint copy = new GeoSymbolicPoint(cons);
			copy.set(this);
			copy.setSymbolicMode(symbolicMode, false);
			return copy;
		}

		@Override
		public boolean isLaTeXDrawableGeo() {
			return true;
		}
	}
}
