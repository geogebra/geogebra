package geogebra3D.euclidian3D;




import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
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
	
	private Coords[] vertices;
	

	

	
	//cube reduction
	private double reduction = (1-1/Math.sqrt(2))/2;
	//private double reduction = (1-1./2.)/2;
	
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
		
		vertices = new Coords[8];
		for (int i=0; i<8; i++)
			vertices[i] = new Coords(0,0,0,1);
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
		

		double scale = getView3D().getScale();

		
		Coords origin = getView3D().getToSceneMatrix().getOrigin();
		double x0 = origin.getX(), y0 = origin.getY(), z0 = origin.getZ();
		
		double xmin = (renderer.getLeft())/scale+x0;
		double xmax = (renderer.getRight())/scale+x0;
		double ymin = (renderer.getBottom())/scale+z0;
		double ymax = (renderer.getTop())/scale+z0;
		double zmin  = (renderer.getFront(false))/scale+y0;
		double zmax = (renderer.getBack(false))/scale+y0;
		
		
		double xr = (xmax-xmin)*reduction;
		double yr = (ymax-ymin)*reduction;
		double zr = (zmax-zmin)*reduction;
		
		
		minMax[0][0] = xmin+xr;
		minMax[0][1] = xmax-xr;
		minMax[2][0] = ymin+yr;
		minMax[2][1] = ymax-yr;
		minMax[1][0] = zmin+zr;
		minMax[1][1] = zmax-zr;
		
		setVertices();
		//Application.debug(xmin+","+xmax+","+ymin+","+ymax+","+zmin+","+zmax);
		
		return minMax;
	}
	
	private void setVertices(){
		for (int x=0; x<2; x++)
			for (int y=0; y<2; y++)
				for (int z=0; z<2; z++){
					Coords vertex = vertices[x+2*y+4*z];
					vertex.setX(minMax[0][x]);
					vertex.setY(minMax[1][y]);
					vertex.setZ(minMax[2][z]);				
				}
	}
	
	/**
	 * 
	 * @param i index
	 * @return i-th vertex
	 */
	public Coords getVertex(int i){
		return vertices[i];
	}
	
	@Override
	protected boolean isVisible(){
		return getView3D().useClippingCube();
	}

	@Override
	protected boolean updateForItSelf(){
		

		Renderer renderer = getView3D().getRenderer();
		

		clippingBorder =  (float) (GeoElement.MAX_LINE_WIDTH*PlotterBrush.LINE3D_THICKNESS/getView3D().getScale());
				
		//geometry
		PlotterBrush brush = renderer.getGeometryManager().getBrush();

		brush.start(8);
		//clippingBorder = 
		brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());
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
