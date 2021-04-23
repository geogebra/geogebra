/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

/*
 * GeoNumeric.java
 *
 * Created on 18. September 2001, 12:04
 */

package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.AnimationManager;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.arithmetic.Evaluate2Var;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.cas.AlgoIntegralDefiniteInterface;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * 
 * @author Markus
 */
public class GeoNumeric extends GeoElement
		implements GeoNumberValue, AbsoluteScreenLocateable, GeoFunctionable,
		Animatable, HasExtendedAV, SymbolicParametersBotanaAlgo,
		HasSymbolicMode, AnimationExportSlider, Evaluate2Var {

	private PVariable[] botanaVars;

	/** eg boxplot */
	public static final int DEFAULT_THICKNESS = 2;
	/** sliders */
	public static final int DEFAULT_SLIDER_THICKNESS = 10;
	// on 10 range and 10 speed this should yield 0.1 increment
	// (nextPrettyNumber(0.09)=0.1)
	private static final double AUTO_STEP_MUL = 0.0009;
	private static final double AUTO_STEP_MUL_ANGLE = 0.0025;
	/** placeholder for autostep */
	public static final double AUTO_STEP = Double.NaN;

	private static final int DEFAULT_SLIDER_WIDTH_RW = 4;
	/** default slider width in pixels */
	public final static int DEFAULT_SLIDER_WIDTH_PIXEL = 200;
	/** default slider blob size */
	public final static int DEFAULT_SLIDER_BLOB_SIZE = 5;
	/**
	 * Default width of angle slider in pixels
	 * 
	 * Should be a factor of 360 to work well 72 gives increment of 5 degrees
	 * 144 gives increment of 2.5 degrees (doesn't look good) 180 gives
	 * increment of 2 degrees
	 */
	public final static int DEFAULT_SLIDER_WIDTH_PIXEL_ANGLE = 180;

	/** Default maximum value when displayed as slider */
	public final static double DEFAULT_SLIDER_MIN = -5;
	/** Default minimum value when displayed as slider */
	public final static double DEFAULT_SLIDER_MAX = 5;
	/** Default increment when displayed as slider */
	public final static double DEFAULT_SLIDER_INCREMENT = 0.1;
	/** Default increment when displayed as slider */
	public final static double DEFAULT_SLIDER_SPEED = 1;

	/** value of the number or angle */
	public double value;
	/** true if drawable */
	public boolean isDrawable = false;
	// private boolean isRandomNumber = false;

	private int slopeTriangleSize = 1;

	// for slider
	private NumberValue intervalMin;
	private NumberValue intervalMax;
	private double sliderWidth = this instanceof GeoAngle
			? DEFAULT_SLIDER_WIDTH_PIXEL_ANGLE : DEFAULT_SLIDER_WIDTH_PIXEL;
	private double sliderBlobSize = DEFAULT_SLIDER_BLOB_SIZE;
	private SliderPosition sliderPos;
	private boolean sliderFixed = false;
	private boolean sliderHorizontal = true;
	private double animationValue = Double.NaN;

	/** absolute screen location, true by default */
	boolean hasAbsoluteScreenLocation = true;

	private boolean autoStep = false;
	private boolean symbolicMode = false;

	// is a constant depending on a function
	private boolean isDependentConst = false;
	private ArrayList<GeoNumeric> minMaxListeners;
	private boolean randomSlider = false;

	private Double origSliderWidth = null;
	private Double origSliderX = null;
	private Double origSliderY = null;
	private ArrayList<EuclidianViewInterfaceSlim> evListeners = null;

	private boolean showExtendedAV = true;
	private static volatile Comparator<GeoNumberValue> comparator;

	/**
	 * Creates new GeoNumeric
	 * 
	 * @param c
	 *            Construction
	 */
	public GeoNumeric(Construction c) {
		this(c, true);
	}

	/**
	 * Creates new numeric
	 * 
	 * @param c
	 *            construction
	 * @param setDefaults
	 *            true to set from defaults
	 */
	public GeoNumeric(Construction c, boolean setDefaults) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		if (setDefaults) {
			setConstructionDefaults(); // init visual settings
		}

		setEuclidianVisible(false);
		// setAlphaValue(ConstructionDefaults.DEFAULT_POLYGON_ALPHA);
		// setAnimationStep(DEFAULT_SLIDER_INCREMENT);
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_SLIDER;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.NUMERIC;
	}

	/**
	 * Creates new number
	 * 
	 * @param c
	 *            Cons
	 * @param x
	 *            Number value
	 */
	public GeoNumeric(Construction c, double x) {
		this(c);

		value = x;
	}

	@Override
	public GeoNumeric copy() {
		GeoNumeric copy = new GeoNumeric(cons, value);
		copy.setDrawable(isDrawable, false);
		return copy;
	}

	@Override
	public void setZero() {
		setValue(0);
	}

	@Override
	public boolean isDrawable() {
		return isDrawable || (getDrawAlgorithm() != getParentAlgorithm())
				|| (isIndependent() && isLabelSet() && isSimple());
	}

	@Override
	public boolean isFillable() {
		return isDrawable && !isSlider() && getDrawAlgorithm() != null;
	}

	/**
	 * Sets whether the number should be drawable (as slider or angle in case of
	 * GeoAngle) If possible, makes the number also visible.
	 * 
	 * @param flag
	 *            true iff this number should be drawable
	 */
	public final void setDrawable(boolean flag) {
		setDrawable(flag, true);
	}

	/**
	 * Sets whether the number should be drawable (as slider or angle in case of
	 * GeoAngle) and visible.
	 * 
	 * @param flag
	 *            true iff this number should be drawable
	 * @param visible
	 *            true iff this number should be visible
	 */
	public final void setDrawable(boolean flag, boolean visible) {
		isDrawable = flag;
		if (visible && isDrawable && kernel.isNotifyViewsActive()
				&& kernel.isAllowVisibilitySideEffects()) {
			setEuclidianVisible(true);
		}
	}

	@Override
	public void setEuclidianVisible(boolean visible) {
		if (visible == isSetEuclidianVisible() || kernel.isMacroKernel()) {
			return;
		}

		// slider is only possible for independent
		// number with given min and max
		if (isIndependent()) {
			if (visible) { // TODO: Remove cast from GeoNumeric
				isDrawable = true;
				GeoNumeric num = kernel.getAlgoDispatcher()
						.getDefaultNumber(isAngle());
				// make sure the slider value is not fixed
				setFixed(false);
				if (!isIntervalMinActive()
						&& !(intervalMin instanceof GeoNumeric)) {
					if (!isIntervalMaxActive()
							&& !(intervalMax instanceof GeoNumeric)) {
						// set both to default
						setMinFrom(num);
						setMaxFrom(num);
					} else {
						// max is available but no min
						double min = Math.min(num.getIntervalMin(),
								Math.floor(value));
						setIntervalMin(new MyDouble(kernel, min));
					}
				} else { // min exists
					if (!isIntervalMaxActive()
							&& !(intervalMax instanceof GeoNumeric)) {
						// min is available but no max
						double max = Math.max(num.getIntervalMax(),
								Math.ceil(value));
						setIntervalMax(new MyDouble(kernel, max));
					}
				}

				// init screen location
				if (sliderPos == null) {
					initScreenLocation();
				}

				// make sure
			}

			/*
			 * we don't want to remove min, max values when slider is hidden
			 * else { // !visible intervalMinActive = false; intervalMaxActive =
			 * false; }
			 */
		}

		super.setEuclidianVisible(visible);
	}

	private void setMaxFrom(GeoNumeric num) {
		double max = num.getIntervalMax();
		if (value > max) {
			if (Math.ceil(value) < 0) {
				max = 0;
			} else {
				max = MyMath.nextPrettyNumber(value, 0);
			}
		}

		setIntervalMax(new MyDouble(kernel, max));

	}

	private void setMinFrom(GeoNumeric num) {
		double min = num.getIntervalMin();
		if (value < min) {
			if (Math.floor(value) > 0) {
				min = 0;
			} else {
				min = -MyMath.nextPrettyNumber(Math.abs(value), 0);
			}
		}
		setIntervalMin(new MyDouble(kernel, min));

	}

	private void initScreenLocation() {
		int count = countSliders();
		if (getConstruction().getKernel().getApplication()
				.isUnbundled()) {
			count++;
		}
		sliderPos = new SliderPosition();
		if (isAbsoluteScreenLocActive()) {
			sliderPos.x = 30;
			EuclidianViewInterfaceSlim ev = kernel.getApplication()
					.getActiveEuclidianView();
			if (ev != null) {
				sliderPos.y = ev.getSliderOffsetY() + 40 * count;
			} else {
				sliderPos.y = 50 + 40 * count;
			}
			// make sure slider is visible on screen
			sliderPos.y = (int) (sliderPos.y / 400) * 10 + sliderPos.y % 400;
		} else {
			sliderPos.x = -5;
			sliderPos.y = 10 - count;
		}

	}

	private int countSliders() {
		int count = 0;

		// get all number and angle sliders
		TreeSet<GeoElement> numbers = cons
				.getGeoSetLabelOrder(GeoClass.NUMERIC);
		TreeSet<GeoElement> angles = cons.getGeoSetLabelOrder(GeoClass.ANGLE);

		numbers.addAll(angles);

		Iterator<GeoElement> it = numbers.iterator();
		while (it.hasNext()) {
			GeoNumeric num = (GeoNumeric) it.next();
			if (num.isSlider()) {
				count++;
			}
		}

		return count;
	}

	/**
	 * @return true if displayed as slider
	 */
	public boolean isSlider() {
		return isIndependent() && isEuclidianVisible();
	}

	@Override
	public boolean showInEuclidianView() {
		return isDrawable && isDefined() && !Double.isInfinite(value);
	}

	@Override
	public void set(GeoElementND geo) {
		setValue(geo.evaluateDouble());
		reuseDefinition(geo);
	}

	@Override
	final public void setUndefined() {
		value = Double.NaN;
	}

	@Override
	final public boolean isDefined() {
		AlgoElement algo;
		// make sure shaded-only integrals are drawn
		if ((algo = getParentAlgorithm()) instanceof AlgoIntegralDefiniteInterface) {
			AlgoIntegralDefiniteInterface aid = (AlgoIntegralDefiniteInterface) algo;
			if (aid.evaluateOnly()) {
				return true;
			}
		}
		return !Double.isNaN(value);
	}

	/**
	 * Returns true iff defined and infinite
	 * 
	 * @return true iff defined and infinite
	 */
	final public boolean isFinite() {
		return isDefined() && !isInfinite();
	}

	@Override
	final public boolean isInfinite() {
		return Double.isInfinite(value);
	}

	@Override
	public String getLaTeXdescription() {
		if (strLaTeXneedsUpdate) {
			if (!isDefined()) {
				strLaTeX = "?";
			} else if (isInfinite()) {
				if (value >= 0) {
					strLaTeX = "\\infty";
				} else {
					strLaTeX = "-\\infty";
				}
			} else {
				strLaTeX = toLaTeXString(false, StringTemplate.latexTemplate);
			}
		}
		return strLaTeX;
	}

	// Michael Borcherds 2008-04-30
	@Override
	final public boolean isEqual(GeoElementND geo) {
		// return false if it's a different type, otherwise use equals() method
		if (geo.isGeoNumeric()) {
			return DoubleUtil.isEqual(value, ((GeoNumeric) geo).value);
		}
		return false;
	}

	@Override
	public double getAnimationStep() {

		if (getAnimationStepObject() == null) {
			GeoNumeric num = kernel.getAlgoDispatcher()
					.getDefaultNumber(isGeoAngle());
			setAnimationStep(num.getAnimationStep());
		}

		if (isAutoStep()) {
			double dragIncrement = isAngle() ? Kernel.PI_180 : DEFAULT_SLIDER_INCREMENT;
			return isAnimating() || getAutoStepValue() >= dragIncrement ? getAutoStepValue()
					: dragIncrement;
		}

		return super.getAnimationStep();

	}

	private double getAutoStepValue() {
		if (intervalMin == null || intervalMax == null) {
			return isAngle() ? Math.PI / 180 : 0.05;
		}
		if (isAngle()) {
			// default 360 *10/200 -> 2deg
			return MyMath.nextPrettyNumber(
					(intervalMax.getDouble() - intervalMin.getDouble())
							* getAnimationSpeed() * (180 / Math.PI)
							* AUTO_STEP_MUL_ANGLE,
					0) * (Math.PI / 180);
		}
		return MyMath.nextPrettyNumber(
				(intervalMax.getDouble() - intervalMin.getDouble())
						* getAnimationSpeed() * AUTO_STEP_MUL,
				0);
	}

	/**
	 * indicates that animation step is computed automatically or not.
	 * 
	 * @return true is automatic animation step is set
	 */
	public boolean isAutoStep() {
		return autoStep;
	}

	/**
	 * Sets automatic animation step on or off.
	 * 
	 * @param autoStep
	 *            true if step should be computed automatically.
	 */
	public void setAutoStep(boolean autoStep) {
		this.autoStep = autoStep;
	}

	@Override
	public double getAnimationSpeed() {
		if (getAnimationSpeedObject() == null) {
			GeoNumeric num = kernel.getAlgoDispatcher()
					.getDefaultNumber(isGeoAngle());
			setAnimationSpeed(num.getAnimationSpeed());
		}
		return super.getAnimationSpeed();
	}

	/**
	 * Sets value of the number
	 * 
	 * @param x
	 *            number value
	 */
	@Override
	final public void setValue(double x) {
		setValue(x, true);
	}

	/**
	 * @param val0
	 *            preferred value
	 * @return value that respects min, max, step
	 */
	final public double restrictToSliderValues(double val0) {
		double min = getIntervalMin();
		double max = getIntervalMax();
		double val = val0;
		if (val > max) {
			val = max;
		} else {
			if (val < min) {
				val = min;
			}
		}

		// round to animation step scale
		val = Kernel.roundToScale(val - min, getAnimationStep()) + min;

		if (getAnimationStep() > Kernel.MIN_PRECISION) {
			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			val = DoubleUtil.checkDecimalFraction(val);
		}

		return val;

	}

	/**
	 * Sets value of the number
	 * 
	 * @param x
	 *            number value
	 * @param changeAnimationValue
	 *            if true, value is changed also for animation TODO reduce
	 *            visibility again
	 */
	public synchronized void setValue(double x, boolean changeAnimationValue) {
		setDefinition(null);
		if (Double.isNaN(x)) {
			value = Double.NaN;
		} else if (isIntervalMinActive() && x < getIntervalMin()) {
			value = getIntervalMin();
			if (getCorrespondingCasCell() != null) {
				getCorrespondingCasCell().setInputFromTwinGeo(true, false);
			}
		} else if (isIntervalMaxActive() && x > getIntervalMax()) {
			value = getIntervalMax();
			if (getCorrespondingCasCell() != null) {
				getCorrespondingCasCell().setInputFromTwinGeo(true, false);
			}
		} else {
			value = x;
		}

		// remember value for animation also
		if (changeAnimationValue) {
			animationValue = value;
		}

		if (isLabelSet() && isSliderable() && isSelected()) {
			kernel.getApplication().readLater(this);
		}
	}

	/**
	 * Returns value of the number
	 * 
	 * @return number value
	 */
	final public synchronized double getValue() {
		return value;
	}

	@Override
	public String toString(StringTemplate tpl) {
		// #4186
		if (tpl.hasCASType()) {
			return toValueString(tpl);
		}

		if (LabelManager.isShowableLabel(label)) {
			return label + tpl.getEqualsWithSpace() + toValueString(tpl);
		} else {
			return toValueString(tpl);
		}
	}

	/**
	 * @return string representation for regression output
	 */
	final public String toStringMinimal() {
		return regrFormat(value);
	}

	@Override
	public String toValueString(StringTemplate tpl) {

		// see MyDouble.toString()
		if (tpl.hasCASType()) {
			if (this.label != null && (this.label.startsWith("c_")
					|| this.label.startsWith("k_"))) {
				// needed for GGB-903
				// if label starts with c_
				// look up if it is stored as constant
				GeoNumeric geo = this.cons.lookupConstantLabel(label);
				if (geo != null) {
					this.setSendValueToCas(false);
				} else {
					this.setSendValueToCas(true);
				}
			}
			if (!sendValueToCas) {
				return "(" + Kernel.TMP_VARIABLE_PREFIX + label + ")";
			}
			// make sure random() works inside Sequence, see #3558 TRAC-1465
			if (this.isRandomGeo() && !this.isLabelSet()
					&& !(getParentAlgorithm() instanceof SetRandomValue)) {
				return "exact(rand(0,1))";
			}

			if (Double.isNaN(value)) {
				return "undef";
			}

			if (Double.isInfinite(value)) {
				if (value > 0) {
					return "inf";
				}
				return "-inf";
			}
			if (getDefinition() != null) {
				return getDefinition().toValueString(tpl);
			}
			return StringUtil.wrapInExact(kernel.format(value, tpl), tpl);
		}
		if (symbolicMode && getDefinition() != null && tpl.supportsFractions()) {
			return getDefinition().toFractionString(tpl);
		}
		return kernel.format(value, tpl);
	}

	/**
	 * interface NumberValue
	 */
	@Override
	public MyDouble getNumber() {
		return new MyDouble(kernel, value);
	}

	@Override
	final public double getDouble() {
		return value;
	}

	@Override
	public void setAllVisualPropertiesExceptEuclidianVisible(GeoElement geo,
			boolean keepAdvanced, boolean setAuxiliaryProperty) {
		super.setAllVisualPropertiesExceptEuclidianVisible(geo, keepAdvanced,
				setAuxiliaryProperty);

		if (geo.isGeoNumeric() && !geo.isGeoAngle()) {
			isDrawable = ((GeoNumeric) geo).isDrawable;
		}
	}

	@Override
	public void setVisualStyle(GeoElement geo, boolean setAuxiliaryProperty) {
		super.setVisualStyle(geo, setAuxiliaryProperty);

		if (geo.isGeoNumeric()) {
			slopeTriangleSize = ((GeoNumeric) geo).slopeTriangleSize;
			setAutoStep(((GeoNumeric) geo).autoStep);
			symbolicMode = ((GeoNumeric) geo).symbolicMode;
			sliderFixed = ((GeoNumeric) geo).sliderFixed;
		}
	}

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		sb.append("\t<value val=\"");
		sb.append(value);
		sb.append("\"");
		if (isRandom()) {
			sb.append(" random=\"true\"");
		}
		sb.append("/>\n");
		XMLBuilder.appendSymbolicMode(sb, this, false);
		// colors
		getXMLvisualTags(sb);

		// if number is drawable then we need to save visual options too
		if (isDrawable || isSliderable()) {
			// save slider info before show to have min and max set
			// before setEuclidianVisible(true) is called
			getXMLsliderTag(sb);

			// line thickness and type
			getLineStyleXML(sb);

			// for slope triangle
			if (slopeTriangleSize > 1) {
				sb.append("\t<slopeTriangleSize val=\"");
				sb.append(slopeTriangleSize);
				sb.append("\"/>\n");
			}
		}
		getXMLanimationTags(sb);
		getXMLfixedTag(sb);
		getAuxiliaryXML(sb);
		getBreakpointXML(sb);
		getScriptTags(sb);
	}

	/**
	 * Returns true iff slider is possible
	 * 
	 * @return true iff slider is possible
	 */
	public boolean isSliderable() {
		return hasValidIntervals() && isSimple();
	}

	private boolean hasValidIntervals() {
		return isIntervalMinActive()
				&& isIntervalMaxActive()
				&& getIntervalMin() < getIntervalMax();
	}

	@Override
	public boolean isFixable() {
		// visible slider should not be fixable if whiteboard doesn't active
		return !isSetEuclidianVisible() && !isDefaultGeo();
	}

	@Override
	public boolean showFixUnfix() {
		return false;
	}

	/**
	 * @param b
	 *            - true, if is constant depending on function
	 */
	public void setIsDependentConst(boolean b) {
		this.isDependentConst = b;
	}

	/**
	 * @return true - if is constant depending on function
	 */
	public boolean isDependentConst() {
		return this.isDependentConst;
	}

	/**
	 * Adds the slider tag to the string builder
	 * 
	 * @param sb
	 *            String builder to be written to
	 */
	protected void getXMLsliderTag(StringBuilder sb) {
		if (!isSliderable()) {
			return;
		}

		StringTemplate tpl = StringTemplate.xmlTemplate;
		sb.append("\t<slider");
		if (isIntervalMinActive() || intervalMin instanceof GeoNumeric) {
			sb.append(" min=\"");
			StringUtil.encodeXML(sb, getIntervalMinObject().getLabel(tpl));
			sb.append("\"");
		}
		if (isIntervalMaxActive() || intervalMax instanceof GeoNumeric) {
			sb.append(" max=\"");
			StringUtil.encodeXML(sb, getIntervalMaxObject().getLabel(tpl));
			sb.append("\"");
		}

		if (hasAbsoluteScreenLocation) {
			sb.append(" absoluteScreenLocation=\"true\"");
		}

		sb.append(" width=\"");
		sb.append(sliderWidth);
		if (sliderPos != null) {
			sb.append("\" x=\"");
			sb.append(sliderPos.x);
			sb.append("\" y=\"");
			sb.append(sliderPos.y);
		}
		sb.append("\" fixed=\"");
		sb.append(sliderFixed);
		sb.append("\" horizontal=\"");
		sb.append(sliderHorizontal);
		sb.append("\" showAlgebra=\"");
		sb.append(isShowingExtendedAV());
		sb.append("\"/>\n");
		if (sliderBlobSize != DEFAULT_SLIDER_BLOB_SIZE) {
			sb.append("\t<pointSize val=\"");
			sb.append(sliderBlobSize);
			sb.append("\"/>\n");
		}
	}

	@Override
	public boolean isNumberValue() {
		return true;
	}

	@Override
	public boolean isGeoNumeric() {
		return true;
	}

	/**
	 * Returns size of the triangle when used for slop
	 * 
	 * @return size of the triangle when used for slope
	 */
	final public int getSlopeTriangleSize() {
		return slopeTriangleSize;
	}

	/**
	 * Set size of the triangle when used for slop
	 * 
	 * @param i
	 *            Size of the slope triangle
	 */
	public void setSlopeTriangleSize(int i) {
		slopeTriangleSize = i;
	}

	/**
	 * Changes maximal value for slider
	 * 
	 * @param max
	 *            New maximum for slider
	 */
	public void setIntervalMax(NumberValue max) {
		if (intervalMax instanceof GeoNumeric) {
			((GeoNumeric) intervalMax).unregisterMinMaxListener(this);
		}
		intervalMax = max;
		if (max instanceof GeoNumeric) {
			((GeoNumeric) max).registerMinMaxListener(this);
		}
		resolveMinMax();
	}

	/**
	 * Changes minimal value for slider
	 * 
	 * @param min
	 *            New minimum for slider
	 */
	public void setIntervalMin(NumberValue min) {
		if (intervalMin instanceof GeoNumeric) {
			((GeoNumeric) intervalMin).unregisterMinMaxListener(this);
		}
		intervalMin = min;
		if (min instanceof GeoNumeric) {
			((GeoNumeric) min).registerMinMaxListener(this);
		}
		resolveMinMax();
	}

	/**
	 * Changes slider width in pixels
	 * 
	 * @param width
	 *            slider width in pixels
	 * @param fromUser
	 *            whether this is triggered by user (and should override
	 *            original width)
	 */
	public final void setSliderWidth(double width, boolean fromUser) {
		if (width > 0 && !Double.isInfinite(width) && fromUser) {
			setOrigSliderWidth(width);
		}
		sliderWidth = width;
	}

	/**
	 * Changes slider blob size in pixels
	 * 
	 * @param blobSize
	 *            slider blob size in pixels
	 */
	public final void setSliderBlobSize(double blobSize) {
		if (blobSize > 0 && !Double.isInfinite(blobSize)) {
			sliderBlobSize = blobSize;
		}
	}

	/**
	 * Sets the location of the slider for this number.
	 * 
	 * @param x
	 *            x-coord of the slider
	 * @param y
	 *            y-coord of the slider
	 * @param force
	 *            when false, this method ignores fixed sliders
	 */
	public final void setSliderLocation(double x, double y, boolean force) {
		if (!force && sliderFixed) {
			return;
		}
		if (sliderPos == null) {
			sliderPos = new SliderPosition();
		}
		sliderPos.x = x;
		sliderPos.y = y;
		if (origSliderX == null) {
			origSliderX = x;
			origSliderY = y;
		}
	}

	/**
	 * Returns maximal value for slider
	 * 
	 * @return maximal value for slider
	 */
	@Override
	public final double getIntervalMax() {
		if (intervalMax == null) {
			return Double.NaN;
		}
		return intervalMax.getDouble();
	}

	/**
	 * Returns minimal value for slider
	 * 
	 * @return minimal value for slider
	 */
	@Override
	public final double getIntervalMin() {
		if (intervalMin == null) {
			return Double.NaN;
		}
		return intervalMin.getDouble();
	}

	/**
	 * Returns slider width in pixels
	 * 
	 * @return slider width in pixels
	 */
	public final double getSliderWidth() {
		return sliderWidth;
	}

	/**
	 * Returns slider blob size
	 * 
	 * @return slider blob size
	 */
	public double getSliderBlobSize() {
		return sliderBlobSize;
	}

	/**
	 * Returns x-coord of the slider
	 * 
	 * @return x-coord of the slider
	 */
	public final double getSliderX() {
		return sliderPos == null ? 0 : sliderPos.x;
	}

	/**
	 * Returns y-coord of the slider
	 * 
	 * @return y-coord of the slider
	 */
	public final double getSliderY() {
		return sliderPos == null ? 0 : sliderPos.y;
	}

	/**
	 * Returns true if slider max value wasn't disabled in Properties
	 * 
	 * @return true if slider max value wasn't disabled
	 */
	public final boolean isIntervalMaxActive() {
		return isValidInterval(getIntervalMax());
	}

	private boolean isValidInterval(double value) {
		return !Double.isNaN(value) && !Double.isInfinite(value);
	}

	/**
	 * Returns true if slider min value wasn't disabled in Properties
	 * 
	 * @return true if slider min value wasn't disabled
	 */
	public final boolean isIntervalMinActive() {
		return isValidInterval(getIntervalMin());
	}

	/**
	 * Returns true iff slider is fixed in graphics view
	 * 
	 * @return true iff slider is fixed in graphics view
	 */
	@Override
	public final boolean isLockedPosition() {
		return sliderFixed;
	}

	/**
	 * Sets whether slider is fixed in graphics view
	 * 
	 * @param lockedPosition
	 *            true iff slider is fixed in graphics view
	 */
	public final void setSliderFixed(boolean lockedPosition) {
		sliderFixed = lockedPosition;
	}

	/**
	 * Returns whether slider shoud be horizontal or vertical
	 * 
	 * @return true iff should be horizontal
	 */
	public final boolean isSliderHorizontal() {
		return sliderHorizontal;
	}

	/**
	 * Sets whether slider should be horizontal or vertical
	 * 
	 * @param sliderHorizontal
	 *            true iff should be horizontal
	 */
	public void setSliderHorizontal(boolean sliderHorizontal) {
		this.sliderHorizontal = sliderHorizontal;
	}

	/**
	 * Sets the location of the slider for this number.
	 * 
	 * @param x
	 *            x-coord of the slider
	 * @param y
	 *            y-coord of the slider
	 * @param force
	 *            when false, this method ignores fixed sliders
	 */
	public void setAbsoluteScreenLoc(int x, int y, boolean force) {
		setSliderLocation(x, y, force);
	}

	@Override
	public void setAbsoluteScreenLoc(int x, int y) {
		setSliderLocation(x, y, true);
	}

	@Override
	public int getAbsoluteScreenLocX() {
		return sliderPos == null ? 0 : (int) sliderPos.x;
	}

	@Override
	public int getAbsoluteScreenLocY() {
		return sliderPos == null ? 0 : (int) sliderPos.y;
	}

	@Override
	public void setRealWorldLoc(double x, double y) {
		if (sliderPos == null) {
			sliderPos = new SliderPosition();
		}
		sliderPos.x = x;
		sliderPos.y = y;
	}

	@Override
	public double getRealWorldLocX() {
		return sliderPos == null ? 0 : sliderPos.x;
	}

	@Override
	public double getRealWorldLocY() {
		return sliderPos == null ? 0 : sliderPos.y;
	}

	@Override
	public void setAbsoluteScreenLocActive(boolean flag) {
		hasAbsoluteScreenLocation = flag;
		if (flag) {
			sliderWidth = this instanceof GeoAngle
					? DEFAULT_SLIDER_WIDTH_PIXEL_ANGLE
					: DEFAULT_SLIDER_WIDTH_PIXEL;
		} else {
			sliderWidth = DEFAULT_SLIDER_WIDTH_RW;
		}
	}

	@Override
	public boolean isAbsoluteScreenLocActive() {
		return hasAbsoluteScreenLocation;
	}

	@Override
	public boolean isAbsoluteScreenLocateable() {
		return isSliderable();
	}

	/**
	 * Creates a GeoFunction of the form f(x) = thisNumber
	 * 
	 * @return constant function
	 * @deprecated see parent
	 */
	@Deprecated
	@Override
	public GeoFunction getGeoFunction() {
		Function fun = getFunction();
		GeoFunction ret;

		// we get a dependent function if this number has a label or is
		// dependent
		if (isLabelSet() || !isIndependent()) {
			ret = new AlgoDependentFunction(cons, fun, false).getFunction();
		} else {
			ret = new GeoFunction(cons);
			ret.setFunction(fun);
		}

		return ret;
	}

	@Override
	public boolean isRealValuedFunction() {
		return true;
	}

	@Override
	public void doRemove() {
		super.doRemove();

		// if this was a random number, make sure it's removed
		cons.removeRandomGeo(this);
		if (intervalMin instanceof GeoNumeric) {
			((GeoNumeric) intervalMin).unregisterMinMaxListener(this);
		}
		if (intervalMax instanceof GeoNumeric) {
			((GeoNumeric) intervalMax).unregisterMinMaxListener(this);
		}
	}

	/**
	 * Given geo depends on this one (via min or max value for slider) and
	 * should be updated
	 * 
	 * @param geo
	 *            geo to be updated
	 */
	public void registerMinMaxListener(GeoNumeric geo) {
		if (minMaxListeners == null) {
			minMaxListeners = new ArrayList<>();
		}
		minMaxListeners.add(geo);
	}

	/**
	 * Given geo no longer depends on this one (via min or max value for slider)
	 * and should not be updated any more
	 * 
	 * @param geo
	 *            slider whose min/max is this numeric
	 */
	public void unregisterMinMaxListener(GeoNumeric geo) {
		if (minMaxListeners == null) {
			minMaxListeners = new ArrayList<>();
		}
		minMaxListeners.remove(geo);
	}

	/**
	 * @return list of min/max listeners
	 */
	public List<GeoNumeric> getMinMaxListeners() {
		return minMaxListeners;
	}

	/**
	 * @param random
	 *            true for random slider
	 */
	public void setRandom(boolean random) {
		randomSlider = random;
		if (random) {
			cons.addRandomGeo(this);
		} else {
			cons.removeRandomGeo(this);
		}
	}

	/**
	 * returns true for random sliders (can be hidden to make random numbers
	 * which still use intervalMin, Max, interval)
	 * 
	 * @return true for random sliders
	 */
	public boolean isRandom() {
		return randomSlider;
	}

	/**
	 * Updates random slider
	 */
	public void updateRandom() {
		if (randomSlider && isIntervalMaxActive() && isIntervalMinActive()) {
			// update all algorithms in the algorithm set of this GeoElement
			value = getRandom();
			updateCascade();
		}

	}

	/**
	 * Updates random slider - no updateCascade()
	 */
	public void updateRandomNoCascade() {
		if (randomSlider && isIntervalMaxActive() && isIntervalMinActive()) {
			// update all algorithms in the algorithm set of this GeoElement
			value = getRandom();
		}

	}

	/*
	 * returns a random number in the slider's range (and using step-size)
	 */
	private double getRandom() {
		double min = getIntervalMin();
		double max = getIntervalMax();
		double increment = getAnimationStep();
		int n = 1 + (int) Math.round((max - min) / increment);
		return DoubleUtil.checkDecimalFraction(
				Math.floor(kernel.getApplication().getRandomNumber() * n)
						* increment + min);
	}

	@Override
	public void update(boolean drag) {
		super.update(drag);
		if (minMaxListeners != null) {
			for (int i = 0; i < minMaxListeners.size(); i++) {
				GeoNumeric geo = minMaxListeners.get(i);
				geo.resolveMinMax();
			}
		}

		if (evListeners != null) {
			for (EuclidianViewInterfaceSlim ev : evListeners) {
				ev.updateBounds(true, true);
			}
		}
	}

	private void resolveMinMax() {
		double oldValue = value;
		if (intervalMin == null || intervalMax == null) {
			return;
		}
		boolean okMin = isIntervalMinActive();
		boolean okMax = isIntervalMaxActive();
		boolean ok = (getIntervalMin() <= getIntervalMax());
		if (ok && okMin && okMax) {
			setValue(isDefined() ? value : 1.0);
			isDrawable = true;
		} else if (okMin && okMax) {
			setUndefined();
		}
		if (oldValue != value) {
			updateCascade();
		} else {
			// we want to make the slider visible again if it was not
			// do what GeoElement.update does (no need to call listeners)
			// also don't update the CAS
			updateGeo(false);
			kernel.notifyUpdate(this);
		}
	}

	/**
	 * Returns whether this number can be animated. Only free numbers with min
	 * and max interval values can be animated (i.e. shown or hidden sliders).
	 */
	@Override
	public boolean isAnimatable() {
		return isIndependent() && isIntervalMinActive()
				&& isIntervalMaxActive();
	}

	/**
	 * Sets the state of this object to animating on or off.
	 */
	@Override
	public synchronized void setAnimating(boolean flag) {
		animationValue = Double.NaN;
		super.setAnimating(flag);
	}

	/**
	 * Performs the next automatic animation step for this numbers. This changes
	 * the value but will NOT call update() or updateCascade().
	 * 
	 * @return this or null, depending on whether the value of this number was
	 *         changed
	 */
	@Override
	final public synchronized GeoNumeric doAnimationStep(double frameRate,
			GeoList parent) {
		// check that we have valid min and max values
		if (!isIntervalMinActive() || !isIntervalMaxActive()) {
			return null;
		}

		// special case for random slider
		// animationValue goes from 0 to animationStep
		if (isRandom()) {

			double animationStep = getAnimationStep();

			// check not silly value
			if (animationValue < -2 * animationStep) {
				animationValue = 0;
			}

			double intervalWidth = getIntervalMax() - getIntervalMin();
			double step = intervalWidth * getAnimationSpeed()
					/ (AnimationManager.STANDARD_ANIMATION_TIME * frameRate);
			// update animation value
			if (Double.isNaN(animationValue) || animationValue < 0) {
				animationValue = 0;
			}
			animationValue = animationValue + Math.abs(step);

			if (animationValue > animationStep) {
				animationValue -= animationStep;
				setValue(getRandom(), false);
				return this;
			}

			// no update needed
			return null;
		}

		// remember old value of number to decide whether update is necessary
		double oldValue = getValue();

		// compute animation step based on speed and frame rates
		double intervalWidth = getIntervalMax() - getIntervalMin();
		double step = intervalWidth * getAnimationSpeed()
				* getAnimationDirection()
				/ (AnimationManager.STANDARD_ANIMATION_TIME * frameRate);

		// update animation value
		if (Double.isNaN(animationValue)) {
			animationValue = oldValue;
		}
		animationValue = animationValue + step;

		// make sure we don't get outside our interval
		switch (getAnimationType()) {
		case GeoElementND.ANIMATION_DECREASING:
		case GeoElementND.ANIMATION_INCREASING:
			// jump to other end of slider
			if (animationValue > getIntervalMax()) {
				animationValue = animationValue - intervalWidth;
			} else if (animationValue < getIntervalMin()) {
				animationValue = animationValue + intervalWidth;
			}
			break;

		case GeoElementND.ANIMATION_INCREASING_ONCE:
			// stop if outside range
			if (animationValue > getIntervalMax()) {
				setAnimating(false);
				boolean changed = getIntervalMax() != value;
				setValue(getIntervalMax(), false);
				return changed ? this : null;
			} else if (animationValue < getIntervalMin()) {
				setAnimating(false);
				setValue(getIntervalMin(), false);
				return this;
			}
			break;

		case GeoElementND.ANIMATION_OSCILLATING:
		default:
			boolean parentStep = false;
			if (animationValue >= getIntervalMax()) {
				animationValue = getIntervalMax();
				changeAnimationDirection();
				parentStep = true;
			} else if (animationValue <= getIntervalMin()) {
				animationValue = getIntervalMin();
				changeAnimationDirection();
				parentStep = true;
			}
			if (parentStep && parent != null) {
				parent.selectNext();
				return null;
			}
			break;
		}

		double newValue;

		// take current slider increment size into account:
		// round animationValue to newValue using slider's increment setting
		double param = animationValue - getIntervalMin();
		param = Kernel.roundToScale(param, getAnimationStep());
		newValue = getIntervalMin() + param;

		if (getAnimationStep() > Kernel.MIN_PRECISION) {
			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			newValue = DoubleUtil.checkDecimalFraction(newValue);
		}

		// change slider's value without changing animationValue
		setValue(newValue, false);

		// return whether value of slider has changed
		return getValue() != oldValue ? this : null;
	}

	/**
	 * Returns a comparator for NumberValue objects. If equal, doesn't return
	 * zero (otherwise TreeSet deletes duplicates)
	 * 
	 * @return 1 if first is greater (or same but sooner in construction), -1
	 *         otherwise
	 */
	public static Comparator<GeoNumberValue> getComparator() {
		if (comparator == null) {
			comparator = new Comparator<GeoNumberValue>() {
				@Override
				public int compare(GeoNumberValue itemA, GeoNumberValue itemB) {

					double comp = itemA.getDouble() - itemB.getDouble();
					if (DoubleUtil.isZero(comp)) {
						// don't return 0 for equal objects, otherwise the
						// TreeSet deletes duplicates
						return itemA.getConstructionIndex() > itemB
								.getConstructionIndex() ? -1 : 1;
					}
					return comp < 0 ? -1 : +1;
				}
			};
		}

		return comparator;
	}

	@Override
	final public void updateRandomGeo() {
		// set random value (for numbers used in trees using random())
		setValue(kernel.getApplication().getRandomNumber());

		final AlgoElement algo = getParentAlgorithm();
		if (algo != null) {
			algo.compute(); // eg AlgoRandom etc
		} else {
			updateRandom();
		}
	}

	/**
	 * @return minimum line thickness (normally 1, but 0 for polygons, integrals
	 *         etc)
	 */
	@Override
	public int getMinimumLineThickness() {
		return (isSlider() ? 1 : 0);
	}

	/**
	 * Set interval min
	 * 
	 * @param value
	 *            new min for this slider
	 */
	public void setIntervalMin(double value) {
		setIntervalMin(new MyDouble(kernel, value));
	}

	/**
	 * Set interval max
	 * 
	 * @param value
	 *            new max for this slider
	 */
	public void setIntervalMax(double value) {
		setIntervalMax(new MyDouble(kernel, value));
	}

	/**
	 * Get interval min as geo
	 * 
	 * @return interval min
	 */
	public NumberValue getIntervalMinObject() {
		return intervalMin;
	}

	/**
	 * Get interval max as geo
	 * 
	 * @return interval max
	 */
	public NumberValue getIntervalMaxObject() {
		return intervalMax;
	}

	@Override
	public boolean canHaveClickScript() {
		return false;
	}

	@Override
	final public boolean isCasEvaluableObject() {
		return true;
	}

	/**
	 * @param ev
	 *            euclidian view which listens to this numeric
	 */
	public void addEVSizeListener(EuclidianViewInterfaceSlim ev) {
		if (evListeners == null) {
			evListeners = new ArrayList<>();
		}

		if (!evListeners.contains(ev)) {
			evListeners.add(ev);
		}
	}

	/**
	 * @param ev
	 *            euclidian view which listens to this numeric
	 */
	public void removeEVSizeListener(EuclidianViewInterfaceSlim ev) {
		if (evListeners != null) {
			evListeners.remove(ev);
		}
	}

	@Override
	public void moveDependencies(GeoElement oldGeo) {
		if (!oldGeo.isGeoNumeric()) {
			return;
		}
		GeoNumeric num = (GeoNumeric) oldGeo;
		if (num.evListeners != null) {

			evListeners = num.evListeners;
			for (EuclidianViewInterfaceSlim ev : num.evListeners) {
				ev.replaceBoundObject(num, this);
			}

			num.evListeners = null;
		}
		if (num.minMaxListeners != null) {
			minMaxListeners = num.minMaxListeners;
			for (GeoNumeric slider : minMaxListeners) {
				if (slider.getIntervalMaxObject() == num) {
					slider.setIntervalMax(this);
				}
				if (slider.getIntervalMinObject() == num) {
					slider.setIntervalMin(this);
				}
			}
		}
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return false;
	}

	@Override
	public boolean hasLineOpacity() {
		return true;
	}

	@Override
	public void addToSpreadsheetTraceList(
			ArrayList<GeoNumeric> spreadsheetTraceList) {
		GeoNumeric xx = this.copy(); // should handle GeoAngle
		// too
		spreadsheetTraceList.add(xx);

	}

	@Override
	public boolean isPinnable() {
		return isSlider();
	}

	@Override
	public ExpressionValue integral(FunctionVariable fv, Kernel kernel0) {
		return new ExpressionNode(kernel0, this, Operation.MULTIPLY, fv);
	}

	/**
	 * @param num
	 *            number to update
	 * @param isAngle
	 *            whether it's angle
	 * @return num
	 */
	public static GeoNumeric setSliderFromDefault(GeoNumeric num,
			boolean isAngle) {
		return setSliderFromDefault(num, isAngle, true);

	}

	/**
	 * @param num
	 *            number to update
	 * @param isAngle
	 *            whether it's angle
	 * @param visible
	 *            visible in EV
	 * @return num
	 */
	public static GeoNumeric setSliderFromDefault(GeoNumeric num,
			boolean isAngle, boolean visible) {
		GeoNumeric defaultNum = num.getKernel().getAlgoDispatcher()
				.getDefaultNumber(false);
		GeoNumeric defaultAngleOrNum = num.getKernel().getAlgoDispatcher()
				.getDefaultNumber(isAngle);
		num.setSliderFixed(defaultNum.isLockedPosition());
		num.setEuclidianVisible(visible);
		num.setIntervalMin(defaultAngleOrNum.getIntervalMinObject());
		num.setIntervalMax(defaultAngleOrNum.getIntervalMaxObject());
		num.setAnimationStep(defaultAngleOrNum.getAnimationStep());
		num.setAutoStep(defaultAngleOrNum.isAutoStep());
		num.setAbsoluteScreenLocActive(true);
		num.setAnimationType(defaultNum.getAnimationType());
		num.setSliderWidth(defaultAngleOrNum.getSliderWidth(), true);
		num.setRandom(defaultNum.isRandom());
		num.setLineThickness(DEFAULT_SLIDER_THICKNESS);
		num.update();
		return num;
	}

	@Override
	public boolean isShowingExtendedAV() {
		return showExtendedAV;
	}

	@Override
	public void setShowExtendedAV(boolean showExtendedAV) {
		this.showExtendedAV = showExtendedAV;
		notifyUpdate();
	}

	@Override
	protected void setLabelModeDefault() {

		// label visibility
		App app = getKernel().getApplication();
		int labelingStyle = app == null
				? ConstructionDefaults.LABEL_VISIBLE_USE_DEFAULTS
				: app.getCurrentLabelingStyle();

		// automatic labelling:
		// if algebra window open -> all labels
		// else -> no labels
		boolean visible = false;
		switch (labelingStyle) {
		case ConstructionDefaults.LABEL_VISIBLE_ALWAYS_ON:
			visible = true;
			break;

		case ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF:
			visible = false;
			break;

		case ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY:
			// we want sliders and angles to be labeled always
			visible = true;
			break;

		default:
		case ConstructionDefaults.LABEL_VISIBLE_USE_DEFAULTS:
			// don't change anything
			visible = true;
			break;
		}

		if (visible) {
			labelMode = LABEL_NAME_VALUE;
		} else {
			labelMode = LABEL_VALUE;
		}

	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersBotanaAlgo) {
			return ((SymbolicParametersBotanaAlgo) algoParent)
					.getBotanaVars(this);
		}

		if (algoParent == null) {
			if (botanaVars == null) {
				botanaVars = new PVariable[1];
				botanaVars[0] = new PVariable(kernel); // ,true
				Log.debug("Variable " + geo.getLabelSimple() + "("
						+ botanaVars[0] + ")");
			}
		}

		return botanaVars;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersBotanaAlgo) {
			return ((SymbolicParametersBotanaAlgo) algoParent)
					.getBotanaPolynomials(this);
		}
		return null; // Here maybe an exception should be thrown...?
	}

	/**
	 * @param geoElement
	 *            make sure min/max interval is big enough to contain the value
	 */
	public void extendMinMax(GeoElement geoElement) {
		if (geoElement instanceof GeoNumeric) {
			value = geoElement.evaluateDouble();
			if (getIntervalMaxObject() != null
					&& isChangeable(getIntervalMaxObject())) {
				setMaxFrom(this);
			}
			value = geoElement.evaluateDouble();
			if (getIntervalMinObject() != null
					&& isChangeable(getIntervalMinObject())) {
				setMinFrom(this);
			}
		}

	}

	/**
	 * @param val
	 *            value
	 * @return whether value is either not numeric or it's unlabeled independent
	 *         numeric
	 */
	public static boolean isChangeable(NumberValue val) {
		if (!(val instanceof GeoElement)) {
			return true;
		}
		return ((GeoElement) val).isIndependent()
				&& !((GeoElement) val).isLabelSet();
	}

	@Override
	public ValueType getValueType() {
		return ValueType.NUMBER;
	}

	/**
	 * Update min and max for slider in Algebra
	 */
	public void initAlgebraSlider() {
		if (!showExtendedAV) {
			return;
		}
		SliderPosition old = sliderPos;
		setEuclidianVisible(true);
		setEuclidianVisible(false);
		sliderPos = old;

	}

	@Override
	public ExpressionValue getUndefinedCopy(Kernel kernel1) {
		return new MyDouble(kernel1, Double.NaN);
	}

	@Override
	public ValidExpression toValidExpression() {
		return getNumber();
	}

	@Override
	public void initSymbolicMode() {
		ExpressionNode definition = getDefinition();
		boolean symbolicMode =
				(definition == null)
						|| (!definition.isSimpleFraction() && definition.isFractionNoPi());
		setSymbolicMode(symbolicMode, false);
	}

	@Override
	public void setSymbolicMode(boolean mode, boolean update) {
		this.symbolicMode = mode;
	}

	@Override
	public boolean isSymbolicMode() {
		return symbolicMode;
	}

	@Override
	public DescriptionMode getDescriptionMode() {
		if (getDefinition() != null
				&& (getDefinition().isFraction() || value != Math.round(value))
				&& !"?".equals(getDefinition(StringTemplate.defaultTemplate))) {
			return DescriptionMode.DEFINITION_VALUE;
		}
		if (isSimple() || (!isDefined() && isIndependent())) {
			// matters in scientific where we don't have AV sliders
			return DescriptionMode.VALUE;
		}
		return super.getDescriptionMode();
	}

	/**
	 * @return original slider width from XML
	 */
	public Double getOrigSliderWidth() {
		return origSliderWidth;
	}

	/**
	 * @param origSliderWidth
	 *            original slider width from XML
	 */
	public void setOrigSliderWidth(Double origSliderWidth) {
		this.origSliderWidth = origSliderWidth;
	}

	/**
	 * @return original slider x-coord from XML
	 */
	public Double getOrigSliderX() {
		return origSliderX;
	}

	/**
	 * @param origSliderX
	 *            original slider x-coord from XML
	 */
	public void setOrigSliderX(Double origSliderX) {
		this.origSliderX = origSliderX;
	}

	/**
	 * @return original slider y-coord from XML
	 */
	public Double getOrigSliderY() {
		return origSliderY;
	}

	/**
	 * @param origSliderY
	 *            original slider y-coord from XML
	 */
	public void setOrigSliderY(Double origSliderY) {
		this.origSliderY = origSliderY;
	}

	@Override
	public double evaluate(double x, double y) {
		return value;
	}

	@Override
	public ExpressionNode getFunctionExpression() {
		return wrap();
	}

	@Override
	public Function getFunction() {
		return new Function(wrap(), new FunctionVariable(kernel));
	}

	@Override
	public String getVarString(StringTemplate defaulttemplate) {
		return "x";
	}

	@Override
	public int getTotalWidth(EuclidianViewInterfaceCommon ev) {
		return 0;
	}

	@Override
	public boolean isFurniture() {
		return false;
	}

	@Override
	public int getTotalHeight(EuclidianViewInterfaceCommon ev) {
		return 0;
	}

	@Override
	public double evaluateDouble() {
		return getDouble();
	}

	private void addAuralSliderValue(ScreenReaderBuilder sb) {
		if (!addAuralCaption(sb)) {
			sb.append(getLabelSimple());
			sb.append(getLabelDelimiterWithSpace(StringTemplate.screenReader));
			sb.append(toValueString(StringTemplate.defaultTemplate));
		}
	}

	@Override
	public void addAuralName(ScreenReaderBuilder sb) {
		if (!isSliderable()) {
			super.addAuralName(sb);
			return;
		}
		if (StringUtil.empty(getCaptionSimple())) {
			sb.appendMenuDefault("Slider", "Slider");
			sb.appendSpace();
		}
		addAuralSliderValue(sb);
		sb.endSentence();
	}

	@Override
	public void addAuralOperations(Localization loc, ScreenReaderBuilder sb) {
		if (!isSliderable()) {
			return;
		}

		if (isAnimating()) {
			sb.append(loc.getMenuDefault("PressSpaceStopAnimation",
					"Press space to stop animation"));
		} else {
			sb.append(loc.getMenuDefault("PressSpaceStartAnimation",
					"Press space to start animation"));
		}
		sb.endSentence();
		if (getIntervalMax() != getValue()) {
			sb.append(loc.getMenuDefault("PressUpToIncrease",
					"Press up arrow to increase the value"));
			sb.endSentence();
		}
		if (getIntervalMin() != getValue()) {
			sb.append(loc.getMenuDefault("PressDownToDecrease",
					"Press down arrow to decrease the value"));
			sb.endSentence();
		}
		super.addAuralOperations(loc, sb);

	}

	@Override
	public String getAuralTextForSpace() {
		if (!isSlider()) {
			return null;
		}

		Localization loc = kernel.getLocalization();
		ScreenReaderBuilder sb = new ScreenReaderBuilder(loc);
		if (isAnimating()) {

			// don't need this for stopping as the value is read out afterwards
			// anyway
			addAuralCaption(sb);
			sb.appendSpace();
			sb.append(loc.getMenuDefault("AnimationStarted",
					"animation is started"));
		} else {
			sb.append(loc.getMenuDefault("AnimationStopped", "animation is stopped"));
		}
		sb.endSentence();
		return sb.toString();
	}

	/**
	 * 
	 * @return the current value as readable, aural text.
	 */
	public String getAuralCurrentValue() {
		ScreenReaderBuilder sb = new ScreenReaderBuilder(kernel.getLocalization());
		addAuralSliderValue(sb);
		return sb.toString().trim();
	}

	@Override
	public Function getFunctionForRoot() {
		return getFunction();
	}

	@Override
	public double value(double x) {
		return value;
	}

	@Override
	public boolean isPolynomialFunction(boolean forRoot) {
		return true;
	}

	@Override
	public boolean showLineProperties() {
		return isDrawable() && !isSlider() && getDrawAlgorithm() != null;
	}

	/**
	 * Creates slider.
	 */
	public void createSlider() {
		isDrawable = true;
		setShowExtendedAV(true);
		initAlgebraSlider();
	}

	/**
	 * Removes the slider.
	 */
	public void removeSlider() {
		isDrawable = false;
		setShowExtendedAV(false);
		intervalMax = null;
		intervalMin = null;
		setEuclidianVisible(false);
	}

	@Override
	public String toLaTeXString(boolean symbolic, boolean symbolicContext, StringTemplate tpl) {
		return toLaTeXString(symbolic || symbolicContext, tpl);
	}
}
