package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;


/**
 * Algo for cylinder between two end points and given radius.
 * @author mathieu
 *
 */
public abstract class AlgoQuadricLimitedPointPointRadius extends AlgoElement3D {

	//input
	private GeoPointND origin, secondPoint;
	private NumberValue radius;
	
	//output
	private GeoQuadric3DPart side;
	protected GeoConic3D bottom;
	protected GeoConic3D top;
	private GeoQuadric3DLimited quadric;
	

	/**
	 * 
	 * @param c
	 * @param label
	 * @param origin
	 * @param secondPoint
	 * @param r
	 */
	public AlgoQuadricLimitedPointPointRadius(Construction c, String[] labels, GeoPointND origin, GeoPointND secondPoint, NumberValue r, int type) {
		super(c);
		
		this.origin=origin;
		this.secondPoint=secondPoint;
		this.radius=r;
		
		quadric=new GeoQuadric3DLimited(c,origin,secondPoint);
		quadric.setType(type);

		input = new GeoElement[] {(GeoElement) origin,(GeoElement) secondPoint,(GeoElement) r};

		
		((GeoElement) origin).addAlgorithm(this);
		((GeoElement) secondPoint).addAlgorithm(this);
		((GeoElement) r).addAlgorithm(this);
		
		
		
    	// parent of output
        quadric.setParentAlgorithm(this);       
        cons.addToAlgorithmList(this); 
        
		compute();
        
 		AlgoQuadricSide algo = new AlgoQuadricSide(cons, quadric,true);            
		cons.removeFromConstructionList(algo);
		side = (GeoQuadric3DPart) algo.getQuadric();
		
		createEnds();

		quadric.setParts(side,bottom,top);

		//output = new GeoElement[] {quadric,bottom,top,side};
		setOutput();
	

		
		quadric.initLabels(labels);
		quadric.updatePartsVisualStyle();
		
				
	}
	
	/**
	 * sets the output
	 */
	abstract protected void setOutput();
	
	abstract protected void createEnds();
	
	protected void compute() {
		
		//check end points
		if (!((GeoElement) origin).isDefined() || origin.isInfinite()
				||	!((GeoElement) secondPoint).isDefined() || secondPoint.isInfinite()
		){
			getQuadric().setUndefined();
			return;
		}
		
		Coords o = origin.getInhomCoordsInD(3);
		Coords o2 = secondPoint.getInhomCoordsInD(3);
		Coords d = o2.sub(o);
		
		if (d.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			getQuadric().setUndefined();
			return;
		}
		
		double r = radius.getDouble();
		
		d.calcNorm();
		double altitude = d.getNorm();
		
		quadric.setDefined();
		
		setQuadric(o,o2,d.mul(1/altitude),r, 0, altitude);

		quadric.calcVolume();

	}

	abstract protected void setQuadric(Coords o1, Coords o2, Coords d, double r, double min, double max);

	
	public GeoQuadric3DLimited getQuadric(){
		return quadric;
	}
	
	//compute and update quadric (for helper algos)
	public void update() {
        compute();
        quadric.update();
    }
    

	

}
