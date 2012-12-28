package geogebra3D.euclidian3D;

import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.plots.CurveMesh;
import geogebra3D.euclidian3D.plots.CurveTree;
import geogebra3D.kernel3D.GeoCurveCartesian3D;

/**
 * @author ggb3D
 * 
 *         Drawable for GeoCurveCartesian3D
 * 
 */
public class DrawCurve3D extends Drawable3DCurves {
	private final boolean useOldCurves = false;

	private CurveMesh mesh;
	private CurveTree tree;

	/** handle to the curve */
	private GeoCurveCartesian3D curve;

	/** current domain for the function on the format {umin, umax} */
	private double[] domain = new double[2];

	/** Current culling box - set to view3d.(x|y|z)(max|min) */
	private double[] cullingBox = new double[6];

	/**
	 * @param a_view3d
	 *            the 3D view where the curve is drawn
	 * @param curve
	 *            the 3D curve to draw
	 */
	public DrawCurve3D(EuclidianView3D a_view3d, GeoCurveCartesian3D curve) {
		super(a_view3d, curve);
		this.curve = curve;
		if (useOldCurves)
			tree = new CurveTree(curve, a_view3d);
		else {
			updateDomain();
			updateCullingBox();
			mesh = new CurveMesh(curve, cullingBox, (float) a_view3d.getScale());
		}
	}

	@Override
	public void drawGeometry(Renderer renderer) {

		renderer.getGeometryManager().draw(getGeometryIndex());

	}

	/**
	 * Decides if the curve should be redrawn or not depending on how the view
	 * changes
	 * 
	 * @return
	 */
	// private boolean needRedraw(){
	// double currRad = currentRadius();
	// if(currRad>savedRadius*radiusMaxFactor || currRad<
	// savedRadius*radiusMinFactor){
	// savedRadius=currRad;
	// return true;
	// }
	// return false;
	// }

	private boolean updateDomain() {
		boolean changed = false;

		double t = curve.getMinParameter();
		if (t != domain[0]) {
			changed = true;
			domain[0] = t;
		}
		t = curve.getMaxParameter();
		if (t != domain[1]) {
			changed = true;
			domain[1] = t;
		}

		return changed;
	}

	/**
	 * gets the viewing radius based on the viewing frustum
	 */
	private void updateCullingBox() {
		EuclidianView3D view = getView3D();
		cullingBox[0] = view.getXMinMax()[0];
		cullingBox[1] = view.getXMinMax()[1];
		cullingBox[2] = view.getYMinMax()[0];
		cullingBox[3] = view.getYMinMax()[1];
		cullingBox[4] = view.getZMinMax()[0];
		cullingBox[5] = view.getZMinMax()[1];
	}

	@Override
	protected boolean updateForItSelf() {

		boolean ret = true;

		// updateColors();

		if (useOldCurves) {
//			Renderer renderer = getView3D().getRenderer();
//
//			if (!curve.isEuclidianVisible() || !curve.isDefined()) {
//				setGeometryIndex(-1);
//			} else {
//				needRedraw();
//				tree = new CurveTree(curve, getView3D());
//
//				PlotterBrush brush = renderer.getGeometryManager().getBrush();
//
//				brush.setThickness(getGeoElement().getLineThickness(),
//						(float) getView3D().getScale());
//
//				brush.start(8);
//
//				brush.draw(tree, savedRadius);
//
//				setGeometryIndex(brush.end());
//			}
		} else {
			if (elementHasChanged) {
				if (updateDomain()) {
					//domain has changed - create a new mesh
					mesh = new CurveMesh(curve, cullingBox, (float) getView3D().getScale());
				} else {
					//otherwise, update the surface
					elementHasChanged = false;
					mesh.updateParameters();
				}
			}

			Renderer renderer = getView3D().getRenderer();
			mesh.setCullingBox(cullingBox);
			ret = mesh.optimize();

			PlotterBrush brush = renderer.getGeometryManager().getBrush();
			brush.start(8);
			brush.draw(mesh);

			setGeometryIndex(brush.end());
		}
		return false;
	}

	@Override
	protected void updateForView() {
		updateCullingBox();
		EuclidianView3D view = getView3D();
		mesh.updateScale((float) view.getScale());
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}

}
