/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.common.kernel.geos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.MyBoolean;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.plugin.GeoClass;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 
 * @author Markus
 */
public class GeoBoolean extends GeoElement implements BooleanValue,
		GeoNumberValue, AbsoluteScreenLocateable {

	private boolean value = false;
	private boolean isDefined = true;
	private boolean checkboxFixed = false;

	private ArrayList<GeoElement> condListenersShowObject;

	/**
	 * Creates new boolean
	 * @param c construction
	 */
	public GeoBoolean(Construction c) {
		super(c);
		setEuclidianVisible(false);
	}

	/**
	 * Creates new boolean
	 * @param cons construction
	 * @param value value
	 */
	public GeoBoolean(Construction cons, boolean value) {
		this(cons);
		this.value = value;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.BOOLEAN;
	}

	/**
	 * Changes value of this boolean
	 * @param val new value
	 */
	public void setValue(boolean val) {
		value = val;
	}

	final public boolean getBoolean() {
		return value;
	}

	final public MyBoolean getMyBoolean() {
		return new MyBoolean(kernel, value);
	}

	@Override
	public GeoElement copy() {
		GeoBoolean ret = new GeoBoolean(cons);
		ret.setValue(value);
		return ret;
	}

	/**
	 * Registers geo as a listener for updates of this boolean object. If this
	 * object is updated it calls geo.updateConditions()
	 * @param geo geo which should use this boolean as condition to show
	 */
	public void registerConditionListener(GeoElement geo) {
		if (condListenersShowObject == null)
			condListenersShowObject = new ArrayList<GeoElement>();
		condListenersShowObject.add(geo);
	}
	/**
	 * Unregisters geo as a listener for updates of this boolean object.
	 * @param geo geo which uses this boolean as condition to show
	 */
	public void unregisterConditionListener(GeoElement geo) {
		if (condListenersShowObject != null) {
			condListenersShowObject.remove(geo);
		}
	}

	/**
	 * Calls super.update() and update() for all registered condition listener
	 * geos.
	 */
	@Override
	public void update() {
		super.update();

		// update all registered locatables (they have this point as start
		// point)
		if (condListenersShowObject != null) {
			for (int i = 0; i < condListenersShowObject.size(); i++) {
				GeoElement geo = condListenersShowObject.get(i);
				kernel.notifyUpdate(geo);
			}
		}
	}

	/**
	 * Tells conidition listeners that their condition is removed and calls
	 * super.remove()
	 */
	@Override
	public void doRemove() {
		if (condListenersShowObject != null) {
			// copy conditionListeners into array
			Object[] geos = condListenersShowObject.toArray();
			condListenersShowObject.clear();

			// tell all condition listeners
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				geo.removeCondition(this);
				kernel.notifyUpdate(geo);
			}
		}

		super.doRemove();
	}

	@Override
	public void resolveVariables(boolean forEquation) {
		//do nothing
	}

	@Override
	public boolean showInEuclidianView() {
		return isIndependent();
	}

	private static int lastLocY = 5;

	/**
	 * Set initial absolue screen location
	 */
	public void initLocation() {
		setAbsoluteScreenLoc(5, lastLocY);
		lastLocY += 30;
	}

	@Override
	public final boolean showInAlgebraView() {
		return true;
	}

	@Override
	public boolean isFixable() {
		// visible checkbox should not be fixable
		return isIndependent() && !isSetEuclidianVisible();
	}

	@Override
	public void set(GeoElement geo) {
		if (geo.isGeoNumeric()) { // eg SetValue[checkbox, 0]
			// 1 = true
			// 0 = false
			setValue(Kernel.isZero(((GeoNumeric) geo).getDouble() - 1));
			isDefined = true;
		} else {
			GeoBoolean b = (GeoBoolean) geo;
			setValue(b.value);
			isDefined = b.isDefined;
		}
	}

	@Override
	final public void setUndefined() {
		isDefined = false;
	}

	/**
	 * Changes the defined flag of this boolean
	 */
	final public void setDefined() {
		isDefined = true;
	}

	@Override
	final public boolean isDefined() {
		return isDefined;
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		switch (tpl.getStringType()) {
		case MATH_PIPER:
			return value ? "True" : "False";

		default:
			return value ? "true" : "false";
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		StringBuilder sbToString = new StringBuilder();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(toValueString(tpl));
		return sbToString.toString();
	}


	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		sb.append("\t<value val=\"");
		sb.append(value);
		sb.append("\"/>\n");

		getXMLvisualTags(sb, isIndependent());
		getXMLfixedTag(sb);
		getAuxiliaryXML(sb);

		// checkbox fixed
		if (checkboxFixed) {
			sb.append("\t<checkbox fixed=\"");
			sb.append(checkboxFixed);
			sb.append("\"/>\n");
		}
		getScriptTags(sb);
	}

	@Override
	public boolean isBooleanValue() {
		return true;
	}

	@Override
	public boolean isGeoBoolean() {
		return true;
	}

	@Override
	public boolean isVectorValue() {
		return false;
	}

	@Override
	public boolean isPolynomialInstance() {
		return false;
	}

	@Override
	public boolean isTextValue() {
		return false;
	}

	public double getRealWorldLocX() {
		return 0;
	}

	public double getRealWorldLocY() {
		return 0;
	}

	public boolean isAbsoluteScreenLocActive() {
		return true;
	}

	@Override
	public boolean isAbsoluteScreenLocateable() {
		return isIndependent();
	}

	public void setAbsoluteScreenLoc(int x, int y) {
		if (checkboxFixed)
			return;

		labelOffsetX = x;
		labelOffsetY = y;
	}

	public int getAbsoluteScreenLocX() {
		return labelOffsetX;
	}

	public int getAbsoluteScreenLocY() {
		return labelOffsetY;
	}

	public void setAbsoluteScreenLocActive(boolean flag) {
		//do nothing
	}

	public void setRealWorldLoc(double x, double y) {
		//do nothing
	}
	/**
	 * @return true for fixed checkboxes
	 */
	public final boolean isCheckboxFixed() {
		return checkboxFixed;
	}
	/**
	 * @param checkboxFixed true to fix checkbox
	 */
	public final void setCheckboxFixed(boolean checkboxFixed) {
		this.checkboxFixed = checkboxFixed;
	}

	// Michael Borcherds 2008-04-30
	@Override
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type, otherwise check
		if (geo.isGeoBoolean()) {
			return value == ((GeoBoolean) geo).getBoolean();
		}
		return false;
	}

	@Override
	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNumberValue() {
		return true;
	}

	/**
	 * Returns 1 for true and 0 for false.
	 */
	public double getDouble() {
		return value ? 1 : 0;
	}

	public MyDouble getNumber() {
		return new MyDouble(kernel, getDouble());
	}

	/**
	 * Returns whether the value (e.g. equation) should be shown as part of the
	 * label description
	 */
	@Override
	final public boolean isLabelValueShowable() {
		return false;
	}

	@Override
	public boolean canHaveClickScript() {
		return false;
	}

	@Override
	final public boolean isCasEvaluableObject() {
		return true;
	}

	@Override
	public void moveDependencies(GeoElement oldGeo) {
		if (oldGeo.isGeoBoolean()
				&& ((GeoBoolean) oldGeo).condListenersShowObject != null) {

			condListenersShowObject = ((GeoBoolean) oldGeo).condListenersShowObject;
			for (GeoElement geo : condListenersShowObject)
				geo.condShowObject = this;

			((GeoBoolean) oldGeo).condListenersShowObject = null;
		}
	}
	
}