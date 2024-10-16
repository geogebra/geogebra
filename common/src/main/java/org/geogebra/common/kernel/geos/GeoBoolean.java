/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package org.geogebra.common.kernel.geos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.MyBoolean;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.StringUtil;

/**
 * 
 * @author Markus
 */
public class GeoBoolean extends GeoElement implements BooleanValue,
		GeoNumberValue, AbsoluteScreenLocateable, HasExtendedAV, Locateable {

	private boolean value = false;
	private boolean isDefined = true;
	private boolean checkboxFixed;
	private boolean showExtendedAV = true;

	private List<GeoElement> conditionals;
	private GeoPointND startPoint;

	/**
	 * Creates new boolean
	 * 
	 * @param c
	 *            construction
	 */
	public GeoBoolean(Construction c) {
		super(c);
		checkboxFixed = true;
		setEuclidianVisible(false);
		conditionals = new ArrayList<>();
		setConstructionDefaults();
	}

	/**
	 * Creates new boolean
	 * 
	 * @param cons
	 *            construction
	 * @param value
	 *            value
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
	 * 
	 * @param val
	 *            new value
	 */
	public void setValue(boolean val) {
		setDefinition(null);
		value = val;
	}

	@Override
	final public boolean getBoolean() {
		return value;
	}

	@Override
	final public MyBoolean getMyBoolean() {
		return new MyBoolean(kernel, value);
	}

	@Override
	public GeoBoolean copy() {
		GeoBoolean ret = new GeoBoolean(cons);
		ret.setValue(value);
		return ret;
	}

	/**
	 * Registers geo as a listener for updates of this boolean object. If this
	 * object is updated it calls geo.updateConditions()
	 * 
	 * @param geo
	 *            geo which should use this boolean as condition to show
	 */
	public void registerConditionListener(GeoElement geo) {
		ArrayList<GeoElement> newConditionals = new ArrayList<>(conditionals);
		newConditionals.add(geo);
		conditionals = newConditionals;
	}

	/**
	 * Unregisters geo as a listener for updates of this boolean object.
	 * 
	 * @param geo
	 *            geo which uses this boolean as condition to show
	 */
	public void unregisterConditionListener(GeoElement geo) {
		ArrayList<GeoElement> newConditionals = new ArrayList<>(conditionals);
		newConditionals.remove(geo);
		conditionals = newConditionals;
	}

	/**
	 * Calls super.update() and update() for all registered condition listener
	 * geos.
	 */
	@Override
	public void update(boolean drag) {
		super.update(drag);

		// update all registered conditionals (they have this boolean as condition to show object)
		for (GeoElement geo: conditionals) {
			geo.notifyUpdate();
		}
	}

	/**
	 * Tells condition listeners that their condition is removed and calls
	 * super.remove()
	 */
	@Override
	public void doRemove() {
		List<GeoElement> conditionalsCopy = new ArrayList<>(conditionals);
		conditionals.clear();

		for (GeoElement geo : conditionalsCopy) {
			geo.removeCondition(this);
			kernel.notifyUpdate(geo);
		}

		super.doRemove();
	}

	@Override
	public boolean showInEuclidianView() {
		return isIndependent() && isSimple();
	}

	@Override
	public boolean isFixable() {
		// visible checkbox should not be fixable
		return isIndependent() && !isSetEuclidianVisible()
				&& this.condShowObject == null && !isDefaultGeo();
	}

	@Override
	public boolean showFixUnfix() {
		return false;
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo.isGeoNumeric()) { // eg SetValue[checkbox, 0]
			// 1 = true
			// 0 = false
			setValue(DoubleUtil.isZero(((GeoNumeric) geo).getDouble() - 1));
			isDefined = true;
		} else {
			GeoBoolean b = (GeoBoolean) geo;
			setValue(b.value);
			isDefined = b.isDefined;
		}
	}

	/**
	 * Changes value to false. See also GeoBoolean.setUndefinedProverOnly()
	 */
	@Override
	final public void setUndefined() {
		// don't change this, needed for compatibility
		// eg SetValue[a,?] sets it to false
		value = false;
	}

	/**
	 * Set the undefined flag. Normal algos should use setUndefined which
	 * changes value to false.
	 */
	final public void setUndefinedProverOnly() {
		// Needed for prover's yes/no/undefined trichotomy
		// Reset value => boolean arithmetic is predictable without checking for ? everywhere
		isDefined = false;
		value = false;
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
		if (!isDefined) {
			return "?";
		}
		return value ? "true" : "false";
	}

	@Override
	final public String toString(StringTemplate tpl) {
		StringBuilder sbToString = new StringBuilder();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(tpl.getEqualsWithSpace());
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

		getStyleXML(sb);
	}

	@Override
	protected void getStyleXML(StringBuilder sb) {
		XMLBuilder.getXMLvisualTags(this, sb, isIndependent());
		getXMLfixedTag(sb);
		getAuxiliaryXML(sb);
		getBreakpointXML(sb);
		// checkbox fixed
		if (checkboxFixed) {
			sb.append("\t<checkbox fixed=\"true\"/>\n");
		}
		getScriptTags(sb);
		getCaptionXML(sb);
		if (startPoint != null) {
			startPoint.appendStartPointXML(sb, isAbsoluteScreenLocActive());
		}
	}

	@Override
	public boolean isGeoBoolean() {
		return true;
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
	public boolean isAbsoluteScreenLocActive() {
		return true;
	}

	@Override
	public boolean isAbsoluteScreenLocateable() {
		return isIndependent();
	}

	@Override
	public void setAbsoluteScreenLoc(int x, int y) {
		labelOffsetX = x;
		labelOffsetY = y;
	}

	@Override
	public int getAbsoluteScreenLocX() {
		return startPoint == null ? labelOffsetX : (int) startPoint.getInhomX();
	}

	@Override
	public int getAbsoluteScreenLocY() {
		return startPoint == null ? labelOffsetY : (int) startPoint.getInhomY();
	}

	@Override
	public void setAbsoluteScreenLocActive(boolean flag) {
		// do nothing
	}

	@Override
	public void setRealWorldLoc(double x, double y) {
		// do nothing
	}

	/**
	 * @return true for fixed checkboxes
	 */
	@Override
	public final boolean isLockedPosition() {
		return checkboxFixed;
	}

	/**
	 * @param checkboxFixed
	 *            true to fix checkbox
	 */
	public final void setCheckboxFixed(boolean checkboxFixed) {
		this.checkboxFixed = checkboxFixed;
	}

	// Michael Borcherds 2008-04-30
	@Override
	final public ExtendedBoolean isEqualExtended(GeoElementND geo) {
		// return false if it's a different type, otherwise check
		return ExtendedBoolean.newExtendedBoolean(geo.isGeoBoolean()
				&& value == ((GeoBoolean) geo).getBoolean());
	}

	@Override
	public boolean isNumberValue() {
		return true;
	}

	/**
	 * Returns 1 for true and 0 for false.
	 */
	@Override
	public double getDouble() {
		return value ? 1 : 0;
	}

	@Override
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
		if (!oldGeo.isGeoBoolean()) {
			return;
		}

		GeoBoolean geoBoolean = (GeoBoolean) oldGeo;
		conditionals.clear();
		conditionals.addAll(geoBoolean.conditionals);
	}

	@Override
	public void setEuclidianVisible(boolean visible) {
		if (visible && labelOffsetX == 0 && labelOffsetY == 0
				&& isIndependent()) {
			initScreenLocation();
		}
		super.setEuclidianVisible(visible);
	}

	private void initScreenLocation() {
		int count = countCheckboxes();
		labelOffsetX = 5;
		EuclidianViewInterfaceSlim ev = kernel.getApplication()
				.getActiveEuclidianView();
		if (ev != null) {
			labelOffsetY = ev.getSliderOffsetY() - 45 + 30 * count;
		} else {
			labelOffsetY = 5 + 30 * count;
		}
		// make sure checkbox is visible on screen
		labelOffsetY = labelOffsetY / 400 * 10 + labelOffsetY % 400;
	}

	private int countCheckboxes() {
		int count = 0;

		// get all number and angle sliders
		TreeSet<GeoElement> bools = cons.getGeoSetLabelOrder(GeoClass.BOOLEAN);

		for (GeoElement bool : bools) {
			GeoBoolean num = (GeoBoolean) bool;
			if (num.isIndependent() && num.isEuclidianVisible()) {
				count++;
			}
		}

		return count;
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public boolean isShowingExtendedAV() {
		return showExtendedAV;
	}

	@Override
	public void setShowExtendedAV(boolean showExtendedAV) {
		this.showExtendedAV = showExtendedAV;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.BOOLEAN;
	}

	@Override
	public ValidExpression toValidExpression() {
		return getMyBoolean();
	}

	@Override
	public int getTotalWidth(EuclidianViewInterfaceCommon ev) {
		return (int) (32 + StringUtil.getPrototype().estimateLength(
				getCaption(StringTemplate.defaultTemplate),
				ev.getApplication().getFontCanDisplay(label)));
	}

	@Override
	public boolean isFurniture() {
		return false;
	}

	@Override
	public int getTotalHeight(EuclidianViewInterfaceCommon ev) {
		return 32;
	}

	@Override
	public void addAuralType(ScreenReaderBuilder sb) {
		sb.appendMenuDefault("Checkbox", "Checkbox");
		sb.appendSpace();
	}

	@Override
	public void addAuralStatus(Localization loc, ScreenReaderBuilder sb) {
		if (sb.isMobile()) {
			sb.append(getAuralCheckboxStatus(sb));
		}
	}

	@Override
	public void addAuralOperations(Localization loc, ScreenReaderBuilder sb) {
		if (sb.isMobile()) {
			return;
		}

		if (getBoolean()) {
			sb.append(
					loc.getMenuDefault("PressSpaceCheckboxOff", "Press space to uncheck checkbox"));
		} else {
			sb.append(loc.getMenuDefault("PressSpaceCheckboxOn", "Press space to check checkbox"));
		}
		sb.endSentence();
		super.addAuralOperations(loc, sb);
	}

	@Override
	public String getAuralTextForSpace() {
		Localization loc = kernel.getLocalization();
		ScreenReaderBuilder sb = new ScreenReaderBuilder(loc);
		return getAuralCheckboxStatus(sb);
	}

	private String getAuralCheckboxStatus(ScreenReaderBuilder sb) {
		addAuralName(sb);
		if (getBoolean()) {
			sb.appendMenuDefault("Checked", "checked");
		} else {
			sb.appendMenuDefault("Unchecked", "unchecked");

		}
		sb.append(".");
		return sb.toString();
	}

	@Override
	public void setStartPoint(GeoPointND p) throws CircularDefinitionException {
		if (startPoint != null) {
			startPoint.getLocateableList().unregisterLocateable(this);
		}

		// set new location
		if (p == null) {
			if (startPoint != null) {
				startPoint = startPoint.copy();
			}

			labelOffsetX = 0;
			labelOffsetY = 0;
		} else {
			startPoint = p;

			// add new dependencies
			startPoint.getLocateableList().registerLocateable(this);
		}
	}

	@Override
	public GeoPointND getStartPoint() {
		return startPoint;
	}

	@Override
	public void setStartPoint(GeoPointND p, int number) throws CircularDefinitionException {
		setStartPoint(p);
	}

	@Override
	public void initStartPoint(GeoPointND p, int number) {
		this.startPoint = p;
	}

	@Override
	public boolean hasStaticLocation() {
		return false;
	}

	@Override
	public boolean isAlwaysFixed() {
		return false;
	}

	@Override
	public void updateLocation() {
		update();
	}

	@Override
	public BigDecimal toDecimal() {
		return value ? BigDecimal.ONE : BigDecimal.ZERO;
	}
}