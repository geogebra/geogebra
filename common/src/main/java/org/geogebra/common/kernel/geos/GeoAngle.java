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

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoAngle;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * 
 * @author Markus
 */
public class GeoAngle extends GeoNumeric implements AngleProperties {

	private int arcSize;

	// states whether a special right angle appearance should be used to draw
	// this angle
	private boolean emphasizeRightAngle = true;

	private double rawValue;
	/** Default minimum value when displayed as slider */
	final public static double DEFAULT_SLIDER_MIN_ANGLE = 0;
	/** Default maximum value when displayed as slider */
	final public static double DEFAULT_SLIDER_MAX_ANGLE = Kernel.PI_2;
	/** Default increment when displayed as slider */
	final public static double DEFAULT_SLIDER_INCREMENT_ANGLE = Math.PI / 180.0;

	private boolean keepDegrees = false;

	/**
	 * different angle styles
	 *
	 */
	public enum AngleStyle {
		/**
		 * (old allowReflexAngle=true)
		 */
		ANTICLOCKWISE(0),
		/**
		 * Force angle not to be reflex ie [0,180] (old allowReflexAngle=true)
		 */
		NOTREFLEX(1),
		/**
		 * Force angle to be reflex ie [180,360]
		 */
		ISREFLEX(2),
		/**
		 * allow angles to be in the range (-infinity, infinity)
		 * 
		 * only for Angles which aren't drawable
		 */
		UNBOUNDED(3);

		private final int xmlVal;

		/**
		 * @return number for this style in XML
		 */
		public int getXmlVal() {
			return xmlVal;
		}

		AngleStyle(int xmlVal) {
			this.xmlVal = xmlVal;
		}

		/**
		 * @param style
		 *            integer from XML
		 * @return Enum
		 */
		public static AngleStyle getStyle(int style) {
			for (AngleStyle l : AngleStyle.values()) {
				if (l.xmlVal == style) {
					return l;
				}
			}

			return AngleStyle.ANTICLOCKWISE;
		}
	}

	private AngleStyle angleStyle = AngleStyle.ANTICLOCKWISE;

	/** interval minima for different angle styles */
	private static final String[] INTERVAL_MIN = {
			"0" + Unicode.DEGREE_CHAR,
			"0" + Unicode.DEGREE_CHAR,
			"180" + Unicode.DEGREE_CHAR,
			"-" + Unicode.INFINITY
	};
	/** interval maxima for different angle styles */
	private static final String[] INTERVAL_MAX = {
			"360" + Unicode.DEGREE_CHAR,
			"180" + Unicode.DEGREE_CHAR,
			"360" + Unicode.DEGREE_CHAR,
			"" + Unicode.INFINITY
	};

	/**
	 * @author Loic
	 * @return List of decoration types.
	 */
	public static Integer[] getDecoTypes() {
		return new Integer[] {
				DECORATION_NONE,
				DECORATION_ANGLE_TWO_ARCS,
				DECORATION_ANGLE_THREE_ARCS,
				DECORATION_ANGLE_ONE_TICK,
				DECORATION_ANGLE_TWO_TICKS,
				DECORATION_ANGLE_THREE_TICKS,
				DECORATION_ANGLE_ARROW_ANTICLOCKWISE,
				DECORATION_ANGLE_ARROW_CLOCKWISE
		};
	}

	/**
	 * @param i
	 *            index
	 * @return i-th interval maximum
	 */
	public static String getIntervalMinList(int i) {
		return INTERVAL_MIN[i];
	}

	/**
	 * @return number of min/max intervals
	 */
	public static int getIntervalMinListLength() {
		return INTERVAL_MIN.length;
	}

	/**
	 * @param i
	 *            index
	 * @return i-th interval minimum
	 */
	public static String getIntervalMaxList(int i) {
		return INTERVAL_MAX[i];
	}

	/**
	 * Creates new GeoAngle
	 * 
	 * @param c
	 *            Construction
	 */
	public GeoAngle(Construction c) {
		super(c);
		// setAlphaValue(ConstructionDefaults.DEFAULT_ANGLE_ALPHA);
		// setLabelMode(GeoElement.LABEL_NAME);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		// setEuclidianVisible(false);
	}

	@Override
	public void setAllVisualPropertiesExceptEuclidianVisible(GeoElement geo,
			boolean keepAdvanced, boolean setAuxiliaryProperty) {
		super.setAllVisualPropertiesExceptEuclidianVisible(geo, keepAdvanced, setAuxiliaryProperty);

		if (geo.isGeoAngle()) {
			setAngleStyle(((GeoAngle) geo).getAngleStyle());
		}
	}

	/**
	 * Creates labeled angle of given size
	 * 
	 * @param c
	 *            Construction
	 * @param label
	 *            Name for angle
	 * @param x
	 *            Size of the angle
	 */
	public GeoAngle(Construction c, String label, double x) {
		this(c, x);
		setLabel(label);
	}

	/**
	 * Creates labeled angle of given size
	 * 
	 * @param c
	 *            Construction
	 * @param x
	 *            Size of the angle
	 * @param style
	 *            eg UNBOUNDED
	 */
	public GeoAngle(Construction c, double x, AngleStyle style) {
		this(c);

		// must set style before value
		setAngleStyle(style);
		setValue(x);
	}

	/**
	 * Creates labeled angle of given size
	 *
	 * @param c
	 *            Construction
	 * @param x
	 *            Size of the angle
	 * @param style
	 *            eg UNBOUNDED
	 * @param keepDegrees
	 *            keep degrees
	 */
	public GeoAngle(Construction c, double x, AngleStyle style, boolean keepDegrees) {
		this(c);

		this.keepDegrees = keepDegrees;
		// must set style before value
		setAngleStyle(style);
		setValue(x);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.ANGLE;
	}

