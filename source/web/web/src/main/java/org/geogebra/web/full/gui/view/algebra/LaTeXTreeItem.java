package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.util.StringUtil;
import org.geogebra.editor.share.serializer.TeXSerializer;
import org.geogebra.editor.web.MathFieldW;
import org.geogebra.web.editor.MathFieldProcessing;
import org.geogebra.web.full.gui.util.SyntaxAdapterImplWithPaste;
import org.geogebra.web.html5.util.DataTest;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.user.client.ui.FlowPanel;

import com.himamis.retex.renderer.web.FactoryProviderGWT;

public class LaTeXTreeItem extends RadioTreeItem {

	private MathFieldW mf;

	public LaTeXTreeItem(Kernel kernel, AlgebraViewW av) {
		super(kernel, av);
	}

	public LaTeXTreeItem(GeoElement geo) {
		super(geo);
	}

	@Override
	public void insertMath(String string) {
		if (mf != null) {
			mf.getInternal().convertAndInsert(string);
		}
	}

	@Override
	public void setText(String text) {
		if (!"".equals(text)) {
			removeDummy();
		}
		if (mf != null) {
			if (isTextItem()) {
				mf.getInternal().setPlainText(text);
			} else {
				mf.parse(text);
			}
		}
		inputControl.ensureInputMoreMenu();
		updateEditorAriaLabel(text);
		updatePreview();
	}

	@Override
	public String getText() {
		if (mf == null) {
			return "";
		}
		return mf.getText();
	}

	@Override
	public String getCommand() {
		return controller.getCommand(mf);
	}

	private void initMathField() {
		if (latexItem == null) {
			latexItem = new FlowPanel();
		}

		FactoryProviderGWT.ensureLoaded();
		mf = new MathFieldW(new SyntaxAdapterImplWithPaste(app.getKernel()), latexItem, canvas,
				getLatexController(), app.getEditorFeatures());
		DataTest.ALGEBRA_INPUT.apply(mf.getInputTextArea());
		mf.setExpressionReader(ScreenReader.getExpressionReader(app));
		updateEditorAriaLabel("");
		mf.setFontSize(getFontSize());
		mf.getInternal().registerMathFieldInternalListener(syntaxController);
		mf.setPixelRatio(app.getPixelRatio());
		mf.setOnBlur((blurEvent) -> {
			toastController.hide();
			controller.onBlur(blurEvent);
		});
		mf.setOnFocus(focusEvent -> setFocusedStyle(true));
	}

	private void updateEditorAriaLabel(String text) {
		if (mf != null) {
			if (!StringUtil.emptyTrim(text)) {
				String label = ScreenReader.getAriaExpression(app, mf.getFormula(),
						ariaPreview);
				if (StringUtil.empty(label)) {
					label = mf.getDescription();
				}
				mf.setAriaLabel(label);
			} else {
				mf.setAriaLabel(loc.getMenu("EnterExpression"));
			}
		}
	}

	@Override
	public void adjustCaret(int x, int y) {
		if (mf != null) {
			mf.adjustCaret(x, y, app.getGeoGebraElement().getScaleX());
		}
	}

	@Override
	public final void updateFonts() {
		if (mf != null) {
			mf.setFontSize(getFontSize());
		}

		if (dummyLabel != null) {
			updateFont(dummyLabel);
		}
	}

	@Override
	public void insertString(String text) {
		new MathFieldProcessing(mf).autocomplete(
				app.getParserFunctions().toEditorAutocomplete(text, loc));
	}

	@Override
	protected String getEditorLatex() {
		return mf == null ? null
				: TeXSerializer.serialize(mf.getFormula().getRootNode());
	}

	@Override
	protected void requestEditorFocus() {
		getMathField().requestViewFocus();
	}

	@Override
	protected void updateAriaLabel() {
		if (mf != null) {
			updateEditorAriaLabel(getText());
		}
	}

	@Override
	protected void setEnabled(boolean enabled) {
		if (mf != null) {
			mf.setEnabled(false);
		}
	}

	@Override
	protected void setEditorFocus(boolean focus) {
		mf.setFocus(focus);
	}

	@Override
	protected void onInputModeChange(boolean plainTextMode) {
		mf.setPlainTextMode(plainTextMode);
	}

	@Override
	public MathFieldW getMathField() {
		return mf;
	}

	@Override
	protected boolean ensureCanvas() {
		if (canvas == null) {
			canvas = Canvas.createIfSupported();
			initMathField();
			return true;
		}
		if (mf == null) {
			initMathField();
		}

		return false;
	}

	@Override
	public RadioTreeItem copy() {
		return new LaTeXTreeItem(geo);
	}

	@Override
	public void setPixelRatio(double pixelRatio) {
		if (mf != null) {
			mf.setPixelRatio(pixelRatio);
			mf.repaint();
		}
	}
}
