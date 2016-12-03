package org.geogebra.common.main.settings;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;

/**
 * Settings for an euclidian view. To which view these settings are associated
 * is determined in {@link Settings}.
 */
public class EuclidianSettings extends AbstractSettings {
	public static final int[] DELETE_SIZES = { 20, 40, 80 };

	/**
	 * Color of the euclidian view's background.
	 */
	protected GColor backgroundColor;

	/**
	 * Color of the axes.
	 */
	private GColor axesColor;

	/**
	 * Color of the grid lines.
	 */
	private GColor gridColor;

	/**
	 * Line style of axes.
	 */
	private int axesLineStyle = EuclidianStyleConstants.AXES_LINE_TYPE_ARROW;

	/**
	 * Line style of grid.
	 */
	private int gridLineStyle = EuclidianStyleConstants.LINE_TYPE_FULL;

	/**
	 * Various distances between lines of the grid.
	 */
	double[] gridDistances = null;// { 2, 2, Math.PI/6 };

	// we need 3 values for 3D view, as it may copy values from ev1
	private final double[] axisCross = { 0, 0, 0 };
	private final boolean[] positiveAxes = { false, false, false };
	private final boolean[] drawBorderAxes = { false, false, false };
	private NumberValue xminObject, xmaxObject, yminObject, ymaxObject;

	private int tooltipsInThisView = EuclidianStyleConstants.TOOLTIPS_AUTOMATIC;

	private GDimension sizeFromFile, size;

	protected App app;

	// settings for the base EuclidianView (or null if this is the base)
	//private final EuclidianSettings euclidianSettings1;

	public EuclidianSettings(App app, EuclidianSettings euclidianSettings1) {
		//this.euclidianSettings1 = euclidianSettings1;
		xZero = EuclidianView.XZERO_STANDARD; // needs to be positive
		yZero = EuclidianView.YZERO_STANDARD; // needs to be positive
		preferredSize = AwtFactory.getPrototype().newDimension(0, 0);
		this.app = app;
		resetNoFire();
	}

	/*
	 * some settings are not stored in XML, eg eg automaticGridDistance so we
	 * need to clear these parameters to make sure the others are set OK see
	 * EuclidianView.settingsChanged()
	 */
	public void reset() {
		resetNoFire();
		settingChanged();
	}

	protected void resetNoFire() {
		sizeFromFile = AwtFactory.getPrototype().newDimension(0, 0);
		size = null;

		gridDistances = null;
		// length might be 2 or 3
		for (int i = 0; i < axisNumberingDistances.length; i++) {
			axisNumberingDistances[i] = null;
		}

		xminObject = null;
		xmaxObject = null;
		yminObject = null;
		ymaxObject = null;

		setGridLineStyle(EuclidianStyleConstants.LINE_TYPE_FULL);
		setAxesLineStyle(EuclidianStyleConstants.AXES_LINE_TYPE_ARROW);
		setAxesColor(GColor.BLACK);
		setGridColor(GColor.LIGHT_GRAY);
		setBackground(GColor.WHITE);

		pointCapturingMode = EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC;

		// length might be 2 or 3
		for (int i = 0; i < showAxesNumbers.length; i++) {
			showAxesNumbers[i] = true;
		}

		// length might be 2 or 3
		for (int i = 0; i < axesLabels.length; i++) {
			axesLabels[i] = null;
		}

		// length might be 2 or 3
		for (int i = 0; i < axesUnitLabels.length; i++) {
			axesUnitLabels[i] = null;
		}

		// length might be 2 or 3
		for (int i = 0; i < piAxisUnit.length; i++) {
			piAxisUnit[i] = false;
		}

		// length might be 2 or 3
		for (int i = 0; i < axesTickStyles.length; i++) {
			axesTickStyles[i] = EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR;
		}

		// for axes labeling with numbers
		// length might be 2 or 3
		for (int i = 0; i < automaticAxesNumberingDistances.length; i++) {
			automaticAxesNumberingDistances[i] = true;
		}

		// length might be 2 or 3
		for (int i = 0; i < automaticAxesNumberingDistances.length; i++) {
			automaticAxesNumberingDistances[i] = true;
		}

		// distances between grid lines
		automaticGridDistance = true;

		// length might be 2 or 3
		for (int i = 0; i < axisCross.length; i++) {
			axisCross[i] = 0;
		}

		// length might be 2 or 3
		for (int i = 0; i < positiveAxes.length; i++) {
			positiveAxes[i] = false;
		}

		// length might be 2 or 3
		for (int i = 0; i < selectionAllowed.length; i++) {
			selectionAllowed[i] = true;
		}

		// length might be 2 or 3
		for (int i = 0; i < showAxes.length; i++) {
			showAxes[i] = true;
		}

		showGrid = false;

		axisFontStyle = GFont.PLAIN;
		axesLabelsSerif = false;
	}

