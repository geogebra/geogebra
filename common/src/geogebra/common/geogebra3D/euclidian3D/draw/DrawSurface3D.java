package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.SurfaceEvaluable;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Class for drawing a 2-var function
 * 
 * @author matthieu
 * 
 */
public class DrawSurface3D extends Drawable3DSurfaces {


	/** The function being rendered */
	SurfaceEvaluable surfaceGeo;
	
	private static final long MAX_SPLIT = 256;

	private TreeMap<CoordsIndex, Coords> mesh;

	/**
	 * common constructor
	 * 
	 * @param a_view3d
	 * @param function
	 */
	public DrawSurface3D(EuclidianView3D a_view3d, SurfaceEvaluable surface) {
		super(a_view3d, (GeoElement) surface);
		this.surfaceGeo = surface;
		this.mesh = new TreeMap<CoordsIndex, Coords>(new CompareUthenV());
		
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

		
		double uMin = surfaceGeo.getMinParameter(0);
		double uMax = surfaceGeo.getMaxParameter(0);
		double vMin = surfaceGeo.getMinParameter(1);
		double vMax = surfaceGeo.getMaxParameter(1);
		double uDelta = uMax-uMin;
		double vDelta = vMax-vMin;

		Coords p1 = surfaceGeo.evaluatePoint(uMin, vMin);
		Coords p2 = surfaceGeo.evaluatePoint(uMax, vMin);
		Coords p3 = surfaceGeo.evaluatePoint(uMin, vMax);
		Coords p4 = surfaceGeo.evaluatePoint(uMax, vMax);
		CoordsIndex i1 = new CoordsIndex(0, 0);
		CoordsIndex i2 = new CoordsIndex(MAX_SPLIT,0);
		CoordsIndex i3 = new CoordsIndex(0, MAX_SPLIT);
		CoordsIndex i4 = new CoordsIndex(MAX_SPLIT, MAX_SPLIT);
		mesh.put(i1, p1);
		mesh.put(i2, p2);
		mesh.put(i3, p3);
		mesh.put(i4, p4);
		
		surface.start();
		
		splitOrDraw(surface,i1,i2,i3,i4,uMin,uDelta,vMin,vDelta);
		//surface.drawQuadNoTexture(p1,p2,p4,p3);

		setSurfaceIndex(surface.end());

		return true;
	}

	/*
	 * 
	 */
	private void splitOrDraw(PlotterSurface surface, CoordsIndex TL, CoordsIndex TR, CoordsIndex BL, CoordsIndex BR, double uMin, double uDelta, double vMin, double vDelta )
	{
		//test if this quad may be drawn or must be splitted
		//index delta
		long iDelta = (TR.iu-TL.iu);
		
		double d1 = mesh.get(TL).distance(mesh.get(BR));
		Coords centerValue = surfaceGeo.evaluatePoint(uMin+(TL.iu+iDelta/2)*uDelta/MAX_SPLIT, vMin+(TL.iv+iDelta/2)*vDelta/MAX_SPLIT);
		double d2 = mesh.get(TL).distance(centerValue)+centerValue.distance(mesh.get(BR));
		double rapport = Math.abs(d2/d1-1);
		
		//this split test is temporary
		if ((iDelta>=2)&&((iDelta>=8)||(rapport>1.e-12))){
			//split
			//index of the five new points T,L,C,R,B
			//  TL....iT....TR
			//  .      .     .
			//  .      .     .
			//  iL    iC    iR
			//  .      .     .
			//  .      .     .
			//  BL....iB....BR
			iDelta /= 2;
			CoordsIndex iT = new CoordsIndex(TL.iu+iDelta, TL.iv);
			CoordsIndex iL = new CoordsIndex(TL.iu, TL.iv+iDelta);
			CoordsIndex iC = new CoordsIndex(TL.iu+iDelta, TL.iv+iDelta);
			CoordsIndex iR = new CoordsIndex(TR.iu, TR.iv+iDelta);
			CoordsIndex iB = new CoordsIndex(BL.iu+iDelta, BL.iv);
			Coords fiT = surfaceGeo.evaluatePoint(uMin+iT.iu*uDelta/MAX_SPLIT, vMin+iT.iv*vDelta/MAX_SPLIT);
			Coords fiL = surfaceGeo.evaluatePoint(uMin+iL.iu*uDelta/MAX_SPLIT, vMin+iL.iv*vDelta/MAX_SPLIT);
			Coords fiC = surfaceGeo.evaluatePoint(uMin+iC.iu*uDelta/MAX_SPLIT, vMin+iC.iv*vDelta/MAX_SPLIT);
			Coords fiR = surfaceGeo.evaluatePoint(uMin+iR.iu*uDelta/MAX_SPLIT, vMin+iR.iv*vDelta/MAX_SPLIT);
			Coords fiB = surfaceGeo.evaluatePoint(uMin+iB.iu*uDelta/MAX_SPLIT, vMin+iB.iv*vDelta/MAX_SPLIT);
			mesh.put(iT, fiT);
			mesh.put(iL, fiL);
			mesh.put(iC, fiC);
			mesh.put(iR, fiR);
			mesh.put(iB, fiB);
			
			//square TL
			splitOrDraw(surface,TL,iT,iL,iC,uMin,uDelta,vMin,vDelta);
			//square TR
			splitOrDraw(surface,iT,TR,iC,iR,uMin,uDelta,vMin,vDelta);
			//square BL
			splitOrDraw(surface,iL,iC,BL,iB,uMin,uDelta,vMin,vDelta);
			//square BR
			splitOrDraw(surface,iC,iR,iB,BR,uMin,uDelta,vMin,vDelta);
		}
		else {
			//draw
			surface.drawQuad(mesh.get(TL),mesh.get(TR),mesh.get(BR),mesh.get(BL));
		}
		
	}
	
	@Override
	protected void updateForView() {
		if (getView3D().viewChanged()){
			updateForItSelf();
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
	
	
	class Node {
		private Coords pointPos;
		private Node n,s,w,e;
		
		public Node(Coords p, Node n, Node s, Node w, Node e) {
			this.pointPos = p;
			this.n = n;
			this.s = s;
			this.w = w;
			this.e = e;
		}
	}
	
	
	
	class CoordsIndex {

		protected long iu, iv ;
		
		public CoordsIndex(long iu, long iv){
			this.iu = iu;
			this.iv = iv;
		}
		
		
	}
	
	class CompareUthenV implements Comparator<CoordsIndex>{

		public int compare(CoordsIndex c1, CoordsIndex c2) {
			if (c1.iu>c2.iu) return 1;
			if (c1.iu<c2.iu) return -1;
			if (c1.iv>c2.iv) return 1;
			if (c1.iv<c2.iv) return -1;
			return 0;
		}
		
	}
	class CompareVthenU implements Comparator<CoordsIndex>{

		public int compare(CoordsIndex c1, CoordsIndex c2) {
			if (c1.iv>c2.iv) return 1;
			if (c1.iv<c2.iv) return -1;
			if (c1.iu>c2.iu) return 1;
			if (c1.iu<c2.iu) return -1;
			return 0;
		}
		
	}
	
}
