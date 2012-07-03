package geogebra.web.gui;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.web.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.gui.view.algebra.InputPanel;
import geogebra.web.gui.view.algebra.InputPanel.DialogType;
import geogebra.web.main.Application;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InputDialogW extends  PopupPanel implements ClickHandler{
	
	protected Application app;

	public static final int DEFAULT_COLUMNS = 30;
	public static final int DEFAULT_ROWS = 10;
	
	protected InputPanel inputPanel;
		
	protected Button btCancel, btOK;
	protected String initString;
	
	public InputDialogW(boolean modal) {
	    super(false, modal);
    }

	/**
	 * @param title
	 * @param message
	 * @param autoComplete
	 * @param columns
	 * @param rows
	 * @param showSymbolPopupIcon
	 * @param selectInitText
	 * @param showProperties
	 * @param showApply
	 * @param type
	 */
	protected void createGUI(String title, String message,
			boolean autoComplete, int columns, int rows,
			boolean showSymbolPopupIcon, boolean selectInitText,
			boolean showProperties, boolean showApply, DialogType type) {

		// Create components to be displayed
		inputPanel = new InputPanel(initString, app, rows, columns,
				showSymbolPopupIcon/*, type*/);

		
		// create buttons
//		btProperties = new JButton();
//		btProperties.setActionCommand("OpenProperties");
//		btProperties.addActionListener(this);
		btOK = new Button(app.getPlain("OK"));
		btOK.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btOK.addClickHandler(this);
		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);
//		btApply = new JButton();
//		btApply.setActionCommand("Apply");
//		btApply.addActionListener(this);

		// create button panel
		HorizontalPanel btPanel = new HorizontalPanel();
		btPanel.add(btOK);
		btPanel.add(btCancel);


		// =====================================================================
		// Create the optionPane: a panel with message label on top, button
		// panel on bottom. The center panel holds the inputPanel, which is
		// added later.
		// =====================================================================

		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.add(inputPanel);
		centerPanel.add(btPanel);
		
		setWidget(centerPanel);
	}
	
	public void onClick(ClickEvent event) {
	    // do nothing - overriden method
	    
    }

}