	/**
	 * Change background color.
	 * 
	 * @param col
	 */
	public void setBackground(GColor col) {
		if (!col.equals(backgroundColor)) {
			backgroundColor = col;
			settingChanged();
		}
	}

	/**
	 * @return background color
	 */
	public GColor getBackground() {
		return backgroundColor;
	}

	/**
	 * Change axes color.
	 * 
	 * @param col
	 */
	public void setAxesColor(GColor col) {
		if (!col.equals(axesColor)) {
			axesColor = col;
			settingChanged();
		}
	}

	/**
	 * @return axes color
	 */
	public GColor getAxesColor() {
		return axesColor;
	}

	/**
	 * Change grid color.
	 * 
	 * @param col
	 */
	public void setGridColor(GColor col) {
		if (!col.equals(gridColor)) {
			gridColor = col;
			settingChanged();
		}
	}

	/**
	 * @return color of the grid
	 */
	public GColor getGridColor() {
		return gridColor;
	}

	/**
	 * Change line style of axes.
	 * 
	 * @param style
	 */
	public void setAxesLineStyle(int style) {
		if (axesLineStyle != style) {
			axesLineStyle = style;
			settingChanged();
		}
	}

	/**
	 * @return line style of axes
	 */
	public int getAxesLineStyle() {
		return axesLineStyle;
	}

	/**
	 * Change line style of grid.
	 * 
	 * @param style
	 */
	public void setGridLineStyle(int style) {
		if (gridLineStyle != style) {
			gridLineStyle = style;
			settingChanged();
		}
	}

	/**
	 * @return line style of grid
	 */
	public int getGridLineStyle() {
		return gridLineStyle;
	}

	/**
	 * Change grid distances.
	 * 
	 * @param dists
	 */
	public void setGridDistances(double[] dists) {
		boolean changed = false;

		if (gridDistances == null) {
			changed = true;
		} else if (gridDistances.length != dists.length) {
			changed = true;
		} else {
			for (int i = 0; i < dists.length; ++i) {
				if (dists[i] != gridDistances[i]) {
					changed = true;
					break;
				}
			}
		}

		if (changed) {
			gridDistances = dists;
			if (dists == null) {
				setAutomaticGridDistance(true, false);
			} else {
				setAutomaticGridDistance(false, false);
			}
			settingChanged();
		}
	}

	/**
	 * @return grid distances
	 */
	public double[] getGridDistances() {
		return gridDistances;
	}

	public void setAutomaticGridDistance(boolean agd, boolean callsc) {
		if (automaticGridDistance != agd) {
			automaticGridDistance = agd;

			if (agd) {
				gridDistances = null;
				if (callsc) {
					settingChanged();
				}
			} else if (callsc) {
				settingChanged();
			}
		}
	}

	public boolean getAutomaticGridDistance() {
		return automaticGridDistance;
	}

