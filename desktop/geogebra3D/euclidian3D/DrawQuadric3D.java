package geogebra3D.euclidian3D;

import geogebra.common.euclidian.Previewable;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoQuadric3D;

import java.util.ArrayList;

/**
 * Class for drawing quadrics.
 * @author matthieu
 *
 */
public class DrawQuadric3D extends Drawable3DSurfaces
implements Previewable {
	
	


	/**
	 * common constructor
	 * @param a_view3d
	 * @param a_quadric
	 */
	public DrawQuadric3D(EuclidianView3D a_view3d, GeoQuadric3D a_quadric) {
		
		super(a_view3d, a_quadric);
		
	}
	
	@Override
	public void drawGeometry(Renderer renderer) {
		renderer.setLayer(getGeoElement().getLayer());
		renderer.getGeometryManager().draw(getSurfaceIndex());
		renderer.setLayer(0);
	}
	
	@Override
	protected void drawSurfaceGeometry(Renderer renderer){
		drawGeometry(renderer);
	}

	@Override
	void drawGeometryHiding(Renderer renderer) {
		drawSurfaceGeometry(renderer);
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawGeometryPicked(Renderer renderer) {
		// TODO Auto-generated method stub

	}
	
	
	
	@Override
	public void drawOutline(Renderer renderer) {
		// no outline
	}

	
	
	
	
	private int longitude = 0;
	
	
	
	@Override
	protected boolean updateForItSelf(){
		
		Renderer renderer = getView3D().getRenderer();
		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		PlotterSurface surface;
		
		switch(quadric.getType()){
		case GeoQuadricNDConstants.QUADRIC_SPHERE:
			surface = renderer.getGeometryManager().getSurface();
			surface.start();
			longitude = surface.drawSphere(quadric.getMidpoint3D(), quadric.getHalfAxis(0), getView3D().getScale());
			setSurfaceIndex(surface.end());			
			break;
			
		case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			double[] minmax;
			float min, max;
			
			minmax = getMinMax();
			
			
			
			min = (float) minmax[0]; 
			max = (float) minmax[1];		

			surface = renderer.getGeometryManager().getSurface();
			surface.start(quadric);
			surface.setU((float) quadric.getMinParameter(0), (float) quadric.getMaxParameter(0));surface.setNbU(60);
			
			setSurfaceV(min, max, surface);
			
			surface.draw();
			
			setSurfaceIndex(surface.end());
			
			break;

		}
		
		
		return true;
	}


	/**
	 * 
	 * @return min and max value along the axis of the quadric
	 */
	protected double[] getMinMax(){
		
		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		
		Coords o = getView3D().getToScreenMatrix().mul(quadric.getMidpoint());
		Coords v = getView3D().getToScreenMatrix().mul(quadric.getEigenvec3D(2));
		
		double[] minmax = getView3D().getRenderer().getIntervalInFrustum(
				new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
				o, v, true);

		return minmax;
	}
	
	
	protected void setSurfaceV(float min, float max, PlotterSurface surface){
		float fade = (max-min)/10f;

		switch(((GeoQuadric3D) getGeoElement()).getType()){
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			surface.setV(min,max);surface.setNbV(3);
			surface.setVFading(fade, fade);
			break;

		case GeoQuadricNDConstants.QUADRIC_CONE:
			if (min*max<0){
				surface.setV(min,0);surface.setNbV(2);surface.setVFading(fade, 0);surface.draw();
				surface.setV(0,max);surface.setNbV(2);surface.setVFading(0, fade);surface.draw();
			}else{
				surface.setV(min,max);surface.setNbV(3);surface.setVFading(fade, fade);surface.draw();
			}
			break;
		}
		
		
	}
	
	
	
	@Override
	protected void updateForView(){
		
		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		
		switch(quadric.getType()){
		case GeoQuadricNDConstants.QUADRIC_SPHERE:
			if (getView3D().viewChangedByZoom()){
				Renderer renderer = getView3D().getRenderer();		
				PlotterSurface surface;

				surface = renderer.getGeometryManager().getSurface();
				
				// check if longitude length changes
				int l = surface.calcSphereLongitudesNeeded(quadric.getHalfAxis(0), getView3D().getScale());
				if (l != longitude){
					//App.debug(l+","+longitude);
					longitude = l;
					surface.start();
					surface.drawSphere(quadric.getMidpoint3D(), quadric.getHalfAxis(0), longitude);
					setSurfaceIndex(surface.end());
					recordTrace();
				}
							
			}
			break;
		case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			updateForItSelf();
			break;
		}
	}
	
	
	
	
	
	
	
	


	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_2D;
	}
	
	
	


	@Override
	public void addToDrawable3DLists(Drawable3DLists lists){
		switch(((GeoQuadric3D) getGeoElement()).getType()){
		case GeoQuadricNDConstants.QUADRIC_SPHERE:
		case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			addToDrawable3DLists(lists,DRAW_TYPE_CLOSED_SURFACES_CURVED);
			break;
		default:
			addToDrawable3DLists(lists,DRAW_TYPE_SURFACES);
		}
	}
    
    @Override
	public void removeFromDrawable3DLists(Drawable3DLists lists){
    	switch(((GeoQuadric3D) getGeoElement()).getType()){
		case GeoQuadricNDConstants.QUADRIC_SPHERE:
		case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			removeFromDrawable3DLists(lists,DRAW_TYPE_CLOSED_SURFACES_CURVED);
			break;
		default:
			removeFromDrawable3DLists(lists,DRAW_TYPE_SURFACES);
		}
    }

	

	////////////////////////////////
	// Previewable interface 
	
	
	@SuppressWarnings("rawtypes")
	private ArrayList selectedPoints;

	/**
	 * constructor for previewable
	 * @param view3D
	 * @param selectedPoints
	 * @param cs1D
	 */
	@SuppressWarnings("unchecked")
	public DrawQuadric3D(EuclidianView3D view3D, ArrayList selectedPoints, int type){
		
		super(view3D);
		
		GeoQuadric3D q = new GeoQuadric3D(view3D.getKernel().getConstruction());
		setGeoElement(q);
		q.setIsPickable(false);
		q.setType(type);
		//setGeoElement(q);
		
		this.selectedPoints = selectedPoints;
		
		updatePreview();
		
	}	

	




	public void updateMousePos(double xRW, double yRW) {	
		
	}


	public void updatePreview() {
		
		GeoPointND firstPoint = null;
		GeoPointND secondPoint = null;
		if (selectedPoints.size()>=1){
			firstPoint = (GeoPointND) selectedPoints.get(0);
			if (selectedPoints.size()==2)
				secondPoint = (GeoPointND) selectedPoints.get(1);
			else
				secondPoint = getView3D().getCursor3D();
		}
			
		
		if (selectedPoints.size()>=1){		
			((GeoQuadric3D) getGeoElement()).setSphereND(firstPoint, secondPoint);
			getGeoElement().setEuclidianVisible(true);
			setWaitForUpdate();
		}else{
			getGeoElement().setEuclidianVisible(false);
		}
		
			
	}

}
