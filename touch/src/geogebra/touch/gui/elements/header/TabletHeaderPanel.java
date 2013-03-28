package geogebra.touch.gui.elements.header;

import geogebra.common.kernel.Kernel;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.dialogs.InputDialog;
import geogebra.touch.gui.dialogs.InputDialog.DialogType;
import geogebra.touch.model.GuiModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Extends from {@link HeaderPanel}.
 */
public class TabletHeaderPanel extends HorizontalPanel
{
	private TabletHeaderPanelLeft leftHeader;
	Button title;
	private TabletHeaderPanelRight rightHeader;

	protected InputDialog dialog = new InputDialog(DialogType.Title);

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

				TabletHeaderPanel.this.dialog.setText(TabletHeaderPanel.this.title.getText());
				TabletHeaderPanel.this.dialog.show();
			}
		}, ClickEvent.getType());

		this.dialog.addCloseHandler(new CloseHandler<PopupPanel>()
		{

			@Override
			public void onClose(CloseEvent<PopupPanel> event)
			{
				TabletHeaderPanel.this.title.setText(TabletHeaderPanel.this.dialog.getInput());
			}
		});

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.add(this.leftHeader);

		this.title.setPixelSize(Window.getClientWidth() - 396, 61);
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
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

	public void onResize(ResizeEvent event)
	{
		this.setWidth(event.getWidth() + "px");
		this.title.setWidth(Window.getClientWidth() - this.leftHeader.getOffsetWidth() - this.rightHeader.getOffsetWidth() + "px");
	}
}