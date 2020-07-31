package com.himamis.retex.editor.web;

import com.himamis.retex.editor.share.model.Korean;

/**
 * Composition handler for math input
 * 
 * @author Zbynek
 */
final class EditorCompositionHandler
		implements CompositionHandler, CompositionEndHandler {
	private MathFieldW editor;
	private boolean insertOnEnd = false;

	/**
	 * @param editor
	 *            equation editor
	 */
	public EditorCompositionHandler(MathFieldW editor) {
		this.editor = editor;
	}

	@Override
	public void onCompositionUpdate(CompositionUpdateEvent event) {
		// this works fine for Korean as the editor has support for
		// combining Korean characters
		// but for eg Japanese probably will need to hook into
		// compositionstart & compositionend events as well

		// in Chrome typing fast gives \u3137\uB450
		// instead of \u3137\u315C
		// so flatten the result and send just the last character
		String data = Korean.flattenKorean(event.getData());
		insertOnEnd = false;
		// ^: fix for swedish
		if (!"^".equals(data) && data.length() > 0) {
			char inputChar = data.charAt(data.length() - 1);
			char jamo = Korean.convertToCompatibilityJamo(inputChar);
			if (Korean.isCompatibilityChar(jamo)
					|| Korean.isSingleKoreanChar(jamo)) {
				editor.insertString("" + jamo);
			} else {
				insertOnEnd = true;
			}

		}
	}

	@Override
	public void onCompositionEnd(CompositionEndEvent event) {
		if (insertOnEnd) {
			// inserted string should only depend on `compositionend`
			// in Safari the data in `cmpositionupdate` is just Latin chars
			editor.insertString(event.getData());
			editor.getInternal().notifyAndUpdate(event.getData());
		}
	}
}