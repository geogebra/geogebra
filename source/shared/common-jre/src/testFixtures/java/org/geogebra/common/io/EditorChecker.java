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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.App;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.common.util.debug.Log;
import org.geogebra.editor.share.controller.CursorController;
import org.geogebra.editor.share.controller.EditorState;
import org.geogebra.editor.share.editor.AddPlaceholders;
import org.geogebra.editor.share.editor.EditorFeatures;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.event.KeyEvent;
import org.geogebra.editor.share.input.KeyboardInputAdapter;
import org.geogebra.editor.share.io.latex.Parser;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.tree.Formula;
import org.geogebra.editor.share.tree.SequenceNode;
import org.geogebra.editor.share.serializer.GeoGebraSerializer;
import org.geogebra.editor.share.serializer.TeXBuilder;
import org.geogebra.editor.share.serializer.TeXSerializer;
import org.geogebra.editor.share.util.JavaKeyCodes;
import org.geogebra.editor.share.util.FormulaConverter;
import org.geogebra.editor.share.util.Unicode;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.CharAtom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.PhantomAtom;
import com.himamis.retex.renderer.share.ResizeAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.SymbolAtom;

class EditorChecker {
	private final MathFieldCommon mathField;
	private final EditorTyper typer;
	private final App app;

	protected EditorChecker(App app) {
		this(app, new TemplateCatalog());
	}

	protected EditorChecker(App app, TemplateCatalog catalog) {
		this.app = app;
		mathField = new MathFieldCommon(catalog, null);
		typer = new EditorTyper(mathField);
	}

	public void checkAsciiMath(String output) {
		SequenceNode rootComponent = getRootComponent();
		assertEquals(output,
				GeoGebraSerializer.serialize(rootComponent, (EditorFeatures) null));
		// clean the checker after typing
		reset();
	}

	public void checkLaTeX(String output) {
		assertEquals(output,
				TeXSerializer.serialize(getRootComponent()));
		// clean the checker after typing
		reset();
	}

	public void checkGGBMath(String output) {
		checkGGBMath(output, null);
	}

	public void checkGGBMath(String output, @CheckForNull EditorFeatures editorFeatures) {
		SequenceNode rootComponent = getRootComponent();
		String exp = new GeoGebraSerializer(editorFeatures)
				.serialize(rootComponent, new StringBuilder()).toString();
		try {
			ValidExpression en = parse(exp);
			assertEquals(output, en.toString(StringTemplate.defaultTemplate));
		} catch (ParseException e) {
			Log.debug(e);
			assertEquals(output, "Exception for " + exp + ":" + e);
		}
		reset();
	}

	public EditorChecker checkPlaceholders(String expected) {
		MathFieldInternal mathFieldInternal = mathField.getInternal();
		mathFieldInternal.update();
		EditorState editorState = mathFieldInternal.getEditorState();
		SequenceNode currentField = editorState.getCurrentNode();
		TeXBuilder builder = new TeXBuilder();
		RowAtom atom =
				(RowAtom) builder.build(currentField, currentField, editorState.getCurrentOffset(),
						false);
		assertEquals(expected, serializeRow(atom));
		return this;
	}

	private String serializeRow(RowAtom row) {
		StringBuilder sb = new StringBuilder();
		for (Atom atom: row.getElements()) {
			sb.append(serializeAtom(atom));
		}
		return sb.toString();
	}

	private String serializeAtom(Atom atom) {
		if (atom instanceof PhantomAtom) {
			return "|";
		}

		if (atom instanceof ColorAtom) {
			return "_";
		}

		if (atom instanceof CharAtom) {
			return ((CharAtom) atom).getCharacter() + "";
		}

		if (atom instanceof SymbolAtom) {
			return ((SymbolAtom) atom).getUnicode() + "";
		}

		if (atom instanceof ResizeAtom) {
			return serializeAtom(((ResizeAtom) atom).getTrueBase());
		}
		if (atom instanceof SpaceAtom && ((SpaceAtom) atom).getWidth() == 0) {
			return Unicode.ZERO_WIDTH_SPACE + "";
		}
		return null;
	}

