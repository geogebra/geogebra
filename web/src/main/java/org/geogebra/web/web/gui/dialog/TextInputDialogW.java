package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.TextInputDialog;
import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

public class TextInputDialogW extends InputDialogW implements TextInputDialog{

	GeoText editGeo;
	GeoPointND startPoint;
	boolean rw;
	private TextEditPanel editor;
	private int cols;
	private int rows;
	private String dialogTitle;

	public TextInputDialogW(App app2, String title, GeoText editGeo,
            GeoPointND startPoint, boolean rw, int cols, int rows, boolean isTextMode) {
		super(false, (AppW) app2);
		this.startPoint = startPoint;
		this.rw = rw;
		dialogTitle = title;
		this.cols = cols;
		this.rows = rows;
//		this.isTextMode = isTextMode;
		this.editGeo = editGeo;
//		textInputDialog = this;
		inputHandler = new TextInputHandler();

//		isIniting = true;		

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

			public void onClose(CloseEvent<GPopupPanel> event) {
				resetEditor();
			}
		});
	}

	/**
	 * Removes current editor
	 */
	void resetEditor() {
		editor = null;
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
		t.setLaTeX(editor.isLatex(), true);
		t.setFontStyle(editor.getFontStyle());
		t.setSerifFont(editor.isSerif());
		// make sure for new LaTeX texts we get nice "x"s
		if (editor.isLatex())
			t.setSerifFont(true);

	}

	/**
	 * @return whether latex checkbox is active
	 */
	boolean isLatex() {
		if (editor == null) {
			Log.debug("null editor");
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

		public void processInput(String input,
				AsyncOperation<Boolean> callback) {
			if (input == null) {
				callback.callback(false);
				return;
			}
			String inputValue = input;
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

			if (inputValue.equals("\"\"")) {
				callback.callback(false);
				return;
			}
			// create new GeoText
			boolean createText = editGeo == null;
			if (createText) {
				kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionHandling(inputValue,
								false, app.getErrorHandler(), true,
								getCallback(callback));

			}

			// change existing text
			try {
				kernel.getAlgebraProcessor().changeGeoElement(editGeo,
						inputValue, true, true,
						new AsyncOperation<GeoElement>() {

							@Override
							public void callback(GeoElement newText) {
								if (newText instanceof GeoText) {
									// make sure newText is using correct LaTeX
									// setting
									((GeoText) newText).setLaTeX(isLatex(),
											true);

									if (newText.getParentAlgorithm() != null)
										newText.getParentAlgorithm().update();
									else
										newText.updateRepaint();

									app.doAfterRedefine(newText);

									// make redefined text selected
									app.getSelectionManager()
											.addSelectedGeo(newText);
								}

							}
						});

				callback.callback(true);
			} catch (Exception e) {
				app.showError(e.getMessage());
				callback.callback(false);
			}
		}

		private AsyncOperation<GeoElement[]> getCallback(
				final AsyncOperation<Boolean> callback) {
			// TODO Auto-generated method stub
			return new AsyncOperation<GeoElement[]>() {

				@Override
				public void callback(GeoElement[] ret) {
					if (ret != null && ret[0] instanceof GeoText) {
						GeoText t = (GeoText) ret[0];
						updateTextStyle(t);

						EuclidianViewInterfaceCommon activeView = kernel
								.getApplication().getActiveEuclidianView();

						if (startPoint.isLabelSet()) {
							t.checkVisibleIn3DViewNeeded();
							try {
								t.setStartPoint(startPoint);
							} catch (Exception e) {
								// circular definition
							}
						} else {

							// // Michael Borcherds 2008-04-27 changed to
							// RealWorld
							// not absolute
							// startpoint contains mouse coords
							// t.setAbsoluteScreenLoc(euclidianView.toScreenCoordX(startPoint.inhomX),
							// euclidianView.toScreenCoordY(startPoint.inhomY));
							// t.setAbsoluteScreenLocActive(true);
							if (rw) {
								Coords coords = startPoint.getInhomCoordsInD3();
								t.setRealWorldLoc(coords.getX(), coords.getY());
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
								.memorizeJustCreatedGeos(ret);

						t.updateRepaint();
						app.storeUndoInfo();
						callback.callback(true);
						return;
					}
					callback.callback(false);
					return;

				}

			};
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

	public void reInitEditor(GeoText text, GeoPointND startPoint2,
			boolean rw1) {
		if (editor == null) {
			createTextGUI(dialogTitle, "", false, cols, rows, /* false */true,
					false,
					false, false, DialogType.DynamicText);

			// return;
		}
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
	public void setLabels(){
		super.setLabels();
		if(editor != null){
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
