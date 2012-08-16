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
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.main.App;


public class AlgoFractionTextPoint extends AlgoElement {

	private GeoPointND p; //input
    private GeoText text; //output	
    
    private double xCoord[] = {0,0};
    private double yCoord[] = {0,0};
    private double zCoord[] = {0,0};
 
    private StringBuilder sb = new StringBuilder();
    
    public AlgoFractionTextPoint(Construction cons, String label, GeoPointND p) {
    	this(cons, p);
        text.setLabel(label);
    }

    AlgoFractionTextPoint(Construction cons, GeoPointND p) {
        super(cons);
        this.p = p;
               
        text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		
        setInputOutput();
        compute();
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoFractionText;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = (GeoElement) p;

        setOutputLength(1);
        setOutput(0, text);
        setDependencies(); // done by AlgoElement
    }

    public GeoText getResult() {
        return text;
    }

    @Override
	public final void compute() {
    	StringTemplate tpl = StringTemplate.get(app.getFormulaRenderingType());
		if (input[0].isDefined()) {
			
			Coords coords = p.getInhomCoords();
			
			xCoord = AlgoFractionText.DecimalToFraction(coords.getX(),Kernel.STANDARD_PRECISION);
			yCoord = AlgoFractionText.DecimalToFraction(coords.getY(),Kernel.STANDARD_PRECISION);
			zCoord = AlgoFractionText.DecimalToFraction(coords.getZ(),Kernel.STANDARD_PRECISION);
			
			switch (app.getFormulaRenderingType()) {
			case MATHML:
				sb.setLength(0);
				App.warn("Not implemented");
				break;
			case LATEX:
				
				sb.setLength(0);
				sb.append("{ \\left( ");
				AlgoFractionText.appendLaTeX(sb, xCoord, tpl, kernel);
				sb.append(',');
				AlgoFractionText.appendLaTeX(sb, yCoord, tpl, kernel);
				if (p.getDimension() == 3) {
					sb.append(',');
					AlgoFractionText.appendLaTeX(sb, zCoord, tpl, kernel);
				}
				sb.append(" \\right) }");
				
				text.setTextString(sb.toString());
		    	break;
				
			}
	    	text.setLaTeX(true,false);
			

		} else
			text.setTextString(app.getPlain("undefined"));
	}
  	
	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

	// TODO Consider locusequability

}
