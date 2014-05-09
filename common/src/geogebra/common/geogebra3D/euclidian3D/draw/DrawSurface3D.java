package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.SurfaceEvaluable;

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
		
		Coords p1 = surfaceGeo.evaluatePoint(uMin, vMin);
		Coords p2 = surfaceGeo.evaluatePoint(uMax, vMin);
		Coords p3 = surfaceGeo.evaluatePoint(uMin, vMax);
		Coords p4 = surfaceGeo.evaluatePoint(uMax, vMax);
		
		Node tlNode = new Node(p1);
		Node trNode = new Node(p2);
		Node blNode = new Node(p3);
		Node surfaceGlobal= new Node(p4);
		
		surface.start();

		splitOrDraw(surface, 0, 0, surfaceGlobal,tlNode,trNode,blNode, MAX_SPLIT);
		
		setSurfaceIndex(surface.end());

		return true;
	}
	
	
	private void splitOrDraw(PlotterSurface surface, long TLu, long TLv, Node currentNode, Node aboveLeft, Node above, Node left, long iDelta)
	{
		
		
		if (iDelta>1) {
			
			double diagLength = Math.max(aboveLeft.pointPos.distance(currentNode.pointPos),above.pointPos.distance(left.pointPos));
			
			Coords centerValue1 = (aboveLeft.pointPos.add(currentNode.pointPos)).mul(0.5);
			Coords centerValue2 = (above.pointPos.add(left.pointPos)).mul(0.5);

			iDelta /=2;
			
			long Tu = TLu+iDelta;
			long Lv = TLv+iDelta;
			currentNode.tl = new Node(Tu, Lv);
			
			double centerDistance = Math.max(currentNode.tl.pointPos.distance(centerValue1),currentNode.tl.pointPos.distance(centerValue2));

			//this split test is temporary
			if ((iDelta>=MIN_SPLIT)
					||((diagLength>limit1)&&(centerDistance>limit2))
					&&((inCullingBox(currentNode.pointPos))||(inCullingBox(aboveLeft.pointPos))||(inCullingBox(above.pointPos))||(inCullingBox(left.pointPos)))){


				long Ru = TLu+2*iDelta;
				long Bv = TLv+2*iDelta;


				if (aboveLeft.br == null){
					aboveLeft.br = new Node(aboveLeft);
				}
				if (above.bl == null){
					above.bl = new Node(Tu, TLv);
				}
				if (left.tr == null){
					left.tr = new Node(TLu, Lv);
				}

				splitOrDraw(surface, TLu, TLv, currentNode.tl, aboveLeft.br, above.bl, left.tr, iDelta);


				currentNode.tr = new Node(Ru, Lv);
				if (above.br == null){
					above.br = new Node(above);
				}

				splitOrDraw(surface, Tu, TLv, currentNode.tr, above.bl, above.br, currentNode.tl, iDelta);


				currentNode.bl = new Node(Tu, Bv);
				if (left.br == null){
					left.br = new Node(left);
				}

				splitOrDraw(surface, TLu, Lv, currentNode.bl, left.tr, currentNode.tl, left.br, iDelta);

				currentNode.br = new Node(currentNode);

				splitOrDraw(surface, Tu, Lv, currentNode.br, currentNode.tl, currentNode.tr, currentNode.bl, iDelta);
			
			}else{		
				//draw
				surface.drawQuadNoTexture(aboveLeft.pointPos,above.pointPos,currentNode.pointPos,left.pointPos);
			}

		}else{		
			//draw
			surface.drawQuadNoTexture(aboveLeft.pointPos,above.pointPos,currentNode.pointPos,left.pointPos);
		}
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom() || getView3D().viewChangedByTranslate()){
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
		private Node tl, tr, bl, br; // top-left child and other children

		public Node(Coords p, Node tl, Node tr, Node bl, Node br) {
			this.pointPos = p;
			this.tl = tl;
			this.tr = tr;
			this.bl = bl;
			this.br = br;
		}
		
		public Node(Coords p) {
			this.pointPos = p;
		}
		
		public Node(long u, long v){
			this(surfaceGeo.evaluatePoint(uMin+u*uDelta, vMin+v*vDelta));
		}
		
		public Node(Node node){
			this.pointPos = node.pointPos;
		}

	}
	
}
