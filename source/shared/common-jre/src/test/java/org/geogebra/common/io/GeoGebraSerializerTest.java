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

package org.geogebra.common.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.editor.EditorFeatures;
import org.geogebra.editor.share.io.latex.ParseException;
import org.geogebra.editor.share.io.latex.Parser;
import org.geogebra.editor.share.serializer.GeoGebraSerializer;
import org.geogebra.editor.share.tree.Formula;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.SequenceNode;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

class GeoGebraSerializerTest {

	@Test
	@Issue("APPS-8196")
	void serializeShouldNotSearchParentForEachCharacter() throws ParseException {
		IndexCountingSequenceNode sequence = new IndexCountingSequenceNode();
		for (Node child : parse("a,b,c+d").getRootNode()) {
			sequence.addChild(child);
		}
		assertEquals("a,b,c+d", GeoGebraSerializer.serialize(sequence, (EditorFeatures) null));
		assertEquals(0, sequence.indexOfCalls);
	}

	@Test
	void primeAfterLetterShouldSerializeAsDerivative() throws ParseException {
		Formula formula = parse("f′(x)+g″(x)");
		assertEquals(
				"f'(x)+g''(x)", GeoGebraSerializer.serialize(formula.getRootNode(), (EditorFeatures) null));
	}

	private Formula parse(String text) throws ParseException {
		return new Parser(new TemplateCatalog()).parse(text);
	}

	private static final class IndexCountingSequenceNode extends SequenceNode {
		private int indexOfCalls;

		@Override
		public int indexOf(Node child) {
			indexOfCalls++;
			return super.indexOf(child);
		}
	}
}
