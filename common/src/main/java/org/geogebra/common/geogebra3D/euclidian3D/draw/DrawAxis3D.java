package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.TreeMap;

import org.geogebra.common.euclidian.DrawAxis;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hits3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush.Ticks;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Geometry3DGetterManager;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.Geometry3DGetter.GeometryType;
import org.geogebra.common.util.debug.Log;

/**
 * Class for drawing axis (Ox), (Oy), ...
 * 
 * @author mathieu
 *
 */
public class DrawAxis3D extends DrawLine3D {

	private TreeMap<String, DrawLabel3D> labels;
	private float numbersXOffset;
	private float numbersYOffset;
	private float numbersZOffset;
	private boolean outsideBox = false;
	static private double SQRT3_DIV_3 = Math.sqrt(3.0) / 3.0;
	private Coords tmpCoords1 = new Coords(4);
	private Coords tmpCoords2 = new Coords(4);

	/**
	 * common constructor
	 * 
	 * @param view3D
	 *            view
	 * @param axis3D
	 *            axis
	 */
	public DrawAxis3D(EuclidianView3D view3D, GeoAxisND axis3D) {

		super(view3D, axis3D);

		super.setDrawMinMax(-2, 2);

		labels = new TreeMap<>();
	}

	/**
	 * drawLabel is used here for ticks
	 */
	@Override
	public void drawLabel(Renderer renderer) {

		// Application.debug(getGeoElement()+":
		// "+getGeoElement().isLabelVisible());

		if (!getGeoElement().isEuclidianVisible()) {
			return;
		}

		if (!getGeoElement().isLabelVisible()) {
			return;
		}

		for (DrawLabel3D currentLabel : labels.values()) {
			currentLabel.draw(renderer);
		}

		super.drawLabel(renderer);

	}

	@Override
	public void setWaitForReset() {
		super.setWaitForReset();
		for (DrawLabel3D currentLabel : labels.values()) {
			currentLabel.setWaitForReset();
		}
	}

