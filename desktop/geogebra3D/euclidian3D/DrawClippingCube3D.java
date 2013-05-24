package geogebra3D.euclidian3D;




import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
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
	
	private double[][] minMax, minMaxLarge;
	
	private Coords[] vertices;
	

	static private double REDUCTION_LARGE = 0; //(1-1./1)/2	
	

	static private double[] REDUCTION_VALUES = {
		(1-1./Math.sqrt(3))/2, //small
		(1-1./Math.sqrt(2))/2, //medium
		REDUCTION_LARGE //large
	};
	
	static private int REDUCTION_LENGTH = 3;

	
	
	/**
	 * Common constructor
	 * @param a_view3D view
	 * @param clippingCube geo
	 */
	public DrawClippingCube3D(EuclidianView3D a_view3D, GeoClippingCube3D clippingCube){
		
		super(a_view3D, clippingCube);
		
		minMax = new double[3][];
		minMaxLarge = new double[3][];
		
		for (int i=0; i<3; i++){
			minMax[i] = new double[2];
			minMaxLarge[i] = new double[2];
		}
		
		vertices = new Coords[8];
		for (int i=0; i<8; i++)
			vertices[i] = new Coords(0,0,0,1);
	}
	
	/*
	public double xmin(){ return minMax[0][0]; }
	public double ymin(){ return minMax[1][0]; }	
	public double zmin(){ return minMax[2][0]; }	
	public double xmax(){ return minMax[0][1]; }
	public double ymax(){ return minMax[1][1]; }	
	public double zmax(){ return minMax[2][1]; }	
	*/
	

	
	
	/**
	 * update the x,y,z min/max values
	 * @return the min/max values
	 */
	public double[][] updateMinMax(){
		
		EuclidianView3D view = getView3D(); 
		
		Renderer renderer = view.getRenderer();
		

		double scale = view.getScale();

		
		Coords origin = getView3D().getToSceneMatrix().getOrigin();
		double x0 = origin.getX(), y0 = origin.getY(), z0 = origin.getZ();
		
		double xmin = (renderer.getLeft())/scale+x0;
		double xmax = (renderer.getRight())/scale+x0;
		double ymin = (renderer.getBottom())/scale+z0;
		double ymax = (renderer.getTop())/scale+z0;
		double zmin  = (renderer.getFront(false))/scale+y0;
		double zmax = (renderer.getBack(false))/scale+y0;
		
		int reductionIndex = ((GeoClippingCube3D) getGeoElement()).getReduction();
		double rv = REDUCTION_VALUES[reductionIndex];
		double xr = (xmax-xmin)*rv;
		double yr = (ymax-ymin)*rv;
		double zr = (zmax-zmin)*rv;
		
		
		minMax[0][0] = xmin+xr;
		minMax[0][1] = xmax-xr;
		minMax[2][0] = ymin+yr;
		minMax[2][1] = ymax-yr;
		minMax[1][0] = zmin+zr;
		minMax[1][1] = zmax-zr;
		
		
		setVertices();
		view.setXYMinMax(minMax);

		
		
		
		

		//minMaxLarge to cut lines
		
		rv = (1-(1-rv*2)*1.1)/2;
		xr = (xmax-xmin)*rv;
		yr = (ymax-ymin)*rv;
		zr = (zmax-zmin)*rv;
		
		
		minMaxLarge[0][0] = xmin+xr;
		minMaxLarge[0][1] = xmax-xr;
		minMaxLarge[2][0] = ymin+yr;
		minMaxLarge[2][1] = ymax-yr;
		minMaxLarge[1][0] = zmin+zr;
		minMaxLarge[1][1] = zmax-zr;

		
		
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
	
	private Coords getVertexWithBorder(int x, int y, int z){
		return vertices[x+2*y+4*z].add(new Coords(clippingBorder*(1-2*x),clippingBorder*(1-2*y),clippingBorder*(1-2*z),0));
	}
	
	/*
	@Override
	protected boolean isVisible(){
		return getView3D().useClippingCube();
	}
    */

	@Override
	protected boolean updateForItSelf(){
		

		Renderer renderer = getView3D().getRenderer();
		

		//clippingBorder =  (float) (GeoElement.MAX_LINE_WIDTH*PlotterBrush.LINE3D_THICKNESS/getView3D().getScale());
				
		//geometry
		PlotterBrush brush = renderer.getGeometryManager().getBrush();

		brush.start(8);
		clippingBorder = 
				brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());
		brush.setAffineTexture(
				0.5f,  0.25f);

		brush.segment(getVertexWithBorder(0, 0, 0),getVertexWithBorder(1, 0, 0));
		brush.segment(getVertexWithBorder(0, 0, 0),getVertexWithBorder(0, 1, 0));
		brush.segment(getVertexWithBorder(0, 0, 0),getVertexWithBorder(0, 0, 1));
		
		brush.segment(getVertexWithBorder(1, 1, 0),getVertexWithBorder(0, 1, 0));
		brush.segment(getVertexWithBorder(1, 1, 0),getVertexWithBorder(1, 0, 0));
		brush.segment(getVertexWithBorder(1, 1, 0),getVertexWithBorder(1, 1, 1));
		
		brush.segment(getVertexWithBorder(1, 0, 1),getVertexWithBorder(0, 0, 1));
		brush.segment(getVertexWithBorder(1, 0, 1),getVertexWithBorder(1, 1, 1));
		brush.segment(getVertexWithBorder(1, 0, 1),getVertexWithBorder(1, 0, 0));
		
		brush.segment(getVertexWithBorder(0, 1, 1),getVertexWithBorder(1, 1, 1));
		brush.segment(getVertexWithBorder(0, 1, 1),getVertexWithBorder(0, 0, 1));
		brush.segment(getVertexWithBorder(0, 1, 1),getVertexWithBorder(0, 1, 0));
		
		setGeometryIndex(brush.end());

		
		updateEquations();
		
		return true;
	}
	
	
	private void updateEquations(){
		Renderer renderer = getView3D().getRenderer();
		CoordMatrix mInvTranspose = getView3D().getToSceneMatrixTranspose();		
		renderer.setClipPlane(0, mInvTranspose.mul( new Coords(1,0,0,-minMax[0][0])).get());
		renderer.setClipPlane(1, mInvTranspose.mul( new Coords(-1,0,0,minMax[0][1])).get());
		renderer.setClipPlane(2, mInvTranspose.mul( new Coords(0,1,0,-minMax[1][0])).get());
		renderer.setClipPlane(3, mInvTranspose.mul( new Coords(0,-1,0,minMax[1][1])).get());
		renderer.setClipPlane(4, mInvTranspose.mul( new Coords(0,0,1,-minMax[2][0])).get());
		renderer.setClipPlane(5, mInvTranspose.mul( new Coords(0,0,-1,minMax[2][1])).get());

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
	
	
	/** for a line described by (o,v), return the min and max parameters to draw the line
	 * @param minmax initial interval
	 * @param o origin of the line
	 * @param v direction of the line
	 * @return interval to draw the line
	 */
	public double[] getIntervalClipped(double[] minmax, 
			Coords o, Coords v){
		
		for (int i = 1; i <= 3 ; i++){
			double min = (minMaxLarge[i-1][0] - o.get(i))/v.get(i);
			double max = (minMaxLarge[i-1][1] - o.get(i))/v.get(i);		
			updateInterval(minmax, min, max);
		}
		
		return minmax;
	}
		
	/** return the intersection of intervals [minmax] and [v1,v2]
	 * @param minmax initial interval
	 * @param v1 first value
	 * @param v2 second value
	 * @return intersection interval
	 */
	private static double[] updateInterval(double[] minmax, double v1, double v2){
		
		if (v1>v2){
			double v = v1;
			v1 = v2; v2 = v;
		}
		
		if (v1>minmax[0])
			minmax[0] = v1;
				
		if (v2<minmax[1])
			minmax[1] = v2;
	
		return minmax;
	}
	
	
	

}
