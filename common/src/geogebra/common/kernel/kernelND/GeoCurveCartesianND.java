package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.DistanceFunction;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.VarString;
import geogebra.common.kernel.algos.AlgoDependentFunction;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.Traceable;

/**
 * Abstract class for cartesian curves in any dimension
 * @author matthieu
 *
 */
public abstract class GeoCurveCartesianND extends GeoElement implements Traceable, Path, VarString, CasEvaluableFunction {
	
	/** samples to find interval with closest parameter position to given point */
	protected static final int CLOSEST_PARAMETER_SAMPLES = 100;

	
	/** coordinates  functions */
	protected final Function[] fun;
	protected final Function[] funExpanded;
	protected final boolean[] containsFunctions;
	/** derivative  functions */
	protected Function[] funD1;
	/** second derivative  functions */
	protected Function[] funD2;
	/** start parameter */
	protected double startParam;
	/**end parameter*/
	protected double endParam;
	
	/** flag for isDefined()*/
	protected boolean isDefined = true;
	protected DistanceFunction distFun;

	/** common constructor
	 * @param c construction
	 */
	public GeoCurveCartesianND(Construction c, int dimension) {
		super(c);
		this.fun = new Function[dimension];
		this.funExpanded = new Function[dimension];
		this.containsFunctions = new boolean[dimension];
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

	}
	
	/** constructor with functions
	 * @param c construction
	 * @param fun functions of parameter
	 */
	public GeoCurveCartesianND(Construction c, Function[] fun) {
		super(c);
		
		this.fun = fun;
		this.funExpanded = new Function[fun.length];
		this.containsFunctions = new boolean[fun.length];
		setConstructionDefaults();
	}	
	
	/**
	 * set functions
	 * @param fun functions
	 */
	public void setFun(Function[] fun){
		for(int i=0; i<fun.length;i++){
			this.fun[i] = fun[i];
		}
	}

	@Override
	public boolean isGeoCurveCartesian() {
		return true;
	}	
	
	/**
     * Replaces geo and all its dependent geos in this function's
     * expression by copies of their values.
     * @param geo Element to be replaced
     */
    public void replaceChildrenByValues(GeoElement geo) {

    	for (int i=0; i<fun.length; i++)
    		if (fun[i] != null) {
    			fun[i].replaceChildrenByValues(geo);
    		}
    }
	
	/** 
	 * Sets the start and end parameter value of this curve.
	 * @param startParam start parameter
	 * @param endParam end parameter
	 */
	public void setInterval(double startParam, double endParam) {
		
		this.startParam = startParam;
		this.endParam = endParam;
		
		isDefined = startParam <= endParam;	
	}
	
	
	
	/**
	 * Returns the start parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * @return start parameter
	 */
	public double getMinParameter() {
		return startParam;
	}
	
	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * @return end parameter
	 */
	public double getMaxParameter() {
		return endParam;
	}
	
	
	
	
	
	
	/**
	* returns all class-specific xml tags for getXML
	*/
	@Override
	protected void getXMLtags(StringBuilder sb) {
	   super.getXMLtags(sb);
	 
	   //	line thickness and type  
		getLineStyleXML(sb);
 
   }
	

	@Override
	public boolean isPath() {
		return true;
	}
	
	

	@Override
	final public boolean isDefined() {
		return isDefined;
	}

	/**
	 * @param defined new value of defined flag
	 */
	public void setDefined(boolean defined) {
		isDefined = defined;
	}

	@Override
	public void setUndefined() {
		isDefined = false;
	}

	
	
