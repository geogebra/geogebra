package geogebra3D.euclidian3D;




import org.python.modules.math;

import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoClippingCube3D;




/**
 * Class for drawing 3D constant planes.
 * @author matthieu
 *
 */
public class DrawClippingCube3D extends Drawable3DCurves {
	
	/** "border extension" for clipping cube */
	private float clippingBorder;
	/** min-max values clipping cube */
	/*
	private double xmin, xmax, 
			ymin, ymax, 
			zmin, zmax;
			*/
	
	private double[][] minMax;
	
	//orientation of the scene
	private short leftRightIndex=0, bottomTopIndex=1, frontBackIndex=2;
	
	//center
	private double x0, y0, z0;
	
	//cube reduction
	private double reduction = (1-1/Math.sqrt(2))/2;
	
	/**
	 * Common constructor
	 * @param a_view3D view
	 * @param clippingCube geo
	 */
	public DrawClippingCube3D(EuclidianView3D a_view3D, GeoClippingCube3D clippingCube){
		
		super(a_view3D, clippingCube);
		
		minMax = new double[3][];
		for (int i=0; i<3; i++)
			minMax[i] = new double[2];
		
	}
	
	public double xmin(){ return minMax[0][0]; }
	public double ymin(){ return minMax[1][0]; }	
	public double zmin(){ return minMax[2][0]; }	
	public double xmax(){ return minMax[0][1]; }
	public double ymax(){ return minMax[1][1]; }	
	public double zmax(){ return minMax[2][1]; }	
	
	/**
	 * update the x,y,z min/max values
	 * @return the min/max values
	 */
	public double[][] updateMinMax(){
		
		Renderer renderer = getView3D().getRenderer();
		
		x0 = getView3D().getXZero();
		y0 = getView3D().getYZero();
		z0 = getView3D().getZZero();
		double scale = getView3D().getScale();

		/*
		//center
		double x0 = -getView3D().getXZero()/scale;
		double y0 = -getView3D().getYZero()/scale;
		double z0 = -getView3D().getZZero()/scale;
		
		
		minMax[0][0] = x0;
		minMax[0][1] = x0;
		minMax[1][0] = y0;
		minMax[1][1] = y0;
		minMax[2][0] = z0;
		minMax[2][1] = z0;
		*/
		
		//check orientation of the scene
		
		Coords d = getView3D().getViewDirection();
		//Application.debug(d);
		double x = Math.abs(d.getX());
		double y = Math.abs(d.getY());
		double z = Math.abs(d.getZ());
		if (x>y){
			if (x>z){//x axis is the most orthogonal to screen
				//Application.debug("x");
				leftRightIndex = 1;//y
				bottomTopIndex = 2;//z
				frontBackIndex = 0;//x
				if (d.getX()>0)
					x0=-x0;
			}else{//z axis is the most orthogonal to screen
				setOrientationZ();
			}
		}else if (y>z){//y axis is the most orthogonal to screen
			//Application.debug("y");
			leftRightIndex = 0;//x
			bottomTopIndex = 2;//z
			frontBackIndex = 1;//y
			if (d.getY()<0)
				x0=-x0;
		}else{//z axis is the most orthogonal to screen
			setOrientationZ();
		}

	
		
		/*
		minMax[leftRightIndex][0] += renderer.getLeft()/scale;
		minMax[leftRightIndex][1] += renderer.getRight()/scale;
		minMax[bottomTopIndex][0] += renderer.getBottom()/scale;
		minMax[bottomTopIndex][1] += renderer.getTop()/scale;
		minMax[frontBackIndex][0] += renderer.getFront(false)/scale;
		minMax[frontBackIndex][1] += renderer.getBack(false)/scale;
		*/
		/*
		minMax[leftRightIndex][0] = (renderer.getLeft()-x0)/scale;
		minMax[leftRightIndex][1] = (renderer.getRight()-x0)/scale;
		minMax[bottomTopIndex][0] = (renderer.getBottom()-y0)/scale;
		minMax[bottomTopIndex][1] = (renderer.getTop()-y0)/scale;
		minMax[frontBackIndex][0] = (renderer.getFront(false)-z0)/scale;
		minMax[frontBackIndex][1] = (renderer.getBack(false)-z0)/scale;
		*/
		
		/*
		double xmin = (renderer.getLeft()-x0)/scale;
		double xmax = (renderer.getRight()-x0)/scale;
		double ymin = (renderer.getBottom()-y0)/scale;
		double ymax = (renderer.getTop()-y0)/scale;
		double zmin  = (renderer.getFront(false)-z0)/scale;
		double zmax = (renderer.getBack(false)-z0)/scale;
		*/
		
		Coords origin = getView3D().getToSceneMatrix().getOrigin();
		//Application.debug(origin);
		x0 = origin.getX(); y0 = origin.getY(); z0 = origin.getZ();
		
		double xmin = (renderer.getLeft())/scale+x0;
		double xmax = (renderer.getRight())/scale+x0;
		double ymin = (renderer.getBottom())/scale+z0;
		double ymax = (renderer.getTop())/scale+z0;
		double zmin  = (renderer.getFront(false))/scale+y0;
		double zmax = (renderer.getBack(false))/scale+y0;
		
		
		double xr = (xmax-xmin)*reduction;
		double yr = (ymax-ymin)*reduction;
		double zr = (zmax-zmin)*reduction;
		
		leftRightIndex=0;
		bottomTopIndex=2;
		frontBackIndex=1;
		
		minMax[leftRightIndex][0] = xmin+xr;
		minMax[leftRightIndex][1] = xmax-xr;
		minMax[bottomTopIndex][0] = ymin+yr;
		minMax[bottomTopIndex][1] = ymax-yr;
		minMax[frontBackIndex][0] = zmin+zr;
		minMax[frontBackIndex][1] = zmax-zr;
		
		/*
		xmin = -2; xmax = 2; 
		ymin = -2; ymax = 2; 
		zmin = -1; zmax = 3;
		*/
		
		//Application.debug(xmin+","+xmax+","+ymin+","+ymax+","+zmin+","+zmax);
		
		return minMax;
	}
	
