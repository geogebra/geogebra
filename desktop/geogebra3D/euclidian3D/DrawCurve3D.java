package geogebra3D.euclidian3D;

import geogebra.kernel.Matrix.Coords;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.plots.CurveTree;
import geogebra3D.euclidian3D.plots.CurveMesh;
import geogebra3D.kernel3D.GeoCurveCartesian3D;

/**
 * @author ggb3D
 * 
 * Drawable for GeoCurveCartesian3D
 *
 */
public class DrawCurve3D extends Drawable3DCurves {
	private final boolean useOldCurves = true;

	private CurveMesh mesh;
	private CurveTree tree;
	
	
	/** handle to the curve */
	private GeoCurveCartesian3D curve;
	
	private double savedRadius;
	
	private final double radiusMaxFactor = 1.1;
	private final double radiusMinFactor = 0.9;
	
	
	/**
	 * @param a_view3d the 3D view where the curve is drawn
	 * @param curve the 3D curve to draw
	 */
	public DrawCurve3D(EuclidianView3D a_view3d, GeoCurveCartesian3D curve) {
		super(a_view3d,curve);
		this.curve=curve;
		if(useOldCurves)
			tree = new CurveTree(curve, a_view3d);
		else {
			updateRadius();
			mesh = new CurveMesh(curve, savedRadius,(float)a_view3d.getScale());
		}
	}
	

	public void drawGeometry(Renderer renderer) {
		
		renderer.getGeometryManager().draw(getGeometryIndex());
		
	}
	
	/**
	 * Decides if the curve should be redrawn or not depending on how the view changes
	 * @return
	 */
	private boolean needRedraw(){
		double currRad = currentRadius();
		if(currRad>savedRadius*radiusMaxFactor || currRad< savedRadius*radiusMinFactor){
			savedRadius=currRad;
			return true;
		}
		return false;
	}
	
	/** gets the viewing radius based on the viewing frustum 
	 */
	private void updateRadius() {
		EuclidianView3D view = getView3D();
		Renderer temp = view.getRenderer();
		double x1 = temp.getLeft();
		double x2 = temp.getRight();
		double y1 = temp.getTop();
		double y2 = temp.getBottom();
		double z1 = temp.getFront(true);
		double z2 = temp.getBack(true);
		Coords [] v = new Coords[8];
		v[0] = new Coords(x1,y1,z1,0);
		v[1] = new Coords(x1,y2,z1,0);
		v[2] = new Coords(x1,y1,z2,0);
		v[3] = new Coords(x1,y2,z2,0);
		v[4] = new Coords(x2,y1,z1,0);
		v[5] = new Coords(x2,y2,z1,0);
		v[6] = new Coords(x2,y1,z2,0);
		v[7] = new Coords(x2,y2,z2,0);

		savedRadius=0;
		double norm;
		for(int i = 0; i < 8; i++){
			view.toSceneCoords3D(v[i]);
			norm = v[i].norm();
			if(norm>savedRadius)
				savedRadius=norm;
		}
	}
	
		/** gets the viewing radius based on the viewing frustum 
	 */
	private double currentRadius() {
		EuclidianView3D view = getView3D();
		Renderer temp = view.getRenderer();
		double x1 = temp.getLeft();
		double x2 = temp.getRight();
		double y1 = temp.getTop();
		double y2 = temp.getBottom();
		double z1 = temp.getFront(true);
		double z2 = temp.getBack(true);
		Coords [] v = new Coords[8];
		v[0] = new Coords(x1,y1,z1,1);
		v[1] = new Coords(x1,y2,z1,1);
		v[2] = new Coords(x1,y1,z2,1);
		v[3] = new Coords(x1,y2,z2,1);
		v[4] = new Coords(x2,y1,z1,1);
		v[5] = new Coords(x2,y2,z1,1);
		v[6] = new Coords(x2,y1,z2,1);
		v[7] = new Coords(x2,y2,z2,1);
		
		double radius=0;
		for(int i = 0; i < 8; i++){
			view.toSceneCoords3D(v[i]);
			if(v[i].norm()>radius)
				radius=v[i].norm();
		}
		return radius;
	}
	
	protected boolean updateForItSelf(){

		boolean ret = true;
		


		
		//updateColors();

		if(useOldCurves){
			Renderer renderer = getView3D().getRenderer();
		
			if (!curve.isEuclidianVisible() || !curve.isDefined()){
				setGeometryIndex(-1);
			
			}else{
				

			
				needRedraw();
				tree = new CurveTree(curve, getView3D());
			
				PlotterBrush brush = renderer.getGeometryManager().getBrush();

				brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());

				brush.start(8);
			
				brush.draw(tree,savedRadius);

				setGeometryIndex(brush.end());
			}
		} else {
			
			if(elementHasChanged){
				elementHasChanged = false;
				mesh.updateParameters();
			}
			
			Renderer renderer = getView3D().getRenderer();
//			mesh.setRadius(savedRadius);
//			ret = mesh.optimize();
				
		
			PlotterBrush brush = renderer.getGeometryManager().getBrush();
			brush.start(8);
			brush.draw(mesh,savedRadius);

			setGeometryIndex(brush.end());
		}
		return ret;
	}
	
	
	protected void updateForView(){
		if (!getView3D().viewChanged())
			return;
		
		if(!useOldCurves){
			EuclidianView3D view = getView3D();
//			mesh.updateScale((float)view.getScale());
		}
		
		if(needRedraw()){
			updateForItSelf();
		}
	}
	
	
	
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}

	

}
