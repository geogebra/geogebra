package geogebra.web.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.handler.NumberInputHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.util.Unicode;
import geogebra.web.gui.DialogManagerWeb;
import geogebra.web.gui.inputfield.AutoCompleteTextField;
import geogebra.web.main.Application;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public class InputDialogAngleFixed extends AngleInputDialog implements KeyUpHandler {
	private static final long serialVersionUID = 1L;
	private static String defaultRotateAngle = "45\u00b0"; // 45 degrees

	private GeoPoint2 geoPoint1;
	GeoSegment[] segments;
	GeoPoint2[] points;
	GeoElement[] selGeos;

	private Kernel kernel;
		
	public InputDialogAngleFixed(Application app, String title, InputHandler handler, GeoSegment[] segments, GeoPoint2[] points, GeoElement[] selGeos, Kernel kernel) {
		super(app, app.getPlain("Angle"), title, defaultRotateAngle, false, handler, false);
		
		geoPoint1 = points[0];
		this.segments = segments;
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
			if (source == btOK || source == inputPanel.getTextComponent()) {
					if (!processInput())
						show();
					else
						hide();
					//setVisibleForTools(!processInput());
				//} else if (source == btApply) {
				//	processInput();
				} else if (source == btCancel) {
					//setVisibleForTools(false);
					hide();
			} 
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			//setVisibleForTools(false);
			hide();
		}
	}
	
	private boolean processInput() {
		
		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		
		inputText = inputPanel.getText();
		
		// negative orientation ?
		if (rbClockWise.getValue()) {
			inputText = "-(" + inputText + ")";
		}
		
		boolean success = inputHandler.processInput(inputText);

		cons.setSuppressLabelCreation(oldVal);
		
		
		
		if (success) {
			String angleText = inputPanel.getText();
			// keep angle entered if it ends with 'degrees'
			if (angleText.endsWith("\u00b0") ) defaultRotateAngle = angleText;
			else defaultRotateAngle = "45"+"\u00b0";


			DialogManagerWeb.doAngleFixed(kernel, segments, points, selGeos, ((NumberInputHandler)inputHandler).getNum(), rbClockWise.getValue());

			return true;
		}

		
		return false;
		
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
	}*/

	/*
	 * auto-insert degree symbol when appropriate
	 */
	public void onKeyUp(KeyUpEvent e) {
		
		// return unless digit typed (instead of !Character.isDigit)
		if (e.getNativeKeyCode() < 48 ||
			(e.getNativeKeyCode() >  57 && e.getNativeKeyCode() < 96) ||
			e.getNativeKeyCode() > 105)
			return;
		
		AutoCompleteTextField tc = inputPanel.getTextComponent();
		String text = tc.getText();
		
		// if text already contains degree symbol or variable
		for (int i = 0 ; i < text.length() ; i++) {
			if (!Character.isDigit(text.charAt(i))) return;
		}
		
		int caretPos = tc.getCaretPosition();
		
		tc.setText(tc.getText()+Unicode.degree);
		
		tc.setCaretPosition(caretPos);
	}
}
