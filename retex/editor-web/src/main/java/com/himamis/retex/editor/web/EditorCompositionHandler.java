package com.himamis.retex.editor.web;

import com.himamis.retex.editor.share.model.Korean;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

final class EditorCompositionHandler
		implements CompositionHandler, CompositionEndHandler {
	private MathFieldW editor;
	private String toInsert = null;

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
		toInsert = null;
		// ^: fix for swedish
		if (!"^".equals(data) && data.length() > 0) {
			char inputChar = data.charAt(data.length() - 1);
			char jamo = Korean.convertToCompatibilityJamo(inputChar);
			if (Korean.isCompatibilityChar(jamo)
					|| Korean.isSingleKoreanChar(jamo)) {
				editor.insertString("" + jamo);
			} else {
				toInsert = "" + jamo;
			}

		}
	}

	@Override
	public void onCompositionEnd(CompositionEndEvent event) {
		FactoryProvider.debugS("ENDING" + event.getData());
		if (toInsert != null) {
			editor.insertString(toInsert);
		}
	}
}