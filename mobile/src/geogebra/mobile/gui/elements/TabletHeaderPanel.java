package geogebra.mobile.gui.elements;

import geogebra.mobile.gui.elements.InputDialog.InputCallback;

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
				TabletHeaderPanel.this.dialog = new InputDialog("Title", TabletHeaderPanel.this.getElement().getInnerText(), new InputCallback(){

					@Override
          public void onOk()
          {
	          TabletHeaderPanel.this.setCenter(TabletHeaderPanel.this.dialog.getText());
          }

					@Override
          public void onCancel()
          {
	          TabletHeaderPanel.this.dialog.close();
          }
				});

				TabletHeaderPanel.this.dialog.show();
			}
		}, ClickEvent.getType());
	}
}