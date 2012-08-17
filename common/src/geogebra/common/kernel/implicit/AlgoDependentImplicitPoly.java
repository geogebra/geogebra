package geogebra.common.kernel.implicit;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.Polynomial;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.main.MyError;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Dependent implicit polynomial (or line / conic)
 */
public class AlgoDependentImplicitPoly extends AlgoElement {


	private Equation equation;
	private ExpressionValue[][] coeff;  // input
	private GeoElement geoElement;     // output (will be a implicitPoly, line or conic)
//	private FunctionNVar[] dependentFromFunctions;
	private Set<FunctionNVar> dependentFromFunctions;
    
	/**
	 * Creates new implicit polynomial from equation. This  algo may also return line or conic.
	 * @param c construction
	 * @param label label
	 * @param equ equation
	 */
	public AlgoDependentImplicitPoly(Construction c,String label, Equation equ) {
		super(c, false);
		equation=equ;
		Polynomial lhs = equ.getNormalForm();
		coeff=lhs.getCoeff();
		for (int i=0;i<coeff.length;i++){
			for (int j=0;j<coeff[i].length;j++){
				if (coeff[i][j]!=null){
					// find constant parts of input and evaluate them right now
	    			if (coeff[i][j].isConstant()){
	    				coeff[i][j]=coeff[i][j].evaluate(StringTemplate.defaultTemplate);
	    			}
	    			
	    			// check that coefficient is a number: this may throw an exception
	                ExpressionValue eval = coeff[i][j].evaluate(StringTemplate.defaultTemplate);
	                ((NumberValue) eval).getDouble(); 
				}
    		}
    	}
    	c.addToConstructionList(this, false);
    	if (equ.isForcedLine()){
    		geoElement=new GeoLine(c);
    	}else if (equ.isForcedConic()){
    		geoElement=new GeoConic(c);
    	}else if (equ.isForcedImplicitPoly()){
    		geoElement=new GeoImplicitPoly(c);
    	}else{
	    	switch (equ.degree()) {
				// linear equation -> LINE   
				case 1 :
					geoElement=new GeoLine(c);
					break;
				// quadratic equation -> CONIC                                  
				case 2 :
					geoElement=new GeoConic(c);
					break;
				default :
					geoElement=new GeoImplicitPoly(c);
	    	}
    	}
    	setInputOutput(); // for AlgoElement    
    	
    	compute(true); 

    	geoElement.setLabel(label);
    }
	
	@Override
	public void compute(){
		compute(false);
	}
	
	/**
	 * Replace output element with new one; needed if changes e.g. from line to conic
	 * @param newElem replacement element
	 */
	protected void replaceGeoElement(GeoElement newElem){
		String label=geoElement.getLabel(StringTemplate.defaultTemplate);
		geoElement.doRemove();
		geoElement=newElem;
		setInputOutput();
		geoElement.setLabel(label);
	}
	
	/**
	 * @return equation
	 */
	public Equation getEquation(){
		return equation;
	}

	private void compute(boolean first) {
		if (!first){
			try{
				if (equation.isFunctionDependent()){
//					boolean functionChanged=false;
					Set<FunctionNVar> functions=new HashSet<FunctionNVar>();
					addAllFunctionalDescendents(this, functions, new TreeSet<AlgoElement>());
					
//					for (int i=0;i<dependentFromFunctions.length;i++){
//						if (dependentFromFunctions[i]!=null){
//							if (!(input[i] instanceof FunctionalNVar)){
//								functionChanged=true;
//								break;
//							}else{
//								if (((FunctionalNVar)input[i]).getFunction()!=dependentFromFunctions[i]){
//									functionChanged=true;
//									break;
//								}
//							}
//						}
//					}
					if (!functions.equals(dependentFromFunctions)){
						equation.initEquation();
						coeff=equation.getNormalForm().getCoeff();
						dependentFromFunctions=functions;
					}
				}
			}catch(MyError e){
				geoElement.setUndefined();
				return;
			}
		}
		//use the forced behavior here
		int degree = equation.isForcedImplicitPoly()? 3 : 
			(equation.isForcedConic() ? 2 :equation.degree()); 
		switch (degree) {
			// linear equation -> LINE   
			case 1 :
				if (geoElement instanceof GeoLine){
					setLine();
				}else{
					if (geoElement.hasChildren())
						geoElement.setUndefined();
					else{
						replaceGeoElement(new GeoLine(getConstruction()));
						setLine();
					}
				}
				break;
			// quadratic equation -> CONIC                                  
			case 2 :
				if (geoElement instanceof GeoConic){
					setConic();
				}else{
					if (geoElement.hasChildren())
						geoElement.setUndefined();
					else{
						replaceGeoElement(new GeoConic(getConstruction()));
						setConic();
					}
				}
				break;
			default :
				if (geoElement instanceof GeoImplicitPoly){
					((GeoImplicitPoly)geoElement).setDefined();
					((GeoImplicitPoly)geoElement).setCoeff(coeff);
				}else{
					if (geoElement.hasChildren())
						geoElement.setUndefined();
					else{
						replaceGeoElement(new GeoImplicitPoly(getConstruction()));
						((GeoImplicitPoly)geoElement).setDefined();
						((GeoImplicitPoly)geoElement).setCoeff(coeff);
					}
				}
		}
	}