	public void checkRaw(String output) {
		SequenceNode rootComponent = getRootComponent();
		assertEquals(output, rootComponent + "");
	}

	public void checkLength(int length) {
		assertEquals(length, getRootComponent().size());
	}

	private SequenceNode getRootComponent() {
		MathFieldInternal mathFieldInternal = mathField.getInternal();
		EditorState editorState = mathFieldInternal.getEditorState();
		return editorState.getRootNode();
	}

	/**
	 * Types input to the checker.
	 * Example ('|' is the cursor):
	 * - content before: "xyz|"
	 * - type("ABC");
	 * - content after : "xyzABC|"
	 * @param input to type.
	 * @return the modified checker.
	 */
	public EditorChecker type(String input) {
		typer.type(input);
		return this;
	}

	public EditorChecker pressSingleKey(String key) {
		KeyboardInputAdapter.onKeyboardInput(mathField.getInternal(), key);
		return this;
	}

	public EditorChecker typeKey(int key) {
		typer.typeKey(key);
		return this;
	}

	/**
	 * Moves the cursor left to given times.
	 * Example ('|' is the cursor):
	 *  - content before: "xyz|"
	 *  - left("2");
	 *  - content after : "x|yz"
	 * @param count of the cursor moves
	 * @return the modified checker.
	 */
	public EditorChecker left(int count) {
		return repeatKey(JavaKeyCodes.VK_LEFT, count);
	}

	/**
	 * Moves the cursor right to given times.
	 * Example ('|' is the cursor):
	 *  - content before: "|xyz"
	 *  - right("2");
	 *  - content after : "xy|z"
	 * @param count of the cursor moves
	 * @return the modified checker.
	 */
	public EditorChecker right(int count) {
		return repeatKey(JavaKeyCodes.VK_RIGHT, count);
	}

	public EditorChecker setModifiers(int modifiers) {
		typer.setModifiers(modifiers);
		return this;
	}

	public EditorChecker repeatKey(int key, int count) {
		typer.repeatKey(key, count);
		return this;
	}

	public EditorChecker insert(String input) {
		typer.insert(input);
		return this;
	}

	public EditorChecker fromParser(String input) {
		Parser parser = new Parser(mathField.getCatalog());
		Formula formula;
		try {
			formula = parser.parse(input);
			mathField.getInternal().setFormula(formula);
		} catch (Exception e) {
			throw new AssertionError("Problem parsing: " + input, e);
		}
		return this;
	}

	public EditorChecker withPlaceholders() {
		new AddPlaceholders().process(mathField.getInternal().getFormula()
				.getRootNode().getChild(0));
		return this;
	}

	public EditorChecker convertFormula(String input) {
		return convertFormulaAndProtect(input, true);
	}