	protected boolean[] showAxes = { true, true, true };
	protected boolean[] selectionAllowed = { true, true, true };
	protected boolean[] showAxesNumbers = { true, true, true };
	protected boolean[] logAxes = { false, false, false };
	protected String[] axesLabels = { null, null, null };

	protected String[] axesUnitLabels = { null, null, null };

	protected boolean[] piAxisUnit = { false, false, false };

	protected int[] axesTickStyles = {
			EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR,
			EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR,
			EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR };

	// for axes labeling with numbers
	protected boolean[] automaticAxesNumberingDistances = { true, true, true };

	protected GeoNumberValue axisNumberingDistances[] = new GeoNumeric[] {
			null,
			null,
			null };

	// distances between grid lines
	protected boolean automaticGridDistance = true;

	protected double xZero;

	protected double yZero;

	protected double xscale = EuclidianView.SCALE_STANDARD;

	protected double yscale = EuclidianView.SCALE_STANDARD;

	private GDimension preferredSize;

	private boolean showGrid;

	protected boolean gridIsBold;

	private int gridType;

	private int pointCapturingMode = EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC;

	// set to false because it was set to false in Desktop anyway
	// (due to a bug in MyXMLHandler?), and it does some speedup in Web
	private boolean allowShowMouseCoords = false;

	private double lockedAxesRatio = -1;

	private int deleteToolSize = EuclidianConstants.DEFAULT_ERASER_SIZE;

	private int axisFontStyle = GFont.PLAIN;

	private boolean axesLabelsSerif = false;

	private Boolean enabled = null;

	public boolean getAllowShowMouseCoords() {
		return allowShowMouseCoords;
	}

	public void setAllowShowMouseCoords(boolean neverShowMouseCoords) {
		if (neverShowMouseCoords == this.allowShowMouseCoords) {
			return;
		}
		this.allowShowMouseCoords = neverShowMouseCoords;
		settingChanged();
	}

	/*
	 * change visibility of axes
	 */
	public boolean setShowAxis(int axis, boolean flag) {
		boolean changed = flag != showAxes[axis];

		if (changed) {
			showAxes[axis] = flag;
			settingChanged();
		}
		return changed;
	}

	/*
	 * change logarithmic of axes
	 */
	public boolean setLogAxis(int axis, boolean flag) {
		boolean changed = flag != logAxes[axis];

		if (changed) {
			logAxes[axis] = flag;
			settingChanged();
		}
		return changed;
	}

	/**
	 * says if the axis is shown or not
	 * 
	 * @param axis
	 *            id of the axis
	 * @return if the axis is shown
	 */
	public boolean getShowAxis(int axis) {
		return showAxes[axis];
	}
	
	public boolean getLogAxis(int axis){
		return logAxes[axis];
	}

	/**
	 * sets the axis label to axisLabel
	 * 
	 * @param axis
	 * @param axisLabel
	 */
	public void setAxisLabel(int axis, String axisLabel) {
		boolean changed = false;
		if ((axisLabel == null) || (axisLabel.length() == 0)) {
			changed = axesLabels[axis] != null;
			axesLabels[axis] = null;
		} else {
			changed = axesLabels[axis] != null ? !axesLabels[axis]
					.equals(axisLabel) : true;
			changed = !axisLabel.equals(axesLabels[axis]);
			axesLabels[axis] = axisLabel;
		}

		if (changed) {
			settingChanged();
		}

	}

	public String[] getAxesLabels() {
		return axesLabels;
	}

	public String[] getAxesUnitLabels() {
		return axesUnitLabels;
	}

	public void setAxesUnitLabels(String[] axesUnitLabels) {
		this.axesUnitLabels = axesUnitLabels;

		// check if pi is an axis unit
		for (int i = 0; i < 2; i++) {
			piAxisUnit[i] = (axesUnitLabels[i] != null)
					&& axesUnitLabels[i].equals(Unicode.PI_STRING);
		}
		// setAxesIntervals(xscale, 0);
		// setAxesIntervals(yscale, 1);

		settingChanged();
	}

