/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ListValue;
import geogebra.kernel.arithmetic.MyList;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra.util.Util;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * List of GeoElements
 */
public class GeoList extends GeoElement implements ListValue, LineProperties,
		PointProperties, TextProperties, Traceable, Path, Transformable {

	public final static int ELEMENT_TYPE_MIXED = -1;

	public boolean trace;

	private static final long serialVersionUID = 1L;
	private static String STR_OPEN = "{";
	private static String STR_CLOSE = "}";

	// GeoElement list members
	private ArrayList<GeoElement> geoList;

	// lists will often grow and shrink dynamically,
	// so we keep a cacheList of all old list elements
	private ArrayList<GeoElement> cacheList;

	private boolean isDefined = true;
	private boolean isDrawable = true;
	private int elementType = ELEMENT_TYPE_MIXED;

	/**
	 * Whether this lists show all properties in the properties dialog. This is
	 * just recommended for the default GeoList in order to show all possible
	 * properties in the default configuration dialog.
	 */
	private boolean showAllProperties = false;

	private ArrayList<GeoElement> colorFunctionListener; // Michael Borcherds 2008-04-02

	public GeoList(Construction c) {
		this(c, 20);
	}

	private GeoList(Construction c, int size) {
		super(c);
		
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		geoList = new ArrayList<GeoElement>(size);
		cacheList = new ArrayList<GeoElement>(size);
		setEuclidianVisible(false);
	}

	public void setParentAlgorithm(AlgoElement algo) {
		super.setParentAlgorithm(algo);
		setEuclidianVisible(true);
	}

	public GeoList(GeoList list) {
		this(list.cons, list.size());
		set(list);
	}

	public String getClassName() {
		return "GeoList";
	}

	protected String getTypeString() {
		return "List";
	}

	public int getGeoClassType() {
		return GEO_CLASS_LIST;
	}

	/**
	 * Returns the element type of this list.
	 * 
	 * @return ELEMENT_TYPE_MIXED or GeoElement.GEO_CLASS_xx constant
	 */
	public int getElementType() {
		return elementType;
	}

	public GeoElement copy() {
		return new GeoList(this);
	}

	public void set(GeoElement geo) {

		if (geo.isGeoNumeric()) { // eg SetValue[list, 2]
			// 1 -> first element
			selectedIndex = -1 + (int) ((GeoNumeric) geo).getDouble();
			isDefined = true;

			return;
		}
		GeoList l = (GeoList) geo;

		if (l.cons != cons && isAlgoMacroOutput()) {
			// MACRO CASE
			// this object is an output object of AlgoMacro
			// we need to check the references to all geos in the list
			AlgoMacro algoMacro = (AlgoMacro) getParentAlgorithm();
			algoMacro.initList(l, this);
		} else {
			// STANDARD CASE
			// copy geoList
			copyListElements(l);
		}

		isDefined = l.isDefined;
		elementType = l.elementType;
	}

	/**
	 * Set if the list should show all properties in the properties dialog. This
	 * is just recommended for the default list.
	 * 
	 * @param showAllProperties
	 */
	public void setShowAllProperties(boolean showAllProperties) {
		this.showAllProperties = showAllProperties;
	}

	private void copyListElements(GeoList otherList) {
		int otherListSize = otherList.size();
		ensureCapacity(otherListSize);
		geoList.clear();

		for (int i = 0; i < otherListSize; i++) {
			GeoElement otherElement = otherList.get(i);
			GeoElement thisElement = null;

			// try to reuse cached GeoElement
			if (i < cacheList.size()) {
				GeoElement cachedGeo = (GeoElement) cacheList.get(i);
				if (!cachedGeo.isLabelSet()
						&& cachedGeo.getGeoClassType() == otherElement
								.getGeoClassType()) {
					// cached geo is unlabeled and has needed object type: use
					// it
					cachedGeo.set(otherElement);
					thisElement = cachedGeo;
				}
			}

			// could not use cached element -> get copy element
			if (thisElement == null) {
				thisElement = getCopyForList(otherElement);
			}

			// set list element
			add(thisElement);
		}
	}

	private GeoElement getCopyForList(GeoElement geo) {
		if (geo.isLabelSet()) {
			// take original element
			return geo;
		} else {
			// create a copy of geo
			GeoElement ret = geo.copyInternal(cons);
			ret.setParentAlgorithm(getParentAlgorithm());
			return ret;
		}
	}

	private void applyVisualStyle(GeoElement geo) {

		if (!geo.isLabelSet()) {
			geo.setObjColor(this.getObjectColor());

			// copy color function
			if (this.getColorFunction() != null) {
				geo.setColorFunction(this.getColorFunction());
			} else {
				geo.removeColorFunction();
			}

			geo.setLineThickness(this.getLineThickness());
			geo.setLineType(this.getLineType());

			if (geo instanceof PointProperties) {
				((PointProperties) geo).setPointSize(this.getPointSize());
				((PointProperties) geo).setPointStyle(this.getPointStyle());
			}

			if (geo instanceof TextProperties) {
				((TextProperties) geo).setFontSize(this.getFontSize());
				((TextProperties) geo).setFontStyle(this.getFontStyle());
				((TextProperties) geo).setSerifFont(this.isSerifFont());
				if (useSignificantFigures)
					((TextProperties) geo).setPrintFigures(this
							.getPrintFigures(), false);
				else
					((TextProperties) geo).setPrintDecimals(this
							.getPrintDecimals(), false);

			}

			geo.setAlphaValue(this.getAlphaValue());

			geo.setLayer(this.getLayer());

			// copy ShowObjectCondition, unless it generates a
			// CirclularDefinitionException
			try {
				geo.setShowObjectCondition(this.getShowObjectCondition());
			} catch (Exception e) {
			}

			setElementEuclidianVisible(geo, isSetEuclidianVisible());
		}
	}

	public final void removeColorFunction() {
		super.removeColorFunction();

		if (geoList == null || geoList.size() == 0)
			return;

		int size = geoList.size();
		for (int i = 0; i < size; i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet())
				geo.removeColorFunction();
		}
	}

	public final void setColorFunction(GeoList col) {
		super.setColorFunction(col);

		if (geoList == null || geoList.size() == 0)
			return;

		int size = geoList.size();
		for (int i = 0; i < size; i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet())
				geo.setColorFunction(col);
		}

	}

	/*
	 * we DON'T want to do this, as objects without label set eg the point in
	 * this {(1,1)} are drawn by the list public final void setLayer(int layer)
	 * { super.setLayer(layer);
	 * 
	 * if (geoList == null || geoList.size() == 0) return;
	 * 
	 * int size = geoList.size(); for (int i=0; i < size; i++) { GeoElement geo
	 * = (GeoElement)geoList.get(i); if (!geo.isLabelSet()) geo.setLayer(layer);
	 * }
	 * 
	 * }
	 */

	public final void setShowObjectCondition(GeoBoolean bool)
			throws CircularDefinitionException {
		super.setShowObjectCondition(bool);

		if (geoList == null || geoList.size() == 0)
			return;

		int size = geoList.size();
		for (int i = 0; i < size; i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet())
				geo.setShowObjectCondition(bool);
		}

	}

	public void setVisualStyle(GeoElement style) {
		super.setVisualStyle(style);

		// set point style
		if (style instanceof PointProperties) {
			setPointSize(((PointProperties) style).getPointSize());
			setPointStyle(((PointProperties) style).getPointStyle());
		}

		// set visual style
		if (geoList == null || geoList.size() == 0)
			return;
		int size = geoList.size();
		for (int i = 0; i < size; i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet())
				geo.setVisualStyle(style);
		}
	}

	public void setObjColor(Color color) {
		super.setObjColor(color);

		if (geoList == null || geoList.size() == 0)
			return;

		int size = geoList.size();
		for (int i = 0; i < size; i++) {
			GeoElement geo = get(i);
			if (!geo.isLabelSet())
				geo.setObjColor(color);
		}
	}

	public void setBackgroundColor(Color color) {
		super.setBackgroundColor(color);

		if (geoList == null || geoList.size() == 0)
			return;

		int size = geoList.size();
		for (int i = 0; i < size; i++) {
			GeoElement geo = get(i);
			if (!geo.isLabelSet())
				geo.setBackgroundColor(color);
		}
	}

	public void setEuclidianVisible(boolean visible) {
		super.setEuclidianVisible(visible);

		if (geoList == null || geoList.size() == 0)
			return;

		int size = geoList.size();
		for (int i = 0; i < size; i++) {
			GeoElement geo = get(i);
			setElementEuclidianVisible(geo, visible);
		}
	}

	private void setElementEuclidianVisible(GeoElement geo, boolean visible) {
		if (!geo.isLabelSet() && !geo.isGeoNumeric())
			geo.setEuclidianVisible(visible);
	}

	/**
	 * Returns this GeoList as a MyList object.
	 */
	public MyList getMyList() {
		int size = geoList.size();
		MyList myList = new MyList(kernel, size);

		for (int i = 0; i < size; i++) {
			myList.addListElement(new ExpressionNode(kernel,
					(GeoElement) geoList.get(i)));
		}

		return myList;
	}

	final public boolean isDefined() {
		return isDefined;
	}

	public void setDefined(boolean flag) {
		isDefined = flag;

		if (!isDefined) {
			int size = geoList.size();
			for (int i = 0; i < size; i++) {
				GeoElement geo = (GeoElement) geoList.get(i);
				if (!geo.isLabelSet())
					geo.setUndefined();
			}
		}
	}

	public void setUndefined() {
		setDefined(false);
	}

	protected boolean showInEuclidianView() {
		return isDefined() && isDrawable();
	}

	public boolean isDrawable() {
		return isDrawable;
	}

	public boolean showInAlgebraView() {
		return true;
	}

	public final void clear() {
		geoList.clear();
	}

	/*
	 * free up memory and set undefined
	 */
	public final void clearCache() {
		if (cacheList.size() > 0)
			for (int i = 0; i < cacheList.size(); i++) {
				GeoElement geo = (GeoElement) cacheList.get(i);
				if (geo != null && !geo.isLabelSet())
					geo.remove();
			}
		cacheList.clear();
		clear();
		setUndefined();
		System.gc();
	}

	public final void add(GeoElement geo) {
		// add geo to end of list
		geoList.add(geo);

		/*
		 * // needed for Corner[Element[text // together with swapping these
		 * lines over in MyXMLio: //kernel.updateConstruction();
		 * //kernel.setNotifyViewsActive(oldVal);
		 * 
		 * if (geo.isGeoText()) {
		 * ((GeoText)geo).setNeedsUpdatedBoundingBox(true); }
		 */

		// add to cache
		int pos = geoList.size() - 1;
		if (pos < cacheList.size()) {
			cacheList.set(pos, geo);
		} else {
			cacheList.add(geo);
		}

		// init element type
		if (pos == 0) {
			isDrawable = geo.isDrawable();
			elementType = geo.getGeoClassType();
		}
		// check element type
		else if (elementType != geo.getGeoClassType()) {
			elementType = ELEMENT_TYPE_MIXED;
		}
		isDrawable = isDrawable && geo.isDrawable();

		// set visual style of this list
		applyVisualStyle(geo);
		// if (!geo.isLabelSet())
		// geo.setVisualStyle(this);

	}

	/**
	 * Removes geo from this list. Note: geo is not removed from the
	 * construction.
	 * @param geo element to be removed
	 */
	public final void remove(GeoElement geo) {
		geoList.remove(geo);
	}

	/**
	 * Removes i-th element from this list. Note: this element is not removed
	 * from the construction.
	 * @param index position of element to be removed
	 */
	public final void remove(int index) {
		geoList.remove(index);
	}

	/**
	 * Returns the element at the specified position in this list.
	 * @param index element position
	 * @return the element at the specified position in this list.
	 */
	final public GeoElement get(int index) {
		return (GeoElement) geoList.get(index);
	}

	/**
	 * Returns the element at the specified position in this (2D) list.
	 * @param index element position -- row
	 * @param index2 element position -- column
	 * @return the element at the specified position in this (2D) list.
	 */
	final public GeoElement get(int index, int index2) {
		return ((GeoList) geoList.get(index)).get(index2);
	}

	/**
	 * Tries to return this list as an array of double values
	 * 
	 * @return array of double values from this list
	 */
	public double[] toDouble() {
		try {
			double[] valueArray = new double[geoList.size()];
			for (int i = 0; i < valueArray.length; i++) {
				valueArray[i] = ((NumberValue) geoList.get(i)).getDouble();
			}
			return valueArray;
		} catch (Exception e) {
			return null;
		}
	}

	final public void ensureCapacity(int size) {
		geoList.ensureCapacity(size);
		cacheList.ensureCapacity(size);
	}

	final public int size() {
		return geoList.size();
	}

	final public int getCacheSize() {
		return cacheList.size();
	}

	/**
	 * Returns the cached element at the specified position in this list's
	 * cache.
	 * @param index element position
	 * @return cached alement at given position
	 */
	final public GeoElement getCached(int index) {
		return (GeoElement) cacheList.get(index);
	}

	public String toString() {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(buildValueString());
		return sbToString.toString();
	}
	
	public String toStringMinimal() {
		sbBuildValueString.setLength(0);
		if (!isDefined) {
			sbBuildValueString.append("?");
			return sbBuildValueString.toString();
		}

		// first (n-1) elements
		int lastIndex = geoList.size() - 1;
		if (lastIndex > -1) {
			for (int i = 0; i < lastIndex; i++) {
				GeoElement geo = (GeoElement) geoList.get(i);

				sbBuildValueString.append(geo.getAlgebraDescriptionRegrOut());
				sbBuildValueString.append(" ");
			}

			// last element
			GeoElement geo = (GeoElement) geoList.get(lastIndex);
			sbBuildValueString.append(geo.getAlgebraDescriptionRegrOut());
		}

		return sbBuildValueString.toString();
	}


	StringBuilder sbToString = new StringBuilder(50);

	public String toValueString() {
		return buildValueString().toString();
	}

	private StringBuilder buildValueString() {
		sbBuildValueString.setLength(0);
		if (!isDefined) {
			sbBuildValueString.append("?");
			return sbBuildValueString;
		}
		
		if(kernel.getCASPrintForm()==ExpressionNode.STRING_TYPE_LATEX)
			sbBuildValueString.append("\\left\\");
		if (kernel.getCASPrintForm()==ExpressionNode.STRING_TYPE_MPREDUCE)
			sbBuildValueString.append("list(");
		else
			sbBuildValueString.append(STR_OPEN);		
		// first (n-1) elements
		int lastIndex = geoList.size() - 1;
		if (lastIndex > -1) {
			for (int i = 0; i < lastIndex; i++) {
				GeoElement geo = (GeoElement) geoList.get(i);
				sbBuildValueString.append(geo.toOutputValueString());
				sbBuildValueString.append(Application.unicodeComma);
				sbBuildValueString.append(" ");
			}

			// last element
			GeoElement geo = (GeoElement) geoList.get(lastIndex);
			sbBuildValueString.append(geo.toOutputValueString());
		}
		if(kernel.getCASPrintForm()==ExpressionNode.STRING_TYPE_LATEX)
			sbBuildValueString.append("\\right\\");
		if (kernel.getCASPrintForm()==ExpressionNode.STRING_TYPE_MPREDUCE)
			sbBuildValueString.append(")");
		else
			sbBuildValueString.append(STR_CLOSE);		
		return sbBuildValueString;
	}

	private StringBuilder sbBuildValueString = new StringBuilder(50);

	public boolean isGeoList() {
		return true;
	}

	public boolean isListValue() {
		return true;
	}	

	/**
	 * save object in XML format
	 */
	public final void getXML(StringBuilder sb) {

		// an independent list needs to add
		// its expression itself
		// e.g. {1,2,3}
		if (isIndependent() && getDefaultGeoType() < 0) {
			sb.append("<expression");
			sb.append(" label =\"");
			sb.append(Util.encodeXML(label));
			sb.append("\" exp=\"");
			sb.append(Util.encodeXML(toValueString()));
			sb.append("\"/>\n");
		}

		sb.append("<element");
		sb.append(" type=\"list\"");
		sb.append(" label=\"");
		sb.append(label);
		if (getDefaultGeoType() >= 0) {
			sb.append("\" default=\"");
			sb.append(getDefaultGeoType());
		}
		sb.append("\">\n");
		getXMLtags(sb);

		if (selectedIndex != 0) {
			sb.append("\t<selectedIndex val=\"");
			sb.append(selectedIndex);
			sb.append("\"/>\n");
		}

		// point style
		sb.append("\t<pointSize val=\"");
		sb.append(pointSize);
		sb.append("\"/>\n");

		sb.append("\t<pointStyle val=\"");
		sb.append(pointStyle);
		sb.append("\"/>\n");

		// font settings
		if (serifFont || fontSize != 0 || fontStyle != 0) {
			sb.append("\t<font serif=\"");
			sb.append(serifFont);
			sb.append("\" size=\"");
			sb.append(fontSize);
			sb.append("\" style=\"");
			sb.append(fontStyle);
			sb.append("\"/>\n");
		}

		// print decimals
		if (printDecimals >= 0 && !useSignificantFigures) {
			sb.append("\t<decimals val=\"");
			sb.append(printDecimals);
			sb.append("\"/>\n");
		}

		// print significant figures
		if (printFigures >= 0 && useSignificantFigures) {
			sb.append("\t<significantfigures val=\"");
			sb.append(printFigures);
			sb.append("\"/>\n");
		}
		sb.append("</element>\n");

	}

	/**
	 * Registers geo as a listener for updates of this boolean object. If this
	 * object is updated it calls geo.updateConditions()
	 * 
	 * @param geo
	 */
	public void registerColorFunctionListener(GeoElement geo) {
		if (colorFunctionListener == null)
			colorFunctionListener = new ArrayList<GeoElement>();
		colorFunctionListener.add(geo);
	}
	
	public void unregisterColorFunctionListener(GeoElement geo) {
		if (colorFunctionListener != null) {
			colorFunctionListener.remove(geo);
		}
	}

	/**
	 * Calls super.update() and update() for all registered condition listener
	 * geos. // Michael Borcherds 2008-04-02
	 */
	public void update() {
		super.update();

		if (getParentAlgorithm() instanceof AlgoMirror) {
			AlgoMirror algo = (AlgoMirror) getParentAlgorithm();
			GeoElement[] geos = algo.getInput();
			GeoList list = (GeoList) geos[0];
			Application.debug("size = " + list.size());
		}

		// update all registered locatables (they have this point as start
		// point)
		if (colorFunctionListener != null) {
			// Application.debug("GeoList update listeners");
			for (int i = 0; i < colorFunctionListener.size(); i++) {
				GeoElement geo = (GeoElement) colorFunctionListener.get(i);
				kernel.notifyUpdate(geo);
				// geo.toGeoElement().updateCascade();
			}
		}
	}

	/**
	 * Tells conidition listeners that their condition is removed and calls
	 * super.remove() // Michael Borcherds 2008-04-02
	 */
	public void doRemove() {

		if (colorFunctionListener != null) {
			// copy conditionListeners into array
			Object[] geos = colorFunctionListener.toArray();
			colorFunctionListener.clear();

			// tell all condition listeners
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				geo.removeColorFunction();
				kernel.notifyUpdate(geo);
			}
		}

		super.doRemove();
	}

	/**
	 * return whether this list equals GeoList list Michael Borcherds 2008-04-12
	 */
	final public boolean isEqual(GeoElement geo) {

		if (!geo.isGeoList())
			return false;

		GeoList list = (GeoList) geo;

		// check sizes
		if (geoList.size() != list.size())
			return false;

		// check each element
		for (int i = 0; i < list.geoList.size(); i++) {
			GeoElement geoA = (GeoElement) geoList.get(i);
			GeoElement geoB = list.get(i);

			if (!geoA.isEqual(geoB))
				return false;
			/*
			 * if (geoA.isGeoNumeric() && geoB.isGeoNumeric()) { if
			 * (!((GeoNumeric)geoA).equals((GeoNumeric)geoB)) return false; }
			 * else if (geoA.isGeoConicPart() && geoB.isGeoConicPart()) { if
			 * (!((GeoConicPart)geoA).equals((GeoConicPart)geoB)) return false;
			 * } else if (geoA.isGeoConic() && geoB.isGeoConic()) { if
			 * (!((GeoConic)geoA).equals((GeoConic)geoB)) return false; } else
			 * if (geoA.isGeoAngle() && geoB.isGeoAngle()) { if
			 * (!((GeoAngle)geoA).equals((GeoAngle)geoB)) return false; } else
			 * if (geoA.isGeoPoint() && geoB.isGeoPoint()) { if
			 * (!((GeoPoint)geoA).equals((GeoPoint)geoB)) return false; } else
			 * if (geoA.isGeoPolygon() && geoB.isGeoPolygon()) { if
			 * (!((GeoPolygon)geoA).equals((GeoPolygon)geoB)) return false; }
			 * else if (geoA.isGeoSegment() && geoB.isGeoSegment()) { if
			 * (!((GeoSegment)geoA).equals((GeoSegment)geoB)) return false; }
			 * else if (geoA.isGeoList() && geoB.isGeoList()) { if
			 * (!((GeoList)geoA).equals((GeoList)geoB)) return false; } else if
			 * (!geoA.equals(geoB)) return false;
			 */
		}

		// all list elements equal
		return true;
	}

	public void setZero() {
		geoList.clear();
	}

	public void setLineThickness(int thickness) {

		super.setLineThickness(thickness);

		if (geoList == null || geoList.size() == 0)
			return;

		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet())
				geo.setLineThickness(thickness);
		}

		// Application.debug("GeoList.setLineThickness "+thickness);
	}

	public int getLineThickness() {
		return super.getLineThickness();
	}

	/**
	 * @return minimum line thickness (normally 1, but 0 for polygons, integrals
	 *         etc)
	 */
	public int getMinimumLineThickness() {
		if (geoList == null || geoList.size() == 0)
			return 1;

		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet())
				if (geo.getMinimumLineThickness() == 1)
					return 1;
		}

		return 0;
	}

	public void setLineType(int type) {

		super.setLineType(type);

		if (geoList == null || geoList.size() == 0)
			return;

		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet())
				geo.setLineType(type);
		}

		// Application.debug("GeoList.setLineType");

	}

	public int getLineType() {
		return super.getLineType();
	}

	public int pointSize = EuclidianView.DEFAULT_POINT_SIZE;
	private int pointStyle = -1; // use global option if -1

	public void setPointSize(int size) {
		pointSize = size;

		if (geoList == null || geoList.size() == 0)
			return;

		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet() && geo instanceof PointProperties)
				((PointProperties) geo).setPointSize(size);
		}
	}

	public int getPointSize() {
		return pointSize;
	}

	public void setPointStyle(int style) {
		pointStyle = style;

		if (geoList == null || geoList.size() == 0)
			return;

		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet() && geo instanceof PointProperties)
				((PointProperties) geo).setPointStyle(style);
		}
	}

	public float getAlphaValue() {
		if (super.getAlphaValue() == -1) {
			// no alphaValue set
			// so we need to set it to that of the first element, if there is
			// one
			if (geoList != null && geoList.size() > 0) {

				// get alpha value of first element
				float alpha = ((GeoElement) geoList.get(0)).getAlphaValue();

				// Application.debug("setting list alpha to "+alpha);

				super.setAlphaValue(alpha);

				// set all the other elements in the list
				// if appropriate
				if (geoList.size() > 1) {
					for (int i = 1; i < geoList.size(); i++) {
						GeoElement geo = (GeoElement) geoList.get(i);
						if (!geo.isLabelSet())
							geo.setAlphaValue(alpha);
					}

				}
			} else {
				return -1.0f;
			}
		}

		return super.getAlphaValue();
	}

	public void setAlphaValue(float alpha) {

		if (alpha == -1) {
			// wait until we have a GeoElement in the list to use
			// see getAlphaValue()
			alphaValue = -1;
			return;
		}

		super.setAlphaValue(alpha);

		if (geoList == null || geoList.size() == 0)
			return;

		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet())
				geo.setAlphaValue(alpha);
		}

	}

	public int getPointStyle() {
		return pointStyle;
	}

	public boolean isFillable() {
		if (geoList == null || geoList.size() == 0)
			return false;

		boolean someFillable = false;
		boolean allLabelsSet = true;

		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (geo.isFillable())
				someFillable = true;
			if (!geo.isLabelSet())
				allLabelsSet = false;
		}

		return someFillable && !allLabelsSet;
	}

	public GeoElement getGeoElementForPropertiesDialog() {
		if (geoList.size() > 0 && elementType != ELEMENT_TYPE_MIXED) {
			return get(0).getGeoElementForPropertiesDialog(); // getGeoElementForPropertiesDialog()
																// to cope with
																// lists of
																// lists
		} else {
			return this;
		}
	}

	/*
	 * is this a list in the form { {1,2}, {3,4}, {5,6} } etc
	 */
	public boolean isMatrix() {

		if (getElementType() != GeoElement.GEO_CLASS_LIST)
			return false;

		if (size() > 0) {

			GeoElement geo0 = get(0);
			if (geo0.isGeoList()) {
				int length = ((GeoList) geo0).size();
				if (length == 0) {
					return false;
				} else {
					if (size() > 0)
						for (int i = 0; i < size(); i++) {
							GeoElement geoi = get(i);
							// Application.debug(((GeoList)geoi).get(0).getGeoClassType()+"");
							if (!get(i).isGeoList()
									|| ((GeoList) geoi).size() == 0
									|| ((GeoList) geoi).size() != length)
								return false;
							else {
								for (int j=0; j<((GeoList) geoi).size(); j++){
									GeoElement geoij=((GeoList)geoi).get(j);
									if (geoij.getGeoClassType() != GeoElement.GEO_CLASS_NUMERIC
											&& geoij.getGeoClassType() != GeoElement.GEO_CLASS_FUNCTION
											&& geoij.getGeoClassType() != GeoElement.GEO_CLASS_FUNCTION_NVAR)
										return false;
								}
							}
						}
				}
			}

		}

		return true;
	}

	// font options
	private boolean serifFont = false;
	private int fontStyle = Font.PLAIN;
	private int fontSize = 0; // size relative to default font size
	private int printDecimals = -1;
	private int printFigures = -1;
	public boolean useSignificantFigures = false;

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int size) {
		fontSize = size;

		if (geoList == null || geoList.size() == 0)
			return;

		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (geo instanceof TextProperties && !geo.isLabelSet())
				((TextProperties) geo).setFontSize(size);
		}
	}

	public int getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;

		if (geoList == null || geoList.size() == 0)
			return;

		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (geo instanceof TextProperties && !geo.isLabelSet())
				((TextProperties) geo).setFontStyle(fontStyle);
		}
	}

	final public int getPrintDecimals() {
		return printDecimals;
	}

	final public int getPrintFigures() {
		return printFigures;
	}

	public void setPrintDecimals(int printDecimals, boolean update) {
		this.printDecimals = printDecimals;
		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (geo instanceof TextProperties && !geo.isLabelSet())
				((TextProperties) geo).setPrintDecimals(printDecimals, update);
		}
	}

	public void setPrintFigures(int printFigures, boolean update) {
		this.printFigures = printFigures;
		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (geo instanceof TextProperties && !geo.isLabelSet())
				((TextProperties) geo).setPrintFigures(printFigures, update);
		}
	}

	public boolean useSignificantFigures() {
		return useSignificantFigures;

	}

	public boolean isSerifFont() {
		return serifFont;
	}

	public void setSerifFont(boolean serifFont) {
		this.serifFont = serifFont;
		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (geo instanceof TextProperties && !geo.isLabelSet())
				((TextProperties) geo).setSerifFont(serifFont);
		}
	}

	public void setHatchingAngle(int angle) {
		super.setHatchingAngle(angle);
		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet())
				geo.setHatchingAngle(angle);
		}
	}

	public void setHatchingDistance(int distance) {
		super.setHatchingDistance(distance);
		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet())
				geo.setHatchingDistance(distance);
		}
	}

	public void setFillType(int type) {
		super.setFillType(type);
		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet())
				geo.setFillType(type);
		}
	}

	public void setFillImage(String filename) {
		super.setFillImage(filename);
		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet())
				geo.setFillImage(filename);
		}
	}

	public void setImageFileName(String filename) {
		super.setImageFileName(filename);
		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet())
				geo.setImageFileName(filename);
		}
	}

	/*
	 * for a list like this: {Circle[B, A], (x(A), y(A)), "text"} we want to be
	 * able to set the line properties
	 */
	public boolean showLineProperties() {
		if (showAllProperties)
			return true;

		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (geo instanceof LineProperties && !geo.isLabelSet())
				return true;
		}

		return false;
	}

	/*
	 * for a list like this: {Circle[B, A], (x(A), y(A)), "text"} we want to be
	 * able to set the point properties
	 */
	public boolean showPointProperties() {
		if (showAllProperties)
			return true;

		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (geo instanceof PointProperties && !geo.isLabelSet())
				return true;
		}

		return false;
	}

	public boolean isVector3DValue() {
		return false;
	}

	public String toLaTeXString(boolean symbolic) {

		if (isMatrix()) {
			
			//int rows = size();
			int cols = ((GeoList)get(0)).size();
			
			StringBuilder sb = new StringBuilder();
			sb.append("\\left(\\begin{array}{");
			// eg rr
			for (int i = 0; i < cols ; i++) sb.append('r');
			sb.append("}");
			for (int i = 0; i < size(); i++) {
				GeoList geo = (GeoList) get(i);
				for (int j = 0; j < geo.size(); j++) {
					sb.append(geo.get(j).toLaTeXString(symbolic));
					if (j < geo.size() - 1)
						sb.append("&");
				}
				sb.append("\\\\");
			}
			sb.append(" \\end{array}\\right)");
			return sb.toString();
			// return "\\begin{array}{ll}1&2 \\\\ 3&4 \\\\ \\end{array}";
		}

		return super.toLaTeXString(symbolic);

	}

	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		getLineStyleXML(sb);
		getScriptTags(sb);

	}

	/*
	 * for CmdSetLabelMode
	 *
	 * removed: no point setting labelmode for objects with no labels
	 * and we don't want to set labelmode for objects with labels...
	public void setLabelMode(int mode) {
		super.setLabelMode(mode);

		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (!geo.isLabelSet())
				geo.setLabelMode(mode);
		}

	}*/

	// G.Sturr 2010-6-12
	// Selection index for lists used in comboBoxes
	private int selectedIndex = 0;

	private int closestPointIndex;

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex0) {
		selectedIndex = selectedIndex0;
	}

	// END G.Sturr

	/*
	 * mathieu : for drawing 3D elements of the list
	 */
	public boolean hasDrawable3D() {
		return true;
	}

	/*
	 * when list is visible (as a combobox) this returns the element selected by
	 * the user or null if there's a problem
	 */
	public GeoElement getSelectedElement() {
		if (selectedIndex > -1 && selectedIndex < size())
			return get(selectedIndex);
		else
			return null;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}

	public boolean isTraceable() {
		return true;
	}

	public boolean isLimitedPath() {
		return false;
	}

	public boolean isPath() {
		return true;
	}

	/*
	 * adapted from GeoLocus
	 */
	public void pointChanged(GeoPointND P) {
		// Application.debug("pointChanged",1);

		// GeoPoint P = (GeoPoint) PI;

		P.updateCoords();
		
		// update closestPointIndex
		getNearestPoint(P);

		
		GeoElement geo = get(closestPointIndex);
		if (!(geo instanceof PathOrPoint)) {
			Application.debug("TODO: " + geo.getClassName()
					+ " should implement PathOrPoint interface");
			return;
		}
		PathOrPoint path = (PathOrPoint) get(closestPointIndex);

		path.pointChanged(P);

		PathParameter pp = P.getPathParameter();

		// update path param
		// 0-1 for first obj
		// 1-2 for second
		// etc
		// Application.debug(pp.t+" "+path.getMinParameter()+" "+path.getMaxParameter());
		pp.t = closestPointIndex
				+ PathNormalizer.toNormalizedPathParameter(pp.t, path
						.getMinParameter(), path.getMaxParameter());
		// Application.debug(pp.t);

	}

	public Point2D.Double getNearestPoint(GeoPointND p) {
		// Application.printStacktrace(p.inhomX+" "+p.inhomY);
		double distance = Double.POSITIVE_INFINITY;
		closestPointIndex = 0; // default - first object
		// double closestIndex = -1;
		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			double d = geo.distance(p);
			//Application.debug(i+" "+d+" "+distance+" "+getLabel());
			if (d < distance) {
				distance = d;
				closestPointIndex = i;
			}
		}

		// Application.debug("closestPointIndex="+closestPointIndex);

		return get(closestPointIndex).getNearestPoint(p);
	}

	public double distance(GeoPoint p) {
		double distance = Double.POSITIVE_INFINITY;
		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			double d = geo.distance(p);
			if (d < distance) {
				distance = d;
			}
		}
		
		return distance;
	}

	public void pathChanged(GeoPointND PI) {

		PathParameter pp = PI.getPathParameter();

		double t = pp.getT();
		int n = (int) Math.floor(t);

		// check n is in a sensible range
		if (n >= size() || n < 0) {
			double check = t - size();
			// t = size() when at very end of path
			// so check == 0 is OK, just need to set n = size() - 1
			if (check != 0.0)
				Application.debug("problem with path param " + PI.getLabel());
			n = (n < 0) ? 0 : size() - 1;
		}
		PathOrPoint path = (PathOrPoint) get(n);

		pp.setT(PathNormalizer.toParentPathParameter(t - n, path
				.getMinParameter(), path.getMaxParameter()));

		// Application.debug("pathChanged "+n);

		path.pathChanged(PI);

		t = pp.getT();
		// Application.debug(PathNormalizer.toNormalizedPathParameter(t,
		// path.getMinParameter(), path.getMaxParameter()));
		pp.setT(PathNormalizer.toNormalizedPathParameter(t, path
				.getMinParameter(), path.getMaxParameter())
				+ n);

	}

	public boolean isOnPath(GeoPointND PI, double eps) {
		// Application.debug("isOnPath",1);
		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);
			if (((PathOrPoint) geo).isOnPath(PI, eps))
				return true;
		}
		return false;
	}

	public double getMinParameter() {
		return 0;
	}

	public double getMaxParameter() {
		return geoList.size();
	}

	public boolean isClosedPath() {
		// TODO Auto-generated method stub
		return false;
	}

	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	public boolean justFontSize() {
		return false;
	}

	public boolean hasMoveableInputPoints(EuclidianViewInterface view) {
		//we don't want e.g. DotPlots to be dragged
		if(!(getParentAlgorithm() == null || getParentAlgorithm() instanceof AlgoDependentList))
			return false;		
		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);

			if (geo.isGeoPoint()) {
				if (!geo.isMoveable())
					return false;
			} else {
				// not point
				if (!geo.hasMoveableInputPoints(view))
					return false;
			}
		}

		return true;
	}

	/*
	 * allow lists like this to be dragged {Segment[A, B], Segment[B, C], (3.92,
	 * 4)}
	 */
	public ArrayList<GeoPoint> getFreeInputPoints(EuclidianViewInterface view) {
		ArrayList<GeoPoint> al = new ArrayList<GeoPoint>();
		
		for (int i = 0; i < geoList.size(); i++) {
			GeoElement geo = (GeoElement) geoList.get(i);

			if (geo.isGeoPoint()) {
				GeoPoint p = (GeoPoint) geo;
				if (p.isMoveable() && !al.contains(p))
					al.add(p);

			} else {
				ArrayList<GeoPoint> al2 = geo.getFreeInputPoints(view);

				if (al2 != null)
					for (int j = 0; j < al2.size(); j++) {
						GeoPoint p = al2.get(j);
						// make sure duplicates aren't added
						if (!al.contains(p))
							al.add(p);
					}
			}
		}
		return al;

	}

	
	final public boolean isCasEvaluableObject() {
		return true;
	}
	
	String getCASString(boolean symbolic) {
		 
		// isMatrix() is rather expensive, and we only need it 
		// if we're using Maxima, so test for that first
		int casPrinttype = kernel.getCASPrintForm();
		if ((casPrinttype != ExpressionNode.STRING_TYPE_MAXIMA && casPrinttype != ExpressionNode.STRING_TYPE_MPREDUCE) || !isMatrix())
			return super.getCASString(symbolic);
			
		StringBuilder sb = new StringBuilder();
		if (casPrinttype == ExpressionNode.STRING_TYPE_MAXIMA){
			sb.append("matrix(");
			for (int i = 0; i < size(); i++) {
				GeoList geo = (GeoList) get(i);
				sb.append('[');
				for (int j = 0; j < geo.size(); j++) {
					sb.append(geo.get(j).getCASString(symbolic));
					if (j != geo.size() - 1)
						sb.append(',');
				}
				sb.append(']');
				if (i != size() - 1)
					sb.append(',');
			}
			sb.append(')');
		} else {
			sb.append("mat(");
			for (int i = 0; i < size(); i++) {
				GeoList geo = (GeoList) get(i);
				sb.append("(");
				for (int j = 0; j < geo.size(); j++) {
					sb.append(geo.get(j).getCASString(symbolic));
					if (j != geo.size() - 1)
						sb.append(',');
				}
				sb.append(')');
				if (i != size() - 1)
					sb.append(',');
			}
			sb.append(')');
		}
		return sb.toString();
	}
	
	public boolean listContains(GeoElement geo){
		if(geoList == null)
			return true;
		return geoList.contains(geo);
	}
	
	@Override
	public  boolean isLaTeXDrawableGeo(String latexStr) {
		
		
		// check for matrix
		if (getElementType() == GeoElement.GEO_CLASS_LIST) return true;
		
		// don't check getGeoElementForPropertiesDialog
		// as we want matrices to use latex
		if (getElementType() == GeoElement.GEO_CLASS_NUMERIC) return false;
		
		return  super.isLaTeXDrawableGeo(latexStr);
	}
	
}