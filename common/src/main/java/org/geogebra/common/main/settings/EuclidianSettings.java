package org.geogebra.common.main.settings;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.StringUtil;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Settings for an euclidian view. To which view these settings are associated
 * is determined in {@link Settings}.
 */
public class EuclidianSettings extends AbstractSettings {

	private static final String[] DEFAULT_AXIS_LABELS = { "x", "y", "z" };
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
	 * Line style of ruler.
	 */
	private int rulerLineStyle = EuclidianStyleConstants.LINE_TYPE_FULL;

	/**
	 * Various distances between lines of the grid.
	 */
	double[] gridDistances = null; // { 2, 2, Math.PI/6 };

	// we need 3 values for 3D view, as it may copy values from ev1
	private final double[] axisCross = { 0, 0, 0 };
	private final boolean[] positiveAxes = { false, false, false };
	private final boolean[] drawBorderAxes = { false, false, false };
	private NumberValue xminObject;
	private NumberValue xmaxObject;
	private NumberValue yminObject;
	private NumberValue ymaxObject;

	private int tooltipsInThisView = EuclidianStyleConstants.TOOLTIPS_AUTOMATIC;

	private GDimension sizeFromFile;
	private GDimension size;

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

	protected GeoNumberValue[] axisNumberingDistances = new GeoNumeric[] { null,
			null, null };

	// distances between grid lines
	protected boolean automaticGridDistance = true;

	protected double xZero;

	protected double yZero;

	protected double xscale = EuclidianView.SCALE_STANDARD;

	protected double yscale = EuclidianView.SCALE_STANDARD;

	private GDimension preferredSize;

	private boolean showGrid;

	protected boolean gridIsBold;

	private boolean rulerBold = false;

	private int gridType = EuclidianView.GRID_CARTESIAN;

	private int pointCapturingMode = EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC;

	// set to false because it was set to false in Desktop anyway
	// (due to a bug in MyXMLHandler?), and it does some speedup in Web
	private boolean allowShowMouseCoords = false;

	private double lockedAxesRatio = -1;

	private int deleteToolSize = EuclidianConstants.DEFAULT_ERASER_SIZE;

	private int axisFontStyle = GFont.PLAIN;

	private boolean axesLabelsSerif = false;

	private ExtendedBoolean enabledEB = ExtendedBoolean.UNKNOWN;

	@Weak
	protected App app;
	protected int dimension;

	private BackgroundType backgroundType = BackgroundType.RULER;

	private double bgRulerGap = 50;

	private GColor bgRulerColor;
	private GColor bgSubLineColor;
	private double fileXZero;
	private double fileYZero;
	private double fileXScale;
	private double fileYScale;

	private int defaultLabelingStyle = ConstructionDefaults.LABEL_VISIBLE_NOT_SET;

	private GColor lastSelectedPenColor = GColor.BLACK;
	private GColor lastSelectedHighlighterColor = GColor.MOW_GREEN;
	private int lastPenThickness = EuclidianConstants.DEFAULT_PEN_SIZE;
	private int lastHighlighterThinckness = EuclidianConstants.DEFAULT_HIGHLIGHTER_SIZE;

	/**
	 * @param app
	 *            application
	 */
	public EuclidianSettings(App app) {
		// this.euclidianSettings1 = euclidianSettings1;
		xZero = EuclidianView.XZERO_STANDARD; // needs to be positive
		yZero = EuclidianView.YZERO_STANDARD; // needs to be positive
		preferredSize = AwtFactory.getPrototype().newDimension(0, 0);
		this.app = app;
		dimension = 2;
		resetNoFire();
	}