	public void setShowAxisNumbers(int axis, boolean showAxisNumbers) {
		showAxesNumbers[axis] = showAxisNumbers;
		settingChanged();
	}

	public boolean[] getShowAxisNumbers() {
		return showAxesNumbers;
	}

	public GeoNumberValue getAxisNumberingDistance(int i) {
		return axisNumberingDistances[i];
	}

	public void setAxisNumberingDistance(int i, GeoNumberValue dist) {
		axisNumberingDistances[i] = dist;

		setAutomaticAxesNumberingDistance(false, i, false);

		settingChanged();
	}

	public void setAutomaticAxesNumberingDistance(boolean flag, int axis,
			boolean callsc) {

		if (automaticAxesNumberingDistances[axis] != flag) {
			automaticAxesNumberingDistances[axis] = flag;

			if (flag) {
				axisNumberingDistances[axis] = null;
				if (callsc) {
					settingChanged();
				}
			} else if (callsc) {
				settingChanged();
			}
		}
	}

	public boolean getAutomaticAxesNumberingDistance(int axis) {
		return automaticAxesNumberingDistances[axis];
	}

	public int[] getAxesTickStyles() {
		return axesTickStyles;
	}

	public void setAxisTickStyle(int axis, int tickStyle) {

		if (axesTickStyles[axis] != tickStyle) {
			axesTickStyles[axis] = tickStyle;
			settingChanged();
		}
	}

	public double[] getAxesCross() {
		return axisCross;
	}

	public void setAxisCross(int axis, double cross) {
		if (axisCross[axis] != cross) {
			axisCross[axis] = cross;
			settingChanged();
		}
	}

	public boolean[] getPositiveAxes() {
		return positiveAxes;
	}

	// for xml handler
	public void setPositiveAxis(int axis, boolean isPositiveAxis) {
		if (positiveAxes[axis] == isPositiveAxis) {
			return;
		}
		positiveAxes[axis] = isPositiveAxis;
		settingChanged();
	}

	/**
	 * @return the xminObject
	 */
	public GeoNumeric getXminObject() {
		return (GeoNumeric) xminObject;
	}

	/**
	 * @param xminObjectNew
	 *            the xminObject to set
	 * @param callsc
	 *            whether settingChanged should be called
	 */
	public void setXminObject(NumberValue xminObjectNew, boolean callsc) {
		this.xminObject = xminObjectNew;
		if (callsc) {
			settingChanged();
		}
	}

	/**
	 * @return the xmaxObject
	 */
	public GeoNumeric getXmaxObject() {
		return (GeoNumeric) xmaxObject;
	}

	/**
	 * @param xmaxObjectNew
	 *            the xmaxObject to set
	 * @param callsc
	 *            whether settingChanged should be called
	 */
	public void setXmaxObject(NumberValue xmaxObjectNew, boolean callsc) {
		this.xmaxObject = xmaxObjectNew;
		if (callsc) {
			settingChanged();
		}
	}

	/**
	 * @return the yminObject
	 */
	public GeoNumeric getYminObject() {
		return (GeoNumeric) yminObject;
	}

	/**
	 * @param yminObjectNew
	 *            the yminObject to set
	 * @param callsc
	 *            whether settingChanged should be called
	 */
	public void setYminObject(NumberValue yminObjectNew, boolean callsc) {
		this.yminObject = yminObjectNew;
		if (callsc) {
			settingChanged();
		}
	}

	/**
	 * @return the ymaxObject
	 */
	public GeoNumeric getYmaxObject() {
		return (GeoNumeric) ymaxObject;
	}

	/**
	 * @param ymaxObjectNew
	 *            the ymaxObject to set
	 * @param callsc
	 *            whether settingChanged should be called
	 */
	public void setYmaxObject(NumberValue ymaxObjectNew, boolean callsc) {
		this.ymaxObject = ymaxObjectNew;
		if (callsc) {
			settingChanged();
		}
	}

