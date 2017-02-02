package org.geogebra.web.web.gui.view.algebra;


import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.io.latex.GeoGebraSerializer;
import org.geogebra.common.io.latex.ParseException;
import org.geogebra.common.io.latex.Parser;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.editor.MathFieldProcessing;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.web.gui.inputbar.AlgebraInputW;
import org.geogebra.web.web.gui.inputfield.InputSuggestions;
import org.geogebra.web.web.util.LaTeXHelper;
import org.geogebra.web.web.util.ReTeXHelper;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.editor.web.MathFieldW;
import com.himamis.retex.renderer.share.CursorBox;

/**
 * 
 * @author Zbynek
 *
 */
public class LatexTreeItem extends RadioTreeItem {
	// implements MathFieldListener {

	private MathFieldW mf;


	/**
	 * @param geo0
	 *            geo element
	 */
	public LatexTreeItem(GeoElement geo0) {
		super(geo0);
		getWidget().addStyleName("latexEditor");
	}

	/**
	 * @param kernel
	 *            Kernel
	 */
	public LatexTreeItem(Kernel kernel) {
		super(kernel);
		this.insertHelpToggle();
		if (app.has(Feature.AV_INPUT_BUTTON_COVER)) {
			content.addStyleName("scrollableTextBox");
			if (isInputTreeItem()) {
				content.addStyleName("inputBorder");
			}
		}
		getWidget().addStyleName("latexEditor");
		content.addStyleName("avPreview");
		renderLatex("", false);
	}

	@Override
	protected RadioTreeItemController createController() {
		return new LatexTreeItemController(this);
	}

	/**
	 * 
	 * @return The controller as LatexTreeItemController.
	 */
	public LatexTreeItemController getLatexController() {
		return (LatexTreeItemController) getController();
	}

	@Override
	protected void showKeyboard() {
		getLatexController().showKeyboard();

	}

	/**
	 * @param old
	 *            what to replace
	 */
	private void renderLatex(String text0, boolean showKeyboard) {
		// if (!forceMQ) {
		// canvas = DrawEquationW.paintOnCanvas(geo, text0, canvas,
		// getFontSize());
		//
		// if (canvas != null && ihtml.getElement().isOrHasChild(old)) {
		// ihtml.getElement().replaceChild(canvas.getCanvasElement(), old);
		// }
		//
		// }

		// latexItem.addStyleName("avTextItem");
		// TODO updateColor(latexItem);

		content.clear();

		if (app.has(Feature.AV_SINGLE_TAP_EDIT) && !(latexItem == null
				|| isInputTreeItem() || isSliderItem())) {
			latexItem.getElement().getStyle().setProperty("minHeight",
					getController().getEditHeigth() + "px");
		}

		ensureCanvas();
		appendCanvas();


		if (!content.isAttached()) {
			main.add(content);
		}

		setText(text0);
		getLatexController().initAndShowKeyboard(showKeyboard);

	}

	private void appendCanvas() {
		if (latexItem == null) {
			latexItem = new FlowPanel();
		}
		latexItem.clear();
		latexItem.add(canvas);
		content.add(latexItem);

	}

	/**
	 * @return whether canvas was created
	 */
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


	private void initMathField() {
		if (latexItem == null) {
			latexItem = new FlowPanel();
		}
		mf = new MathFieldW(latexItem, canvas,
				getLatexController());
		mf.setFontSize(getFontSize() + 1);
		mf.setPixelRatio(app.getPixelRatio());
		mf.setOnBlur(getLatexController());
	}

	@Override
	public void setFocus(boolean focus, boolean sv) {
		if (focus && !app.has(Feature.RETEX_EDITOR)
				&& GWT.create(LaTeXHelper.class) instanceof ReTeXHelper) {
			return;
		}
		if (focus) {
			removeDummy();
		}


		if (app.has(Feature.AV_INPUT_BUTTON_COVER)) {
			if (focus) {
				content.addStyleName("scrollableTextBox");
				if (isInputTreeItem()) {
					setItemWidth(getAV().getOffsetWidth());
				}
			} else {
				if (isInputTreeItem()) {
					setItemWidth(getAV().getMaxItemWidth());
				} else {
					content.removeStyleName("scrollableTextBox");
				}
			}
		}

		if (ensureCanvas()) {
			main.clear();
			main.add(this.marblePanel);

			if (isInputTreeItem()) {
				appendCanvas();
			}
			main.add(content);
			if (controls != null) {
				main.add(controls);
				updateButtonPanelPosition();
			}

		}

		if (focus) {
			canvas.setVisible(true);
		} else {
			if (geo == null) {
				addDummyLabel();
			}
		}
		mf.setFocus(focus);

		int kH = (int) (app.getAppletFrame().getKeyboardHeight());
		if (getAlgebraDockPanel().getOffsetHeight() < kH) {
			app.adjustViews();
		}
	}

	@Override
	public String getText() {
		if (mf == null) {
			return "";
		}
		GeoGebraSerializer s = new GeoGebraSerializer();
		return s.serialize(mf.getFormula());
	}

	@Override
	public void onEnter(final boolean keepFocus) {
		getLatexController().onEnter(keepFocus && !softEnter);
	}


	
	@Override
	public void setText(String text0) {
		removeDummy();
		if(mf!=null){
			Parser parser = new Parser(mf.getMetaModel());
			MathFormula formula;
			try {
				formula = parser.parse(text0);
				mf.setFormula(formula);
			} catch (ParseException e) {
				Log.warn("Problem parsing: " + text0);
				e.printStackTrace();
			}
		}
		updatePreview();
	}
	@Override
	public void setLabels() {
		if (dummyLabel != null) {
			dummyLabel.setText(loc.getPlain("InputLabel") + Unicode.ellipsis);
		}
	}

