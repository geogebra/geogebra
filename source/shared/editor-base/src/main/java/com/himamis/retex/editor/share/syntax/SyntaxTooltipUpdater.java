package com.himamis.retex.editor.share.syntax;

/**
 * Callback that notifies when the syntax hint changes.
 */
public interface SyntaxTooltipUpdater {

	/**
	 * Callback to notify when to update the syntax hint.
	 * @param hint syntax hint
	 */
	void updateSyntaxTooltip(SyntaxHint hint);
}