	private void setOrientationZ(){
		//Application.debug("z\n"+getView3D().getToSceneMatrix()+"\nto screen\n"+getView3D().getToScreenMatrix());

		frontBackIndex = 2;//z
		
		Coords vx = getView3D().getToScreenMatrix().getVx();
		Coords vy = getView3D().getToScreenMatrix().getVy();
		if (Math.abs(vx.getX())>Math.abs(vy.getX())){
			leftRightIndex = 0;//x
			bottomTopIndex = 1;//y
			if (vx.getX()<0) x0=-x0;
			if (vy.getY()<0) y0=-y0;
		}else{
			leftRightIndex = 1;//y
			bottomTopIndex = 0;//x
			if (vy.getX()<0) x0=-x0;
			if (vx.getY()<0) y0=-y0;
		}
			
	}


	@Override
	protected boolean updateForItSelf(){
		

		Renderer renderer = getView3D().getRenderer();
		
		//geometry
		PlotterBrush brush = renderer.getGeometryManager().getBrush();

		brush.start(8);
		clippingBorder = brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());
		brush.setAffineTexture(
				0.5f,  0.25f);

		Coords corner = new Coords(xmin(), ymin(), zmin(), 0);
		brush.segment(corner,new Coords(xmax(), ymin(), zmin(), 0));
		brush.segment(corner,new Coords(xmin(), ymax(), zmin(), 0));
		brush.segment(corner,new Coords(xmin(), ymin(), zmax(), 0));

		corner = new Coords(xmax(), ymax(), zmax(), 0);
		brush.segment(corner,new Coords(xmin(), ymax(), zmax(), 0));
		brush.segment(corner,new Coords(xmax(), ymin(), zmax(), 0));
		brush.segment(corner,new Coords(xmax(), ymax(), zmin(), 0));

		brush.segment(new Coords(xmax(), ymax(), zmin(), 0),new Coords(xmax(), ymin(), zmin(), 0));
		brush.segment(new Coords(xmax(), ymin(), zmax(), 0),new Coords(xmax(), ymin(), zmin(), 0));
		brush.segment(new Coords(xmax(), ymin(), zmax(), 0),new Coords(xmin(), ymin(), zmax(), 0));
		brush.segment(new Coords(xmin(), ymax(), zmax(), 0),new Coords(xmin(), ymin(), zmax(), 0));
		brush.segment(new Coords(xmin(), ymax(), zmax(), 0),new Coords(xmin(), ymax(), zmin(), 0));
		brush.segment(new Coords(xmax(), ymax(), zmin(), 0),new Coords(xmin(), ymax(), zmin(), 0));

		setGeometryIndex(brush.end());

		
		updateEquations();
		
		return true;
	}
	
	
	private void updateEquations(){
		Renderer renderer = getView3D().getRenderer();
		CoordMatrix mInvTranspose = getView3D().getToSceneMatrixTranspose();		
		renderer.setClipPlane(0, mInvTranspose.mul( new Coords(1,0,0,-xmin()+clippingBorder)).get());
		renderer.setClipPlane(1, mInvTranspose.mul( new Coords(-1,0,0,xmax()+clippingBorder)).get());
		renderer.setClipPlane(2, mInvTranspose.mul( new Coords(0,1,0,-ymin()+clippingBorder)).get());
		renderer.setClipPlane(3, mInvTranspose.mul( new Coords(0,-1,0,ymax()+clippingBorder)).get());
		renderer.setClipPlane(4, mInvTranspose.mul( new Coords(0,0,1,-zmin()+clippingBorder)).get());
		renderer.setClipPlane(5, mInvTranspose.mul( new Coords(0,0,-1,zmax()+clippingBorder)).get());

	}
	
	

	@Override
	protected void updateForView(){

	}
	

	@Override
	public void drawGeometry(Renderer renderer) {
		
		renderer.getGeometryManager().draw(getGeometryIndex());		
	}


	@Override
	public int getPickOrder() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

}
