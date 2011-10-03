/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;




public class AlgoTableText extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
    private GeoText text; //output	
    private GeoText args; //input	
    
    private GeoList[] geoLists;
    
    private StringBuffer sb = new StringBuffer();
    
    private int VERTICAL = 0;
    private int HORIZONTAL = 1;
    
    // style variables
    private int alignment;
	private boolean verticalLines, horizontalLines;
	private String justification, openBracket, closeBracket, openString, closeString;
    
	// getters for style variables (used by EuclidianStyleBar)
	public int getAlignment() {
		return alignment;
	}

	public boolean isVerticalLines() {
		return verticalLines;
	}

	public boolean isHorizontalLines() {
		return horizontalLines;
	}

	public String getJustification() {
		return justification;
	}

	public String getOpenSymbol() {
		return openString;
	}

	public String getCloseSymbol() {
		return closeString;
	}
	
    

    AlgoTableText(Construction cons, String label, GeoList geoList, GeoText args) {
    	this(cons, geoList, args);
        text.setLabel(label);
    }

    AlgoTableText(Construction cons, GeoList geoList, GeoText args) {
        super(cons);
        this.geoList = geoList;
        this.args = args;
               
        text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		
        setInputOutput();
        compute();
        
        // set sans-serif LaTeX default
		text.setSerifFont(false);
    }

    public String getClassName() {
        return "AlgoTableText";
    }

    protected void setInputOutput(){
    	if (args == null) {
	        input = new GeoElement[1];
	        input[0] = geoList;
    	} else {
            input = new GeoElement[2];
            input[0] = geoList;
            input[1] = args;
    	}

        output = new GeoElement[1];
        output[0] = text;
        setDependencies(); // done by AlgoElement
    }

    GeoText getResult() {
        return text;
    }
    
    
   
    private void parseArgs() {
    	
    	int columns = geoList.size();
   
    	// set defaults
    	alignment = HORIZONTAL;
    	verticalLines = false;
    	horizontalLines = false;
    	justification = "l"; 
    	// need an open & close together, so can't use ""
    	openBracket = "\\left.";
    	closeBracket = "\\right.";

    	
    	if (args != null) {
    		String optionsStr = args.getTextString();
    		if (optionsStr.indexOf("v") > -1) alignment = VERTICAL; // vertical table
    		if (optionsStr.indexOf("|") > -1 && optionsStr.indexOf("||") == -1) verticalLines = true; 
    		if (optionsStr.indexOf("_") > -1) horizontalLines = true; // vertical table
    		if (optionsStr.indexOf("c") > -1) justification = "c";
    		else if (optionsStr.indexOf("r") > -1) justification = "r";	
    		
    		if (optionsStr.indexOf("||||") > -1) {
    			openBracket = "\\left| \\left|";
    			closeBracket = "\\right| \\right|";
    			openString = "||";
    			closeString = "||";
    		} else if (optionsStr.indexOf("||") > -1) {
    			openBracket = "\\left|";
    			closeBracket = "\\right|";
    			openString = "|";
    			closeString = "|";
    		} else if (optionsStr.indexOf('(') > -1) {
    			openBracket = "\\left(";
    			openString = "(";
    		} else if (optionsStr.indexOf('[') > -1) {
    			openBracket = "\\left[";
    			openString = "[";
    			
    		} else if (optionsStr.indexOf('{') > -1) {
    			openBracket = "\\left\\{";
    			openString = "{";
    		} 
    		
    		if (optionsStr.indexOf(')') > -1) {
    			closeBracket = "\\right)";
    			closeString = ")";
    		} else if (optionsStr.indexOf(']') > -1) {
    			closeBracket = "\\right]";
    			closeString = "]";
    		} else if (optionsStr.indexOf('}') > -1) {
    			closeBracket = "\\right\\}";
    			closeString = "}";
    		} 
    		
    	} else if (geoList.get(columns-1).isGeoText()) {
    		
    		// support for older files before the fix   		
     		GeoText options = (GeoText)geoList.get(columns-1);
     		String optionsStr = options.getTextString();
     		if (optionsStr.indexOf("h") > -1) alignment = HORIZONTAL; // horizontal table
     		if (optionsStr.indexOf("c") > -1) justification = "c";
     		else if (optionsStr.indexOf("r") > -1) justification = "r";
    	}
    }
    
    
    
    
    
    protected final void compute() {
    	
    	int columns = geoList.size();
    	
    	if (!geoList.isDefined() ||  columns == 0) {
    		text.setTextString("");
    		return;
    		//throw new MyError(app, app.getError("InvalidInput"));   		
    	}
    	
    	parseArgs();
    	
    		
    	// support for older files before the fix
    	if (geoList.get(columns-1).isGeoText()) {
     		columns --;
    	}

    	
    	if (columns == 0) {
    		text.setTextString("");
    		return;
    		//throw new MyError(app, app.getError("InvalidInput"));   		
    	}
    	

    	if (geoLists == null || geoLists.length < columns)
    	    		geoLists = new GeoList[columns];
    	
    	int rows = 0;
    	
		for (int c = 0 ; c < columns ; c++) {
			GeoElement geo = geoList.get(c);
			if (!geo.isGeoList()) {
				text.setTextString("");
				return;
	    		//throw new MyError(app, app.getPlain("SyntaxErrorAisNotAList",geo.toValueString()));
			}
			geoLists[c] = (GeoList)geoList.get(c);
			if (geoLists[c].size() > rows) rows = geoLists[c].size();
		}
		
    	if (rows == 0) {
    		text.setTextString("");
    		return;
    		//throw new MyError(app, app.getError("InvalidInput"));   		
    	}

    	
    	text.setTemporaryPrintAccuracy();
    	
    	sb.setLength(0);
    	
    	
    	// surround in { } to make eg this work:
    	// FormulaText["\bgcolor{ff0000}"+TableText[matrix1]]
    	sb.append('{');
    	
    	sb.append(openBracket);
		// Added by Loïc 2009/12/15
    	sb.append("\\begin{array}{");
		// end Loïc
    	
    	if (alignment == VERTICAL) {
    	
	    	for (int c = 0 ; c < columns ; c++) {
	    		if (verticalLines) sb.append("|");
	    		sb.append(justification); // "l", "r" or "c"
	    	}
    		if (verticalLines) sb.append("|");
	    	sb.append("}");

	    	if (horizontalLines) sb.append("\\hline ");

	    	for (int r=0; r < rows; r++) {
	    		for (int c = 0 ; c < columns ; c++) {
	    			// Added by Loïc 2009/12/15
	    			boolean finalCell = (c == columns - 1);
	    			addCell(c, r,finalCell);
	    			// end Loïc
	   		}
	    		sb.append(" \\\\ "); // newline in LaTeX ie \\
		    	if (horizontalLines) sb.append("\\hline ");
	    	}   
    	
    	}
    	else
    	{ // alignment == HORIZONTAL
    	
	    	for (int c = 0 ; c < rows ; c++) {
	    		if (verticalLines) sb.append("|");
	    		sb.append(justification); // "l", "r" or "c"
	    	}
	    	if (verticalLines) sb.append("|");
	    	sb.append("}");
	    	
	    	if (horizontalLines) sb.append("\\hline ");
	    	
	    	// TableText[{11.1,322,3.11},{4,55,666,7777,88888},{6.11,7.99,8.01,9.81},{(1,2)},"c()"]
	    	
			for (int c = 0 ; c < columns ; c++) {
	    	for (int r=0; r < rows; r++) {
    			// Added by Loïc 2009/12/15
    			boolean finalCell = (r == rows - 1);
    			addCell(c, r,finalCell);
    			// end Loïc
	    		}
	    		sb.append(" \\\\ "); // newline in LaTeX ie \\
		    	if (horizontalLines) sb.append("\\hline ");
	    	}   
		
    	}

    	text.restorePrintAccuracy();
		// Added by Loïc 2009/12/15
    	sb.append("\\end{array}");
    	sb.append(closeBracket);
		// end Loïc 2009/12/15
    	
    	// surround in { } to make eg this work:
    	// FormulaText["\bgcolor{ff0000}"+TableText[matrix1]]
    	sb.append('}');
    	
    	//Application.debug(sb.toString());
    	text.setTextString(sb.toString());
    	text.setLaTeX(true,false);
    }
	// Modify by Loïc Le Coq 2009/12/15
    private void addCell(int c, int r, boolean finalCell) {
    	// End Loïc
		if (geoLists[c].size() > r) { // check list has an element at this position
			GeoElement geo1 = geoLists[c].get(r);
			
			// replace " " and "" with a hard space (allow blank columns/rows)
			String text = geo1.toLaTeXString(false);
			if (" ".equals(text) || "".equals(text))
				text = "\\;"; // problem with JLaTeXMath, was "\u00a0";	
			// Modify by Loïc Le Coq 2009/12/15
			if (geo1.isTextValue()){
				sb.append("\\text{"); // preserve spaces
				sb.append(text);
				sb.append("}");
			}
			else 
			sb.append(text);
			// End Loïc
		}
		// Modify by Loïc Le Coq 2009/12/15
		if (!finalCell) sb.append("&"); // separate columns    				
		// End Loïc
    }

	
  
}
