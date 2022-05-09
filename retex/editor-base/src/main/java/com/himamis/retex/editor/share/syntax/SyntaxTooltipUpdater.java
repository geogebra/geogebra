package com.himamis.retex.editor.share.syntax;

import com.himamis.retex.editor.share.syntax.SyntaxHint;

/**
 * Callback that notifies when the syntax hint changes.
 */
public interface SyntaxTooltipUpdater {

	/**
	 * Callback to notify when to update the syntax hint.
	 * @param hint
	 */
	void updateSyntaxTooltip(SyntaxHint hint);
}
