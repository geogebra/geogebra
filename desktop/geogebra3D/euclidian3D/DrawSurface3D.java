package geogebra3D.euclidian3D;

import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.plots.SurfaceMesh2;
import geogebra3D.kernel3D.GeoSurfaceCartesian3D;

/**
 * Class for drawing a 2-var function
 * @author matthieu
 *
 */
public class DrawSurface3D extends Drawable3DSurfaces {
	
	/** The mesh currently being rendered - is occasionally reset */
	private SurfaceMesh2 mesh;
	
	/** The function being rendered */
	GeoSurfaceCartesian3D surface;
	
	/** current domain for the function on the format {xmin, xmax, ymin, ymax} */
	private double[] domain = new double[4];
	
	/** Current culling box - set to view3d.(x|y|z)(max|min) */
	private double[] cullingBox = new double[6];

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
		
		updateDomain();
		
		updateCullingBox();
		
		mesh = new SurfaceMesh2(surface, cullingBox, domain);
	}
	
	private boolean updateDomain(){
		boolean changed = false;
		
		double t = surface.getMinParameter(0);
		if(t != domain[0]) {
			changed = true;
			domain[0] = t;
		}
		t = surface.getMaxParameter(0);
		if(t != domain[1]) {
			changed = true;
			domain[1] = t;
		}
		t = surface.getMinParameter(1);
		if(t != domain[2]) {
			changed = true;
			domain[2] = t;
		}
		t = surface.getMaxParameter(1);
		if(t != domain[3]) {
			changed = true;
			domain[3] = t;
		}
		
		return changed;
	}
	
	private void updateCullingBox(){
		EuclidianView3D view = getView3D();
		cullingBox[0] = view.getXMinMax()[0];
		cullingBox[1] = view.getXMinMax()[1];
		cullingBox[2] = view.getYMinMax()[0];
		cullingBox[3] = view.getYMinMax()[1];
		cullingBox[4] = view.getZMinMax()[0];
		cullingBox[5] = view.getZMinMax()[1];
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
	
	@Override
	protected boolean updateForItSelf(){
		if(elementHasChanged){
			if (updateDomain()) {
				//domain has changed - create a new mesh
				mesh = new SurfaceMesh2(surface, cullingBox, domain);
			} else {
				//otherwise, update the surface
				elementHasChanged = false;
				mesh.updateParameters();
			}
		}
		
		Renderer renderer = getView3D().getRenderer();
		mesh.setCullingBox(cullingBox);
		boolean ret = mesh.optimize();
		
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

		return false;
	}
	
	protected void updateForView(){
		updateCullingBox();
	}

	public int getPickOrder() {
		return DRAW_PICK_ORDER_2D;
	}

	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_CLIPPED_SURFACES);
	}
    
    public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_CLIPPED_SURFACES);
    }
    

}
