package geogebra3D.euclidian3D;




import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
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
	
	
	protected double xmin, xmax, ymin, ymax;
	double[] minmaxXFinal,minmaxYFinal;
	
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
		//renderer.initMatrix();
		renderer.getGeometryManager().draw(getGeometryIndex());
		//renderer.resetMatrix();
		
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
		
		float fading;
		fading = (float) (xdelta * geo.getFading());
		surface.setUFading(fading, fading);
		fading = (float) (ydelta * geo.getFading());
		surface.setVFading(fading, fading);
		surface.draw();
		setGeometryIndex(surface.end());
		
		
		
		
		// grid
		removeGeometryIndex(gridIndex);
		
		//if (Kernel.isZero(coordsys.getNormal().dotproduct(getView3D().getViewDirection()))){
		//Application.debug("v=\n"+coordsys.getEquationVector()+"\neye=\n"+getView3D().getEyePosition());
		if (Kernel.isZero(coordsys.getEquationVector().dotproduct(getView3D().getEyePosition()))){
			viewDirectionIsParallel = true;
		}else{
			viewDirectionIsParallel = false;
		}

		
		PlotterBrush brush = renderer.getGeometryManager().getBrush();
		
		brush.start(8);
		brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());

		brush.setColor(getGeoElement().getObjectColor());
		
		//double dx = Math.max(geo.getGridXd(), geo.getGridYd()); //TODO
		double dx = Math.min(geo.getGridXd(), geo.getGridYd());
		double dy = dx; //TODO
		
		//Application.debug("geo:"+getGeoElement().getLabel()+"\n"+dx+","+dy+"\nx="+geo.getXmin()+","+geo.getXmax()+"\nthickness="+getGeoElement().getLineThickness());
		
		
		if (viewDirectionIsParallel){
			//draws the rectangle outline
			brush.setPlainTexture();
			brush.segment(coordsys.getPointForDrawing(geo.getXmin(),geo.getYmin()), 
					coordsys.getPointForDrawing(geo.getXmin(),geo.getYmax()));	
			brush.segment(coordsys.getPointForDrawing(geo.getXmin(),geo.getYmin()), 
					coordsys.getPointForDrawing(geo.getXmax(),geo.getYmin()));				
			brush.segment(coordsys.getPointForDrawing(geo.getXmax(),geo.getYmax()), 
					coordsys.getPointForDrawing(geo.getXmin(),geo.getYmax()));	
			brush.segment(coordsys.getPointForDrawing(geo.getXmax(),geo.getYmax()), 
					coordsys.getPointForDrawing(geo.getXmax(),geo.getYmin()));				
		}else{
			//along x axis
			brush.setAffineTexture(
					(0f-ymin)/ydelta,
					0.25f);
			for(int i=(int) (geo.getYmin()/dy);i<=geo.getYmax()/dy;i++)
				brush.segment(coordsys.getPointForDrawing(geo.getXmin(),i*dy), 
						coordsys.getPointForDrawing(geo.getXmax(),i*dy));	
			//along y axis
			brush.setAffineTexture(
					(0f-xmin)/xdelta,
					0.25f);
			for(int i=(int) (geo.getXmin()/dx);i<=geo.getXmax()/dx;i++)
				brush.segment(coordsys.getPointForDrawing(i*dx, geo.getYmin()), 
						coordsys.getPointForDrawing(i*dx, geo.getYmax()));
		}
		
		
	
		gridIndex = brush.end();
		
		if (checkTime)
			return timesUpForUpdate();
		else
			return true;
	}

	protected void updateForView(){
		if (getView3D().viewChanged()){
			setWaitForUpdate();
			/*
			if (getView3D().viewChangedByRotate()){
				//Application.debug(getGeoElement().getMainDirection().dotproduct(getView3D().getViewDirection()));
				((GeoPlane3D) getGeoElement()).updateViewForPlaneDirection(
						getView3D().getViewDirection(),
						getView3D().getToScreenMatrix());
			}
			*/
		}
		
	}
	
	public void setWaitForUpdate(){
		
		super.setWaitForUpdate();
		setMinMax();
	}
	
	protected void setMinMax(){
		
		setTime();
		
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
		
			
			
		reduceBounds(minmaxXFinal);
		reduceBounds(minmaxYFinal);
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
