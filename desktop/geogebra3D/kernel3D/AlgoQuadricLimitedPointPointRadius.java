package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Transform;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoRadius;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algo for cylinder between two end points and given radius.
 * @author mathieu
 *
 */
public abstract class AlgoQuadricLimitedPointPointRadius extends AlgoElement3D {//implements AlgoTransformable {

	//input
	private GeoPointND origin, secondPoint;
	private NumberValue radius;
	
	//output
	private GeoQuadric3DPart side;
	protected GeoConic3D bottom;
	protected GeoConic3D top;
	private GeoQuadric3DLimited quadric;
	
	public AlgoQuadricLimitedPointPointRadius(Construction c, String[] labels, GeoPointND origin, GeoPointND secondPoint, NumberValue r, int type) {
		super(c);
		
		this.origin=origin;
		this.secondPoint=secondPoint;
		this.radius=r;
		
		quadric=new GeoQuadric3DLimited(c);//,origin,secondPoint);
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
		
		quadric.initLabelsIncludingBottom(labels);
		quadric.updatePartsVisualStyle();			
	}
	
	/**
	 * sets the output
	 */
	abstract protected void setOutput();
	
	abstract protected void createEnds();
	
	@Override
	public void compute() {
		
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
	
	

	////////////////////////
	// ALGOTRANSFORMABLE
	////////////////////////
	
	/**
	 * 
	 * @param labels transformed labels
	 * @param p1 transformed first point
	 * @param p2 transformed second point
	 * @param r transformed radius
	 * @return new algo for transformed inputs
	 */
	protected abstract AlgoElement getTransformedAlgo(String[] labels, GeoPointND p1, GeoPointND p2, GeoNumeric r);

	
	public GeoElement[] getTransformedOutput(Transform t){
		
		GeoPointND p1 = (GeoPointND) t.transform((GeoElement) origin, Transform.transformedGeoLabel((GeoElement) origin))[0];
		GeoPointND p2 = (GeoPointND) t.transform((GeoElement) secondPoint, Transform.transformedGeoLabel((GeoElement) secondPoint))[0];
		Transform.setVisualStyleForTransformations((GeoElement) origin, (GeoElement) p1);
		Transform.setVisualStyleForTransformations((GeoElement) secondPoint, (GeoElement) p2);
		
		GeoNumeric r = (new AlgoRadius(this.cons, null, getQuadric().getBottom())).getRadius();
		r.setAuxiliaryObject(true);
		
		GeoElement[] output = getOutput();
		String[] labels = new String[output.length];
		for (int i = 0; i < output.length; i++){
			labels[i] = Transform.transformedGeoLabel(output[i]);
		}
		
		AlgoElement algo = getTransformedAlgo(labels, p1, p2, r);
	
		GeoElement[] ret = algo.getOutput();
		for (int i = 0; i < ret.length; i++){
			Transform.setVisualStyleForTransformations(output[i], ret[i]);
		}
		
		algo.update();
	
		
		return ret;
	}
	

	@Override
	public void update() {
		
		if (stopUpdateCascade) {
			return;
		}
		
		
        compute();
        quadric.update();
		
		if(!getQuadric().isLabelSet()){ // geo is in sequence/list : update bottom, top and side
			getQuadric().getBottom().getParentAlgorithm().update();
			getQuadric().getTop().getParentAlgorithm().update();
			getQuadric().getSide().getParentAlgorithm().update();
		}
		
	}

}
