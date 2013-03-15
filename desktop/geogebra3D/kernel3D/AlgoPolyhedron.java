package geogebra3D.kernel3D;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.main.App;

import java.util.ArrayList;

/**
 * @author ggb3D
 * 
 * Creates a new GeoPolyhedron
 *
 */
public abstract class AlgoPolyhedron extends AlgoElement3D{

	
	
	
	/** points generated as output  */
	protected OutputHandler<GeoPoint3D> outputPoints;

	protected OutputHandler<GeoPolyhedron> outputPolyhedron;
	
	protected GeoPolyhedron polyhedron;
	
	
	/////////////////////////////////////////////
	// POLYHEDRON OF DETERMINED TYPE
	////////////////////////////////////////////
	
	protected AlgoPolyhedron(Construction c){
		super(c);
		
		cons.addToAlgorithmList(this);

		setIsOldFileVersion();

		outputPolyhedron=new OutputHandler<GeoPolyhedron>(new elementFactory<GeoPolyhedron>() {
			public GeoPolyhedron newElement() {
				GeoPolyhedron p=new GeoPolyhedron(cons);
				p.setParentAlgorithm(AlgoPolyhedron.this);
				return p;
			}
		});
		
		outputPolyhedron.adjustOutputSize(1);
		polyhedron = getPolyhedron();
		
		
		
		outputPoints=new OutputHandler<GeoPoint3D>(new elementFactory<GeoPoint3D>() {
			public GeoPoint3D newElement() {
				GeoPoint3D p=new GeoPoint3D(cons);
				p.setCoords(0, 0, 0, 1);
				p.setParentAlgorithm(AlgoPolyhedron.this);
				return p;
			}
		});
		
		createOutputPolygons();
		
		
		createOutputSegments();
			
		
		
		


		
		
	}
	
	
	private boolean isOldFileVersion;
	
	/**
	 * sets if it's an old file version
	 */
	protected void setIsOldFileVersion(){
		isOldFileVersion = GeoGebraConstants.IS_PRE_RELEASE 
				&& app.fileVersionBefore(App.getSubValues("4.9.10.0"));
	}
	
	/**
	 * @return if it's an old file version
	 */	
	protected boolean isOldFileVersion(){
		return isOldFileVersion;
	}

	/**
	 * create the faces of the polyhedron
	 */
    protected void createFaces(){
    	
    	if (isOldFileVersion())
    		polyhedron.updateFacesDeprecated();
    	else
    		polyhedron.createFaces();
    }
	
	/**
	 * create the output segments handlers
	 */
	abstract protected void createOutputSegments();
	
	/**
	 * @return an output handler for segments
	 */
	protected OutputHandler<GeoSegment3D> createOutputSegmentsHandler(){
		return new OutputHandler<GeoSegment3D>(new elementFactory<GeoSegment3D>() {
			public GeoSegment3D newElement() {
				GeoSegment3D s=new GeoSegment3D(cons);
				//s.setParentAlgorithm(AlgoPolyhedron.this);
				return s;
			}
		});
	}
	

	/**
	 * create the output polygons handlers
	 */
	abstract protected void createOutputPolygons();
		
	
	/**
	 * @return an output handler for polygons
	 */
	protected OutputHandler<GeoPolygon3D> createOutputPolygonsHandler(){
		return new OutputHandler<GeoPolygon3D>(new elementFactory<GeoPolygon3D>() {
			public GeoPolygon3D newElement() {
				GeoPolygon3D p=new GeoPolygon3D(cons);
				//p.setParentAlgorithm(AlgoPolyhedron.this);
				return p;
			}
		});
	}

	protected void addAlgoToInput(){
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}
	}
	
	
	abstract protected void updateOutput();

	

	
	protected void setOutput(){
		
		updateOutput();
        //cons.addToAlgorithmList(this);  
		
	}
	
	/**
	 * @return the polyhedron
	 */
	public GeoPolyhedron getPolyhedron(){
		return outputPolyhedron.getElement(0);
	}
	

	
	
	/*
    @Override
	public void update() {
    	
        // compute output from input
        compute();
        
        //polyhedron
        polyhedron.update();
        

    }
    
  */
    
	
	
	
	@Override
	protected void getOutputXML(StringBuilder sb){
		super.getOutputXML(sb);
		
		//append XML for polygon and segments linked once more, to avoid override of specific properties		
		for (GeoPolygon polygon : polyhedron.getPolygonsLinked())
			polygon.getXML(sb);
		for (GeoSegmentND segment : polyhedron.getSegmentsLinked())
			((GeoElement) segment).getXML(sb);
		
	}
	
	
	
	


	@Override
	public void removeOutputExcept(GeoElement keepGeo) {
		for (int i = 0; i < super.getOutputLength(); i++) {
			GeoElement geo = super.getOutput(i);
			if (geo != keepGeo) {
				if (geo.isGeoPoint()) {
					removePoint(geo);
				} else {
					geo.doRemove();
				}
			}
		}
	}


	private void removePoint(GeoElement oldPoint) {

		// remove dependent algorithms (e.g. segments) from update sets of
		// objects further up (e.g. polygon) the tree
		ArrayList<AlgoElement> algoList = oldPoint.getAlgorithmList();
		for (int k = 0; k < algoList.size(); k++) {
			AlgoElement algo = algoList.get(k);
			for (int j = 0; j < input.length; j++)
				input[j].removeFromUpdateSets(algo);
		}

		// remove old point
		oldPoint.setParentAlgorithm(null);

		// remove dependent segment algorithm that are part of this polygon
		// to make sure we don't remove the polygon as well
		GeoPolyhedron poly = getPolyhedron();
		for (int k = 0; k < algoList.size(); k++) {
			AlgoElement algo = algoList.get(k);
			// make sure we don't remove the polygon as well
			if (algo instanceof AlgoJoinPoints3D
					&& ((AlgoJoinPoints3D) algo).getPoly() == poly) {
				continue;
			}else if(algo instanceof AlgoPolygon3D
					&& ((AlgoPolygon3D) algo).getPolyhedron() == poly) {
				continue;
			}
			algo.remove();

		}

		algoList.clear();
		// remove point
		oldPoint.doRemove();

	}
	
	
}
