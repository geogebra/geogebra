package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable.LevelOfDetail;

public class SurfaceParameter {
	private static final short ROOT_MESH_INTERVALS_SPEED_TO_QUALITY_FACTOR = 2;
	// max factor
	private static final short ROOT_MESH_INTERVALS_MAX_FACTOR = 2;
	private static final double ROOT_MESH_INTERVALS_MAX_FACTOR_INVERSE = 1.0
			/ ROOT_MESH_INTERVALS_MAX_FACTOR;
	/**
	 * says if we draw borders for wireframe (we use short for array index
	 * shifting)
	 */
	public int wireframeBorder;
	/** says if only one wireframe line is drawn */
	public boolean wireframeUnique;
	/** steps to draw wireframe */
	public int wireFrameStep;
	public double delta;
	public double borderMin;
	public double borderMax;
	public double step;
	public double max;
	public int n;

	/**
	 * @param levelOfDetail
	 *            level of detail
	 */
	public void init(LevelOfDetail levelOfDetail) {
		wireFrameStep = 1;
		wireframeUnique = false;
		max = Double.NaN;
		if (!Double.isNaN(this.step)) {
			if (this.step > this.delta) {
				// we have maximum one wireframe line
				this.wireframeUnique = true;
				double uWireFrame = Math.ceil(this.borderMin / this.step)
						* this.step;
				this.wireFrameStep = (int) ((n * (this.borderMax - uWireFrame))
						/ this.delta);

				this.step = this.delta / n;
				this.max = uWireFrame + this.step * this.wireFrameStep;
				if (this.wireFrameStep == n - 1) {
					this.wireFrameStep--;
					this.max -= this.step;
				}
				// uMin = this.borderMin;
			} else {
				double factor = n * this.step / this.delta;
				if (factor > 1) {
					this.wireFrameStep = (int) Math.ceil(factor);
				} else if (factor < ROOT_MESH_INTERVALS_MAX_FACTOR_INVERSE) {
					int stepFactor = (int) Math.ceil(
							ROOT_MESH_INTERVALS_MAX_FACTOR_INVERSE / factor);
					// Log.debug("stepFactor = " + stepFactor);
					this.step *= stepFactor;
				}
				if (levelOfDetail == LevelOfDetail.QUALITY
						&& this.wireFrameStep == 1) {
					this.wireFrameStep *= ROOT_MESH_INTERVALS_SPEED_TO_QUALITY_FACTOR;
				}
				this.max = Math.floor(this.borderMax / this.step) * this.step;
				double uMin = Math.ceil(this.borderMin / this.step) * this.step;
				this.delta = this.max - uMin;
				int ratioInt = (int) Math.ceil(this.delta / this.step);
				n = (ratioInt + 1) * this.wireFrameStep + 1;
				// delta has to widened a bit to start at a correct tick
				this.delta += (1 + 1.0 / this.wireFrameStep) * this.step;
			}
		} else {
			if (levelOfDetail == LevelOfDetail.QUALITY) {
				this.wireFrameStep *= ROOT_MESH_INTERVALS_SPEED_TO_QUALITY_FACTOR;
				n *= ROOT_MESH_INTERVALS_SPEED_TO_QUALITY_FACTOR;
			}
		}

		if (Double.isNaN(this.max)) {
			double du = this.delta / this.n;
			this.max = this.borderMax - du;
		}
	}

	/**
	 * @param surfaceGeo
	 *            surface
	 * @param view3d
	 *            3D view
	 * @param index
	 *            parameter index
	 */
	public void initBorder(SurfaceEvaluable surfaceGeo, EuclidianView3D view3d,
			int index) {
		borderMin = surfaceGeo.getMinParameter(index);
		borderMax = surfaceGeo.getMaxParameter(index);
		step = Double.NaN;
		step = Double.NaN;

		if (((GeoElement) surfaceGeo).isGeoFunctionNVar()
				|| (surfaceGeo instanceof GeoFunction)) {
			if (Double.isNaN(borderMin)) {
				borderMin = view3d.getClippingCubeDrawable()
						.getMinMax()[index][0];
			}
			if (Double.isNaN(borderMax)) {
				borderMax = view3d.getClippingCubeDrawable()
						.getMinMax()[index][1];
			}

			// don't draw borders
			wireframeBorder = 0;

			// wireframe follows the grid
			step = view3d.getAxisNumberingDistance(index);
		} else if (((GeoSurfaceCartesianND) surfaceGeo)
				.isSurfaceOfRevolutionAroundOx()) {
			// cartesian surface of revolution
			if (index == 0) {
				borderMin = view3d.getXmin();
				borderMax = view3d.getXmax();
				// draw borders for v, not for u=x
				wireframeBorder = 0;
				// wireframe follows the grid
				step = view3d.getAxisNumberingDistance(0);
			} else {
				wireframeBorder = 1;
			}
		} else {
			// cartesian surface NOT of revolution
			// draw borders for u and v
			wireframeBorder = 1;
		}

		delta = borderMax - borderMin;
	}

	/**
	 * @return number of mesh nodes
	 */
	public int getCornerCount() {
		if (this.wireframeUnique) {
			if (this.wireFrameStep < 0) {
				return 2 * this.wireframeBorder;
			}
			return 1 + 2 * this.wireframeBorder;
		}
		return (this.n - 1) / this.wireFrameStep + 2 * this.wireframeBorder;
	}

}
