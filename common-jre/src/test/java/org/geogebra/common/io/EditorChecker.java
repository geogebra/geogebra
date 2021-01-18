package org.geogebra.common.io;

import java.util.ArrayList;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.App;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.junit.Assert;

import com.himamis.retex.editor.share.controller.CursorController;
import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.share.util.JavaKeyCodes;

class EditorChecker {
	private MathFieldCommon mathField;
	private EditorTyper typer;
	private App app;

	protected EditorChecker(App app) {
		this(app, new MetaModel());
	}

	protected EditorChecker(App app, MetaModel model) {
		this.app = app;
		mathField = new MathFieldCommon(model, null);
		typer = new EditorTyper(mathField);
	}

	public void checkAsciiMath(String output) {
		MathSequence rootComponent = getRootComponent();
		Assert.assertEquals(output,
				GeoGebraSerializer.serialize(rootComponent));
	}

	public void checkGGBMath(String output) {
		MathSequence rootComponent = getRootComponent();

		String exp = GeoGebraSerializer.serialize(rootComponent);

		try {
			ExpressionNode en = parse(exp);
			Assert.assertEquals(output, en.toString(StringTemplate.defaultTemplate));
		} catch (ParseException e) {
			e.printStackTrace();
			Assert.assertEquals(output, "Exception: " + e.toString());
		}

	}

	public void checkRaw(String output) {
		MathSequence rootComponent = getRootComponent();
		Assert.assertEquals(output, rootComponent + "");
	}

	private MathSequence getRootComponent() {
		MathFieldInternal mathFieldInternal = mathField.getInternal();
		EditorState editorState = mathFieldInternal.getEditorState();
		return editorState.getRootComponent();
	}

	public EditorChecker type(String input) {
		typer.type(input);
		return this;
	}

	public EditorChecker typeKey(int key) {
		typer.typeKey(key);
		return this;
	}

	public EditorChecker left(int count) {
		return repeatKey(JavaKeyCodes.VK_LEFT, count);
	}

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
		Parser parser = new Parser(mathField.getMetaModel());
		MathFormula formula;
		try {
			formula = parser.parse(input);
			mathField.getInternal().setFormula(formula);
		} catch (Exception e) {
			Assert.fail("Problem parsing: " + input);
		}
		return this;
	}

	public EditorChecker matrixFromParser(String input) {
		Parser parser = new Parser(mathField.getMetaModel());
		MathFormula formula;
		try {
			formula = parser.parse(input);
			mathField.getInternal().setFormula(formula);
			mathField.getInternal().getFormula().getRootComponent().setProtected();
			mathField.getInternal().setLockedCaretPath();
		} catch (Exception e) {
			Assert.fail("Problem parsing: " + input);
		}
		return this;
	}

	public EditorChecker checkPath(Integer... indexes) {
		MathFieldInternal mathFieldInternal = mathField.getInternal();
		mathField.requestViewFocus();
		mathFieldInternal.update();
		ArrayList<Integer> actual = CursorController.getPath(mathFieldInternal
				.getEditorState());
		Assert.assertArrayEquals(indexes, actual.toArray());
		return this;
	}

	protected void checkEditorInsert(String input, String output) {
		new EditorChecker(app).insert(input).checkAsciiMath(output);
	}

	public ExpressionNode parse(String exp) throws ParseException {
		return app.getKernel().getParser().parseExpression(exp);
	}

	public void setFormatConverter(SyntaxAdapterImpl formatConverter) {
		mathField.setFormatConverter(formatConverter);
	}
}
