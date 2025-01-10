package com.himamis.retex.renderer.share.serialize;

import javax.annotation.CheckForNull;

import com.himamis.retex.renderer.share.Atom;

public interface IsAccentedAtom {

	/**
	 * @return LaTeX command defining this atom
	 */
	@CheckForNull String getCommand();

	Atom getTrueBase();

	/**
	 * @return accent
	 */
	Atom getAccent();
}
