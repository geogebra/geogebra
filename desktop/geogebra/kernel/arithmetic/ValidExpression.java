/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/**
 * Used as internal return type in Parser.
 * Stores a label. 
 */ 
package geogebra.kernel.arithmetic;

import java.util.Set;
import java.util.Vector;

public abstract class ValidExpression implements ExpressionValue {                
        
    private Vector labels;
    private boolean inTree; // used by ExpressionNode
    private boolean keepInputUsed; // flag used by GeoGebraCAS

    public String toRealString(){
    	return toString();
    }
	public void addLabel(String label) {  
    	initLabels();
        labels.add(label);
    }   
    
    private void initLabels() {
    	if (labels == null)
    		labels = new Vector();
    }
    
    public void addNullLabel() {
    	initLabels();
    	labels.add(null);
    }
    
    public void addLabel(Vector labellist) { 
    	initLabels();
        labels.addAll(labellist);
    }     
    
    public int labelCount() {
    	if (labels == null) 
    		return 0;
    	else
    		return labels.size();
    }
    
    public String getLabel(int index) {
        if (index < 0 || index >= labelCount()) 
        	return null;
        else 
        	return (String) labels.get(index);
    }
    
    public String [] getLabels() {
    	int size = labelCount();
    	if (size == 0) 
    		return null;
    	
        String [] ret = new String[size];
        for (int i=0; i < size; i++) {
            ret[i] = (String) labels.get(i);
        }
        return ret;
    }
    
    public String getLabel() {
        return getLabel(0);
    }
    
    public void setLabel(String label) {
    	initLabels();
        labels.clear();
        labels.add(label);
    }
    
	public void setLabels(String [] str) {
		initLabels();
		labels.clear();
		if (str == null) return;
		for (int i=0; i < str.length; i++) {
			labels.add(str[i]);
		}
	}
	
	public boolean isVariable() {
		return false;
	}   
	
	final public boolean isInTree() {
		return inTree;
	}
	
	final public void setInTree(boolean flag) {
		inTree = flag;
	}
	 
	final public boolean isGeoElement() {
	   return false;
	}
	
	public boolean isTopLevelCommand() {
		return false;
	}
	
	public Command getTopLevelCommand() {
		return null;
	}
	
	 public String getLabelForAssignment() {
		 return getLabel();
	 }
	 
	 /**
	  * Includes the label and assignment operator.
	  * E.g. while toString() would return x^2, this method
	  * would return f(x) := x^2
	  * @return
	  */
	 public String toAssignmentString() {
		 if (labels == null) {
			 return toString();
		 }
		 
		 StringBuilder sb = new StringBuilder();
		 sb.append(getLabelForAssignment());
		 sb.append(getAssignmentOperator());
		 sb.append(toString());
		 return sb.toString();
	 }
	 
	 public String toAssignmentLaTeXString() {
		 if (labels == null) {
			 return toLaTeXString(true);
		 }
		 
		 StringBuilder sb = new StringBuilder();
		 sb.append(getLabelForAssignment());
		 sb.append(getAssignmentOperatorLaTeX());
		 sb.append(toLaTeXString(true));
		 return sb.toString();
	 }
	 
	 public String getAssignmentOperator() {
		 return " := ";
	 }
	 
	 public String getAssignmentOperatorLaTeX() {
		 return " \\, :=  \\, ";
	 }
	 
	  public void addCommands(Set<Command> cmds) {
		// do nothing, see Command, ExpressionNode classes
	  }
	  
	   public boolean isKeepInputUsed() {
			return keepInputUsed;
		}

		public void setKeepInputUsed(boolean keepInputUsed) {
			this.keepInputUsed = keepInputUsed;
		}
	
}