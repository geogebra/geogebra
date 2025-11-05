package org.geogebra.editor.share.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.io.latex.ParseException;
import org.geogebra.editor.share.io.latex.Parser;
import org.geogebra.editor.share.serializer.GeoGebraSerializer;
import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.Formula;
import org.geogebra.editor.share.tree.SequenceNode;
import org.junit.jupiter.api.Test;

public class AddPlaceholdersTest {
	private final Parser parser = new Parser(new TemplateCatalog());
	private final AddPlaceholders placeholders = new AddPlaceholders();

	@Test
	public void testEmptyPoint() {
		Formula formula = getFormula("(?,?)");
		SequenceNode rootSequence = formula.getRootNode();
		ArrayNode arrayNode = (ArrayNode) (rootSequence.getChild(0));
		placeholders.process(arrayNode);
		assertEquals("ArrayNode[SequenceNode[CharPlaceholderNode[], ,,"
				+ " CharPlaceholderNode[]]]", arrayNode.toString());
	}

	@Test
	public void testHalfEmptyPoint() {
		Formula formula = getFormula("(1,?)");
		SequenceNode rootSequence = formula.getRootNode();
		ArrayNode arrayNode = (ArrayNode) (rootSequence.getChild(0));
		placeholders.process(arrayNode);
		assertEquals("ArrayNode[SequenceNode[1, ,,"
				+ " CharPlaceholderNode[]]]", arrayNode.toString());
	}

	@Test
	public void deepFractionsShouldParseAndSerializeFast() {
		int depth = 15;
		long start = System.currentTimeMillis();
		Formula formula = getFormula("1/(".repeat(depth) + "1" + ")".repeat(depth));
		placeholders.process(formula.getRootNode());
		assertEquals("((1)/(".repeat(depth) + "1" + "))".repeat(depth),
				GeoGebraSerializer.serialize(formula.getRootNode(), (EditorFeatures) null));
		assertTrue(System.currentTimeMillis() - start < 1000, "Traversing too slow");
	}

	private Formula getFormula(String text) {
		try {
			return parser.parse(text);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