	@Override
	public String toString(StringTemplate tpl) {
		StringBuilder sbToString = new StringBuilder(80);
		
		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			// sbToString.append('(');
			// sbToString.append(funX.getVarString());
			// sbToString.append(") = ");
			// changed to ':' to make LaTeX output better
			sbToString.append(':');
		}
		sbToString.append(toValueString(tpl));
		return sbToString.toString();
	}
	
	
	
	
	

	@Override
	public String toValueString(StringTemplate tpl) {
		if (isDefined) {
			StringBuilder sbTemp = new StringBuilder(80);
			
			sbTemp.setLength(0);
			sbTemp.append('(');
			
			for (int i=0; i< fun.length;i++){
			sbTemp.append(fun[i].toValueString(tpl));
			if (i<fun.length-1)
				sbTemp.append(", ");
			}
			
			sbTemp.append(')');
			return sbTemp.toString();
		}
		return loc.getPlain("Undefined");
	}	
	
	/**
	 * @param tpl string template
	 * @return symbolic string representation
	 */
	public String toSymbolicString(StringTemplate tpl) {
		StringBuilder sbTemp = null;
		if (isDefined) {
			sbTemp = new StringBuilder(80);
			
			sbTemp.setLength(0);
			sbTemp.append('(');
			
			for (int i=0; i< fun.length;i++){
			sbTemp.append(fun[i].toString(tpl));
			if (i<fun.length-1)
				sbTemp.append(", ");
			}
			
			sbTemp.append(')');
			return sbTemp.toString();
		} 
		return loc.getPlain("Undefined");
	}
	
	@Override
	public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
		StringBuilder sbTemp = null;
		if (isDefined) {
			sbTemp = new StringBuilder(80);
			
			sbTemp.setLength(0);
			sbTemp.append("\\left(\\begin{array}{c}");
			
			for (int i=0; i< fun.length;i++){
				sbTemp.append(fun[i].toLaTeXString(symbolic,tpl));
				if (i<fun.length-1)
					sbTemp.append("\\\\");
				}
			
			sbTemp.append("\\end{array}\\right)");
			return sbTemp.toString();
		}
		return loc.getPlain("Undefined");		
	}	
	
	/**
	 * @param i
	 * @return i-th function
	 */
	public Function getFun(int i){
		return fun[i];
	}
	
	public final void update(){
		super.update();
		for(int i=0; i< this.funExpanded.length; i++){
			this.funExpanded[i]=null;
		}
	}
	
	public FunctionVariable[] getFunctionVariables() {
		return getFun(0).getFunctionVariables();
	}
	
	public String getVarString(StringTemplate tpl) {
		return getFun(0).getVarString(tpl);
	}
	
	/**
	 * Set this curve by applying CAS command to f.
	 */
	public void setUsingCasCommand(String ggbCasCmd, CasEvaluableFunction f,
			boolean symbolic,MyArbitraryConstant arbconst) {
		GeoCurveCartesianND c = (GeoCurveCartesianND) f;

		if (c.isDefined()) {
			//register the variable name to make sure parsing of CAS output runs OK, see #3006
			GeoNumeric geo = new GeoNumeric(this.cons);
			this.cons.addLocalVariable(getFun(0).getVarString(StringTemplate.defaultTemplate), geo);
			this.isDefined = true;
			for(int k = 0; k < getDimension(); k++){
				setFun(k, (Function) c.getFunExpanded(k).evalCasCommand(ggbCasCmd, symbolic,arbconst));
				this.isDefined = this.isDefined && getFun(k) != null;
			}
			this.cons.removeLocalVariable(getFun(0).getVarString(StringTemplate.defaultTemplate));
			if (this.isDefined)
				setInterval(c.startParam, c.endParam);
		} else {
			this.isDefined = false;
		}
		this.distFun = null;
	}
	
	public void clearCasEvalMap(String key) {
		for(int k = 0; k < getDimension(); k++){
			getFun(k).clearCasEvalMap(key);
		}		
	}
	
	protected void setFun(int i, Function f) {
		this.fun[i] = f;
		this.funExpanded[i]=null;
		this.containsFunctions[i]=AlgoDependentFunction.containsFunctions(this.fun[i].getExpression());
	}
	
	protected Function getFunExpanded(int i) {
		if(!this.containsFunctions[i]){
			return getFun(i);
		}
		if(this.funExpanded[i] == null){
			this.funExpanded[i] = new Function(getFun(i),this.kernel);
			ExpressionNode expr = AlgoDependentFunction.expandFunctionDerivativeNodes(getFun(i).getExpression().deepCopy(this.kernel)).wrap();
			this.funExpanded[i].setExpression(expr);
		}
		return this.funExpanded[i];
	}

	/**
	 * Set this curve to the n-th derivative of c
	 * @param curve curve whose derivative we want
	 * 
	 * @param n
	 *            order of derivative
	 */
	public void setDerivative(GeoCurveCartesianND curve, int n) {
		if (curve.isDefined()) {
			this.isDefined = true;
			for(int k = 0; k < getDimension(); k++){
				setFun(k, curve.getFunExpanded(k).getDerivative(n, true));
				this.isDefined = this.isDefined && getFun(k) != null;
			}

			if (this.isDefined)
				setInterval(curve.startParam, curve.endParam);
		} else {
			this.isDefined = false;
		}
		this.distFun = null;
	}

	public int getDimension() {
		return this.fun.length;
	}

	public abstract double getClosestParameter(GeoPointND a, double minParameter);

	public abstract double evaluateCurvature(double t);
}