	@Override
	protected void updateLabel() {
		// draw numbers
		GeoAxisND axis = (GeoAxisND) getGeoElement();

		int axisIndex = axis.getType();

		double distance = getView3D().getAxisNumberingDistance(axisIndex);

		// Application.debug("drawMinMax="+getDrawMin()+","+getDrawMax());
		double[] minmax = getDrawMinMax();

		int iMin = (int) (minmax[0] / distance);
		int iMax = (int) (minmax[1] / distance);
		if (minmax[0] > 0) {
			iMin++;
		} else if (minmax[1] < 0) {
			iMax--;
		}
		int nb = iMax - iMin + 1;
		// Application.debug("iMinMax="+iMin+","+iMax);

		if (nb < 1) {
			Log.debug("nb=" + nb);
			// labels = null;
			return;
		}

		// sets all already existing labels not visible
		for (DrawLabel3D currentLabel : labels.values()) {
			currentLabel.setIsVisible(false);
		}

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
				String strNum = DrawAxis.tickDescription(getView3D(), i,
						axisIndex);
				if (unitLabel != null) {
					strNum += unitLabel;
				}
				// check if the label already exists
				DrawLabel3D tickLabel = labels.get(strNum);
				if (tickLabel != null) {
					// sets the label visible
					tickLabel.setIsVisible(true);
					tickLabel.update(strNum, getView3D().getFontAxes(),
							getGeoElement().getObjectColor(),
							origin.copyVector(), numbersXOffset,
							numbersYOffset, numbersZOffset);
					tickLabel.updatePosition(getView3D().getRenderer());
					// TODO optimize this
				} else {
					// creates new label
					tickLabel = new DrawLabel3D(getView3D(), this);
					tickLabel.setAnchor(true);
					tickLabel.update(strNum, getView3D().getFontAxes(),
							getGeoElement().getObjectColor(),
							origin.copyVector(), numbersXOffset,
							numbersYOffset, numbersZOffset);
					tickLabel.updatePosition(getView3D().getRenderer());
					labels.put(strNum, tickLabel);
				}

			}

		}

		// update end of axis label
		String text = getView3D().getAxisLabel(axisIndex);
		if (text == null || text.length() == 0) {
			label.setIsVisible(false);
		} else {
			label.setAnchor(true);

			if (getView3D().isXRDrawing()) {
				updateDrawPositionLabel();
			} else {
				label.update(text, getView3D().getAxisLabelFont(axisIndex),
				getGeoElement().getObjectColor(),
				((GeoAxisND) getGeoElement()).getPointInD(3, minmax[1]),
				getGeoElement().labelOffsetX, // -4,
				getGeoElement().labelOffsetY, // -6
				0);
			}
			label.updatePosition(getView3D().getRenderer());
		}

	}

	/**
	 * update position for end of axis label
	 */
	private void updateDrawPositionLabel() {
		GeoAxisND axis = (GeoAxisND) getGeoElement();
		int axisIndex = axis.getType();

		String text = getView3D().getAxisLabel(axisIndex);
		if (text == null || text.length() == 0) {
			return;
		}
		label.update(text, getView3D().getAxisLabelFont(axisIndex),
				getGeoElement().getObjectColor(),
				((GeoAxisND) getGeoElement()).getPointInD(3, getDrawMinMax()[1]),
				-numbersXOffset,
				-numbersYOffset,
				-numbersZOffset);
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
		brush.setTicksDistance(
				(float) getView3D().getAxisNumberingDistance(axisIndex));
		brush.setTicksOffset((float) (-minmax[0] / (minmax[1] - minmax[0])));
		super.updateForItSelf(false);
		brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);
		brush.setTicks(Ticks.NONE);

		return true;

	}

	@Override
	protected double getScale() {
		return getView3D()
				.getScale(((GeoAxisND) getGeoElement()).getType());
	}

	@Override
	protected void setBrushThickness(PlotterBrush brush, int thickness, float scale) {
		brush.setThickness(thickness, true, scale);
	}

	@Override
	protected void setAffineTexture(PlotterBrush brush, double[] minmax) {
		brush.setAffineTexture(0f, 0f);
	}

	/**
	 * update values for ticks and labels
	 */
	public void updateDecorations() {

		if (getView3D().isXRDrawing()) {
			// update decorations
			GeoAxisND axis = (GeoAxisND) getGeoElement();
			// getToScreenMatrixForGL = rotation + translation
			// for AR, we need the rotation+translation from Renderer.arViewModelMatrix
			tmpCoords2.setMul(getView3D().getToScreenMatrixForGL(),
					axis.getDirectionInD3());
			tmpCoords1.setMul(getView3D().getRenderer().getArViewModelMatrix(),
					tmpCoords2);
			tmpCoords1.setZ(0);
			tmpCoords1.setW(0);
			tmpCoords1.normalize();
			double valueX = tmpCoords1.getX();
			tmpCoords1.setX(-tmpCoords1.getY());
			tmpCoords1.setY(valueX);

			getView3D().getRenderer().getArViewModelMatrix().solve(tmpCoords1,
					tmpCoords2);
			numbersXOffset  = (float) tmpCoords2.getX();
			numbersYOffset  = (float) tmpCoords2.getY();
			numbersZOffset  = (float) tmpCoords2.getZ();

			if (axis.getType() == GeoAxisND.X_AXIS) {
				numbersXOffset = -numbersXOffset;
				numbersYOffset = -numbersYOffset;
				numbersZOffset = -numbersZOffset;
			}

			getGeoElement().setLabelOffset((int) -numbersXOffset, (int) -numbersYOffset);
		} else {
			// update decorations
			GeoAxisND axis = (GeoAxisND) getGeoElement();

			// gets the direction vector of the axis as it is drawn on screen
			Coords v = new Coords(4);
			v.setMul(getView3D().getToScreenMatrixForGL(), axis.getDirectionInD3());
			v.set(3, 0); // set z-coord to 0

			// calc orthogonal offsets
			int vx = (int) (v.get(1) * 1.5 * axis.getTickSize());
			int vy = (int) (v.get(2) * 1.5 * axis.getTickSize());
			if (getView3D().isXRDrawing() && axis.getType() == GeoAxisND.Y_AXIS
					&& vx == 0 && vy == 0) {
				vx = (int) (-SQRT3_DIV_3 * 1.5 * axis.getTickSize());
				vy = (int) (-SQRT3_DIV_3 * 1.5 * axis.getTickSize());
			}
			numbersXOffset = -vy;
			numbersYOffset = vx;

			if (axis.getType() == GeoAxisND.X_AXIS) {
				numbersXOffset = -numbersXOffset;
				numbersYOffset = -numbersYOffset;
			}

			getGeoElement().setLabelOffset(((-vx - ((int) numbersXOffset)) * 3) / 2, // -vx,
					// -2*xOffset,
					((-vy - ((int) numbersYOffset)) * 3) / 2// -vy//-2*yOffset
			);
		}

	}

	/**
	 * @return distance between two ticks
	 */
	public double getNumbersDistance() {
		return 1; // ((GeoAxisND) getGeoElement()).getNumbersDistance();
	}

	@Override
	protected void updateForView() {
		// done in 3D view
	}

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
			default:
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
			for (DrawLabel3D currentLabel : labels.values()) {
				currentLabel.setIsVisible(false);
			}
		}

		super.setDrawMinMax(min, max);
	}

	@Override
	final public boolean isVisible() {
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

	@Override
	public void export(Geometry3DGetterManager manager, boolean exportSurface) {
		if (isVisible()) {
			manager.export(getGeoElement(), getGeometryIndex(),
					getGeoElement().getObjectColor(), 1, GeometryType.AXIS);
		}
	}

	/**
	 * update axis position for ticks and labels
	 */
	public void updateDrawPositionAxes() {
	    updateDecorations();
		int tickSize = ((GeoAxisND) getGeoElement()).getTickSize();
		for (DrawLabel3D currentLabel : labels.values()) {
			currentLabel.updateDrawPositionAxes(numbersXOffset, numbersYOffset, numbersZOffset,
                    tickSize);
		}
		label.updateDrawPositionAxes(-numbersXOffset, -numbersYOffset, -numbersZOffset, tickSize);
	}
}
