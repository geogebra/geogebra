package geogebra3D.euclidianInput3D;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.euclidian3D.Input3D;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterCursor;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3DConstant;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra3D.awt.GPointWithZ;
import geogebra3D.euclidian3D.EuclidianView3DD;
import geogebra3D.euclidian3D.opengl.RendererLogicalPickingGL2;

/**
 * EuclidianView3D with controller using 3D input
 * 
 * @author mathieu
 * 
 */
public class EuclidianViewInput3D extends EuclidianView3DD {
	
	private Input3D input3D;

	/**
	 * constructor
	 * 
	 * @param ec
	 *            euclidian controller
	 * @param settings
	 *            settings
	 */
	public EuclidianViewInput3D(EuclidianController3D ec,
			EuclidianSettings settings) {
		super(ec, settings);
		
		input3D = ((EuclidianControllerInput3D) ec).input3D;

		mouse3DScenePosition = new Coords(4);
		mouse3DScenePosition.setW(1);

		startPos = new Coords(4);
		startPos.setW(1);
	}

	private Coords mouse3DScreenPosition = null;
	private Coords mouse3DScenePosition;

	@Override
	public void drawMouseCursor(Renderer renderer1) {
		
		if (input3D.currentlyUseMouse2D()){
			return;
		}

		// use a 3D mouse position
		mouse3DScreenPosition = ((EuclidianControllerInput3D) getEuclidianController())
				.getMouse3DPosition();

		if (((EuclidianControllerInput3D) getEuclidianController()).useInputDepthForHitting()) {
			mouse3DScenePosition.set(mouse3DScreenPosition);
			toSceneCoords3D(mouse3DScenePosition);
			for (int i = 1; i <= 3; i++) {
				transparentMouseCursorMatrix.set(i, i, 1 / getScale());
			}
			transparentMouseCursorMatrix.setOrigin(mouse3DScenePosition);
		}

		drawMouseCursor(renderer1, mouse3DScreenPosition);

	}

	private CoordMatrix4x4 transparentMouseCursorMatrix = new CoordMatrix4x4();

	@Override
	public void drawTransp(Renderer renderer1) {

		// sphere for mouse cursor
		if (((EuclidianControllerInput3D) getEuclidianController()).useInputDepthForHitting() 
				&& mouse3DScreenPosition != null) {
			renderer1.setMatrix(transparentMouseCursorMatrix);
			if (getCursor3DType() == PREVIEW_POINT_FREE){
				renderer1.drawCursor(PlotterCursor.TYPE_SPHERE);
			}else{
				renderer1.drawCursor(PlotterCursor.TYPE_SPHERE_HIGHLIGHTED);
			}
		}

		super.drawTransp(renderer1);

	}

	@Override
	public void drawHiding(Renderer renderer1) {

		// sphere for mouse cursor
		if (((EuclidianControllerInput3D) getEuclidianController()).useInputDepthForHitting() 
				&& mouse3DScreenPosition != null) {
			renderer1.setMatrix(transparentMouseCursorMatrix);
			renderer1.drawCursor(PlotterCursor.TYPE_SPHERE);
		}

		super.drawHiding(renderer1);

	}

	@Override
	public boolean isPolarized() {
		return true;
	}

	@Override
	public double getScreenZOffset() {
		//App.debug(""+clippingCubeDrawable.getHorizontalDiagonal());
		return clippingCubeDrawable.getHorizontalDiagonal() / 2;
	}

	private Coords startPos;

	private CoordMatrix4x4 startTranslation = CoordMatrix4x4.Identity();

	// private CoordMatrix4x4 startTranslationScreen =
	// CoordMatrix4x4.Identity();

	/**
	 * set mouse start pos
	 * 
	 * @param screenStartPos
	 *            mouse start pos (screen)
	 */
	public void setStartPos(Coords screenStartPos) {

		startPos.set(screenStartPos);
		toSceneCoords3D(startPos);
		startTranslation.setOrigin(screenStartPos.add(startPos));

	}

