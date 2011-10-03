package geogebra.main.settings;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.util.Unicode;

import java.awt.Color;
import java.awt.Dimension;

/**
 * Settings for an euclidian view. To which view these settings are associated
 * is determined in {@link Settings}.
 */
public class EuclidianSettings extends AbstractSettings {
	/**
	 * Color of the euclidian view's background.
	 */
	private Color backgroundColor;
	
	/**
	 * Color of the axes.
	 */
	private Color axesColor;
	
	/**
	 * Color of the grid lines.
	 */
	private Color gridColor;

	/**
	 * Line style of axes.
	 */
	private int axesLineStyle = EuclidianView.AXES_LINE_TYPE_ARROW;
	
	/**
	 * Line style of grid.
	 */
	private int gridLineStyle = EuclidianView.LINE_TYPE_DASHED_SHORT;
	
	/**
	 * Various distances between lines of the grid.
	 */
	double[] gridDistances = null;//{ 2, 2, Math.PI/6 };
	
	private double[] axisCross = {0,0};
	private boolean[] positiveAxes = {false, false};
	private boolean[] drawBorderAxes = {false,false};
	NumberValue xminObject, xmaxObject, yminObject, ymaxObject;
	
	private int tooltipsInThisView = EuclidianView.TOOLTIPS_AUTOMATIC;

	
	/* 
	 * some settings are not stored in XML, eg
	 * 	eg automaticGridDistance
	 * so we need to clear these parameters
	 * to make sure the others are set OK
	 * see EuclidianView.settingsChanged()
	 */
	public void reset() {
		gridDistances = null;
		axisNumberingDistanceX = Double.NaN;
		axisNumberingDistanceY = Double.NaN;

		setGridLineStyle(EuclidianView.LINE_TYPE_DASHED_SHORT);
		setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_ARROW);
		setAxesColor(Color.black); // Michael Borcherds 2008-01-26 was darkgray
		setGridColor(Color.lightGray);
		setBackground(Color.white);

		pointCapturingMode = EuclidianView.POINT_CAPTURING_AUTOMATIC;
			
		showAxesNumbers[0] = true;
		showAxesNumbers[1] = true;
		axesLabels[0] = null;
		axesLabels[1] = null;
		axesUnitLabels[0] = null;
		axesUnitLabels[1] = null;
		piAxisUnit[0] = false;
		piAxisUnit[1] = false;
		axesTickStyles[0] = EuclidianView.AXES_TICK_STYLE_MAJOR;
		axesTickStyles[1] = EuclidianView.AXES_TICK_STYLE_MAJOR;

		// for axes labeling with numbers
		automaticAxesNumberingDistances[0] = true;
		automaticAxesNumberingDistances[1] = true;
		automaticAxesNumberingDistances[2] = true;

		// distances between grid lines
		automaticGridDistance = true;
		
