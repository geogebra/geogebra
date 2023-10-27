package com.himamis.retex.editor.share.controller;

public enum ExpRelation {
	EMPTY("empty %0"), END_OF("end of %0"), START_OF("start of %0"), AFTER(
			"after %0"), BEFORE("before %0");

	private final String pattern;

	ExpRelation(String s) {
		this.pattern = s;
	}

	@Override
	public String toString() {
		return pattern;
	}

}
