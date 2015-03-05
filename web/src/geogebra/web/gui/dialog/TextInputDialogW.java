package geogebra.web.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.TextInputDialog;
import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.html5.Browser;
import geogebra.html5.main.AppW;

public class TextInputDialogW extends InputDialogW implements TextInputDialog{

	GeoText editGeo;
	GeoPointND startPoint;
	private TextEditPanel editor;

	public TextInputDialogW(App app2, String title, GeoText editGeo,
            GeoPointND startPoint, int cols, int rows, boolean isTextMode) {
	    // TODO Auto-generated constructor stub
		super(false);
		this.app = (AppW) app2;
		this.startPoint = startPoint;
//		this.isTextMode = isTextMode;
		this.editGeo = editGeo;
//		textInputDialog = this;
		inputHandler = new TextInputHandler();

//		isIniting = true;		

		createGUI(title, "", false, cols, rows, /*false*/ true, false, false, false,
				DialogType.DynamicText);
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
	}

	int getFontStyle() {
		if(editor == null){
			App.debug("null editor");
		}
		return editor.getFontStyle();
	}


	boolean isLatex() {
		if(editor == null){
			App.debug("null editor");
		}
		return editor.isLatex();
	}

	boolean isSerif() {
		if(editor == null){
			App.debug("null editor");
		}
		return editor.isSerif();
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

		public boolean processInput(String inputValue) {
			if (inputValue == null)
				return false;

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

			if (inputValue.equals("\"\""))
				return false;

			// create new GeoText
			boolean createText = editGeo == null;
			if (createText) {
				GeoElement[] ret = kernel.getAlgebraProcessor()
						.processAlgebraCommand(inputValue, false);
				if (ret != null && ret[0] instanceof GeoText) {
					GeoText t = (GeoText) ret[0];
					t.setLaTeX(isLatex(), true);
					t.setFontStyle(getFontStyle());
					t.setSerifFont(isSerif());
					// make sure for new LaTeX texts we get nice "x"s
					if (isLatex())
						t.setSerifFont(true);

					if (startPoint.isLabelSet()) {
						try {
							t.setStartPoint(startPoint);
						} catch (Exception e) {
						}
					} else {

						// // Michael Borcherds 2008-04-27 changed to RealWorld
						// not absolute
						// startpoint contains mouse coords
						// t.setAbsoluteScreenLoc(euclidianView.toScreenCoordX(startPoint.inhomX),
						// euclidianView.toScreenCoordY(startPoint.inhomY));
						// t.setAbsoluteScreenLocActive(true);
						Coords coords = startPoint.getInhomCoordsInD3();
						t.setRealWorldLoc(coords.getX(), coords.getY());
						t.setAbsoluteScreenLocActive(false);
					}

					// make sure (only) the output of the text tool is selected
					kernel.getApplication().getActiveEuclidianView()
							.getEuclidianController()
							.memorizeJustCreatedGeos(ret);

					t.updateRepaint();
					app.storeUndoInfo();
					return true;
				}
				return false;
			}

			// change existing text
			try {
				GeoText newText = (GeoText) kernel.getAlgebraProcessor()
						.changeGeoElement(editGeo, inputValue, true, true);

				
				// make sure newText is using correct LaTeX setting
				newText.setLaTeX(isLatex(), true);

				if (newText.getParentAlgorithm() != null)
					newText.getParentAlgorithm().update();
				else
					newText.updateRepaint();

				app.doAfterRedefine(newText);

				// make redefined text selected
				app.getSelectionManager().addSelectedGeo(newText);
				return true;
			} catch (Exception e) {
				app.showError("ReplaceFailed");
				return false;
			} catch (MyError err) {
				app.showError(err);
				return false;
			}
		}
	}

	public void focus() {
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

	public void reInitEditor(GeoText text, GeoPointND startPoint2) {
		this.startPoint = startPoint2;
		setGeoText(text);
		focus();
    }

	public void setGeoText(GeoText geo) {

//		handlingDocumentEventOff = true;

		editGeo = geo;
		editor.setEditGeo(geo);
		boolean createText = geo == null;
		
		
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
	
	@Override
    public void setVisible(boolean visible) {
		
		inputPanel.setVisible(visible);
		wrappedPopup.setVisible(visible);
		if (visible){
			inputPanel.setTextComponentFocus();
		}else{
			if(app!=null){
				app.setErrorHandler(null);
			}
		}
	}
	
}
