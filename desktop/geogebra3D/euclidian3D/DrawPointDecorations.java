package geogebra3D.euclidian3D;

import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoPoint3D;

/**
 * Class for drawing decorations of points (altitude segment from the point to xOy plane, ...)
 * @author matthieu
 *
 */
public class DrawPointDecorations extends DrawCoordSys1D {
	
	//private GgbMatrix4x4 segmentMatrix;
	private CoordMatrix4x4 planeMatrix;
	
	private Coords p1, p2;
	
	
	

	/**
	 * common constructor
	 * @param aView3d
	 */
	public DrawPointDecorations(EuclidianView3D aView3d) {
		super(aView3d);
		
		setDrawMinMax(0, 1);
		
		
		p1 = new Coords(4);
		p1.setW(1);
		
		p2 = p1.copyVector();
		
		planeMatrix = CoordMatrix4x4.Identity();
		planeMatrix.setVx((Coords) EuclidianView3D.vx.mul(0.2)); 
		planeMatrix.setVy((Coords) EuclidianView3D.vy.mul(0.2));
		

		setWaitForUpdate();
	}
	
	
	
	@Override
	protected boolean isVisible(){
		return true; //no geoelement connected
	}
	
	
	
	/** set the point for which decorations are made
	 * @param point
	 */
	public void setPoint(GeoPoint3D point){
		
		p1 = point.getCoords();
				
		//set origin to projection of the point on xOy plane
		p2 = new Coords(4);
		p2.set(p1);
		p2.set(3, 0);

		planeMatrix.setOrigin(p2);
		
		setWaitForUpdate();
		
	}
	


	
	
	@Override
	public void drawHidden(Renderer renderer){
		
		renderer.getTextures().setDashFromLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		drawOutline(renderer);		

	} 
	
	@Override
	public void drawOutline(Renderer renderer) {
		
		renderer.setColor(new Coords(0, 0, 0, 1));//black
		drawGeometry(renderer);

	}
	
	


	
	@Override
	protected boolean updateForItSelf() {
		
		updateForItSelf(p1, p2);
		
		return true;
	}
	
	@Override
	protected void updateLabel(){
		//nothing to do : there's no label
	}


	@Override
	protected int getLineThickness(){
		return 1;
	}
	

	@Override
	protected void updateForView(){
		if (getView3D().viewChangedByZoom())
			updateForItSelf();
	}


	///////////////////////////////////////////
	// UNUSED METHODS
	///////////////////////////////////////////
	
	@Override
	public void drawGeometryPicked(Renderer renderer) { }
	@Override
	public int getPickOrder() {return 0;}
	@Override
	public boolean isTransparent() {return false;}


	@Override
	protected double getColorShift(){
		return COLOR_SHIFT_NONE;
	}

	@Override
	protected void updateColors(){
		
	}
}
