package geogebra3D.euclidian3D;

import geogebra.common.kernel.arithmetic.Functional2Var;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.kernelND.SurfaceEvaluable;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.plots.SurfaceMesh2;

/**
 * Class for drawing a 2-var function
 * 
 * @author matthieu
 * 
 */
public class DrawSurface3D extends Drawable3DSurfaces {

	/** The mesh currently being rendered - is occasionally reset */
	private SurfaceMesh2 mesh;

	/** The function being rendered */
	SurfaceEvaluable surface;

	/** if set to true - the domain we're looking at is R^2 */
	private boolean unboundedDomain;
	
	/** current domain for the function on the format {xmin, xmax, ymin, ymax} 
	 *  when unboundedDomain is true, this will be slightly larger than the intervals in cullingBox */
	private double[] activeDomain = new double[4];
	
	/** */
	private final static double domainThreshold = 0.0001;
	
	/** */
	private final static double domainScale = 40;
	
	/** Current culling box - set to view3d.(x|y|z)(max|min) */
	private double[] cullingBox = new double[6];

	/**
	 * common constructor
	 * 
	 * @param a_view3d
	 * @param function
	 */
	public DrawSurface3D(EuclidianView3D a_view3d, SurfaceEvaluable surface) {
		super(a_view3d, (GeoElement) surface);
		this.surface = surface;
		
		if (Double.isNaN(surface.getMinParameter(0))){
			unboundedDomain=true;
		}else{
			unboundedDomain=false;
		}

		updateDomain();

		updateCullingBox();

		mesh = new SurfaceMesh2(surface, cullingBox, activeDomain);
	}

	private boolean updateDomain(){
		boolean changed = false;
		if (!unboundedDomain) {
			double t = surface.getMinParameter(0);
			if(t != activeDomain[0]) {
				changed = true;
				activeDomain[0] = t;
			}
			t = surface.getMaxParameter(0);
			if(t != activeDomain[1]) {
				changed = true;
				activeDomain[1] = t;
			}
			t = surface.getMinParameter(1);
			if(t != activeDomain[2]) {
				changed = true;
				activeDomain[2] = t;
			}
			t = surface.getMaxParameter(1);
			if(t != activeDomain[3]) {
				changed = true;
				activeDomain[3] = t;
			}
		}
		return changed;
	}

	private boolean updateCullingBox() {
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
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getSurfaceIndex());
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

	@Override
	protected boolean updateForItSelf() {
		if (elementHasChanged) {
			if (updateDomain()) {
				// domain has changed - create a new mesh
				mesh = new SurfaceMesh2(surface, cullingBox, activeDomain);
			} else {
				// otherwise, update the surface
				elementHasChanged = false;
				mesh.updateParameters();
			}
		}

		Renderer renderer = getView3D().getRenderer();
		mesh.setCullingBox(cullingBox);
		boolean ret = mesh.optimize();

		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		GeoElement geo = getGeoElement();

		float uMin, uMax, vMin, vMax;
		
		if (geo instanceof Functional2Var) {
			Functional2Var fun = (Functional2Var) geo;
			surface.start(fun);
			uMin = (float) fun.getMinParameter(0);
			uMax = (float) fun.getMaxParameter(0);
			vMin = (float) fun.getMinParameter(1);
			vMax = (float) fun.getMaxParameter(1);
		} else {
			GeoFunctionNVar fun = (GeoFunctionNVar) geo;
			surface.start(fun);
			if (unboundedDomain){
				uMin = -1; uMax = 1; vMin = -1; vMax = 1;
			}else{
				uMin = (float) fun.getMinParameter(0);
				uMax = (float) fun.getMaxParameter(0);
				vMin = (float) fun.getMinParameter(1);
				vMax = (float) fun.getMaxParameter(1);
			}
		}

		surface.setU(uMin, uMax);
		surface.setNbU((int) (uMax - uMin) * 10);
		surface.setV(vMin, vMax);
		surface.setNbV((int) (vMax - vMin) * 10);

		// TODO use fading texture

		surface.draw(mesh);
		setSurfaceIndex(surface.end());

		return false;
	}

	@Override
	protected void updateForView() {
		if(updateCullingBox()){
			mesh = new SurfaceMesh2(surface, cullingBox, activeDomain);
		}
		if (updateForItSelf()) {
			//the perspective has changed so the mesh has to be updated
			//TODO: calling setWaitForUpdate() refines the whole mesh - fix?
			//setWaitForUpdate(); 
		}
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_2D;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
	}
	
	
	@Override
	protected void updateColors(){
		super.updateColors();
	}

}
