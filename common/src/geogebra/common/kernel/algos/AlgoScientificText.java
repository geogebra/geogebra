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
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.ScientificFormatAdapter;
import geogebra.common.util.Unicode;

public class AlgoScientificText extends AlgoElement {

	private GeoNumeric num, precision; //input
	private GeoText text; //output	

	protected StringBuilder sb = new StringBuilder();

	public AlgoScientificText(Construction cons, String label, GeoNumeric num, GeoNumeric precision) {
		this(cons, num, precision);
		text.setLabel(label);
	}

	AlgoScientificText(Construction cons, GeoNumeric num, GeoNumeric precision) {
		super(cons);
		this.num = num;
		this.precision = precision;

		text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		text.setLaTeX(true, false);

		setInputOutput();
		compute();
	}

	public AlgoScientificText(Construction cons) {
		super(cons);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoScientificText;
	}

	@Override
	protected void setInputOutput(){
		input = new GeoElement[2];
		input[0] = num;
		input[1] = precision;
		
		setOutputLength(1);
		setOutput(0, text);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns resulting text
	 * @return resulting text
	 */
	public GeoText getResult() {
		return text;
	}

	@Override
	public void compute() {   	
		StringTemplate tpl = StringTemplate.get(app.getFormulaRenderingType());
		if (num.isDefined() && precision.isDefined()) {

			sb.setLength(0);

			double decimal = num.getDouble();
			
			int prec = (int) precision.getDouble();
			if (prec < 1 || prec > 15) {
				text.setUndefined();
				return;				
			}

			StringTemplate stl = StringTemplate.printScientific(StringType.GEOGEBRA, prec,false);

			// returns string like 3456E-7
			String str = kernel.format(decimal, stl);

			String[] strs = str.split("E");

			if (strs.length != 2) {
				text.setUndefined();
				return;
			}
			
			sb.append(strs[0]);
			
			// remove . from end (if it's there)
			int l = sb.length();
			if (sb.charAt(l - 1) == '.') {
				sb.setLength(l - 1);
			}
			
			sb.append(" \\times ");
			sb.append("10");
			sb.append("^{");
			sb.append(strs[1]);
			sb.append("}");


			
			/* Unicode version. Doesn't work too well as Unicode.Superscript_0 is the wrong size
			sb.append(strs[0]);
			sb.append(Unicode.multiply);
			sb.append("10");
			//sb.append('^');
			//sb.append(strs[1]);

			for (int i = 0 ; i < strs[1].length() ; i++) {
				switch (strs[1].charAt(i)) {
				case '0' : sb.append(Unicode.Superscript_0); break;
				case '1' : sb.append(Unicode.Superscript_1); break;
				case '2' : sb.append(Unicode.Superscript_2); break;
				case '3' : sb.append(Unicode.Superscript_3); break;
				case '4' : sb.append(Unicode.Superscript_4); break;
				case '5' : sb.append(Unicode.Superscript_5); break;
				case '6' : sb.append(Unicode.Superscript_6); break;
				case '7' : sb.append(Unicode.Superscript_7); break;
				case '8' : sb.append(Unicode.Superscript_8); break;
				case '9' : sb.append(Unicode.Superscript_9); break;
				case '-' : sb.append(Unicode.Superscript_Minus); break;

				default:
					AbstractApplication.warn("Unexpected character in ScientificText[]");
					text.setUndefined();
					return;
				}
			}
			
			*/

			text.setTextString(sb.toString());
			text.setLaTeX(true, false);


		} else {
			text.setUndefined();
		}			
	}

	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

}
