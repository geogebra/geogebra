/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.euclidian.draw.CanvasDrawable;
import org.geogebra.common.euclidian.draw.DrawDropDownList;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.PathOrPoint;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoConicPartCircumcircle;
import org.geogebra.common.kernel.algos.AlgoConicPartConicPoints;
import org.geogebra.common.kernel.algos.AlgoDependentList;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoMacroInterface;
import org.geogebra.common.kernel.algos.AlgoSemicircle;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.properties.DelegateProperties;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * List of GeoElements
 */
public class GeoList extends GeoElement
		implements ListValue, DelegateProperties, TextProperties, Traceable, Path,
		Transformable, SpreadsheetTraceable, AbsoluteScreenLocateable, InequalityProperties,
		AngleProperties, Animatable, SegmentProperties {

	private static final int MAX_ITEMS_FOR_SCREENREADER = 8;

	private final static GeoClass ELEMENT_TYPE_MIXED = GeoClass.DEFAULT;

	private boolean trace;

	// GeoElement list members
	private final ArrayList<GeoElement> elements;

	// lists will often grow and shrink dynamically,
	// so we keep a cacheList of all old list elements
	private final ArrayList<GeoElementND> cacheList;

	private boolean isDefined = true;
	private boolean isDrawable = true;
	private boolean drawAsComboBox = false;
	private GeoClass elementType = ELEMENT_TYPE_MIXED;

	/**
	 * Whether this lists show all properties in the properties dialog. This is
	 * just recommended for the default GeoList in order to show all possible
	 * properties in the default configuration dialog.
	 */
	private boolean showAllProperties = false;

	private ArrayList<GeoElement> colorFunctionListener;
	private String typeStringForXML = null;
	private final StringBuilder sbBuildValueString = new StringBuilder(50);

	// Selection index for lists used in comboBoxes
	private int selectedIndex = 0;

	private int closestPointIndex;

	private TraceModesEnum traceModes = null;

	private boolean showOnAxis;

	private boolean[] directionInfoArray = null; // true if minParameter is for
													// start
	private int[] directionInfoOrdering = null; // simple map to the ordered
												// indexes
	private boolean shouldUseAlgoLocusList = true; // whether AlgoLocus is not
													// enough
	private boolean locusCalledAlgoLocusList = false; // if a locus ever used
														// this list as a path
	private int pointSize = EuclidianStyleConstants.DEFAULT_POINT_SIZE;
	private int pointStyle = -1; // use global option if -1
	// font options
	private boolean serifFont = false;
	private int fontStyle = GFont.PLAIN;
	private double fontSizeD = 1; // size relative to default font size
	private int printDecimals = -1;
	private int printFigures = -1;
	private boolean useSignificantFigures = false;

	private AngleStyle angleStyle = AngleStyle.ANTICLOCKWISE;
	private boolean emphasizeRightAngle = true;
	private int arcSize = EuclidianStyleConstants.DEFAULT_ANGLE_SIZE;

	private int totalWidth = 0;
	private int totalHeight = 0;

	private boolean wasDefinedWithCurlyBrackets = true;

	/**
	 * Creates new GeoList, size defaults to 20
	 *
	 * @param c
	 *            construction
	 */
	public GeoList(final Construction c) {
		this(c, 20);
	}

	private GeoList(final Construction c, final int size) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		elements = new ArrayList<>(size);
		cacheList = new ArrayList<>(size);
		setEuclidianVisible(false);
		// don't add here, see GGB-264
		// setBackgroundColor(GColor.WHITE);
	}

	@Override
	public void setParentAlgorithm(final AlgoElement algo) {
		super.setParentAlgorithm(algo);
		setEuclidianVisible(true);
		// GGB-1999 reset background to null: defaults should only apply to
		// dropdowns or independent lists
		setBackgroundColor(null);
	}

	/**
	 * Copy constructor
	 *
	 * @param list
	 *            list to copy
	 */
	public GeoList(final GeoList list) {
		this(list.cons, list.size());
		set(list);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.LIST;
	}

	/**
	 * Returns the element type of this list.
	 *
	 * @return ELEMENT_TYPE_MIXED == GeoClass.DEFAULT or GeoClass.NUMERIC,
	 *         GeoClass.POINT etc constant
	 */
	public GeoClass getElementType() {
		return elementType;
	}

	@Override
	public GeoList copy() {
		return new GeoList(this);
	}

	@Override
	public GeoList deepCopyGeo() {
		GeoList ret = new GeoList(cons);

		for (int i = 0; i < elements.size(); i++) {
			ret.add(elements.get(i).deepCopyGeo());
		}

		return ret;
	}

	@Override
	public void set(final GeoElementND geo) {
		reuseDefinition(geo);
		if (geo.isGeoNumeric()) { // eg SetValue[list, 2]
			// 1 -> first element
			setSelectedIndex(-1 + (int) ((GeoNumeric) geo).getDouble(), false);
			isDefined = true;

			return;
		}
		if (!(geo instanceof GeoList)) {
			setUndefined();
			return;
		}
		final GeoList l = (GeoList) geo;

		if ((l.cons != cons) && isAlgoMacroOutput()) {
			// MACRO CASE
			// this object is an output object of AlgoMacro
			// we need to check the references to all geos in the list
			final AlgoMacroInterface algoMacro = (AlgoMacroInterface) getParentAlgorithm();
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
	 *            true to show all properties
	 */
	public void setShowAllProperties(final boolean showAllProperties) {
		this.showAllProperties = showAllProperties;
	}

	private void copyListElements(final GeoList otherList) {
		final int otherListSize = otherList.size();
		ensureCapacity(otherListSize);
		elements.clear();

		for (int i = 0; i < otherListSize; i++) {
			final GeoElement otherElement = otherList.get(i);
			GeoElementND thisElement = null;

			// try to reuse cached GeoElement
			if (i < cacheList.size()) {
				final GeoElementND cachedGeo = cacheList.get(i);
				if (!cachedGeo.isLabelSet() && (cachedGeo
						.getGeoClassType() == otherElement.getGeoClassType())) {
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

	private GeoElement getCopyForList(final GeoElement geo) {
		if (geo.isLabelSet()) {
			// take original element
			return geo;
		}
		// create a copy of geo
		final GeoElement ret = geo.copyInternal(cons);
		ret.setParentAlgorithm(getParentAlgorithm());
		if (geo.getDefinition() != null) {
			ret.setDefinition(geo.getDefinition().deepCopy(kernel));
		}
		return ret;
	}

	private void applyVisualStyle(final GeoElement geo) {

		if (!geo.isLabelSet()) {
			geo.setObjColor(getObjectColor());

			geo.setColorSpace(getColorSpace());

			// copy color function
			if (getColorFunction() != null) {
				geo.setColorFunction(getColorFunction());
			} else {
				geo.removeColorFunction();
			}

			geo.setLineThickness(getLineThickness());
			geo.setLineType(getLineType());
			geo.setLineOpacity(getLineOpacity());
			geo.setLineTypeHidden(getLineTypeHidden());

			if (geo instanceof PointProperties) {
				((PointProperties) geo).setPointSize(getPointSize());
				((PointProperties) geo).setPointStyle(getPointStyle());
			}

			if (geo instanceof TextProperties) {
				((TextProperties) geo)
						.setFontSizeMultiplier(getFontSizeMultiplier());
				((TextProperties) geo).setFontStyle(getFontStyle());
				((TextProperties) geo).setSerifFont(isSerifFont());
				if (useSignificantFigures) {
					((TextProperties) geo).setPrintFigures(getPrintFigures(),
							false);
				} else {
					((TextProperties) geo).setPrintDecimals(getPrintDecimals(),
							false);
				}

			}

			geo.setFillType(fillType);
			geo.setHatchingAngle(hatchingAngle);
			geo.setHatchingDistance(hatchingDistance);
			if (!geo.getGeoElementForPropertiesDialog().isGeoImage()) {
				geo.setImageFileName(getGraphicsAdapter().getImageFileName());
			}
			geo.setAlphaValue(getAlphaValue());

			geo.setLayer(getLayer());
			geo.setBackgroundColor(getBackgroundColor());
			// copy ShowObjectCondition, unless it generates a
			// CirclularDefinitionException
			try {
				geo.setShowObjectCondition(getShowObjectCondition());
			} catch (final Exception e) {
				// Circular definition -- do nothing
			}

			setElementEuclidianVisible(geo, isSetEuclidianVisible());
		}
	}

	@Override
	public final void removeColorFunction() {
		if (getColorFunction() == null) {
			return;
		}
		super.removeColorFunction();

		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		final int size = elements.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.removeColorFunction();
			}
		}
	}

	@Override
	public final void setColorFunction(final GeoList col) {
		super.setColorFunction(col);

		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		final int size = elements.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.setColorFunction(col);
			}
		}

	}

	@Override
	public final void setColorSpace(final int colorSpace) {
		super.setColorSpace(colorSpace);

		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		final int size = elements.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.setColorSpace(colorSpace);
			}
		}

	}

	/*
	 * we DON'T want to override setLayer, as objects without label set eg the
	 * point in this {(1,1)} are drawn by the list
	 */

	@Override
	public final void setShowObjectCondition(final GeoBoolean bool)
			throws CircularDefinitionException {
		super.setShowObjectCondition(bool);

		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		final int size = elements.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.setShowObjectCondition(bool);
			}
		}

	}

	@Override
	public void setVisualStyle(final GeoElement style,
			boolean setAuxiliaryProperty) {
		super.setVisualStyle(style, setAuxiliaryProperty);

		// set point style
		if (style instanceof PointProperties) {
			setPointSize(((PointProperties) style).getPointSize());
			setPointStyle(((PointProperties) style).getPointStyle());
		}

		// set visual style
		if ((elements == null) || (elements.size() == 0)) {
			return;
		}
		final int size = elements.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.setVisualStyle(style, setAuxiliaryProperty);
			}
		}
	}

	@Override
	public void setObjColor(final GColor color) {
		super.setObjColor(color);
		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		final int size = elements.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = get(i);
			if (!geo.isLabelSet()) {
				geo.setObjColor(color);
			}
		}
	}

	@Override
	public void setBackgroundColor(final GColor color) {
		super.setBackgroundColor(color);

		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		final int size = elements.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = get(i);
			if (!geo.isLabelSet()) {
				geo.setBackgroundColor(color);
			}
		}
	}

	@Override
	public void setEuclidianVisible(final boolean visible) {
		super.setEuclidianVisible(visible);

		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		if (visible && drawAsComboBox && labelOffsetX == 0
				&& labelOffsetY == 0) {
			initScreenLocation();
		}
		final int size = elements.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = get(i);
			setElementEuclidianVisible(geo, visible);
		}
	}

	private void initScreenLocation() {
		int count = countComboBoxes();
		labelOffsetX = 5;
		EuclidianViewInterfaceSlim ev = kernel.getApplication()
				.getActiveEuclidianView();
		if (ev != null) {
			labelOffsetY = ev.getComboOffsetY() - 45 + 30 * count;
		} else {
			labelOffsetY = 5 + 30 * count;
		}
		// make sure combobox is visible on screen
		labelOffsetY = labelOffsetY / 400 * 10 + labelOffsetY % 400;
	}

	private int countComboBoxes() {
		int count = 0;

		// get all number and angle sliders
		TreeSet<GeoElement> lists = cons.getGeoSetLabelOrder(GeoClass.LIST);

		if (lists != null) {
			Iterator<GeoElement> it = lists.iterator();
			while (it.hasNext()) {
				GeoList list = (GeoList) it.next();
				if (list.isEuclidianVisible() && list.drawAsComboBox()) {
					count++;
				}
			}
		}

		return count;
	}

	private static void setElementEuclidianVisible(final GeoElement geo,
			final boolean visible) {
		if (!geo.isLabelSet()
				&& (!geo.isGeoNumeric() || !geo.isIndependent())) {
			geo.setEuclidianVisible(visible);
		}
	}

	@Override
	public void setVisibility(int viewId, boolean setVisible) {
		super.setVisibility(viewId, setVisible);
		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		final int size = elements.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = get(i);
			if (!geo.isLabelSet()) {
				geo.setVisibility(viewId, setVisible);
			}
		}
	}

	/**
	 * Returns this GeoList as a MyList object.
	 */
	@Override
	public MyList getMyList() {
		final int size = elements.size();
		final MyList myList = new MyList(kernel, size);
		copyListElements(myList);
		return myList;
	}

	/**
	 * @param myList list to copy into
	 */
	public void copyListElements(MyList myList) {
		for (GeoElement element : elements) {
			myList.addListElement(new ExpressionNode(kernel, element));
		}
	}

	@Override
	final public boolean isDefined() {
		return isDefined;
	}

	/**
	 * @param flag
	 *            true to make this list defined
	 */
	public void setDefined(final boolean flag) {
		isDefined = flag;

		if (!isDefined) {

			final int size = elements.size();
			for (int i = 0; i < size; i++) {
				final GeoElement geo = elements.get(i);
				if (!geo.isLabelSet()) {
					geo.setUndefined();
				}
			}

			// set also cached geos to undefined (for lists of lists)
			for (int i = size; i < cacheList.size(); i++) {
				final GeoElementND geo = cacheList.get(i);
				if (!geo.isLabelSet()) {
					geo.setUndefined();
				}
			}

		}
	}

	@Override
	public void setUndefined() {
		setDefined(false);
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined() && isDrawable();
	}

	@Override
	public boolean isDrawable() {
		return isDrawable || drawAsComboBox();
	}

	/**
	 * Clear the list
	 */
	public final void clear() {
		elements.clear();
	}

	/**
	 * free up memory and set undefined
	 */
	public final void clearCache() {
		if (cacheList.size() > 0) {
			for (int i = 0; i < cacheList.size(); i++) {
				final GeoElementND geo = cacheList.get(i);
				if ((geo != null) && !geo.isLabelSet()) {
					geo.remove();
				}
			}
		}
		cacheList.clear();
		clear();
		setUndefined();
	}

	/**
	 * Adds a geo to this list
	 *
	 * @param geo
	 *            geo to be added
	 */
	public final void add(final GeoElementND geo) {
		// add geo to end of list
		elements.add(geo.toGeoElement());

		if (elements.size() == 1) {
			setTypeStringForXML(geo.getXMLtypeString());
		}

		/*
		 * // needed for Corner[Element[text // together with swapping these
		 * lines over in MyXMLio: //kernel.updateConstruction();
		 * //kernel.setNotifyViewsActive(oldVal);
		 *
		 * if (geo.isGeoText()) {
		 * ((GeoText)geo).setNeedsUpdatedBoundingBox(true); }
		 */

		// add to cache
		final int pos = elements.size() - 1;
		if (pos < cacheList.size()) {
			cacheList.set(pos, geo);
		} else {
			cacheList.add(geo);
		}

		// init element type
		if (pos == 0) {
			isDrawable = true;
			elementType = geo.getGeoClassType();
		}
		// check element type
		else if (elementType != geo.getGeoClassType()) {
			if ((elementType == GeoClass.POINT3D
					|| elementType == GeoClass.POINT) && geo.isGeoPoint()) {
				elementType = GeoClass.POINT3D;
			} else {
				elementType = ELEMENT_TYPE_MIXED;
			}
		}
		updateDrawableFlag(geo);

		// set visual style of this list
		applyVisualStyle(geo.toGeoElement());
		if (!geo.isLabelSet()) {
			geo.setViewFlags(getViewSet());
			geo.setVisibleInView3D(this);
			geo.setVisibleInViewForPlane(this);
		}
	}

	private void updateDrawableFlag(GeoElementND geo) {
		isDrawable = isDrawable && geo.isDrawable() && !geo.isGeoButton()
				&& !(geo instanceof GeoBoolean) && !(geo instanceof GeoNumeric
						&& ((GeoNumeric) geo).isSlider());
	}

	/**
	 * Removes geo from this list. Note: geo is not removed from the
	 * construction.
	 *
	 * @param geo
	 *            element to be removed
	 */
	public final void remove(final GeoElement geo) {
		elements.remove(geo);

	}

	/**
	 * Removes the first geo from the beginning of this list, and adds a new geo
	 * to its end (useful for indefinite appending)
	 *
	 * @param geo
	 *            element to be added
	 */
	public final void addQueue(final GeoElement geo) {
		GeoElement first = get(0);
		remove(first);
		first.remove();
		add(geo);
	}

	/**
	 * Removes i-th element from this list. Note: this element is not removed
	 * from the construction.
	 *
	 * @param index
	 *            position of element to be removed
	 */
	public final void remove(final int index) {
		elements.remove(index);

	}

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param index
	 *            element position
	 * @return the element at the specified position in this list.
	 */
	final public GeoElement get(final int index) {
		return elements.get(index);
	}

	/**
	 * Returns the element at the specified position in this (2D) list.
	 *
	 * @param index
	 *            element position -- row
	 * @param index2
	 *            element position -- column
	 * @return the element at the specified position in this (2D) list.
	 */
	final public GeoElement get(final int index, final int index2) {
		return ((GeoList) elements.get(index)).get(index2);
	}

	/**
	 * Tries to return this list as an array of double values
	 *
	 * @return array of double values from this list
	 */
	@Override
	public double[] toDouble(int offset) {
		int length = elements.size();
		try {
			final double[] valueArray = new double[length - offset];
			for (int i = offset; i < length; i++) {
				valueArray[i - offset] = elements.get(i).evaluateDouble();
			}
			return valueArray;
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Increases capcity of this list if necessary
	 *
	 * @param size
	 *            capcity to ensure
	 */
	final public void ensureCapacity(final int size) {
		elements.ensureCapacity(size);
		cacheList.ensureCapacity(size);
	}

	@Override
	final public int size() {
		return elements.size();
	}

	/**
	 * @return number of elements in this list's cache
	 */
	final public int getCacheSize() {
		return cacheList.size();
	}

	/**
	 * Returns the cached element at the specified position in this list's
	 * cache.
	 *
	 * @param index
	 *            element position
	 * @return cached element at given position
	 */
	final public GeoElement getCached(final int index) {
		return cacheList.get(index).toGeoElement();
	}

	@Override
	public String toString(StringTemplate tpl) {
		StringBuilder sbToString = new StringBuilder(50);
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(buildValueString(tpl).toString());
		return sbToString.toString();
	}

	@Override
	public String toStringMinimal(StringTemplate tpl) {
		sbBuildValueString.setLength(0);
		if (!isDefined) {
			sbBuildValueString.append("?");
			return sbBuildValueString.toString();
		}

		// first (n-1) elements
		final int lastIndex = elements.size() - 1;
		if (lastIndex > -1) {
			for (int i = 0; i < lastIndex; i++) {
				final GeoElement geo = elements.get(i);

				sbBuildValueString
						.append(geo.getAlgebraDescriptionRegrOut(tpl));
				sbBuildValueString.append(" ");
			}

			// last element
			final GeoElement geo = elements.get(lastIndex);
			sbBuildValueString.append(geo.getAlgebraDescriptionRegrOut(tpl));
		}

		return sbBuildValueString.toString();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return buildValueString(tpl).toString();
	}

	private StringBuilder buildValueString(StringTemplate tpl) {
		sbBuildValueString.setLength(0);
		if (!isDefined) {
			sbBuildValueString.append("?");
			return sbBuildValueString;
		}

		tpl.leftCurlyBracket(sbBuildValueString);

		// first (n-1) elements
		final int lastIndex = elements.size() - 1;
		if (lastIndex > -1) {
			for (int i = 0; i < lastIndex; i++) {
				final GeoElement geo = elements.get(i);
				sbBuildValueString.append(geo.toOutputValueString(tpl));
				tpl.getComma(sbBuildValueString, getLoc());
				tpl.appendOptionalSpace(sbBuildValueString);
			}

			// last element
			final GeoElement geo = elements.get(lastIndex);
			sbBuildValueString.append(geo.toOutputValueString(tpl));
		}

		tpl.rightCurlyBracket(sbBuildValueString);

		return sbBuildValueString;
	}

	@Override
	public boolean isGeoList() {
		return true;
	}

	@Override
	public boolean evaluatesToList() {
		return true;
	}

	/**
	 * save object in XML format
	 */
	@Override
	public final void getXML(boolean getListenersToo, final StringBuilder sb) {
		// an independent list needs to add
		// its expression itself
		// e.g. {1,2,3}
		if (isDefined() && isIndependent() && (getDefaultGeoType() < 0)) {
			sb.append("<expression");
			sb.append(" label=\"");
			StringUtil.encodeXML(sb, label);
			sb.append("\" exp=\"");
			if (getDefinition() != null) {
				StringUtil.encodeXML(sb,
						getDefinition().toString(StringTemplate.xmlTemplate));
			} else {
				StringUtil.encodeXML(sb,
						toValueString(StringTemplate.xmlTemplate));
			}
			sb.append("\"/>\n");
		}

		getElementOpenTagXML(sb);
		getXMLtags(sb);

		if (size() == 0 && getTypeStringForXML() != null) {
			sb.append("<listType val=\"");
			sb.append(getTypeStringForXML());
			sb.append("\"/>\n");
		}

		if (selectedIndex != 0) {
			sb.append("\t<selectedIndex val=\"");
			sb.append(selectedIndex);
			sb.append("\"/>\n");
		}

		if (drawAsComboBox) {
			sb.append("\t<comboBox val=\"true\"/>\n");
		}

		// point style
		XMLBuilder.appendPointProperties(sb, this);

		GeoText.appendFontTag(sb, serifFont, fontSizeD, fontStyle, false,
				kernel.getApplication());

		// print decimals
		if ((printDecimals >= 0) && !useSignificantFigures) {
			sb.append("\t<decimals val=\"");
			sb.append(printDecimals);
			sb.append("\"/>\n");
		}

		// print significant figures
		if ((printFigures >= 0) && useSignificantFigures) {
			sb.append("\t<significantfigures val=\"");
			sb.append(printFigures);
			sb.append("\"/>\n");
		}

		// AngleProperties
		XMLBuilder.appendAngleStyle(sb, angleStyle, emphasizeRightAngle);

		if (isSymbolicMode()) {
			sb.append("\t<symbolic val=\"true\" />\n");
		}
		// AngleProperties end

		// for ComboBoxes (and comments)
		getCaptionXML(sb);
		if (getListenersToo) {
			getListenerTagsXML(sb);
		}

		sb.append("</element>\n");
	}

	// needed for eg x(Element[list1,1]) when list1 is saved as an empty list
	// The Element command needs to know in advance what type of geo the list
	// will hold

	/**
	 * needed for eg x(Element[list1,1]) when list1 is saved as an empty list
	 * The Element/Cell/Object command needs to know in advance what type of geo
	 * the list will hold
	 *
	 * @return type, eg "point"
	 */
	public String getTypeStringForXML() {
		return typeStringForXML;
	}

	/**
	 * needed for eg x(Element[list1,1]) when list1 is saved as an empty list
	 * The Element/Cell/Object command needs to know in advance what type of geo
	 * the list will hold
	 *
	 * @param type
	 *            eg "point"
	 */
	public void setTypeStringForXML(String type) {
		typeStringForXML = type;
	}

	/**
	 * Registers geo as a listener for updates of this boolean object. If this
	 * object is updated it calls geo.updateConditions()
	 *
	 * @param geo
	 *            element which should use this list as dynamic color
	 */
	public void registerColorFunctionListener(final GeoElement geo) {
		if (colorFunctionListener == null) {
			colorFunctionListener = new ArrayList<>();
		}
		colorFunctionListener.add(geo);
	}

	/**
	 * Unregisters geo as a listener for updates of this boolean object.
	 *
	 * @param geo
	 *            element which uses this list as dynamic color
	 */
	public void unregisterColorFunctionListener(final GeoElement geo) {
		if (colorFunctionListener != null) {
			colorFunctionListener.remove(geo);
		}
	}

	/**
	 * Calls super.update() and update() for all registered condition listener
	 * geos. // Michael Borcherds 2008-04-02
	 */
	@Override
	public void update(boolean drag) {
		super.update(drag);

		// update information on whether this path is fit for AlgoLocus
		// or it can only support AlgoLocusList (call this method here again
		// because the GeoList might change - code will run only if locus used
		// it)
		shouldUseAlgoLocusList(false);

		// update all registered locatables (they have this point as start
		// point)
		if (colorFunctionListener != null) {
			// AbstractApplication.debug("GeoList update listeners");
			for (int i = 0; i < colorFunctionListener.size(); i++) {
				final GeoElement geo = colorFunctionListener.get(i);
				// kernel.notifyUpdate(geo);
				// geo.toGeoElement().updateCascade();
				geo.updateVisualStyle(GProperty.COLOR);
				// AbstractApplication.debug(geo);
			}
			// kernel.notifyRepaint();
		}
	}

	/**
	 * Tells condition listeners that their condition is removed and calls
	 * super.remove()
	 */
	@Override
	public void doRemove() {
		if (colorFunctionListener != null) {
			// copy conditionListeners into array
			final Object[] geos = colorFunctionListener.toArray();
			colorFunctionListener.clear();

			// tell all condition listeners
			for (int i = 0; i < geos.length; i++) {
				final GeoElement geo = (GeoElement) geos[i];
				geo.removeColorFunction();
				kernel.notifyUpdate(geo);
			}
		}

		super.doRemove();
	}

	/**
	 * return whether this list equals GeoList list
	 */
	@Override
	final public boolean isEqual(final GeoElementND geo) {
		if (!geo.isGeoList()) {
			return false;
		}

		final GeoList list = (GeoList) geo;

		// check sizes
		if (elements.size() != list.size()) {
			return false;
		}

		// check each element
		for (int i = 0; i < list.elements.size(); i++) {
			final GeoElement geoA = elements.get(i);
			final GeoElement geoB = list.get(i);

			if (!geoA.isEqual(geoB)) {
				return false;
			}
		}

		// all list elements equal
		return true;
	}

	@Override
	public void setZero() {
		elements.clear();
	}

	@Override
	public void setLineThickness(final int thickness) {
		super.setLineThickness(thickness);

		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.setLineThickness(thickness);
			}
		}

		// Application.debug("GeoList.setLineThickness "+thickness);
	}

	/**
	 * @return minimum line thickness (normally 1, but 0 for polygons, integrals
	 *         etc)
	 */
	@Override
	public int getMinimumLineThickness() {
		if ((elements == null) || (elements.size() == 0)) {
			return 1;
		}

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				if (geo.getMinimumLineThickness() == 1) {
					return 1;
				}
			}
		}

		return 0;
	}

	@Override
	public void setLineType(final int type) {
		super.setLineType(type);

		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.setLineType(type);
			}
		}

	}

	@Override
	public void setLineTypeHidden(final int type) {
		super.setLineTypeHidden(type);

		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.setLineTypeHidden(type);
			}
		}

	}

	@Override
	public void setPointSize(final int size) {
		pointSize = size;
		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet() && (geo instanceof PointProperties)) {
				((PointProperties) geo).setPointSize(size);
			}
		}
	}

	@Override
	public int getPointSize() {
		return pointSize;
	}

	@Override
	public void setPointStyle(final int style) {
		pointStyle = style;

		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet() && (geo instanceof PointProperties)) {
				((PointProperties) geo).setPointStyle(style);
			}
		}
	}

	@Override
	public double getAlphaValue() {
		if (super.getAlphaValue() == -1) {
			// no alphaValue set
			// so we need to set it to that of the first element, if there is
			// one
			if ((elements != null) && (elements.size() > 0)) {

				// get alpha value of first element
				final double alpha = elements.get(0).getAlphaValue();

				// Application.debug("setting list alpha to "+alpha);

				super.setAlphaValue(alpha);

				// set all the other elements in the list
				// if appropriate
				if (elements.size() > 1) {
					for (int i = 1; i < elements.size(); i++) {
						final GeoElement geo = elements.get(i);
						if (!geo.isLabelSet()) {
							geo.setAlphaValue(alpha);
						}
					}

				}
			} else {
				return -1.0f;
			}
		}

		return super.getAlphaValue();
	}

	@Override
	public void setAlphaValue(final double alpha) {
		if (alpha == -1) {
			// wait until we have a GeoElement in the list to use
			// see getAlphaValue()
			alphaValue = -1;
			return;
		}

		super.setAlphaValue(alpha);

		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.setAlphaValue(alpha);
			}
		}

	}

	@Override
	public int getPointStyle() {
		return pointStyle;
	}

	@Override
	public boolean isFillable() {
		if ((elements == null) || (elements.size() == 0)) {
			return false;
		}

		boolean someFillable = false;
		boolean allLabelsSet = true;

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (geo.isFillable()) {
				someFillable = true;
			}
			if (!geo.isLabelSet()) {
				allLabelsSet = false;
			}
		}

		return someFillable && !allLabelsSet;
	}

	@Override
	public GeoElement getGeoElementForPropertiesDialog() {
		if ((elements.size() > 0) && (elementType != ELEMENT_TYPE_MIXED)) {
			return get(0).getGeoElementForPropertiesDialog(); // getGeoElementForPropertiesDialog()
			// to cope with
			// lists of
			// lists
		}
		return this;
	}

	/**
	 * @return true iff this list is in the form { {1,2}, {3,4}, {5,6} } etc
	 */
	@Override
	public boolean isMatrix() {
		if (!getElementType().equals(GeoClass.LIST) || (size() == 0)) {
			return false;
		}

		final GeoElement geo0 = get(0);
		if (geo0.isGeoList()) {
			final int length = ((GeoList) geo0).size();
			if (length == 0) {
				return false;
			}
			if (size() > 0) {
				for (int i = 0; i < size(); i++) {
					final GeoElement geoi = get(i);
					// Application.debug(((GeoList)geoi).get(0).getGeoClassType()+"");
					if (!get(i).isGeoList() || (((GeoList) geoi).size() == 0)
							|| (((GeoList) geoi).size() != length)) {
						return false;
					}
					for (int j = 0; j < ((GeoList) geoi).size(); j++) {
						final GeoElement geoij = ((GeoList) geoi).get(j);
						if (!geoij.getGeoClassType().equals(GeoClass.NUMERIC)
								&& !geoij.getGeoClassType()
										.equals(GeoClass.FUNCTION)
								&& !geoij.getGeoClassType()
										.equals(GeoClass.FUNCTION_NVAR)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public double getFontSizeMultiplier() {
		return fontSizeD;
	}

	@Override
	public void setFontSizeMultiplier(final double size) {
		fontSizeD = size;

		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if ((geo instanceof TextProperties) && !geo.isLabelSet()) {
				((TextProperties) geo).setFontSizeMultiplier(size);
			}
		}
	}

	@Override
	public int getFontStyle() {
		return fontStyle;
	}

	@Override
	public void setFontStyle(final int fontStyle) {
		this.fontStyle = fontStyle;

		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if ((geo instanceof TextProperties) && !geo.isLabelSet()) {
				((TextProperties) geo).setFontStyle(fontStyle);
			}
		}
	}

	@Override
	final public int getPrintDecimals() {
		return printDecimals;
	}

	@Override
	final public int getPrintFigures() {
		return printFigures;
	}

	@Override
	public void setPrintDecimals(final int printDecimals,
			final boolean update) {
		this.printDecimals = printDecimals;
		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if ((geo instanceof TextProperties) && !geo.isLabelSet()) {
				((TextProperties) geo).setPrintDecimals(printDecimals, update);
			}
		}
	}

	@Override
	public void setPrintFigures(final int printFigures, final boolean update) {
		this.printFigures = printFigures;
		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if ((geo instanceof TextProperties) && !geo.isLabelSet()) {
				((TextProperties) geo).setPrintFigures(printFigures, update);
			}
		}
	}

	@Override
	public boolean useSignificantFigures() {
		return useSignificantFigures;

	}

	@Override
	public boolean isSerifFont() {
		return serifFont;
	}

	@Override
	public void setSerifFont(final boolean serifFont) {
		this.serifFont = serifFont;
		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if ((geo instanceof TextProperties) && !geo.isLabelSet()) {
				((TextProperties) geo).setSerifFont(serifFont);
			}
		}
	}

	@Override
	public void setHatchingAngle(final int angle) {
		super.setHatchingAngle(angle);
		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.setHatchingAngle(angle);
			}
		}
	}

	@Override
	public void setHatchingDistance(final int distance) {
		super.setHatchingDistance(distance);
		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.setHatchingDistance(distance);
			}
		}
	}

	@Override
	public void setFillType(final FillType type) {
		super.setFillType(type);
		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.setFillType(type);
			}
		}
	}

	@Override
	public void setFillImage(final String filename) {
		super.setFillImage(filename);
		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.setFillImage(filename);
			}
		}
	}

	@Override
	public void setImageFileName(final String filename) {
		super.setImageFileName(filename);
		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.setImageFileName(filename);
			}
		}
	}

	/**
	 * for a list like this: {Circle[B, A], (x(A), y(A)), "text"} we want to be
	 * able to set the line properties
	 *
	 * @return true if all elements have line properties
	 */
	@Override
	public boolean showLineProperties() {
		if (showAllProperties) {
			return true;
		}

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (geo.showLineProperties() && !geo.isLabelSet()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * for a list like this: {Circle[B, A], (x(A), y(A)), "text"} we want to be
	 * able to set the point properties
	 *
	 * @return true if all elements have point properties
	 */
	@Override
	public boolean showPointProperties() {
		if (showAllProperties) {
			return true;
		}

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if ((geo instanceof PointProperties)
					&& ((PointProperties) geo).showPointProperties() && !geo.isLabelSet()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toLaTeXString(final boolean symbolic, StringTemplate tpl) {
		return toLaTeXString(symbolic, false, tpl);
	}

	@Override
	public String toLaTeXString(
			final boolean symbolic, boolean symbolicContext, StringTemplate tpl) {
		if (isMatrix()) {

			// int rows = size();
			final int cols = ((GeoList) get(0)).size();

			final StringBuilder sb = new StringBuilder();

			sb.append("\\left(\\begin{array}{");
			// eg rr
			for (int i = 0; i < cols; i++) {
				sb.append('r');
			}
			sb.append("}");
			for (int i = 0; i < size(); i++) {
				final GeoList row = (GeoList) get(i);
				for (int j = 0; j < row.size(); j++) {
					GeoElement geo = row.get(j);
					sb.append(symbolic ? geo.getLabel(tpl)
							: geo.toLaTeXString(false, symbolicContext, tpl));
					if (j < row.size() - 1) {
						sb.append("&");
					}
				}
				sb.append("\\\\");
			}
			sb.append(" \\end{array}\\right)");
			return sb.toString();
			// return "\\begin{array}{ll}1&2 \\\\ 3&4 \\\\ \\end{array}";
		}

		return super.toLaTeXString(symbolic, tpl);

	}

	@Override
	protected void getXMLtags(final StringBuilder sb) {
		super.getXMLtags(sb);

		getLineStyleXML(sb);
	}

	/**
	 * @return selected index
	 */
	public int getSelectedIndex() {
		if (selectedIndex >= size()) {
			selectedIndex = 0;
		}

		return selectedIndex;
	}

	/**
	 * @param selectedIndex0
	 *            new selected index
	 * @param update
	 *            t
	 */
	public void setSelectedIndex(final int selectedIndex0, boolean update) {
		selectedIndex = selectedIndex0;

		if (selectedIndex < 0 || selectedIndex > size() - 1) {
			selectedIndex = 0;
		}

		if (update) {
			updateCascade();
			getKernel().notifyRepaint();
			getKernel().storeUndoInfo();
		}
	}

	/*
	 * mathieu : for drawing 3D elements of the list
	 */
	@Override
	public boolean hasDrawable3D() {
		return true;
	}

	/**
	 * when list is visible (as a combobox) this returns the element selected by
	 * the user or null if there's a problem
	 *
	 * @return selected element
	 */
	public GeoElement getSelectedElement() {
		if ((selectedIndex > -1) && (selectedIndex < size())) {
			return get(selectedIndex);
		}
		return null;
	}

	@Override
	public void setTrace(final boolean trace) {
		this.trace = trace;
	}

	@Override
	public boolean getTrace() {
		return trace;
	}

	@Override
	public boolean isTraceable() {
		return true;
	}

	@Override
	public boolean isLimitedPath() {
		return false;
	}

	@Override
	public boolean isPath() {
		return true;
	}

	/*
	 * adapted from GeoLocus
	 */
	@Override
	public void pointChanged(final GeoPointND P) {
		// Application.debug("pointChanged",1);

		P.updateCoords();

		// update closestPointIndex
		getNearestPoint(P);
		if (elements.size() == 0) {
			if (P.isDefined()) {
				P.setUndefined();
			}
			return;
		}
		final GeoElement geo = get(closestPointIndex);
		if (!(geo instanceof PathOrPoint)) {
			Log.debug("TODO: " + geo.getGeoClassType()
					+ " should implement PathOrPoint interface");
			return;
		}
		final PathOrPoint path = (PathOrPoint) get(closestPointIndex);

		int type = P.getPathParameter().getPathType();

		path.pointChanged(P);

		final PathParameter pp = P.getPathParameter();

		pp.setPathType(type);

		// update path param
		// 0-1 for first obj
		// 1-2 for second
		// etc
		// Application.debug(pp.t+" "+path.getMinParameter()+"
		// "+path.getMaxParameter());

		int closestPointIndexBack = closestPointIndex;
		if (directionInfoOrdering != null) {
			for (int i = 0; i < this.size(); i++) {
				if (directionInfoOrdering[i] == closestPointIndex) {
					closestPointIndexBack = i;
					break;
				}
			}
		}

		double normalized = PathNormalizer.toNormalizedPathParameter(pp.t,
				path.getMinParameter(), path.getMaxParameter());
		if (path.isGeoPoint()) {
			normalized = Kernel.STANDARD_PRECISION; // to avoid rounding errors
		}
		if ((directionInfoArray == null)
				|| directionInfoArray[closestPointIndex]) {
			pp.t = closestPointIndexBack + normalized;
		} else {
			pp.t = closestPointIndexBack + 1 - normalized;
		}
	}

	/**
	 * Nearest point to p
	 *
	 * @param p
	 *            point
	 */
	public void getNearestPoint(final GeoPointND p) {
		// Application.printStacktrace(p.inhomX+" "+p.inhomY);
		double distance = Double.POSITIVE_INFINITY;
		closestPointIndex = 0; // default - first object

		// double closestIndex = -1;
		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (geo instanceof PathOrPoint) {
				final double d = p.distanceToPath((PathOrPoint) geo);

				// Log.debug(i+" "+d+" "+distance+" "+geo);
				if (d < distance) {
					distance = d;
					closestPointIndex = i;
				}
			}
		}

		// Application.debug("closestPointIndex="+closestPointIndex);

		// return get(closestPointIndex).getNearestPoint(p);
	}

	@Override
	public double distance(final GeoPoint p) {
		double distance = Double.POSITIVE_INFINITY;
		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			final double d = geo.distance(p);
			if (d < distance) {
				distance = d;
			}
		}

		return distance;
	}

	@Override
	public double distance(final GeoPointND p) {
		double distance = Double.POSITIVE_INFINITY;
		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			final double d = geo.distance(p);
			if (d < distance) {
				distance = d;
			}
		}

		return distance;
	}

	@Override
	public void pathChanged(final GeoPointND PI) {
		if (size() == 0) {
			PI.setUndefined();
			return;
		}
		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(PI)) {
			pointChanged(PI);
			return;
		}

		final PathParameter pp = PI.getPathParameter();

		double t = pp.getT();
		int n = getIndexFromParameter(t);

		// check n is in a sensible range
		if ((n >= size()) || (n < 0)) {

			n = (n < 0) ? 0 : size() - 1;
		}

		int n1 = n;

		if (directionInfoOrdering != null) {
			n = directionInfoOrdering[n];
		}

		GeoElement elementN = get(n);

		if (!(elementN instanceof PathOrPoint)) {
			Log.debug("not path or point");
			return;
		}

		final PathOrPoint path = (PathOrPoint) elementN;

		int pt = pp.getPathType();
		if (path instanceof GeoQuadricND) {
			pp.setPathType(((GeoQuadricND) path).getType());
		}

		// check direction of the path, as it is not sure that the main list
		// path
		// has the same direction for minParameter and maxParameter as the
		// subpathes
		if ((directionInfoArray == null) || directionInfoArray[n]) {
			pp.setT(PathNormalizer.toParentPathParameter(t - n1,
					path.getMinParameter(), path.getMaxParameter()));
		} else {
			pp.setT(PathNormalizer.toParentPathParameter(n1 - t + 1,
					path.getMinParameter(), path.getMaxParameter()));
		}

		// Application.debug("pathChanged "+n);

		path.pathChanged(PI);

		t = pp.getT();
		// Application.debug(PathNormalizer.toNormalizedPathParameter(t,
		// path.getMinParameter(), path.getMaxParameter()));

		if ((directionInfoArray == null) || directionInfoArray[n]) {
			pp.setT(PathNormalizer.toNormalizedPathParameter(t,
					path.getMinParameter(), path.getMaxParameter()) + n1);
		} else {
			pp.setT(1
					- PathNormalizer.toNormalizedPathParameter(t,
							path.getMinParameter(), path.getMaxParameter())
					+ n1);
		}

		pp.setPathType(pt);
	}

	private int getIndexFromParameter(double t) {
		return t < 0 ? 0 : Math.min((int) Math.floor(t), size() - 1);
	}

	@Override
	public boolean isOnPath(final GeoPointND PI, final double eps) {
		// Application.debug("isOnPath",1);
		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (((PathOrPoint) geo).isOnPath(PI, eps)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public double getMinParameter() {
		return 0;
	}

	@Override
	public double getMaxParameter() {
		return elements.size();
	}

	@Override
	public boolean isClosedPath() {
		return !shouldUseAlgoLocusList;
	}

	@Override
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	@Override
	public boolean hasMoveableInputPoints(
			final EuclidianViewInterfaceSlim view) {
		// we don't want e.g. DotPlots to be dragged
		if (!((getParentAlgorithm() == null)
				|| (getParentAlgorithm() instanceof AlgoDependentList))) {
			return false;
		}
		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);

			if (geo.isGeoPoint()) {
				if (!geo.isMoveable()) {
					return false;
				}
			} else {
				// not point
				if (!geo.hasMoveableInputPoints(view)) {
					return false;
				}
			}
		}

		return true;
	}

	/*
	 * allow lists like this to be dragged {Segment[A, B], Segment[B, C], (3.92,
	 * 4)}
	 */
	@Override
	public ArrayList<GeoPointND> getFreeInputPoints(
			final EuclidianViewInterfaceSlim view) {
		final ArrayList<GeoPointND> al = new ArrayList<>();

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);

			if (geo.isGeoPoint()) {
				final GeoPoint p = (GeoPoint) geo;
				if (p.isMoveable() && !al.contains(p)) {
					al.add(p);
				}

			} else {
				final ArrayList<GeoPointND> al2 = geo.getFreeInputPoints(view);

				if (al2 != null) {
					for (int j = 0; j < al2.size(); j++) {
						final GeoPointND p = al2.get(j);
						// make sure duplicates aren't added
						if (!al.contains(p)) {
							al.add(p);
						}
					}
				}
			}
		}
		return al;

	}

	@Override
	final public boolean isCasEvaluableObject() {
		return true;
	}

	@Override
	public String getCASString(StringTemplate tpl, final boolean symbolic) {
		// isMatrix() is rather expensive, and we only need it
		// if we're using Maxima, so test for that first
		final StringType casPrinttype = tpl.getStringType();
		if ((!casPrinttype.isGiac()) || !isMatrix()) {
			return super.getCASString(tpl, symbolic);
		}

		final StringBuilder sb = new StringBuilder();
		if (casPrinttype.isGiac()) {
			sb.append("matrix(");
			for (int i = 0; i < size(); i++) {
				final GeoList geo = (GeoList) get(i);
				sb.append('[');
				for (int j = 0; j < geo.size(); j++) {
					sb.append(geo.get(j).getCASString(tpl, symbolic));
					if (j != (geo.size() - 1)) {
						sb.append(',');
					}
				}
				sb.append(']');
				if (i != (size() - 1)) {
					sb.append(',');
				}
			}
			sb.append(')');
		} else {
			sb.append("mat(");
			for (int i = 0; i < size(); i++) {
				final GeoList geo = (GeoList) get(i);
				sb.append("(");
				for (int j = 0; j < geo.size(); j++) {
					sb.append(geo.get(j).getCASString(tpl, symbolic));
					if (j != (geo.size() - 1)) {
						sb.append(',');
					}
				}
				sb.append(')');
				if (i != (size() - 1)) {
					sb.append(',');
				}
			}
			sb.append(')');
		}
		return sb.toString();
	}

	/**
	 * Returns true if this list contains given geo (check based on ==, not
	 * value equality)
	 *
	 * @param geo
	 *            geo to check
	 * @return true if the list contains given geo
	 */
	public boolean listContains(final GeoElement geo) {
		if (elements == null) {
			return true;
		}
		return elements.contains(geo);
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		if (size() == 0) {
			return false;
		}
		// check for matrix
		if (getElementType().equals(GeoClass.LIST)) {
			GeoClass geoClass = ((GeoList) get(0)).getElementType();
			return geoClass.equals(GeoClass.NUMERIC)
					|| geoClass.equals(GeoClass.FUNCTION)
					|| (!geoClass.equals(GeoClass.LIST)
							&& get(0).isLaTeXDrawableGeo());
		}

		// don't check getGeoElementForPropertiesDialog
		// as we want matrices to use latex
		if (getElementType().equals(GeoClass.NUMERIC)) {
			return false;
		}
		boolean ret = true;
		for (int i = 0; i < elements.size(); i++) {
			GeoElement geo1 = elements.get(i);
			if (!geo1.isLaTeXDrawableGeo()) {
				return false;
			}
		}
		return ret;
	}

	@Override
	public void updateColumnHeadingsForTraceValues() {
		resetSpreadsheetColumnHeadings();

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (geo instanceof SpreadsheetTraceable) {
				final ArrayList<GeoText> geoHead = geo.getColumnHeadings();
				for (int j = 0; j < geoHead.size(); j++) {
					spreadsheetColumnHeadings.add(geoHead.get(j));
				}
			}
		}

	}

	/**
	 * @param geos
	 *            list of geos
	 * @return available trace to spreadsheet mode (values/copy) for the geos
	 *         list
	 */
	final static public TraceModesEnum getTraceModes(
			ArrayList<GeoElement> geos) {

		TraceModesEnum traceModes = null;

		for (GeoElement geo : geos) {

			if (!geo.isSpreadsheetTraceable()) {
				traceModes = TraceModesEnum.NOT_TRACEABLE;
				return traceModes;
			}

			TraceModesEnum geoMode = geo.getTraceModes();
			if (traceModes == null) {
				traceModes = geoMode;
				if (geoMode == TraceModesEnum.NOT_TRACEABLE) {
					return traceModes;
				}
			} else {
				switch (geoMode) {
				case NOT_TRACEABLE:
					traceModes = TraceModesEnum.NOT_TRACEABLE;
					return traceModes;
				case ONE_VALUE_ONLY:
				case SEVERAL_VALUES_ONLY:
					if (traceModes == TraceModesEnum.ONLY_COPY) {
						traceModes = TraceModesEnum.NOT_TRACEABLE;
						return traceModes;
					}

					traceModes = TraceModesEnum.SEVERAL_VALUES_ONLY;
					break;

				case ONE_VALUE_OR_COPY:
				case SEVERAL_VALUES_OR_COPY:
					if (traceModes == TraceModesEnum.ONE_VALUE_ONLY) {
						traceModes = TraceModesEnum.SEVERAL_VALUES_ONLY;
					} else if (traceModes == TraceModesEnum.ONE_VALUE_OR_COPY) {
						traceModes = TraceModesEnum.SEVERAL_VALUES_OR_COPY;
					}
					break;

				case ONLY_COPY:
					if (traceModes == TraceModesEnum.ONE_VALUE_ONLY
							|| traceModes == TraceModesEnum.SEVERAL_VALUES_ONLY) {
						traceModes = TraceModesEnum.NOT_TRACEABLE;
						return traceModes;
					}

					traceModes = TraceModesEnum.ONLY_COPY;
					break;

				}
			}
		}

		return traceModes;

	}

	@Override
	public TraceModesEnum getTraceModes() {
		if (traceModes != null) {
			return traceModes;
		}

		if (getParentAlgorithm() != null
				&& (getParentAlgorithm() instanceof AlgoDependentList)) {
			// list = {A, B} : traceModes is computed from A, B
			traceModes = getTraceModes(elements);
		} else {
			// e.g. Sequence[...] is only copied
			traceModes = TraceModesEnum.ONLY_COPY;
		}

		return traceModes;
	}

	@Override
	public boolean hasSpreadsheetTraceModeTraceable() {
		return getTraceModes() != TraceModesEnum.NOT_TRACEABLE;
	}

	@Override
	public String getTraceDialogAsValues() {
		StringBuilder sb = new StringBuilder();

		if (getParentAlgorithm() != null
				&& (getParentAlgorithm() instanceof AlgoDependentList)) {
			// list = {A, B} : names for A, B
			boolean notFirst = false;
			for (GeoElement geo : elements) {
				if (notFirst) {
					sb.append(", ");
				}
				sb.append(geo.getTraceDialogAsValues());
				notFirst = true;
			}
		} else {
			// e.g. Sequence[...] : name of the list
			sb.append(super.getTraceDialogAsValues());
		}

		return sb.toString();
	}

	/*
	 * default for elements implementing NumberValue interface eg GeoSegment,
	 * GeoPolygon
	 */
	@Override
	public void addToSpreadsheetTraceList(
			ArrayList<GeoNumeric> spreadsheetTraceList) {

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (geo instanceof SpreadsheetTraceable) {
				((SpreadsheetTraceable) geo)
						.addToSpreadsheetTraceList(spreadsheetTraceList);
			}
		}

	}

	/**
	 * Performs all GeoScriptActions contained in this list
	 *
	 * @param info
	 *            evaluation flags
	 * @return number of actions that were performed
	 */
	public int performScriptActions(EvalInfo info) {
		int actions = 0;
		for (int i = 0; i < size(); i++) {
			if (get(i) instanceof GeoScriptAction) {
				if (info.isScripting()) {
					((GeoScriptAction) get(i)).perform();
				}
				actions++;
			}
			if (get(i) instanceof GeoList) {
				actions += ((GeoList) get(i)).performScriptActions(info);
			}
		}
		return actions;
	}

	/**
	 * Returns position of needle in this list or -1 when not found
	 *
	 * @param needle
	 *            geo to be found
	 * @return position of needle in this list or -1 when not found
	 */
	public int find(GeoElement needle) {
		return elements.indexOf(needle);
	}

	/**
	 * @return whether this list should be drawn as combobox
	 */
	public boolean drawAsComboBox() {
		return drawAsComboBox;
	}

	/**
	 * @param b
	 *            whether this list should be drawn as combobox
	 */
	public void setDrawAsComboBox(boolean b) {
		drawAsComboBox = b;
	}

	@Override
	public boolean isAbsoluteScreenLocateable() {
		return drawAsComboBox();
	}

	@Override
	public boolean isMoveable() {
		return drawAsComboBox();
	}

	@Override
	public boolean isAbsoluteScreenLocActive() {
		return true;
	}

	@Override
	public void setAbsoluteScreenLoc(int x, int y) {
		labelOffsetX = x;
		labelOffsetY = y;
	}

	@Override
	public int getAbsoluteScreenLocX() {
		return labelOffsetX;
	}

	@Override
	public int getAbsoluteScreenLocY() {
		return labelOffsetY;
	}

	@Override
	public void setAbsoluteScreenLocActive(boolean flag) {
		// do nothing
	}

	@Override
	public void setRealWorldLoc(double x, double y) {
		// do nothing
	}

	@Override
	public double getRealWorldLocX() {
		return 0;
	}

	@Override
	public double getRealWorldLocY() {
		return 0;
	}

	@Override
	public boolean isFurniture() {
		return drawAsComboBox();
	}

	@Override
	public ExpressionValue getListElement(int i) {
		return get(i);
	}

	/**
	 * attempts to calculate mean of the list if any non-numeric elements are
	 * found, Double.NAN will be returned
	 *
	 * @return mean or Double.NAN
	 */
	public double mean() {
		if (size() == 0) {
			return Double.NaN;
		}

		double sum = 0;
		for (int i = 0; i < size(); i++) {
			GeoElement geo = get(i);
			if (geo instanceof NumberValue) {
				sum += geo.evaluateDouble();
			} else {
				return Double.NaN;
			}
		}

		return sum / size();

	}

	/**
	 * Before creating a locus based on this GeoList as a path, this method
	 * decides whether the locus algo should be AlgoLocus or AlgoLocusList.
	 *
	 * @param locusCalling
	 *            whether this method was called by locus
	 *
	 * @return boolean true if AlgoLocusList should be used.
	 */
	public boolean shouldUseAlgoLocusList(boolean locusCalling) {
		GeoPointND[] minParArray = new GeoPoint[this.size()];
		GeoPointND[] maxParArray = new GeoPoint[this.size()];
		GeoPointND[] minParStatic = new GeoPoint[this.size()];
		GeoPointND[] maxParStatic = new GeoPoint[this.size()];

		// if there is no locus using this, the answer is not important
		if (!locusCalledAlgoLocusList && !locusCalling) {
			directionInfoArray = null;
			directionInfoOrdering = null;
			return true;
		}
		if (size() == 0) {
			return false;
		}
		directionInfoArray = new boolean[this.size()];
		directionInfoOrdering = new int[this.size()];
		shouldUseAlgoLocusList = true;
		locusCalledAlgoLocusList = true;

		int i = 0;
		for (; i < this.size(); i++) {
			directionInfoArray[i] = true; // at first this is used as helper
											// array
			directionInfoOrdering[i] = i;

			if (get(i) instanceof GeoSegment) {
				minParArray[i] = ((GeoSegment) get(i)).getStartPoint();
				maxParArray[i] = ((GeoSegment) get(i)).getEndPoint();
			} else if (get(i) instanceof GeoLine) {
				minParArray[i] = ((GeoLine) get(i)).getStartPoint();
				maxParArray[i] = ((GeoLine) get(i)).getEndPoint();
			} else if (get(i) instanceof GeoConicPart) {
				AlgoElement conicParentAlgorithm = get(i).getParentAlgorithm();
				if (conicParentAlgorithm instanceof AlgoConicPartConicPoints) {
					minParArray[i] = ((AlgoConicPartConicPoints) conicParentAlgorithm)
							.getStartPoint();
					maxParArray[i] = ((AlgoConicPartConicPoints) conicParentAlgorithm)
							.getEndPoint();
				} else if (conicParentAlgorithm instanceof AlgoConicPartCircumcircle) {
					minParArray[i] = ((AlgoConicPartCircumcircle) conicParentAlgorithm)
							.getA();
					maxParArray[i] = ((AlgoConicPartCircumcircle) conicParentAlgorithm)
							.getC();
				} else if (conicParentAlgorithm instanceof AlgoSemicircle) {
					// AlgoSemiCircle's endpoints counted in reverse order in
					// GeoConicPart
					minParArray[i] = ((AlgoSemicircle) conicParentAlgorithm)
							.getB();
					maxParArray[i] = ((AlgoSemicircle) conicParentAlgorithm)
							.getA();
				} else {
					minParArray[i] = ((GeoConicPart) get(i)).getPointParam(0);
					maxParArray[i] = ((GeoConicPart) get(i)).getPointParam(1);
				}
			} else {
				minParArray[i] = null;
				maxParArray[i] = null;
				break;
			}
			minParStatic[i] = minParArray[i];
			maxParStatic[i] = maxParArray[i];
		}

		if (i < this.size() || minParArray[this.size() - 1] == null) {
			directionInfoArray = null;
			directionInfoOrdering = null;
			return true;
		}

		// this algorithm is just for deciding if this is a "directed graph
		// circle"

		for (int j = 0; j < this.size(); j++) {
			// to get this data, at first the subpaths should be joined,
			// to have compatible directions, and then the joined thing
			// should be made compatible with the main path too

			for (i = j + 1; i < this.size(); i++) { // search, join
				if (GeoPoint.samePosition(minParArray[j], minParArray[i])) {
					minParArray[i] = maxParArray[j];
					i = 0;
					break;
				} else if (GeoPoint.samePosition(minParArray[j],
						maxParArray[i])) {
					maxParArray[i] = maxParArray[j];
					i = 0;
					break;
				} else if (GeoPoint.samePosition(maxParArray[j],
						minParArray[i])) {
					minParArray[i] = minParArray[j];
					i = 0;
					break;
				} else if (GeoPoint.samePosition(maxParArray[j],
						maxParArray[i])) {
					maxParArray[i] = minParArray[j];
					i = 0;
					break;
				}
			}

			if (i != 0 && j < this.size() - 1) {
				// there was no match, so this path is not a circle graph
				directionInfoArray = null;
				directionInfoOrdering = null;
				return true; // AlgoLocusList
			}
		}
		// otherwise everything has been reduced to one
		if (!GeoPoint.samePosition(minParArray[this.size() - 1],
				maxParArray[this.size() - 1])) {
			// this path is not a circle graph, but a line graph
			directionInfoArray = null;
			directionInfoOrdering = null;
			return true; // AlgoLocusList
		}

		// otherwise use AlgoLocus

		// now it's time to get the final contents of directionInfoArray

		// directionInfoOrdering: which index is the next
		// at first search for a minimum index to start from
		int ii = 0;
		boolean direction = true; // min-max direction is true in theory... (why
									// false in testing?)

		// starting from ii, determine the ordering
		// here we use the information that this is a "directed graph circle"
		for (int j = 0; j < this.size(); j++) {
			directionInfoOrdering[j] = ii;
			directionInfoArray[ii] = direction; // direction of ii

			// search for the thing after ii with the help of the ref tree
			for (i = 0; i < this.size(); i++) {
				if (i == ii) {
					continue; // it is not the same as itself
				}

				if (j > 0) {
					// direction)
					if (directionInfoOrdering[j - 1] == i) {
						continue;
					}
				}

				if (direction) {
					// if direction of ii is true, then use its maxParStatic
					// end to match with i
					if (GeoPoint.samePosition(maxParStatic[ii],
							minParStatic[i])) {
						ii = i;
						direction = true;
						break;
					} else if (GeoPoint.samePosition(maxParStatic[ii],
							maxParStatic[i])) {
						ii = i;
						direction = false;
						break;
					}
				} else {
					if (GeoPoint.samePosition(minParStatic[ii],
							minParStatic[i])) {
						ii = i;
						direction = true;
						break;
					} else if (GeoPoint.samePosition(minParStatic[ii],
							maxParStatic[i])) {
						ii = i;
						direction = false;
						break;
					}
				}
			}
		}

		shouldUseAlgoLocusList = false;
		return false;
	}

	@Override
	public boolean showOnAxis() {
		return showOnAxis;
	}

	/**
	 * For inequalities.
	 *
	 * @param showOnAxis
	 *            true iff should be drawn on x-Axis only
	 */
	@Override
	public void setShowOnAxis(boolean showOnAxis) {
		this.showOnAxis = showOnAxis;

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet() && (geo instanceof InequalityProperties)) {
				((InequalityProperties) geo).setShowOnAxis(showOnAxis);
			}
		}
	}

	/**
	 * @return true if this list contains a 3D geo
	 */
	public boolean containsGeoElement3D() {
		for (GeoElement geo : elements) {
			boolean contains = false;
			if (geo.isGeoList()) {
				contains = ((GeoList) geo).containsGeoElement3D();
			} else {
				contains = geo.isGeoElement3D();
			}
			if (contains) {
				return true;
			}
		}

		return false;
	}

	@Override
	final public Coords getMainDirection() {
		if (elements.size() <= closestPointIndex) {
			return Coords.VX;
		}
		return elements.get(closestPointIndex).getMainDirection();
	}

	@Override
	public boolean isLaTeXTextCommand() {
		return false;
	}

	@Override
	public void setAngleStyle(int style) {
		setAngleStyle(AngleStyle.getStyle(style));
	}

	/**
	 * Changes angle style and recomputes the value from raw. See
	 * GeoAngle.ANGLE_*
	 *
	 * @param angleStyle
	 *            clockwise, anticlockwise, (force) reflex or (force) not reflex
	 */
	@Override
	public void setAngleStyle(AngleStyle angleStyle) {
		AngleStyle newAngleStyle = angleStyle;
		if (newAngleStyle == this.angleStyle) {
			return;
		}

		this.angleStyle = newAngleStyle;
		switch (newAngleStyle) {
		// case GeoAngle.ANGLE_ISCLOCKWISE:
		// newAngleStyle = GeoAngle.ANGLE_ISCLOCKWISE;
		// break;

		case NOTREFLEX:
			newAngleStyle = AngleStyle.NOTREFLEX;
			break;

		case ISREFLEX:
			newAngleStyle = AngleStyle.ISREFLEX;
			break;

		default:
			newAngleStyle = AngleStyle.ANTICLOCKWISE;
		}

		for (GeoElement geo : elements) {
			if (!geo.isLabelSet() && (geo instanceof AngleProperties)) {
				((AngleProperties) geo).setAngleStyle(angleStyle);
			}
		}
	}

	@Override
	public AngleStyle getAngleStyle() {
		return angleStyle;
	}

	@Override
	public boolean hasOrientation() {
		return true;
	}

	/**
	 * Depending upon angleStyle, some values > pi will be changed to (2pi -
	 * value). raw_value contains the original value.
	 *
	 * @param allowReflexAngle
	 *            If true, angle is allowed to be> 180 degrees
	 *
	 */
	@Override
	final public void setAllowReflexAngle(boolean allowReflexAngle) {
		switch (angleStyle) {
		case NOTREFLEX:
			if (allowReflexAngle) {
				setAngleStyle(AngleStyle.ANTICLOCKWISE);
			}
			break;
		case ISREFLEX:
			// do nothing
			break;
		default: // ANGLE_ISANTICLOCKWISE
			if (!allowReflexAngle) {
				setAngleStyle(AngleStyle.NOTREFLEX);
			}
			break;

		}
		if (allowReflexAngle) {
			setAngleStyle(AngleStyle.ANTICLOCKWISE);
		} else {
			setAngleStyle(AngleStyle.NOTREFLEX);
		}

		for (GeoElement geo : elements) {
			if (!geo.isLabelSet() && (geo instanceof AngleProperties)) {
				((AngleProperties) geo).setAllowReflexAngle(allowReflexAngle);
			}
		}

	}

	/**
	 * Sets this angle shuld be drawn differently when right
	 *
	 * @param emphasizeRightAngle
	 *            true iff this angle shuld be drawn differently when right
	 */
	@Override
	public void setEmphasizeRightAngle(boolean emphasizeRightAngle) {
		this.emphasizeRightAngle = emphasizeRightAngle;

		for (GeoElement geo : elements) {
			if (!geo.isLabelSet() && (geo instanceof AngleProperties)) {
				((AngleProperties) geo)
						.setEmphasizeRightAngle(emphasizeRightAngle);
			}
		}

	}

	/**
	 * Forces angle to be reflex or switches it to anticlockwise
	 *
	 * @param forceReflexAngle
	 *            switch to reflex for true
	 */
	@Override
	final public void setForceReflexAngle(boolean forceReflexAngle) {
		if (forceReflexAngle) {
			setAngleStyle(AngleStyle.ISREFLEX);
		} else if (angleStyle == AngleStyle.ISREFLEX) {
			setAngleStyle(AngleStyle.ANTICLOCKWISE);
		}

		for (GeoElement geo : elements) {
			if (!geo.isLabelSet() && (geo instanceof AngleProperties)) {
				((AngleProperties) geo).setForceReflexAngle(forceReflexAngle);
			}
		}
	}

	@Override
	public void setDecorationType(int type) {
		setDecorationType(type, GeoAngle.getDecoTypes().length);

		if (elements != null) {
			for (GeoElement geo : elements) {
				if (!geo.isLabelSet()) {
					if (geo instanceof AngleProperties) {
						((AngleProperties) geo).setDecorationType(type);
					} else if (geo instanceof SegmentProperties) {
						((SegmentProperties) geo).setDecorationType(type);
					}
				}
			}
		}

	}

	/**
	 * Change the size of the arc in pixels,
	 *
	 * @param i
	 *            arc size, should be in <10,100>
	 */
	@Override
	public void setArcSize(int i) {
		arcSize = i;

		for (GeoElement geo : elements) {
			if (!geo.isLabelSet() && (geo instanceof AngleProperties)) {
				((AngleProperties) geo).setArcSize(i);
			}
		}

	}

	/**
	 * returns size of the arc in pixels
	 *
	 * @return arc size in pixels
	 */
	@Override
	public int getArcSize() {
		return arcSize;
	}

	/**
	 *
	 * @return true iff this angle should be drawn differently when 90 degrees
	 */
	@Override
	public boolean isEmphasizeRightAngle() {
		return emphasizeRightAngle;
	}

	/**
	 * @param vars
	 *            sequence variable that should be replaced by its free copy
	 */
	public void replaceChildrenByValues(GeoElement vars) {
		if (this.elementType != GeoClass.FUNCTION
				&& this.elementType != GeoClass.CURVE_CARTESIAN
				&& this.elementType != GeoClass.CURVE_CARTESIAN3D
				&& this.elementType != GeoClass.FUNCTION_NVAR
				&& this.elementType != GeoClass.SURFACECARTESIAN
				&& this.elementType != GeoClass.SURFACECARTESIAN3D
				&& this.elementType != GeoClass.LIST
				&& this.elementType != ELEMENT_TYPE_MIXED) {
			return;
		}
		for (GeoElement listElement : this.elements) {
			if (listElement instanceof CasEvaluableFunction) {
				CasEvaluableFunction f = (CasEvaluableFunction) listElement;
				f.replaceChildrenByValues(vars);
			}

			else if (listElement.isGeoList()) {
				((GeoList) listElement).replaceChildrenByValues(vars);
			}
		}

	}

	@Override
	public String getLabelDescription() {
		if (labelMode == LABEL_CAPTION) {
			return getCaption(StringTemplate.defaultTemplate);
		}

		// return label;
		// Mathieu Blossier - 2009-06-30
		return getLabel(StringTemplate.defaultTemplate);
	}

	@Override
	final public HitType getLastHitType() {
		// TODO check elements
		return HitType.ON_FILLING;
	}

	/**
	 * Add number to the end, use cache if possible. Assume all cached elements
	 * are GeoNumerics.
	 *
	 * @param value
	 *            value
	 * @param parent
	 *            parent algo
	 */
	public void addNumber(double value, AlgoElement parent) {
		GeoNumeric listElement;
		if (size() < getCacheSize()) {
			// use existing list element
			listElement = (GeoNumeric) getCached(size());
		} else {
			// create a new list element
			listElement = new GeoNumeric(cons);
			listElement.setParentAlgorithm(parent);
			listElement.setConstructionDefaults();
			listElement.setUseVisualDefaults(false);
		}

		add(listElement);
		listElement.setValue(value);
	}

	/**
	 * Add point to the end, use cache if possible. Assumes all cached elements
	 * are points.
	 *
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 * @param parent
	 *            parent algorithm of the new point
	 */
	public void addPoint(double x, double y, double z, AlgoElement parent) {
		GeoPoint listElement;
		if (size() < getCacheSize()) {
			// use existing list element
			listElement = (GeoPoint) getCached(size());
		} else {
			// create a new list element
			listElement = new GeoPoint(cons);
			listElement.setParentAlgorithm(parent);
			listElement.setConstructionDefaults();
			listElement.setUseVisualDefaults(false);
		}

		add(listElement);
		listElement.setCoords(x, y, z);
	}

	@Override
	public int getListDepth() {
		return isMatrix() ? 2 : 1;
	}

	/**
	 * @return element of the same type as other elements in this list
	 */
	public GeoElement createTemplateElement() {
		if (size() == 0) {
			if (getTypeStringForXML() != null) {
				return kernel.createGeoElement(cons, getTypeStringForXML());
			}
			// guess
			return new GeoNumeric(cons);
		}
		// list not zero length
		return get(0).copyInternal(cons);
	}

	@Override
	public boolean hasLineOpacity() {
		return true;
	}

	@Override
	public int getLineOpacity() {
		return lineOpacity;
	}

	@Override
	public void setLineOpacity(int lineOpacity) {
		this.lineOpacity = lineOpacity;

		if ((elements == null) || (elements.size() == 0)) {
			return;
		}

		for (int i = 0; i < elements.size(); i++) {
			final GeoElement geo = elements.get(i);
			if (!geo.isLabelSet()) {
				geo.setLineOpacity(lineOpacity);
			}
		}
	}

	@Override
	public ValueType getValueType() {
		return ValueType.LIST;

	}

	@Override
	public boolean hasBackgroundColor() {
		if (drawAsComboBox
				|| (this.size() > 0 && this.get(0).hasBackgroundColor())) {
			return true;
		}
		if (this.size() > 0 && !this.get(0).hasBackgroundColor()) {
			return false;
		}
		return createTemplateElement().hasBackgroundColor();
	}

	/**
	 * @param geo
	 *            element to be added
	 */
	public void addCopy(GeoElement geo) {
		if (!geo.isLabelSet()) {
			add(geo.copyInternal(cons));
		} else {
			add(geo);
		}
	}

	@Override
	public ValidExpression toValidExpression() {
		return getMyList();
	}

	@Override
	public void initSymbolicMode() {
		setSymbolicMode(true, false);
	}

	@Override
	public void setSymbolicMode(boolean mode, boolean updateParent) {
		for (int i = 0; i < this.size(); i++) {
			if (get(i) instanceof HasSymbolicMode) {
				((HasSymbolicMode) get(i)).setSymbolicMode(mode, updateParent);
			}
		}
	}

	@Override
	public boolean isSymbolicMode() {
		return size() > 0 && get(0) instanceof HasSymbolicMode
				&& ((HasSymbolicMode) get(0)).isSymbolicMode();
	}

	@Override
	public DescriptionMode getDescriptionMode() {
		if (isMatrix() && isIndependent()) {
			return DescriptionMode.VALUE;
		}
		if (!isIndependent()) {
			return DescriptionMode.DEFINITION_VALUE;
		}

		for (GeoElement geo : elements) {
			if (geo.getDescriptionMode() == DescriptionMode.DEFINITION_VALUE
					&& !Equation.isAlgebraEquation(geo)) {
				return DescriptionMode.DEFINITION_VALUE;
			}
		}

		return DescriptionMode.VALUE;
	}

	@Override
	public boolean hasSpecialEditor() {
		// Check for lists defined per element like {{1,0},{0,1}} or {1, 2, 3}
		// (also dependent like {{a+1,1}})
		if (isIndependent()) {
			return true;
		}

		if (getParentAlgorithm() instanceof AlgoDependentList) {
			AlgoElement algo = getParentAlgorithm();
			for (int i = 0; i < algo.getInputLength(); i++) {
				GeoElementND element = algo.getInput(i);
				if (!element.isIndependent() && !(element
						.getParentAlgorithm() instanceof AlgoDependentList)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Gets LaTeX string including the label for edit
	 *
	 * @param substituteNumbers
	 *            whether value should be used
	 * @param tpl
	 *            template
	 * @return string for AV editing
	 */

	@Override
	public String getLaTeXAlgebraDescriptionWithFallback(
			final boolean substituteNumbers, StringTemplate tpl,
			boolean fallback) {
		if (hasSpecialEditor()) {
			return getLabel(tpl) + " = "
					+ toLaTeXString(!substituteNumbers, tpl);
		}
		return super.getLaTeXAlgebraDescriptionWithFallback(substituteNumbers,
				tpl, fallback);
	}

	@Override
	public void resetDefinition() {
		super.resetDefinition();
		for (int i = 0; i < size(); i++) {
			this.elements.get(i).resetDefinition();
		}
	}

	@Override
	public GeoElementND doAnimationStep(double frameRate, GeoList parent) {
		if (size() > selectedIndex) {
			if (get(selectedIndex).isAnimatable()) {
				return ((Animatable) get(selectedIndex)).doAnimationStep(
						frameRate, this);
			}
		}
		return null;
	}

	@Override
	public boolean isAnimatable() {
		return false;
	}

	/**
	 * Select next or previous index based on animation props
	 */
	public void selectNext() {
		if (selectedIndex >= size() - 1 && getAnimationDirection() > 0) {
			this.changeAnimationDirection();
			return;
		}
		if (selectedIndex == 0 && getAnimationDirection() < 0) {
			this.changeAnimationDirection();
			return;
		}
		selectedIndex += this.getAnimationDirection();
	}

	/**
	 * May break dependencies, use on free lists only
	 *
	 * @param i
	 *            index
	 * @param element
	 *            new element
	 */
	public void setListElement(int i, GeoElement element) {
		this.elements.set(i, element);
		this.applyVisualStyle(element);
		// this.elementType = element.getGeoClassType();
		isDrawable = true;
		for (int idx = 0; idx < size(); idx++) {
			updateDrawableFlag(get(idx));
		}
	}

	@Override
	public GColor getBackgroundColor() {
		if (drawAsComboBox && bgColor == null) {
			return GColor.WHITE;
		}
		return bgColor;
	}

	/**
	 * Sets the total width of the geo.
	 *
	 * @param width
	 *            to set.
	 */
	public void setTotalWidth(int width) {
		totalWidth = width;
	}

	/**
	 * Sets the total height of the geo.
	 *
	 * @param height
	 *            to set.
	 */
	public void setTotalHeight(int height) {
		totalHeight = height;
	}

	@Override
	public int getTotalWidth(EuclidianViewInterfaceCommon ev) {
		return totalWidth;
	}

	@Override
	public int getTotalHeight(EuclidianViewInterfaceCommon ev) {
		return totalHeight;
	}

	/**
	 * Make sure this list can't be drawn
	 */
	public void setNotDrawable() {
		isDrawable = false;
	}

	@Override
	protected boolean mayShowDescriptionInsteadOfDefinitionNoAlgoParent() {
		return false;
	}

	public String getSelectedItemDisplayString(StringTemplate tpl) {
		return getItemDisplayString(getSelectedElement(), tpl);
	}

	/**
	 *
	 * @param geoItem
	 *            an item of the list
	 * @param tpl
	 *            template
	 * @return The displayed string of item.
	 */
	public String getItemDisplayString(GeoElement geoItem,
			StringTemplate tpl) {
		String displayString = "";
		if (!"".equals(geoItem.getRawCaption())) {
			displayString =  geoItem.getCaption(tpl);
		} else if (geoItem.isGeoPoint() || geoItem.isGeoVector() || geoItem.isGeoList()) {
			if (geoItem.getLabelSimple() == null) {
				// eg Element of list
				displayString = geoItem.toValueString(tpl);
			} else {
				displayString = geoItem.getLabel(tpl);
			}
		} else {
			displayString = geoItem.toValueString(tpl);
		}
		if (tpl.hasType(StringType.SCREEN_READER) && geoItem.isGeoText()
				&& CanvasDrawable.isLatexString(displayString)) {
			displayString = ((GeoText) geoItem).getAuralTextLaTeX();
		}

		if (StringUtil.empty(displayString)
				&& tpl.getStringType() == StringType.SCREEN_READER) {
			return kernel.getLocalization().getMenuDefault("EmptyItem", "empty element");
		}

		return displayString;
	}

	/**
	 *
	 * @param idx
	 *            Item index.
	 * @param tpl
	 *            template
	 * @return the display string of the item at idx.
	 */
	public String getItemDisplayString(int idx, StringTemplate tpl) {
		return getItemDisplayString(get(idx), tpl);
	}

	private void addAuralLabelOrCaption(ScreenReaderBuilder sb) {
		sb.append(" ");
		String caption0 = getCaptionSimple();
		sb.append(caption0 == null ? getLabelSimple() : caption0);
	}

	@Override
	public void addAuralType(ScreenReaderBuilder sb) {
		sb.appendMenuDefault("Dropdown", "dropdown");
		sb.appendSpace();
	}

	@Override
	public void addAuralContent(Localization loc, ScreenReaderBuilder sb) {
		if (size() > 0) {
			String item = getSelectedItemDisplayString(StringTemplate.screenReader);
			sb.append(loc.getPlainDefault("ElementASelected",
					"element %0 selected", item));
		}
	}

	@Override
	public void addAuralOperations(Localization loc, ScreenReaderBuilder sb) {
		sb.append(loc.getMenuDefault("PressSpaceToOpen", "Press space to open"));
		sb.appendSpace();
		super.addAuralOperations(loc, sb);
	}

	@Override
	public String getAuralTextForSpace() {
		DrawableND d = app.getEuclidianView1().getDrawableND(this);
		if (d instanceof DrawDropDownList && !((DrawDropDownList) d).isOptionsVisible()) {
			ScreenReaderBuilder sb = new ScreenReaderBuilder(kernel.getLocalization());
			appendAuralItemSelected(sb);
			sb.endSentence();
			return sb.toString();
		}
		return null;
	}

	/**
	 * @param geoItem
	 *            geo
	 * @return whether it should be painted in LaTeX
	 */
	public static boolean needsLatex(GeoElement geoItem) {
		return geoItem instanceof FunctionalNVar || geoItem.isGeoImage()
				|| (geoItem.isGeoText() && geoItem.isLaTeXDrawableGeo());
	}

	/**
	 * Appends the aural text of the currently selected element.
	 */
	public void appendAuralItemSelected(ScreenReaderBuilder sb) {
		Localization loc = kernel.getLocalization();
		addAuralContent(kernel.getLocalization(), sb);
		sb.appendSpace();
		sb.append(loc.getMenuDefault("DropdownClosed",
					"Dropdown closed"));
	}

	/**
	 * @return some aural instructions at opening the drop down.
	 *
	 */
	public String getAuralTextAsOpened() {
		Localization loc = kernel.getLocalization();
		ScreenReaderBuilder sb = new ScreenReaderBuilder(loc);
		sb.append(getSelectedItemDisplayString(StringTemplate.screenReader));
		sb.appendSpace();
		sb.append(getIndexDescription(getSelectedIndex()));
		sb.endSentence();
		sb.appendMenuDefault("PressArrowsToGo",
				"Press up arrow and down arrow to go to different options");
		sb.endSentence();
		sb.appendMenuDefault("PressEnterToSelect",
				"Press enter to select");
		return sb.toString();
	}

	/**
	 * @param selectedIndex index
	 * @return localized "[index] of [size]"
	 */
	public String getIndexDescription(int selectedIndex) {
		return kernel.getLocalization().getPlainDefault("AofB", "%0 of %1",
				(selectedIndex + 1) + "", size() + "");
	}

	/**
	 * Sets whether the the list was defined with curly brackets.
	 *
	 * @param definedWithCurlyBrackets If true, the evaluated list should be also printed
	 *                                 with curly brackets,
	 *                                 otherwise it should be printed without curly brackets.
	 */
	public void setDefinedWithCurlyBrackets(boolean definedWithCurlyBrackets) {
		this.wasDefinedWithCurlyBrackets = definedWithCurlyBrackets;
	}

	/**
	 * @return Returns true if the list was defined in the input with curly brackets,
	 * otherwise returns false.
	 */
	public boolean wasDefinedWithCurlyBrackets() {
		return wasDefinedWithCurlyBrackets;
	}

	/**
	 * @return new array with elements
	 */
	public GeoElement[] elementsAsArray() {
		return elements.toArray(new GeoElement[size()]);
	}

	@Override
	public void setNeedsUpdatedBoundingBox(boolean b) {
		//
	}

	@Override
	public void calculateCornerPoint(GeoPoint corner, int double1) {
		corner.setUndefined();
	}
}
