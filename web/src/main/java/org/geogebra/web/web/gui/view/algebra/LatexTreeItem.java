package org.geogebra.web.web.gui.view.algebra;


import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.io.latex.GeoGebraSerializer;
import org.geogebra.common.io.latex.ParseException;
import org.geogebra.common.io.latex.Parser;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.inputbar.AlgebraInputW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * 
 * @author Zbynek
 *
 */
public class LatexTreeItem extends RadioTreeItem
		implements MathFieldListener, BlurHandler {

	private MathFieldW mf;
	/** Listener for enter */
	RetexKeyboardListener retexListener;

	/**
	 * @param geo0
	 *            geo element
	 */
	public LatexTreeItem(GeoElement geo0) {
		super(geo0);
	}

	/**
	 * @param kernel
	 *            Kernel
	 */
	public LatexTreeItem(Kernel kernel) {
		super(kernel);
		this.insertHelpToggle();
		addDomHandlers(main);
		if (canvas != null) {
			canvas.getElement().getStyle().setMarginLeft(40, Unit.PX);
			canvas.getElement().getStyle().setMarginTop(5, Unit.PX);
		}
	}

	@Override
	protected boolean startEditing(boolean substituteNumbers) {
		String text = geo == null ? "" : geo.getDefinitionForEditor();
		if (text == null) {
			return false;
		}
		if (errorLabel != null) {
			errorLabel.setText("");
		}

		Widget old = latex ? (canvas != null ? canvas : latexItem)
					: getPlainTextItem();

		renderLatex(text, old.getElement());
		mf.setFocus(true);

		canvas.addBlurHandler(this);
		// DrawEquationW.editEquationMathQuillGGB(this, latexItem, false);

		app.getGuiManager().setOnScreenKeyboardTextField(this.retexListener);
		CancelEventTimer.keyboardSetVisible();
		ClickStartHandler.init(main, new ClickStartHandler(false, false) {
			@Override
			public void onClickStart(int x, int y,
					final PointerEventType type) {
				app.getGuiManager()
						.setOnScreenKeyboardTextField(retexListener);
				// prevent that keyboard is closed on clicks (changing
				// cursor position)
				CancelEventTimer.keyboardSetVisible();
			}
		});

		return true;
	}

	/**
	 * @param old
	 *            what to replace
	 */
	private void renderLatex(String text0, Element old) {
		// if (!forceMQ) {
		// canvas = DrawEquationW.paintOnCanvas(geo, text0, canvas,
		// getFontSize());
		//
		// if (canvas != null && ihtml.getElement().isOrHasChild(old)) {
		// ihtml.getElement().replaceChild(canvas.getCanvasElement(), old);
		// }
		//
		// }
		Log.debug("RENDERING LATEX");
		// // // Log.debug(REFX + "renderLatex 2");
		if (latexItem == null) {
			latexItem = new FlowPanel();
		}
		latexItem.clear();
		latexItem.addStyleName("avTextItem");
		// TODO updateColor(latexItem);

		ihtml.clear();


		ensureCanvas();
		ihtml.add(canvas);

		setText(text0);
		retexListener = new RetexKeyboardListener(canvas, mf);
		app.getAppletFrame().showKeyBoard(true, retexListener, false);


	}

	private boolean ensureCanvas() {
		if (canvas == null) {
			Log.debug("CANVAS IS NULL");
			canvas = Canvas.createIfSupported();
			mf = new MathFieldW(canvas, canvas.getContext2d(), this);
			return true;
		}
		if (mf == null) {
			mf = new MathFieldW(canvas, canvas.getContext2d(), this);
		}
		return false;
	}

	@Override
	public void onEnter() {

		onEnter(geo == null);
		
	}

	@Override
	public void setFocus(boolean b, boolean sv) {
		if (b) {
			removeDummy();
		}
		if (ensureCanvas()) {
			main.clear();
			if (geo == null) {
				insertHelpToggle();
				canvas.getElement().getStyle().setMarginLeft(40, Unit.PX);
				canvas.getElement().getStyle().setMarginTop(5, Unit.PX);
			}
			ihtml.add(canvas);
			main.add(ihtml);
		}
		if (b) {
			canvas.setVisible(true);
		} else {
			addDummyLabel();
		}
		mf.setFocus(b);
		//canvas.setFocus(b);
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
		if (geo == null) {
			if (StringUtil.empty(getText())) {
				return;
			}
			createGeoFromInput(false);
			if (!keepFocus) {
				setFocus(false, false);
			}
			return;
		}
		stopEditing(getText(), new AsyncOperation<GeoElement>() {

			@Override
			public void callback(GeoElement obj) {
				if (obj != null && !keepFocus) {
					obj.update();
				}
			}
		});
	}

	private void createGeoFromInput(final boolean keepFocus) {
		String newValue = getText();
		final String input = app.has(Feature.INPUT_BAR_PREVIEW)
				? kernel.getInputPreviewHelper().getInput(newValue) : newValue;
		final boolean valid = !app.has(Feature.INPUT_BAR_PREVIEW)
				|| input.equals(newValue);
		AsyncOperation<GeoElement[]> callback = new AsyncOperation<GeoElement[]>() {

			@Override
			public void callback(GeoElement[] geos) {

				if (geos == null) {
					// inputField.getTextBox().setFocus(true);
					setFocus(true);
					return;
				}

				// need label if we type just eg
				// lnx
				if (geos.length == 1 && !geos[0].labelSet) {
					geos[0].setLabel(geos[0].getDefaultLabel());
				}

				InputHelper.updateProperties(geos, app.getActiveEuclidianView());
				app.setScrollToShow(false);
				/**
				 * if (!valid) { addToHistory(input, null);
				 * addToHistory(newValueF, latexx); } else { addToHistory(input,
				 * latexx); }
				 */

				Scheduler.get()
						.scheduleDeferred(new Scheduler.ScheduledCommand() {
							@Override
							public void execute() {
								scrollIntoView();
								if (isInputTreeItem() && keepFocus) {
									setFocus(true);
								}
							}
						});

				// actually this (and only this) means return true!
				// cb.callback(null);
				if (!keepFocus) {
					setText("");
				}
				updateLineHeight();
			}

		};

		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(input, true,
						getErrorHandler(valid), true, callback);

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
	}
	@Override
	public void setLabels() {
		if (dummyLabel != null) {
			dummyLabel.setText(app.getPlain("InputLabel") + Unicode.ellipsis);
		}
	}

	@Override
	public void scrollCursorIntoView() {
		// if (latexItem != null) {
		// DrawEquationW.scrollCursorIntoView(this, latexItem.getElement(),
		// isInputTreeItem());
		// }
	}

	@Override
	public String getCommand() {
		// TODO get it from JLM
		return null;
	}

	@Override
	public void autocomplete(String text) {
		GuiManagerW.makeKeyboardListener(retexListener).insertString(text);

	}

	@Override
	protected void focusAfterHelpClosed() {
		setFocus(true);
	}

	public void onKeyTyped() {
		updatePreview();

	}

	private void updatePreview() {
		if (app.has(Feature.INPUT_BAR_PREVIEW)) {
			String text = getText();
			app.getKernel().getInputPreviewHelper().updatePreviewFromInputBar(
					text, AlgebraInputW.getWarningHandler(this, app));
		}

	}

	@Override
	public void onBlur(BlurEvent event) {


			if (isEmpty()) {
				addDummyLabel();
			}

			if (((AlgebraViewW) av).isNodeTableEmpty()) {
				// #5245#comment:8, cases B and C excluded
				updateGUIfocus(event == null ? this : event.getSource(), true);
			}

	}

}
