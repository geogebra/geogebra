package geogebra3D.euclidian3D;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.Textures;
import geogebra3D.kernel3D.GeoPlane3D;

import java.awt.Color;




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
	
	
	

	public void drawGeometry(Renderer renderer) {

		if (!((GeoPlane3D)getGeoElement()).isPlateVisible())
			return;

		renderer.setLayer(getGeoElement().getLayer()-1f); //-1f for z-fighting with planes
		renderer.getGeometryManager().draw(getGeometryIndex());	
		renderer.setLayer(0);
		

		
	}
	
	
	public void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}
	
	
	
	public void drawGeometryPicked(Renderer renderer){}
	

	public void drawHidden(Renderer renderer){
		//renderer.setMatrix(getMatrix());
		//setHighlightingColor(1f);
		drawGeometryHidden(renderer);
	}; 

	public void drawGeometryHidden(Renderer renderer){ 
		
		if(!isVisible())
			return;
		
		if (!isGridVisible()) return;

		//dash
		renderer.getTextures().loadTextureNearest(Textures.DASH_SHORT);
		renderer.getGeometryManager().draw(gridIndex);
		
		renderer.getTextures().loadTextureNearest(Textures.DASH_LONG);
		renderer.getGeometryManager().draw(gridOutlineIndex);

		
	};
	
	/**
	 * 
	 * @return true if grid is visible
	 */
	protected boolean isGridVisible(){
		return ((GeoPlane3D)getGeoElement()).isGridVisible() || viewDirectionIsParallel;
	}
	
	
	
	
	protected boolean updateForItSelf(){
		return updateForItSelf(true);
	}

	
	protected boolean updateForItSelf(boolean checkTime){
		
		
		//super.updateForItSelf();

		Renderer renderer = getView3D().getRenderer();
		GeoPlane3D geo = (GeoPlane3D) getGeoElement();
		CoordSys coordsys = geo.getCoordSys();
		
		
		
		if (checkTime)
			setMinMaxToGeo();
		
		float xmin = (float) geo.getXmin(), xmax = (float) geo.getXmax(), xdelta = xmax-xmin; 
		float ymin = (float) geo.getYmin(), ymax = (float) geo.getYmax(), ydelta = ymax-ymin; 
		
		// plane	
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		
		surface.start(geo);
		
		surface.setU(xmin,xmax);surface.setNbU(2);
		surface.setV(ymin,ymax);surface.setNbV(2);

		if (!getView3D().useClippingCube()){
			float fading;
			fading = xdelta * geo.getFading();
			surface.setUFading(fading, fading);
			fading = ydelta * geo.getFading();
			surface.setVFading(fading, fading);
		}
		surface.draw();
		setGeometryIndex(surface.end());
		
		
		
		
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

		brush.setColor(geogebra.awt.Color.getAwtColor(getGeoElement().getObjectColor()));
		
		//double dx = Math.max(geo.getGridXd(), geo.getGridYd()); //TODO
		double dx = Math.min(geo.getGridXd(), geo.getGridYd());
		double dy = dx; //TODO
		
		//Application.debug("geo:"+getGeoElement().getLabel()+"\n"+dx+","+dy+"\nx="+geo.getXmin()+","+geo.getXmax()+"\nthickness="+getGeoElement().getLineThickness());
		
			//}else{
		if (!viewDirectionIsParallel){
			//along x axis
			brush.setAffineTexture(
					(0f-xmin)/ydelta,
					0.25f);
			int i0 = (int) (ymin/dy);
			if (ymin>0)
				i0++;
			for(int i=i0;i<=ymax/dy;i++)
				brush.segment(coordsys.getPointForDrawing(xmin,i*dy), 
						coordsys.getPointForDrawing(xmax,i*dy));	
			//along y axis
			brush.setAffineTexture(
					(0f-ymin)/xdelta,
					0.25f);
			i0 = (int) (xmin/dx);
			if (xmin>0)
				i0++;
			for(int i=i0;i<=xmax/dx;i++)
				brush.segment(coordsys.getPointForDrawing(i*dx, ymin), 
						coordsys.getPointForDrawing(i*dx, ymax));
		}
		
		gridIndex = brush.end();
		
		
		brush.start(8);
		boolean showClippingCube = getView3D().showClippingCube();
		if (viewDirectionIsParallel || showClippingCube){
			//draws the rectangle outline
			if (showClippingCube){
				brush.setAffineTexture(
						(0f-xmin)/ydelta,
						0.25f);
			}else
				brush.setPlainTexture();
			brush.segment(coordsys.getPointForDrawing(xmin,ymax-thickness), 
					coordsys.getPointForDrawing(xmax,ymax-thickness));
			brush.segment(coordsys.getPointForDrawing(xmin,ymin+thickness), 
					coordsys.getPointForDrawing(xmax,ymin+thickness));	
			
			if (showClippingCube){
				brush.setAffineTexture(
						(0f-ymin)/xdelta,
						0.25f);
			}			
			brush.segment(coordsys.getPointForDrawing(xmin+thickness,ymin), 
					coordsys.getPointForDrawing(xmin+thickness,ymax));
			brush.segment(coordsys.getPointForDrawing(xmax-thickness,ymin), 
					coordsys.getPointForDrawing(xmax-thickness,ymax));	
								
		}
		
		gridOutlineIndex = brush.end();
		
		if (checkTime)
			return timesUpForUpdate();

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
	
	/*
	private void updateMinMax(Coords v){
		double x = v.getX(), y = v.getY();
		if (x<minmaxXFinal[0])
			minmaxXFinal[0]=x;
		if (x>minmaxXFinal[1])
			minmaxXFinal[1]=x;
		if (y<minmaxYFinal[0])
			minmaxYFinal[0]=y;
		if (y>minmaxYFinal[1])
			minmaxYFinal[1]=y;		
	}
	*/
	
	protected void setMinMax(){

		//if (getView3D().useClippingCube()){
			

			GeoPlane3D geo = (GeoPlane3D) getGeoElement();
			CoordMatrix m = geo.getCoordSys().getDrawingMatrix();
			Coords o = getView3D().getClippingVertex(0).projectPlane(m)[1];
			minmaxXFinal[0]=o.getX();
			minmaxYFinal[0]=o.getY();
			minmaxXFinal[1]=o.getX();
			minmaxYFinal[1]=o.getY();
			Coords[] v = new Coords[3];
			v[0] = getView3D().getClippingVertex(1).projectPlane(m)[1].sub(o);
			v[1] = getView3D().getClippingVertex(2).projectPlane(m)[1].sub(o);
			v[2] = getView3D().getClippingVertex(4).projectPlane(m)[1].sub(o);
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
		
		/*
		}else{
			GeoPlane3D geo = (GeoPlane3D) getGeoElement();

			//record old values
			xmin=geo.getXmin();
			xmax=geo.getXmax();
			ymin=geo.getYmin();
			ymax=geo.getYmax();

			//calc new values
			Coords origin = geo.evaluatePoint(0, 0);
			Coords vx = geo.evaluatePoint(1, 0).sub(origin);
			Coords vy = geo.evaluatePoint(0, 1).sub(origin);

			Coords screenOrigin = getView3D().getToSceneMatrix().getOrigin();
			Coords[] project = geo.getCoordSys().getNormalProjectionForDrawing(screenOrigin);


			//Coords o = getView3D().getToScreenMatrix().mul(origin);
			Coords o = getView3D().getToScreenMatrix().mul(project[0]);

			//Application.debug("screenOrigin=\n"+screenOrigin+"\nproject[0]=\n"+project[0]+"\nScreenMatrix()=\n"+getView3D().getToScreenMatrix()+"\no=\n"+o);

			minmaxXFinal = getView3D().getRenderer().getIntervalInFrustum(
					new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
					o, getView3D().getToScreenMatrix().mul(vx), false);	

			minmaxYFinal = getView3D().getRenderer().getIntervalInFrustum(
					new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
					o, getView3D().getToScreenMatrix().mul(vy), false);		


			for (int i=0;i<2;i++){
				minmaxXFinal[i]+=project[1].getX();
				minmaxYFinal[i]+=project[1].getY();
			}



			setTime();
			reduceBounds(minmaxXFinal);
			reduceBounds(minmaxYFinal);
		}*/
	}
	
	private static final double REDUCE_BOUNDS_FACTOR = 0.975;
	
	private void reduceBounds(double[] minmax){
		double min = minmax[0];
		double max = minmax[1];
		
		
		minmax[0] = min*REDUCE_BOUNDS_FACTOR+max*(1-REDUCE_BOUNDS_FACTOR);
		minmax[1] = min*(1-REDUCE_BOUNDS_FACTOR)+max*REDUCE_BOUNDS_FACTOR;
	}

	protected void setMinMaxToGeo(){
		long deltaT = getDeltaT();
		
		//Application.debug("deltaT="+deltaT+"\nSystem.currentTimeMillis()="+System.currentTimeMillis());
		GeoPlane3D geo = (GeoPlane3D) getGeoElement();
		
		if (deltaT>0){
			if (deltaT>TIME_DURATION){				
				geo.setGridCorners(minmaxXFinal[0], minmaxYFinal[0], minmaxXFinal[1], minmaxYFinal[1]);
			}else{
				double[] minmax = new double[4];
				double dt = (double) deltaT*TIME_FACTOR;
				minmax[0]=minmaxXFinal[0]*dt+xmin*(1-dt);
				minmax[1]=minmaxYFinal[0]*dt+ymin*(1-dt);
				minmax[2]=minmaxXFinal[1]*dt+xmax*(1-dt);
				minmax[3]=minmaxYFinal[1]*dt+ymax*(1-dt);
				geo.setGridCorners(minmax[0], minmax[1], minmax[2], minmax[3]);

			}
		}else
			geo.setGridCorners(xmin, ymin, xmax, ymax);
	}
	
	
	
	
	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D;
	}		
	
	
	
	//TODO remove that
	public CoordMatrix4x4 getMatrix(){
		return CoordMatrix4x4.Identity();
	}
	
	
	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_CURVES);
		super.addToDrawable3DLists(lists);
	}
    
    public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_CURVES);
    	super.removeFromDrawable3DLists(lists);
    }
}