	/**
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

		gridLineStyle = EuclidianStyleConstants.LINE_TYPE_FULL;
		axesLineStyle = EuclidianStyleConstants.AXES_LINE_TYPE_ARROW;
		axesColor = GColor.BLACK;
		gridColor = GColor.LIGHT_GRAY;
		backgroundColor = GColor.WHITE;
		backgroundType = BackgroundType.NONE;
		setBgRulerColorNoFire(GColor.MOW_RULER);
		gridType = EuclidianView.GRID_CARTESIAN_WITH_SUBGRID;

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
	 *            background color
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
	 *            axes color
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
	 *            grid color
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
	 *            axes style
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
	 *            grid style
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
	 *            grid distances for x, y
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

	/**
	 * @param agd
	 *            automatic grid sistance
	 * @param fire
	 *            whether to notify listeners
	 */
	public void setAutomaticGridDistance(boolean agd, boolean fire) {
		if (automaticGridDistance != agd) {
			automaticGridDistance = agd;

			if (agd) {
				gridDistances = null;
			}
			if (fire) {
				settingChanged();
			}
		}
	}

	/**
	 * @return whether grid distance is automatic
	 */
	public boolean getAutomaticGridDistance() {
		return automaticGridDistance;
	}

	/**
	 * @return whether mouse coords are allowed
	 */
	public boolean getAllowShowMouseCoords() {
		return allowShowMouseCoords;
	}

	/**
	 * @param neverShowMouseCoords
	 *            whther to show mouse coordinates
	 */
	public void setAllowShowMouseCoords(boolean neverShowMouseCoords) {
		if (neverShowMouseCoords == this.allowShowMouseCoords) {
			return;
		}
		this.allowShowMouseCoords = neverShowMouseCoords;
		settingChanged();
	}

	/**
	 * change visibility of axes
	 * 
	 * @param axis
	 *            axis
	 * @param flag
	 *            whether it should be visible
	 * @return whether setting changed
	 */
	public boolean setShowAxis(int axis, boolean flag) {
		boolean changed = setShowAxisNoFireSettingChanged(axis, flag);
		if (changed) {
			settingChanged();
		}
		return changed;
	}

	/**
	 * change visibility of axes
	 * 
	 * @param axis
	 *            axis
	 * @param flag
	 *            whether it should be visible
	 * @return whether setting changed
	 */
	public boolean setShowAxisNoFireSettingChanged(int axis, boolean flag) {
		boolean changed = flag != showAxes[axis];

		if (changed) {
			showAxes[axis] = flag;
		}
		return changed;
	}

	/**
	 * change logarithmic of axes
	 * 
	 * @param axis
	 *            axis
	 * @param flag
	 *            whether it should be logarithmic
	 * @return whether setting changed
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
	
	public boolean axisShown() {
		return showAxes[0] && showAxes[1];
	}

	public boolean[] getShowAxes() {
		return showAxes;
	}

	public boolean getLogAxis(int axis) {
		return logAxes[axis];
	}

	/**
	 * sets the axis label to axisLabel
	 * 
	 * @param axis
	 *            axis
	 * @param axisLabel
	 *            label
	 */
	public void setAxisLabel(int axis, String axisLabel) {
		setAxisLabel(axis, axisLabel, true);
	}

	/**
	 * sets the axis label to axisLabel
	 * 
	 * @param axis
	 *            axis
	 * @param axisLabel
	 *            label
	 * @param fireSettingsChanged
	 *            whether to notify listeners
	 * @return whether settings changed
	 */
	public boolean setAxisLabel(int axis, String axisLabel, boolean fireSettingsChanged) {
		boolean changed = false;
		if (StringUtil.empty(axisLabel)) {
			changed = axesLabels[axis] != null;
			axesLabels[axis] = null;
		} else {
			changed = !axisLabel.equals(axesLabels[axis]);
			axesLabels[axis] = axisLabel;
		}

		if (changed && fireSettingsChanged) {
			settingChanged();
		}

		return changed;
	}

	public String[] getAxesLabels() {
		return axesLabels;
	}

	public String[] getAxesUnitLabels() {
		return axesUnitLabels;
	}

	/**
	 * Change units.
	 * 
	 * @param axesUnitLabels
	 *            unit labels
	 */
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

