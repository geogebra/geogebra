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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.TestGeo;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.debug.Log;

/**
 * n-th element of a GeoList object.
 * 
 * Note: the type of the returned GeoElement object is determined by the type of
 * the first list element. If the list is initially empty, a GeoNumeric object
 * is created for element.
 * 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoListElement extends AlgoElement {

	private GeoList geoList; // input
	private GeoNumberValue index;
	private GeoNumberValue[] indexes = null; // input
	private GeoElement indexGeo;
	private GeoElement element; // output
	private String elementLabel;

	public AlgoListElement(Construction cons, GeoList geoList,
			GeoNumberValue index) {
		this(cons, geoList, index, true);
	}

	/**
	 * Creates new labeled element algo
	 *
	 * @param cons
	 *            construction
	 * @param geoList
	 *            haystack
	 * @param index
	 *            index
	 */
	public AlgoListElement(Construction cons, GeoList geoList,
			GeoNumberValue index, boolean topLevel) {
		super(cons);
		this.geoList = geoList;
		this.index = index;
		indexGeo = index.toGeoElement();

		element = createGenericElementForFlatList(geoList, indexFrom(index), topLevel);

		if (element.isGeoPolygon()) { // ensure type will not be categorized as e.g.
										// "triangle"
			((GeoPolygon) element).setNotFixedPointsLength(true);
		}

		setInputOutput();
		compute();
	}

	private GeoElement createGenericElementForFlatList(GeoList list, int index, boolean topLevel) {
		if (list == null) {
			return getOutputGeo(topLevel);
		}

		if (index < list.size()) {
			return getGenericElement(list, index).copyInternal(cons);
		}

		if (!list.isEmptyList()) {
			return getGenericElement(list, 0).copyInternal(cons);
		}

		if (list.getTypeStringForXML() != null) {
			return kernel.createGeoElement(cons, list.getTypeStringForXML());
		}

		return getOutputGeo(topLevel);
	}

	private static GeoElement getGenericElement(GeoList geoList, int index) {
		GeoElement toCopy = geoList.get(index);
		if (geoList.getElementType() == GeoClass.DEFAULT
		// we have list {2,x}, not eg Factors[2x]
				&& (geoList.getParentAlgorithm() == null || geoList
						.getParentAlgorithm() instanceof AlgoDependentList)
				// for {a,x} also return number a, not function
				&& !Inspecting.isDynamicGeoElement(toCopy)) {
			for (int i = 0; i < geoList.size(); i++) {
				if (TestGeo.canSet(geoList.get(i), toCopy)) {
					toCopy = geoList.get(i);
				}
			}
		}
		return toCopy;
	}

	/**
	 * @param cons construction
	 * @param geoList list
	 * @param nums element coordinates
	 * @param topLevelCommand whether this is top level
	 */
	public AlgoListElement(Construction cons, GeoList geoList,
			GeoNumberValue[] nums, boolean topLevelCommand) {
		super(cons);
		this.geoList = geoList;
		this.indexes = nums;
		element = createGenericElementForNestedList(topLevelCommand);

		if (element == null) {
			element = getOutputGeo(topLevelCommand);
		}
		setInputOutput();
		compute();
	}

	private GeoElement getOutputGeo(boolean topLevelCommand) {
		return topLevelCommand ? cons.getOutputGeo() : new GeoNumeric(cons);
	}

	private GeoElement createGenericElementForNestedList(boolean topLevel) {
		try {
			GeoElement current = geoList;
			int depth = 0;
			while (isList(current) && depth < maxDepth() - 1) {
				current = getElementInDepth(current, depth);
				depth++;
			}
			return depth == maxDepth() - 1 && isList(current)
					? createGenericElementForFlatList((GeoList) current, 0, topLevel) : null;
		} catch (Exception e) {
			Log.debug("error initialising list");
		}
		return null;
	}

	private boolean isList(GeoElement geo) {
		return geo != null && geo.isGeoList();
	}

	private int maxDepth() {
		return indexes.length;
	}

	private GeoElement getElementInDepth(GeoElement current, int depth) {
		int initIndex = indexFrom(indexes[depth]);
		GeoList currentList = (GeoList) current;
		if (currentList.size() > initIndex) {
			return getCurrent(depth, currentList, initIndex);
		} else if (currentList.size() > 0) {
			return getCurrent(depth, currentList, 0);
		}
		return null;
	}

	private static int indexFrom(GeoNumberValue number) {
		return Math.max(0, (int) Math.round(number.getDouble()) - 1);
	}

	private GeoElement getCurrent(int depth, GeoList currentList, int initIndex) {
		return depth == maxDepth() - 1
				? getGenericElement(currentList, initIndex)
				: currentList.get(initIndex);
	}

	@Override
	public Commands getClassName() {
		return Commands.Element;
	}

	@Override
	protected void setInputOutput() {
		if (isFlatList()) {
			setInputForFlatList();
		} else {
			setInputForListOfLists();
		}

		setOnlyOutput(element);
		setDependencies(); // done by AlgoElement
	}

	private boolean isFlatList() {
		return indexes == null;
	}

	private void setInputForFlatList() {
		input = new GeoElement[2];
		input[0] = geoList;
		input[1] = indexGeo;
	}

	private void setInputForListOfLists() {
		input = new GeoElement[maxDepth() + 1];
		input[0] = geoList;
		for (int i = 0; i < maxDepth(); i++) {
			input[i + 1] = indexes[i].toGeoElement();
		}
	}

	/**
	 * Returns chosen element
	 * 
	 * @return chosen element
	 */
	public GeoElement getElement() {
		return element;
	}

	@Override
	public final void compute() {
		if ((indexGeo != null && !indexGeo.isDefined()) || !geoList.isDefined()) {
			element.setUndefined();
			return;
		}

		if (isFlatList()) {
			retrieveElementFromFlatList();
		} else {
			retrieveElementFromDepth();
		}
	}

	private void retrieveElementFromFlatList() {
		// index of wanted element
		int n = (int) Math.round(index.getDouble()) - 1;
		if (n >= 0 && n < geoList.size()) {
			GeoElement nth = geoList.get(n);
			setElement(nth);
		} else {
			element.setUndefined();
		}
	}

	private void retrieveElementFromDepth() {
		for (int depth = 0; depth < maxDepth(); depth++) {
			if (!indexes[depth].toGeoElement().isDefined()) {
				element.setUndefined();
				return;
			}
		}

		int m = (int) Math.round(indexes[maxDepth() - 1].getDouble()) - 1;
		GeoElement current = geoList;
		for (int depth = 0; depth < maxDepth() - 1; depth++) {
			int index = (int) Math.round(indexes[depth].getDouble() - 1);
			if (index >= 0 && current.isGeoList()
					&& index < ((GeoList) current).size()) {
				current = ((GeoList) current).get(index);
			} else {
				element.setUndefined();
				return;
			}
		}

		if (!(current instanceof GeoList)) { // not deep enough
			element.setUndefined();
			return;
		}
		GeoList list = (GeoList) current;

		if (m >= 0 && m < list.size()) {
			current = list.get(m);
		} else {
			element.setUndefined();
			return;
		}

		setElement(current);
	}

	private void setElement(GeoElement geo) {
		if (canTypeSet(geo)) {
			element.set(geo);
			elementLabel = geo.isLabelSet()
					? geo.getLabel(StringTemplate.realTemplate) : null;
			if (hasDrawInformationAlgo(geo)) {
				element.setDrawAlgorithm(
						((DrawInformationAlgo) geo.getDrawAlgorithm()).copy());
			}
		} else {
			element.setUndefined();
		}
	}

	private static boolean hasDrawInformationAlgo(GeoElement nth) {
		return nth.getDrawAlgorithm() instanceof DrawInformationAlgo;
	}

	private boolean canTypeSet(GeoElement nth) {
		return nth.getGeoClassType() == element.getGeoClassType()
				|| TestGeo.canSet(element, nth);
	}

	/**
	 * So that Name(Element(list1,1)) works
	 * 
	 * @return label
	 */
	public String getLabel() {
		return elementLabel;
	}

	/*
	 * @Override public String getCommandDescription(StringTemplate tpl,boolean
	 * real) {
	 * 
	 * return super.getCommandDescription(tpl,real);
	 * 
	 * TODO re enable this for shortSyntax flag true for 5.0 sb.setLength(0);
	 * 
	 * 
	 * int length = input.length;
	 * 
	 * sb.append(geoList.getLabel()+"("); // input
	 * sb.append(real?input[1].getRealLabel():input[1].getLabel()); // Michael
	 * Borcherds 2008-05-15 added input.length>0 for Step[] for (int i = 2; i <
	 * length; ++i) { sb.append(", "); sb.append(real?
	 * input[i].getRealLabel():input[i].getLabel()); } sb.append(")"); return
	 * sb.toString();
	 * 
	 * }
	 */

}
