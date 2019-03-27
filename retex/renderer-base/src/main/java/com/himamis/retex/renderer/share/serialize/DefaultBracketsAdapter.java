package com.himamis.retex.renderer.share.serialize;

public class DefaultBracketsAdapter implements BracketsAdapterI {

	@Override
	public String subscriptContent(String sub) {
		if (sub.length() > 1) {
			return "_{" + sub + "}";
		}
		return "_" + sub;
	}

	@Override
	public String transformBrackets(String left, String base, String right) {
		return left + base + right;
	}
}
