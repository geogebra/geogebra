package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

/**
 * @author ggb3D
 *
 */
public class AlgoFunctionNVarND extends AlgoElement3D {

	
	/** input function */
	protected GeoFunctionNVar inputFunction; 

	/** output function */
	protected GeoFunctionNVar function; 
	
	//private NumberValue[] coords; // input : expression for each coord x, y, z, ...
	/** input : "from" values for each var */
	protected NumberValue[] from;  
	/** input : "to" values for each var */
	protected NumberValue[] to;
    //private GeoNumeric[] localVar;     // input : variables u, v, ...
	
	
	private AlgoFunctionNVarND(Construction cons,NumberValue[] from, NumberValue[] to){
		super(cons);
		
		//this.coords = coords;
    	this.from = from;
    	this.to = to;
    	//this.localVar = localVar;
	}
			
	/**
	 * Construct a function
	 * 
	 * @param cons 
	 * @param label 
	 * @param coords description of the function
	 * @param localVar var of the function
	 * @param from "from" values for each var
	 * @param to "to" values for each var
	 * 
	 */
	public AlgoFunctionNVarND(Construction cons, String label, 
			NumberValue[] coords,  
			GeoNumeric[] localVar, NumberValue[] from, NumberValue[] to)  {


		this(cons,from,to);
    	
    	// we need to create Function objects for the coord NumberValues,
    	// so let's get the expressions of xcoord and ycoord and replace
    	// the localVar by a functionVar
    	FunctionVariable[] funVar = new FunctionVariable[localVar.length];
    	for (int i=0;i<localVar.length; i++){
    		funVar[i] = new FunctionVariable(kernel);
    		funVar[i].setVarString(localVar[i].getLabel());
    	}
		
		ExpressionNode[] exp = new ExpressionNode[coords.length];
		FunctionNVar[] fun = new FunctionNVar[coords.length];

		for (int i=0;i<coords.length;i++){
			exp[i]= kernel.convertNumberValueToExpressionNode(coords[i]);
			for (int j=0;j<localVar.length; j++)
				exp[i].replaceAndWrap(localVar[j], funVar[j]);
			fun[i] = new FunctionNVar(exp[i], funVar);
		}
        
		// create the function
		function = new GeoFunctionNVar(cons, fun[0]);
		
		//end of construction
		setInputOutput(coords,localVar);
		
		compute();
		function.setLabel(label);
	}
	
	
	/**
	 * Construct a function
	 * 
	 * @param cons 
	 * @param label 
	 * @param f (x,y) function
	 * @param from "from" values for each var
	 * @param to "to" values for each var
	 * 
	 */
	public AlgoFunctionNVarND(Construction cons, String label, 
			GeoFunctionNVar f,  
			NumberValue[] from, NumberValue[] to)  {


		this(cons,from,to);
 		
		inputFunction = f;
		//function = new GeoFunctionNVar(cons, inputFunction.getFunction());//(GeoFunctionNVar) inputFunction.copy();
		function = (GeoFunctionNVar) inputFunction.copy();
		
		
		
		//end of construction
		setInputOutput((NumberValue[]) null,(GeoNumeric[]) null);
		
		compute();
		function.setLabel(label);
		
	}
		
	private void setInputOutput(NumberValue[] coords, GeoNumeric[] localVar){
       
		int inputLength = from.length+to.length;
		if (coords!=null)
			inputLength+=coords.length;
		else
			inputLength+=1; //for the function
		if (localVar!=null)
			inputLength+=localVar.length;
		GeoElement[] input = new GeoElement[inputLength];
		
		int index = 0;
		
		if(coords!=null)
			for (int i=0;i<coords.length;i++){
				input[index]=(GeoElement) coords[i];
				index++;
			}
		else{
			input[index]=inputFunction;
			index++;
		}
		
		for (int i=0;i<from.length;i++){
			if (localVar!=null){
				input[index]=(GeoElement) localVar[i];
				index++;
			}
			input[index]=(GeoElement) from[i];
			index++;
			input[index]=(GeoElement) to[i];
			index++;		
		}
		
		
		super.setInputOutput(
				input, 
				new GeoElement[] {function});
		
		   
		
	}

	/**
	 * @return the function
	 */
	public GeoFunctionNVar getFunction(){
		return function;
	}
	
	

	protected void compute() {

		
		if (inputFunction!=null)
			function.set(inputFunction);
			
		
		function.setInterval(
				getDouble(from), 
				getDouble(to)			
		);
		
		//Application.debug(function.getFunction().evaluate(new double[] {1,1}));
		
	}
	
	private double[] getDouble(NumberValue[] values){
		double[] ret = new double[values.length];
		for (int i=0; i<values.length; i++){
			ret[i]=values[i].getDouble();
			//Application.debug("ret["+i+"]="+ret[i]);
		}
		return ret;
	}

	public String getClassName() {
		
		return "AlgoFunctionInterval";
	}
	
	

}
