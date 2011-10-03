package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

/**
 * Class for part of a quadric (e.g. side of a limited cone, cylinder, ...)
 * @author mathieu
 *
 */
public class GeoQuadric3DPart extends GeoQuadric3D implements NumberValue{
	
	/** min value for limites */
	private double min;
	/** max value for limites */
	private double max;

	/**
	 * constructor
	 * @param c
	 */
	public GeoQuadric3DPart(Construction c) {
		super(c);
	}

	public GeoQuadric3DPart(GeoQuadric3DPart quadric){
		super(quadric);		
	}

	public void set(GeoElement geo) {
		super.set(geo);
		GeoQuadric3DPart quadric = (GeoQuadric3DPart) geo;
		setLimits(quadric.min, quadric.max);
		area=quadric.getArea();
	}

	
	/**
	 * sets the min and max values for limits
	 * @param min
	 * @param max
	 */
	public void setLimits(double min, double max){
		this.min = min;
		this.max = max;
	}
	
	
	public double getMinParameter(int index) {

		if (index==1)
			return min;
		else
			return super.getMinParameter(index);
	}
	
	

	public double getMaxParameter(int index) {
		if (index==1)
			return max;
		else
			return super.getMaxParameter(index);
	}
	

	public void set(Coords origin, Coords direction, double r){
		switch(type){
		case QUADRIC_CYLINDER:
			setCylinder(origin, direction, r);
			break;

		case QUADRIC_CONE:
			setCone(origin, direction, r);
			break;
		}
	}
	
	
    public int getGeoClassType() {

        return GeoElement3D.GEO_CLASS_QUADRIC_PART;

    }

    public String toValueString() {
		switch(type){
		case QUADRIC_CYLINDER:
			return kernel.format(area);
		
		}
		
		return "todo-GeoQuadric3DPart";
		
	}
	
	protected StringBuilder buildValueString() {
		return new StringBuilder(toValueString());
	}
	
	
	
    public GeoElement copy() {

        return new GeoQuadric3DPart(this);

    }
    
    //////////////////////////
    // REGION
    //////////////////////////
    
	protected Coords getNormalProjectionParameters(Coords coords){
		
		Coords parameters = super.getNormalProjectionParameters(coords);

		if (parameters.getY()<getMinParameter(1))
			parameters.setY(getMinParameter(1));
		else if (parameters.getY()>getMaxParameter(1))
			parameters.setY(getMaxParameter(1));
		
		return parameters;
		
	}



    //////////////////////////
    // AREA
    //////////////////////////
    
    private double area;

    public void calcArea(){


    	//Application.debug("geo="+getLabel()+", half="+getHalfAxis(0)+", min="+min+", max="+max+", type="+type);
    	
    	switch(type){
    	case QUADRIC_CYLINDER:
    		area=2*getHalfAxis(0)*Math.PI*(max-min);
    		break;
    	}
    }

    public double getArea(){
    	if (defined)
    		return area;				        
    	else 
    		return Double.NaN;			        	
    }
    
    
	//////////////////////////////////
	// NumberValue
	//////////////////////////////////


	public MyDouble getNumber() {
		return new MyDouble(kernel,  getDouble() );
	}


	public double getDouble() {		
		return getArea();
	}
	
	public boolean isNumberValue() {
		return true;
	}


}