	private void setLine(){
		ExpressionValue[] expr=new ExpressionValue[3];
		expr[2]=expr[1]=expr[0]=null;
		if (coeff.length>0){
			if (coeff[0].length>0){
				expr[2]=coeff[0][0];
				if (coeff[0].length>1){
					expr[1]=coeff[0][1];
				}
			}
			if (coeff.length>1){
				if (coeff[1].length>0){
					expr[0]=coeff[1][0];
				}
			}
		}
		double[] dCoeff=new double[expr.length];
		for (int i=0;i<expr.length;i++){
			if (expr[i]!=null){
				dCoeff[i]=expr[i].evaluateNum().getDouble();
			}else{
				dCoeff[i]=0;
			}
		}
		((GeoLine)geoElement).setCoords(dCoeff[0], dCoeff[1], dCoeff[2]);
	}
	
	private void setConic(){
		ExpressionValue[] expr=new ExpressionValue[6];
		for (int i=0;i<6;i++){
			expr[i]=null;
		}
		if (coeff.length>0){
			if (coeff[0].length>0){
				expr[5]=coeff[0][0];
				if (coeff[0].length>1){
					expr[4]=coeff[0][1];
					if (coeff[0].length>2){
						expr[2]=coeff[0][2];
					}
				}
			}
			if (coeff.length>1){
				if (coeff[1].length>0){
					expr[3]=coeff[1][0];
					if (coeff[1].length>1){
						expr[1]=coeff[1][1];
					}
				}
				if (coeff.length>2){
					if (coeff[2].length>0){
						expr[0]=coeff[2][0];
					}
				}
			}
		}
		double[] dCoeff=new double[expr.length];
		for (int i=0;i<expr.length;i++){
			if (expr[i]!=null){
				dCoeff[i]=expr[i].evaluateNum().getDouble();
			}else{
				dCoeff[i]=0;
			}
		}
		((GeoConic)geoElement).setDefined();
		((GeoConic)geoElement).setCoeffs(dCoeff);
	}
	
	/**
	 * Adds all functions from inputs of algo and its ancestors to destination set
	 * @param algo algo whose input functions need adding
	 * @param set destination set
	 * @param algos set of algorithms that were already processed
	 */
	protected void addAllFunctionalDescendents(AlgoElement algo,Set<FunctionNVar> set,Set<AlgoElement> algos){
		GeoElement[] in=algo.getInput();
		for (int i=0;i<in.length;i++){
			AlgoElement p=in[i].getParentAlgorithm();
			if (p!=null && !algos.contains(p)){
				algos.add(p);
				addAllFunctionalDescendents(p, set, algos);
			}
			if (in[i] instanceof FunctionalNVar){
				set.add(((FunctionalNVar)in[i]).getFunction());
			}
		}
	}

	@Override
	protected void setInputOutput() {
		if (input==null){
			input = equation.getGeoElementVariables();
			dependentFromFunctions=new HashSet<FunctionNVar>();
			addAllFunctionalDescendents(this, dependentFromFunctions, new TreeSet<AlgoElement>());
		}
		if (getOutputLength()==0)
			setOutputLength(1);        
        setOutput(0,geoElement);        
        setDependencies(); // done by AlgoElement
	}

	@Override
    public Algos getClassName() {
		return Algos.AlgoDependentImplicitPoly;
	}
	
	/**
	 * @return resulting poly, conic or line
	 */
	public GeoElement getGeo(){
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
	public final String toRealString(StringTemplate tpl) {
        return equation.toRealString(tpl);
    }

	@Override
	protected String toExpString(StringTemplate tpl) {
		return geoElement.getLabel(tpl)+": "+equation.toString(tpl);
	}

	// TODO Consider locusequability
	

}
