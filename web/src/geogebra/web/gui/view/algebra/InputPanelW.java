package geogebra.web.gui.view.algebra;

import geogebra.common.gui.view.algebra.DialogType;
import geogebra.web.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author gabor
 * 
 * Creates an InputPanel for GeoGEbraWeb
 *
 */
public class InputPanelW extends HorizontalPanel {

	private AppW app;
	private boolean autoComplete;
	private AutoCompleteTextFieldW textComponent;
	private boolean showSymbolPopup;

	public InputPanelW(String initText, AppW app, int columns, boolean autoComplete) {
	   super();
	   this.app = app;
	   this.autoComplete = autoComplete;
	   setHorizontalAlignment(ALIGN_CENTER);
	   setVerticalAlignment(ALIGN_MIDDLE);
	   addStyleName("InputPanel");
	   
	   textComponent = new AutoCompleteTextFieldW(columns, app);
	   add(textComponent);
    }

	public InputPanelW(String initText, AppW app, int rows, int columns, boolean showSymbolPopupIcon) {
		this(initText, app, rows, columns, showSymbolPopupIcon, false/*, null*/, DialogType.GeoGebraEditor);
		if (textComponent instanceof AutoCompleteTextFieldW) {
			AutoCompleteTextFieldW atf = (AutoCompleteTextFieldW) textComponent;
			atf.setAutoComplete(false);
		}
	}
	
	public InputPanelW(String initText, AppW app, int rows, int columns,
	        boolean showSymbolPopupIcon, boolean showSymbolButtons,
	        /*KeyListener keyListener,*/ DialogType type) {

		this.app = app;
		this.showSymbolPopup = showSymbolPopupIcon;

		// set up the text component:
		// either a textArea, textfield or HTML textpane
//		if (rows > 1) {
//
//			switch (type) {
//			case TextArea:
//				textComponent = new JTextArea(rows, columns);
//				break;
//			case DynamicText:
//				textComponent = new DynamicTextInputPane(app);
//				break;
//			case GeoGebraEditor:
//				textComponent = new GeoGebraEditorPane(app, rows, columns);
//				((GeoGebraEditorPane) textComponent).setEditorKit("geogebra");
//				break;
//			}
//
//		} else {

			textComponent = new AutoCompleteTextFieldW(columns, app);
			
			textComponent.setShowSymbolTableIcon(showSymbolPopup);
			//if (!showSymbolPopup) textComponent.removeSymbolButton();
//		}

//		textComponent.addFocusListener(this);
//		textComponent.setFocusable(true);
//
//		if (keyListener != null)
//			textComponent.addKeyListener(keyListener);
//
//		if (initText != null)
//			textComponent.setText(initText);
//
//		// create the GUI
//
//		if (rows > 1) { // JTextArea
//			setLayout(new BorderLayout(5, 5));
//			// put the text pane in a border layout to prevent JTextPane's auto
//			// word wrap
//			JPanel noWrapPanel = new JPanel(new BorderLayout());
//			noWrapPanel.add(textComponent);
//			scrollPane = new JScrollPane(noWrapPanel);
//			scrollPane.setAutoscrolls(true);
//			add(scrollPane, BorderLayout.CENTER);
//
//		}
//
//		else { // JTextField
//			setLayout(new BorderLayout(0, 0));
//			tfPanel = new JPanel(new BorderLayout(0, 0));
//			tfPanel.add(textComponent, BorderLayout.CENTER);
//			add(tfPanel, BorderLayout.CENTER);
			add(textComponent);
//		}
	}

	public AutoCompleteTextFieldW getTextComponent() {
		return textComponent;
	}

	public String getText() {
		return textComponent.getText();
	}
}
