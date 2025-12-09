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

package org.geogebra.web.full.gui;

import org.geogebra.common.gui.inputfield.AnsProvider;
import org.geogebra.common.gui.inputfield.HasLastItem;
import org.geogebra.web.editor.MathFieldProcessing;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;

public class AlgebraMathFieldProcessing extends MathFieldProcessing {

	private final RadioTreeItem avInput;
	private final AnsProvider ansProvider;

	/**
	 * @param textField AV input
	 * @param lastItemProvider last element provider
	 */
	public AlgebraMathFieldProcessing(RadioTreeItem textField, HasLastItem lastItemProvider) {
		super(textField.getMathField());
		ansProvider = lastItemProvider != null ? new AnsProvider(lastItemProvider) : null;
		avInput = textField;
	}

	@Override
	public void ansPressed() {
		if (!requestsAns()) {
			return;
		}
		boolean isInputInTextMode = mf.getInternal().getInputController().getPlainTextMode();
		String currentInput = mf.getText();
		String ans =
				isInputInTextMode
						? ansProvider.getAnsForTextInput(avInput.getGeo(), currentInput)
						: ansProvider.getAns(avInput.getGeo(), currentInput);
		mf.insertString(ans);
	}

	@Override
	public boolean requestsAns() {
		return ansProvider != null && avInput != null;
	}
}
