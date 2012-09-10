/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * GeoAngle.java
 *
 * The toString() depends on the kernels angle unit state (DEGREE or RADIANT)
 *
 * Created on 18. September 2001, 12:04
 */

package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AngleAlgo;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.plugin.GeoClass;

/**
 * 
 * @author Markus
 */
public class GeoAngle extends GeoNumeric {

	//public int arcSize = EuclidianStyleConstants.DEFAULT_ANGLE_SIZE;
	private int arcSize;

	// allow angle > pi
	// private boolean allowReflexAngle = true;

	// shows whether the current value was changed to (2pi - value)
	// private boolean changedReflexAngle;

	// states whether a special right angle appearance should be used to draw
	// this angle
	private boolean emphasizeRightAngle = true;

	// Michael Borcherds 2007-10-20
	private double rawValue;
	/** Default minimum value when displayed as slider*/
	final public static double DEFAULT_SLIDER_MIN_ANGLE = 0;
	/** Default maximum value when displayed as slider*/
	final public static double DEFAULT_SLIDER_MAX_ANGLE = Kernel.PI_2;
	/** default increment when displayed as slider */
	final public static double DEFAULT_SLIDER_INCREMENT_ANGLE = Math.PI / 180.0;
	/** Measure angle anticlockwise*/
	final public static int ANGLE_ISANTICLOCKWISE = 0; // old allowReflexAngle=true
	/** Measure angle clockwise*/
	final public static int ANGLE_ISCLOCKWISE = 1;
	/** Force angle not to be reflex */
	final public static int ANGLE_ISNOTREFLEX = 2; // old allowReflexAngle=false
	/** Force angle to be reflex */
	final public static int ANGLE_ISREFLEX = 3;

	private int angleStyle = ANGLE_ISANTICLOCKWISE;

	/**
	 * @author Lo�c
	 * @return List of decoration types.
	 */
	public static final Integer[] getDecoTypes() {
		Integer[] ret = { new Integer(GeoElement.DECORATION_NONE),
				Integer.valueOf(GeoElement.DECORATION_ANGLE_TWO_ARCS),
				Integer.valueOf(GeoElement.DECORATION_ANGLE_THREE_ARCS),
				Integer.valueOf(GeoElement.DECORATION_ANGLE_ONE_TICK),
				Integer.valueOf(GeoElement.DECORATION_ANGLE_TWO_TICKS),
				Integer.valueOf(GeoElement.DECORATION_ANGLE_THREE_TICKS),				
				Integer.valueOf(GeoElement.DECORATION_ANGLE_ARROW_ANTICLOCKWISE), 
				Integer.valueOf(GeoElement.DECORATION_ANGLE_ARROW_CLOCKWISE) 
		};
		return ret;
	}
	

	//////////////////////////////////////////
	// INTERVAL
	//////////////////////////////////////////
	/** interval minima for different angle styles */
	public static final String[] INTERVAL_MIN = {
		"0\u00b0",
		"0\u00b0",
		"180\u00b0"
	};
	/** interval maxima for different angle styles */
	public static final String[] INTERVAL_MAX = {
		"360\u00b0",
		"180\u00b0",
		"360\u00b0"
	};
	
	/** orders have to be changed both in following arrays */
	public static final int[] INTERVAL_TO_STYLE = {
		ANGLE_ISANTICLOCKWISE,
		ANGLE_ISNOTREFLEX,
		ANGLE_ISREFLEX
	};
	/** inverse of INTERVAL_TO_STYLE */
	public static final int[] STYLE_TO_INTERVAL = {
		0,//ANGLE_ISANTICLOCKWISE,
		-1,
		1,//ANGLE_ISNOTREFLEX,
		2//ANGLE_ISREFLEX		
	};
	/**
	 * @param index index of currently used interval
	 */
	public void setAngleInterval(int index){
		setAngleStyle(INTERVAL_TO_STYLE[index]);
	}
	/**
	 * @return index of currently used interval
	 */
	public int getAngleInterval(){
		return STYLE_TO_INTERVAL[getAngleStyle()];
	}
	
	/** Creates new GeoAngle 
	 * @param c Construction 
	 */
	public GeoAngle(Construction c) {
		super(c);
		
		//setAlphaValue(ConstructionDefaults.DEFAULT_ANGLE_ALPHA);
		//setLabelMode(GeoElement.LABEL_NAME);
		
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		//setEuclidianVisible(false);
	}

	/**
	 * Creates labeled angle of given size
	 * @param c Construction
	 * @param label Name for angle
	 * @param x Size of the angle
	 */
	public GeoAngle(Construction c, String label, double x) {
		this(c, x);
		setLabel(label);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.ANGLE;
	}

	/**
	 * Creates new angle of given size
	 * @param c Construction
	 * @param x Size of the angle
	 */
	public GeoAngle(Construction c, double x) {
		this(c);
		setValue(x);
	}

	@Override
	final public boolean isGeoAngle() {
		return true;
	}
	
	@Override
	final public boolean isAngle() {
		return true;
	}

