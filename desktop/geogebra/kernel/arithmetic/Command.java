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

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.AbstractCommand;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.ReplaceableValue;
import geogebra.common.util.Unicode;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoVec2D;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author  Markus
 * @version 
 */
public class Command extends AbstractCommand 
implements ReplaceableValue {
    
     
    // list of arguments
    private ArrayList<ExpressionNode> args = new ArrayList<ExpressionNode>();
    private String name; // internal command name (in English)
    
    private Kernel kernel;
    private Application app;
    private GeoElement[] evalGeos; // evaluated Elements
    private Macro macro; // command may correspond to a macro 
    private boolean allowEvaluationForTypeCheck = true;
    
    /** Creates new Command */
    public Command(Kernel kernel, String name, boolean translateName) {    
    	this(kernel, name, translateName, true);
    }
    
    /**
     * Creates a new command object.
     * 
     * @param kernel
     * @param name
     * @param translateName
     * @param allowEvaluationForTypeCheck whether this command is allowed to be evaluated in type checks like isTextValue()
     */
    public Command(Kernel kernel, String name, boolean translateName, boolean allowEvaluationForTypeCheck) {
        this.kernel = kernel;
        app = kernel.getApplication();
        this.allowEvaluationForTypeCheck = allowEvaluationForTypeCheck;
        
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
    	
    	ExpressionValue ev = args.get(i).getLeft();
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
		case MATH_PIPER:
		case MAXIMA:
		case MPREDUCE:
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
		        	sbToString.append( toString(args.get(i), symbolic, LaTeX));
		        	sbToString.append(',');
		        }
		        sbToString.setCharAt(sbToString.length()-1, ']');		        
		        return sbToString.toString();		    	
    	}
    	
    }
    private StringBuilder sbToString;  
    
    private static String toString(ExpressionValue ev, boolean symbolic, boolean LaTeX) {
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
	            args.get(i).resolveVariables();        
	        }
	        
	        // avoid evaluation of command
	        allowEvaluationForTypeCheck = false;
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
        return allowEvaluationForTypeCheck && evaluate().isNumberValue();
    }

    public boolean isVectorValue() {
        return allowEvaluationForTypeCheck && evaluate().isVectorValue();
    }
    
    final public boolean isBooleanValue() {
        return allowEvaluationForTypeCheck && evaluate().isBooleanValue();
    }

    public boolean isPolynomialInstance() {
        return false;
                
        //return evaluate().isPolynomial();
    }
    
    public boolean isTextValue() {
        return allowEvaluationForTypeCheck && evaluate().isTextValue();
    }   

    public ExpressionValue deepCopy(AbstractKernel kernel) {
        Command c = new Command((Kernel)kernel, name, false);
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
            HashSet s = args.get(i).getVariables();
            if (s != null) set.addAll(s);
        } 
        return set;
    }

    @Override
    public void addCommands(Set<AbstractCommand> commands) {
    	commands.add(this);
    	
    	int size = args.size();
        for (int i=0; i < size; i++) {
            args.get(i).addCommands(commands);
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
    
    public AbstractCommand getTopLevelCommand() {
		return this;
	}

	public String toOutputValueString() {
		return toValueString();
	}
	
	public ExpressionValue replace(ExpressionValue oldOb, ExpressionValue newOb) {
        for (int i=0; i < args.size(); i++) {
        	ExpressionNode en = args.get(i);
            en = en.replaceAndWrap(oldOb, newOb);
            args.set(i, en);
        }     	
        return this;
    }

}