	@Override
	public String getCommand() {
		return mf == null ? "" : mf.getCurrentWord();
	}

	@Override
	public void autocomplete(String text) {
		getLatexController().autocomplete(text);
	}

	@Override
	protected void focusAfterHelpClosed() {
		getController().setFocus(true);
	}

	/**
	 * Update after key was typed
	 */
	public void onKeyTyped() {
		app.closePerspectivesPopup();
		updatePreview();
		popupSuggestions();
		onCursorMove();
	}

	/**
	 * Cursor listener
	 */
	public void onCursorMove() {
		if (latexItem.getOffsetWidth() + latexItem.getElement().getScrollLeft()
				- 10 < CursorBox.startX) {
			latexItem.getElement().setScrollLeft(
					(int) CursorBox.startX - latexItem.getOffsetWidth() + 10);
		} else if (CursorBox.startX < latexItem.getElement().getScrollLeft() + 10) {
			latexItem.getElement().setScrollLeft((int) CursorBox.startX - 10);
		}

	}

	@Override
	public boolean popupSuggestions() {
		return getInputSuggestions().popupSuggestions();
	}

	/**
	 * @return suggestions model
	 */
	InputSuggestions getInputSuggestions() {
		return getLatexController().getInputSuggestions();
	}

	private void updatePreview() {
		String text = getText();
		app.getKernel()
				.getInputPreviewHelper()
				.updatePreviewFromInputBar(text,
						AlgebraInputW.getWarningHandler(this, app));
	}

	@Override
	public RadioTreeItem copy() {
		return new LatexTreeItem(geo);
	}

	@Override
	public void insertString(String text) {
		new MathFieldProcessing(mf).autocomplete(text);

	}

	@Override
	public void cancelEditing() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void blurEditor() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void renderLatex(String text0, Widget w, boolean forceMQ) {
		if (forceMQ) {
			// TODO
			// editLatexMQ(text0);
		} else {
			replaceToCanvas(text0, w);
		}

	}

	@Override
	protected void clearInput() {
		setText("");

	}

	@Override
	public void handleFKey(int key, GeoElement geoElement) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateGUIfocus(Object source, boolean blurtrue) {
		if (geo == null) {
			updateEditorFocus(source, blurtrue);
		}
	}

	@Override
	public List<String> getCompletions() {
		return getInputSuggestions().getCompletions();
	}

	@Override
	public List<String> resetCompletions() {
		return getInputSuggestions().resetCompletions();
	}

	@Override
	public boolean getAutoComplete() {
		return true;
	}

	@Override
	public boolean isSuggesting() {
		return getLatexController().isSuggesting();
	}



	@Override
	public void setPixelRatio(double pixelRatio) {
		if (mf != null) {
			mf.setPixelRatio(pixelRatio);
			mf.repaint();
		}

	}

	@Override
	protected void updateAfterRedefine(boolean success) {
		if (mf != null && success) {
			mf.setEnabled(false);
		}
		super.updateAfterRedefine(success);
	}

	@Override
	public boolean isInputTreeItem() {
		if (app.has(Feature.AV_SCROLL)) {
			return getAV().getInputTreeItem() == this;
		}
		return super.isInputTreeItem();
	}



	@Override
	protected void updateButtonPanelPosition() {
		super.updateButtonPanelPosition();
		if (isInputTreeItem() && !getController().isEditing()) {
			// getAlgebraDockPanel().scrollToActiveItem();
		}
	}

	/**
	 * @return math field
	 */
	public MathFieldW getMathField() {
		return mf;
	}

	@Override
	public boolean onEditStart(boolean substituteNumbers) {
		String text = geo == null ? "" : geo.getDefinitionForEditor();
		if (geo != null && !geo.isDefined() && lastInput != null) {
			text = lastInput;
		}
		if (text == null || !app.has(Feature.RETEX_EDITOR)) {
			return false;
		}
		clearErrorLabel();
		removeDummy();

		renderLatex(text, true);
		getMathField().requestViewFocus();
		app.getGlobalKeyDispatcher().setFocused(true);
		// canvas.addBlurHandler(getLatexController());
		CancelEventTimer.keyboardSetVisible();
		ClickStartHandler.init(main, new ClickStartHandler(false, false) {
			@Override
			public void onClickStart(int x, int y,
					final PointerEventType type) {
				getLatexController().setOnScreenKeyboardTextField();
			}
		});

		return true;

	}

	@Override
	public void adjustCaret(int x, int y) {
		if (mf != null) {
			mf.adjustCaret(x, y);
		}

	}

	@Override
	public void updateFonts() {
		if (mf != null) {
			mf.setFontSize(getFontSize());
		}
		super.updateFonts();
	}

	@Override
	protected String getEditorLatex() {
		return mf == null ? null
				: TeXSerializer.serialize(mf.getFormula().getRootComponent(),
				mf.getMetaModel());
	}

	@Override
	protected void doUpdate() {
		if (mf != null) {
			mf.setEnabled(false);
		}
		super.doUpdate();
	}

	@Override
	public void preventBlur() {
		((LatexTreeItemController) getController()).preventBlur();

	}

}
