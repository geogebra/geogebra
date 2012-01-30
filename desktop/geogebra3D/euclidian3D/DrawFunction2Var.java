package geogebra3D.euclidian3D;

import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.plots.SurfaceMesh2;

/**
 * Class for drawing a 2-var function
 * @author matthieu/andre
 */
public class DrawFunction2Var extends Drawable3DSurfaces {
	
	/** The mesh currently being rendered - is occasionally reset */
	private SurfaceMesh2 mesh;
	
	/** The function being rendered */
	private GeoFunctionNVar function;
	
	/** if set to true - the domain we're looking at is R^2 */
	private boolean unboundedDomain;
	
	/** current domain for the function on the format {xmin, xmax, ymin, ymax} 
	 *  when unboundedDomain is true, this will be slightly larger than the intervals in cullingBox */
	private double[] activeDomain = new double[4];
	
	/** */
	private final static double domainThreshold = 0.4;
	
	/** */
	private final static double domainScale = 1.3;
	
	/** Current culling box - set to view3d.(x|y|z)(max|min) */
	private double[] cullingBox = new double[6];

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

		if (Double.isNaN(function.getMinParameter(0))){
			unboundedDomain=true;
		}else{
			unboundedDomain=false;
		}
		

		updateDomain();
		
		updateCullingBox();
		
		mesh = new SurfaceMesh2(function, cullingBox, activeDomain);
	}
	
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
	}

	void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}

	private boolean updateDomain(){
		boolean changed = false;
		if (!unboundedDomain) {
			double t = function.getMinParameter(0);
			if(t != activeDomain[0]) {
				changed = true;
				activeDomain[0] = t;
			}
			t = function.getMaxParameter(0);
			if(t != activeDomain[1]) {
				changed = true;
				activeDomain[1] = t;
			}
			t = function.getMinParameter(1);
			if(t != activeDomain[2]) {
				changed = true;
				activeDomain[2] = t;
			}
			t = function.getMaxParameter(1);
			if(t != activeDomain[3]) {
				changed = true;
				activeDomain[3] = t;
			}
		}
		return changed;
	}
	
	private boolean updateCullingBox(){
		EuclidianView3D view = getView3D();
		cullingBox[0] = view.getXMinMax()[0];
		cullingBox[1] = view.getXMinMax()[1];
		cullingBox[2] = view.getYMinMax()[0];
		cullingBox[3] = view.getYMinMax()[1];
		cullingBox[4] = view.getZMinMax()[0];
		cullingBox[5] = view.getZMinMax()[1];
		if(unboundedDomain) {
			//see if current culling box is inside active culling box
			final double[] cb = cullingBox;
			final double[] ab = activeDomain;
			boolean refit = true;
			if (cb[0]>ab[0] && cb[2]>ab[2] && cb[1]<ab[1] && cb[3]<ab[3]) {
				//culling box inside active culling box - test if active culling box should be shrunk
				final double newBase = (cb[1]-cb[0])*(cb[3]-cb[2]);
				final double activeBase = (ab[1]-ab[0])*(ab[3]-ab[2]);
				//ratio of surface area that is visible: newBase/activeBase 
				// - if this is too small rendering will be slow
				if(newBase > domainThreshold * activeBase) {
					refit=false;
				}
			}
			if(refit){
				//re-fit active culling box
				final double[] c = new double[]{(cb[0]+cb[1])*0.5, (cb[2]+cb[3])*0.5};
				activeDomain[0] = c[0] + (cb[0]-c[0]) * domainScale;
				activeDomain[1] = c[0] + (cb[1]-c[0]) * domainScale;
				activeDomain[2] = c[1] + (cb[2]-c[1]) * domainScale;
				activeDomain[3] = c[1] + (cb[3]-c[1]) * domainScale;
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected boolean updateForItSelf(){
		if(elementHasChanged){
			if (updateDomain()) {
				//domain has changed - create a new mesh
				mesh = new SurfaceMesh2(function, cullingBox, activeDomain);
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
		GeoFunctionNVar geo = (GeoFunctionNVar) getGeoElement();
		surface.start(geo);
		
		float uMin, uMax, vMin, vMax;
		if (unboundedDomain){
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
		if(updateCullingBox()){
			mesh = new SurfaceMesh2(function, cullingBox, activeDomain);
		}
		if (updateForItSelf()) {
			//the perspective has changed so the mesh has to be updated
			//TODO: calling setWaitForUpdate() refines the whole mesh - fix?
			//setWaitForUpdate(); 
		}
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

	@Override
	public void drawGeometryPicked(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}
    

}
