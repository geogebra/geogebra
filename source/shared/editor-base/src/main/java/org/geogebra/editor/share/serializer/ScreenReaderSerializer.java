/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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
