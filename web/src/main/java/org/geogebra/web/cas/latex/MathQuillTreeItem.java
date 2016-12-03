package org.geogebra.web.cas.latex;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.keyboard.KeyboardListener;
import org.geogebra.web.web.gui.view.algebra.EquationEditorListener;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class MathQuillTreeItem extends RadioTreeItem
		implements EquationEditorListener, GeoContainer {

	public MathQuillTreeItem(GeoElement geo0) {
		super(geo0);
		content.addStyleName("mathQuillEditor");
	}

	public MathQuillTreeItem(Kernel kernel) {
		super(kernel);
	}

	/**
	 * Creates the specific tree item due to the type of the geo element.
	 * 
	 * @param geo0
	 *            the geo element which is the item for.
	 * @return The appropriate RadioTreeItem descendant.
	 */
	public static RadioTreeItem create(GeoElement geo0) {
		if (geo0.isMatrix()) {
			return new MatrixTreeItem(geo0);
		} else if (geo0.isGeoCurveCartesian()) {
			return new ParCurveTreeItem(geo0);
		} else if (geo0.isGeoFunctionConditional()) {
			return new CondFunctionTreeItem(geo0);
		}
		return new MathQuillTreeItem(geo0);
	}

	@Override
	public RadioTreeItem copy() {
		return new MathQuillTreeItem(geo);
	}

	/**
	 * This method can be used to invoke a keydown event on MathQuillGGB, e.g.
	 * key=8,alt=false,ctrl=false,shift=false will trigger a Backspace event
	 * 
	 * @param key
	 *            keyCode of the event, which is the same as "event.which", used
	 *            at keydown
	 * @param alt
	 *            boolean
	 * @param ctrl
	 *            boolean
	 * @param shift
	 *            boolean
	 */
	@Override
	public void keydown(int key, boolean alt, boolean ctrl, boolean shift) {
		// if (isMinMaxPanelVisible()) {
		// return;
		// }
		if (commonEditingCheck()) {
			MathQuillHelper.triggerKeydown(this, latexItem.getElement(), key,
					alt, ctrl, shift);
		}
	}

	/**
	 * This method should be used to invoke a keypress on MathQuillGGB, e.g.
	 * keypress(47, false, false, false); will trigger a '/' press event... This
	 * method should be used instead of "keydown" in case we are interested in
	 * the Character meaning of the key (to be entered in a textarea) instead of
	 * the Controller meaning of the key.
	 * 
	 * @param character
	 *            charCode of the event, which is the same as "event.which",
	 *            used at keypress
	 * @param alt
	 *            boolean maybe not useful
	 * @param ctrl
	 *            boolean maybe not useful
	 * @param shift
	 *            boolean maybe not useful
	 */
	@Override
	public void keypress(char character, boolean alt, boolean ctrl,
			boolean shift, boolean more) {
		// if (isMinMaxPanelVisible()) {
		// return;
		// }

		if (commonEditingCheck()) {
			MathQuillHelper.triggerKeypress(this, latexItem.getElement(),
					character, alt, ctrl, shift, more);
		}
	}

	/**
	 * This method can be used to invoke a keyup event on MathQuillGGB, e.g.
	 * key=13,alt=false,ctrl=false,shift=false will trigger a Enter event
	 * 
	 * @param key
	 *            keyCode of the event, which is the same as "event.which", used
	 *            at keyup
	 * @param alt
	 *            boolean
	 * @param ctrl
	 *            boolean
	 * @param shift
	 *            boolean
	 */
	@Override
	public final void keyup(int key, boolean alt, boolean ctrl, boolean shift) {
		// if (isMinMaxPanelVisible()) {
		// return;
		// }

		if (commonEditingCheck()) {
			MathQuillHelper.triggerKeyUp(latexItem.getElement(), key, alt, ctrl,
					shift);
		}
	}

	@Override
	public final Element getLaTeXElement() {
		return latexItem.getElement();
	}

	@Override
	public boolean resetAfterEnter() {
		return true;
	}

	@Override
	public String getLaTeX() {
		// TODO atm needed for CAS only
		return null;
	}


	@Override
	public void scrollCursorIntoView() {
		if (latexItem != null) {
			MathQuillHelper.scrollCursorIntoView(this, latexItem.getElement(),
					isInputTreeItem());
		}
	}

	@Override
	public void setFocus(boolean b, boolean sv) {
		MathQuillHelper.focusEquationMathQuillGGB(latexItem, b);
	}

	@Override
	public void insertString(String text) {
		// even worse
		// for (int i = 0; i < text.length(); i++)
		// geogebra.html5.main.DrawEquationWeb.writeLatexInPlaceOfCurrentWord(
		// seMayLatex, "" + text.charAt(i), "", false);

		MathQuillHelper.writeLatexInPlaceOfCurrentWord(this,
				latexItem.getElement(), text, "", false);
	}

	@Override
	public void cancelEditing() {
		if (isInputTreeItem()) {
			return;
		}
		// if (LaTeX) {
		MathQuillHelper.endEditingEquationMathQuillGGB(this, latexItem);
		// if (c != null) {
		// LayoutUtil.replace(ihtml, c, latexItem);
		// // ihtml.getElement().replaceChild(c.getCanvasElement(),
		// // latexItem.getElement());
		// }
		//
		// if (!latex && getPlainTextItem() != null) {
		// LayoutUtil.replace(ihtml, getPlainTextItem(), latexItem);
		// //
		// this.ihtml.getElement().replaceChild(getPlainTextItem().getElement(),
		// // latexItem.getElement());
		// }
		//
		doUpdate();
	}

	/**
	 * Starts the equation editor for the item.
	 * 
	 * @param substituteNumbers
	 *            Sets that variables must be substituted or not
	 * @return
	 */
	@Override
	protected boolean startEditing(boolean substituteNumbers) {
		String text = getTextForEditing(substituteNumbers,
				StringTemplate.latexTemplateMQedit);
		if (text == null) {
			return false;
		}
		if (errorLabel != null) {
			errorLabel.setText("");
		}
		if (isDefinitionAndValue()) {
			editLatexMQ(text);
		} else {
			renderLatexEdit(text);
		}

		MathQuillHelper.editEquationMathQuillGGB(this, latexItem, false);

		app.getGuiManager().setOnScreenKeyboardTextField(this);
		CancelEventTimer.keyboardSetVisible();
		ClickStartHandler.init(main, new ClickStartHandler(false, false) {
			@Override
			public void onClickStart(int x, int y,
					final PointerEventType type) {
				app.getGuiManager()
						.setOnScreenKeyboardTextField(MathQuillTreeItem.this);
				// prevent that keyboard is closed on clicks (changing
				// cursor position)
				CancelEventTimer.keyboardSetVisible();
			}
		});

		if (app.has(Feature.AV_INPUT_BUTTON_COVER)) {
			if (!app.has(Feature.AV_SINGLE_TAP_EDIT)) {
				content.insert(getClearInputButton(), 1);
			}
			controls.setVisible(false);
		}

		return true;
	}

	public void onEnter(final boolean keepFocus) {
		if (!editing) {
			return;
		}
		stopEditing(getText(), new AsyncOperation<GeoElementND>() {

			@Override
			public void callback(GeoElementND obj) {
				if (keepFocus) {
					MathQuillHelper.stornoFormulaMathQuillGGB(
							MathQuillTreeItem.this, latexItem.getElement());
				}

			}
		});
	}

	@Override
	public String getText() {
		return getEditorValue(false);
	}

	/**
	 * @param latexValue
	 *            true for latex output, false for plain text
	 * @return editor content
	 */
	protected String getEditorValue(boolean latexValue) {
		if (latexItem == null)
			return "";

		String ret = MathQuillHelper
				.getActualEditedValue(latexItem.getElement(), latexValue);

		if (ret == null)
			return "";

		return ret;
	}

	@Override
	protected void blurEditor() {
		MathQuillHelper.focusEquationMathQuillGGB(latexItem, false);
	}

	private void editLatexMQ(String text0) {
		if (latexItem == null) {
			latexItem = new FlowPanel();
		}
		latexItem.clear();
		latexItem.addStyleName("avTextItem");
		updateColor(latexItem);

		content.clear();

		String text = text0;
		if (text0 == null) {
			text = "";
		}
		text = MathQuillHelper.inputLatexCosmetics(text);

		String latexString = "";
		if (!isInputTreeItem()) {
			latexString = (isDefinitionAndValue() ? "\\mathrm {"
					: " \\mathbf {") + text + "}";
		}

		if (!isInputTreeItem() && geo.needToShowBothRowsInAV()) {
			createDVPanels();
			if (latex) {
				definitionPanel.addStyleName("avDefinition");
			} else {
				definitionPanel.addStyleName("avDefinitionPlain");
			}
			updateValuePanel();
			outputPanel.add(valuePanel);
			content.add(latexItem);
			content.add(outputPanel);

			latexItem.addStyleName("avDefinition");

			MathQuillHelper.drawEquationAlgebraView(latexItem, latexString,
					isInputTreeItem());

		} else {
			latexItem.removeStyleName("avDefinition");
			content.add(latexItem);
			MathQuillHelper.drawEquationAlgebraView(latexItem, latexString,
					isInputTreeItem());
		}
	}

	@Override
	protected void renderLatex(String text0, Widget w, boolean forceMQ) {
		if (definitionAndValue) {
			if (forceMQ) {
				editLatexMQ(text0);
			} else {
				replaceToCanvas(text0, w);
			}

		} else {
			if (forceMQ) {
				renderLatexEdit(text0);
			} else {
				renderLatexCanvas(text0, w.getElement());
			}
		}
	}

	private void renderLatexEdit(String text0) {
		if (latexItem == null) {
			latexItem = new FlowPanel();
		}
		latexItem.clear();
		latexItem.addStyleName("avTextItem");
		updateColor(latexItem);

		content.clear();

		String text = text0;
		if (text0 == null) {
			text = "";
		}
		text = MathQuillHelper.inputLatexCosmetics(text);

		String latexString = "";
		if (!isInputTreeItem()) {
			latexString = isDefinitionAndValue() ? "\\mathrm {"
					: " \\mathbf {" + text + "}";
		}

		content.add(latexItem);
		MathQuillHelper.drawEquationAlgebraView(latexItem, latexString,
				isInputTreeItem());
	}

	protected void clearInput() {
		MathQuillHelper.stornoFormulaMathQuillGGB(MathQuillTreeItem.this,
				latexItem);

	}

	public KeyboardListener getKeyboardListener() {
		return new MathQuillProcessing(this);
	}

	@Override
	public String getCommand() {
		return getEquationEditor().getCurrentCommand();
	}

	protected EquationEditor getEquationEditor() {
		return null;

	}

	/**
	 * @param fkey
	 *            2 for F2, 3 for F3 etc
	 * @param geo2
	 *            selected element
	 */
	public void handleFKey(int fkey, GeoElement geo2) {
		switch (fkey) {
		case 3: // F3 key: copy definition to input field
			getEquationEditor().setText(geo2.getDefinitionForInputBar(), true);
			ensureEditing();
			break;

		case 4: // F4 key: copy value to input field
			getEquationEditor().autocomplete(
					" " + geo2.getValueForInputBar() + " ", false);
			ensureEditing();
			break;

		case 5: // F5 key: copy name to input field
			getEquationEditor().autocomplete(
					" " + geo2.getLabel(StringTemplate.defaultTemplate) + " ",
					false);
			ensureEditing();
			break;
		}

	}

	protected String stopCommon(String newValue) {
		return EquationEditor.stopCommon(newValue);
	}

	/**
	 * Method to be overridden in InputTreeItem
	 */
	@Override
	public boolean hideSuggestions() {
		return false;
	}

	/**
	 * Method to be overridden in InputTreeItem
	 * 
	 * @return
	 */
	@Override
	public void showOrHideSuggestions() {
		// override
	}

	/**
	 * Method to be overridden in InputTreeItem
	 */
	@Override
	public void shuffleSuggestions(boolean down) {
		// override
	}

	@Override
	public void onBlur(BlurEvent be) {
		// to be overridden in InputTreeItem
	}

	@Override
	public void onFocus(FocusEvent be) {
		// to be overridden in InputTreeItem
		// AppW.anyAppHasFocus = true;
	}
	
	public void onKeyPress(String s) {
		// TODO Auto-generated method stub

	}

	/**
	 * Stop new formula creation Much of this code is copied from
	 * AlgebraInputW.onKeyUp
	 * 
	 * @param newValue0
	 * @return boolean whether it was successful
	 */
	public boolean stopNewFormulaCreation(final String newValue0,
			final String latexx, final AsyncOperation<Object> cb) {
		return stopNewFormulaCreation(newValue0, latexx, cb, true);
	}

	/**
	 * Stop new formula creation Much of this code is copied from
	 * AlgebraInputW.onKeyUp
	 * 
	 * @param newValue0
	 * @return boolean whether it was successful
	 */
	public boolean stopNewFormulaCreation(final String newValue0,
			final String latexx, final AsyncOperation<Object> cb,
			final boolean keepFocus) {

		// TODO: move to InputTreeItem? Wouldn't help much...

		String newValue = newValue0;
		if (newValue0 != null) {
			newValue = stopCommon(newValue);
		}

		app.getKernel().clearJustCreatedGeosInViews();
		final String input = kernel.getInputPreviewHelper().getInput(newValue);

		if (input == null || input.length() == 0) {
			app.getActiveEuclidianView().requestFocusInWindow();
			scrollIntoView();
			return false;
		}
		final boolean valid = input.equals(newValue);
		final String newValueF = newValue;
		app.setScrollToShow(true);

		try {
			AsyncOperation<GeoElementND[]> callback = new AsyncOperation<GeoElementND[]>() {

				@Override
				public void callback(GeoElementND[] geos) {

					if (geos == null) {
						// inputField.getTextBox().setFocus(true);
						getController().setFocus(true);
						return;
					}

					// need label if we type just eg
					// lnx
					if (geos.length == 1 && !geos[0].isLabelSet()) {
						geos[0].setLabel(geos[0].getDefaultLabel());
					}

					InputHelper.updateProperties(geos,
							app.getActiveEuclidianView());
					app.setScrollToShow(false);
					if (!valid) {
						addToHistory(input, null);
						addToHistory(newValueF, latexx);
					} else {
						addToHistory(input, latexx);
					}

					Scheduler.get()
							.scheduleDeferred(new Scheduler.ScheduledCommand() {
								@Override
								public void execute() {
									scrollIntoView();
									if (isInputTreeItem() && keepFocus) {
										getController().setFocus(true);
									}
								}
							});

					// actually this (and only this) means return true!
					cb.callback(null);
					if (!keepFocus) {
						setText("");
					}
					updateLineHeight();
				}

			};

			app.getKernel().getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(input, true,
							getErrorHandler(valid), true, callback);

		} catch (Exception ee) {
			// TODO: better exception handling
			// GOptionPaneW.setCaller(inputField.getTextBox());// we have no
			// good FocusWidget
			// app.showError(ee, inputField);
			if (ee.getCause() instanceof MyError) {
				app.showError((MyError) ee.getCause());
			} else {
				app.showError(ee.getMessage());// we use what we have
			}
			return false;
		} catch (MyError ee) {
			// TODO: better error handling
			// GOptionPaneW.setCaller(inputField.getTextBox());// we have no
			// good FocusWidget
			// inputField.showError(ee);
			app.showError(ee);// we use what we have
			return false;
		}
		// there is also a timer to make sure it scrolls into view
		if (keepFocus) {
			Timer tim = new Timer() {
				@Override
				public void run() {
					scrollIntoView();
					if (isInputTreeItem()) {
						getController().setFocus(true);
					}
				}
			};
			tim.schedule(500);
		} else {
			blurEditor();
		}
		return true;
	}

	public void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub

	}


}
