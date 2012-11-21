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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.util.Unicode;


public class AlgoFractionText extends AlgoElement {

	private GeoNumberValue num; //input
    private GeoText text; //output	
    
    private double frac[] = {0,0};
 
    private StringBuilder sb = new StringBuilder();
    
    public AlgoFractionText(Construction cons, String label, GeoNumberValue num) {
    	this(cons, num);
        text.setLabel(label);
    }

    AlgoFractionText(Construction cons, GeoNumberValue num) {
        super(cons);
        this.num = num;
               
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
        input[0] = num.toGeoElement();

        setOutputLength(1);
        setOutput(0, text);
        setDependencies(); // done by AlgoElement
    }

    public GeoText getResult() {
        return text;
    }

    @Override
	public void compute() {
    	StringTemplate tpl = StringTemplate.get(app.getFormulaRenderingType());
		if (input[0].isDefined()) {
			frac = DecimalToFraction(num.getDouble(),Kernel.STANDARD_PRECISION);
			
			switch (app.getFormulaRenderingType()) {
			case MATHML:
				if (frac[1] == 1) { // integer
					sb.setLength(0);
					sb.append("<cn>");
					sb.append(kernel.format(frac[0],tpl));				
					sb.append("</cn>");
					text.setTextString(sb.toString());
				} else if (frac[1] == 0) { // 1 / 0 or -1 / 0
			    	if (frac[0] < 0) {
			    		text.setTextString("<apply><minus/><infinity/></apply>");		
			    	} else {
			    		text.setTextString("<infinity/>");
			    	}
				} else {
					sb.setLength(0);
			    	sb.append("<apply><divide/><cn>");
			    	// checkDecimalFraction() needed for eg FractionText[20.0764]
			    	sb.append(kernel.format(Kernel.checkDecimalFraction(frac[0]),tpl));
			    	sb.append("</cn><cn>");
			    	sb.append(kernel.format(Kernel.checkDecimalFraction(frac[1]),tpl));
			    	sb.append("</cn></apply>");
			    	
			    	text.setTextString(sb.toString());
				}
		    	break;
			case LATEX:
				sb.setLength(0);
				appendLaTeX(sb, frac, tpl, kernel);			    	
		    	text.setTextString(sb.toString());
		    	break;
				
			}
	    	text.setLaTeX(true,false);
			

		} else
			text.setTextString(app.getPlain("Undefined"));
	}
  
    /*	Algorithm To Convert A Decimal To A Fraction
     * by
     * John Kennedy
     * Mathematics Department
     * Santa Monica College
     * 1900 Pico Blvd.
     * Santa Monica, CA 90405
     * http://homepage.smc.edu/kennedy_john/DEC2FRAC.PDF
     */
	public static double[] DecimalToFraction(double decimal, double AccuracyFactor) {
	double FractionNumerator, FractionDenominator;
	double DecimalSign;
	double Z;
	double PreviousDenominator;
	double ScratchValue;
	
	
	double ret[] = {0,0};
	if (Double.isNaN(decimal)) return ret; // return 0/0 
	
	if (decimal == Double.POSITIVE_INFINITY) {
	  ret[0] = 1;
	  ret[1] = 0 ; // 1/0
	  return ret;
	}
	if (decimal == Double.NEGATIVE_INFINITY) {
		ret[0] = -1;
		ret[1] = 0; // -1/0
		return ret;
	}
	
	if (decimal < 0.0) DecimalSign = -1.0; else DecimalSign = 1.0;
	
	decimal = Math.abs(decimal);
	
	if (Math.abs(decimal - Math.floor(decimal)) < AccuracyFactor) { // handles exact integers including 0 �
		FractionNumerator = decimal * DecimalSign;
		FractionDenominator = 1.0;
		
		ret[0] = FractionNumerator;
		ret[1] = FractionDenominator;
		return ret;
	}
	if (decimal < 1.0E-19) { // X = 0 already taken care of �
		FractionNumerator = DecimalSign;
		FractionDenominator = 9999999999999999999.0;
		
		ret[0] = FractionNumerator;
		ret[1] = FractionDenominator;
		return ret;
	}
	if (decimal > 1.0E19) {
		FractionNumerator = 9999999999999999999.0 * DecimalSign;
		FractionDenominator = 1.0;
		
		ret[0] = FractionNumerator;
		ret[1] = FractionDenominator;
		return ret;
	}
	
	Z = decimal;
	PreviousDenominator = 0.0;
	FractionDenominator = 1.0;
	do {
		Z = 1.0/(Z - Math.floor(Z));
		ScratchValue = FractionDenominator;
		FractionDenominator = FractionDenominator * Math.floor(Z) + PreviousDenominator;
		PreviousDenominator = ScratchValue;
		FractionNumerator = Math.floor(decimal * FractionDenominator + 0.5); // Rounding Function
	} while ( Math.abs((decimal - (FractionNumerator /FractionDenominator))) > AccuracyFactor && Z != Math.floor(Z));
	FractionNumerator = DecimalSign*FractionNumerator;
	
	ret[0] = FractionNumerator;
	ret[1] = FractionDenominator;
	return ret;
	}
	
	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}
	
	public static void appendLaTeX(StringBuilder sb, double[] num, StringTemplate tpl, Kernel kernel) {
		if (num[1] == 1) { // integer
	    	sb.append(kernel.format(num[0],tpl));				
		} else if (num[1] == 0) { // 1 / 0 or -1 / 0

			if (num[0] < 0) {
				sb.append('-');
			}
			sb.append(Unicode.Infinity);

		} else {
	    	sb.append("\\frac{");
	    	// checkDecimalFraction() needed for eg FractionText[20.0764]
	    	sb.append(kernel.format(Kernel.checkDecimalFraction(num[0]),tpl));
	    	sb.append("}{");
	    	sb.append(kernel.format(Kernel.checkDecimalFraction(num[1]),tpl));
	    	sb.append("}");
	    	
		}
		
	}

	// TODO Consider locusequability


}
