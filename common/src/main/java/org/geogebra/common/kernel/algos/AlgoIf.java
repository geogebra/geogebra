/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.TestGeo;
import org.geogebra.common.plugin.Operation;

/**
 * 
 * @author Markus
 */
public class AlgoIf extends AlgoElement {

	private GeoElement result; // output
	private ArrayList<GeoElement> alternatives;
	private ArrayList<GeoBoolean> conditions;

	/**
	 * Algorithm for handling of an if-then-else construct
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param conditions
	 *            bool expressions
	 * @param alternatives
	 *            possible outputs
	 * 
	 */
	public AlgoIf(Construction cons, String label,
			ArrayList<GeoBoolean> conditions,
			ArrayList<GeoElement> alternatives) {
		super(cons);
		this.conditions = conditions;
		this.alternatives = alternatives;

		this.result = alternatives.get(0);
		// create output GeoElement of same type as ifGeo
		int i = 1;
		while (i < alternatives.size()
				&& TestGeo.canSet(alternatives.get(i), result)) {
			result = alternatives.get(i);
			i++;
		}
		result = result.copyInternal(cons);
		if (result.isGeoList()) {
			updateListType();
		}

		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
		result.setLabel(label);
	}

	private void updateListType() {
		for (GeoElement alt: alternatives) {
			if (alt.isGeoList()) {
				String typeStringForXML = ((GeoList) alt).getTypeStringForXML();
				if (typeStringForXML != null) {
					((GeoList) result).setTypeStringForXML(typeStringForXML);
					break;
				}
			}
		}
	}

	@Override
	public Commands getClassName() {
		return Commands.If;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[conditions.size() + alternatives.size()];
		for (int i = 0; i < this.conditions.size(); i++) {
			input[2 * i] = conditions.get(i);
			input[2 * i + 1] = alternatives.get(i);
		}
		if (alternatives.size() > conditions.size()) {
			input[input.length - 1] = alternatives.get(alternatives.size() - 1);
		}

		super.setOutputLength(1);
		super.setOutput(0, result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return result
	 */
	public GeoElement getGeoElement() {
		return result;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {

		try {
			for (int i = 0; i < conditions.size(); i++) {
				if (conditions.get(i).getBoolean()) {
					setResult(alternatives.get(i));

					return;
				}
				GeoElement last = alternatives.get(alternatives.size() - 1);
				if (conditions.size() == alternatives.size()) {
					result.setUndefined();
				} else {
					setResult(last);
				}

			}
		} catch (Exception e) {
			// e.printStackTrace();
			result.setUndefined();
		}
	}

	private void setResult(GeoElement newResult) {
		// undefined should work for all input types
		// we don't want to do list.set(number) as it has different semantics
		if (!newResult.isDefined()
				|| (result.isGeoList() && newResult.isGeoNumeric())) {
			result.setUndefined();
			return;
		}

		result.set(newResult);

		if (!newResult.isIndependent()) {
			result.setDefinition(newResult.getDefinition() == null ? null
					: newResult.getDefinition().deepCopy(kernel));
		}
		if (newResult.getDrawAlgorithm() instanceof DrawInformationAlgo) {
			result.setDrawAlgorithm(
					((DrawInformationAlgo) newResult.getDrawAlgorithm())
							.copy());
		}

	}

	/**
	 * For Curve[If[t&gt;0,t^2,-t^2],t,t,-5,5]
	 * 
	 * @return expression expansion of this algo
	 */
	public ExpressionNode toExpression() {
		if (this.alternatives.size() == 1) {
			return new ExpressionNode(kernel,
					kernel.convertNumberValueToExpressionNode(
							this.conditions.get(0)),
					Operation.IF, kernel.convertNumberValueToExpressionNode(
							this.alternatives.get(0)));
		} else if (this.conditions.size() == 1) {
			return new ExpressionNode(kernel,
					new MyNumberPair(kernel,
							kernel.convertNumberValueToExpressionNode(
									this.conditions.get(0)),
							kernel.convertNumberValueToExpressionNode(
									this.alternatives.get(0))),
					Operation.IF_ELSE,
					kernel.convertNumberValueToExpressionNode(
							this.alternatives.get(1)));
		}
		MyList cond = new MyList(kernel), funs = new MyList(kernel);
		for (GeoBoolean f : conditions) {
			cond.addListElement(kernel.convertNumberValueToExpressionNode(f));
		}
		for (GeoElement f : alternatives) {
			funs.addListElement(kernel.convertNumberValueToExpressionNode(f));
		}
		return new ExpressionNode(kernel, cond, Operation.IF_LIST, funs);
	}

	@Override
	public boolean isUndefined() {
		for (int i = 0; i < conditions.size(); i++) {
			if (conditions.get(i).getBoolean()) {
				return !alternatives.get(i).isDefined();
			}

		}
		return false;
	}

}