	/**
	 * Creates new angle of given size
	 * 
	 * @param c
	 *            Construction
	 * @param x
	 *            Size of the angle
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
	final public int getAngleDim() {
		return 1;
	}

	@Override
	public void set(GeoElementND geo) {
		GeoNumberValue num = (GeoNumberValue) geo;
		setValue(num.isGeoAngle() ? ((GeoAngle) num).getRawAngle()
				: num.getDouble());
	}

	@Override
	public void setVisualStyle(GeoElement geo, boolean setAuxiliaryProperty) {
		super.setVisualStyle(geo, setAuxiliaryProperty);

		if (geo.isGeoAngle()) {
			GeoAngle ang = (GeoAngle) geo;
			arcSize = ang.arcSize;
			if (!ang.isIndependent() || this.isDefaultGeo()) { // avoids also
																// default angle
																// to apply its
																// style (angle
																// interval)
				// to all new angles (e.g. independent angles)
				setAngleStyle(ang.angleStyle); // to update the value
			}
			emphasizeRightAngle = ang.emphasizeRightAngle;
		}
	}

	/**
	 * Sets the value of this angle. Every value is limited between 0 and 2pi.
	 * Under some conditions a value > pi will be changed to (2pi - value).
	 * 
	 * @see #setAngleStyle(int)
	 */
	@Override
	public synchronized void setValue(double val,
			boolean changeAnimationValue) {
		double angVal = calcAngleValue(val);
		super.setValue(angVal, changeAnimationValue);
	}

	/**
	 * Converts the val to a value between 0 and 2pi.
	 */
	private double calcAngleValue(double val) {

		// limit to [0, 2pi]
		double angVal;

		if (angleStyle != AngleStyle.UNBOUNDED) {
			angVal = DoubleUtil.convertToAngleValue(val);
		} else {
			angVal = val;
		}

		rawValue = angVal;

		// if needed: change angle
		switch (angleStyle) {
		case NOTREFLEX:
			if (angVal > Math.PI) {
				angVal = 2.0 * Math.PI - angVal;
			}
			break;

		case ISREFLEX:
			if (angVal < Math.PI) {
				angVal = 2.0 * Math.PI - angVal;
			}
			break;

		default:
			break;
		}

		return angVal;
	}

	@Override
	public GeoAngle copy() {
		GeoAngle angle = new GeoAngle(cons);
		angle.setValue(rawValue);
		angle.setAngleStyle(angleStyle);
		angle.setDrawable(isDrawable, false);
		return angle;
	}

	/**
	 * Depending upon angleStyle, some values > pi will be changed to (2pi -
	 * value). raw_value contains the original value.
	 * 
	 * @param allowReflexAngle
	 *            If true, angle is allowed to be> 180 degrees
	 * 
	 * @see #setValue(double)
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
		if (angleStyle == this.angleStyle) {
			return;
		}

		this.angleStyle = angleStyle;

		// we have to reset the value of this angle
		if (algoParent == null) {
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
	@Override
	public AngleStyle getAngleStyle() {
		return angleStyle;
	}

	/**
	 * 
	 * @return true if has a "super" orientation (e.g. in 3D, from a specific
	 *         oriented plane)
	 */
	@Override
	public boolean hasOrientation() {
		return true; // orientation of xOyPlane
	}

	/**
	 * Returns the raw value of angle
	 * 
	 * @return raw value of angle (irrespective of angle style)
	 */
	final public double getRawAngle() {
		return rawValue;
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		if (isEuclidianVisible()) {
			return kernel.formatAngle(value, 1 / getAnimationStep(), tpl,
					angleStyle == AngleStyle.UNBOUNDED, keepDegrees).toString();
		}
		return kernel
				.formatAngle(value, tpl, angleStyle == AngleStyle.UNBOUNDED,
						keepDegrees).toString();
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
	 * 
	 * @return arc size in pixels
	 */
	@Override
	public int getArcSize() {
		return arcSize;
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
	}

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {

		// from ggb44 need to save before value in case it's unbounded
		XMLBuilder.appendAngleStyle(sb, angleStyle, emphasizeRightAngle);

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
		} else if (GeoElementSpreadsheet.isSpreadsheetLabel(label)) {
			// make sure colors saved for spreadsheet objects
			appendObjectColorXML(sb);
		}

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
	public void getXMLtagsMinimal(StringBuilder sb, StringTemplate tpl) {
		sb.append(regrFormat(rawValue));
		if (isDrawable() || isSliderable()) {
			sb.append(" ");
			sb.append(regrFormat(arcSize));
		}
	}

	@Override
	public void setDecorationType(int type) {
		setDecorationType(type, getDecoTypes().length);
	}

	/**
	 * Returns true if this angle shuld be drawn differently when right
	 * 
	 * @return true iff this angle shuld be drawn differently when right
	 */
	@Override
	public boolean isEmphasizeRightAngle() {
		return emphasizeRightAngle;
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
	}

	@Override
	public void setZero() {
		rawValue = 0;
	}

	@Override
	public boolean isDrawable() {
		return isDrawable || (getDrawAlgorithm() != getParentAlgorithm())
				|| (isIndependent() && isLabelSet()
						|| getParentAlgorithm() instanceof AlgoAngle);
	}

	@Override
	public boolean hasDrawable3D() {
		return true;
	}

	@Override
	public boolean canHaveClickScript() {
		return isDrawable();
	}
}
