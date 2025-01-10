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
