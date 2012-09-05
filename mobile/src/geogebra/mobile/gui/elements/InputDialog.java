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
 * @see com.googlecode.mgwt.ui.client.dialog.PopinDialog PopinDialog
 * 
 */
public class InputDialog extends PopinDialog
{

	private TextBox inputBar;

	/**
	 * Is called from the {@link geogebra.mobile.gui.elements.toolbar.ToolBar ToolBar}, to handle commands from the input bar.
	 * @param handler the clickhandler
	 */
	public InputDialog(ClickHandler handler)
	{
		this.inputBar = new TextBox();
		initDialog(handler);
	}
	
	/**
	 * Is called from {@link TabletHeaderPanel} to change the title of the app.
	 * @param handler the clickhandler
	 * @param titel the title of the app
	 */
	public InputDialog(ClickHandler handler, String titel)
	{
		this.inputBar = new TextBox();
		this.inputBar.setText(titel);
		initDialog(handler);
	}

	private void initDialog(ClickHandler handler)
	{
		setHideOnBackgroundClick(true);
		setCenterContent(true);

		RoundPanel roundPanel = new RoundPanel();
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