	/**
	 * set the coord system regarding 3D mouse move
	 * 
	 * @param startPos1
	 *            start 3D position (screen)
	 * @param newPos
	 *            current 3D position (screen)
	 * @param rotX
	 *            relative mouse rotate around x (screen)
	 * @param rotZ
	 *            relative mouse rotate around z (view)
	 */
	public void setCoordSystemFromMouse3DMove(Coords startPos1, Coords newPos,
			double rotX, double rotZ) {

		// translation
		Coords v = new Coords(4);
		v.set(newPos.sub(startPos1));
		toSceneCoords3D(v);

		// rotation
		setRotXYinDegrees(aOld + rotX, bOld + rotZ);

		updateRotationAndScaleMatrices();

		// center rotation on pick point ( + v for translation)
		CoordMatrix m1 = rotationAndScaleMatrix.inverse().mul(startTranslation)
				.mul(rotationAndScaleMatrix);
		Coords t1 = m1.getOrigin();
		setXZero(t1.getX() - startPos.getX() + v.getX());
		setYZero(t1.getY() - startPos.getY() + v.getY());
		setZZero(t1.getZ() - startPos.getZ() + v.getZ());
		getSettings().updateOriginFromView(getXZero(), getYZero(), getZZero());
		// update the view
		updateTranslationMatrix();
		updateUndoTranslationMatrix();
		setGlobalMatrices();

		setViewChangedByTranslate();
		setViewChangedByRotate();
		setWaitForUpdate();

	}

	@Override
	protected void setPickPointFromMouse(GPoint mouse) {
		super.setPickPointFromMouse(mouse);
		
		if (input3D.currentlyUseMouse2D()){
			return;
		}

		if (mouse instanceof GPointWithZ) {
			pickPoint.setZ(((GPointWithZ) mouse).getZ());
		}
	}

	@Override
	protected void drawFreeCursor(Renderer renderer1) {
		
		if (input3D.currentlyUseMouse2D()){
			super.drawFreeCursor(renderer1);
		}else{		
			// free point in space
			renderer1.drawCursor(PlotterCursor.TYPE_CROSS3D);
		}
	}

	@Override
	public GeoElement getLabelHit(geogebra.common.awt.GPoint p) {
		if (input3D.currentlyUseMouse2D()){
			return super.getLabelHit(p);
		}
		return null;
	}

	@Override
	public int getMousePickWidth() {
		if (input3D.currentlyUseMouse2D()){
			return super.getMousePickWidth();
		}
		return Renderer.MOUSE_PICK_DEPTH;
	}
	
	
	@Override
	public void setHits(PointerEventType type) {

		super.setHits(type);
		
		if (input3D.currentlyUseMouse2D()){
			return;
		}

		// not moving a geo : see if user stays on the same hit to select it
		if (getEuclidianController().getMoveMode() == EuclidianController.MOVE_NONE
				&& !input3D.getLeftButton()){
			long time = System.currentTimeMillis();
			hittedGeo.setHitted(getHits3D(), time);
			if (hittedGeo.hasLongDelay(time)){
				input3D.setLeftButtonPressed(true);
			}
		}

	}
	
	private class HittedGeo{
		
		private GeoElement geo;
		
		private long startTime;
		
		public void setHitted(Hits hits, long time){
			//App.debug("\nHittedGeo:\n"+getHits3D());
			if (hits.isEmpty()){
				geo = null;
				//App.debug("\n -- geo = null");
			}else{
				GeoElement newGeo = hits.get(0);
				if (newGeo != geo){
					geo = newGeo;
					startTime = time;
				}
				//App.debug("\n "+(time-startTime)+"-- geo = "+geo);
			}
		}
		
		/**
		 * 
		 * @param time current time
		 * @return true if hit was long enough to process left press
		 */
		public boolean hasLongDelay(long time){
			
			if (geo == null){
				return false;
			}
			
			int delay = (int) ((time-startTime) /100);
			String s = "";
			for (int i = 0 ; i < delay ; i++){
				s+="=";
			}
			for (int i = delay ; i <= 10 ; i++){
				s+=" ";
			}
			s+="|";
			App.debug("\n  hit delay : "+s);
			if ((time-startTime) > 1000){
				geo = null; // consume event
				return true;
			}
			
			return false;
		}
	}
	
	private HittedGeo hittedGeo = new HittedGeo();
	
	@Override
	protected Renderer createRenderer() {

		return new RendererLogicalPickingGL2(this, !app.isApplet());

	}

	@Override
	public boolean isMoveable(GeoElement geo) {
		
		if (input3D.currentlyUseMouse2D()){
			return super.isMoveable(geo);
		}
		
		if (geo.isGeoPlane() && geo.isIndependent() && !(geo instanceof GeoPlane3DConstant)){
			return true;
		}
		
		return super.isMoveable(geo);
	}
	
	
	@Override
	protected int getCapturingThreshold(PointerEventType type){
		if (input3D.currentlyUseMouse2D()){
			return super.getCapturingThreshold(type);
		}
		return 5 * super.getCapturingThreshold(type);
	}
	
	/*
	@Override
	protected void setDefault2DCursor() {
		setTransparentCursor();
	}
	*/
}
