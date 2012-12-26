/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;


/**
 * Handles rotated texts
 * @author Michael
 *
 */

public class AlgoRotateText extends AlgoElement {

	private GeoText text; //output	
    private GeoText args; //input	
    private GeoNumeric angle; // input
     
    private StringBuilder sb = new StringBuilder();
    
    /**
     * Creates new text rotation algo
     * @param cons construction
     * @param label label for output
     * @param args input text
     * @param angle angle of rotation
     */
    public AlgoRotateText(Construction cons, String label, GeoText args, GeoNumeric angle) {
    	this(cons,  args, angle);
        text.setLabel(label);
    }

    /**
     * Creates new unlabeled text rotation algo
     * @param cons construction
     * @param args input text
     * @param angle angle of rotation
     */
    AlgoRotateText(Construction cons, GeoText args, GeoNumeric angle) {
        super(cons);
        this.args = args;
        this.angle = angle;
               
        text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		
        setInputOutput();
        compute();        
    }

    @Override
	public Commands getClassName() {
        return Commands.RotateText;
    }

    @Override
	protected void setInputOutput(){
	    input = new GeoElement[2];
	    input[0] = args;
	    input[1] = angle;
	    
	    args.addTextDescendant(text);

        setOutputLength(1);
        setOutput(0,text);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the resulting text
     * @return resulting text
     */
    public GeoText getResult() {
        return text;
    }

    @Override
	public final void compute() {
    	if (!args.isDefined() || !angle.isDefined() || angle.isInfinite()) {
    		text.setTextString("");
    		return;
    	}
    	
    	sb.setLength(0);
    	appendRotatedText(sb,args,angle.getValue()*180/Math.PI);
    	

    	text.setTextString(sb.toString());
    	text.setLaTeX(true,false);
    }
    
    /**
     * Appends LaTeX command for the rotated text to the string builder.
     * @param sbuilder string builder
     * @param text text
     * @param degrees rotation algo
     */
    public static void appendRotatedText(StringBuilder sbuilder,GeoText text,double degrees){
    	boolean latex = text.isLaTeX();
    	sbuilder.append("\\rotatebox{");
    	sbuilder.append(degrees); // convert to degrees
    	sbuilder.append("}{ ");
    	if (!latex) sbuilder.append("\\text{ ");
    	sbuilder.append(text.getTextString());
    	if (!latex) sbuilder.append(" } ");
    	sbuilder.append(" }");
    }

	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

	// TODO Consider locusequability

}
