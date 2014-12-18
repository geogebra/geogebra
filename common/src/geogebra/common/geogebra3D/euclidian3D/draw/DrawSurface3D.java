package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.SurfaceEvaluable;

import java.util.ArrayList;

/**
 * Class for drawing a 2-var function
 * 
 * @author matthieu
 * 
 */
public class DrawSurface3D extends Drawable3DSurfaces {


	/** The function being rendered */
	SurfaceEvaluable surfaceGeo;

	private static final long MAX_SPLIT = 32768;
	private static final long MIN_SPLIT = 512;
	private static final double MAX_CENTER_QUAD_DISTANCE = 1.e-3;
	private static final double MAX_DIAGONAL_QUAD_LENGTH = 1.e-3;
	private double uDelta;
	private double vDelta;
	private double limit1;
	private double limit2;
	private double uMin;
	private double vMin;
	
	
	
	/** Current culling box - set to view3d.(x|y|z)(max|min) */
	private double[] cullingBox = new double[6];
	private double cullingBoxDelta; 


	/**
	 * common constructor
	 * 
	 * @param a_view3d
	 * @param function
	 */
	public DrawSurface3D(EuclidianView3D a_view3d, SurfaceEvaluable surface) {
		super(a_view3d, (GeoElement) surface);
		this.surfaceGeo = surface;

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
	public void drawOutline(Renderer renderer) {
		// no outline
	}

	@Override
	protected boolean updateForItSelf() {

		Renderer renderer = getView3D().getRenderer();

		PlotterSurface surface = renderer.getGeometryManager().getSurface();


		uMin = surfaceGeo.getMinParameter(0);
		double uMax = surfaceGeo.getMaxParameter(0);
		vMin = surfaceGeo.getMinParameter(1);
		double vMax = surfaceGeo.getMaxParameter(1);
		uDelta = (uMax-uMin)/MAX_SPLIT;
		vDelta = (vMax-vMin)/MAX_SPLIT;

		updateCullingBox();
		cullingBoxDelta = (cullingBox[5]-cullingBox[4]);
		limit1 = cullingBoxDelta * MAX_DIAGONAL_QUAD_LENGTH;
		limit2 = cullingBoxDelta * MAX_CENTER_QUAD_DISTANCE;
		
		
		surface.start(getReusableSurfaceIndex());
		
		Corner al = new Corner(uMin, vMin);
		Corner a = new Corner(uMax, vMin);
		Corner l = new Corner(uMin, vMax);
		Corner corner = new Corner(uMax, vMax, a, l, al);
		
		currentSplit = new ArrayList<DrawSurface3D.Corner>(); 
		nextSplit = new ArrayList<DrawSurface3D.Corner>();
		currentSplit.add(corner);
		split(4);
		corner.draw(surface);

		
		setSurfaceIndex(surface.end());

		return true;
	}
	
	

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom() || getView3D().viewChangedByTranslate()){
			updateForItSelf();
		}
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_SURFACE;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
	}

	
	private boolean updateCullingBox() {
		EuclidianView3D view = getView3D();
		cullingBox[0] = view.getXmin();
		cullingBox[1] = view.getXmax();
		cullingBox[2] = view.getYmin();
		cullingBox[3] = view.getYmax();
		cullingBox[4] = view.getZmin();
		cullingBox[5] = view.getZmax();
		return true;
	}

	private boolean inCullingBox(Coords p) {
		if ( //(p.isDefined())
				//&& (p.isFinite())
				//&&
				(p.getX()>cullingBox[0])
				&& (p.getX()<cullingBox[1])
				&& (p.getY()>cullingBox[2])
				&& (p.getY()<cullingBox[3])
				&& (p.getZ()>cullingBox[4])
				&& (p.getZ()<cullingBox[5])) {
			return true;}
		else {return false;}
	}
	
	
	private ArrayList<Corner> currentSplit;

	/**
	 * list of next corners to split
	 */
	ArrayList<Corner> nextSplit;

	private void split(int depth){
		
		boolean recordNext = depth > 1;
		for (Corner corner : currentSplit){
			corner.split(recordNext);
		}
		
		
		if (recordNext){
			// swap stacks
			ArrayList<Corner> tmp = currentSplit;
			currentSplit = nextSplit;
			nextSplit = tmp;
			nextSplit.clear();
			// split again
			split(depth - 1);
		}
		
		
	}
	
	private class Corner{
		private Coords p;
		private double u, v;
		
		
		private Corner a, l; // above, left
		
		
		public Corner(double u, double v){
			this.u = u;
			this.v = v;
			p = surfaceGeo.evaluatePoint(u, v);
		}

		
		public Corner(double u, double v, Corner a, Corner l, Corner al){
			this(u, v);
			
			// neighbors
			this.a = a;
			this.l = l;
			
			// neighbors of neighbors
			l.a = al;
			a.l = al;

		}
		
		public void draw(PlotterSurface surface){
			if (a!=null && l!=null){
				drawQuad(surface);
				a.draw(surface);
				l.drawLeft(surface);
			}
		} 
		
		private void drawLeft(PlotterSurface surface){
			if (l!=null){
				drawQuad(surface);
				l.drawLeft(surface);
			}

		}
		
		private void drawQuad(PlotterSurface surface){
			
			if (a == null || a.l == null){
				return;
			}
			
			surface.drawQuadWireFrame(
					a.l.p, 
					a.p, 
					p, 
					l.p);
		}
		
		
		public void split(boolean recordNext){
			
			double um, vm;
			
			// south
			Corner s;
			if (l.a == null){ // already splitted on left
				s = l;
				um = s.u;
			}else{ // split on left
				um = (u + l.u)/2;
				s = new Corner(um, v);
				s.l = l;
				l = s;
			}
			
			// east
			Corner e;
			if (a.l == null){ // already splitted on above
				e = a;
				vm = a.v;
			}else{ // split on above
				vm = (v + a.v)/2;
				e = new Corner(u, vm);
				e.a = a;
				a = e;
			}
			
			// middle
			Corner m = new Corner(um, vm);
			s.a = m;
			e.l = m;
			
			// north
			Corner n = new Corner(um, e.a.v);
			n.l = e.a.l;
			e.a.l = n;
			m.a = n;
			
			
			// west
			Corner w = new Corner(s.l.u, vm);
			w.a = s.l.a;
			s.l.a = w;
			m.l = w;			
			
			
			// split again
			if (recordNext){
				nextSplit.add(this);
				nextSplit.add(s);
				nextSplit.add(e);
				nextSplit.add(m);
			}
		}
	
	}
	

		
}
