/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param text
	 *            input text
	 * @param num
	 *            index
	 */
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

		setOnlyOutput(textOut);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return result
	 */
	public GeoText getText() {
		return textOut;
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
