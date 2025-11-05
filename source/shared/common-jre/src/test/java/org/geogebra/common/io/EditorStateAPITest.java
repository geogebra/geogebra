package org.geogebra.common.io;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.common.util.StringUtil;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.controller.CursorController;
import org.geogebra.editor.share.event.KeyEvent;
import org.geogebra.editor.share.util.JavaKeyCodes;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class EditorStateAPITest {

	private static Integer[][] expectedPaths = new Integer[][] { { 5 },
			{ 3, 0, 4 }, { 2, 0, 4 }, { 1, 0, 4 }, { 0, 0, 4 }, { 4 }, { 3 },
			{ 2 }, { 1 }, { 0 } };

	@BeforeClass
	public static void setupFactoryProvider() {
		FactoryProvider.setInstance(new FactoryProviderCommon());
	}

	@Test
	public void parseAndSerializeShouldKeepProperties() {
		ArrayList<Integer> oldCaretPath = new ArrayList<>();
		oldCaretPath.add(2);
		EditorStateDescription jsonHandler = new EditorStateDescription("x+1",
				oldCaretPath);
		String json = jsonHandler.asJSON();
		EditorStateDescription state = EditorStateDescription.fromJSON(json);
		assertEquals("x+1", state.getContent());
		assertEquals(oldCaretPath, state.getCaretPath());
	}

	@Test
	public void getPathShouldFollowExpressionStructure() {
		MathFieldCommon mathField = new MathFieldCommon(new TemplateCatalog(), null);
		mathField.insertString("x+x*(x+1)");

		for (int i = 0; i < expectedPaths.length; i++) {
			ArrayList<Integer> oldCaretPath = mathField.getCaretPath();
			assertEquals(StringUtil.join(",", expectedPaths[i]),
					StringUtil.join(",", oldCaretPath));
			moveLeft(mathField);
		}
	}

	private static void moveLeft(MathFieldCommon mathField) {
		mathField.getInternal()
				.onKeyPressed(new KeyEvent(JavaKeyCodes.VK_LEFT, KeyEvent.KeyboardType.EXTERNAL));
	}

	@Test
	public void setPathGetPathShouldBeCompatible() {
		MathFieldCommon mathField = new MathFieldCommon(new TemplateCatalog(), null);
		mathField.insertString("x+x*(x+1)");

		for (int i = 0; i < expectedPaths.length; i++) {
			ArrayList<Integer> caretPath = new ArrayList<>();
			caretPath.addAll(Arrays.asList(expectedPaths[i]));
			CursorController.setPath(caretPath,
					mathField.getInternal().getEditorState());
			ArrayList<Integer> oldCaretPath = mathField.getCaretPath();
			String expected = StringUtil.join(",", expectedPaths[i]);
			assertEquals(
					i + "-th paths should be " + expected, expected,
					StringUtil.join(",", oldCaretPath));
		}
	}
}