	/**
	 * @param axis
	 *            axis index
	 * @param showAxisNumbers
	 *            whether to show numbers
	 */
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
		setAxisNumberingDistance(i, dist, true);
	}

	/**
	 * @param i
	 *            axis
	 * @param dist
	 *            distance
	 * @param fireSettingChanged
	 *            whether to notify listeners
	 */
	public void setAxisNumberingDistance(int i, GeoNumberValue dist, boolean fireSettingChanged) {
		axisNumberingDistances[i] = dist;
		setAutomaticAxesNumberingDistance(false, i, false);
		if (fireSettingChanged) {
			settingChanged();
		}
	}

	/**
	 * @param flag
	 *            automatic distance flag
	 * @param axis
	 *            axis
	 * @param callsc
	 *            whether to notify listeners
	 */
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

	public boolean[] getAutomaticAxesNumberingDistances() {
		return automaticAxesNumberingDistances;
	}

	public int[] getAxesTickStyles() {
		return axesTickStyles;
	}

	/**
	 * Set minor or major ticks.
	 * 
	 * @param axis
	 *            axis
	 * @param tickStyle
	 *            EuclidianStyleConstants.AXES_TICK_STYLE_* constant
	 */
	public void setAxisTickStyle(int axis, int tickStyle) {

		if (axesTickStyles[axis] != tickStyle) {
			axesTickStyles[axis] = tickStyle;
			settingChanged();
		}
	}

	public double[] getAxesCross() {
		return axisCross;
	}

	/**
	 * @param axis
	 *            axis
	 * @param cross
	 *            cross cordinate
	 */
	public void setAxisCross(int axis, double cross) {
		if (axisCross[axis] != cross) {
			axisCross[axis] = cross;
			settingChanged();
		}
	}

	/**
	 * @return whether each axis is only shown in positive direction from cross
	 */
	public boolean[] getPositiveAxes() {
		return positiveAxes;
	}

	/**
	 * Set axes to be shown in positive direction from cross.
	 * 
	 * @param axis
	 *            axis index
	 * @param isPositiveAxis
	 *            whether to only show positive
	 * 
	 */
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
	 * @return x coordinate of axes origin.
	 */
	public double getXZero() {
		return xZero;
	}

	/**
	 * @return y coordinate of axes origin.
	 */
	public double getYZero() {
		return yZero;
	}

	/**
	 * The scale is the number of pixels in screen space that represent one unit
	 * in user space.
	 * 
	 * @return xscale of this view.
	 */
	public double getXscale() {
		return xscale;
	}

	/**
	 * The scale is the number of pixels in screen space that represent one unit
	 * in user space.
	 * 
	 * @return the yscale of this view.
	 */
	public double getYscale() {
		return yscale;
	}

	/**
	 * @return whether dynamic bounds exist
	 */
	public boolean hasDynamicBounds() {
		return xminObject != null && yminObject != null && xmaxObject != null
				&& ymaxObject != null;
	}

	/**
	 * @param xZero
	 *            x-coord of the origin
	 * @param yZero
	 *            y-coord of the origin
	 * @param xscale
	 *            x scale
	 * @param yscale
	 *            y scale
	 * @param fire
	 *            whether to notify listeners
	 */
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
		if (fire) {
			settingChanged();
		}
	}

	/**
	 * Stores the original coordinate system
	 * 
	 * @param xZero
	 *            x-coord of the origin
	 * @param yZero
	 *            y-coord of the origin
	 * @param xscale
	 *            x scale
	 * @param yscale
	 *            y scale
	 */
	public void setFileCoordSystem(double xZero, double yZero, double xscale, double yscale) {
		fileXZero = xZero;
		fileYZero = yZero;
		fileXScale = xscale;
		fileYScale = yscale;

	}

	/**
	 * @param tickDist
	 *            tick distance
	 * @param axis
	 *            axis index
	 * @param fireSettingChanged
	 *            whether to notify listeners
	 */
	public void setAxesNumberingDistance(GeoNumberValue tickDist, int axis,
			boolean fireSettingChanged) {
		setAxisNumberingDistance(axis, tickDist, fireSettingChanged);
	}

	/**
	 * @param tickDist
	 *            tick distance
	 * @param axis
	 *            axis
	 */
	public void setAxesNumberingDistance(GeoNumberValue tickDist, int axis) {
		setAxesNumberingDistance(tickDist, axis, true);
	}

	/**
	 * @param dimension
	 *            preferred view size
	 */
	public void setPreferredSize(GDimension dimension) {
		preferredSize = dimension;
		settingChanged();

	}

	public GDimension getPreferredSize() {
		return preferredSize;
	}

	/**
	 * @param x
	 *            whether to show xAxis
	 * @param y
	 *            whether to show yAxis
	 * @return whether settings changed
	 */
	public boolean setShowAxes(boolean x, boolean y) {
		boolean changedX = setShowAxisNoFireSettingChanged(0, x);
		changedX = setShowAxisNoFireSettingChanged(1, y) || changedX;
		if (changedX) {
			settingChanged();
		}
		return changedX;
	}

	/**
	 * @param flag
	 *            whether to show axes
	 * @return whether setting changed
	 */
	public boolean setShowAxes(boolean flag) {
		boolean changed = setShowAxisNoFireSettingChanged(0, flag);
		changed = setShowAxisNoFireSettingChanged(1, flag) || changed;
		setShowAxisNoFireSettingChanged(2, false);
		if (changed) {
			settingChanged();
			if (app.getConfig().hasPreviewPoints()) {
				app.getSpecialPointsManager().updateSpecialPoints(null);
			}
		}
		return changed;
	}

	/**
	 * @param x
	 *            xAxis logarithmic?
	 * @param y
	 *            yAxis logarithmic?
	 * @return whether settings changed
	 */
	public boolean setLogAxes(boolean x, boolean y) {
		boolean changedX = this.setLogAxis(0, x);
		return this.setLogAxis(1, y) || changedX;
		// settingChanged() is called from those above
	}

	/**
	 * @param show
	 *            whether to show grid
	 * @return whether settings changed
	 */
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

	/**
	 * @param gridIsBold
	 *            bold grid?
	 */
	public void setGridIsBold(boolean gridIsBold) {
		if (this.gridIsBold == gridIsBold) {
			return;
		}
		this.gridIsBold = gridIsBold;
		settingChanged();
	}

	/**
	 * @return grid type
	 */
	final public int getGridType() {
		return gridType;
	}

	/**
	 * Set grid type: cartesian, polar, isometric, ....
	 * 
	 * @param type
	 *            one fof EuclidianView.GRID_* constants
	 */
	public void setGridType(int type) {
		if (gridType == type) {
			return;
		}
		// make sure the grid flag is also changed, for point capturing
		if (type == EuclidianView.GRID_NOT_SHOWN) {
			this.showGrid = false;
		}
		gridType = type;
		settingChanged();
	}

	/**
	 * Returns point capturing mode.
	 * 
	 * @return EuclidianStyleConstants.POINT_CAPTURING_*
	 */
	final public int getPointCapturingMode() {
		return pointCapturingMode;
	}

	/**
	 * Set capturing of points to the grid.
	 * 
	 * @param mode
	 *            EuclidianStyleConstants.POINT_CAPTURING_*
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

	/**
	 * @param setto
	 *            whether to allow tooltips
	 */
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

	/**
	 * @param axis
	 *            axis
	 * @param value
	 *            whether axis should stick to border
	 */
	public void setDrawBorderAxes(int axis, boolean value) {
		if ((axis == 0) || (axis == 1)) {
			if (drawBorderAxes[axis] == value) {
				return;
			}
			drawBorderAxes[axis] = value;
			settingChanged();
		}
	}

	/**
	 * @return whether each axis is sticking to border
	 */
	final public boolean[] getDrawBorderAxes() {
		return drawBorderAxes;
	}

	/**
	 * @param ratio
	 *            x:y ratio; ratio &lt; 0 means unlocked
	 */
	public void setLockedAxesRatio(double ratio) {
		if (DoubleUtil.isEqual(lockedAxesRatio, ratio)) {
			return;
		}
		lockedAxesRatio = ratio;
		settingChanged();
	}

	/**
	 * @return axes ratio if locked; number &lt; 1 otherwise
	 */
	public double getLockedAxesRatio() {
		return lockedAxesRatio;
	}

	/**
	 * @param bold
	 *            bold axes
	 */
	public void setBoldAxes(boolean bold) {
		int oldAxesLineStyle = axesLineStyle;
		axesLineStyle = EuclidianView.getBoldAxes(bold, axesLineStyle);

		if (oldAxesLineStyle != axesLineStyle) {
			settingChanged();
		}
	}

	/**
	 * @return delete tool size
	 */
	public int getDeleteToolSize() {
		return this.deleteToolSize;
	}

	/**
	 * @param size
	 *            delete tool size
	 */
	public void setDeleteToolSize(int size) {
		this.deleteToolSize = size;
	}

	/**
	 * @param i
	 *            axis index
	 * @param sbxml
	 *            xml builder
	 */
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
					.getLabel(StringTemplate.xmlTemplate));
		}

		// axis crossing values
		if (drawBorderAxes[i]) {
			sbxml.append("\" axisCrossEdge=\"");
			sbxml.append(true);
		} else if (!DoubleUtil.isZero(axisCross[i]) && !drawBorderAxes[i]) {
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
	 * Adds ruler XML
	 * 
	 * @param sbxml
	 *            xml builder
	 */
	public void addRulerXML(StringBuilder sbxml) {
		sbxml.append("\t<ruler type=\"");
		sbxml.append(backgroundType.value());
		sbxml.append("\" color=\"");
		sbxml.append(backgroundType.value());
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

	/**
	 * @param scale
	 *            screen : RW ratio for x-coordinate
	 */
	final public void setXscale(double scale) {
		if (this.xscale != scale) {
			setXscaleValue(scale);
			settingChanged();
		}
	}

	protected void setXscaleValue(double scale) {
		this.xscale = scale;
	}

	/**
	 * @param scale
	 *            screen : RW ratio for y-coordinate
	 */
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
		return axisFontStyle;
	}

	/**
	 * @return whether axes labels are in serif font
	 */
	public boolean getAxesLabelsSerif() {
		return axesLabelsSerif;
	}

	/**
	 * @param serif
	 *            whether to use serif font
	 */
	public void setAxesLabelsSerif(boolean serif) {
		if (axesLabelsSerif != serif) {
			axesLabelsSerif = serif;
			settingChanged();
		}
	}

	/**
	 * @param style
	 *            axes font style
	 */
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
	 *            size from file
	 */
	public void setSizeFromFile(GDimension newDimension) {
		this.sizeFromFile = newDimension;
		this.size = null;
	}

	/**
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	public void setSize(int w, int h) {
		size = AwtFactory.getPrototype().newDimension(w, h);
	}

	/**
	 * @param xZero
	 *            origin screen x-coord
	 * @param yZero
	 *            origin screen y-coord
	 */
	public void setOriginNoUpdate(double xZero, double yZero) {
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
		if (enabledEB == ExtendedBoolean.UNKNOWN
				|| enabledEB.boolVal() != enable) {
			this.enabledEB = ExtendedBoolean.newExtendedBoolean(enable);
			settingChanged();
		}
	}

	/**
	 * reset 3d enable (needed for exam mode)
	 */
	public void resetEnabled() {
		enabledEB = ExtendedBoolean.UNKNOWN;
	}

	/**
	 * @return whether this view is enabled
	 */
	public boolean isEnabled() {
		// UNKNOWN / TRUE -> true
		return enabledEB != ExtendedBoolean.FALSE;
	}

	/**
	 * @return the dimensionality of the settings arrays
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * @param i
	 *            axis index
	 * @return default label
	 */
	public static String getDefaultAxisLabel(int i) {
		return DEFAULT_AXIS_LABELS[i];
	}

	/**
	 * 
	 * @return the background type for MOW
	 */
	public BackgroundType getBackgroundType() {
		return backgroundType;
	}

	/**
	 * Sets the background type for MOW.
	 * 
	 * @param backgroundType
	 *            {@link BackgroundType}
	 */
	public void setBackgroundType(BackgroundType backgroundType) {
		this.backgroundType = backgroundType;
		settingChanged();
	}

	/**
	 * 
	 * @return the y gap between two ruler lines
	 */
	public double getBackgroundRulerGap() {
		return bgRulerGap;
	}

	/**
	 * Sets the y gap between two ruler lines
	 * 
	 * @param backgroundRulerGap
	 *            to set.
	 */
	public void setBackgroundRulerGap(double backgroundRulerGap) {
		this.bgRulerGap = backgroundRulerGap;
	}

	/**
	 * 
	 * @return the ruler main color
	 */
	public GColor getBgRulerColor() {
		return bgRulerColor;
	}

	/**
	 * @return color of sub lines
	 */
	public GColor getBgSubLineColor() {
		return bgSubLineColor;
	}

	/**
	 * Set color of lines
	 * Color sub lines is derived from that.
	 * 
	 * @param color
	 *            color of main lines.
	 */
	public void setBgRulerColor(GColor color) {
		setBgRulerColorNoFire(color);
		settingChanged();
	}

	private void setBgRulerColorNoFire(GColor color) {
		this.bgRulerColor = color;
		bgSubLineColor = GColor.getSubGridColor(color);
	}

	/**
	 * 
	 * @return if ruler lines are bold.
	 */
	public boolean isRulerBold() {
		return rulerBold;
	}

	/**
	 * 
	 * @param rulerBold
	 *            to set.
	 */
	public void setRulerBold(boolean rulerBold) {
		this.rulerBold = rulerBold;
		settingChanged();
	}

	/**
	 * 
	 * @return the ruler linestyle
	 */
	public int getRulerLineStyle() {
		return rulerLineStyle;
	}

	/**
	 * Sets the ruler line style
	 * 
	 * @param rulerLineStyle
	 *            ruler line style
	 */
	public void setRulerLineStyle(int rulerLineStyle) {
		if (this.rulerLineStyle != rulerLineStyle) {
			this.rulerLineStyle = rulerLineStyle;
			settingChanged();
		}
	}

	/**
	 * 
	 * @param rulerType
	 *            type as int.
	 */
	public void setRulerType(int rulerType) {
		setBackgroundType(BackgroundType.fromInt(rulerType));
	}

	/**
	 * 
	 * @return the original x zero that comes from file.
	 */
	public double getFileXZero() {
		return fileXZero;
	}

	/**
	 * 
	 * @return the original y zero that comes from file.
	 */
	public double getFileYZero() {
		return fileYZero;
	}

	/**
	 * 
	 * @return the original x scale that comes from file.
	 */
	public double getFileXScale() {
		return fileXScale;
	}

	/**
	 * 
	 * @return the original y scale that comes from file.
	 */
	public double getFileYScale() {
		return fileYScale;
	}

	public int getDefaultLabelingStyle() {
		return defaultLabelingStyle;
	}

	public void setDefaultLabelingStyle(int labelingStyle) {
		this.defaultLabelingStyle = labelingStyle;
	}

	/**
	 * @return last selected pen color
	 */
	public GColor getLastSelectedPenColor() {
		return lastSelectedPenColor;
	}

	/**
	 * @param lastSelectedPenColor
	 *            update last selected pen color
	 */
	public void setLastSelectedPenColor(GColor lastSelectedPenColor) {
		this.lastSelectedPenColor = lastSelectedPenColor;
	}

	/**
	 * @return last selected highlighter color
	 */
	public GColor getLastSelectedHighlighterColor() {
		return lastSelectedHighlighterColor;
	}

	/**
	 * @param lastSelectedHighlighterColor
	 *            update last selected highlighter color
	 */
	public void setLastSelectedHighlighterColor(GColor lastSelectedHighlighterColor) {
		this.lastSelectedHighlighterColor = lastSelectedHighlighterColor;
	}

	/**
	 * @return last selected size for pen
	 */
	public int getLastPenThickness() {
		return lastPenThickness;
	}

	/**
	 * @param lastPenThickness size of pen
	 */
	public void setLastPenThickness(int lastPenThickness) {
		this.lastPenThickness = lastPenThickness;
	}

	/**
	 * @return last selected size of highlighter
	 */
	public int getLastHighlighterThinckness() {
		return lastHighlighterThinckness;
	}

	/**
	 * @param lastHighlighterThinckness size of highlighter
	 */
	public void setLastHighlighterThinckness(int lastHighlighterThinckness) {
		this.lastHighlighterThinckness = lastHighlighterThinckness;
	}
}
