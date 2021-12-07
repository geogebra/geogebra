package com.himamis.retex.editor.share.model;

import com.himamis.retex.editor.share.model.inspect.Inspecting;
import com.himamis.retex.editor.share.model.traverse.Traversing;

public class MathPlaceholder extends MathComponent {

	private final String content;

	public MathPlaceholder(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	@Override
	public MathPlaceholder copy() {
		return new MathPlaceholder(content);
	}

	@Override
	public boolean inspect(Inspecting inspecting) {
		return inspecting.check(this);
	}

	@Override
	public MathComponent traverse(Traversing traversing) {
		return traversing.process(this);
	}
}
