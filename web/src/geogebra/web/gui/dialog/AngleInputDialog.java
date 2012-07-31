/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.web.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AngleInputDialog extends PopupPanel implements ClickHandler {
	
	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_COLUMNS = 30;
	public static final int DEFAULT_ROWS = 10;
	public enum DialogType  { TextArea, DynamicText, GeoGebraEditor }

	protected RadioButton rbCounterClockWise, rbClockWise;
	Button btOK, btCancel;

	public boolean success = true;
	protected String inputText = null;

	protected AppW app;
	protected String initString;
	protected InputPanelW inputPanel;
	protected InputHandler inputHandler;

	/**
	 * Input Dialog for a GeoAngle object.
	 */
	public AngleInputDialog(AppW app,  String message, String title, String initString,
					boolean autoComplete, InputHandler handler, boolean modal) {
		super(false, true);
		//super(app.getFrame(), modal);
		this.app = app;
		inputHandler = handler;
		this.initString = initString;

		// create radio buttons for "clockwise" and "counter clockwise"
		String id = DOM.createUniqueId();
		rbCounterClockWise = new RadioButton(id, app.getPlain("counterClockwise"));
		rbClockWise = new RadioButton(id, app.getPlain("clockwise"));
		rbCounterClockWise.setChecked(true);

		//createGUI(title, message, autoComplete, DEFAULT_COLUMNS, 1, true, false, false, false, DialogType.GeoGebraEditor);

		// Create components to be displayed
		inputPanel = new InputPanelW(initString, app, DEFAULT_COLUMNS, true);

		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.add(inputPanel);
		centerPanel.add(rbCounterClockWise);
		centerPanel.add(rbClockWise);

		HorizontalPanel btPanel = new HorizontalPanel();
		centerPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		centerPanel.add(btPanel);

		btOK = new Button(app.getPlain("OK"));
		btOK.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btOK.addClickHandler(this);
		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);
		//btApply = new Button("Apply");
		//btApply.addActionListener(this);

		btPanel.add(btOK);
		btPanel.add(btCancel);

		setWidget(centerPanel);
		center();
		inputPanel.getTextComponent().getTextField().setFocus(true);
	}

	public boolean isCounterClockWise() {
		return rbCounterClockWise.getValue();
	}

	public void onClick(ClickEvent e) {
		Object source = e.getSource();

		boolean finished = false;
		success=true;
		try {

			if (source == btOK || source == inputPanel.getTextComponent().getTextField()) {
				inputText = inputPanel.getText();
				inputPanel.getTextComponent().hideTablePopup();

				// negative orientation ?
				if (rbClockWise.getValue()) {
					inputText = "-(" + inputText + ")";
				}

				finished = inputHandler.processInput(inputText);
			} else if (source == btCancel) {
				finished = true;		
				success=false;
				inputPanel.getTextComponent().hideTablePopup();
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			success=false;
		}
		if (finished) hide();
		else show();
	}
}
