package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.TextInputDialog;
import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Web implementation of Text Dialog
 */
public class TextInputDialogW extends InputDialogW implements TextInputDialog {
	/** edited text */
	GeoText editGeo;
	/** start point */
	GeoPointND startPoint;
	/** whether to position text in RW coords or EV */
	boolean rw;
	private TextEditPanel editor;
	private int cols;
	private int rows;
	private String dialogTitle;
	private boolean isTextMode;

	/**
	 * @param app2
	 *            app
	 * @param title
	 *            title
	 * @param editGeo
	 *            text to edit
	 * @param startPoint
	 *            start point
	 * @param rw
	 *            whether to use RW for position
	 * @param cols
	 *            columns of edit area
	 * @param rows
	 *            rows of edit area
	 * @param isTextMode
	 *            whether text mode was active when this was called
	 */
	public TextInputDialogW(App app2, String title, GeoText editGeo,
            GeoPointND startPoint, boolean rw, int cols, int rows, boolean isTextMode) {
		super(false, (AppW) app2, false);
		this.startPoint = startPoint;
		this.rw = rw;
		dialogTitle = title;
		this.cols = cols;
		this.rows = rows;
		this.isTextMode = isTextMode;
		this.editGeo = editGeo;
		setInputHandler(new TextInputHandler());

		createTextGUI(title, "", false, cols, rows, /* false */ true, false,
				false, false,
				DialogType.DynamicText);
	}

	private void createTextGUI(String title, String message,
			boolean autoComplete, int columns, int rows1,
			boolean showSymbolPopupIcon, boolean selectInitText,
			boolean showProperties, boolean showApply, DialogType type) {
		super.createGUI(title, message, autoComplete, columns, rows1,
				showSymbolPopupIcon, selectInitText, showProperties, showApply,
				type);
		app.unregisterPopup(wrappedPopup);
		editor = inputPanel.getTextAreaComponent();
		if (editor != null) {
			editor.setText(editGeo);
		} else if (inputPanel.getTextComponent() != null) {
			// this branch probably does not run (rows > 1), educated guess
			inputPanel.getTextComponent().setText(editGeo.getTextString());
		}

		wrappedPopup.addStyleName("TextInputDialog");
		wrappedPopup.center();
		wrappedPopup.show();

		focus();
		wrappedPopup.addCloseHandler(new CloseHandler<GPopupPanel>() {

			@Override
			public void onClose(CloseEvent<GPopupPanel> event) {
				resetEditor();
			}
		});
	}

	/*
	 * Close iOS keyboard at creating text. At using TextInputDialog iOS
	 * keyboard has to be closed programmatically at clicking on OK or Cancel,
	 * otherwise it won't be closed after the dialog will be hidden.
	 */
	@Override
	protected void closeIOSKeyboard() {
		if (app.isWhiteboardActive()) {
			return;
		}
		if (inputPanel == null || inputPanel.getText().equals("")
				|| inputPanel.getTextAreaComponent() == null) {
			return;
		}
		TextBox dummyTextBox = new TextBox();
		editor.add(dummyTextBox);
		dummyTextBox.setFocus(true);
		dummyTextBox.setFocus(false);
		editor.remove(dummyTextBox);
	}

	/**
	 * Removes current editor
	 */
	void resetEditor() {
		editor = null;
	}

	@Override
	protected void resetMode() {
		if (isTextMode) {
			app.setMode(EuclidianConstants.MODE_TEXT);
		}
	}

	/**
	 * Updates latex / serif / font size of the text from GUI
	 * 
	 * @param t
	 *            text
	 */
	void updateTextStyle(GeoText t) {
		if (editor == null) {
			Log.debug("null editor");
			return;
		}
		editor.updateTextStyle(t);
	}

	/**
	 * @return whether latex checkbox is active
	 */
	boolean isLatex() {
		if (editor == null) {
			Log.debug("null editor");
			return false;
		}
		return editor.isLatex();
	}
	
	// =============================================================
	// TextInputHandler
	// =============================================================

