package com.himamis.retex.renderer.share.platform.box;

import com.himamis.retex.renderer.share.Box;

/**
 * Decorates a box.
 */
public interface BoxDecorator {

	/**
	 * @param box The returned decorated box is based on this one.
	 * @return A decorated box.
	 */
	Box decorate(Box box);
}
