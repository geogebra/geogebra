package com.himamis.retex.renderer.share.serialize;

import org.jspecify.annotations.Nullable;

import com.himamis.retex.renderer.share.Atom;

public interface IsAccentedAtom {

	/**
	 * @return LaTeX command defining this atom
	 */
	@Nullable String getCommand();

	Atom getTrueBase();

	/**
	 * @return accent
	 */
	Atom getAccent();
}
