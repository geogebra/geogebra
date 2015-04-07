package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.TextEditPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author gabor
 * 
 *         Creates an InputPanel for GeoGebraWeb
 * 
 */
public class InputPanelW extends FlowPanel {

	private AppW app;
	private boolean autoComplete;
	private AutoCompleteTextFieldW textComponent;
	private boolean showSymbolPopup;
	private TextEditPanel textAreaComponent;

	public InputPanelW(String initText, AppW app, int columns,
	        boolean autoComplete) {
		super();
		this.app = app;
		this.autoComplete = autoComplete;
		//setHorizontalAlignment(ALIGN_CENTER);
		//setVerticalAlignment(ALIGN_MIDDLE);
		addStyleName("InputPanel");

		textComponent = new AutoCompleteTextFieldW(columns, app);
		add(textComponent);
	}

	public InputPanelW(String initText, AppW app, int rows, int columns,
	        boolean showSymbolPopupIcon) {
		this(initText, app, rows, columns, showSymbolPopupIcon,
		        false/* , null */, DialogType.GeoGebraEditor);
		if (textComponent instanceof AutoCompleteTextFieldW) {
			AutoCompleteTextFieldW atf = (AutoCompleteTextFieldW) textComponent;
			atf.setAutoComplete(false);
		}
	}

	public InputPanelW(String initText, AppW app, int rows, int columns,
	        boolean showSymbolPopupIcon, boolean showSymbolButtons,
	        /* KeyListener keyListener, */DialogType type) {

		this.app = app;
		this.showSymbolPopup = showSymbolPopupIcon;

		// set up the text component:
		// either a textArea, textfield or HTML textpane
		if (rows > 1) {

			textAreaComponent = new TextEditPanel(app);

			// switch (type) {
			// case TextArea:
			// textComponent = new JTextArea(rows, columns);
			// break;
			// case DynamicText:
			// textComponent = new DynamicTextInputPane(app);
			// break;
			// case GeoGebraEditor:
			// textComponent = new GeoGebraEditorPane(app, rows, columns);
			// ((GeoGebraEditorPane) textComponent).setEditorKit("geogebra");
			// break;

		} else {

			textComponent = new AutoCompleteTextFieldW(columns, app);

			textComponent.prepareShowSymbolButton(showSymbolPopup);
		}

		// textComponent.addFocusListener(this);
		// textComponent.setFocusable(true);
		//
		// if (keyListener != null)
		// textComponent.addKeyListener(keyListener);
		//

		//
		// // create the GUI
		//
		if (rows > 1) { // JTextArea
			// setLayout(new BorderLayout(5, 5));
			// // put the text pane in a border layout to prevent JTextPane's
			// auto
			// // word wrap
			// JPanel noWrapPanel = new JPanel(new BorderLayout());
			// noWrapPanel.add(textComponent);
			// scrollPane = new JScrollPane(noWrapPanel);
			// scrollPane.setAutoscrolls(true);
			// add(scrollPane, BorderLayout.CENTER);
			//
			if (initText != null)
				textAreaComponent.setText(initText);
			add(textAreaComponent);

		}
		//
		else { // JTextField
			   // setLayout(new BorderLayout(0, 0));
			   // tfPanel = new JPanel(new BorderLayout(0, 0));
			   // tfPanel.add(textComponent, BorderLayout.CENTER);
			   // add(tfPanel, BorderLayout.CENTER);
			if (initText != null)
				textComponent.setText(initText);
			add(textComponent);
		}
	}

	public AutoCompleteTextFieldW getTextComponent() {
		return textComponent;
	}

	public TextEditPanel getTextAreaComponent() {
		return textAreaComponent;
	}

	public String getText() {
		if (textComponent != null) {
			return textComponent.getText();
		}
		return textAreaComponent.getText();
	}

	public void setTextComponentFocus() {
		if (textComponent != null) {
			textComponent.getTextBox().getElement().focus();
		} else {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				public void execute() {
					textAreaComponent.getTextArea().setFocus(true);
				}
			});

		}

	}
	
	@Override
    public void setVisible(boolean visible){
		super.setVisible(visible);
		if (textComponent != null) {
			textComponent.setVisible(visible);
		}
		if (textAreaComponent != null) {
			textAreaComponent.setVisible(visible);
		}
	}
	
	/**
	 * Sets the input field enabled/disabled
	 * @param b true iff input field should be enabled
	 */
	public void setEnabled(boolean b) {
		textComponent.setEditable(b);
	}
	
	public static AutoCompleteTextFieldW newTextComponent(AppW app) {
		return new InputPanelW(null, app, -1, false).getTextComponent();
	}
}
