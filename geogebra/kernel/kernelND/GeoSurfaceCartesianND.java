package geogebra.kernel.kernelND;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.FunctionNVar;


/**
 * Abstract class for cartesian curves in any dimension
 * @author matthieu
 *
 */
public abstract class GeoSurfaceCartesianND extends GeoElement{
	
	/** coordinates and derivative functions */
	protected FunctionNVar[] fun;
	
	protected double[] startParam, endParam;
	

	protected boolean isDefined = true;


	/** common constructor
	 * @param c
	 */
	public GeoSurfaceCartesianND(Construction c) {
		super(c);
		
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

	}
	
	/** constructor with functions
	 * @param c
	 * @param fun 
	 */
	public GeoSurfaceCartesianND(Construction c, FunctionNVar[] fun) {
		this(c);
		this.fun = fun;
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
	 * @param startParam 
	 * @param endParam 
	 */
	public void setIntervals(double[] startParam, double endParam[]) {
		
		this.startParam = startParam;
		this.endParam = endParam;
		
		isDefined = true;
		
		for (int i=0; i<startParam.length && isDefined; i++)
			isDefined = startParam[i] <= endParam[i];	
	}
	
	
	
	/**
	 * @return the ith start parameter value for this
	 * surface (may be Double.NEGATIVE_INFINITY)
	 * 
	 */
	public double getMinParameter(int i) {
		return startParam[i];
	}
	
	/**
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
	protected void getXMLtags(StringBuilder sb) {
	   super.getXMLtags(sb);
	 
	   //	line thickness and type  
	   //	getLineStyleXML(sb);
 
   }
	

	

	final public boolean isDefined() {
		return isDefined;
	}

	public void setDefined(boolean defined) {
		isDefined = defined;
	}

	public void setUndefined() {
		isDefined = false;
	}

	
	
	public String toString() {
		if (sbToString == null) {
			sbToString = new StringBuilder(80);
		}
		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			sbToString.append('(');
			sbToString.append(fun[0].getFunctionVariables()[0].toString());
			sbToString.append(',');
			sbToString.append(fun[0].getFunctionVariables()[1].toString());
			sbToString.append(") = ");					
		}		
		sbToString.append(toValueString());
		return sbToString.toString();
	}
	protected StringBuilder sbToString;
	protected StringBuilder sbTemp;
	
	

	public String toValueString() {		
		if (isDefined) {
			if (sbTemp == null) {
				sbTemp = new StringBuilder(80);
			}
			sbTemp.setLength(0);
			sbTemp.append('(');
			
			for (int i=0; i< fun.length;i++){
			sbTemp.append(fun[i].toValueString());
			if (i<fun.length-1)
				sbTemp.append(", ");
			}
			
			sbTemp.append(')');
			return sbTemp.toString();
		} else
			return app.getPlain("undefined");
	}	
	
	public String toSymbolicString() {	
		if (isDefined) {
			if (sbTemp == null) {
				sbTemp = new StringBuilder(80);
			}
			sbTemp.setLength(0);
			sbTemp.append('(');
			
			for (int i=0; i< fun.length;i++){
			sbTemp.append(fun[i].toString());
			if (i<fun.length-1)
				sbTemp.append(", ");
			}
			
			sbTemp.append(')');
			return sbTemp.toString();
		} else
			return app.getPlain("undefined");
	}
	
	public String toLaTeXString(boolean symbolic) {
		if (isDefined) {
			if (sbTemp == null) {
				sbTemp = new StringBuilder(80);
			}
			sbTemp.setLength(0);
			sbTemp.append("\\left(\\begin{array}{c}");
			
			for (int i=0; i< fun.length;i++){
				sbTemp.append(fun[i].toLaTeXString(symbolic));
				if (i<fun.length-1)
					sbTemp.append("\\\\");
				}
			
			sbTemp.append("\\end{array}\\right)");
			return sbTemp.toString();
		} else
			return app.getPlain("undefined");		
	}		
	
}
