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

package geogebra.common.kernel.geos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.kernel.AnimationManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoDependentNumber;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.cas.AlgoIntegralDefiniteInterface;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * 
 * @author Markus
 */
public class GeoNumeric extends GeoElement implements GeoNumberValue,
		AbsoluteScreenLocateable, GeoFunctionable, Animatable {

	private static int DEFAULT_SLIDER_WIDTH_RW = 4;
	private static int DEFAULT_SLIDER_WIDTH_PIXEL = 100;
	private static int DEFAULT_SLIDER_WIDTH_PIXEL_ANGLE = 72;
	/** Default maximum value when displayed as slider */
	public static double DEFAULT_SLIDER_MIN = -5;
	/** Default minimum value when displayed as slider */
	public static double DEFAULT_SLIDER_MAX = 5;
	/** Default increment when displayed as slider */
	public static double DEFAULT_SLIDER_INCREMENT = 0.1;
	/** Default increment when displayed as slider */
	public static double DEFAULT_SLIDER_SPEED = 1;

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
	private double sliderX, sliderY;
	private boolean sliderFixed = false;
	private boolean sliderHorizontal = true;
	private double animationValue = Double.NaN;

	/** absolute screen location, true by default */
	boolean hasAbsoluteScreenLocation = true;

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
	public String getClassName() {
		return "GeoNumeric";
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_SLIDER;
	}

	@Override
	public String getTypeString() {
		return "Numeric";
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.NUMERIC;
	}

	/**
	 * Creates new labeled number
	 * 
	 * @param c
	 *            Cons
	 * @param label
	 *            Label for new number
	 * @param x
	 *            Number value
	 */
	public GeoNumeric(Construction c, String label, double x) {
		this(c, x);
		setLabel(label);
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
	public GeoElement copy() {
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
	public void setDrawable(boolean flag) {
		isDrawable = flag;
		if (isDrawable && kernel.isNotifyViewsActive()
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
						double min = Math.min(num.getIntervalMin(),
								Math.floor(value));
						double max = Math.max(num.getIntervalMax(),
								Math.ceil(value));
						setIntervalMin(new MyDouble(kernel, min));
						setIntervalMax(new MyDouble(kernel, max));
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
				if (sliderX == 0 && sliderY == 0) {
					int count = countSliders();

					if (isAbsoluteScreenLocActive()) {
						sliderX = 30;
						sliderY = 50 + 40 * count;
						// make sure slider is visible on screen
						sliderY = (int) sliderY / 400 * 10 + sliderY % 400;
					} else {
						sliderX = -5;
						sliderY = 10 - count;
					}
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

	private int countSliders() {
		int count = 0;

		// get all number and angle sliders
		TreeSet<GeoElement> numbers = cons
				.getGeoSetLabelOrder(GeoClass.NUMERIC);
		TreeSet<GeoElement> angles = cons.getGeoSetLabelOrder(GeoClass.ANGLE);
		if (numbers != null) {
			if (angles != null)
				numbers.addAll(angles);
		} else {
			numbers = angles;
		}

		if (numbers != null) {
			Iterator<GeoElement> it = numbers.iterator();
			while (it.hasNext()) {
				GeoNumeric num = (GeoNumeric) it.next();
				if (num.isSlider())
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
		return isDrawable()
				&& isDefined()
				&& (intervalMin == null || intervalMax == null || (isIntervalMinActive() && isIntervalMaxActive()));
	}

	@Override
	public final boolean showInAlgebraView() {
		return true;
	}

	@Override
	public void set(GeoElement geo) {
		NumberValue num = (NumberValue) geo;
		setValue(num.getDouble());
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
				strLaTeX = app.getPlain("undefined");
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
		return super.getAnimationStep();
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
	public void setValue(double x, boolean changeAnimationValue) {
		if (Double.isNaN(x))
			value = Double.NaN;
		else if (isIntervalMinActive() && x < getIntervalMin()){
			value = getIntervalMin();
			if(getCorrespondingCasCell()!=null)
				getCorrespondingCasCell().setInputFromTwinGeo(true);
		}
		else if (isIntervalMaxActive() && x > getIntervalMax()){
			value = getIntervalMax();
			if(getCorrespondingCasCell()!=null)
				getCorrespondingCasCell().setInputFromTwinGeo(true);
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
		if (sbToString == null)
			return null;
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
		if (sbToString == null)
			return null;
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

	private StringBuilder sbToString = new StringBuilder(50);
	private ArrayList<GeoNumeric> minMaxListeners;
	private boolean randomSlider = false;

	@Override
	public String toValueString(StringTemplate tpl) {
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
	final public boolean isConstant() {
		return false;
	}

	@Override
	final public boolean isLeaf() {
		return true;
	}

	@Override
	final public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> varset = new HashSet<GeoElement>();
		varset.add(this);
		return varset;
	}

	@Override
	public void setAllVisualProperties(GeoElement geo, boolean keepAdvanced) {
		super.setAllVisualProperties(geo, keepAdvanced);

		if (geo.isGeoNumeric()) {
			isDrawable = ((GeoNumeric) geo).isDrawable;
		}
	}

	@Override
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);

		if (geo.isGeoNumeric()) {
			slopeTriangleSize = ((GeoNumeric) geo).slopeTriangleSize;
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
		return !isSetEuclidianVisible();
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
		sb.append("\" x=\"");
		sb.append(sliderX);
		sb.append("\" y=\"");
		sb.append(sliderY);
		sb.append("\" fixed=\"");
		sb.append(sliderFixed);
		sb.append("\" horizontal=\"");
		sb.append(sliderHorizontal);
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

	@Override
	public boolean isVectorValue() {
		return false;
	}

	@Override
	public boolean isPolynomialInstance() {
		return false;
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

	@Override
	public boolean isTextValue() {
		return false;
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
		sliderX = x;
		sliderY = y;
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
		return sliderX;
	}

	/**
	 * Returns y-coord of the slider
	 * 
	 * @return y-coord of the slider
	 */
	public final double getSliderY() {
		return sliderY;
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
		return (int) sliderX;
	}

	public int getAbsoluteScreenLocY() {
		return (int) sliderY;
	}

	public void setRealWorldLoc(double x, double y) {
		sliderX = x;
		sliderY = y;
	}

	public double getRealWorldLocX() {
		return sliderX;
	}

	public double getRealWorldLocY() {
		return sliderY;
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
			// don't create a label for the new dependent function
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			// TODO remove cast
			ret = kernel.getAlgoDispatcher().DependentFunction(null, fun);
			cons.setSuppressLabelCreation(oldMacroMode);
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

	/*
	 * returns a random number in the slider's range (and using step-size)
	 */
	private double getRandom() {
		double min = getIntervalMin();
		double max = getIntervalMax();
		double increment = getAnimationStep();
		int n = 1 + (int) Math.round((max - min) / increment);
		return Kernel.checkDecimalFraction(Math.floor(app.getRandomNumber() * n)
				* increment + min);
	}

	@Override
	public void update() {
		super.update();
		if (minMaxListeners != null) {
			for (int i = 0; i < minMaxListeners.size(); i++) {
				GeoNumeric geo = minMaxListeners.get(i);
				geo.resolveMinMax();
			}
		}
		if (evListeners != null)
			for (EuclidianViewInterfaceSlim ev : evListeners)
				ev.updateBounds();
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
		setIntervalMaxActive(ok && okMax);
		if (ok && okMin && okMax)
			setValue(isDefined() ? value : 1.0);
		else if (okMin && okMax)
			setUndefined();
		if(oldValue!=value)
			updateCascade();
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
				setValue(getIntervalMax(), false);
				return true;
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

	private static Comparator<GeoNumberValue> comparator;

	// protected void setRandomNumber(boolean flag) {
	// isRandomNumber = flag;
	// }

	// public boolean isRandomNumber() {
	// return isRandomNumber;
	// }

	@Override
	final public void updateRandomGeo() {
		// set random value (for numbers used in trees using random())
		setValue(app.getRandomNumber());

		super.updateRandomGeo();
	}

	@Override
	public boolean isVector3DValue() {
		return false;
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
	public GeoElement getIntervalMinObject() {
		if (intervalMin == null)
			return null;
		return intervalMin.toGeoElement();
	}

	/**
	 * Get interval max as geo
	 * 
	 * @return interval max
	 */
	public GeoElement getIntervalMaxObject() {
		if (intervalMax == null)
			return null;
		return intervalMax.toGeoElement();
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
	public boolean isLaTeXDrawableGeo(String latexStr) {
		return false;
	}

	@Override
	public void addToSpreadsheetTraceList(ArrayList<GeoNumeric> spreadsheetTraceList) {
		GeoNumeric xx = (GeoNumeric) this.copy(); // should handle GeoAngle
		// too
		spreadsheetTraceList.add(xx);

	}
	
	@Override
	public boolean isPinnable() {
		return false;
	}
	
	@Override
	public boolean contains(final ExpressionValue ev) {
		
		if (getParentAlgorithm() instanceof AlgoDependentNumber) {
			// needed in eg CmdSequence, see #2552
			AlgoDependentNumber algo = (AlgoDependentNumber)getParentAlgorithm();
			return algo.getExpression().contains(ev);
		}
		return ev == this;
	}
	private void setIntervalMinActive(boolean intervalMinActive) {
		this.intervalMinActive = intervalMinActive;
	}
	private void setIntervalMaxActive(boolean intervalMaxActive) {
		this.intervalMaxActive = intervalMaxActive;
	}



}