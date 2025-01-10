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

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Algorithm that takes a list of GeoElement objects to build a Geolist with
 * them.
 *
 * @author Markus Hohenwarter
 */
public class AlgoDependentList extends AlgoElement implements DependentAlgo {

	private ArrayList<? extends GeoElementND> listItems; // input GeoElements
	private GeoList geoList; // output

	private boolean isCellRange;
	private StringBuilder sb;

	/**
	 * Creates an unlabeled algorithm that takes a list of GeoElements to build
	 * a Geolist with them.
	 * 
	 * @param cons
	 *            construction
	 * @param listItems
	 *            list of GeoElement objects
	 * @param isCellRange
	 *            true for A1:A10
	 */
	public AlgoDependentList(Construction cons,
			ArrayList<? extends GeoElementND> listItems, boolean isCellRange) {
		super(cons);
		this.listItems = listItems;
		this.isCellRange = isCellRange;

		// create output object
		geoList = new GeoList(cons);
		setInputOutput();

		// compute value of dependent number
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.Expression;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		// create input array from listItems array-list
		// and fill the geoList with these objects
		int size = listItems.size();
		input = new GeoElement[size];
		for (int i = 0; i < size; i++) {
			input[i] = listItems.get(i).toGeoElement();

			if (!input[i].isLabelSet()) {
				input[i].setLabelWanted(false);
			}
		}

		setOnlyOutput(geoList);

		if (isCellRange) {
			setDependenciesOutputOnly();
		} else {
			setDependencies(); // done by AlgoElement
		}
	}

	/**
	 * Call super.remove() to remove the list. Then remove all unlabeled input
	 * objects (= list elements)
	 */
	@Override
	public void remove() {
		if (removed) {
			return;
		}
		super.remove();

		// removing unlabeled input
		for (int i = 0; i < input.length; i++) {
			if (!input[i].isLabelSet()) {
				input[i].remove();
			}
		}

	}

	/**
	 * Returns the list
	 * 
	 * @return the list as geo
	 */
	public GeoList getGeoList() {
		return geoList;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_CREATE_LIST;
	}

	@Override
	public final void compute() {
		geoList.clear();
		for (int i = 0; i < input.length; i++) {
			// add input and its siblings to the list
			// if the siblings are of the same type

			AlgoElement algo = input[i].getParentAlgorithm();
			if (algo != null && algo.getOutputLength() > 1
					&& algo.hasSingleOutputType()) {
				// all siblings have same type: add them all
				for (int k = 0; k < algo.getOutputLength(); k++) {
					GeoElement geo = algo.getOutput(k);
					if ((geo == input[i] || geo.isDefined())
							&& !geoList.listContains(geo)) {
						geoList.add(geo);
					}
				}
			} else {
				// independent or mixed sibling types:
				// add only this element
				geoList.add(input[i]);
			}
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		if (geoList.getDefinition() != null) {
			return geoList.getDefinition().toString(tpl);
		}
		if (sb == null) {
			sb = new StringBuilder();
		} else {
			sb.setLength(0);
		}

		tpl.leftCurlyBracket(sb);

		if (input.length > 0) {
			for (int i = 0; i < input.length - 1; i++) {
				sb.append(input[i].getLabel(tpl));
				sb.append(", ");
			}
			sb.append(input[input.length - 1].getLabel(tpl));
		}

		tpl.rightCurlyBracket(sb);

		return sb.toString();
	}

	@Override
	public ExpressionNode getExpression() {
		return geoList.wrap();
	}

	@Override
	protected void getExpXML(StringTemplate tpl, StringBuilder sb) {
		if (!isDefinedAsEmpty()) {
			super.getExpXML(tpl, sb);
		}
	}

	private boolean isDefinedAsEmpty() {
		return geoList.getDefinition() != null
				&& (geoList.getDefinition().unwrap() instanceof MyList)
				&& ((MyList) geoList.getDefinition().unwrap()).size() == 0;
	}

}
