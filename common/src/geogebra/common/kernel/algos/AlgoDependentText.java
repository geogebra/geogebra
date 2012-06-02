/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.MyStringBuffer;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.AbstractApplication;

import java.util.HashSet;
import java.util.Iterator;

/**
 * 
 * @author Markus
 */
public class AlgoDependentText extends AlgoElement {

	private ExpressionNode root; // input
	private GeoText text; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for result
	 * @param root
	 *            root element
	 */
	public AlgoDependentText(Construction cons, String label,
			ExpressionNode root) {
		super(cons);
		this.root = root;

		text = new GeoText(cons);
		setInputOutput(); // for AlgoElement

		//set text traceable to spreadsheet, if possible
		setSpreadsheetTraceableText();

		// compute value of dependent number
		compute();
		text.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param root
	 *            root element
	 */
	public AlgoDependentText(Construction cons, ExpressionNode root) {
		super(cons);
		this.root = root;

		text = new GeoText(cons);
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoDependentText;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TEXT;
	}

	/**
	 * @return root expression
	 */
	public ExpressionNode getRoot() {
		return root;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = root.getGeoElementVariables();

		super.setOutputLength(1);
		super.setOutput(0, text);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting text
	 */
	public GeoText getGeoText() {
		return text;
	}

	private StringTemplate oldTpl;

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		StringTemplate tpl = text.getStringTemplate();
		if (oldTpl != tpl) {
			oldTpl = tpl;
			for (int i = 0; i < input.length; i++) {
				if (input[i].isGeoText() && !input[i].isLabelSet()
						&& input[i].getParentAlgorithm()!=null){
					input[i].setVisualStyle(text);
					input[i].getParentAlgorithm().update();
				}
			}
		}

		try {
			boolean latex = text.isLaTeX();
			root.setHoldsLaTeXtext(latex);

			String str;
			if (latex) {
				str = root.evaluate(tpl).toLaTeXString(false, tpl);
			} else {
				str = root.evaluate(tpl).toValueString(tpl);
			}

			text.setTextString(str);
		} catch (Exception e) {
			text.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// was defined as e.g. text0 = "Radius: " + r
		if (root == null)
			return "";
		return root.toString(tpl);
	}

	@Override
	final public String toRealString(StringTemplate tpl) {
		// was defined as e.g. text0 = "Radius: " + r
		if (root == null)
			return "";
		return root.toRealString(tpl);
	}


	private void setSpreadsheetTraceableText(){

		NumberValue numToTrace = null;

		/*
		AbstractApplication.debug("\nroot: "+root+
				"\nleft: "+root.getLeftTree()+
				"\nright: "+root.getRightTree()+
				"\ngeos:"+root.getVariables()+
				"\nright geos:"+root.getRightTree().getVariables()
				);
				//*/

		HashSet<GeoElement> rightGeos = root.getVariables();

		Iterator<GeoElement> it = rightGeos.iterator();

		while (it.hasNext()) {
			GeoElement geo = it.next();

			if (geo.isNumberValue()) {
				if (numToTrace == null) {
					numToTrace = (NumberValue) geo;
				} else {
					// more than one NumberValue in expression, so don't want to trace
					return;
				}
			}

		}


		text.setSpreadsheetTraceable(new ExpressionNode(kernel, new MyStringBuffer(kernel, ((GeoElement) numToTrace).getLabel(StringTemplate.defaultTemplate))), (NumberValue) numToTrace);



		//AbstractApplication.debug("\nleft string : "+root.getLeftTree().evaluate(StringTemplate.defaultTemplate).toValueString(StringTemplate.defaultTemplate));
		//AbstractApplication.debug("\nleft string latex : "+root.getLeftTree().evaluate(StringTemplate.defaultTemplate).toLaTeXString(false, StringTemplate.defaultTemplate));




	}
}
