package org.geogebra.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.syntax.SyntaxController;
import com.himamis.retex.editor.share.syntax.SyntaxHint;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class SyntaxHintCheck {

	private MathFieldCommon mathField;
	private SyntaxController controller;

	/**
	 * Reset LaTeX factory
	 */
	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@Before
	public void setUp() {
		controller = new SyntaxController();
		mathField = new MathFieldCommon(new MetaModel(), null);
		mathField.getInternal().registerMathFieldInternalListener(controller);
	}

	@Test
	public void readPlaceholdersInitial() {
		KeyboardInputAdapter.onKeyboardInput(mathField.getInternal(),
				"FitPoly(<Points>, <Degree>)");
		SyntaxHint hint = controller.getSyntaxHint();
		assertEquals("FitPoly(", hint.getPrefix());
		assertEquals("Points", hint.getActivePlaceholder());
		assertEquals(", Degree)", hint.getSuffix());
	}

	@Test
	public void readPlaceholdersAfterComma() {
		KeyboardInputAdapter.onKeyboardInput(mathField.getInternal(),
				"FitPoly(<Points>, <Degree>)");
		EditorTyper typer = new EditorTyper(mathField);
		typer.type("{(1,1)},");
		SyntaxHint hint = controller.getSyntaxHint();
		assertEquals("FitPoly(Points, ", hint.getPrefix());
		assertEquals("Degree", hint.getActivePlaceholder());
		assertEquals(")", hint.getSuffix());
	}

	@Test
	public void nonCommandInput() {
		KeyboardInputAdapter.onKeyboardInput(mathField.getInternal(), "\"Hello, there!\"");
		EditorTyper typer = new EditorTyper(mathField);
		typer.type("{(1,1)},");
		SyntaxHint hint = controller.getSyntaxHint();
		assertTrue(hint.isEmpty());
	}

	@Test
	public void changeFunctionName() {
		String input = "FitPoly(<Points>, <Degree>)";
		KeyboardInputAdapter.onKeyboardInput(mathField.getInternal(), input);
		SyntaxHint hint = controller.getSyntaxHint();
		assertEquals("FitPoly(", hint.getPrefix());
		assertEquals("Points", hint.getActivePlaceholder());
		assertEquals(", Degree)", hint.getSuffix());
		EditorTyper typer = new EditorTyper(mathField);
		typer.repeatKey(JavaKeyCodes.VK_LEFT, input.length() - 3);
		typer.typeKey(JavaKeyCodes.VK_DELETE);
		assertTrue(controller.getSyntaxHint().isEmpty());
	}

	@Test
	public void changeFunctionNameAppending() {
		String input = "FitPoly(<Points>, <Degree>)";
		KeyboardInputAdapter.onKeyboardInput(mathField.getInternal(), input);
		SyntaxHint hint = controller.getSyntaxHint();
		assertEquals("FitPoly(", hint.getPrefix());
		assertEquals("Points", hint.getActivePlaceholder());
		assertEquals(", Degree)", hint.getSuffix());
		EditorTyper typer = new EditorTyper(mathField);
		typer.typeKey(JavaKeyCodes.VK_LEFT);
		typer.type("XY");
		typer.typeKey(JavaKeyCodes.VK_RIGHT);
		SyntaxHint syntaxHint = controller.getSyntaxHint();
		assertTrue(syntaxHint.toString(), syntaxHint.isEmpty());
	}

	@Test
	public void steppingOutAndIn() {
		String input = "FitPoly(<Points>, <Degree>)";
		KeyboardInputAdapter.onKeyboardInput(mathField.getInternal(), input);
		SyntaxHint hint = controller.getSyntaxHint();
		assertEquals("FitPoly(", hint.getPrefix());
		assertEquals("Points", hint.getActivePlaceholder());
		assertEquals(", Degree)", hint.getSuffix());
		EditorTyper typer = new EditorTyper(mathField);
		typer.typeKey(JavaKeyCodes.VK_LEFT);
		assertTrue(hint.isEmpty());
		typer.typeKey(JavaKeyCodes.VK_RIGHT);
		assertEquals("FitPoly(", hint.getPrefix());
		assertEquals("Points", hint.getActivePlaceholder());
		assertEquals(", Degree)", hint.getSuffix());
	}

}
