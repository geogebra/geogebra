package geogebra3D.euclidian3D;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.Textures;
import geogebra3D.kernel3D.GeoPlane3D;




/**
 * Class for drawing 3D planes.
 * @author matthieu
 *
 */
public class DrawPlane3D extends Drawable3DSurfaces {



	/** gl index of the grid */
	private int gridIndex = -1;
	private int gridOutlineIndex = -1;
	
	
	protected double xmin, xmax, ymin, ymax;
	double[] minmaxXFinal = new double[2], minmaxYFinal = new double[2];
	
	/** says if the view direction is parallel to the plane */
	private boolean viewDirectionIsParallel; 

	
	
	/**
	 * Common constructor
	 * @param a_view3D
	 * @param a_plane3D
	 */
	public DrawPlane3D(EuclidianView3D a_view3D, GeoPlane3D a_plane3D){
		
		super(a_view3D, a_plane3D);
		
		setMinMax();
		
	}
	
	
	

	@Override
	public void drawGeometry(Renderer renderer) {

		if (((GeoPlane3D)getGeoElement()).isPlateVisible())
			drawPlate(renderer);

		
	}
	
	@Override
	protected void drawSurfaceGeometry(Renderer renderer){
		drawGeometry(renderer);
	}
	
	private void drawPlate(Renderer renderer){
		renderer.setLayer(getGeoElement().getLayer()-1f); //-1f for z-fighting with planes
		renderer.getGeometryManager().draw(getGeometryIndex());	
		renderer.setLayer(0);
	}
	
	
	@Override
	public void drawGeometryHiding(Renderer renderer) {
		GeoPlane3D plane = (GeoPlane3D) getGeoElement();
		if (plane.isPlateVisible()){// || plane.isGridVisible())
			drawPlate(renderer);
			/*
			renderer.setLayer(getGeoElement().getLayer()-1f); //-1f for z-fighting with planes
			renderer.getGeometryManager().draw(getGeometryIndex());	
			renderer.getGeometryManager().draw(hidingIndex);	
			renderer.setLayer(0);
			*/
		}
	}
	
	
	
	@Override
	public void drawGeometryPicked(Renderer renderer){}
	


	@Override
	public void drawGeometryHidden(Renderer renderer){ 
		
		if(!isVisible())
			return;
		
		if (!isGridVisible()) return;

		//dash
		renderer.getTextures().loadTextureNearest(Textures.DASH_SHORT);
		renderer.getGeometryManager().draw(gridIndex);
		
		renderer.getTextures().loadTextureNearest(Textures.DASH_LONG);
		renderer.getGeometryManager().draw(gridOutlineIndex);

		
	}
	
	/*
	@Override
	protected void drawGeometryForPicking(Renderer renderer){
		drawGeometry(renderer);
		renderer.getGeometryManager().draw(gridIndex);
		renderer.getGeometryManager().draw(gridOutlineIndex);
	}
	*/
	
	/**
	 * 
	 * @return true if grid is visible
	 */
	protected boolean isGridVisible(){
		return ((GeoPlane3D)getGeoElement()).isGridVisible() || viewDirectionIsParallel;
	}
	
	
	
	
	@Override
	protected boolean updateForItSelf(){
		((GeoPlane3D) getGeoElement()).setGridCorners(minmaxXFinal[0], minmaxYFinal[0], minmaxXFinal[1], minmaxYFinal[1]);
		return updateGeometry();
	}

	
	//private int hidingIndex = -1;
	
