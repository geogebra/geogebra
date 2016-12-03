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
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.AnimationManager;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.arithmetic.Evaluate2Var;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.cas.AlgoIntegralDefiniteInterface;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * 
 * @author Markus
 */
public class GeoNumeric extends GeoElement implements GeoNumberValue,
		AbsoluteScreenLocateable, GeoFunctionable, Animatable, HasExtendedAV,
		SymbolicParametersBotanaAlgo, HasSymbolicMode, AnimationExportSlider,
		Evaluate2Var {

	private Variable[] botanaVars;

	/** eg boxplot */
	public static final int DEFAULT_THICKNESS = 2;
	/** sliders */
	public static final int DEFAULT_SLIDER_THICKNESS = 10;

	private static final double AUTO_STEP_MUL = 0.0025;
	private static final double AUTO_STEP_MUL_ANGLE = 0.0025;
	/** placeholder for autostep */
	public static final double AUTO_STEP = Double.NaN;

	private static int DEFAULT_SLIDER_WIDTH_RW = 4;
	/** default slider width in pixels */
	public final static int DEFAULT_SLIDER_WIDTH_PIXEL = 200;
	/** 
	 * Default width of angle slider in pixels 
	 *  
	 * Should be a factor of 360 to work well 
	 * 72 gives increment of 5 degrees 
	 * 144 gives increment of 2.5 degrees (doesn't look good) 
	 * 180 gives increment of 2 degrees 
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
	private boolean intervalMinActive = false;
	private boolean intervalMaxActive = false;
	private NumberValue intervalMin;
	private NumberValue intervalMax;
	private double sliderWidth = this instanceof GeoAngle ? DEFAULT_SLIDER_WIDTH_PIXEL_ANGLE
			: DEFAULT_SLIDER_WIDTH_PIXEL;
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
	 * @param c construction
	 * @param setDefaults true to set from defaults
	 */
	public GeoNumeric(Construction c, boolean setDefaults) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		if (setDefaults)
			setConstructionDefaults(); // init visual settings

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
		return new GeoNumeric(cons, value);
	}

	@Override
	public void setZero() {
		setValue(0);
	}

	@Override
	public boolean isDrawable() {
		return isDrawable || (getDrawAlgorithm() != getParentAlgorithm())
				|| (isIndependent() && isLabelSet());
	}

	@Override
	public boolean isFillable() {
		return isDrawable;
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
		if (visible == isSetEuclidianVisible() || kernel.isMacroKernel())
			return;

		// slider is only possible for independent
		// number with given min and max
		if (isIndependent()) {
			if (visible) { // TODO: Remove cast from GeoNumeric
				GeoNumeric num = kernel.getAlgoDispatcher().getDefaultNumber(isAngle());
				// make sure the slider value is not fixed
				setFixed(false);
				if (!isIntervalMinActive() && !(intervalMin instanceof GeoNumeric)) {
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
		sliderPos = new SliderPosition();
		if (isAbsoluteScreenLocActive()) {
			sliderPos.x = 30;
			EuclidianViewInterfaceSlim ev = kernel.getApplication().getActiveEuclidianView();
			if(ev != null){
				sliderPos.y = ev.getSliderOffsetY() + 40 * count;
			}else{
				sliderPos.y = 50 + 40 * count;
			}
			// make sure slider is visible on screen
			sliderPos.y = (int) sliderPos.y / 400 * 10 + sliderPos.y % 400;
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
			if (num.isSlider())
				count++;
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
		if (!isDrawable()) {
			return false;
		}

		if (!isDefined()) {
			return false;
		}

		// Double.isNaN(value) is tested in isDefined()

		if (Double.isInfinite(value)) {
			return false;
		}

		if (intervalMin == null) {
			return true;
		}

		if (intervalMax == null) {
			return true;
		}

		if (!isIntervalMinActive()) {
			return false;
		}

		if (!isIntervalMaxActive()) {
			return false;
		}



		return (getIntervalMin() < getIntervalMax());
	}

	@Override
	public final boolean showInAlgebraView() {
		return true;
	}

	@Override
	public void set(GeoElementND geo) {
		NumberValue num = (NumberValue) geo;
		setValue(num.getDouble());
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
			if (aid.evaluateOnly())
				return true;
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
				if (value >= 0)
					strLaTeX = "\\infty";
				else
					strLaTeX = "-\\infty";
			} else {
				strLaTeX = toLaTeXString(false, StringTemplate.latexTemplate);
			}
		}
		return strLaTeX;
	}

	// Michael Borcherds 2008-04-30
	@Override
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type, otherwise use equals() method
		if (geo.isGeoNumeric()) {
			return Kernel.isEqual(value, ((GeoNumeric) geo).value);
		}
		return false;
	}

	@Override
	public double getAnimationStep() {


		if (getAnimationStepObject() == null) {
			GeoNumeric num = kernel.getAlgoDispatcher().getDefaultNumber(isGeoAngle());
			setAnimationStep(num.getAnimationStep());
		}

		if (isAutoStep()) {
			return getAutoStepValue();
		}

		return super.getAnimationStep();

	}

	private double getAutoStepValue() {
		if (intervalMin == null || intervalMax == null) {
			return isAngle() ? Math.PI / 180 : 0.05;
		}
		if(isAngle()){
			// default 360 *10/200 -> 2deg
			return MyMath.nextPrettyNumber(
					(intervalMax.getDouble() - intervalMin.getDouble())
							* getAnimationSpeed() * (180 / Math.PI)
							* AUTO_STEP_MUL_ANGLE, 0)
					* (Math.PI / 180);
		}
		return MyMath.nextPrettyNumber(
				(intervalMax.getDouble() - intervalMin.getDouble())
						* getAnimationSpeed() * AUTO_STEP_MUL, 0);
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
			GeoNumeric num = kernel.getAlgoDispatcher().getDefaultNumber(isGeoAngle());
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
	final public void setValue(double x) {
		setValue(x, true);
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
		if (Double.isNaN(x))
			value = Double.NaN;
		else if (isIntervalMinActive() && x < getIntervalMin()){
			value = getIntervalMin();
			if(getCorrespondingCasCell()!=null)
				getCorrespondingCasCell().setInputFromTwinGeo(true, false);
		}
		else if (isIntervalMaxActive() && x > getIntervalMax()){
			value = getIntervalMax();
			if(getCorrespondingCasCell()!=null)
				getCorrespondingCasCell().setInputFromTwinGeo(true, false);
		}
		else
			value = x;

		// remember value for animation also
		if (changeAnimationValue)
			animationValue = value;
	}

	/**
	 * Returns value of the number
	 * 
	 * @return number value
	 */
	final public double getValue() {
		return value;
	}

	@Override
	public String toString(StringTemplate tpl) {
		if (sbToString == null) {
			sbToString =  new StringBuilder(50);
		}
		
		// #4186
		if (tpl.hasCASType()) {
			return toValueString(tpl); 
		} 
		
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(toValueString(tpl));
		return sbToString.toString();
	}
	
	/**
	 * @return string representation for regression output
	 */
	final public String toStringMinimal() {
		if (sbToString == null) {
			sbToString =  new StringBuilder(50);
		}
		sbToString.setLength(0);
		sbToString.append(toValueStringMinimal());
		return sbToString.toString();
	}

	/**
	 * @return string representation of value for regression output
	 */
	public String toValueStringMinimal() {
		return regrFormat(value);
	}

	private StringBuilder sbToString;
	private ArrayList<GeoNumeric> minMaxListeners;
	private boolean randomSlider = false;

	private Double origSliderWidth = null;
	private Double origSliderX = null;
	private Double origSliderY = null;

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
				return "(ggbtmpvar" + label + ")";
			}
			//make sure random() works inside Sequence, see #3558
			if (this.isRandomGeo() && !this.isLabelSet()) {
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
		if (symbolicMode && getDefinition() != null
				&& tpl.supportsFractions()) {
			return getDefinition().toFractionString(tpl);
		}
		return kernel.format(value, tpl);
	}

	/**
	 * interface NumberValue
	 */
	public MyDouble getNumber() {
		return new MyDouble(kernel, value);
	}

	final public double getDouble() {
		return value;
	}

	@Override
	public void setAllVisualPropertiesExceptEuclidianVisible(GeoElement geo, boolean keepAdvanced) {
		super.setAllVisualPropertiesExceptEuclidianVisible(geo, keepAdvanced);

		if (geo.isGeoNumeric()) {
			isDrawable = ((GeoNumeric) geo).isDrawable;
		}
	}

	@Override
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);

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
		if (symbolicMode) {
			sb.append("\t<symbolic val=\"true\" />\n");
		}
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
	protected boolean isSliderable() {
		return isIndependent() && (isIntervalMinActive() || isIntervalMaxActive());
	}

	@Override
	public boolean isFixable() {
		// visible slider should not be fixable
		return !isSetEuclidianVisible() && !isDefaultGeo();
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
		if (!isSliderable())
			return;

		StringTemplate tpl = StringTemplate.xmlTemplate;
		sb.append("\t<slider");
		if (isIntervalMinActive() || intervalMin instanceof GeoNumeric) {
			sb.append(" min=\"");
			StringUtil
					.encodeXML(sb, getIntervalMinObject().getLabel(tpl));
			sb.append("\"");
		}
		if (isIntervalMaxActive() || intervalMax instanceof GeoNumeric) {
			sb.append(" max=\"");
			StringUtil
					.encodeXML(sb, getIntervalMaxObject().getLabel(tpl));
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
		setIntervalMaxActive(!Double.isNaN(max.getDouble()));
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
		setIntervalMinActive(!Double.isNaN(min.getDouble()));
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
	 */
	public final void setSliderWidth(double width) {
		if (width > 0 && !Double.isInfinite(width))
			if (getOrigSliderWidth() == null) {
				setOrigSliderWidth(width);
			}
			sliderWidth = width;
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
		if (!force && sliderFixed)
			return;
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
	public final double getIntervalMax() {
		return intervalMax.getDouble();
	}

	/**
	 * Returns minimal value for slider
	 * 
	 * @return minimal value for slider
	 */
	public final double getIntervalMin() {
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
		return intervalMaxActive;
	}

	/**
	 * Returns true if slider min value wasn't disabled in Properties
	 * 
	 * @return true if slider min value wasn't disabled
	 */
	public final boolean isIntervalMinActive() {
		return intervalMinActive;
	}

	/**
	 * Returns true iff slider is fixed in graphics view
	 * 
	 * @return true iff slider is fixed in graphics view
	 */
	public final boolean isSliderFixed() {
		return sliderFixed;
	}

	/**
	 * Sets whether slider is fixed in graphics view
	 * 
	 * @param isSliderFixed
	 *            true iff slider is fixed in graphics view
	 */
	public final void setSliderFixed(boolean isSliderFixed) {
		sliderFixed = isSliderFixed;
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

	public void setAbsoluteScreenLoc(int x, int y) {
		setSliderLocation(x, y, true);
	}

	public int getAbsoluteScreenLocX() {
		return sliderPos == null ? 0 : (int) sliderPos.x;
	}

	public int getAbsoluteScreenLocY() {
		return sliderPos == null ? 0 : (int) sliderPos.y;
	}

	public void setRealWorldLoc(double x, double y) {
		if (sliderPos == null) {
			sliderPos = new SliderPosition();
		}
		sliderPos.x = x;
		sliderPos.y = y;
	}

	public double getRealWorldLocX() {
		return sliderPos == null ? 0 : sliderPos.x;
	}

	public double getRealWorldLocY() {
		return sliderPos == null ? 0 : sliderPos.y;
	}

	public void setAbsoluteScreenLocActive(boolean flag) {
		hasAbsoluteScreenLocation = flag;
		if (flag)
			sliderWidth = this instanceof GeoAngle ? DEFAULT_SLIDER_WIDTH_PIXEL_ANGLE
					: DEFAULT_SLIDER_WIDTH_PIXEL;
		else
			sliderWidth = DEFAULT_SLIDER_WIDTH_RW;
	}

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
	 */
	public GeoFunction getGeoFunction() {
		ExpressionNode en = new ExpressionNode(kernel, this);
		Function fun = new Function(en, new FunctionVariable(kernel));
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
	public boolean isGeoFunctionable() {
		return true;
	}

	@Override
	public void doRemove() {
		super.doRemove();

		// if this was a random number, make sure it's removed
		cons.removeRandomGeo(this);
		if (intervalMin instanceof GeoNumeric)
			((GeoNumeric) intervalMin).unregisterMinMaxListener(this);
		if (intervalMax instanceof GeoNumeric)
			((GeoNumeric) intervalMax).unregisterMinMaxListener(this);
	}

	/**
	 * Given geo depends on this one (via min or max value for slider) and
	 * should be updated
	 * 
	 * @param geo
	 *            geo to be updated
	 */
	public void registerMinMaxListener(GeoNumeric geo) {
		if (minMaxListeners == null)
			minMaxListeners = new ArrayList<GeoNumeric>();
		minMaxListeners.add(geo);
	}

	/**
	 * Given geo no longer depends on this one (via min or max value for slider)
	 * and should not be updated any more
	 * 
	 * @param geo slider whose min/max is this numeric
	 */
	public void unregisterMinMaxListener(GeoNumeric geo) {
		if (minMaxListeners == null)
			minMaxListeners = new ArrayList<GeoNumeric>();
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
		if (random)
			cons.addRandomGeo(this);
		else
			cons.removeRandomGeo(this);
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
		return Kernel.checkDecimalFraction(Math.floor(kernel.getApplication().getRandomNumber() * n)
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
			for (EuclidianViewInterfaceSlim ev : evListeners)
				ev.updateBounds(true, true);
		}
	}

	private void resolveMinMax() {
		double oldValue = value;
		if (intervalMin == null || intervalMax == null)
			return;
		boolean okMin = !Double.isNaN(getIntervalMin())
				&& !Double.isInfinite(getIntervalMin());
		boolean okMax = !Double.isNaN(getIntervalMax())
				&& !Double.isInfinite(getIntervalMax());
		boolean ok = (getIntervalMin() <= getIntervalMax());
		setIntervalMinActive(ok && okMin);
		setIntervalMaxActive((ok && okMin && okMax)|| (getIntervalMin() == getIntervalMax() && okMin && okMax));
		if (ok && okMin && okMax) {
			setValue(isDefined() ? value : 1.0);
		} else if (okMin && okMax)
			setUndefined();
		if(oldValue!=value){
			updateCascade();
		}else{
			//we want to make the slider visible again if it was not
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
		return isIndependent() && isIntervalMinActive() && isIntervalMaxActive();
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
	 * @return whether the value of this number was changed
	 */
	final public synchronized boolean doAnimationStep(double frameRate) {
		// check that we have valid min and max values
		if (!isIntervalMinActive() || !isIntervalMaxActive())
			return false;

		// special case for random slider
		// animationValue goes from 0 to animationStep
		if (isRandom()) {

			double animationStep = getAnimationStep();

			// check not silly value
			if (animationValue < -2 * animationStep) {
				animationValue = 0;
			}

			double intervalWidth = getIntervalMax() - getIntervalMin();
			double step = intervalWidth
					* getAnimationSpeed()
					/ (AnimationManager.STANDARD_ANIMATION_TIME * frameRate);
			// update animation value
			if (Double.isNaN(animationValue) || animationValue < 0)
				animationValue = 0;
			animationValue = animationValue + Math.abs(step);

			if (animationValue > animationStep) {
				animationValue -= animationStep;
				setValue(getRandom(), false);
				return true;
			}

			// no update needed
			return false;
		}

		// remember old value of number to decide whether update is necessary
		double oldValue = getValue();

		// compute animation step based on speed and frame rates
		double intervalWidth = getIntervalMax() - getIntervalMin();
		double step = intervalWidth
				* getAnimationSpeed()
				* getAnimationDirection()
				/ (AnimationManager.STANDARD_ANIMATION_TIME * frameRate);

		// update animation value
		if (Double.isNaN(animationValue))
			animationValue = oldValue;
		animationValue = animationValue + step;

		// make sure we don't get outside our interval
		switch (getAnimationType()) {
		case GeoElement.ANIMATION_DECREASING:
		case GeoElement.ANIMATION_INCREASING:
			// jump to other end of slider
			if (animationValue > getIntervalMax())
				animationValue = animationValue - intervalWidth;
			else if (animationValue < getIntervalMin())
				animationValue = animationValue + intervalWidth;
			break;

		case GeoElement.ANIMATION_INCREASING_ONCE:
			// stop if outside range
			if (animationValue > getIntervalMax()) {
				setAnimating(false);
				boolean changed = getIntervalMax() != value;
				setValue(getIntervalMax(), false);
				return changed;
			} else if (animationValue < getIntervalMin()) {
				setAnimating(false);
				setValue(getIntervalMin(), false);
				return true;
			}
			break;

		case GeoElement.ANIMATION_OSCILLATING:
		default:
			if (animationValue >= getIntervalMax()) {
				animationValue = getIntervalMax();
				changeAnimationDirection();
			} else if (animationValue <= getIntervalMin()) {
				animationValue = getIntervalMin();
				changeAnimationDirection();
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
			newValue = Kernel.checkDecimalFraction(newValue);
		}

		// change slider's value without changing animationValue
		setValue(newValue, false);

		// return whether value of slider has changed
		return getValue() != oldValue;
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
				public int compare(GeoNumberValue itemA, GeoNumberValue itemB) {

					double comp = itemA.getDouble() - itemB.getDouble();
					if (Kernel.isZero(comp)) {
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

	private static volatile Comparator<GeoNumberValue> comparator;

	// protected void setRandomNumber(boolean flag) {
	// isRandomNumber = flag;
	// }

	// public boolean isRandomNumber() {
	// return isRandomNumber;
	// }

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
	 * @param value new min for this slider
	 */
	public void setIntervalMin(double value) {
		setIntervalMin(new MyDouble(kernel, value));
	}

	/**
	 * Set interval max
	 * 
	 * @param value new max for this slider
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
		if (intervalMin == null)
			return null;
		return intervalMin;
	}

	/**
	 * Get interval max as geo
	 * 
	 * @return interval max
	 */
	public NumberValue getIntervalMaxObject() {
		if (intervalMax == null)
			return null;
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

	private ArrayList<EuclidianViewInterfaceSlim> evListeners = null;

	/**
	 * @param ev euclidian view which listens to this numeric
	 */
	public void addEVSizeListener(EuclidianViewInterfaceSlim ev) {
		if (evListeners == null)
			evListeners = new ArrayList<EuclidianViewInterfaceSlim>();

		if (!evListeners.contains(ev))
			evListeners.add(ev);
	}

	/**
	 * @param ev euclidian view which listens to this numeric
	 */
	public void removeEVSizeListener(EuclidianViewInterfaceSlim ev) {
		if (evListeners != null)
			evListeners.remove(ev);
	}

	@Override
	public void moveDependencies(GeoElement oldGeo) {
		if (!oldGeo.isGeoNumeric())
			return;
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
				if (slider.getIntervalMaxObject() == num)
					slider.setIntervalMax(this);
				if (slider.getIntervalMinObject() == num)
					slider.setIntervalMin(this);
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
	public void addToSpreadsheetTraceList(ArrayList<GeoNumeric> spreadsheetTraceList) {
		GeoNumeric xx = this.copy(); // should handle GeoAngle
		// too
		spreadsheetTraceList.add(xx);

	}

	private void setIntervalMinActive(boolean intervalMinActive) {
		this.intervalMinActive = intervalMinActive;
	}
	private void setIntervalMaxActive(boolean intervalMaxActive) {
		this.intervalMaxActive = intervalMaxActive;
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
	public static GeoNumeric setSliderFromDefault(GeoNumeric num, boolean isAngle) {
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
		GeoNumeric defaultNum = num.getKernel().getAlgoDispatcher().getDefaultNumber(false);           
		GeoNumeric defaultAngleOrNum = num.getKernel().getAlgoDispatcher().getDefaultNumber(isAngle);           
		num.setSliderFixed(defaultNum.isSliderFixed());         
		num.setEuclidianVisible(visible);
		num.setIntervalMin(defaultAngleOrNum.getIntervalMinObject());
		num.setIntervalMax(defaultAngleOrNum.getIntervalMaxObject());
		num.setAnimationStep(defaultAngleOrNum.getAnimationStep());
		num.setAutoStep(defaultAngleOrNum.isAutoStep());
		num.setAbsoluteScreenLocActive(true);
		num.setAnimationType(defaultNum.getAnimationType());
		num.setSliderWidth(defaultAngleOrNum.getSliderWidth());
		num.setRandom(defaultNum.isRandom());
		num.setLineThickness(DEFAULT_SLIDER_THICKNESS);
		num.setDrawable(false,false);
		num.update();
		return num;
	}
	
	@Override
	final public HitType getLastHitType(){
		return HitType.ON_BOUNDARY;
	}

	private boolean showExtendedAV = true;


	@Override
	public boolean isShowingExtendedAV() {
		return showExtendedAV;
	}

	@Override
	public void setShowExtendedAV(boolean showExtendedAV) {
		this.showExtendedAV = showExtendedAV;
	}
	
	
	@Override
	protected void setLabelModeDefault(){
		
		// label visibility
		App app = getKernel().getApplication();
		int labelingStyle = app == null ? ConstructionDefaults.LABEL_VISIBLE_USE_DEFAULTS : app
				.getCurrentLabelingStyle();
		
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
				
		if (visible){
			labelMode = LABEL_NAME_VALUE;
		}else{
			labelMode = LABEL_VALUE;
		}

	}

	public Variable[] getBotanaVars(GeoElementND geo) {
		if (algoParent != null
				&& algoParent instanceof SymbolicParametersBotanaAlgo) {
			return ((SymbolicParametersBotanaAlgo) algoParent)
					.getBotanaVars(this);
		}

		if (algoParent == null) {
			if (botanaVars == null) {
				botanaVars = new Variable[2];
				botanaVars[0] = new Variable(true);
				botanaVars[1] = new Variable(true);
				Log.debug("Free point " + geo.getLabelSimple() + "("
						+ botanaVars[0] + "," + botanaVars[1] + ")");
			}
		}

		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (algoParent != null
				&& algoParent instanceof SymbolicParametersBotanaAlgo) {
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

	public void setSymbolicMode(boolean mode, boolean update) {
		this.symbolicMode = mode;
	}

	public boolean isSymbolicMode() {
		return symbolicMode;
	}

	@Override
	public boolean needToShowBothRowsInAV() {
		return super.needToShowBothRowsInAV()
				|| (getDefinition() != null && getDefinition().isFraction());
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

	public double evaluate(double x, double y) {
		return value;
	}

	public ExpressionNode getFunctionExpression() {
		return getGeoFunction().getFunctionExpression();
	}

	public FunctionNVar getFunction() {
		return getGeoFunction().getFunction();
	}

	public String getVarString(StringTemplate defaulttemplate) {
		return "x";
	}
}