package geogebra.web.gui.view.algebra;

import geogebra.web.gui.inputfield.AutoCompleteTextField;
import geogebra.web.main.Application;

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author gabor
 * 
 * Creates an InputPanel for GeoGEbraWeb
 *
 */
public class InputPanel extends HorizontalPanel {

	private Application app;
	private boolean autoComplete;
	private AutoCompleteTextField textComponent;

	public InputPanel(String initText, Application app, int columns, boolean autoComplete) {
	   super();
	   this.app = app;
	   this.autoComplete = autoComplete;
	   setHorizontalAlignment(ALIGN_CENTER);
	   setVerticalAlignment(ALIGN_MIDDLE);
	   addStyleName("InputPanel");
	   
	   textComponent = new AutoCompleteTextField(columns, app);
	   add(textComponent);
    }

	public AutoCompleteTextField getTextComponent() {
		return textComponent;
	}

	public String getText() {
		return textComponent.getText();
	}
}
