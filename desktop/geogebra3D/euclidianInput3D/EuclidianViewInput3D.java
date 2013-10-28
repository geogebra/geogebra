package geogebra3D.euclidianInput3D;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra3D.awt.GPointWithZ;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.euclidian3D.opengl.PlotterCursor;
import geogebra3D.euclidian3D.opengl.Renderer;

/**
 * EuclidianView3D with controller using 3D input
 * @author mathieu
 *
 */
public class EuclidianViewInput3D extends EuclidianView3D{

	/**
	 * constructor
	 * @param ec euclidian controller
	 * @param settings settings
	 */
	public EuclidianViewInput3D(EuclidianController3D ec,
			EuclidianSettings settings) {
		super(ec, settings);
		
		mouse3DScenePosition = new Coords(4);
		mouse3DScenePosition.setW(1);
		
		startPos = new Coords(4);
		startPos.setW(1);
	}
	
	
	private Coords mouse3DScreenPosition = null;
	private Coords mouse3DScenePosition;
	
	
	@Override
	public void drawMouseCursor(Renderer renderer1){
		
		if (((EuclidianControllerInput3D) euclidianController).isMouse3DPressed()){
			mouse3DScreenPosition = null;
			return;
		}
		
		//use a 3D mouse position
		mouse3DScreenPosition = ((EuclidianControllerInput3D) getEuclidianController()).getMouse3DPosition();
		
		mouse3DScenePosition.set(mouse3DScreenPosition);
		toSceneCoords3D(mouse3DScenePosition);
		for (int i = 1; i <= 3 ; i++){
			transparentMouseCursorMatrix.set(i,i,1/getScale());
		}
		transparentMouseCursorMatrix.setOrigin(mouse3DScenePosition);
		
		
		drawMouseCursor(renderer1, mouse3DScreenPosition);
		
	}
	
	
	private CoordMatrix4x4 transparentMouseCursorMatrix = new CoordMatrix4x4(); 
	
	@Override
	public void drawTransp(Renderer renderer1){
			

		// sphere for mouse cursor
		if (getMode() == EuclidianConstants.MODE_MOVE && mouse3DScreenPosition != null){
			renderer1.setMatrix(transparentMouseCursorMatrix);
			renderer1.drawCursor(PlotterCursor.TYPE_SPHERE);	
		}
		
		super.drawTransp(renderer1);

	}
	
	@Override
	public void drawHiding(Renderer renderer1){
		
		// sphere for mouse cursor
		if (getMode() == EuclidianConstants.MODE_MOVE && mouse3DScreenPosition != null){
			renderer1.setMatrix(transparentMouseCursorMatrix);
			renderer1.drawCursor(PlotterCursor.TYPE_SPHERE);	
		}
		
		super.drawHiding(renderer1);

	}

	
	@Override
	public boolean isPolarized(){
		return true;
	}
	
	@Override
	public float getScreenZOffsetFactor(){
		return 1f;
	}
	
	private Coords startPos;
	
	private CoordMatrix4x4 startTranslation = CoordMatrix4x4.Identity();
	//private CoordMatrix4x4 startTranslationScreen = CoordMatrix4x4.Identity();
	
	/**
	 * set mouse start pos
	 * @param screenStartPos mouse start pos (screen)
	 */
	public void setStartPos(Coords screenStartPos){
		
		startPos.set(screenStartPos);
		toSceneCoords3D(startPos);		
		startTranslation.setOrigin(screenStartPos.add(startPos));
		
	}
	
	/**
	 * set the coord system regarding 3D mouse move
	 * @param startPos1 start 3D position (screen)
	 * @param newPos current 3D position (screen)
	 * @param rotX relative mouse rotate around x (screen)
	 * @param rotZ relative mouse rotate around z (view)
	 */
	public void setCoordSystemFromMouse3DMove(Coords startPos1, Coords newPos, double rotX, double rotZ) {	
		
		
		// translation
		Coords v = new Coords(4);
		v.set(newPos.sub(startPos1));
		toSceneCoords3D(v);
		
		// rotation
		setRotXYinDegrees(aOld+rotX, bOld+rotZ);
		
		updateRotationAndScaleMatrices();
		
		// center rotation on pick point ( + v for translation)
		CoordMatrix m1 = rotationAndScaleMatrix.inverse().mul(startTranslation).mul(rotationAndScaleMatrix);		
		Coords t1 = m1.getOrigin();	
		setXZero(t1.getX() - startPos.getX() + v.getX());
		setYZero(t1.getY() - startPos.getY() + v.getY());
		setZZero(t1.getZ() - startPos.getZ() + v.getZ());
				
		// update the view
		updateTranslationMatrix();
		setGlobalMatrices();
		
		setViewChangedByTranslate();
		setViewChangedByRotate();
		setWaitForUpdate();
		
		
	}	
	
	@Override
	public void setTransparentCursor() {

		// 3D cursor and 2D mouse cursor are independents
		setDefault2DCursor();

	}
	
	
	
	public void setHasMouse(boolean flag){
		hasMouse = flag;
	}
	


	@Override
	protected void setPickPointFromMouse(GPoint mouse) {
		super.setPickPointFromMouse(mouse);

		if (mouse instanceof GPointWithZ){
			pickPoint.setZ(((GPointWithZ) mouse).getZ());
		}
	}
	
	@Override
	protected void drawFreeCursor(Renderer renderer1){
		//free point in space
		renderer1.drawCursor(PlotterCursor.TYPE_CROSS3D);	
	}
	
}
