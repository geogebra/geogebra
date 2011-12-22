/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
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
     
    private StringBuffer sb = new StringBuffer();
    
    /**
     * Creates new text rotation algo
     */
    public AlgoRotateText(Construction cons, String label, GeoText args, GeoNumeric angle) {
    	this(cons,  args, angle);
        text.setLabel(label);
    }

    /**
     * Creates new unlabeled text rotation algo
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
	public Algos getClassName() {
        return Algos.AlgoRotateText;
    }

    @Override
	protected void setInputOutput(){
	    input = new GeoElement[2];
	    input[0] = args;
	    input[1] = angle;


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
    	
    	boolean latex = args.isLaTeX();
    	
    	sb.setLength(0);
    	sb.append("\\rotatebox{");
    	sb.append(angle.getValue()*180/Math.PI); // convert to degrees
    	sb.append("}{ ");
    	if (!latex) sb.append("\\text{ ");
    	sb.append(args.getTextString());
    	if (!latex) sb.append(" } ");
    	sb.append(" }");

    	text.setTextString(sb.toString());
    	text.setLaTeX(true,false);
    }

}
