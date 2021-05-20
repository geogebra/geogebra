package org.geogebra.io.latex;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.common.io.EditorStateDescription;
import org.geogebra.common.util.StringUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.desktop.MathFieldD;
import com.himamis.retex.editor.share.controller.CursorController;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class EditorStateAPITest {

	private static Integer[][] expectedPaths = new Integer[][] { { 5 },
			{ 3, 0, 4 }, { 2, 0, 4 }, { 1, 0, 4 }, { 0, 0, 4 }, { 4 }, { 3 },
			{ 2 }, { 1 }, { 0 } };

	@BeforeClass
	public static void setupFactoryProvider() {
		FactoryProvider.setInstance(new FactoryProviderDesktop());
	}

	@Test
	public void parseAndSerializeShouldKeepProperties() {
		ArrayList<Integer> oldCaretPath = new ArrayList<>();
		oldCaretPath.add(2);
		EditorStateDescription jsonHandler = new EditorStateDescription("x+1",
				oldCaretPath);
		String json = jsonHandler.asJSON();
		EditorStateDescription state = EditorStateDescription.fromJSON(json);
		Assert.assertEquals("x+1", state.getContent());
		Assert.assertEquals(oldCaretPath, state.getCaretPath());
	}

	@Test
	public void getPathShouldFollowExpressionStructure() {
		MathFieldD mathField = new MathFieldD(null);
		mathField.insertString("x+x*(x+1)");

		for (int i = 0; i < expectedPaths.length; i++) {
			ArrayList<Integer> oldCaretPath = mathField.getCaretPath();
			Assert.assertEquals(StringUtil.join(",", expectedPaths[i]),
					StringUtil.join(",", oldCaretPath));
			moveLeft(mathField);
		}
	}

	private static void moveLeft(MathFieldD mathField) {
		mathField.getInternal()
				.onKeyPressed(new KeyEvent(JavaKeyCodes.VK_LEFT));
	}

	@Test
	public void setPathGetPathShouldBeCompatible() {
		MathFieldD mathField = new MathFieldD(null);
		mathField.insertString("x+x*(x+1)");

		for (int i = 0; i < expectedPaths.length; i++) {
			ArrayList<Integer> caretPath = new ArrayList<>();
			caretPath.addAll(Arrays.asList(expectedPaths[i]));
			CursorController.setPath(caretPath,
					mathField.getInternal().getEditorState());
			ArrayList<Integer> oldCaretPath = mathField.getCaretPath();
			String expected = StringUtil.join(",", expectedPaths[i]);
			Assert.assertEquals(
					i + "-th paths should be " + expected, expected,
					StringUtil.join(",", oldCaretPath));
		}
	}
}
