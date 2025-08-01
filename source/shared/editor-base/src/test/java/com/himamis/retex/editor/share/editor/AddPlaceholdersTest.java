package com.himamis.retex.editor.share.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;

public class AddPlaceholdersTest {
	private final Parser parser = new Parser(new MetaModel());
	private final AddPlaceholders placeholders = new AddPlaceholders();

	@Test
	public void testEmptyPoint() {
		MathFormula formula = getFormula("(?,?)");
		MathSequence rootSequence = formula.getRootComponent();
		MathArray mathArray = (MathArray) (rootSequence.getArgument(0));
		placeholders.process(mathArray);
		assertEquals("MathArray[MathSequence[MathCharPlaceholder[], ,,"
				+ " MathCharPlaceholder[]]]", mathArray.toString());
	}

	@Test
	public void testHalfEmptyPoint() {
		MathFormula formula = getFormula("(1,?)");
		MathSequence rootSequence = formula.getRootComponent();
		MathArray mathArray = (MathArray) (rootSequence.getArgument(0));
		placeholders.process(mathArray);
		assertEquals("MathArray[MathSequence[1, ,,"
				+ " MathCharPlaceholder[]]]", mathArray.toString());
	}

	@Test
	public void deepFractionsShouldParseAndSerializeFast() {
		int depth = 15;
		long start = System.currentTimeMillis();
		MathFormula formula = getFormula("1/(".repeat(depth) + "1" + ")".repeat(depth));
		placeholders.process(formula.getRootComponent());
		assertEquals("((1)/(".repeat(depth) + "1" + "))".repeat(depth),
				GeoGebraSerializer.serialize(formula.getRootComponent(), (EditorFeatures) null));
		assertTrue(System.currentTimeMillis() - start < 1000, "Traversing too slow");
	}

	private MathFormula getFormula(String text) {
		try {
			return parser.parse(text);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
