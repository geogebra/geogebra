package geogebra3D.euclidian3D;

import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.Matrix.Coords;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.plots.SurfaceMesh2;

/**
 * Class for drawing a 2-var function
 * @author matthieu
 *
 */
public class DrawFunction2Var extends Drawable3DSurfaces {
	
	private SurfaceMesh2 mesh;
	
	private GeoFunctionNVar function;
	
	
	private boolean unlimitedRange;
	
	private double lastBaseRadius;
	
	private static final double unlimitedScaleFactor = 1.3;
	
	private double savedRadius;

	private double[] params = new double[4];

	/**
	 * common constructor
	 * @param a_view3d
	 * @param function
	 */
	public DrawFunction2Var(EuclidianView3D a_view3d, GeoFunctionNVar function) {
		super(a_view3d, function);
		this.function=function;
		
		/*
		Application.debug("function on ["
				+function.getMinParameter(0)+","+function.getMaxParameter(0)
				+"]x["
				+function.getMinParameter(1)+","+function.getMaxParameter(1)
				+"]"
		);
		*/
		
		setParams();

		if (Double.isNaN(function.getMinParameter(0))){
			unlimitedRange=true;
		}else{
			unlimitedRange=false;
		}
		
		updateRadius();
		
		if(unlimitedRange){
			lastBaseRadius=savedRadius*unlimitedScaleFactor;
			mesh = new SurfaceMesh2(function, lastBaseRadius, true);
		} else
			mesh = new SurfaceMesh2(function, savedRadius, false);
	}
	
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
	}

	void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}

	public void drawGeometryHidden(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	public void drawGeometryPicked(Renderer renderer) {
		// TODO Auto-generated method stub

	}
	
	private void setParams(){
		params[0]=function.getMinParameter(0);
		params[1]=function.getMaxParameter(0);
		params[2]=function.getMinParameter(1);
		params[3]=function.getMaxParameter(1);
	}
	
	/** 
	 * gets the viewing radius based on the viewing frustum 
	 */
	private void updateRadius() {
		EuclidianView3D view = getView3D();
		Renderer temp = view.getRenderer();
		double x1 = temp.getLeft();
		double x2 = temp.getRight();
		double y1 = temp.getTop();
		double y2 = temp.getBottom();
		double z1 = temp.getFront(true);
		double z2 = temp.getBack(true);
		Coords [] v = new Coords[8];
		v[0] = new Coords(x1,y1,z1,0);
		v[1] = new Coords(x1,y2,z1,0);
		v[2] = new Coords(x1,y1,z2,0);
		v[3] = new Coords(x1,y2,z2,0);
		v[4] = new Coords(x2,y1,z1,0);
		v[5] = new Coords(x2,y2,z1,0);
		v[6] = new Coords(x2,y1,z2,0);
		v[7] = new Coords(x2,y2,z2,0);

		savedRadius=0;
		double norm;
		for(int i = 0; i < 8; i++){
			view.toSceneCoords3D(v[i]);
			norm = v[i].norm();
			if(norm>savedRadius)
				savedRadius=norm;
		}
	}
	
	protected boolean updateForItSelf(){
		//super.updateForItSelf();
		boolean ret = true;
		
		if(elementHasChanged){
			if( function.getMinParameter(0)!=params[0] || 
					function.getMaxParameter(0)!=params[1] || 
					function.getMinParameter(1)!=params[2] || 
					function.getMaxParameter(1)!=params[3] ){
				if(unlimitedRange){
					lastBaseRadius=savedRadius*unlimitedScaleFactor;
					mesh = new SurfaceMesh2(function, lastBaseRadius, true);
				} else
					mesh = new SurfaceMesh2(function, savedRadius, false);
				setParams();
			} else {
				//otherwise, update the surface
				elementHasChanged = false;
				mesh.updateParameters();
			}
		}
		
		Renderer renderer = getView3D().getRenderer();
		mesh.setRadius(savedRadius);
		ret = mesh.optimize();
		
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		GeoFunctionNVar geo = (GeoFunctionNVar) getGeoElement();
		surface.start(geo);
		
		float uMin, uMax, vMin, vMax;
		if (unlimitedRange){
			uMin = -1; uMax = 1; vMin = -1; vMax = 1;
		}else{
			uMin = (float) geo.getMinParameter(0);
			uMax = (float) geo.getMaxParameter(0);
			vMin = (float) geo.getMinParameter(1);
			vMax = (float) geo.getMaxParameter(1);
		}
		
		surface.setU(uMin,uMax);
		surface.setNbU((int) (uMax-uMin)*10);
		surface.setV(vMin, vMax);
		surface.setNbV((int) (vMax-vMin)*10);
		
		//TODO use fading texture
		
		surface.draw(mesh);
		setGeometryIndex(surface.end());

		return ret;
	}
	
	protected void updateForView(){
		updateRadius();
		if(unlimitedRange && savedRadius>lastBaseRadius){
			lastBaseRadius=savedRadius*unlimitedScaleFactor;
			function.setInterval(new double[] {-lastBaseRadius,lastBaseRadius}, 
								 new double [] {-lastBaseRadius,lastBaseRadius});
			mesh = new SurfaceMesh2(function, lastBaseRadius, true);
		} else if(unlimitedRange && savedRadius<lastBaseRadius/unlimitedScaleFactor*.5) {
			lastBaseRadius=savedRadius/unlimitedScaleFactor;
			function.setInterval(new double[] {-lastBaseRadius,lastBaseRadius}, 
								 new double [] {-lastBaseRadius,lastBaseRadius});
			mesh = new SurfaceMesh2(function, lastBaseRadius, true);
		} 
//		else if(oldRadius!=savedRadius && mesh != null)
//			mesh.turnOnUpdates();
	}

	public int getPickOrder() {
		return DRAW_PICK_ORDER_2D;
	}
	


	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_CLOSED_SURFACES);
	}
    
    public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_CLOSED_SURFACES);
    }
    

}
