package com.himamis.retex.editor.share.serializer;

import com.himamis.retex.editor.share.model.MathComponent;
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
	public static String fullDescription(MathComponent expr, SerializationAdapter adapter) {
		Atom atom = new TeXBuilder().build(expr.wrap(), null, false);
		return new TeXAtomSerializer(adapter).serialize(atom);
	}

}
