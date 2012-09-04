package geogebra.mobile.gui.elements;

import geogebra.mobile.gui.elements.toolbar.ToolBar;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

/**
 * Extends from {@link HeaderPanel}.
 */

public class TabletHeaderPanel extends HeaderPanel
{
	/**
	 * Sets the title of the app.
	 */
	public TabletHeaderPanel()
	{
		this.setCenter("Title");
		
		this.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				final PopinDialog dialog = new PopinDialog();
				dialog.setHideOnBackgroundClick(true);
				dialog.setCenterContent(true);

				RoundPanel roundPanel = new RoundPanel();

				final TextBox inputBar = new TextBox();
				roundPanel.add(inputBar);

				Button button = new Button("ok");
				button.addDomHandler(new ClickHandler()
				{
					@Override
					public void onClick(ClickEvent e)
					{
						TabletHeaderPanel.this.setCenter(inputBar.getText()); 
						dialog.hide();
					}
				}, ClickEvent.getType());
				button.addStyleName("popinButton");
				roundPanel.add(button);

				dialog.add(roundPanel);
				dialog.show();
			}
		}, ClickEvent.getType());
	}
}