	private EditorChecker convertFormulaAndProtect(String input, boolean protect) {
		try {
			FormulaConverter converter =
					new FormulaConverter(mathField.getCatalog());
			mathField.getInternal().setFormula(converter.buildFormula(input));
			if (protect) {
				mathField.getInternal().getFormula().getRootNode().setProtected();
			}

			mathField.getInternal().setLockedCaretPath();
		} catch (org.geogebra.editor.share.io.latex.ParseException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public EditorChecker convertFormulaForAV(String input) {
		return convertFormulaAndProtect(input, false);
	}

	public EditorChecker matrixFromParser(String input) {
		Parser parser = new Parser(mathField.getCatalog());
		Formula formula;
		try {
			formula = parser.parse(input);
			mathField.getInternal().setFormula(formula);
			mathField.getInternal().getFormula().getRootNode().setProtected();
			mathField.getInternal().setLockedCaretPath();
		} catch (Exception e) {
			throw new AssertionError("Problem parsing: " + input, e);
		}
		return this;
	}

	/**
	 * Protect top level sequence
	 * @return this
	 */
	public EditorChecker protect() {
		mathField.getInternal().getFormula().getRootNode().setProtected();
		return this;
	}

	public EditorChecker checkPath(Integer... indexes) {
		MathFieldInternal mathFieldInternal = mathField.getInternal();
		mathField.requestViewFocus();
		mathFieldInternal.update();
		ArrayList<Integer> actual = CursorController.getPath(mathFieldInternal
				.getEditorState());
		assertArrayEquals(indexes, actual.toArray());
		return this;
	}

	public void serializeAs(String latex) {
		TeXSerializer teXSerializer = new TeXSerializer();
		assertEquals(latex,
				teXSerializer.serialize(mathField.getInternal().getFormula()));
	}

	protected void checkEditorInsert(String input, String output) {
		new EditorChecker(app).insert(input).checkAsciiMath(output);
	}

	public ValidExpression parse(String exp) throws ParseException {
		return app.getKernel().getParser().parseGeoGebraExpression(exp);
	}

	public void setFormatConverter(SyntaxAdapterImpl formatConverter) {
		mathField.setFormatConverter(formatConverter);
	}

	public void setForceBracketsAfterFunction() {
		mathField.getCatalog().setForceBracketAfterFunction(true);
	}

	public void setAllowAbs(boolean allowAbs) {
		mathField.getInternal().setAllowAbs(allowAbs);
	}

	public EditorChecker click(int x, int y) {
		mathField.getInternal().onPointerUp(x, y);
		return this;
	}

	/**
	 * Asserts if the cursor is in super- or subscript.
	 * Examples ('|' is the cursor):
	 *  xy^z|, xy_|z, xy^(1/x|)
	 */
	public void checkCursorInScript() {
		assertTrue(mathField.getInternal().getEditorState().isInScript());
	}

	/**
	 * Asserts if the cursor is NOT in super- or subscript.
	 * Note: opposite of cursorInScript().
	 */
	public void checkCursorNotInScript() {
		assertFalse(mathField.getInternal().getEditorState().isInScript());
	}

	/**
	 * Asserts if the cursor is at the root component directly.
	 *
	 * For example ('|' is the cursor) let's take a point (1, 2) in math field.
	 * Then '(1, |2)' is false, but '(1,2)|' or '|(1,2)' are true.
	 */
	public void checkCursorIsAtRoot() {
		EditorState editorState = mathField.getInternal().getEditorState();
		assertEquals(editorState.getCurrentNode(), editorState.getRootNode());
	}

	public EditorChecker select(int from, int to) {
		EditorState state = mathField.getInternal().getEditorState();
		state.setSelectionStart(state.getRootNode().getChild(from));
		state.setSelectionEnd(state.getRootNode().getChild(to));
		return this;
	}

	public void add(String input) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(input, false);
	}

	void checkCopy(String expected) {
		mathField.getInternal().getEditorState().selectAll();
		assertEquals(expected, mathField.getInternal().copy());
	}

	void checkSelection(String from, String to) {
		EditorState editorState = mathField.getInternal().getEditorState();
		assertEquals(from, editorState.getSelectionStart().toString());
		assertEquals(to, editorState.getSelectionEnd().toString());
		reset();
	}

	public void checkSelectionEmpty() {
		assertNull(mathField.getInternal().getEditorState().getSelectionStart());
		reset();
	}

	public EditorChecker shiftOn() {
		return this.setModifiers(KeyEvent.SHIFT_MASK);
	}

	public void shouldDeleteOnly(Integer number) {
		String before = GeoGebraSerializer.serialize(getRootComponent(), (EditorFeatures) null);
		setModifiers(0);
		typeKey(JavaKeyCodes.VK_DELETE);
		checkAsciiMath(before.replace(number.toString(), ""));
	}

	public EditorChecker backspace(int times) {
		return repeatKey(JavaKeyCodes.VK_BACK_SPACE, times);
	}

	public EditorChecker ctrlA() {
		setModifiers(KeyEvent.CTRL_MASK);
		typeKey(JavaKeyCodes.VK_A);
		return this;
	}

	public EditorChecker down(int times) {
		return repeatKey(JavaKeyCodes.VK_DOWN, times);
	}

	private void reset() {
		fromParser("");
		setModifiers(0);
	}
}