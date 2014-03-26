package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.SurfaceEvaluable;

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

	private static final long MAX_SPLIT = 2048;
	private static final long MIN_SPLIT = 256;
	private static final double MAX_CENTER_QUAD_DISTANCE = 1.e-2;
	private static final double MAX_DIAGONAL_QUAD_LENGTH = 1.e-1;

	private TreeMap<Long, TreeMap<Long,Coords>> mesh;
	
	private void putInMesh(Long iu, Long iv, Coords z){
		TreeMap<Long,Coords> meshv = mesh.get(iu);
		if (meshv == null) {meshv = new TreeMap<Long,Coords>();}
		meshv.put(iv, z);
		mesh.put(iu,meshv);
	}
	
	private Coords getFromMesh(Long iu, Long iv){
		return (mesh.get(iu)).get(iv);
	}

	/**
	 * common constructor
	 * 
	 * @param a_view3d
	 * @param function
	 */
	public DrawSurface3D(EuclidianView3D a_view3d, SurfaceEvaluable surface) {
		super(a_view3d, (GeoElement) surface);
		this.surfaceGeo = surface;
		this.mesh = new TreeMap<Long, TreeMap<Long,Coords>>();

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
		//CoordsIndex i1 = new CoordsIndex(0, 0);
		//CoordsIndex i2 = new CoordsIndex(MAX_SPLIT,0);
		//CoordsIndex i3 = new CoordsIndex(0, MAX_SPLIT);
		//CoordsIndex i4 = new CoordsIndex(MAX_SPLIT, MAX_SPLIT);
		putInMesh((long)0,(long)0, p1);
		putInMesh(MAX_SPLIT,(long)0, p2);
		putInMesh((long)0, MAX_SPLIT, p3);
		putInMesh(MAX_SPLIT, MAX_SPLIT, p4);

		surface.start();

		splitOrDraw(surface,0,0,MAX_SPLIT,0,0, MAX_SPLIT,MAX_SPLIT, MAX_SPLIT,uMin,uDelta,vMin,vDelta,MAX_SPLIT);
		
		setSurfaceIndex(surface.end());

		return true;
	}

	/*
	 * 
	 */
	private void splitOrDraw(PlotterSurface surface, long TLu, long TLv, long TRu, long TRv, long BLu, long BLv, long BRu, long BRv, double uMin, double uDelta, double vMin, double vDelta,long iDelta )
	{
		//test if this quad may be drawn or must be splitted
		//index delta
		iDelta /=2;
		Coords pTL = getFromMesh(TLu,TLv);
		Coords pBR = getFromMesh(BRu,BRv);
		Coords pTR = getFromMesh(TRu,TRv);
		Coords pBL = getFromMesh(BLu,BLv);
		if (iDelta>=2){
			long Cu = TLu+iDelta;
			long Cv = TLv+iDelta;
			Coords fiC = surfaceGeo.evaluatePoint(uMin+Cu*uDelta/MAX_SPLIT, vMin+Cv*vDelta/MAX_SPLIT);
			putInMesh(Cu,Cv, fiC);
			double diagLength = Math.max(pTL.distance(pBR),pTR.distance(pBL));
			
			Coords centerValue = (pTL.add(pBR)).mul(0.5);
			double centerDistance = fiC.distance(centerValue);

			//this split test is temporary
			if ((iDelta>=MIN_SPLIT)||
				!(diagLength<MAX_DIAGONAL_QUAD_LENGTH*(uDelta))||
				!(centerDistance<MAX_CENTER_QUAD_DISTANCE))
			{
				//split
				//index of the five new points T,L,C,R,B
				//  TL....iT....TR
				//  .      .     .
				//  .      .     .
				//  iL    iC    iR
				//  .      .     .
				//  .      .     .
				//  BL....iB....BR
				long Tu = TLu+iDelta;
				long Tv = TLv;
				long Lu = TLu;
				long Lv = TLv+iDelta;
				long Ru = TRu;
				long Rv = TRv+iDelta;
				long Bu = BLu+iDelta;
				long Bv = BLv;
				Coords fiT = surfaceGeo.evaluatePoint(uMin+Tu*uDelta/MAX_SPLIT, vMin+Tv*vDelta/MAX_SPLIT);
				Coords fiL = surfaceGeo.evaluatePoint(uMin+Lu*uDelta/MAX_SPLIT, vMin+Lv*vDelta/MAX_SPLIT);
				Coords fiR = surfaceGeo.evaluatePoint(uMin+Ru*uDelta/MAX_SPLIT, vMin+Rv*vDelta/MAX_SPLIT);
				Coords fiB = surfaceGeo.evaluatePoint(uMin+Bu*uDelta/MAX_SPLIT, vMin+Bv*vDelta/MAX_SPLIT);
				putInMesh(Tu,Tv, fiT);
				putInMesh(Lu,Lv, fiL);
				putInMesh(Ru,Rv, fiR);
				putInMesh(Bu,Bv, fiB);

				//square TL
				splitOrDraw(surface,TLu,TLv,Tu,Tv,Lu,Lv,Cu,Cv,uMin,uDelta,vMin,vDelta,iDelta);
				//square TR
				splitOrDraw(surface,Tu,Tv,TRu,TRv,Cu,Cv,Ru,Rv,uMin,uDelta,vMin,vDelta,iDelta);
				//square BL
				splitOrDraw(surface,Lu,Lv,Cu,Cv,BLu,BLv,Bu,Bv,uMin,uDelta,vMin,vDelta,iDelta);
				//square BR
				splitOrDraw(surface,Cu,Cv,Ru,Rv,Bu,Bv,BRu,BRv,uMin,uDelta,vMin,vDelta,iDelta);

			}
			else {
				//draw
				surface.drawQuadNoTexture(pTL,pTR,pBR,pBL);
			}
		}
		else {
			//draw
			surface.drawQuadNoTexture(pTL,pTR,pBR,pBL);
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


/*
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
*/
}
