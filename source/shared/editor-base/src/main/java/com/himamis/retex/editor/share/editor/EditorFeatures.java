package com.himamis.retex.editor.share.editor;

/**
 * Editor features that can be enabled/disabled at run time.
 */
public class EditorFeatures {

	private boolean mixedNumbersEnabled = true;

	public boolean areMixedNumbersEnabled() {
		return mixedNumbersEnabled;
	}

	public void setMixedNumbersEnabled(boolean mixedNumbersEnabled) {
		this.mixedNumbersEnabled = mixedNumbersEnabled;
	}
}
