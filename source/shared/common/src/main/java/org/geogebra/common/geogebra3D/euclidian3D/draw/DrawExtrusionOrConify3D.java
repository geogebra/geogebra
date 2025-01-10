package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoForExtrusion;
import org.geogebra.common.geogebra3D.kernel3D.algos.ExtrusionComputer;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.AsyncOperation;

/**
 * Class for drawing extrusions.
 * 
 * @author matthieu
 *
 */
public abstract class DrawExtrusionOrConify3D extends Drawable3DSurfaces
		implements Previewable {

	/** basis */
	private ArrayList<GeoPolygon> selectedPolygons;
	private ArrayList<GeoConicND> selectedConics;

	/** extrusion computer */
	protected ExtrusionComputer extrusionComputer;

	private GeoNumeric height;

	private GeoElement basis;
	private CreatePolyhedronCallback callback;

	// drawing

	@Override
	protected void drawSurfaceGeometry(Renderer renderer) {
		// nothing to do
	}

	@Override
	public void drawGeometry(Renderer renderer) {
		// nothing to do
	}

	@Override
	public void drawGeometryHiding(Renderer renderer) {
		// nothing to do
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {
		// nothing to do
	}

	@Override
	public void drawOutline(Renderer renderer) {
		// no outline
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_SURFACE;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_CLOSED_SURFACES_CURVED);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_CLOSED_SURFACES_CURVED);
	}

	@Override
	protected boolean updateForItSelf() {
		return true;

	}

	// //////////////////////////////
	// Previewable interface

	/**
	 * Constructor for previewable
	 * 
	 * @param a_view3D
	 *            view
	 * @param selectedPolygons
	 *            polygons
	 * @param selectedConics
	 *            conics
	 */
	public DrawExtrusionOrConify3D(EuclidianView3D a_view3D,
			ArrayList<GeoPolygon> selectedPolygons,
			ArrayList<GeoConicND> selectedConics) {

		super(a_view3D);

		this.selectedPolygons = selectedPolygons;
		this.selectedConics = selectedConics;

		updatePreview();
	}

	@Override
	public void updateMousePos(double xRW, double yRW) {
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 * @param extBasis
	 *            polygon
	 * @param extHeight
	 *            altitude
	 * @return new algo from polygon and height
	 */
	abstract protected AlgoForExtrusion getAlgo(GeoPolygon extBasis,
			GeoNumeric extHeight);

	/**
	 * 
	 * @param extBasis
	 *            conic
	 * @param extHeight
	 *            altitude
	 * @return new algo from polygon and height
	 */
	abstract protected AlgoForExtrusion getAlgo(GeoConicND extBasis,
			GeoNumeric extHeight);

	@Override
	public void updatePreview() {

		if (extrusionComputer == null) {
			if (selectedPolygons.size() == 1) {
				basis = selectedPolygons.get(0);
				// create the height
				height = new GeoNumeric(
						getView3D().getKernel().getConstruction(), 0.0001);
				// create the algo
				extrusionComputer = new ExtrusionComputer(
						getAlgo((GeoPolygon) basis, height));

			} else if (selectedConics.size() == 1) {
				basis = selectedConics.get(0);
				// create the height
				height = new GeoNumeric(
						getView3D().getKernel().getConstruction(), 0.0001);
				// create the algo
				extrusionComputer = new ExtrusionComputer(
						getAlgo((GeoConicND) basis, height));
			}

			if (extrusionComputer != null) {

				extrusionComputer.getAlgo().removeOutputFromAlgebraView();
				extrusionComputer.getAlgo().removeOutputFromPicking();
				extrusionComputer.getAlgo()
						.setOutputPointsEuclidianVisible(false);
				extrusionComputer.getAlgo().notifyUpdateOutputPoints();

				// sets the top face to be handled
				((EuclidianController3D) getView3D().getEuclidianController())
						.setHandledGeo(
								extrusionComputer.getAlgo().getGeoToHandle(),
								basis);

				// ensure correct drawing of visible parts of the previewable
				extrusionComputer.getAlgo()
						.setOutputOtherEuclidianVisible(true);
				extrusionComputer.getAlgo().notifyUpdateOutputOther();

			}
		}
	}

	@Override
	public void disposePreview() {
		super.disposePreview();

		((EuclidianController3D) getView3D().getEuclidianController())
				.disposeHandledGeo();

		if (extrusionComputer != null) {
			// remove the algo
			extrusionComputer.getAlgo().remove();
			extrusionComputer = null;
		}

	}

	/**
	 * Creates the polyhedron
	 */
	public void createPolyhedron() {
		((EuclidianController3D) getView3D().getEuclidianController())
				.disposeHandledGeo();

		if (extrusionComputer != null) {

			// add current height to selected numeric (will be used on next
			// EuclidianView3D::rightPrism() call)
			Hits hits = new Hits();

			if (!extrusionComputer.getWasComputedByDragging(getView3D().isXREnabled())) {
				// if height has not been set by dragging, ask for one
				App app = getView3D().getApplication();
				if (callback == null) {
					callback = new CreatePolyhedronCallback();
				}
				callback.set(basis, getView3D(), extrusionComputer);

				app.getDialogManager().showNumberInputDialog(
						extrusionComputer.getAlgo().getOutput(0)
								.translatedTypeString(),
						app.getLocalization().getMenu("Altitude"), "",
						// check basis direction / view direction to say if the
						// sign has to be forced
						basis.getMainDirection()
								.dotproduct(getView3D().getViewDirection()) > 0,
						app.getLocalization().getMenu(
								"PositiveValuesFollowTheView"),
						callback);

			} else {
				hits.add(height);
				getView3D().getEuclidianController()
						.addSelectedNumberValue(hits, 1, false, false);
			}

			if (extrusionComputer != null) {
				// remove the algo
				extrusionComputer.getAlgo().remove();
				extrusionComputer = null;
			}
		}
	}

	/**
	 * Callback after height is entered
	 *
	 */
	static class CreatePolyhedronCallback
			implements AsyncOperation<GeoNumberValue> {

		private GeoElement basis;
		private EuclidianView3D view;
		private ExtrusionComputer extrusionComputer;

		/**
		 * @param basis
		 *            polyhedron / quadric basis
		 * @param view
		 *            view
		 * @param extrusionComputer
		 *            conifier / extruder
		 */
		public void set(GeoElement basis, EuclidianView3D view,
				ExtrusionComputer extrusionComputer) {
			this.basis = basis;
			this.view = view;
			this.extrusionComputer = extrusionComputer;
		}

		@Override
		public void callback(GeoNumberValue obj) {
			GeoNumberValue num = obj;

			if (extrusionComputer != null) {
				// remove the algo
				extrusionComputer.getAlgo().remove();
				extrusionComputer = null;
			}

			GeoElement ret;
			if (basis.isGeoPolygon()) {
				if (view.getEuclidianController()
						.getMode() == EuclidianConstants.MODE_EXTRUSION) {
					// prism
					ret = basis.getKernel().getManager3D().prism(null,
							(GeoPolygon) basis, num)[0];
				} else {
					// pyramid
					ret = basis.getKernel().getManager3D().pyramid(null,
							(GeoPolygon) basis, num)[0];
				}

			} else { // basis.isGeoConic()
				if (view.getEuclidianController()
						.getMode() == EuclidianConstants.MODE_EXTRUSION) {
					// cylinder
					ret = basis.getKernel().getManager3D().cylinderLimited(null,
							(GeoConicND) basis, num)[0];

				} else {
					// cone
					ret = basis.getKernel().getManager3D().coneLimited(null,
							(GeoConicND) basis, num)[0];
				}
			}

			view.getEuclidianController().setDialogOccurred();
			view.getApplication().getSelectionManager().clearLists();
			view.getApplication().getSelectionManager().clearSelectedGeos(false,
					false);
			view.getApplication().getSelectionManager().addSelectedGeo(ret,
					true, true);
			view.disposePreview();
			view.getApplication().storeUndoInfo();
		}

	}

}
