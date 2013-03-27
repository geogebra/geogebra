package geogebra.touch.gui.elements.header;

import geogebra.common.kernel.Kernel;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.InputDialog;
import geogebra.touch.gui.elements.InputDialog.InputCallback;
import geogebra.touch.model.GuiModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Extends from {@link HeaderPanel}.
 */
public class TabletHeaderPanel extends HorizontalPanel
{
	private TabletHeaderPanelLeft leftHeader;
	private Button title;
	private TabletHeaderPanelRight rightHeader;
	
	protected InputDialog dialog;

	public TabletHeaderPanel(TabletGUI tabletGUI, Kernel kernel, GuiModel guiModel)
	{
		this.setWidth(Window.getClientWidth() + "px");
		
		this.leftHeader = new TabletHeaderPanelLeft(tabletGUI, kernel, guiModel);
		
		// TODO get text from some I18n list
		this.title = new Button("GeoGebraTouch");
		
		this.rightHeader = new TabletHeaderPanelRight(kernel);
		
		
		this.title.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				TabletHeaderPanel.this.dialog = new InputDialog("Title", TabletHeaderPanel.this.getElement().getInnerText(), new InputCallback()
				{

					@Override
					public void onOk()
					{
						setTitle(TabletHeaderPanel.this.dialog.getText());
					}

					@Override
					public void onCancel()
					{
						TabletHeaderPanel.this.dialog.hide();
					}
				});

				TabletHeaderPanel.this.dialog.show();
			}
		}, ClickEvent.getType());

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.add(this.leftHeader);
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_JUSTIFY);
		this.add(this.title);
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.add(this.rightHeader);
	}

	/**
	 * Sets the title of this worksheet and shows at the center of the
	 * headerpanel.
	 */
	@Override
	public void setTitle(String title)
	{
		this.title.setText(title);
	}
}