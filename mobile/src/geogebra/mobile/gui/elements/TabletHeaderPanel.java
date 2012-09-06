package geogebra.mobile.gui.elements;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;

/**
 * Extends from {@link HeaderPanel}.
 */
public class TabletHeaderPanel extends HeaderPanel
{
	protected InputDialog dialog;

	/**
	 * Sets the title of the app.
	 */
	public TabletHeaderPanel()
	{
		this.setCenter("GeoGebra");

		this.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				TabletHeaderPanel.this.dialog = new InputDialog(
						new ClickHandler()
						{
							@Override
							public void onClick(ClickEvent e)
							{
								TabletHeaderPanel.this
										.setCenter(TabletHeaderPanel.this.dialog
												.getText());
								TabletHeaderPanel.this.dialog.hide();
							}
						});
			}
		}, ClickEvent.getType());
	}
}