	/**
	 * Handles creating or redefining GeoText using the current editor string.
	 * 
	 */
	private class TextInputHandler implements InputHandler {

		private Kernel kernel;

		TextInputHandler() {
			kernel = app.getKernel();
		}

		@Override
		public void processInput(String input, ErrorHandler handler,
				AsyncOperation<Boolean> callback) {
			if (input == null) {
				callback.callback(false);
				return;
			}
			String inputValue = TextEditPanel.handleUnderscores(app, input, isLatex());
			// no quotes?
			if (inputValue.indexOf('"') < 0) {
				// this should become either
				// (1) a + "" where a is an object label or
				// (2) "text", a plain text

				// ad (1) OBJECT LABEL
				// add empty string to end to make sure
				// that this will become a text object
				if (kernel.lookupLabel(inputValue.trim()) != null) {
					inputValue = "(" + inputValue + ") + \"\"";
				}
				// ad (2) PLAIN TEXT
				// add quotes to string
				else {
					inputValue = "\"" + inputValue + "\"";
				}
			} else {
				// replace \n\" by \"\n, this is useful for e.g.:
				// "a = " + a +
				// "b = " + b
				inputValue = inputValue.replaceAll("\n\"", "\"\n");
			}

			if ("\"\"".equals(inputValue)) {
				callback.callback(false);
				return;
			}
			// create new GeoText
			boolean createText = editGeo == null;
			handler.resetError();
			if (createText) {
				kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionHandling(inputValue,
								false, handler, true,
								getCallback(callback));
				return;

			}

			// change existing text
			try {
				kernel.getAlgebraProcessor().changeGeoElement(editGeo,
						inputValue, true, true, TextInputDialogW.this,
						new AsyncOperation<GeoElementND>() {

							@Override
							public void callback(GeoElementND newText) {
								if (newText instanceof GeoText) {
									// make sure newText is using correct LaTeX
									// setting
									((GeoText) newText).setLaTeX(isLatex(),
											true);

									if (newText.getParentAlgorithm() != null) {
										newText.getParentAlgorithm().update();
									} else {
										newText.updateRepaint();
									}

									app.doAfterRedefine(newText);

									// make redefined text selected
									app.getSelectionManager()
											.addSelectedGeo(newText);
								}

							}
						});

				callback.callback(true);
			} catch (Exception e) {
                app.showGenericError(e);
				callback.callback(false);
			}
		}

		private AsyncOperation<GeoElementND[]> getCallback(
				final AsyncOperation<Boolean> callback) {
			// TODO Auto-generated method stub
			return new AsyncOperation<GeoElementND[]>() {

				@Override
				public void callback(GeoElementND[] ret) {
					if (ret != null && ret[0] instanceof GeoText) {
						GeoText t = (GeoText) ret[0];
						t.setEuclidianVisible(true);
						positionText(t);

						app.storeUndoInfo();
						callback.callback(true);
						return;
					}
					callback.callback(false);
					return;

				}

			};
		}

