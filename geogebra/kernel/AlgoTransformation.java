package geogebra.kernel;

import geogebra.main.Application;

/**
 * Algorithms for transformations 
 */
public abstract class AlgoTransformation extends AlgoElement implements EuclidianViewCE{

	/**
	 * Create new transformation algo
	 * @param c
	 */
	public AlgoTransformation(Construction c) {
        super(c);  	
	}		
	
	/**
     * Returns the resulting GeoElement
     * @return the resulting GeoElement
     */
	abstract GeoElement getResult();
	
	abstract protected void setTransformedObject(GeoElement g,GeoElement g2);

	protected void transformList(GeoList ageo2, GeoList bgeo2) {     
		for(int i = bgeo2.size()-1; i>=ageo2.size();i--)
			bgeo2.remove(i);

		for(int i = 0; i<ageo2.size();i++){
			GeoElement trans = null;
			if(i<bgeo2.size()){
				setTransformedObject(ageo2.get(i),bgeo2.get(i));
				compute();    
			}
			else{
				trans = getResultTemplate(ageo2.get(i));

				setTransformedObject(ageo2.get(i),trans);
				compute();
				bgeo2.add(trans);
			}
		}  
		setTransformedObject(ageo2,bgeo2);
	}

	protected GeoElement getResultTemplate(GeoElement geo) {		
		if(geo instanceof GeoPolyLineInterface || geo.isLimitedPath())
			return geo.copyInternal(cons);
		if(geo.isGeoList())        	
        	return new GeoList(cons);
		return geo.copy();		
	}

	protected void transformLimitedPath(GeoElement a,GeoElement b){
		
		if(a instanceof GeoRay){
			setTransformedObject(
					((GeoRay)a).getStartPoint(),
					((GeoRay)b).getStartPoint());
			compute();
			setTransformedObject(a,b);
		}
		else if(a instanceof GeoSegment){
			setTransformedObject(
					((GeoSegment)a).getStartPoint(),
					((GeoSegment)b).getStartPoint());
			compute();
			setTransformedObject(
					((GeoSegment)a).getEndPoint(),
					((GeoSegment)b).getEndPoint());
			compute();
			setTransformedObject(a,b);
		}
		if(a instanceof GeoConicPart){
			double p = ((GeoConicPart)a).getParameterStart();
			double q = ((GeoConicPart)a).getParameterEnd();
			//Application.debug(p+","+q);
			((GeoConicPart)b).setParameters(p, q, 
					swapOrientation(((GeoConicPart)a).positiveOrientation()));
		}
	}
	protected boolean swapOrientation(boolean positiveOrientation) {
		//Application.debug(positiveOrientation);
		return positiveOrientation;
	}

	
	
	private AlgoClosestPoint pt;
    private GeoPoint transformedPoint;
    
    protected void transformLimitedConic(GeoElement a, GeoElement b){
    	
    GeoConicPart arc = (GeoConicPart)b;
    if(a instanceof GeoConicPart){
    	((GeoConicPart)b).setParameters(0, Kernel.PI_2, true);
		if(pt==null){
			transformedPoint = new GeoPoint(cons);
			pt = new AlgoClosestPoint(cons,arc,transformedPoint);
			cons.removeFromConstructionList(pt);
		}
		transformedPoint.removePath();
		setTransformedObject(
				((GeoConicPart)a).getPointParam(0),transformedPoint
		);
		compute();								
		transformedPoint.updateCascade();
		//Application.debug("start"+transformedPoint);
		double d = pt.getP().getPathParameter().getT();
		transformedPoint.removePath();
		setTransformedObject(
				((GeoConicPart)a).getPointParam(1),transformedPoint
		);
		compute();
		transformedPoint.updateCascade();
		//Application.debug("end"+transformedPoint);			
		double e = pt.getP().getPathParameter().getT();
		//Application.debug(d+","+e);
		arc.setParameters(d*Kernel.PI_2,e*Kernel.PI_2,swapOrientation(((GeoConicPart)a).positiveOrientation()));
		
		setTransformedObject(a,b);
	}
   }
}
