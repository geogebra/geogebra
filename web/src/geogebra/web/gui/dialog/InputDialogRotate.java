package geogebra.web.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
import geogebra.web.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public class InputDialogRotate extends AngleInputDialog implements KeyUpHandler {

	private static final long serialVersionUID = 1L;

	GeoPolygon[] polys;
	GeoPointND[] points;
	GeoElement[] selGeos;

	private Kernel kernel;
	private static String defaultRotateAngle = "45\u00b0"; // 45 degrees

	public InputDialogRotate(AppW app, String title,
			InputHandler handler, GeoPolygon[] polys, GeoPointND[] points,
			GeoElement[] selGeos, Kernel kernel) {
		super(app, app.getPlain("Angle"), title, defaultRotateAngle, false,
				handler, false);

		this.polys = polys;
		this.points = points;
		this.selGeos = selGeos;
		this.kernel = kernel;

		this.inputPanel.getTextComponent().getTextField().addKeyUpHandler(this);
	}

	/**
	 * Handles button clicks for dialog.
	 */
	@Override
	public void onClick(ClickEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent().getTextField()) {
				//FIXME setVisibleForTools(!processInput());
				if (!processInput()) {
					wrappedPopup.show();
					inputPanel.getTextComponent().hideTablePopup();
				} else {
					wrappedPopup.hide();
					inputPanel.getTextComponent().hideTablePopup();
					app.getActiveEuclidianView().requestFocusInWindow();
				}
			//} else if (source == btApply) {
			//	processInput();
			} else if (source == btCancel) {
				//FIXME setVisibleForTools(false);
				wrappedPopup.hide();
				inputPanel.getTextComponent().hideTablePopup();
				app.getActiveEuclidianView().requestFocusInWindow();
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			//FIXME setVisibleForTools(false);
			wrappedPopup.hide();
			inputPanel.getTextComponent().hideTablePopup();
			app.getActiveEuclidianView().requestFocusInWindow();
		}
	}

	private boolean processInput() {
		
		defaultRotateAngle = DialogManagerW.rotateObject(app, inputPanel.getText(), rbClockWise.getValue(), polys, points, selGeos);
		
		return true;
		/*

		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		inputText = inputPanel.getText();

		// negative orientation ?
		if (rbClockWise.isSelected()) {
			inputText = "-(" + inputText + ")";
		}

		boolean success = inputHandler.processInput(inputText);

		cons.setSuppressLabelCreation(oldVal);

		if (success) {
			// GeoElement circle = kernel.Circle(null, geoPoint1,
			// ((NumberInputHandler)inputHandler).getNum());
			NumberValue num = ((NumberInputHandler) inputHandler).getNum();
			// geogebra.gui.AngleInputDialog dialog =
			// (geogebra.gui.AngleInputDialog) ob[1];
			String angleText = getText();

			// keep angle entered if it ends with 'degrees'
			if (angleText.endsWith("\u00b0"))
				defaultRotateAngle = angleText;
			else
				defaultRotateAngle = "45" + "\u00b0";

			if (polys.length == 1) {

				GeoElement[] geos = kernel.Rotate(null, polys[0], num,
						points[0]);
				if (geos != null) {
					app.storeUndoInfo();
					kernel.getApplication().getActiveEuclidianView()
							.getEuclidianController()
							.memorizeJustCreatedGeos(geos);
				}
				return true;
			}
			ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
			for (int i = 0; i < selGeos.length; i++) {
				if (selGeos[i] != geoPoint1) {
					if (selGeos[i] instanceof Transformable) {
						ret.addAll(Arrays.asList(kernel.Rotate(null,
								selGeos[i], num, geoPoint1)));
					} else if (selGeos[i].isGeoPolygon()) {
						ret.addAll(Arrays.asList(kernel.Rotate(null,
								selGeos[i], num, geoPoint1)));
					}
				}
			}
			if (!ret.isEmpty()) {
				app.storeUndoInfo();
				kernel.getApplication().getActiveEuclidianView()
						.getEuclidianController().memorizeJustCreatedGeos(ret);
			}
			return true;
		}

		return false; */
	}

/*
	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!isModal()) {
			app.setCurrentSelectionListener(null);
		}
		app.getGuiManager().setCurrentTextfield(this, true);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}
*/
	/*
	 * auto-insert degree symbol when appropriate
	 */
	public void onKeyUp(KeyUpEvent e) {

		// return unless digit typed (instead of !Character.isDigit)
		if (e.getNativeKeyCode() < 48 ||
			(e.getNativeKeyCode() >  57 && e.getNativeKeyCode() < 96) ||
			e.getNativeKeyCode() > 105)
			return;

		AutoCompleteTextFieldW tc = inputPanel.getTextComponent();
		String text = tc.getText();

		// if text already contains degree symbol or variable
		for (int i = 0; i < text.length(); i++) {
			if (!StringUtil.isDigit(text.charAt(i)))
				return;
		}

		int caretPos = tc.getCaretPosition();

		tc.setText(tc.getText() + Unicode.degree);

		tc.setCaretPosition(caretPos);
	}
}
