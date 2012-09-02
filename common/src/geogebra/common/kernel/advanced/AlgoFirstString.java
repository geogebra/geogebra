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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;

/**
 * Take first n objects from a list
 * 
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoFirstString extends AlgoElement {

	protected GeoText inputText; // input
	protected GeoNumeric n; // input
	protected GeoText outputText; // output
	protected int size;

	public AlgoFirstString(Construction cons, String label, GeoText inputText,
			GeoNumeric n) {
		super(cons);
		this.inputText = inputText;
		this.n = n;

		outputText = new GeoText(cons);
		outputText.setIsTextCommand(true);

		setInputOutput();
		compute();
		outputText.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoFirstString;
	}

	@Override
	protected void setInputOutput() {

		if (n != null) {
			input = new GeoElement[2];
			input[0] = inputText;
			input[1] = n;
		} else {
			input = new GeoElement[1];
			input[0] = inputText;
		}

        super.setOutputLength(1);
        super.setOutput(0, outputText);
		setDependencies(); // done by AlgoElement
	}

	public GeoText getResult() {
		return outputText;
	}

	@Override
	public void compute() {
		String str = inputText.getTextString();
		size = str.length();
		int outsize = n == null ? 1 : (int) n.getDouble();

		if (!inputText.isDefined() || size == 0 || outsize < 0
				|| outsize > size) {
			outputText.setUndefined();
			return;
		}

		if (outsize == 0)
			outputText.setTextString(""); // return empty string

		else
			outputText.setTextString(str.substring(0, outsize));
	}

	// TODO Consider locusequability

}
