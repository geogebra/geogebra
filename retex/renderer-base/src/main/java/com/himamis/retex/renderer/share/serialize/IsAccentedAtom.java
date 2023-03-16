package com.himamis.retex.renderer.share.serialize;

import com.himamis.retex.renderer.share.Atom;

public interface IsAccentedAtom {
	Atom getTrueBase();


	/**
	 * @return accent
	 */
	Atom getAccent();
}
