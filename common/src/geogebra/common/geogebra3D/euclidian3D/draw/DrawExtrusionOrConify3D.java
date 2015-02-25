package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.Previewable;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoForExtrusion;
import geogebra.common.geogebra3D.kernel3D.algos.ExtrusionComputer;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.main.App;
import geogebra.common.util.AsyncOperation;

import java.util.ArrayList;

/**
 * Class for drawing extrusions.
 * 
 * @author matthieu
 *
 */
public abstract class DrawExtrusionOrConify3D extends Drawable3DSurfaces
		implements Previewable {

	// drawing

	@Override
	protected void drawSurfaceGeometry(Renderer renderer) {

	}

	@Override
	public void drawGeometry(Renderer renderer) {

	}

	@Override
	public void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {
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

	/** basis */
	private ArrayList<GeoPolygon> selectedPolygons;
	private ArrayList<GeoConicND> selectedConics;

	/** segments of the polygon preview */
	private ArrayList<DrawSegment3D> segments;

	@SuppressWarnings("rawtypes")
	private ArrayList<ArrayList> segmentsPoints;

	protected ExtrusionComputer extrusionComputer;

	private GeoNumeric height;

	private GeoElement basis;

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

	public void updateMousePos(double xRW, double yRW) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param basis
	 *            polygon
	 * @param height
	 *            altitude
	 * @return new algo from polygon and height
	 */
	abstract protected AlgoForExtrusion getAlgo(GeoPolygon basis,
			GeoNumeric height);

	/**
	 * 
	 * @param basis
	 *            conic
	 * @param height
	 *            altitude
	 * @return new algo from polygon and height
	 */
	abstract protected AlgoForExtrusion getAlgo(GeoConicND basis,
			GeoNumeric height);

	public void updatePreview() {

		if (extrusionComputer == null) {
			if (selectedPolygons.size() == 1) {
				basis = selectedPolygons.get(0);
				// create the height
				height = new GeoNumeric(getView3D().getKernel()
						.getConstruction(), 0.0001);
				// create the algo
				extrusionComputer = new ExtrusionComputer(getAlgo(
						(GeoPolygon) basis, height));

			} else if (selectedConics.size() == 1) {
				basis = selectedConics.get(0);
				// create the height
				height = new GeoNumeric(getView3D().getKernel()
						.getConstruction(), 0.0001);
				// create the algo
				extrusionComputer = new ExtrusionComputer(getAlgo(
						(GeoConicND) basis, height));
			}

			if (extrusionComputer != null) {

				extrusionComputer.getAlgo().removeOutputFromAlgebraView();
				extrusionComputer.getAlgo().removeOutputFromPicking();
				extrusionComputer.getAlgo().setOutputPointsEuclidianVisible(
						false);
				extrusionComputer.getAlgo().notifyUpdateOutputPoints();

				// sets the top face to be handled
				((EuclidianController3D) getView3D().getEuclidianController())
						.setHandledGeo(extrusionComputer.getAlgo()
								.getGeoToHandle());

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
				.setHandledGeo(null);

		if (extrusionComputer != null) {
			// remove the algo
			extrusionComputer.getAlgo().remove();
			extrusionComputer = null;
		}

	}

	public void createPolyhedron() {
		((EuclidianController3D) getView3D().getEuclidianController())
				.setHandledGeo(null);

		if (extrusionComputer != null) {

			// clear current selections : remove basis polygon from selections
			// (will be re-introduced, due to draw loop)
			if (!getView3D().getRenderer().useLogicalPicking()) {
				getView3D().getEuclidianController().clearSelections();
			}

			// add current height to selected numeric (will be used on next
			// EuclidianView3D::rightPrism() call)
			Hits hits = new Hits();

			if (extrusionComputer.getComputed() == 0) {// if height has not been
														// set by dragging, ask
														// one
				App app = getView3D().getApplication();
				if (callback == null) {
					callback = new CreatePolyhedronCallback();
				}
				callback.set(basis, getView3D(), extrusionComputer);

				app.getDialogManager().showNumberInputDialog(
						// app.getMenu(getView3D().getKernel().getModeText(EuclidianConstants.MODE_RIGHT_PRISM)),
						extrusionComputer.getAlgo().getOutput(0)
								.translatedTypeString(),
						app.getPlain("Altitude"),
						"",
						// check basis direction / view direction to say if the
						// sign has to be forced
						basis.getMainDirection().dotproduct(
								getView3D().getViewDirection()) > 0,
						app.getPlain("PositiveValuesFollowTheView"), callback);

			} else {
				hits.add(height);
				getView3D().getEuclidianController().addSelectedNumberValue(
						hits, 1, false);
			}

			if (extrusionComputer != null){
				// remove the algo
				extrusionComputer.getAlgo().remove();
				extrusionComputer = null;
			}
		}
	}

	private CreatePolyhedronCallback callback;

	private class CreatePolyhedronCallback extends AsyncOperation {

		private GeoElement basis;
		private EuclidianView3D view;
		private ExtrusionComputer extrusionComputer;

		public CreatePolyhedronCallback() {
			super();
		}

		public void set(GeoElement basis, EuclidianView3D view, ExtrusionComputer extrusionComputer) {
			this.basis = basis;
			this.view = view;
			this.extrusionComputer = extrusionComputer;
		}

		@Override
		public void callback(Object obj) {
			GeoNumberValue num = (GeoNumberValue) obj;

			// App.debug("callback : "+num + "," + basis + " , "+extrusionComputer);
			
			if (extrusionComputer != null){
				// remove the algo
				extrusionComputer.getAlgo().remove();
				extrusionComputer = null;
			}

			GeoElement ret;
			if (basis.isGeoPolygon()) {
				if (view.getEuclidianController().getMode() == EuclidianConstants.MODE_EXTRUSION) {
					// prism
					ret = basis.getKernel().getManager3D()
							.Prism(null, (GeoPolygon) basis, num)[0];
				} else {
					// pyramid
					ret = basis.getKernel().getManager3D()
							.Pyramid(null, (GeoPolygon) basis, num)[0];
				}

			} else { // basis.isGeoConic()
				if (view.getEuclidianController().getMode() == EuclidianConstants.MODE_EXTRUSION) {
					// cylinder
					ret = basis.getKernel().getManager3D()
							.CylinderLimited(null, (GeoConicND) basis, num)[0];

				} else {
					// cone
					ret = basis.getKernel().getManager3D()
							.ConeLimited(null, (GeoConicND) basis, num)[0];
				}
			}

			view.getEuclidianController().setDialogOccurred();
			view.getEuclidianController().clearSelected();
			view.getApplication().getSelectionManager()
					.clearSelectedGeos(false, false);
			view.getApplication().getSelectionManager()
					.addSelectedGeo(ret, true, true);
		}

	}

}
