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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyStringBuffer;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * 
 * @author Markus
 */
public class AlgoDependentText extends AlgoElement implements DependentAlgo {

	private GeoText text; // output
	// Curve[If[t>1,(t,t)],t,0,5]
	/**
	 * @param cons
	 *            construction
	 * @param root
	 *            root element
	 */
	public AlgoDependentText(Construction cons, ExpressionNode root,
			boolean mayBeSpreadsheetTraceable) {
		super(cons);

		text = new GeoText(cons);
		text.setDefinition(root);
		setInputOutput(); // for AlgoElement
		if (mayBeSpreadsheetTraceable) {
			text.initSpreadsheetTraceableCase();
		}
		// compute value of dependent number
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.Expression;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TEXT;
	}

	/**
	 * @return root expression
	 */
	public ExpressionNode getRoot() {
		return text.getDefinition();
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = text.getDefinition().getGeoElementVariables();
		for (int i = 0; i < input.length; i++)
			if (input[i].isGeoText())
				((GeoText) input[i]).addTextDescendant(text);
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
						&& input[i].getParentAlgorithm() != null) {
					input[i].setVisualStyle(text);
					input[i].getParentAlgorithm().update();
				}
			}
		}

		nodeToGeoText(text.getDefinition(), text, tpl);

	}

	/**
	 * Converts expression node to geotext
	 * 
	 * @param root
	 *            expression
	 * @param text
	 *            text
	 * @param tpl
	 *            string template
	 */
	public final static void nodeToGeoText(ExpressionNode root, GeoText text,
			StringTemplate tpl) {
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
		if (text.getDefinition() == null)
			return "";
		return text.getDefinition().toString(tpl);
	}

	public void setSpreadsheetTraceableText() {

		/*
		 * AbstractApplication.debug("\nroot: "+root+
		 * "\nleft: "+root.getLeftTree()+ "\nright: "+root.getRightTree()+
		 * "\ngeos:"+root.getVariables()+
		 * "\nright geos:"+root.getRightTree().getVariables() ); //
		 */

		// find first NumberValue in expression and replace
		numToTraceSet = false;
		ExpressionNode copy = getSpecialCopy(text.getDefinition());

		// AbstractApplication.printStacktrace("XXX"+copy.evaluate(StringTemplate.defaultTemplate).toValueString(StringTemplate.defaultTemplate));

		// if (numToTrace != null) {
		// AbstractApplication.debug("YYY"+numToTrace.toOutputValueString(StringTemplate.defaultTemplate));
		// }

		text.setSpreadsheetTraceable(copy, numToTrace);

		// AbstractApplication.debug("\nleft string : "+root.getLeftTree().evaluate(StringTemplate.defaultTemplate).toValueString(StringTemplate.defaultTemplate));
		// AbstractApplication.debug("\nleft string latex : "+root.getLeftTree().evaluate(StringTemplate.defaultTemplate).toLaTeXString(false,
		// StringTemplate.defaultTemplate));

	}

	private ExpressionValue numToTrace;

	// adpated from ExpressionNode.getCopy()
	private ExpressionNode getSpecialCopy(ExpressionNode en) {
		// Application.debug("getCopy() input: " + this);
		ExpressionNode newNode = null;
		ExpressionValue lev = null, rev = null;

		ExpressionValue left = en.getLeft();
		ExpressionValue right = en.getRight();

		if (left != null) {
			lev = copy(left);
		}
		if (right != null) {
			rev = copy(right);
		}

		if (lev != null) {
			newNode = new ExpressionNode(kernel, lev, en.getOperation(), rev);
			newNode.leaf = en.leaf;
		} else {
			// something went wrong
			return null;
		}

		// set member vars that are not set by constructors
		// newNode.forceVector = forceVector;
		// newNode.forcePoint = forcePoint;
		// newNode.forceFunction = forceFunction;
		// Application.debug("getCopy() output: " + newNode);
		return newNode;
	}

	// adpated from ExpressionNode
	// finds first "+ NumberValue" and replaces with " x "
	// eg "value = "+x(A)+"cm"
	private ExpressionValue copy(ExpressionValue ev) {
		if (ev == null) {
			return null;
		}

		ExpressionValue ret = null;
		// Application.debug("copy ExpressionValue input: " + ev);
		if (ev.isNumberValue()) {
			// ************
			// replace first encountered NumberValue, eg x(A) with empty string
			// and make note
			// ************
			setNumToTrace(ev);
			ret = new MyStringBuffer(kernel, " ... ");
		} else if (ev instanceof ExpressionNode) {
			ExpressionNode en = (ExpressionNode) ev;
			ret = getSpecialCopy(en);
			// } else if (ev instanceof MyList) {
			// MyList en = (MyList) ev;
			// ret = getCopy(kernel, en);
		}
		// deep copy
		else if (ev.isConstant() || (ev instanceof Command)) {
			ret = ev.deepCopy(kernel);
		} else if (ev.isGeoElement()) {
			// eg FormulaText[x(A)]
			GeoElement geo = (GeoElement) ev;
			AlgoElement algo = geo.getParentAlgorithm();
			if (algo != null && algo.getInput().length > 0) {
				GeoElement geo2 = algo.getInput()[0];
				if (geo2.isNumberValue()) {
					setNumToTrace(geo2);
					ret = new MyStringBuffer(kernel, " ... ");
				} else {
					ret = ev;
				}
			} else {
				ret = ev;
			}
		} else {
			ret = ev;
		}
		// Application.debug("copy ExpressionValue output: " + ev);
		return ret;
	}

	private boolean numToTraceSet;

	private void setNumToTrace(ExpressionValue ev) {
		if (!numToTraceSet) {
			numToTrace = ev;
			numToTraceSet = true;
		} else
			numToTrace = null;
	}

	public ExpressionNode getExpression() {
		return text.getDefinition();
	}

	

}