	/**
	 * update the geometry
	 * @return true
	 */
	protected boolean updateGeometry(){
		

		Renderer renderer = getView3D().getRenderer();
		GeoPlane3D geo = (GeoPlane3D) getGeoElement();
		CoordSys coordsys = geo.getCoordSys();
		
		
		
		float xmin1 = (float) geo.getXmin(), xmax1 = (float) geo.getXmax(), xdelta1 = xmax1-xmin1; 
		float ymin1 = (float) geo.getYmin(), ymax1 = (float) geo.getYmax(), ydelta1 = ymax1-ymin1; 

		/*
		float xmin2 = (float) geo.getXPlateMin(), xmax2 = (float) geo.getXPlateMax(), xdelta2 = xmax2-xmin2; 
		float ymin2 = (float) geo.getYPlateMin(), ymax2 = (float) geo.getYPlateMax(), ydelta2 = ymax2-ymin2; 
		*/
		
		// plane	
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		
		surface.start(geo);
		
		surface.setU(xmin1,xmax1);surface.setNbU(2);
		surface.setV(ymin1,ymax1);surface.setNbV(2);

		if (!getView3D().useClippingCube()){
			float fading;
			fading = xdelta1 * geo.getFading();
			surface.setUFading(fading, fading);
			fading = ydelta1 * geo.getFading();
			surface.setVFading(fading, fading);
		}
		surface.draw();
		setGeometryIndex(surface.end());
		
		/*
		//hiding borders
		surface.start(geo);	
		surface.setU(xmax1,xmax);surface.setNbU(1);
		surface.setV(ymin,ymax);surface.setNbV(1);
		surface.draw();
		surface.setU(xmin,xmin1);surface.setNbU(1);
		surface.setV(ymin,ymax);surface.setNbV(1);
		surface.draw();
		surface.setU(xmin1,xmax1);surface.setNbU(1);
		surface.setV(ymin,ymin1);surface.setNbV(1);
		surface.draw();
		surface.setU(xmin1,xmax1);surface.setNbU(1);
		surface.setV(ymax1,ymax);surface.setNbV(1);
		surface.draw();
		hidingIndex=surface.end();
		*/
		
		// grid
		
		removeGeometryIndex(gridIndex);
		removeGeometryIndex(gridOutlineIndex);
		
		//if (Kernel.isZero(coordsys.getNormal().dotproduct(getView3D().getViewDirection()))){
		//Application.debug("v=\n"+coordsys.getEquationVector()+"\neye=\n"+getView3D().getEyePosition());
		if (Kernel.isZero(coordsys.getEquationVector().dotproduct(getView3D().getEyePosition()))){
			viewDirectionIsParallel = true;
		}else{
			viewDirectionIsParallel = false;
		}

		
		PlotterBrush brush = renderer.getGeometryManager().getBrush();
		
		brush.start(8);
		float thickness = brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());

		brush.setColor(geogebra.awt.GColorD.getAwtColor(getGeoElement().getObjectColor()));
		
		//double dx = Math.max(geo.getGridXd(), geo.getGridYd()); //TODO
		double dx = Math.min(geo.getGridXd(), geo.getGridYd());
		double dy = dx; //TODO
		
		//Application.debug("geo:"+getGeoElement().getLabel()+"\n"+dx+","+dy+"\nx="+geo.getXmin()+","+geo.getXmax()+"\nthickness="+getGeoElement().getLineThickness());
		
			//}else{
		if (!viewDirectionIsParallel){
			//along x axis
			brush.setAffineTexture(
					(0f-xmin1)/ydelta1,
					0.25f);
			int i0 = (int) (ymin1/dy);
			if (ymin1>0)
				i0++;
			for(int i=i0;i<=ymax1/dy;i++)
				brush.segment(coordsys.getPointForDrawing(xmin1,i*dy), 
						coordsys.getPointForDrawing(xmax1,i*dy));	
			//along y axis
			brush.setAffineTexture(
					(0f-ymin1)/xdelta1,
					0.25f);
			i0 = (int) (xmin1/dx);
			if (xmin1>0)
				i0++;
			for(int i=i0;i<=xmax1/dx;i++)
				brush.segment(coordsys.getPointForDrawing(i*dx, ymin1), 
						coordsys.getPointForDrawing(i*dx, ymax1));
		}
		
		gridIndex = brush.end();
		
		
		brush.start(8);
		boolean showClippingCube = getView3D().showClippingCube();
		if (viewDirectionIsParallel || showClippingCube){
			//draws the rectangle outline
			if (showClippingCube){
				brush.setAffineTexture(
						(0f-xmin1)/ydelta1,
						0.25f);
			}else
				brush.setPlainTexture();
			brush.segment(coordsys.getPointForDrawing(xmin1,ymax1-thickness), 
					coordsys.getPointForDrawing(xmax1,ymax1-thickness));
			brush.segment(coordsys.getPointForDrawing(xmin1,ymin1+thickness), 
					coordsys.getPointForDrawing(xmax1,ymin1+thickness));	
			
			if (showClippingCube){
				brush.setAffineTexture(
						(0f-ymin1)/xdelta1,
						0.25f);
			}			
			brush.segment(coordsys.getPointForDrawing(xmin1+thickness,ymin1), 
					coordsys.getPointForDrawing(xmin1+thickness,ymax1));
			brush.segment(coordsys.getPointForDrawing(xmax1-thickness,ymin1), 
					coordsys.getPointForDrawing(xmax1-thickness,ymax1));	
								
		}
		
