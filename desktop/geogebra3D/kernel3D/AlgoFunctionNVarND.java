package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoNumeric;


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
	 * @param cons construction
	 * @param label label
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
    		funVar[i].setVarString(localVar[i].getLabel(StringTemplate.defaultTemplate));
    	}
		
		ExpressionNode[] exp = new ExpressionNode[coords.length];
		FunctionNVar[] fun = new FunctionNVar[coords.length];

		for (int i=0;i<coords.length;i++){
			exp[i]= kernel.convertNumberValueToExpressionNode(coords[i]);
			for (int j=0;j<localVar.length; j++)
				exp[i]=exp[i].replace(localVar[j], funVar[j]).wrap();
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
	 * @param cons construction
	 * @param label label
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
		GeoElement[] inputElements = new GeoElement[inputLength];
		
		int index = 0;
		
		if(coords!=null)
			for (int i=0;i<coords.length;i++){
				inputElements[index]=(GeoElement) coords[i];
				index++;
			}
		else{
			inputElements[index]=inputFunction;
			index++;
		}
		
		for (int i=0;i<from.length;i++){
			if (localVar!=null){
				inputElements[index] = localVar[i];
				index++;
			}
			inputElements[index]=(GeoElement) from[i];
			index++;
			inputElements[index]=(GeoElement) to[i];
			index++;		
		}
		
		
		super.setInputOutput(
				inputElements, 
				new GeoElement[] {function});
		
		   
		
	}

	/**
	 * @return the function
	 */
	public GeoFunctionNVar getFunction(){
		return function;
	}
	
	

	@Override
	public void compute() {

		
		if (inputFunction!=null)
			function.set(inputFunction);
			
		
		function.setInterval(
				getDouble(from), 
				getDouble(to)			
		);
		
		//Application.debug(function.getFunction().evaluate(new double[] {1,1}));
		
	}
	
	private static double[] getDouble(NumberValue[] values){
		double[] ret = new double[values.length];
		for (int i=0; i<values.length; i++){
			ret[i]=values[i].getDouble();
			//Application.debug("ret["+i+"]="+ret[i]);
		}
		return ret;
	}

	@Override
	public Commands getClassName() {
		return Commands.Function;
	}

	// TODO Consider locusequability
	
	

}
