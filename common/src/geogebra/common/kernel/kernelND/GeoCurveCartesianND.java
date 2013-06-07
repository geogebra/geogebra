package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.geos.GeoElement;


/**
 * Abstract class for cartesian curves in any dimension
 * @author matthieu
 *
 */
public abstract class GeoCurveCartesianND extends GeoElement implements CurveEvaluable3D{
	
	/** coordinates  functions */
	protected Function[] fun;
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


	/** common constructor
	 * @param c construction
	 */
	public GeoCurveCartesianND(Construction c) {
		super(c);
		
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
		this(c);
		this.fun = fun;
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
			sbToString.append('(');
			sbToString.append(fun[0].getFunctionVariables()[0].toString(tpl));
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
		return app.getPlain("Undefined");
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
		return app.getPlain("Undefined");
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
		return app.getPlain("Undefined");		
	}		
	
}
