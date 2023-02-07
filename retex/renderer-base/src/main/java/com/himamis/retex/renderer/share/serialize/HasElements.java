package com.himamis.retex.renderer.share.serialize;

import com.himamis.retex.renderer.share.Atom;

public interface HasElements {

	/**
	 * @param index index
	 * @return element at given index, null if index too big
	 */
	Atom getElement(int index);
}
