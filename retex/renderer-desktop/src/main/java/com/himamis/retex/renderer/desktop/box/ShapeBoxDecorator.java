package com.himamis.retex.renderer.desktop.box;

import com.himamis.retex.renderer.share.Box;
import com.himamis.retex.renderer.share.ShapeBox;
import com.himamis.retex.renderer.share.platform.box.BoxDecorator;

/**
 * Creates a ShapeBox.
 */
public class ShapeBoxDecorator implements BoxDecorator {

	@Override
	public Box decorate(Box box) {
		return ShapeBox.create(box);
	}
}
