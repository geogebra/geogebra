package geogebra3D.euclidian3D;




import geogebra.common.euclidian.EuclidianConstants;
import geogebra.euclidian.Hits;
import geogebra.euclidian.Previewable;
import geogebra.gui.GuiManager.NumberInputHandler;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.AlgoPolyhedronPointsPrism;
import geogebra3D.kernel3D.GeoPolyhedron;

import java.util.ArrayList;






/**
 * Class for drawing 3D polygons.
 * @author matthieu
 *
 */
public class DrawPolyhedron3D extends Drawable3DSurfaces implements Previewable{


	
	
	

	
	//drawing

	public void drawGeometry(Renderer renderer) {

	}
	
	public void drawGeometryPicked(Renderer renderer){
		drawGeometry(renderer);
	}
	
	public void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}
	
	
	public void drawGeometryHidden(Renderer renderer){};
	
	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D;
	}	
	
	




	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_CLOSED_SURFACES);
	}
    
    public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_CLOSED_SURFACES);
    }

	
	
	protected boolean updateForItSelf(){
		return true;
		
	}
	

	
	
	
	////////////////////////////////
	// Previewable interface 

	
	
	@SuppressWarnings("rawtypes")
	/** basis */
	private ArrayList selectedPolygons;
	
	
	/** segments of the polygon preview */
	private ArrayList<DrawSegment3D> segments;
	
	@SuppressWarnings("rawtypes")
	private ArrayList<ArrayList> segmentsPoints;
	
	private AlgoPolyhedronComputed algo;
	
	private GeoNumeric height;
	
	private GeoPolygon basis;
	

	/**
	 * Constructor for previewable
	 * @param a_view3D
	 * @param selectedPolygon
	 */
	@SuppressWarnings("rawtypes")
	public DrawPolyhedron3D(EuclidianView3D a_view3D, ArrayList selectedPolygons){
		
		super(a_view3D);
		
		
		this.selectedPolygons = selectedPolygons;
		
		updatePreview();
		
		
		
	}	

	








	public void updateMousePos(double xRW, double yRW) {	
		// TODO Auto-generated method stub
		
	}

	
	


	public void updatePreview() {
		
		//Application.debug(selectedPolygons.size()+", algo==null:"+(algo==null));
		
		if (selectedPolygons.size()==1 && algo==null){
				
		
			//create the height
			height = new GeoNumeric(getView3D().getKernel().getConstruction(), 0.0001);
			
			//basis
			basis = (GeoPolygon) selectedPolygons.get(0);
			
			//create the algo
			algo = new AlgoPolyhedronComputed(getView3D().getKernel().getConstruction(),
					null, 
					basis, 
					height);
			algo.removeOutputFromAlgebraView();
			algo.removeOutputFromPicking();
			algo.setOutputPointsEuclidianVisible(false);
			algo.notifyUpdateOutputPoints();
			
			//sets the top face to be handled
			getView3D().getEuclidianController().setHandledGeo(algo.getTopFace());


			//ensure correct drawing of visible parts of the previewable
			algo.setOutputSegmentsAndPolygonsEuclidianVisible(true);
			algo.notifyUpdateOutputSegmentsAndPolygons();

		}
	}
	
	
	
	
	public void disposePreview() {
		super.disposePreview();
		
		getView3D().getEuclidianController().setHandledGeo(null);
		
		if (algo!=null){			
			//remove the algo
			algo.remove();	
			algo=null;
		}
	}
	
	public void createPolyhedron(){
		getView3D().getEuclidianController().setHandledGeo(null);
		
		if (algo!=null){
			
			//clear current selections : remove basis polygon from selections
			getView3D().getEuclidianController().clearSelections();
			
			//add current height to selected numeric (will be used on next EuclidianView3D::rightPrism() call)
			Hits hits = new Hits();
			
			if (algo.computed==1){//if height has not been set by dragging, ask one
				Application app = getView3D().getApplication();
				Boolean sign = new Boolean(false);
				NumberValue num = 
					app.getGuiManager().showNumberInputDialog(
							app.getMenu(getView3D().getKernel().getModeText(EuclidianConstants.MODE_RIGHT_PRISM)),
							app.getPlain("Altitude"), null, 
							//check basis direction / view direction to say if the sign has to be forced
							basis.getMainDirection().dotproduct(getView3D().getViewDirection())>0,
							app.getPlain("PositiveValuesFollowTheView"));
				//if (num==null)//button cancel
				//	selectedPolygons.clear();
				//Application.debug(num);
				hits.add((GeoElement) num);
			}else
				hits.add(height);
			
			getView3D().getEuclidianController().addSelectedNumeric(hits, 1, false);

			
			//remove the algo
			algo.remove();	
			algo=null;
		}
	}
	
	
	
	private class AlgoPolyhedronComputed extends AlgoPolyhedronPointsPrism{

		public AlgoPolyhedronComputed(Construction c, String[] labels,
				GeoPolygon polygon, NumberValue height) {
			super(c, labels, polygon, height);
		}
		
		private int computed;
		
		protected void compute() {
			super.compute();
			computed++;			
		}
		
	}
	

}
