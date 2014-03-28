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
		
		Coords p1 = surfaceGeo.evaluatePoint(uMin, vMin);
		Coords p2 = surfaceGeo.evaluatePoint(uMax, vMin);
		Coords p3 = surfaceGeo.evaluatePoint(uMin, vMax);
		Coords p4 = surfaceGeo.evaluatePoint(uMax, vMax);
		
		putInMesh((long)0,(long)0, p1);
		putInMesh(MAX_SPLIT,(long)0, p2);
		putInMesh((long)0, MAX_SPLIT, p3);
		putInMesh(MAX_SPLIT, MAX_SPLIT, p4);
		
		surface.start();

		splitOrDraw(surface,0,0,p1,p2,p3,p4,MAX_SPLIT);
		
		setSurfaceIndex(surface.end());

		return true;
	}

	/*
	 * 
	 */
	private void splitOrDraw(PlotterSurface surface, long TLu, long TLv, Coords pTL, Coords pTR, Coords pBL, Coords pBR, long iDelta )
	{
		//test if this quad may be drawn or must be splitted
		//index delta
		iDelta /=2;
		if (iDelta>=1){
			long Cu = TLu+iDelta;
			long Cv = TLv+iDelta;
			Coords fiC = surfaceGeo.evaluatePoint(uMin+Cu*uDelta, vMin+Cv*vDelta);
			putInMesh(Cu,Cv, fiC);
			double diagLength = Math.max(pTL.distance(pBR),pTR.distance(pBL));
			
			Coords centerValue1 = (pTL.add(pBR)).mul(0.5);
			Coords centerValue2 = (pTR.add(pBL)).mul(0.5);
			
			double centerDistance = Math.max(fiC.distance(centerValue1),fiC.distance(centerValue2));

			//this split test is temporary
			if ((iDelta>=MIN_SPLIT)
					||((diagLength>limit1)&&(centerDistance>limit2))
					&&((inCullingBox(pTL))||(inCullingBox(pTR))||(inCullingBox(pBL))||(inCullingBox(pBR)))
					)
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
				long Ru = TLu+2*iDelta;
				long Rv = Lv;
				long Bu = Tu;
				long Bv = TLv+2*iDelta;
				Coords fiT = surfaceGeo.evaluatePoint(uMin+Tu*uDelta, vMin+Tv*vDelta);
				Coords fiL = surfaceGeo.evaluatePoint(uMin+Lu*uDelta, vMin+Lv*vDelta);
				Coords fiR = surfaceGeo.evaluatePoint(uMin+Ru*uDelta, vMin+Rv*vDelta);
				Coords fiB = surfaceGeo.evaluatePoint(uMin+Bu*uDelta, vMin+Bv*vDelta);
				putInMesh(Tu,Tv, fiT);
				putInMesh(Lu,Lv, fiL);
				putInMesh(Ru,Rv, fiR);
				putInMesh(Bu,Bv, fiB);

				//square TL
				splitOrDraw(surface,TLu,TLv,pTL,fiT,fiL,fiC,iDelta);
				//square TR
				splitOrDraw(surface,Tu,Tv,fiT,pTR,fiC,fiR,iDelta);
				//square BL
				splitOrDraw(surface,Lu,Lv,fiL,fiC,pBL,fiB,iDelta);
				//square BR
				splitOrDraw(surface,Cu,Cv,fiC,fiR,fiB,pBR,iDelta);

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