	/**
	 * Returns x coordinate of axes origin.
	 */
	public double getXZero() {
		return xZero;
	}

	/**
	 * Returns y coordinate of axes origin.
	 */
	public double getYZero() {
		return yZero;
	}

	/**
	 * Returns xscale of this view. The scale is the number of pixels in screen
	 * space that represent one unit in user space.
	 */
	public double getXscale() {
		return xscale;
	}

	/**
	 * Returns the yscale of this view. The scale is the number of pixels in
	 * screen space that represent one unit in user space.
	 */
	public double getYscale() {
		return yscale;
	}

	public boolean hasDynamicBounds() {
		return xminObject != null && yminObject != null && xmaxObject != null
				&& ymaxObject != null;
	}

	public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale, boolean fire) {
		if (Double.isNaN(xscale) || (xscale < Kernel.MAX_DOUBLE_PRECISION)
				|| (xscale > Kernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}
		if (Double.isNaN(yscale) || (yscale < Kernel.MAX_DOUBLE_PRECISION)
				|| (yscale > Kernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}

		this.xZero = xZero;
		this.yZero = yZero;
		this.xscale = xscale;
		this.yscale = yscale;
		if(fire){
			settingChanged();
		}

	}

	public void setAxesNumberingDistance(GeoNumberValue tickDist, int axis) {

		setAxisNumberingDistance(axis, tickDist);

		settingChanged();
	}

	public void setPreferredSize(GDimension dimension) {
		preferredSize = dimension;
		settingChanged();

	}

	public GDimension getPreferredSize() {
		return preferredSize;
	}

	public boolean setShowAxes(boolean x, boolean y) {
		boolean changedX = this.setShowAxis(0, x);
		return this.setShowAxis(1, y) || changedX;
		// settingChanged() is called from those above

	}

	public boolean setShowAxes(boolean flag) {
		boolean changed = this.setShowAxis(0, flag);
		changed = this.setShowAxis(1, flag) || changed;
		return this.setShowAxis(2, flag) || changed;
		// settingChanged() is called from those above

	}

	public boolean setLogAxes(boolean x, boolean y) {
		boolean changedX = this.setLogAxis(0, x);
		return this.setLogAxis(1, y) || changedX;
		// settingChanged() is called from those above

	}

	public boolean setLogAxes(boolean flag) {
		boolean changed = this.setLogAxis(0, flag);
		changed = this.setLogAxis(1, flag) || changed;
		return this.setLogAxis(2, flag) || changed;
		// settingChanged() is called from those above

	}

	public boolean showGrid(boolean show) {
		if (show == showGrid) {
			return false;
		}
		setShowGridSetting(show);
		settingChanged();
		return true;
	}

	public void setShowGridSetting(boolean show) {
		showGrid = show;
	}

	public boolean getShowGrid() {
		return showGrid;
	}

	public boolean getGridIsBold() {
		return gridIsBold;
	}

	public void setGridIsBold(boolean gridIsBold) {
		if (this.gridIsBold == gridIsBold) {
			return;
		}

		this.gridIsBold = gridIsBold;

		settingChanged();
	}

	final public int getGridType() {
		return gridType;
	}

	/**
	 * Set grid type.
	 */
	public void setGridType(int type) {
		if (gridType == type) {
			return;
		}
		gridType = type;
		settingChanged();
	}

	/**
	 * Returns point capturing mode.
	 */
	final public int getPointCapturingMode() {
		return pointCapturingMode;
	}

	/**
	 * Set capturing of points to the grid.
	 * 
	 * @return true if setting changed
	 */
	public boolean setPointCapturing(int mode) {

		if (pointCapturingMode == mode) {
			return false;
		}
		pointCapturingMode = mode;
		settingChanged();
		return false;
	}

	public void setAllowToolTips(int setto) {
		if (setto == tooltipsInThisView) {
			return;
		}
		tooltipsInThisView = setto;
		settingChanged();
	}

	final public int getAllowToolTips() {
		return tooltipsInThisView;
	}

	public void setDrawBorderAxes(int axis, boolean value) {
		if ((axis == 0) || (axis == 1)) {
			if (drawBorderAxes[axis] == value)
				return;
			drawBorderAxes[axis] = value;
			settingChanged();
		}
	}

	final public boolean[] getDrawBorderAxes() {
		return drawBorderAxes;
	}

	public void setLockedAxesRatio(double ratio) {
		if (Kernel.isEqual(lockedAxesRatio, ratio)) {
			return;
		}
		lockedAxesRatio = ratio;
		settingChanged();
	}

	public double getLockedAxesRatio() {
		return lockedAxesRatio;
	}

	public void setBoldAxes(boolean bold) {
		int oldAxesLineStyle = axesLineStyle;
		axesLineStyle = EuclidianView.getBoldAxes(bold, axesLineStyle);

		if (oldAxesLineStyle != axesLineStyle) {
			settingChanged();
		}
	}

	public int getDeleteToolSize() {
		return this.deleteToolSize;
	}

	public void setDeleteToolSize(int size) {
		this.deleteToolSize = size;
	}

	public void addAxisXML(int i, StringBuilder sbxml) {
		sbxml.append("\t<axis id=\"");
		sbxml.append(i);
		sbxml.append("\" show=\"");
		sbxml.append(getShowAxis(i));
		sbxml.append("\" label=\"");
		if (axesLabels[i] != null) {
			StringUtil.encodeXML(sbxml, axisLabelForXML(i));
		}
		sbxml.append("\" unitLabel=\"");
		if (axesUnitLabels[i] != null) {
			StringUtil.encodeXML(sbxml, axesUnitLabels[i]);
		}
		sbxml.append("\" tickStyle=\"");
		sbxml.append(axesTickStyles[i]);
		sbxml.append("\" showNumbers=\"");
		sbxml.append(showAxesNumbers[i]);

		// the tick distance should only be saved if
		// it isn't calculated automatically
		if (!automaticAxesNumberingDistances[i]
				&& axisNumberingDistances[i] != null) {
			sbxml.append("\" tickDistance=\"");
			sbxml.append(axisNumberingDistances[i].getDouble());
			sbxml.append("\" tickExpression=\"");
			sbxml.append(axisNumberingDistances[i]
					.getDefinition(StringTemplate.xmlTemplate));
		}

		// axis crossing values
		if (drawBorderAxes[i]) {
			sbxml.append("\" axisCrossEdge=\"");
			sbxml.append(true);
		} else if (!Kernel.isZero(axisCross[i]) && !drawBorderAxes[i]) {
			sbxml.append("\" axisCross=\"");
			sbxml.append(axisCross[i]);
		}

		// positive direction only flags
		if (positiveAxes[i]) {
			sbxml.append("\" positiveAxis=\"");
			sbxml.append(positiveAxes[i]);
		}

		// selection allowed flags
		if (!selectionAllowed[i]) {
			sbxml.append("\" selectionAllowed=\"");
			sbxml.append(selectionAllowed[i]);
		}

		sbxml.append("\"/>\n");
	}

	/**
	 * Returns axis label including &lt;b> and &lt;i>
	 * 
	 * @param i
	 *            index of axis (0 for x, 1 for y)
	 * @return axis label including formating tags
	 */
	public String axisLabelForXML(int i) {
		return axesLabels[i];
	}

	final public void setXscale(double scale) {
		if (this.xscale != scale) {
			setXscaleValue(scale);
			settingChanged();
		}
	}

	protected void setXscaleValue(double scale) {
		this.xscale = scale;
	}

	final public void setYscale(double scale) {
		if (this.yscale != scale) {
			setYscaleValue(scale);
			settingChanged();
		}

	}

	protected void setYscaleValue(double scale) {
		this.yscale = scale;
	}

	/**
	 * @return if it's about 3D
	 */
	public boolean is3D() {
		return false;
	}

	/**
	 * 
	 * @return if it's a view for plane
	 */
	public boolean isViewForPlane() {
		return false;
	}
	
	/**
	 * convert screen coordinate x to real world coordinate x
	 * 
	 * @param x
	 *            screen coord
	 * @return real world equivalent of screen x-coord
	 */
	final public double toRealWorldCoordX(double x) {
		return (x - xZero) / xscale;
	}

	/**
	 * convert screen coordinate y to real world coordinate y
	 * 
	 * @param y
	 *            screen coord
	 * @return real world equivalent of screen y-coord
	 */
	final public double toRealWorldCoordY(double y) {
		return (yZero - y) / yscale;
	}

	/**
	 * Axis font style
	 * 
	 * @return eg GFont.PLAIN + GFont.BOLD
	 */
	public int getAxisFontStyle() {
		// #5320
		return axisFontStyle ;
	}

	/**
	 * @return whether axes labels are in serif font
	 */
	public boolean getAxesLabelsSerif() {
		return axesLabelsSerif;
	}

	public void setAxesLabelsSerif(boolean b) {
		if (axesLabelsSerif != b) {
			axesLabelsSerif = b;
			settingChanged();
		}
	}

	public void setAxisFontStyle(int style) {

		if (axisFontStyle != style) {
			axisFontStyle = style;
			settingChanged();
		}
	}

	/**
	 * set size from XML file. Reset size value
	 * 
	 * @param newDimension
	 */
	public void setSizeFromFile(GDimension newDimension) {
		this.sizeFromFile = newDimension;
		this.size = null;
	}


	public void setSize(int w, int h) {
		size = AwtFactory.getPrototype().newDimension(w, h);
	}

	public void setOriginNoUpdate(double xZero, double yZero){
		this.xZero = xZero;
		this.yZero = yZero;
	}

	/**
	 * @return width from XML
	 */
	public int getFileWidth() {
		return sizeFromFile.getWidth();
	}

	/**
	 * @return height from XML
	 */
	public int getFileHeight() {
		return sizeFromFile.getHeight();
	}

	/**
	 * @return width
	 */
	public int getWidth() {
		if (size == null) {
			return getFileWidth();
		}
		return size.getWidth();
	}

	/**
	 * @return height
	 */
	public int getHeight() {
		if (size == null) {
			return getFileHeight();
		}
		return size.getHeight();
	}

	/**
	 * @param axis
	 *            axis index
	 * @param flag
	 *            whether to allow selection
	 * @return whether setting was changed
	 */
	public boolean setSelectionAllowed(int axis, boolean flag) {
		boolean changed = flag != selectionAllowed[axis];

		if (changed) {
			selectionAllowed[axis] = flag;
			settingChanged();
		}
		return changed;
	}

	/**
	 * @param axisNo
	 *            axis index
	 * @return whether axis selection is allowed
	 */
	public boolean isSelectionAllowed(int axisNo) {
		return selectionAllowed[axisNo];
	}

	/**
	 * @param enable
	 *            whether this view is enabled (for 3D only)
	 */
	public void setEnabled(boolean enable) {
		if (enabled == null || enabled != enable) {
			this.enabled = enable;
			settingChanged();
		}
	}

	/**
	 * reset 3d enable (needed for exam mode)
	 */
	public void resetEnabled() {
		enabled = null;
	}

	/**
	 * @return whether this view is enabled
	 */
	public boolean isEnabled() {
		return enabled == null || enabled;
	}

	/**
	 * @return whether this view was explicitly disabled
	 */
	public boolean isEnabledSet() {
		return enabled != null;
	}

}