		gridOutlineIndex = brush.end();
		

		return true;
	}

	@Override
	protected void updateForView(){
		if (getView3D().viewChanged()){
			//if (getView3D().useClippingCube())
				if (!getView3D().viewChangedByTranslate() && !getView3D().viewChangedByZoom())	//only rotation			
					return;
			
			setWaitForUpdate();
		}
		
	}
	
	@Override
	public void setWaitForUpdate(){
		
		super.setWaitForUpdate();
		setMinMax();
	}
	
	
	/**
	 * set x-y min/max values
	 */
	protected void setMinMax(){
		setMinMax(getView3D().getClippingVertex(0), getView3D().getClippingVertex(1), getView3D().getClippingVertex(2), getView3D().getClippingVertex(4));
	}
	


	/**
	 * sets the min/max regarding a clipping box
	 * @param origin center of the clipping box
	 * @param vx first edge
	 * @param vy second edge
	 * @param vz third edge
	 */
	private void setMinMax(Coords origin, Coords vx, Coords vy, Coords vz){

		GeoPlane3D geo = (GeoPlane3D) getGeoElement();

		CoordMatrix m = geo.getCoordSys().getDrawingMatrix();
		Coords o = origin.projectPlane(m)[1];
		minmaxXFinal[0]=o.getX();
		minmaxYFinal[0]=o.getY();
		minmaxXFinal[1]=o.getX();
		minmaxYFinal[1]=o.getY();
		Coords[] v = new Coords[3];
		v[0] = vx.projectPlane(m)[1].sub(o);
		v[1] = vy.projectPlane(m)[1].sub(o);
		v[2] = vz.projectPlane(m)[1].sub(o);
		for (int i=0; i<3; i++){
			double x = v[i].getX();
			if (x<0)
				minmaxXFinal[0]+=x; //sub from xmin
			else
				minmaxXFinal[1]+=x; //add to xmax
			double y = v[i].getY();
			if (y<0)
				minmaxYFinal[0]+=y; //sub from ymin
			else
				minmaxYFinal[1]+=y; //add to ymax
			

		}
	}
	
	/*
	 * sets the min/max when the plate is visible
	 *
	protected void setMinMaxWhenPlateVisible(){
		setMinMax(getView3D().getClippingVertex(0), getView3D().getClippingVertex(1), getView3D().getClippingVertex(2), getView3D().getClippingVertex(4));
	}

	/**
	 * sets the min/max when only the grid is visible
	 *
	protected void setMinMaxWhenOnlyGridVisible(){
		
		
		//Application.debug(getView3D().getToScreenMatrix());
		
		Coords origin = getView3D().getToSceneMatrix().getOrigin();
		Coords vx = getView3D().getToSceneMatrix().getVx();
		Coords vy = getView3D().getToSceneMatrix().getVy();
		Coords vz = getView3D().getToSceneMatrix().getVz();
		float x1 = getView3D().getRenderer().getLeft();
		float x2 = getView3D().getRenderer().getRight();
		float y1 = getView3D().getRenderer().getBottom();
		float y2 = getView3D().getRenderer().getTop();
		float z1 = getView3D().getRenderer().getFront(true);
		float z2 = getView3D().getRenderer().getBack(true);
		
		Coords origin2 = origin.add(vx.mul(x1)).add(vy.mul(y1)).add(vz.mul(z1));
		
		setMinMax(origin2,
				origin2.add(vx.mul(x2-x1)),origin2.add(vy.mul(y2-y1)),origin2.add(vz.mul(z2-z1))
				);


	}
	*/

	private static final double REDUCE_BOUNDS_FACTOR = 0.975;
	
	
	
	
	
	
	@Override
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D;
	}		
	
	
	
	//TODO remove that
	public CoordMatrix4x4 getMatrix(){
		return CoordMatrix4x4.Identity();
	}
	
	
	@Override
	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_CURVES);
		super.addToDrawable3DLists(lists);
	}
    
    @Override
	public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_CURVES);
    	super.removeFromDrawable3DLists(lists);
    }
}
