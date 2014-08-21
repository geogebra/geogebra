package geogebra.common.geogebra3D.euclidian3D;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.geogebra3D.euclidianFor3D.EuclidianControllerFor3DCompanion;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Euclidian controller creator for 3D controller
 * @author mathieu
 *
 */
public class EuclidianController3DCompanion extends EuclidianControllerFor3DCompanion{

	/**
	 * constructor
	 * @param ec controller
	 */
	public EuclidianController3DCompanion(EuclidianController ec) {
		super(ec);
	}
	
	
	@Override
	protected void movePoint(boolean repaint, AbstractEvent event) {

		// Application.debug("movePointMode="+movePointMode);

		if (ec.movedGeoPoint instanceof GeoPoint3D) {
			GeoPoint3D movedGeoPoint3D = (GeoPoint3D) ec.movedGeoPoint;

			if (movedGeoPoint3D.hasPath()) {

				((EuclidianController3D) ec).setMouseInformation(movedGeoPoint3D);
				movedGeoPoint3D.doPath();

			} else if (movedGeoPoint3D.hasRegion()) {

				((EuclidianController3D) ec).setMouseInformation(movedGeoPoint3D);
				movedGeoPoint3D.doRegion();
				if (movedGeoPoint3D.getRegion() == ((EuclidianController3D) ec).view3D.getxOyPlane()) {
					Coords coords = movedGeoPoint3D.getCoords();
					((EuclidianController3D) ec).checkXYMinMax(coords);
					movedGeoPoint3D.setWillingCoords(coords);
					movedGeoPoint3D.setWillingDirection(null);
					movedGeoPoint3D.doRegion();
				}
				((EuclidianController3D) ec).view3D.getCursor3D().setMoveNormalDirection(
						movedGeoPoint3D.getRegionParameters().getNormal());

			} else {

				// if (isShiftDown && mouseLoc != null){ //moves the point along
				// z-axis
				if (ec.movedGeoPoint.getMoveMode() == GeoPointND.MOVE_MODE_Z) { // moves
																				// the
																				// point
																				// along
																				// z-axis

					/*
					 * //getting current pick point and direction v if
					 * (movePointMode != MOVE_POINT_MODE_Z){ mouseLocOld =
					 * (Point) mouseLoc.clone(); positionOld =
					 * movedGeoPoint3D.getCoords().copyVector(); movePointMode =
					 * MOVE_POINT_MODE_Z; }
					 */
					Coords o = ((EuclidianController3D) ec).view3D.getPickPoint(ec.mouseLoc);
					((EuclidianController3D) ec).view3D.toSceneCoords3D(o);
					// GgbVector o =
					// view3D.getPickFromScenePoint(positionOld,mouseLoc.x-mouseLocOld.x,mouseLoc.y-mouseLocOld.y);
					// view3D.toSceneCoords3D(o);

					// getting new position of the point
					Coords project = movedGeoPoint3D.getCoords()
							.projectNearLine(o, ((EuclidianController3D) ec).view3D.getViewDirection(),
									Coords.VZ);

					// max z value
					if (project.getZ() > ((EuclidianController3D) ec).zMinMax[1])
						project.setZ(((EuclidianController3D) ec).zMinMax[1]);
					else if (project.getZ() < ((EuclidianController3D) ec).zMinMax[0])
						project.setZ(((EuclidianController3D) ec).zMinMax[0]);
					
					// capturing points
					switch (ec.view.getPointCapturingMode()) {
					case EuclidianStyleConstants.POINT_CAPTURING_STICKY_POINTS:
						//TODO
					case EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC:
						if (!ec.view.isGridOrAxesShown()) {
							break;
						}
					case EuclidianStyleConstants.POINT_CAPTURING_ON:
					case EuclidianStyleConstants.POINT_CAPTURING_ON_GRID:
						double z0 = project.getZ();
						double gz = ec.view.getGridDistances(0);
						double z = Kernel.roundToScale(z0, gz);
						if (ec.view.getPointCapturingMode() == EuclidianStyleConstants.POINT_CAPTURING_ON_GRID
								|| Math.abs(z-z0) < gz * EuclidianStyleConstants.POINT_CAPTURING_GRID){
							project.setZ(z);
						}
					}

					// set point coords
					movedGeoPoint3D.setCoords(project);

					// update the moving plane altitude
					((EuclidianController3D) ec).getCurrentPlane().set(movedGeoPoint3D.getCoords(), 4);

				} else {

					((EuclidianController3D) ec).movePointOnCurrentPlane(movedGeoPoint3D, false);

				}

				// update point decorations
				((EuclidianController3D) ec).view3D.updatePointDecorations(movedGeoPoint3D);

			}

			// update 3D cursor coordinates (false : no path or region update)
			((EuclidianController3D) ec).view3D.getCursor3D().setCoords(movedGeoPoint3D.getCoords(), false);
			((EuclidianController3D) ec).view3D.updateMatrixForCursor3D();

			if (repaint) {
				movedGeoPoint3D.updateRepaint();// for highlighting in
												// algebraView
			} else {
				movedGeoPoint3D.updateCascade();// TODO modify
												// movedGeoPoint3D.updateCascade()
			}

			// update previewable
			if (ec.view.getPreviewDrawable() != null)
				ec.view.updatePreviewable();

			// geo point has been moved
			ec.movedGeoPointDragged = true;

		} else { // 2D point
			Coords o = ((EuclidianController3D) ec).view3D.getPickPoint(ec.mouseLoc);
			((EuclidianController3D) ec).view3D.toSceneCoords3D(o);
			// TODO do this once
			// GgbVector v = new GgbVector(new double[] {0,0,1,0});
			// view3D.toSceneCoords3D(view3D.getViewDirection());
			Coords coords = o.projectPlaneThruVIfPossible(
					CoordMatrix4x4.Identity(), ((EuclidianController3D) ec).view3D.getViewDirection())[1]; // TODO
																				// use
																				// current
																				// region
																				// instead
																				// of
																				// identity
			
			// capturing points
			switch (ec.view.getPointCapturingMode()) {
			case EuclidianStyleConstants.POINT_CAPTURING_STICKY_POINTS:
				//TODO
			case EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC:
				if (!ec.view.isGridOrAxesShown()) {
					break;
				}
			case EuclidianStyleConstants.POINT_CAPTURING_ON:
			case EuclidianStyleConstants.POINT_CAPTURING_ON_GRID:
				double x0 = coords.getX();
				double y0 = coords.getY();
				double gx = ec.view.getGridDistances(0);
				double gy = ec.view.getGridDistances(1);
				double x = Kernel.roundToScale(x0, gx);
				double y = Kernel.roundToScale(y0, gy);
				//App.debug("\n"+x+"\n"+y+"\np=\n"+project);
				if (ec.view.getPointCapturingMode() == EuclidianStyleConstants.POINT_CAPTURING_ON_GRID
						|| (Math.abs(x-x0) < gx * EuclidianStyleConstants.POINT_CAPTURING_GRID 
								&& Math.abs(y-y0) < gy * EuclidianStyleConstants.POINT_CAPTURING_GRID)){
					coords.setX(x);
					coords.setY(y);
				}
			}

			ec.xRW = coords.getX();
			ec.yRW = coords.getY();
			super.movePoint(repaint, ((EuclidianController3D) ec).mouseEvent);

			((EuclidianController3D) ec).view3D.getCursor3D()
					.setCoords(ec.movedGeoPoint.getCoordsInD3(), false);

		}
	}
	
	
	/**
	 * create a new free point or update the preview point
	 */
	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable, boolean complex) {

		GeoPoint3D point3D;

		if (!forPreviewable) {
			// if there's "no" 3D cursor, no point is created
			if (((EuclidianController3D) ec).view3D.getCursor3DType() == EuclidianView3D.PREVIEW_POINT_NONE)
				return null;
			point3D = (GeoPoint3D) ec.kernel.getManager3D().Point3D(null, 0, 0, 0,
					false);
		} else {
			point3D = createNewFreePoint(complex);
			if (point3D == null)
				return null;
			point3D.setPath(null);
			point3D.setRegion(null);
			((EuclidianController3D) ec).view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_FREE);
			return point3D;
		}

		((EuclidianController3D) ec).setCurrentPlane(CoordMatrix4x4.Identity());
		((EuclidianController3D) ec).movePointOnCurrentPlane(point3D, false);

		return point3D;
	}
	
	

	/**
	 * create a new path point or update the preview point
	 */
	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable, Path path,
			boolean complex) {

		GeoPoint3D point3D;

		if (!forPreviewable)
			point3D = (GeoPoint3D) ec.getKernel().getManager3D().Point3D(null,
					path, false);
		else {
			point3D = ((EuclidianController3D) ec).view3D.getCursor3D();
			point3D.setPath(path);
			point3D.setRegion(null);
			((EuclidianController3D) ec).view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_PATH);
		}

		((EuclidianController3D) ec).setMouseInformation(point3D);
		/*
		 * if (((GeoElement) path).isGeoList())
		 * Application.printStacktrace("TODO: path==GeoList"); else
		 */
		point3D.doPath();

		return point3D;
	}
	
	
	/**
	 * create a new region point or update the preview point
	 */
	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable, Region region,
			boolean complex) {

		GeoPoint3D point3D;

		point3D = ((EuclidianController3D) ec).view3D.getCursor3D();
		point3D.setPath(null);
		point3D.setRegion(region);

		((EuclidianController3D) ec).setMouseInformation(point3D);
		point3D.doRegion();
		point3D.setMoveNormalDirection(point3D.getRegionParameters()
				.getNormal());

		// App.debug(point3D);

		if (region == ((EuclidianController3D) ec).view3D.getxOyPlane()) {
			Coords coords = point3D.getInhomCoords();
			if (coords.getX() < ((EuclidianController3D) ec).view3D.getxOyPlane().getXmin()
					|| coords.getX() > ((EuclidianController3D) ec).view3D.getxOyPlane().getXmax()
					|| coords.getY() < ((EuclidianController3D) ec).view3D.getxOyPlane().getYmin()
					|| coords.getY() > ((EuclidianController3D) ec).view3D.getxOyPlane().getYmax()) {
				((EuclidianController3D) ec).view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_NONE);
				return null;
			}
		}

		((EuclidianController3D) ec).view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_REGION);

		if (!forPreviewable) {
			GeoPoint3D ret = (GeoPoint3D) ec.getKernel().getManager3D().Point3DIn(
					null, region, false);
			ret.set((GeoElement) point3D);
			// ret.setRegion(region);
			ret.doRegion();

			// Application.debug("ici");

			return ret;
		}

		return point3D;

	}
	
	

	/**
	 * 
	 * @param complex
	 *            says if complex coords are wanted
	 * @return new free point (eventually on xOy plane with 2D mouse)
	 */
	protected GeoPoint3D createNewFreePoint(boolean complex) {
		return (GeoPoint3D) createNewPoint(true, ((EuclidianController3D) ec).view3D.getxOyPlane(), complex);
	}

	
	

}
