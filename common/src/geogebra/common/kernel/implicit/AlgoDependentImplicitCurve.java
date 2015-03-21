package geogebra.common.kernel.implicit;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.Equation;

/**
 * Dependent implicit polynomial (or line / conic)
 */
public class AlgoDependentImplicitCurve extends AlgoElement {


	private Equation equation;
	private GeoImplicitCurve geoElement;     // output (will be a implicitPoly, line or conic)
//	private FunctionNVar[] dependentFromFunctions;
    
	/**
	 * Creates new implicit polynomial from equation. This  algo may also return line or conic.
	 * @param c construction
	 * @param label label
	 * @param equ equation
	 */
	public AlgoDependentImplicitCurve(Construction c,String label, Equation equ, boolean simplify) {
		super(c, false);
		c.addToConstructionList(this, false);
		this.geoElement = new GeoImplicitCurve(c, equ);
		this.equation = equ;
    	setInputOutput(); // for AlgoElement    
    	
    	compute(true); 

    	geoElement.setLabel(label);
    }
	
	@Override
	public void compute(){
		compute(false);
	}
	

	/**
	 * @return equation
	 */
	public Equation getEquation(){
		return equation;
	}

	private void compute(boolean first) {
		geoElement.updatePath();
	}

	
	


	@Override
	protected void setInputOutput() {
		if (input==null){
			input = equation.getGeoElementVariables();
		}
		if (getOutputLength()==0)
			setOutputLength(1);        
        setOutput(0,geoElement);        
        setDependencies(); // done by AlgoElement
	}

	@Override
    public Algos getClassName() {
		return Algos.Expression;
	}
	
	/**
	 * @return resulting poly, conic or line
	 */
	public GeoImplicitCurve getGeo() {
		return geoElement;
//		if (type==GeoElement.GEO_CLASS_IMPLICIT_POLY)
//			return (GeoImplicitPoly)geoElement;
//		else
//			return null;
	}
	
	@Override
	public final String toString(StringTemplate tpl) {
        return equation.toString(tpl);
    }

	@Override
	protected String toExpString(StringTemplate tpl) {
		return geoElement.getLabel(tpl)+": "+equation.toString(tpl);
	}

	// TODO Consider locusequability
	

}
