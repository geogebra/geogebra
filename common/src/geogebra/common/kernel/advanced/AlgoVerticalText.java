/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;


/**
 * Algorithm for vertical text
 * @author Michael
 */
public class AlgoVerticalText extends AlgoElement {

	private GeoText text; //output	
    private GeoText args; //input	
    private GeoPoint startPoint, startPointCopy; // optional input
    
    private StringBuilder sb = new StringBuilder();
    
    
    /**
     * Creates new algo for vertical text
     * @param cons construction
     * @param label label for output
     * @param args input text
     */
    public AlgoVerticalText(Construction cons, String label, GeoText args) {
    	this(cons,  args, null);
        text.setLabel(label);
    }
    
    /**
     * Creates new unlabeled algo for vertical text
     * @param cons construction
     * @param label label for output
     * @param args input text
     */
    public AlgoVerticalText(Construction cons, GeoText args) {
    	this(cons,  args, null);
    }
    
    
    
    /**
     * Creates new algo for vertical text, with fixed position point
     * @param cons construction
     * @param label label for output
     * @param args input text
     */
    public AlgoVerticalText(Construction cons, String label, GeoText args,  GeoPoint p) {
    	this(cons,  args, p);
        text.setLabel(label);
    }

    /**
     * Creates new unlabeled algo for vertical text, with fixed position point
     * @param cons construction
     * @param args input text
     */
    public AlgoVerticalText(Construction cons, GeoText args,  GeoPoint p) {
        super(cons);
        
        this.args = args;
        this.startPoint = p;
               
        text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		
		// set startpoint
		if (startPoint != null) {
			startPointCopy = (GeoPoint) startPoint.copyInternal(cons);

			try {
				text.setStartPoint(startPointCopy);
			} catch (CircularDefinitionException e) {
				e.printStackTrace();
			}
			text.setAlwaysFixed(true); // disable dragging if p != null
		}
		
        setInputOutput();
        compute();    
    }

    @Override
	public Commands getClassName() {
        return Commands.VerticalText;
    }

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = args;
		args.addTextDescendant(text);
		if (startPoint == null) {
			input = new GeoElement[1];
			input[0] = args;

		} else {
			input = new GeoElement[2];
			input[0] = args;
			input[1] = startPoint;
		}

		super.setOutputLength(1);
		super.setOutput(0, text);
		setDependencies(); // done by AlgoElement
	}

    /**
     * @return resulting text
     */
    public GeoText getResult() {
        return text;
    }

    @Override
	public final void compute() {
		if (!args.isDefined()
				|| (startPoint != null && !startPoint.isDefined())) {
			text.setTextString("");
			return;
		}
    	
    	sb.setLength(0);
    	AlgoRotateText.appendRotatedText(sb, args, 90);
    	
    	text.setTextString(sb.toString());
    	text.setLaTeX(true,false);

		// update startpoint position of text
		if (startPointCopy != null) {
			startPointCopy.setCoords(startPoint);
		}
    			
    }

	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

	// TODO Consider locusequability

}
