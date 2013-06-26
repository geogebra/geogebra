package geogebra.touch.gui.elements.header;

import geogebra.touch.TouchApp;
import geogebra.touch.gui.ResizeListener;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.TitleChangedListener;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;

/**
 * Extends from {@link HeaderPanel}.
 */
public class TabletHeaderPanel extends HorizontalPanel implements ResizeListener
{
	private TabletHeaderPanelLeft leftHeader;
	TextBox worksheetTitle;
	private TabletHeaderPanelRight rightHeader;

	TouchApp app;

	public TabletHeaderPanel(TabletGUI tabletGUI, final TouchApp app, TouchModel touchModel)
	{
		this.setStyleName("headerbar");
		this.setWidth(Window.getClientWidth() + "px");

		this.app = app;
		this.leftHeader = new TabletHeaderPanelLeft(tabletGUI, app, touchModel);
		this.leftHeader.setStyleName("headerLeft");

		this.worksheetTitle = new TextBox();
		this.worksheetTitle.setText(app.getConstructionTitle());
		this.worksheetTitle.setAlignment(TextAlignment.CENTER);

		this.app.addTitleChangedListener(new TitleChangedListener()
		{
			@Override
			public void onTitleChange(String title)
			{
				TabletHeaderPanel.this.worksheetTitle.setText(title);
			}
		});

		this.rightHeader = new TabletHeaderPanelRight(app);
		this.rightHeader.setStyleName("headerRight");

		this.worksheetTitle.addKeyDownHandler(new KeyDownHandler()
		{
			@Override
			public void onKeyDown(KeyDownEvent event)
			{
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				{
					TabletHeaderPanel.this.app.setConstructionTitle(TabletHeaderPanel.this.worksheetTitle.getText());
					TabletHeaderPanel.this.worksheetTitle.setFocus(false);
				}
				else if(event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				{
					TabletHeaderPanel.this.worksheetTitle.setText(TabletHeaderPanel.this.app.getConstructionTitle());
					TabletHeaderPanel.this.worksheetTitle.setFocus(false);
				}
			}
		});
		
		this.worksheetTitle.addMouseOutHandler(new MouseOutHandler()
		{			
			@Override
			public void onMouseOut(MouseOutEvent event)
			{
				TabletHeaderPanel.this.worksheetTitle.setText(TabletHeaderPanel.this.app.getConstructionTitle());
				TabletHeaderPanel.this.worksheetTitle.setFocus(false);
			}
		});

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.add(this.leftHeader);

		this.worksheetTitle.setPixelSize(Window.getClientWidth() - 396, 61);
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.add(this.worksheetTitle);

		this.worksheetTitle.getElement().getStyle().setBackgroundImage("none");
		this.worksheetTitle.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		this.worksheetTitle.getElement().getStyle().setFontSize(35, Unit.PX);

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.add(this.rightHeader);

	}

	@Override
	public void onResize(ResizeEvent event)
	{
		this.setWidth(event.getWidth() + "px");
		this.worksheetTitle.setPixelSize(Window.getClientWidth() - 396, 61);
	}

	public void setLabels()
	{
		this.leftHeader.setLabels();
	}
}