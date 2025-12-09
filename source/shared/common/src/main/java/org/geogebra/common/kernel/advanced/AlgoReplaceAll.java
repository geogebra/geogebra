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
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

public class AlgoReplaceAll extends AlgoElement {

	private GeoText inputText;
	private GeoText textToMatch;
	private GeoText textToReplace;
	private GeoText replacedText;

	/**
	 * constructor for replace all command
	 * @param cons construction
	 * @param inputText input text
	 * @param textToMatch text to find in input
	 * @param textToReplace replacement text
	 */
	public AlgoReplaceAll(Construction cons, GeoText inputText,
			GeoText textToMatch, GeoText textToReplace) {
		super(cons);
		this.inputText = inputText;
		this.textToMatch = textToMatch;
		this.textToReplace = textToReplace;
		replacedText = new GeoText(cons);
		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[]{inputText, textToMatch, textToReplace};
		setOnlyOutput(replacedText);
		setDependencies();
	}

	@Override
	public void compute() {
		String inputStr = inputText.getTextString();
		if (inputStr != null) {
			inputStr = inputStr.replaceAll(textToMatch.getEscapedSpecialCharsString(),
					textToReplace.getTextString());
			replacedText.setTextString(inputStr);
		}
	}

	@Override
	public GetCommand getClassName() {
		return Commands.ReplaceAll;
	}
}
