package com.himamis.retex.editor.share.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.himamis.retex.editor.share.meta.MetaCharacter;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

public class ArgumentHelperTest {

	private final MetaModel metaModel = new MetaModel();
	private final EditorState editorState = new EditorState(metaModel);

	private final MathCharacter mathCharacterOne =
			new MathCharacter(new MetaCharacter("1", '1', 1));
	private final MathCharacter mathCharacterTwo =
			new MathCharacter(new MetaCharacter("2", '2', 1));

	@Test
	public void testFractionNumerator() {
		MathFunction fraction = new MathFunction(metaModel.getGeneral(Tag.FRAC));
		MathSequence numerator = new MathSequence();
		numerator.addArgument(mathCharacterOne);

		fraction.setArgument(0, numerator);
		editorState.setCurrentField(numerator);
		ArgumentHelper.passArgument(editorState, fraction);

		assertEquals(mathCharacterOne, fraction.getArgument(0).getArgument(0));
	}

	@Test
	public void testMixedNumbers() {
		MathFunction mixedNumber = new MathFunction(metaModel.getGeneral(Tag.MIXED_NUMBER));
		MathSequence whole = new MathSequence();
		whole.addArgument(mathCharacterOne);
		MathSequence numerator = new MathSequence();
		numerator.addArgument(mathCharacterTwo);

		mixedNumber.setArgument(0, whole);
		editorState.setCurrentField(whole);
		ArgumentHelper.passArgument(editorState, mixedNumber);

		assertEquals(mathCharacterOne, mixedNumber.getArgument(0).getArgument(0));

		mixedNumber.setArgument(1, numerator);
		ArgumentHelper.passArgument(editorState, mixedNumber);

		assertEquals(mathCharacterTwo, mixedNumber.getArgument(1).getArgument(0));
	}

	@Test
	public void testRecurringDecimals() {
		MathFunction recurringDecimal =
				new MathFunction(metaModel.getGeneral(Tag.RECURRING_DECIMAL));
		MathSequence repeatingDigit = new MathSequence();
		repeatingDigit.addArgument(mathCharacterOne);

		recurringDecimal.setArgument(0, repeatingDigit);
		editorState.setCurrentField(repeatingDigit);
		ArgumentHelper.passSingleCharacter(editorState, repeatingDigit);

		assertEquals(mathCharacterOne, recurringDecimal.getArgument(0).getArgument(0));
	}
}
