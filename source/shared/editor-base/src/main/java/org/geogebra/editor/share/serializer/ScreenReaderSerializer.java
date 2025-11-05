/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.serializer;

import org.geogebra.editor.share.tree.Node;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.serialize.SerializationAdapter;
import com.himamis.retex.renderer.share.serialize.TeXAtomSerializer;

/**
 * Utility class for screen reader serialization.
 *
 */
public class ScreenReaderSerializer {

	/**
	 * @param expr
	 *            part of editor content
	 * @return expression description
	 */
	public static String fullDescription(Node expr, SerializationAdapter adapter) {
		Atom atom = new TeXBuilder().build(expr, null, 0, false);
		return new TeXAtomSerializer(adapter).serialize(atom);
	}

}
