package org.geogebra.keyboard.web.factory.model.inputbox.math;

import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;

import java.util.List;
import java.util.stream.Stream;

import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil;
import org.geogebra.keyboard.web.factory.model.inputbox.util.Cursive;
import org.geogebra.keyboard.web.factory.model.inputbox.util.MathKeyUtil;

import com.himamis.retex.editor.share.input.FunctionVariableAdapter;

public class FunctionMathKeyboardFactory implements KeyboardModelFactory {
	private final List<String> vars;
	private static final int MAX_VARS = 4;

	public FunctionMathKeyboardFactory(List<String> vars) {
		this.vars = vars;
	}

	@Override
	public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
		KeyboardModelImpl mathKeyboard = new KeyboardModelImpl();

		RowImpl row = mathKeyboard.nextRow(9.2f);
		addFunctionVarButtons(row, buttonFactory);
		NumberKeyUtil.addFirstRow(row, buttonFactory);

		row = mathKeyboard.nextRow(9.2f);
		MathKeyUtil.addSqExpRootFrac(row, buttonFactory);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addSecondRow(row, buttonFactory);

		row = mathKeyboard.nextRow(9.2f);
		MathKeyUtil.addImInfDegComma(row, buttonFactory);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addThirdRow(row, buttonFactory);

		row = mathKeyboard.nextRow(9.2f);
		MathKeyUtil.addParenthesesPiE(row, buttonFactory);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addFourthRow(row, buttonFactory);

		return mathKeyboard;
	}

	private void addFunctionVarButtons(RowImpl row, ButtonFactory buttonFactory) {
		getVarsLimited().forEach(varName -> {
			String cursiveBoldLetter = Cursive.getCursiveCaption(varName);
			String buttonCaption = cursiveBoldLetter == null ? varName : cursiveBoldLetter;
			addInputButton(row, buttonFactory, buttonCaption,
					FunctionVariableAdapter.wrap(varName));
		});
		addButton(row, buttonFactory.createEmptySpace(
				vars.size() > MAX_VARS ? 0.2f : 4.2f - vars.size()));
	}

	private Stream<String> getVarsLimited() {
		return vars.stream().limit(MAX_VARS);
	}
}
