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
	private double ccXmin = -4, ccXmax = 4, 
			ccYmin = -4, ccYmax = 4, 
			ccZmin = -4, ccZmax = 4;
	

	
	/**
	 * Common constructor
	 * @param a_view3D view
	 * @param clippingCube geo
	 */
	public DrawClippingCube3D(EuclidianView3D a_view3D, GeoClippingCube3D clippingCube){
		
		super(a_view3D, clippingCube);
		
	}
	
	public double[][] updateMinMax(){
		ccXmin = -2; ccXmax = 2; 
		ccYmin = -2; ccYmax = 2; 
		ccZmin = -1; ccZmax = 3;
		
		return new double[][] {{ccXmin, ccXmax},{ccYmin, ccYmax},{ccZmin, ccZmax}};
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

		Coords corner = new Coords(ccXmin, ccYmin, ccZmin, 0);
		brush.segment(corner,new Coords(ccXmax, ccYmin, ccZmin, 0));
		brush.segment(corner,new Coords(ccXmin, ccYmax, ccZmin, 0));
		brush.segment(corner,new Coords(ccXmin, ccYmin, ccZmax, 0));

		corner = new Coords(ccXmax, ccYmax, ccZmax, 0);
		brush.segment(corner,new Coords(ccXmin, ccYmax, ccZmax, 0));
		brush.segment(corner,new Coords(ccXmax, ccYmin, ccZmax, 0));
		brush.segment(corner,new Coords(ccXmax, ccYmax, ccZmin, 0));

		brush.segment(new Coords(ccXmax, ccYmax, ccZmin, 0),new Coords(ccXmax, ccYmin, ccZmin, 0));
		brush.segment(new Coords(ccXmax, ccYmin, ccZmax, 0),new Coords(ccXmax, ccYmin, ccZmin, 0));
		brush.segment(new Coords(ccXmax, ccYmin, ccZmax, 0),new Coords(ccXmin, ccYmin, ccZmax, 0));
		brush.segment(new Coords(ccXmin, ccYmax, ccZmax, 0),new Coords(ccXmin, ccYmin, ccZmax, 0));
		brush.segment(new Coords(ccXmin, ccYmax, ccZmax, 0),new Coords(ccXmin, ccYmax, ccZmin, 0));
		brush.segment(new Coords(ccXmax, ccYmax, ccZmin, 0),new Coords(ccXmin, ccYmax, ccZmin, 0));

		setGeometryIndex(brush.end());

		
		updateEquations();
		
		return true;
	}
	
	
	private void updateEquations(){
		Renderer renderer = getView3D().getRenderer();
		CoordMatrix mInvTranspose = getView3D().getToSceneMatrixTranspose();		
		renderer.setClipPlane(0, mInvTranspose.mul( new Coords(1,0,0,-ccXmin+clippingBorder)).get());
		renderer.setClipPlane(1, mInvTranspose.mul( new Coords(-1,0,0,ccXmax+clippingBorder)).get());
		renderer.setClipPlane(2, mInvTranspose.mul( new Coords(0,1,0,-ccYmin+clippingBorder)).get());
		renderer.setClipPlane(3, mInvTranspose.mul( new Coords(0,-1,0,ccYmax+clippingBorder)).get());
		renderer.setClipPlane(4, mInvTranspose.mul( new Coords(0,0,1,-ccZmin+clippingBorder)).get());
		renderer.setClipPlane(5, mInvTranspose.mul( new Coords(0,0,-1,ccZmax+clippingBorder)).get());

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
