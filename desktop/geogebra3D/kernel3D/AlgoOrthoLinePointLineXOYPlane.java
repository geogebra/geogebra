package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoOrthoLinePointLine;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;


/**
 * Extension of AlgoOrthoLinePointLine to say we're in xOyPlane (or parallel to)
 * @author mathieu
 *
 */
public class AlgoOrthoLinePointLineXOYPlane extends AlgoOrthoLinePointLine {

	/**
	 * constructor
	 * @param cons cons
	 * @param label label
	 * @param P point
	 * @param l line
	 */
	public AlgoOrthoLinePointLineXOYPlane(Construction cons, String label,
			GeoPoint P, GeoLine l) {
		super(cons, label, P, l);
	}
	
    @Override
	protected void setInput(){
    	input = new GeoElement[3];
    	input[0] = P;
    	input[1] = l;
    	input[2] = ((Construction3D) cons).getXOYPlane();
    }

    @Override
	public String toString(StringTemplate tpl) {
        return loc.getPlain("LineThroughAPerpendicularToBInXOYPlane",P.getLabel(tpl),l.getLabel(tpl));

    }
    

	
    
    @Override
    protected int getInputLengthForXML(){
    	
    	if(kernel.isSaving()){
    		return 2; // if saving mode, ignore "xOyPlane" input to ensure ggb2D compatibility
    	}
		
		return super.getInputLengthForXML();
	}

}
