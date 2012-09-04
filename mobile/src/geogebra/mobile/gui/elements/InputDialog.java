package geogebra.mobile.gui.elements;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

/**
 * A dialog with an InputBar and a Button to close the dialog
 * 
 * @author Thomas Krismayer
 * 
 */
public class InputDialog extends PopinDialog
{

	private TextBox inputBar;

	public InputDialog(ClickHandler handler)
	{
		setHideOnBackgroundClick(true);
		setCenterContent(true);

		RoundPanel roundPanel = new RoundPanel();

		this.inputBar = new TextBox();
		roundPanel.add(this.inputBar);

		Button button = new Button("ok");
		button.addDomHandler(handler, ClickEvent.getType());
		button.addStyleName("popinButton");
		roundPanel.add(button);

		add(roundPanel);
		show();
	}

	public String getText()
	{
		return this.inputBar.getText();
	}
}
