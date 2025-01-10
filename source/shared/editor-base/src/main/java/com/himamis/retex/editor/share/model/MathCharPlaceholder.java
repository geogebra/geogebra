package com.himamis.retex.editor.share.model;

public class MathCharPlaceholder extends MathPlaceholder {

	public MathCharPlaceholder() {
		super("");
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[]";
	}
}
