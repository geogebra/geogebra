package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Traversing.FunctionExpander;
import org.geogebra.common.kernel.geos.GeoElement;


/**
 * Abstract class for cartesian curves in any dimension
 * @author matthieu
 *
 */
public abstract class GeoSurfaceCartesianND extends GeoElement{
	
	/** coordinates functions */
	protected FunctionNVar[] fun;
	/** derivative functions */
	protected FunctionNVar[][] fun1;
	/** start parameters */
	protected double[] startParam;
	/** end parameters */
	protected double[] endParam;
	
	/** flag for isDefined() */
	protected boolean isDefined = true;
	private ExpressionNode point;


	/** common constructor
	 * @param c construction
	 */
	public GeoSurfaceCartesianND(Construction c) {
		super(c);
		
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

	}
	
	/**
	 * constructor with functions
	 * 
	 * @param c
	 *            construction
	 * @param fun
	 *            functions
	 */
	public GeoSurfaceCartesianND(Construction c, ExpressionNode point,
			FunctionNVar[] fun) {
		this(c);
		this.fun = fun;
		this.point = point;
	}	
	
	/**
	 * set derivatives (if not already done)
	 */
	public void setDerivatives(){
		
		if (fun1 != null){
			return;
		}
		
		// set derivatives
		FunctionVariable[] vars = fun[0].getFunctionVariables();
		
		fun1 = new FunctionNVar[vars.length][];
		for (int j = 0; j < vars.length; j++) {
			fun1[j] = new FunctionNVar[fun.length];
		}
		
		if (functionExpander == null){
			functionExpander = new FunctionExpander();
		}
		for (int i = 0; i < fun.length; i++) {
			ExpressionValue ve = fun[i].deepCopy(getKernel()).traverse(functionExpander);
			for (int j = 0; j < vars.length; j++) {				
				fun1[j][i] = new FunctionNVar(ve.derivative(vars[j], getKernel()).wrap(), vars);
			}
		}
		
	}
	
	/**
	 * reset derivatives
	 */
	public void resetDerivatives(){
		fun1 = null;
	}
	
	private static FunctionExpander functionExpander;

	
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
	public void setIntervals(double[] startParam, double endParam[]) {
		
		this.startParam = startParam;
		this.endParam = endParam;
		
		isDefined = true;
		
		for (int i=0; i<startParam.length && isDefined; i++)
			isDefined = startParam[i] <= endParam[i];	
	}
	
	
	
	/**
	 * @param i index of parameter
	 * @return the ith start parameter value for this
	 * surface (may be Double.NEGATIVE_INFINITY)
	 * 
	 */
	public double getMinParameter(int i) {
		return startParam[i];
	}
	
	/**
	 * @param i index of parameter
	 * @return the largest possible ith parameter value for this
	 * surface (may be Double.POSITIVE_INFINITY)
	 * 
	 */
	public double getMaxParameter(int i) {
		return endParam[i];
	}
	
	
	
	
	
	
	/**
	* returns all class-specific xml tags for getXML
	*/
	@Override
	protected void getXMLtags(StringBuilder sb) {
	   super.getXMLtags(sb);
	 
	   //	line thickness and type  
	   //	getLineStyleXML(sb);
 
   }
	

	

	@Override
	final public boolean isDefined() {
		return isDefined;
	}

	/**
	 * @param defined flag to mark as defined/undefined
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
		StringBuilder	sbToString = new StringBuilder(80);
		
		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			sbToString.append('(');
			sbToString.append(fun[0].getFunctionVariables()[0].toString(tpl));
			sbToString.append(',');
			sbToString.append(fun[0].getFunctionVariables()[1].toString(tpl));
			sbToString.append(") = ");					
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
		return "?";
	}	
	
	/**
	 * @param tpl string template
	 * @return symbolic string representation
	 */
	public String toSymbolicString(StringTemplate tpl) {	
		if (isDefined) {
			StringBuilder
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
		return "?";
	}
	
	@Override
	public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
		if (isDefined) {
			StringBuilder	sbTemp = new StringBuilder(80);
			
			if (point == null) {
			sbTemp.append("\\left(\\begin{array}{c}");
			
			for (int i=0; i< fun.length;i++){
				sbTemp.append(fun[i].toLaTeXString(symbolic,tpl));
				if (i<fun.length-1)
					sbTemp.append("\\\\");
				}
			
			sbTemp.append("\\end{array}\\right)");
			} else {
				sbTemp.append(point.toLaTeXString(symbolic, tpl));
			}
			return sbTemp.toString();
		}
		return "?";
	}	
	
	@Override
	public boolean isGeoSurfaceCartesian() {
		return true;
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

	public ExpressionNode getPointExpression() {
		return point;
	}
	
}
