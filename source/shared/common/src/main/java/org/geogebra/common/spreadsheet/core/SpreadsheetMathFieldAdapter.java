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

package org.geogebra.common.spreadsheet.core;

import javax.annotation.Nonnull;

import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.editor.MathFieldInternalListener;
import org.geogebra.editor.share.editor.UnhandledArrowListener;
import org.geogebra.editor.share.event.KeyEvent;
import org.geogebra.editor.share.event.MathFieldListener;
import org.geogebra.editor.share.util.JavaKeyCodes;

/**
 * Adapts between a MathFieldInternal, spreadsheet input processing, and the SpreadsheetController.
 */
final class SpreadsheetMathFieldAdapter implements MathFieldListener, UnhandledArrowListener,
		MathFieldInternalListener {

	private final MathFieldInternal mathField;
	private final int row, column;
	private final SpreadsheetCellProcessor cellProcessor;
	private final SpreadsheetController spreadsheetController;

	/**
	 * @param mathField math input
	 *
	 */
	SpreadsheetMathFieldAdapter(@Nonnull MathFieldInternal mathField,
			int row, int column,
			@Nonnull SpreadsheetCellProcessor cellProcessor,
			@Nonnull SpreadsheetController spreadsheetController) {
		this.mathField = mathField;
		this.row = row;
		this.column = column;
		this.cellProcessor = cellProcessor;
		this.spreadsheetController = spreadsheetController;
	}

	void commitInput() {
		cellProcessor.process(mathField.getText(), row, column);
	}

	@Override
	public void onEnter() {
		if (spreadsheetController.handleKeyPress(JavaKeyCodes.VK_ENTER)) {
			return;
		}
		spreadsheetController.onEnter();
	}

	@Override
	public boolean onEscape() {
		if (spreadsheetController.handleKeyPress(JavaKeyCodes.VK_ESCAPE)) {
			return true;
		}
		spreadsheetController.onEsc();
		return true;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		if (spreadsheetController.handleKeyPress(JavaKeyCodes.VK_TAB)) {
			return true;
		}
		spreadsheetController.onTab();
		return true;
	}

	@Override
	public void onKeyTyped(String key) {
		// TODO scroll cursor into view?
		// note: this is fired *before* actually handling the input & updating the internal state
		spreadsheetController.onEditorTextChanged();
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		// TODO scroll cursor into view?
		// note: this is fired *before* actually handling the input & updating the internal state
		return spreadsheetController.handleKeyPress(keyCode);
	}

	@Override
	public void onArrow(int keyCode, KeyEvent.KeyboardType type) {
		if (type != KeyEvent.KeyboardType.INTERNAL) {
			spreadsheetController.onArrow(keyCode);
		}
	}

	@Override
	public void inputChanged(MathFieldInternal mathFieldInternal) {
		// this can be any change: text changed, cursor movement, ...
		spreadsheetController.onEditorTextOrCursorPositionChanged();
	}
}
