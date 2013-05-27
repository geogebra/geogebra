package geogebra.touch.gui.elements.header;

import geogebra.common.awt.GColor;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.ResizeListener;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.dialogs.InputDialog;
import geogebra.touch.gui.dialogs.InputDialog.DialogType;
import geogebra.touch.model.GuiModel;
import geogebra.touch.utils.TitleChangedListener;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
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
public class TabletHeaderPanel extends HorizontalPanel implements ResizeListener
{
	private TabletHeaderPanelLeft leftHeader;
	Button titleButton;
	private TabletHeaderPanelRight rightHeader;

	protected InputDialog dialog;
	TouchApp app;

	public TabletHeaderPanel(TabletGUI tabletGUI, final TouchApp app, GuiModel guiModel)
	{
		this.setWidth(Window.getClientWidth() + "px");

		this.getElement().getStyle().setBackgroundColor(TabletGUI.getBackgroundColor().toString());
		this.getElement().getStyle().setBorderColor(GColor.BLACK.toString());
		this.getElement().getStyle().setBorderWidth(TabletGUI.FOOTER_BORDER_WIDTH, Unit.PX);
		this.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);

		this.app = app;
		this.dialog = new InputDialog(this.app, DialogType.Title);
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

		this.titleButton.getElement().getStyle().setBackgroundImage("none");
		this.titleButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		this.titleButton.getElement().getStyle().setFontSize(35, Unit.PX);

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.add(this.rightHeader);

	}

	@Override
	public void onResize(ResizeEvent event)
	{
		this.setWidth(event.getWidth() + "px");
		this.titleButton.setPixelSize(Window.getClientWidth() - 396, 61);
		// this.titleButton.setWidth(Window.getClientWidth() -
		// this.leftHeader.getOffsetWidth() - this.rightHeader.getOffsetWidth() +
		// "px");
	}

	public void setLabels()
	{
		this.dialog.setLabels();
		this.leftHeader.dialog.setLabels();
	}
}