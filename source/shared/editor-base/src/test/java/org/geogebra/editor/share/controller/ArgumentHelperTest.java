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

package org.geogebra.editor.share.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.editor.share.catalog.CharacterTemplate;
import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.SequenceNode;
import org.junit.jupiter.api.Test;

/**
 * Test class for the ArgumentHelper <br>
 * Note: In EditorParserTest numerous specific inputs are tested for correct
 * parsing and serialization. These tests also make use of the ArgumentHelper when parsing
 * fractions, mixed numbers, and recurring decimals.
 */
public class ArgumentHelperTest {

	private final TemplateCatalog templateCatalog = new TemplateCatalog();
	private final EditorState editorState = new EditorState(templateCatalog);

	private final CharacterNode characterNodeOne =
			new CharacterNode(new CharacterTemplate("1", '1', 1));
	private final CharacterNode characterNodeTwo =
			new CharacterNode(new CharacterTemplate("2", '2', 1));

	/**
	 * Checks whether parsing e.g. 1&nbsp;&nbsp;&nbsp;&nbsp;/2 deletes all whitespace characters
	 */
	@Test
	public void shouldNotParseWhitespaces() {
		FunctionNode fraction;
		SequenceNode numerator;

		//Test if whitespace and horizontal tab are passed
		CharacterNode whitespace =
				new CharacterNode(new CharacterTemplate(" ", ' ', 1));
		CharacterNode horizontalTab =
				new CharacterNode(
						new CharacterTemplate(java.lang.Character.toString((char) 9), (char) 9, 1));

		numerator = new SequenceNode();
		numerator.addChild(characterNodeOne);
		numerator.addChild(whitespace);
		numerator.addChild(horizontalTab);

		fraction = new FunctionNode(templateCatalog.getGeneral(Tag.FRAC));
		fraction.setChild(0, numerator);

		editorState.setCurrentNode(numerator);
		editorState.setCurrentOffset(numerator.size());
		ArgumentHelper.passArgument(editorState, fraction);

		//There should be no whitespaces passed
		assertEquals(characterNodeOne, fraction.getChild(0).getChild(0));
		assertEquals(1, fraction.getChild(0).size());
		assertNull(fraction.getChild(1));
	}

	/**
	 * Checks whether one, and only one, single character gets passed
	 * @see ArgumentHelper#passSingleCharacter(EditorState, SequenceNode)
	 */
	@Test
	public void passOnlySingleCharacter() {
		// Create a sequence node with two characters
		SequenceNode passFrom = new SequenceNode();
		passFrom.addChild(characterNodeOne);
		passFrom.addChild(characterNodeTwo);

		SequenceNode passTo = new SequenceNode();
		editorState.setCurrentNode(passFrom);
		editorState.setCurrentOffset(passFrom.size());

		ArgumentHelper.passSingleCharacter(editorState, passTo);

		//Expecting only one character to be passed
		assertEquals(1, passTo.size());
		assertEquals(characterNodeTwo, passTo.getChild(0));
	}

	/**
	 * Checks whether the tested method reads only CharacterNodes and not e.g. FunctionNodes
	 * @see ArgumentHelper#readCharacters(EditorState, int)
	 */
	@Test
	public void readOnlyCharacters() {
		SequenceNode sequence = new SequenceNode();
		sequence.addChild(new FunctionNode(templateCatalog.getGeneral(Tag.LIM_EQ)));
		sequence.addChild(characterNodeOne);
		sequence.addChild(characterNodeTwo);

		editorState.setCurrentNode(sequence);

		// "12" is expected since we do not want to read anything other than CharacterNodes
		assertEquals("12", ArgumentHelper.readCharacters(editorState, sequence.size()));

		sequence.addChild(new SequenceNode());

		// Empty string expected, since the last argument is not a CharacterNodes
		assertEquals("", ArgumentHelper.readCharacters(editorState, sequence.size()));
	}
}