		axisCross[0] = 0;
		axisCross[1] = 0;
		positiveAxes[0] = false;
		positiveAxes[1] = false;
}
	
	/**
	 * Change background color.
	 * @param col 
	 */
	public void setBackground(Color col) {
		if(!col.equals(backgroundColor)) {
			backgroundColor = col;
			settingChanged();
		}
	}
	
	/**
	 * @return background color
	 */
	public Color getBackground() {
		return backgroundColor;
	}
	
	/**
	 * Change axes color.
	 * @param col
	 */
	public void setAxesColor(Color col) {
		if(!col.equals(axesColor)) {
			axesColor = col;
			settingChanged();
		}
	} 
	
	/**
	 * @return axes color
	 */
	public Color getAxesColor() {
		return axesColor;
	}

	/**
	 * Change grid color.
	 * @param col
	 */
	public void setGridColor(Color col) {
		if(!col.equals(gridColor)) {
			gridColor = col;
			settingChanged();
		}
	}
	
	/**
	 * @return color of the grid
	 */
	public Color getGridColor() {
		return gridColor;
	}

	/**
	 * Change line style of axes.
	 * @param style
	 */
	public void setAxesLineStyle(int style) {
		if(axesLineStyle != style) {
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
	 * @param style
	 */
	public void setGridLineStyle(int style) {
		if(gridLineStyle != style) {
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
	 * @param dists
	 */
	public void setGridDistances(double[] dists) {
		boolean changed = false;
		
		if(gridDistances == null) {
			changed = true;
		} else if(gridDistances.length != dists.length) {
			changed = true;
		} else {
			for(int i = 0; i < dists.length; ++i) {
				if(dists[i] != gridDistances[i]) {
					changed = true;
					break;
				}
			}
		}

		if(changed) {
			gridDistances = dists;
			if (dists == null)
				setAutomaticGridDistance(true, false);
			else
				setAutomaticGridDistance(false, false);
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
	private boolean showAxesCornerCoords = true;
	
	protected boolean[] showAxesNumbers = { true, true, true };

	protected String[] axesLabels = { null, null, null };

	protected String[] axesUnitLabels = { null, null, null };

	protected boolean[] piAxisUnit = { false, false, false };

	protected int[] axesTickStyles = { EuclidianView.AXES_TICK_STYLE_MAJOR,
			EuclidianView.AXES_TICK_STYLE_MAJOR };

	// for axes labeling with numbers
	protected boolean[] automaticAxesNumberingDistances = { true, true, true };

	protected double axisNumberingDistanceX = Double.NaN;
	protected double axisNumberingDistanceY = Double.NaN;

	// distances between grid lines
	protected boolean automaticGridDistance = true;

	private double xZero;

	private double yZero;

	private double xscale;

	private double yscale;

	private double scaleRatio;

	private double invXscale;

	private double invYscale;

	private Dimension preferredSize;

	private boolean showGrid;

	private boolean gridIsBold;

	private int gridType;

	private int pointCapturingMode = EuclidianView.POINT_CAPTURING_AUTOMATIC;
	
	private boolean allowShowMouseCoords = true;
	
	public boolean getAllowShowMouseCoords() {
		return allowShowMouseCoords;
	}

	public void setAllowShowMouseCoords(boolean neverShowMouseCoords) {
		if (neverShowMouseCoords == this.allowShowMouseCoords) return;
		this.allowShowMouseCoords = neverShowMouseCoords;
		settingChanged();
	}
	
	/*
	 * change visibility of axes
	 */
	public void setShowAxis(int axis, boolean flag){
		boolean changed = flag != showAxes[axis];
		
		if(changed) {
			showAxes[axis] = flag;
			settingChanged();
		}
			
	}
	
	/**
	 * says if the axis is shown or not
	 * @param axis id of the axis
	 * @return if the axis is shown
	 */
	public boolean getShowAxis(int axis){
		return showAxes[axis];
	}

	/**
	 * sets the axis label to axisLabel
	 * @param axis
	 * @param axisLabel
	 */
	public void setAxisLabel(int axis, String axisLabel){
		boolean changed = false;
		if (axisLabel != null && axisLabel.length() == 0) {
			changed = axesLabels[axis] != null;
			axesLabels[axis] = null;
		}
		else {
			axesLabels[axis] = axisLabel;
			changed = !axesLabels[axis].equals(axisLabel);
		}
		
		if(changed) {
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
			piAxisUnit[i] = axesUnitLabels[i] != null
					&& axesUnitLabels[i].equals(Unicode.PI_STRING);
		}
		//setAxesIntervals(xscale, 0);
		//setAxesIntervals(yscale, 1);
		
		settingChanged();
	}

	public void setShowAxisNumbers(int axis, boolean showAxisNumbers){
		showAxesNumbers[axis]=showAxisNumbers;
	}

	public boolean[] getShowAxisNumbers(){
		return showAxesNumbers;
	}

	public double getAxisNumberingDistanceX() {
		return axisNumberingDistanceX;
	}

	public double getAxisNumberingDistanceY() {
		return axisNumberingDistanceY;
	}

	/**
	 * 
	 * @param dist
	 */
	public void setAxisNumberingDistanceX(double dist) {
		
	
		axisNumberingDistanceX = dist;
		
		setAutomaticAxesNumberingDistance(false, 0, false);
		
		settingChanged();
	}

	/**
	 * 
	 * @param dist
	 */
	public void setAxisNumberingDistanceY(double dist) {
		
	
		axisNumberingDistanceY = dist;
		
		setAutomaticAxesNumberingDistance(false, 1, false);
		
		settingChanged();
	}

	public void setAutomaticAxesNumberingDistance(boolean flag, int axis, boolean callsc) {
		if (automaticAxesNumberingDistances[axis] != flag) {
			automaticAxesNumberingDistances[axis] = flag;

			if (flag) {
				axisNumberingDistanceX = Double.NaN;
				axisNumberingDistanceY = Double.NaN;
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

	public void setAxisTickStyle(int axis, int tickStyle){
		
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
		if (positiveAxes[axis] == isPositiveAxis) return;
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
	 * @param xminObjectNew the xminObject to set
	 * @param callsc whether settingChanged should be called
	 */
	public void setXminObject(NumberValue xminObjectNew, boolean callsc) {
		this.xminObject = xminObjectNew;
		if (callsc)
			settingChanged();
	}

	/**
	 * @return the xmaxObject
	 */
	public GeoNumeric getXmaxObject() {
		return (GeoNumeric) xmaxObject;
	}

	/**
	 * @param xmaxObjectNew the xmaxObject to set
	 * @param callsc whether settingChanged should be called
	 */
	public void setXmaxObject(NumberValue xmaxObjectNew, boolean callsc) {
		this.xmaxObject = xmaxObjectNew;
		if (callsc)
			settingChanged();
	}

	/**
	 * @return the yminObject
	 */
	public GeoNumeric getYminObject() {
		return (GeoNumeric) yminObject;
	}

	/**
	 * @param yminObjectNew the yminObject to set
	 * @param callsc whether settingChanged should be called
	 */
	public void setYminObject(NumberValue yminObjectNew, boolean callsc) {
		this.yminObject = yminObjectNew;
		if (callsc)
			settingChanged();
	}

	/**
	 * @return the ymaxObject
	 */
	public GeoNumeric getYmaxObject() {
		return (GeoNumeric) ymaxObject;
	}

	/**
	 * @param ymaxObjectNew the ymaxObject to set
	 * @param callsc whether settingChanged should be called
	 */
	public void setYmaxObject(NumberValue ymaxObjectNew, boolean callsc) {
		this.ymaxObject = ymaxObjectNew;
		if (callsc)
			settingChanged();
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

	public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale) {
		if (Double.isNaN(xscale) || xscale < Kernel.MAX_DOUBLE_PRECISION || xscale > Kernel.INV_MAX_DOUBLE_PRECISION)
			return;
		if (Double.isNaN(yscale) || yscale < Kernel.MAX_DOUBLE_PRECISION || yscale > Kernel.INV_MAX_DOUBLE_PRECISION)
			return;

		this.xZero = xZero;
		this.yZero = yZero;
		this.xscale = xscale;
		this.yscale = yscale;
		scaleRatio = yscale / xscale;
		invXscale = 1.0d / xscale;
		invYscale = 1.0d / yscale;
		
		// set transform for my coord system:
		// ( xscale 0 xZero )
		// ( 0 -yscale yZero )
		// ( 0 0 1 )
		// not needed in Settings
		//coordTransform.setTransform(xscale, 0.0d, 0.0d, -yscale, xZero, yZero);

		// real world values
		// not needed in Settings
		//setRealWorldBounds();
		
	}

	public void setAxesNumberingDistance(double tickDist, int axis) {
		if (axis == 0) 
			setAxisNumberingDistanceX(tickDist);
		else
			setAxisNumberingDistanceY(tickDist);
		
	}

	public void setPreferredSize(Dimension dimension) {
		preferredSize = dimension;
		
	}

	public Dimension getPreferredSize() {
		return preferredSize;
	}

	public void setShowAxes(boolean x, boolean y) {
		showAxes[0] = x;
		showAxes[1] = y;
		settingChanged();
		
	}

	public void showGrid(boolean show) {
		if (show == showGrid)
			return;
		showGrid = show;
		settingChanged();
	}

	public boolean getShowGrid() {
		return showGrid;
	}

	public boolean getGridIsBold() {
		return gridIsBold;
	}
	
	public void setGridIsBold(boolean gridIsBold ) {
		if (this.gridIsBold == gridIsBold) return;
		
		this.gridIsBold=gridIsBold;
		
		settingChanged();
	}

	final public int getGridType() {
		return gridType;
	}

	/**
	 * Set grid type.
	 */
	public void setGridType(int type) {
		if (gridType == type) return;
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
	 */
	public void setPointCapturing(int mode) {
		if (pointCapturingMode == mode) return;
		pointCapturingMode = mode;
		settingChanged();
	}

	public void setAllowToolTips(int setto) {
		if (setto == tooltipsInThisView) return;
		tooltipsInThisView = setto;
		settingChanged();
	}

	final public int getAllowToolTips() {
		return tooltipsInThisView;
	}

	public void setDrawBorderAxes(int axis, boolean value) {
		if (axis == 0 || axis == 1)
			drawBorderAxes[axis] = value;
	}

	final public boolean[] getDrawBorderAxes() {
		return drawBorderAxes;
	}

	// TODO add more settings here
}
