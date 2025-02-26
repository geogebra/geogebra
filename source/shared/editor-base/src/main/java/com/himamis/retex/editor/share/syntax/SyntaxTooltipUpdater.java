package com.himamis.retex.editor.share.syntax;

import javax.annotation.Nonnull;

/**
 * Callback that notifies when the syntax hint changes.
 */
public interface SyntaxTooltipUpdater {

	/**
	 * Callback to notify when to update the syntax hint.
	 * @param hint syntax hint
	 */
	void updateSyntaxTooltip(@Nonnull SyntaxHint hint);
}
