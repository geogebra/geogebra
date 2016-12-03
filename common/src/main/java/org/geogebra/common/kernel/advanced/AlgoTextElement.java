/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * n-th element of a GeoList object.
 * 
 * Note: the type of the returned GeoElement object is determined by the type of
 * the first list element. If the list is initially empty, a GeoNumeric object
 * is created for element.
 * 
 * @author Michael
 * @version 20100205
 */

public class AlgoTextElement extends AlgoElement {

	private GeoText text; // input
	private GeoNumberValue num = null; // input
	private GeoElement numGeo;
	private GeoText textOut; // output

	public AlgoTextElement(Construction cons, String label, GeoText text,
			GeoNumberValue num) {
		super(cons);
		this.text = text;
		this.num = num;
		numGeo = num.toGeoElement();

		textOut = new GeoText(cons);
		textOut.setIsTextCommand(true);

		setInputOutput();
		compute();
		textOut.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Element;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[2];
		input[0] = text;
		input[1] = numGeo;

		super.setOutputLength(1);
		super.setOutput(0, textOut);
		setDependencies(); // done by AlgoElement
	}

	public GeoText getText() {
		return text;
	}

	@Override
	public final void compute() {
		if (!numGeo.isDefined() || !text.isDefined()) {
			textOut.setUndefined();
			return;
		}

		String str = text.getTextString();
		int n = (int) num.getNumber().getDouble();
		if (n < 1 || n > str.length()) {
			textOut.setUndefined();
		} else {
			textOut.setTextString(str.charAt(n - 1) + "");
		}
	}

	

}
