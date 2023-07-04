package com.himamis.retex.editor.share.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.himamis.retex.editor.share.meta.MetaCharacter;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

/**
 * Test class for the ArgumentHelper <br>
 * Note: In EditorParserTest numerous specific inputs are tested for correct
 * parsing and serialization. These tests also make use of the ArgumentHelper when parsing
 * fractions, mixed numbers, and recurring decimals.
 */
public class ArgumentHelperTest {

	private final MetaModel metaModel = new MetaModel();
	private final EditorState editorState = new EditorState(metaModel);

	private final MathCharacter mathCharacterOne =
			new MathCharacter(new MetaCharacter("1", '1', 1));
	private final MathCharacter mathCharacterTwo =
			new MathCharacter(new MetaCharacter("2", '2', 1));

	/**
	 * Checks whether parsing e.g. 1&nbsp;&nbsp;&nbsp;&nbsp;/2 deletes all whitespace characters
	 */
	@Test
	public void shouldNotParseWhitespaces() {
		MathFunction fraction;
		MathSequence numerator;

		//Test if whitespace and horizontal tab are passed
		MathCharacter whitespace =
				new MathCharacter(new MetaCharacter(" ", ' ', 1));
		MathCharacter horizontalTab =
				new MathCharacter(new MetaCharacter(Character.toString((char) 9), (char) 9, 1));

		numerator = new MathSequence();
		numerator.addArgument(mathCharacterOne);
		numerator.addArgument(whitespace);
		numerator.addArgument(horizontalTab);

		fraction = new MathFunction(metaModel.getGeneral(Tag.FRAC));
		fraction.setArgument(0, numerator);

		editorState.setCurrentField(numerator);
		editorState.setCurrentOffset(numerator.size());
		ArgumentHelper.passArgument(editorState, fraction);

		//There should be no whitespaces passed
		assertEquals(mathCharacterOne, fraction.getArgument(0).getArgument(0));
		assertEquals(1, fraction.getArgument(0).size());
		assertNull(fraction.getArgument(1));
	}

	/**
	 * Checks whether one, and only one, single character gets passed
	 * @see ArgumentHelper#passSingleCharacter(EditorState, MathSequence)
	 */
	@Test
	public void passOnlySingleCharacter() {
		// Create a MathSequence with two arguments (MathCharacter)
		MathSequence passFrom = new MathSequence();
		passFrom.addArgument(mathCharacterOne);
		passFrom.addArgument(mathCharacterTwo);

		MathSequence passTo = new MathSequence();
		editorState.setCurrentField(passFrom);
		editorState.setCurrentOffset(passFrom.size());

		ArgumentHelper.passSingleCharacter(editorState, passTo);

		//Expecting only one character to be passed
		assertEquals(1, passTo.size());
		assertEquals(mathCharacterTwo, passTo.getArgument(0));
	}

	/**
	 * Checks whether the tested method reads only MathCharacters and not e.g. MathFunctions
	 * @see ArgumentHelper#readCharacters(EditorState, int)
	 */
	@Test
	public void readOnlyCharacters() {
		MathSequence sequence = new MathSequence();
		sequence.addArgument(new MathFunction(metaModel.getGeneral(Tag.LIM_EQ)));
		sequence.addArgument(mathCharacterOne);
		sequence.addArgument(mathCharacterTwo);

		editorState.setCurrentField(sequence);

		// "12" is expected since we do not want to read anything other than MathCharacters
		assertEquals("12", ArgumentHelper.readCharacters(editorState, sequence.size()));

		sequence.addArgument(new MathSequence());

		// Empty string expected, since the last argument is not a MathCharacter
		assertTrue(ArgumentHelper.readCharacters(editorState, sequence.size()).isEmpty());
	}
}