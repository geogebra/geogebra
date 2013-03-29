package geogebra.touch.gui.elements.header;

import geogebra.touch.TouchApp;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.dialogs.InputDialog;
import geogebra.touch.gui.dialogs.InputDialog.DialogType;
import geogebra.touch.model.GuiModel;
import geogebra.touch.utils.TitleChangedListener;

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
	Button titleButton;
	private TabletHeaderPanelRight rightHeader;

	protected InputDialog dialog = new InputDialog(DialogType.Title);
	TouchApp app;

	public TabletHeaderPanel(TabletGUI tabletGUI, final TouchApp app, GuiModel guiModel)
	{
		this.setWidth(Window.getClientWidth() + "px");

		this.app = app;
		this.leftHeader = new TabletHeaderPanelLeft(tabletGUI, app, guiModel);

		this.titleButton = new Button(app.getConstructionTitle());

		app.addTitleChangedListener(new TitleChangedListener()
		{
			@Override
			public void onTitleChange(String title)
			{
				TabletHeaderPanel.this.titleButton.setText(title);
			}
		});

		this.rightHeader = new TabletHeaderPanelRight(app);

		this.titleButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();

				TabletHeaderPanel.this.dialog.setText(TabletHeaderPanel.this.titleButton.getText());
				TabletHeaderPanel.this.dialog.show();
			}
		}, ClickEvent.getType());

		this.dialog.addCloseHandler(new CloseHandler<PopupPanel>()
		{

			@Override
			public void onClose(CloseEvent<PopupPanel> event)
			{
				TabletHeaderPanel.this.app.setConstructionTitle(TabletHeaderPanel.this.dialog.getInput());
			}
		});

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.add(this.leftHeader);

		this.titleButton.setPixelSize(Window.getClientWidth() - 396, 61);
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.add(this.titleButton);

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.add(this.rightHeader);

	}

	public void onResize(ResizeEvent event)
	{
		this.setWidth(event.getWidth() + "px");
		this.titleButton.setWidth(Window.getClientWidth() - this.leftHeader.getOffsetWidth() - this.rightHeader.getOffsetWidth() + "px");
	}
}