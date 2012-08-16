package geogebra3D.kernel3D;


import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordMatrixUtil;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

import java.util.TreeMap;

public class AlgoIntersectLinePolygon3D extends AlgoElement3D {

	protected int spaceDim = 3;
	protected GeoPolygon p;
	protected GeoLineND g;

	protected OutputHandler<GeoElement> outputPoints; // output
	
    
    private TreeMap<Double, Coords> newCoords;
    


	
	/**
	 * @param c 
	 * @param labels 
	 * @param g 
	 * @param p 
	 */
	AlgoIntersectLinePolygon3D(Construction c, String[] labels,
			GeoPolygon p, GeoLineND g) {
		this(c, labels, g, p);
	}
	
	
    public AlgoIntersectLinePolygon3D(Construction c, String[] labels,
			GeoLineND g, GeoPolygon p) {
    	super(c);
    	
        
		outputPoints=createOutputPoints();
        
        this.g = g;
        this.p = p;

        newCoords = new TreeMap<Double, Coords>(Kernel.DoubleComparator(Kernel.STANDARD_PRECISION));
  
        
        compute();
        
        setInputOutput(); // for AlgoElement
        
        setLabels(labels);
        update();    
    	this.p=p;
    	this.g=g;
    	
	}


	protected OutputHandler<GeoElement> createOutputPoints(){
    	return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoPoint3D newElement() {
				GeoPoint3D p=new GeoPoint3D(cons);
				p.setCoords(0, 0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectLinePolygon3D.this);
				return p;
			}
		});
    }
	
	   protected OutputHandler<GeoElement> createOutputSegments(){
	    	return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
				public GeoSegment3D newElement() {
				
					GeoPoint3D aS = new GeoPoint3D(cons);
					aS.setCoords(0, 0, 0, 1);
					GeoPoint3D aE = new GeoPoint3D(cons);
					aE.setCoords(0, 0, 0, 1);
					GeoSegment3D a=new GeoSegment3D(cons, aS, aE);
					a.setParentAlgorithm(AlgoIntersectLinePolygon3D.this);
					return a;
				}
			});
	    }
    
    protected void intersectionsCoords(GeoLineND g, GeoPolygon p, TreeMap<Double, Coords> newCoords){

    	//TODO: move these to intersectLinePolyline3D
    	//line origin, direction, min and max parameter values
    	Coords o1 = g.getPointInD(3, 0);
    	Coords d1 = g.getPointInD(3, 1).sub(o1);
    	double min = g.getMinParameter();
    	double max = g.getMaxParameter();
    	
    	for(int i=0; i<p.getSegments().length; i++){
    		GeoSegmentND seg = p.getSegments()[i];
    		
    		Coords o2 = seg.getPointInD(3, 0);
           	Coords d2 = seg.getPointInD(3, 1).sub(o2);

           	Coords[] project = CoordMatrixUtil.nearestPointsFromTwoLines(
           			o1,d1,o2,d2
           	);

           	//check if projection is intersection point
           	if (project!=null && project[0].equalsForKernel(project[1], Kernel.STANDARD_PRECISION)){
           	
           		double t1 = project[2].get(1); //parameter on line
           		double t2 = project[2].get(2); //parameter on segment


           		if (t1>=min && t1<=max //TODO optimize that
           				&& t2>=0 && t2<=1)
           			newCoords.put(t1, project[0]);

           	}
        }
        
    }
  

    @Override
	public void compute() {
    	
    	//clear the points map
    	newCoords.clear();
    	
    	//fill a new points map
    	intersectionsCoords(g, p, newCoords);
    	
    	//update and/or create points
    	int index = 0;   	
    	//affect new computed points
    	outputPoints.adjustOutputSize(newCoords.size());
    	for (Coords coords : newCoords.values()){
    		GeoPointND point = (GeoPointND) outputPoints.getElement(index);
    		point.setCoords(coords,false);
    		point.updateCoords();
    		index++;
    	}
    	//other points are undefined
    	for(;index<outputPoints.size();index++)
    		outputPoints.getElement(index).setUndefined();

    }
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoIntersectLinePolygon3D;
	}
	
	protected void setLabels(String[] labels) {
       //if only one label (e.g. "A") for more than one output, new labels will be A_1, A_2, ...
			if (labels!=null &&
					labels.length==1 &&
					outputPoints.size() > 1 &&
					labels[0]!=null &&
					!labels[0].equals("")) {
				outputPoints.setIndexLabels(labels[0]);
          	
 
        	} else {
         		outputPoints.setLabels(labels);
        	}
		
	}
	
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) g;
        input[1] = p;
        
        setDependencies(); // done by AlgoElement
    }

	// TODO Consider locusequability


}