		protected void positionText(GeoText t) {
			updateTextStyle(t);

			EuclidianViewInterfaceCommon activeView = kernel.getApplication()
					.getActiveEuclidianView();

			if (startPoint.isLabelSet()) {
				t.checkVisibleIn3DViewNeeded();
				try {
					t.setStartPoint(startPoint);
				} catch (Exception e) {
					// circular definition
				}
			} else {

				// changed to
				// RealWorld
				// not absolute
				// startpoint contains mouse coords
				// t.setAbsoluteScreenLoc(euclidianView.toScreenCoordX(startPoint.inhomX),
				// euclidianView.toScreenCoordY(startPoint.inhomY));
				// t.setAbsoluteScreenLocActive(true);
				if (rw) {
					Coords coords = startPoint.getInhomCoordsInD3();
					t.setRealWorldLoc(
							activeView.toRealWorldCoordX(
									coords.getX()),
							activeView.toRealWorldCoordY(
									coords.getY()));
					t.setAbsoluteScreenLocActive(false);
				} else {
					Coords coords = startPoint.getInhomCoordsInD3();
					t.setAbsoluteScreenLoc((int) coords.getX(),
							(int) coords.getY());
					t.setAbsoluteScreenLocActive(true);

				}

				// when not a point clicked, show text only in
				// active
				// view
				if (activeView.isEuclidianView3D()) {
					// we need to add it to 3D view since by default
					// it may not
					kernel.getApplication().addToViews3D(t);
					app.removeFromEuclidianView(t);
					t.setVisibleInViewForPlane(false);
					kernel.getApplication()
							.removeFromViewsForPlane(t);
				} else if (activeView.isDefault2D()) {
					if (kernel.getApplication()
							.isEuclidianView3Dinited()) {
						kernel.getApplication()
								.removeFromViews3D(t);
					} else {
						t.removeViews3D();
					}
					t.setVisibleInViewForPlane(false);
					kernel.getApplication()
							.removeFromViewsForPlane(t);
				} else { // view for plane
					app.removeFromEuclidianView(t);
					if (kernel.getApplication()
							.isEuclidianView3Dinited()) {
						kernel.getApplication()
								.removeFromViews3D(t);
					} else {
						t.removeViews3D();
					}
					t.setVisibleInViewForPlane(true);
					kernel.getApplication().addToViewsForPlane(t);
				}
			}
			// make sure (only) the output of the text tool is
			// selected
			activeView.getEuclidianController()
					.memorizeJustCreatedGeos(t.asArray());
			t.updateRepaint();
		}
	}

	private void focus() {
		if (inputPanel.getTextAreaComponent() != null) {
			// probably this branch will run (rows > 1)
			if (Browser.isFirefox()) {
				// Code that works in Firefox but not in IE and Chrome
				inputPanel.getTextAreaComponent().getTextArea().getElement()
				        .blur();
				inputPanel.getTextAreaComponent().getTextArea().getElement()
				        .focus();
			} else {
				// Code that works in Chrome but not in Firefox
				inputPanel.getTextAreaComponent().getTextArea().setFocus(false);
				inputPanel.getTextAreaComponent().getTextArea().setFocus(true);
			}
		} else if (inputPanel.getTextComponent() != null) {
			// what if? educated guess
			inputPanel.getTextComponent().setFocus(false);
			inputPanel.getTextComponent().setFocus(true);
		}
	}

	@Override
	public void reInitEditor(GeoText text, GeoPointND startPoint2,
			boolean rw1) {
		if (editor == null) {
			createTextGUI(dialogTitle, "", false, cols, rows, /* false */true,
					false,
					false, false, DialogType.DynamicText);

			// return;
		}
		isTextMode = app.getMode() == EuclidianConstants.MODE_TEXT;
		this.startPoint = startPoint2;
		this.rw = rw1;
		setGeoText(text);
		focus();
    }

	private void setGeoText(GeoText geo) {

//		handlingDocumentEventOff = true;

		editGeo = geo;
		editor.setEditGeo(geo);

		//isLaTeX = geo == null ? false : geo.isLaTeX();
				
		inputPanel.getTextAreaComponent().setText(geo);
		
	//	inputPanel.getTextAreaComponent().setCaretPosition(0); 
		//editor.setCaretPosition(0);
//		cbLaTeX.setSelected(false);
//		if (isLaTeX) {
//			cbLaTeX.doClick();
//		}

//		handlingDocumentEventOff = false;
//		updatePreviewText();
//		editOccurred = false;
	}
	
	@Override
	public void setLabels() {
		super.setLabels();
		if (editor != null) {
			editor.setLabels();
		}
	}
	
	/*
	 * @Override public void setVisible(boolean visible) {
	 * 
	 * inputPanel.setVisible(visible); wrappedPopup.setVisible(visible); if
	 * (visible){ inputPanel.setTextComponentFocus(); }else{ if(app!=null){
	 * app.setErrorHandler(null); } } }
	 */
	
}
