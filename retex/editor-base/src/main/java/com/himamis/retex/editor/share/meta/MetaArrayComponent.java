package com.himamis.retex.editor.share.meta;

import java.io.Serializable;

public class MetaArrayComponent implements Serializable {
	private final char key;
	private final String tex;

	/**
	 * Symbol for opening / closing / splitting arrays.
	 * @param key plain text representation
	 * @param tex LaTeX representation
	 */
	public MetaArrayComponent(char key, String tex) {
		this.key = key;
		this.tex = tex;
	}

	public char getKey() {
		return key;
	}

	public String getTexName() {
		return tex;
	}
}