	@Override
	public void set(GeoElement geo) {
		GeoNumeric num = (GeoNumeric) geo;
		setValue(num.getValue());
	}

	@Override
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);

		if (geo.isGeoAngle()) {
			GeoAngle ang = (GeoAngle) geo;
			arcSize = ang.arcSize;
			// allowReflexAngle = ang.allowReflexAngle;
			angleStyle = ang.angleStyle;
			emphasizeRightAngle = ang.emphasizeRightAngle;
		}
	}

	// Michael Borcherds 2007-10-21 BEGIN
	/**
	 * Sets the value of this angle. Every value is limited between 0 and 2pi.
	 * Under some conditions a value > pi will be changed to (2pi - value).
	 * 
	 * @see #setAngleStyle(int)
	 */
	@Override
	public void setValue(double val, boolean changeAnimationValue) {
		double angVal = calcAngleValue(val);
		super.setValue(angVal, changeAnimationValue);
	}
	
	/**
	 * Converts the val to a value between 0 and 2pi.
	 */
	private double calcAngleValue(double val) {
		// limit to [0, 2pi]
		double angVal = Kernel.convertToAngleValue(val);

		rawValue = angVal;

		// if needed: change angle
		switch (angleStyle) {
		case ANGLE_ISCLOCKWISE:
			angVal = 2.0 * Math.PI - angVal;
			break;

		case ANGLE_ISNOTREFLEX:
			if (angVal > Math.PI)
				angVal = 2.0 * Math.PI - angVal;
			break;

		case ANGLE_ISREFLEX:
			if (angVal < Math.PI)
				angVal = 2.0 * Math.PI - angVal;
			break;
		}		
		
		return angVal;
	}

	// Michael Borcherds 2007-10-21 END

	@Override
	public void setIntervalMax(double max) {
		if (max > Kernel.PI_2)
			return;
		super.setIntervalMax(max);
	}

	@Override
	public void setIntervalMin(double min) {
		if (min < 0)
			return;
		super.setIntervalMin(min);
	}

	@Override
	public void setEuclidianVisible(boolean flag) {
		if (flag && isIndependent()) {
			setLabelMode(GeoElement.LABEL_NAME_VALUE);
		}
		super.setEuclidianVisible(flag);
	}

	@Override
	public GeoElement copy() {
		GeoAngle angle = new GeoAngle(cons);
		angle.setValue(rawValue);
		angle.setAngleStyle(angleStyle);
		return angle;
	}

	// Michael Borcherds 2007-10-21 BEGIN
	/**
	 * Depending upon angleStyle, some values > pi will be changed to (2pi -
	 * value). raw_value contains the original value.
	 * @param allowReflexAngle If true, angle is allowed to be> 180 degrees
	 * 
	 * @see #setValue(double)
	 */
	final public void setAllowReflexAngle(boolean allowReflexAngle) {
		switch (angleStyle) {
		case ANGLE_ISNOTREFLEX:
			if (allowReflexAngle)
				setAngleStyle(ANGLE_ISANTICLOCKWISE);
			break;
		case ANGLE_ISREFLEX:
			// do nothing
			break;
		default: // ANGLE_ISANTICLOCKWISE
			if (!allowReflexAngle)
				setAngleStyle(ANGLE_ISNOTREFLEX);
			break;

		}
		if (allowReflexAngle)
			setAngleStyle(ANGLE_ISANTICLOCKWISE);
		else
			setAngleStyle(ANGLE_ISNOTREFLEX);
	}

	/**
	 * Forces angle to be reflex or switches it to anticlockwise
	 * @param forceReflexAngle switch to reflex for true
	 */
	final public void setForceReflexAngle(boolean forceReflexAngle) {

		if(forceReflexAngle){
			setAngleStyle(ANGLE_ISREFLEX);
		}
		else if(angleStyle == ANGLE_ISREFLEX){
			setAngleStyle(ANGLE_ISANTICLOCKWISE);
		}		
	}

	/**
	 * Changes angle style and recomputes the value from raw.
	 * See GeoAngle.ANGLE_*
	 * @param angleStyle clockwise, anticlockwise, (force) reflex or (force) not reflex
	 */
	public void setAngleStyle(int angleStyle) {
		int newAngleStyle = angleStyle;
		if (newAngleStyle == this.angleStyle)
			return;

		this.angleStyle = newAngleStyle;
		switch (newAngleStyle) {
		case ANGLE_ISCLOCKWISE:
			newAngleStyle = ANGLE_ISCLOCKWISE;
			break;

		case ANGLE_ISNOTREFLEX:
			newAngleStyle = ANGLE_ISNOTREFLEX;
			break;

		case ANGLE_ISREFLEX:
			newAngleStyle = ANGLE_ISREFLEX;
			break;

		default:
			newAngleStyle = ANGLE_ISANTICLOCKWISE;
		}
		// we have to reset the value of this angle
		if (algoParent == null) {
			// setValue(value);
			setValue(rawValue);
		} else {
			algoParent.update();
		}
	}
	
	/**
	 * Returns angle style. See GeoAngle.ANGLE_*
	 * 
	 * @return Clockwise, counterclockwise reflex or not reflex
	 */
	public int getAngleStyle() {
		return angleStyle;
	}
	
	/**
	 * 
	 * @return true if has a "super" orientation (e.g. in 3D, from a specific oriented plane)
	 */
	public boolean hasOrientation(){
		return true; //orientation of xOyPlane
	}

	/**
	 * Returns the raw value of angle
	 * @return raw value of angle (irrespective of angle style)
	 */
	final public double getRawAngle() {
		return rawValue;
	}

	// Michael Borcherds 2007-10-21 END	

	@Override
	final public String toValueString(StringTemplate tpl) {
		return isEuclidianVisible() ? kernel.formatAngle(value, 1/getAnimationStep(),tpl).toString() : 
			kernel.formatAngle(value,tpl).toString();
	}

	// overwrite
	@Override
	final public MyDouble getNumber() {
		MyDouble ret = new MyDouble(kernel, value);
		ret.setAngle();
		return ret;
	}

	/** 
	 * returns size of the arc in pixels
	 * @return arc size in pixels
	 */
	public int getArcSize() {
		return arcSize;
	}

	/** 
	 * Change the size of the arc in pixels, 
	 * @param i arc size, should be in <10,100>
	 */
	public void setArcSize(int i) {
		arcSize = i;
	}

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {

		sb.append("\t<value val=\"");
		sb.append(rawValue);
		sb.append("\"");
		if (isRandom()) {
			sb.append(" random=\"true\"");
		}
		sb.append("/>\n");

		// if angle is drawable then we need to save visual options too
		if (isDrawable() || isSliderable()) {
			// save slider info before show to have min and max set
			// before setEuclidianVisible(true) is called
			getXMLsliderTag(sb);

			getXMLvisualTags(sb);
			getLineStyleXML(sb);

			// arc size
			sb.append("\t<arcSize val=\"");
			sb.append(arcSize);
			sb.append("\"/>\n");
		}
		getXMLAllowReflexAngleTag(sb);
		getXMLEmphasizeRightAngleTag(sb);		
		getXMLanimationTags(sb);
		getXMLfixedTag(sb);
		getAuxiliaryXML(sb);
		getBreakpointXML(sb);
		getScriptTags(sb);
	}

	/**
	 * returns some class-specific xml tags for getConstructionRegressionOut
	 */
    @Override
	public void getXMLtagsMinimal(StringBuilder sb,StringTemplate tpl) {
    	sb.append(regrFormat(rawValue));
    	if (isDrawable() || isSliderable()) {
    		sb.append(" " + regrFormat(arcSize));
    	}
    }
	
	private void getXMLAllowReflexAngleTag(StringBuilder sb) {
		if (isIndependent())
			return;

		// Michael Borcherds 2007-10-21
		sb.append("\t<allowReflexAngle val=\"");
		sb.append(angleStyle != ANGLE_ISNOTREFLEX);
		sb.append("\"/>\n");
		if (angleStyle == ANGLE_ISREFLEX) {
			sb.append("\t<forceReflexAngle val=\"");
			sb.append(true);
			sb.append("\"/>\n");
		}

		// sb.append("\t<angleStyle val=\"");
		// sb.append(angleStyle);
		// sb.append("\"/>\n");
		// Michael Borcherds 2007-10-21
	}
	
	private void getXMLEmphasizeRightAngleTag(StringBuilder sb) {
		if (emphasizeRightAngle) 
			return;
		
		// only store emphasizeRightAngle if "false"
		sb.append("\t<emphasizeRightAngle val=\"");
		sb.append(emphasizeRightAngle);
		sb.append("\"/>\n");		
	}

	// Michael Borcherds 2007-11-20
	@Override
	public void setDecorationType(int type) {
		if (type >= getDecoTypes().length || type < 0)
			decorationType = DECORATION_NONE;
		else
			decorationType = type;
	}

	// Michael Borcherds 2007-11-20

	/**
	 * Returns true if this angle shuld be drawn differently when right
	 * @return true iff this angle shuld be drawn differently when right
	 */
	public boolean isEmphasizeRightAngle() {
		return emphasizeRightAngle;
	}

	/**
	 * Sets this angle shuld be drawn differently when right
	 * @param emphasizeRightAngle true iff this angle shuld be drawn differently when right
	 */
	public void setEmphasizeRightAngle(boolean emphasizeRightAngle) {
		this.emphasizeRightAngle = emphasizeRightAngle;
	}

	@Override
	public void setZero() {
		rawValue = 0;
	}
	@Override
	public boolean isDrawable() {		
		return isDrawable || (getDrawAlgorithm()!=getParentAlgorithm()) || (isIndependent() && isLabelSet()
				|| getParentAlgorithm() instanceof AngleAlgo);		
	}
	
	@Override
	public boolean hasDrawable3D(){
		return true;
	}	
	
	@Override
	public boolean canHaveClickScript() {
		return isDrawable();
	}
	


}