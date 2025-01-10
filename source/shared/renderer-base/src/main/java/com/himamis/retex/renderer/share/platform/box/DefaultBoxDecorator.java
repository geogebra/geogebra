package com.himamis.retex.renderer.share.platform.box;

import com.himamis.retex.renderer.share.Box;

/**
 * Doesn't decorate the box, but simply returns the original one.
 */
public class DefaultBoxDecorator implements BoxDecorator {

	@Override
	public Box decorate(Box box) {
		return box;
	}
}
