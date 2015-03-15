package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.Hits3D;
import geogebra.common.geogebra3D.euclidian3D.Hitting;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush.Ticks;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.kernelND.GeoAxisND;
import geogebra.common.main.App;
import geogebra.common.util.NumberFormatAdapter;

import java.util.TreeMap;

/**
 * Class for drawing axis (Ox), (Oy), ...
 * 
 * @author matthieu
 *
 */
public class DrawAxis3D extends DrawLine3D {

	private TreeMap<String, DrawLabel3D> labels;

	/**
	 * common constructor
	 * 
	 * @param view3D
	 * @param axis3D
	 */
	public DrawAxis3D(EuclidianView3D view3D, GeoAxisND axis3D) {

		super(view3D, axis3D);

		super.setDrawMinMax(-2, 2);

		labels = new TreeMap<String, DrawLabel3D>();

	}

	/**
	 * drawLabel is used here for ticks
	 */
	@Override
	public void drawLabel(Renderer renderer) {

		// Application.debug(getGeoElement()+": "+getGeoElement().isLabelVisible());

		if (!getGeoElement().isEuclidianVisible())
			return;

		if (!getGeoElement().isLabelVisible())
			return;

		for (DrawLabel3D label : labels.values())
			label.draw(renderer);

		super.drawLabel(renderer);

	}

	@Override
	public void setWaitForReset() {
		super.setWaitForReset();
		for (DrawLabel3D label : labels.values())
			label.setWaitForReset();
	}

	@Override
	protected void updateLabel() {

		// draw numbers
		GeoAxisND axis = (GeoAxisND) getGeoElement();

		int axisIndex = axis.getType();

		NumberFormatAdapter numberFormat = getView3D().getAxisNumberFormat(
				axisIndex);
		double distance = getView3D().getAxisNumberingDistance(axisIndex);

		// Application.debug("drawMinMax="+getDrawMin()+","+getDrawMax());
		double[] minmax = getDrawMinMax();

		int iMin = (int) (minmax[0] / distance);
		int iMax = (int) (minmax[1] / distance);
		if (minmax[0] > 0)
			iMin++;
		else if (minmax[1] < 0)
			iMax--;
		int nb = iMax - iMin + 1;
		// Application.debug("iMinMax="+iMin+","+iMax);

		if (nb < 1) {
			App.debug("nb=" + nb);
			// labels = null;
			return;
		}

		// sets all already existing labels not visible
		for (DrawLabel3D label : labels.values())
			label.setIsVisible(false);

		if (getView3D().getShowAxisNumbers(axisIndex)) {

			String unitLabel = getView3D().getAxisUnitLabel(axisIndex);
			if (getView3D().getPiAxisUnit(axisIndex)) {
				unitLabel = null;
			}

			for (int i = iMin; i <= iMax; i++) {
				double val = i * distance;
				Coords origin = ((GeoAxisND) getGeoElement()).getPointInD(3,
						val);

				// draw numbers
				String strNum = getView3D().getKernel().formatPiE(val,
						numberFormat, StringTemplate.defaultTemplate);
				if (unitLabel != null) {
					strNum += unitLabel;
				}
				// check if the label already exists
				DrawLabel3D label = labels.get(strNum);
				if (label != null) {
					// sets the label visible
					label.setIsVisible(true);
					label.update(strNum, getView3D().getFontAxes(),
							getGeoElement().getObjectColor(),
							origin.copyVector(), numbersXOffset, numbersYOffset);
					label.updatePosition(getView3D().getRenderer());
					// TODO optimize this
				} else {
					// creates new label
					label = new DrawLabel3D(getView3D(), this);
					label.setAnchor(true);
					label.update(strNum, getView3D().getApplication()
							.getPlainFontCommon(), getGeoElement()
							.getObjectColor(), origin.copyVector(),
							numbersXOffset, numbersYOffset);
					label.updatePosition(getView3D().getRenderer());
					labels.put(strNum, label);
				}

			}

		}

		// update end of axis label
		String text = getView3D().getAxisLabel(axisIndex);
		if (text == null || text.length() == 0) {
			label.setIsVisible(false);
		} else {
			label.setAnchor(true);
			label.update(text, getView3D().getAxisLabelFont(axisIndex),
					getGeoElement().getObjectColor(),
					((GeoAxisND) getGeoElement()).getPointInD(3, minmax[1]),
					getGeoElement().labelOffsetX,// -4,
					getGeoElement().labelOffsetY// -6
			);
			label.updatePosition(getView3D().getRenderer());
		}

	}

	@Override
	public void setLabelWaitForReset() {
		super.setLabelWaitForReset();
		for (DrawLabel3D l : labels.values()) {
			l.setWaitForReset();
		}
	}

	@Override
	protected void updateLabelPosition() {

		// nothing to do here

	}

