package geogebra3D.euclidian3D;

import geogebra.kernel.Matrix.Coords;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.plots.ParametricSurfaceMesh;
import geogebra3D.euclidian3D.plots.SurfaceMesh2;
import geogebra3D.kernel3D.GeoSurfaceCartesian3D;

/**
 * Class for drawing a 2-var function
 * @author matthieu
 *
 */
public class DrawSurface3D extends Drawable3DSurfaces {
	
	private SurfaceMesh2 mesh;	
	private double savedRadius;
	
	GeoSurfaceCartesian3D surface;
	private double[] params = new double[4];

	/**
	 * common constructor
	 * @param a_view3d
	 * @param function
	 */
	public DrawSurface3D(EuclidianView3D a_view3d, GeoSurfaceCartesian3D surface) {
		super(a_view3d, surface);
		this.surface=surface;
		
		/*
		Application.debug("function on ["
				+function.getMinParameter(0)+","+function.getMaxParameter(0)
				+"]x["
				+function.getMinParameter(1)+","+function.getMaxParameter(1)
				+"]"
		);
		*/
		
		setParams();
		
		updateRadius();
		
		mesh = new SurfaceMesh2(surface, savedRadius, false);
	}
	
	private void setParams(){
		params[0]=surface.getMinParameter(0);
		params[1]=surface.getMaxParameter(0);
		params[2]=surface.getMinParameter(1);
		params[3]=surface.getMaxParameter(1);
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
			if( surface.getMinParameter(0)!=params[0] || 
					surface.getMaxParameter(0)!=params[1] || 
					surface.getMinParameter(1)!=params[2] || 
					surface.getMaxParameter(1)!=params[3] ){
				mesh = new SurfaceMesh2(surface, savedRadius, false);
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
		GeoSurfaceCartesian3D geo = (GeoSurfaceCartesian3D) getGeoElement();
		surface.start(geo);
		
		float uMin, uMax, vMin, vMax;
		
		uMin = (float) geo.getMinParameter(0);
		uMax = (float) geo.getMaxParameter(0);
		vMin = (float) geo.getMinParameter(1);
		vMax = (float) geo.getMaxParameter(1);
		
		
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
		//updateRadius();
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
