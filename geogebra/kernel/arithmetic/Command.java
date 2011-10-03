/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Command.java
 *
 * Created on 05. September 2001, 12:05
 */

package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.util.Unicode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author  Markus
 * @version 
 */
public class Command extends ValidExpression 
implements ReplaceableValue {
    
     
    // list of arguments
    private ArrayList<ExpressionNode> args = new ArrayList<ExpressionNode>();
    private String name; // internal command name (in English)
    
    private Kernel kernel;
    private Application app;
    private GeoElement[] evalGeos; // evaluated Elements
    private Macro macro; // command may correspond to a macro 
    private boolean allowEvaluation = true;
    
    /** Creates new Command */
    public Command(Kernel kernel, String name, boolean translateName) {    
        this.kernel = kernel;
        app = kernel.getApplication();
        
        /* need to check app.isUsingInternalCommandNames() due to clash with
        * BinomialDist=Binomial
        * Binomial=BinomialCoefficient
        * Should also allow other languages to use English names for different commands
        */
        
        if (translateName && !kernel.isUsingInternalCommandNames()) {           
            //  translate command name to internal name
            this.name = app.translateCommand(name);           
        } else {
            this.name = name;
        }               
    }
    
    public Kernel getKernel() {
        return kernel;
    }
    
    public void addArgument(ExpressionNode arg) {
        args.add( arg );
    }
    
    /**
     * Returns the name of the variable at the specified argument position.
     * If there is no variable name at this position, null is returned.
     */
    public String getVariableName(int i) {
    	if (i >= args.size())
    		return null;
    	
    	ExpressionValue ev = ((ExpressionNode) args.get(i)).getLeft();
    	if (ev instanceof Variable)
    		return ((Variable) ev).getName();
    	else if (ev instanceof GeoElement) {
    		// XML Handler looks up labels of GeoElements
    		// so we may end up having a GeoElement object here
    		// return its name to use as local variable name
    		GeoElement geo = ((GeoElement) ev);
    		if (geo.isLabelSet())
    			return ((GeoElement) ev).getLabel();
    	}     	    	
    	else if (ev instanceof Function) {
    		String str = ev.toString();
    		if (str.length() == 1 && Character.isLetter(str.charAt(0)))
    			return str;
    	}
    	else if (ev instanceof GeoVec2D) {
    		if (((GeoVec2D) ev).isImaginaryUnit()) {
    			return Unicode.IMAGINARY;
    		}
    	}
    	else if (ev instanceof MySpecialDouble) {
    		if (((MySpecialDouble) ev).isEulerConstant()) {
    			return Unicode.EULER_STRING;
    		}
    	}
    	
    	return null;
    }
    
    public ExpressionNode [] getArguments() {
    	return args.toArray(new ExpressionNode[0]);
    }
    
  
    public ExpressionNode getArgument(int i) {
    	return args.get(i);        
    }
    
    public void setArgument(int i, ExpressionNode en) {
        args.set(i, en);
    }
    
    public int getArgumentNumber() {
        return args.size();
    }
    
    public String getName() {
        return name;
    }
    
    public String toString() {  
    	return toString(true, false);
    }
    
    public String toValueString() {
        return toString(false, false);
    }
    
	public String toLaTeXString(boolean symbolic) {
		return toString(symbolic, true);
	}    
    
    private String toString(boolean symbolic, boolean LaTeX) {    
    	switch (kernel.getCASPrintForm()){
		case ExpressionNode.STRING_TYPE_MATH_PIPER:
		case ExpressionNode.STRING_TYPE_MAXIMA:
		case ExpressionNode.STRING_TYPE_MPREDUCE:
    			// MathPiper command syntax
    			return ((geogebra.cas.GeoGebraCAS) kernel.getGeoGebraCAS()).
    				getCASCommand(name, args, symbolic);    			    	
    			
    		default:
    	    	if (sbToString == null)
    	    		 sbToString = new StringBuilder();
    	    	sbToString.setLength(0);
    	    	
    			// GeoGebra command syntax		               
		        if (kernel.isPrintLocalizedCommandNames()) {
		        	sbToString.append(app.getCommand(name));                       
		        } else {
		        	sbToString.append(name);
		        }        
    			sbToString.append('[');
		        int size = args.size();
		        for (int i = 0; i < size; i++) {
		        	sbToString.append( toString((ExpressionValue) args.get(i), symbolic, LaTeX));
		        	sbToString.append(',');
		        }
		        sbToString.setCharAt(sbToString.length()-1, ']');		        
		        return sbToString.toString();		    	
    	}
    	
    }
    private StringBuilder sbToString;  
    
    private String toString(ExpressionValue ev, boolean symbolic, boolean LaTeX) {
    	if (LaTeX) {
    		return ev.toLaTeXString(symbolic);
    	}
    	else {
    		return symbolic ? ev.toString() : ev.toValueString();
    	}    	
    }
    
    public GeoElement [] evaluateMultiple() {
            GeoElement [] geos = null;
            geos = kernel.getAlgebraProcessor().processCommand(this, false);          
            return geos;
     }
    
    
    public ExpressionValue evaluate() {
        // not yet evaluated: process command
        if (evalGeos == null) 
        	evalGeos = evaluateMultiple(); 
      
         if (evalGeos != null && evalGeos.length >= 1) {
            return evalGeos[0]; 
         } else {
            Application.debug("invalid command evaluation: " + name);
            throw new MyError(app, app.getError("InvalidInput") + ":\n" + this);                                                                 
         }          
    }
    
    public void resolveVariables() {
    	// standard case:
    	// nothing to do here: argument variables are resolved
    	// while command processing (see evaluate())
    	
    	// CAS parsing case: we need to resolve arguments also
    	if (kernel.isResolveUnkownVarsAsDummyGeos()) {
	        for (int i=0; i < args.size(); i++) {        
	            ((ExpressionValue) args.get(i)).resolveVariables();        
	        }
	        
	        // avoid evaluation of command
	        allowEvaluation = false;
    	}
    }
    
    // rewritten to cope with {Root[f]}
    // Michael Borcherds 2008-10-02	
    public boolean isConstant() {
    	
        // not yet evaluated: process command
        if (evalGeos == null) 
        	evalGeos = evaluateMultiple();   
    	
    	if (evalGeos == null || evalGeos.length == 0)
    		throw new MyError(app, app.getError("InvalidInput") + ":\n" + this);     
    	
    	for (int i = 0 ; i < evalGeos.length ; i++)
    		if (!evalGeos[i].isConstant()) return false;
    	return true;
    	
    }

    public boolean isLeaf() {
        //return evaluate().isLeaf();
        return true;
    }

    public boolean isNumberValue() {
        return allowEvaluation && evaluate().isNumberValue();
    }

    public boolean isVectorValue() {
        return allowEvaluation && evaluate().isVectorValue();
    }
    
    final public boolean isBooleanValue() {
        return allowEvaluation && evaluate().isBooleanValue();
    }

    public boolean isPolynomialInstance() {
        return false;
                
        //return evaluate().isPolynomial();
    }
    
    public boolean isTextValue() {
        return allowEvaluation && evaluate().isTextValue();
    }   

    public ExpressionValue deepCopy(Kernel kernel) {
        Command c = new Command(kernel, name, false);
        // copy arguments     
        int size = args.size();
        for (int i=0; i < size; i++) {
            c.addArgument(args.get(i).getCopy(kernel));
        }
        return c;
    }
    
    /**
     * Replaces geo and all its dependent geos in this tree by
     * copies of their values.
     */
    public void replaceChildrenByValues(GeoElement geo) {                                                
        int size = args.size();
        for (int i = 0 ; i < size; i++) {
        	args.get(i).replaceChildrenByValues(geo);                	                	
        }         
    }
    
	/**
	 * Looks for GeoDummyVariable objects that hold String var in the tree and replaces
	 * them by their newOb.
	 * @return whether replacement was done
	 */
	public boolean replaceGeoDummyVariables(String var, ExpressionValue newOb) {
		int size = args.size();
		boolean didReplacement = false;
        for (int i = 0 ; i < size; i++) {
        	didReplacement = args.get(i).replaceGeoDummyVariables(var, newOb) || didReplacement;                	                	
        }
        return didReplacement;
    }
		
    public HashSet getVariables() {             
        HashSet set = new HashSet();
       int size = args.size();
        for (int i=0; i < size; i++) {
            HashSet s = ((ExpressionNode)args.get(i)).getVariables();
            if (s != null) set.addAll(s);
        } 
        return set;
    }

    public void addCommands(Set commands) {
    	commands.add(this);
    	
    	int size = args.size();
        for (int i=0; i < size; i++) {
            ((ExpressionNode)args.get(i)).addCommands(commands);
        } 
	}

    final public boolean isExpressionNode() {
        return false;
    }
    
 
    
    final public boolean contains(ExpressionValue ev) {
        return ev == this;
    }

	public boolean isListValue() {		
		return false;
	}

	public final Macro getMacro() {
		return macro;
	}

	public final void setMacro(Macro macro) {
		this.macro = macro;
	}    
	
    final public boolean isVector3DValue() {
    	return false;
    }
    
    public boolean isTopLevelCommand() {
		return true;
	}
    
    public Command getTopLevelCommand() {
		return this;
	}

	public String toOutputValueString() {
		return toValueString();
	}
	
	public ExpressionValue replace(ExpressionValue oldOb, ExpressionValue newOb) {
        for (int i=0; i < args.size(); i++) {
        	ExpressionNode en = (ExpressionNode)args.get(i);
            en = en.replaceAndWrap(oldOb, newOb);
            args.set(i, en);
        }     	
        return this;
    }

}