	@Override
	protected boolean updateForItSelf() {

		setLabelWaitForUpdate();

		double[] minmax = getDrawMinMax();

		int axisIndex = ((GeoAxisND) getGeoElement()).getType();

		PlotterBrush brush = getView3D().getRenderer().getGeometryManager()
				.getBrush();
		brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);
		switch (getView3D().getAxisTickStyle(axisIndex)) {
		case 0:
		default:
			brush.setTicks(Ticks.MAJOR_AND_MINOR);
			break;
		case 1:
			brush.setTicks(Ticks.MAJOR);
			break;
		case 2:
			brush.setTicks(Ticks.NONE);
			break;
		}
		brush.setTicksDistance((float) getView3D().getAxisNumberingDistance(
				axisIndex));
		brush.setTicksOffset((float) (-minmax[0] / (minmax[1] - minmax[0])));
		super.updateForItSelf(false);
		brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);
		brush.setTicks(Ticks.NONE);

		return true;

	}

	private int numbersXOffset, numbersYOffset;

	/**
	 * update values for ticks and labels
	 */
	public void updateDecorations() {

		// update decorations
		GeoAxisND axis = (GeoAxisND) getGeoElement();

		// gets the direction vector of the axis as it is drawn on screen
		Coords v = getView3D().getToScreenMatrix().mul(axis.getDirectionInD3());
		v.set(3, 0); // set z-coord to 0
		// double vScale = v.norm(); //axis scale, used for ticks distance
		double vScale = getView3D().getScale(); // TODO use different scales for
												// x/y/z
		// Application.debug("vScale="+vScale);

		// calc orthogonal offsets
		int vx = (int) (v.get(1) * 1.5 * axis.getTickSize() / vScale);
		int vy = (int) (v.get(2) * 1.5 * axis.getTickSize() / vScale);
		numbersXOffset = -vy;
		numbersYOffset = vx;

		// if (yOffset>0){
		if (axis.getType() == GeoAxisND.X_AXIS) {
			numbersXOffset = -numbersXOffset;
			numbersYOffset = -numbersYOffset;
		}

		getGeoElement().setLabelOffset(((-vx - numbersXOffset) * 3) / 2,// -vx,//-2*xOffset,
				((-vy - numbersYOffset) * 3) / 2// -vy//-2*yOffset
				);

	}

	/**
	 * @return distance between two ticks
	 */
	public double getNumbersDistance() {
		return 1;// ((GeoAxisND) getGeoElement()).getNumbersDistance();
	}

	@Override
	protected void updateForView() {
		// done in 3D view
	}

	private boolean outsideBox = false;

	/**
	 * sets the min/max for drawing immediately
	 * 
	 * @param minMax
	 *            x,y,z min/max
	 */
	public void setDrawMinMaxImmediatly(double[][] minMax) {

		int type = ((GeoAxisND) getGeoElement()).getType();

		double min = minMax[type][0];
		double max = minMax[type][1];
		outsideBox = false;

		if (getView3D().getPositiveAxis(type)) {
			if (min < 0) {
				if (max > 0) {
					min = 0;
				} else {
					outsideBox = true;
				}
			}
		}

		if (!outsideBox) {
			// check if outside the box
			switch (type) {
			case GeoAxisND.X_AXIS:
				outsideBox = (minMax[GeoAxisND.Y_AXIS][0]
						* minMax[GeoAxisND.Y_AXIS][1] > 0)
						|| (minMax[GeoAxisND.Z_AXIS][0]
								* minMax[GeoAxisND.Z_AXIS][1] > 0);
				break;
			case GeoAxisND.Y_AXIS:
				outsideBox = (minMax[GeoAxisND.Z_AXIS][0]
						* minMax[GeoAxisND.Z_AXIS][1] > 0)
						|| (minMax[GeoAxisND.X_AXIS][0]
								* minMax[GeoAxisND.X_AXIS][1] > 0);
				break;
			case GeoAxisND.Z_AXIS:
				outsideBox = (minMax[GeoAxisND.X_AXIS][0]
						* minMax[GeoAxisND.X_AXIS][1] > 0)
						|| (minMax[GeoAxisND.Y_AXIS][0]
								* minMax[GeoAxisND.Y_AXIS][1] > 0);
				break;
			}
		}

		// if outside the box, set all labels invisible
		if (outsideBox) {
			for (DrawLabel3D label : labels.values()) {
				label.setIsVisible(false);
			}
		}

		super.setDrawMinMax(min, max);
	}

	@Override
	final protected boolean isVisible() {
		return (!outsideBox) && super.isVisible();
	}

	@Override
	protected boolean hitLabel(Hitting hitting, Hits3D hits) {
		return false; // no label to hit
	}

	@Override
	public boolean hasPickableLable() {
		return false;
	}

}
