package geogebra.touch.gui.elements.header;

import geogebra.touch.FileManagerM;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.ResizeListener;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.TitleChangedListener;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Extends from {@link HeaderPanel}.
 */
public class TabletHeaderPanel extends HorizontalPanel implements ResizeListener
{
	private TabletHeaderPanelLeft leftHeader;
	private VerticalPanel titlePanel;
	Panel underline;
	TextBox worksheetTitle;
	private TabletHeaderPanelRight rightHeader;

	TouchApp app;
	FileManagerM fm;

	public TabletHeaderPanel(TabletGUI tabletGUI, final TouchApp app, TouchModel touchModel, FileManagerM fm)
	{
		this.setStyleName("headerbar");
		this.setWidth(Window.getClientWidth() + "px");

		this.app = app;
		this.fm = fm;
		this.leftHeader = new TabletHeaderPanelLeft(tabletGUI, app, touchModel, fm);
		this.leftHeader.setStyleName("headerLeft");

		this.titlePanel = new VerticalPanel();

		this.worksheetTitle = new TextBox();
		this.worksheetTitle.setText(app.getConstructionTitle());

		this.app.addTitleChangedListener(new TitleChangedListener()
		{
			@Override
			public void onTitleChange(String title)
			{
				TabletHeaderPanel.this.worksheetTitle.setText(title);
			}
		});

		this.rightHeader = new TabletHeaderPanelRight(app, this);
		this.rightHeader.setStyleName("headerRight");
		this.worksheetTitle.setStyleName("worksheetTitle");

		this.worksheetTitle.addKeyDownHandler(new KeyDownHandler()
		{
			@Override
			public void onKeyDown(KeyDownEvent event)
			{
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				{
					TabletHeaderPanel.this.app.setConstructionTitle(TabletHeaderPanel.this.worksheetTitle.getText());
					TabletHeaderPanel.this.fm.saveFile(TabletHeaderPanel.this.app);
					TabletHeaderPanel.this.worksheetTitle.setFocus(false);
				}
				else if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				{
					TabletHeaderPanel.this.worksheetTitle.setText(TabletHeaderPanel.this.app.getConstructionTitle());
					TabletHeaderPanel.this.worksheetTitle.setFocus(false);
				}
			}
		});

		this.worksheetTitle.addFocusHandler(new FocusHandler()
		{
			@Override
			public void onFocus(FocusEvent event)
			{
				// set underline active
				TabletHeaderPanel.this.underline.removeStyleName("inactive");
				TabletHeaderPanel.this.underline.addStyleName("active");
			}
		});

		this.worksheetTitle.addBlurHandler(new BlurHandler()
		{
			@Override
			public void onBlur(BlurEvent event)
			{
				// set underline inactive
				TabletHeaderPanel.this.underline.removeStyleName("active");
				TabletHeaderPanel.this.underline.addStyleName("inactive");
			}
		});

		this.titlePanel.add(this.worksheetTitle);

		// Input Underline for Android
		this.underline = new LayoutPanel();
		this.underline.setStyleName("inputUnderline");
		this.underline.addStyleName("inactive");

		this.titlePanel.add(this.underline);

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.add(this.leftHeader);

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.add(this.titlePanel);

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.add(this.rightHeader);

	}

	@Override
	public void onResize(ResizeEvent event)
	{
		this.setWidth(event.getWidth() + "px");
	}

	public void setLabels()
	{
		this.leftHeader.setLabels();
	}

	public String getConstructionTitle()
	{
		return this.worksheetTitle.getText();
	}

	public void editTitle()
	{
		this.worksheetTitle.setFocus(true);
		this.worksheetTitle.selectAll();
	}

	public TabletHeaderPanelRight getRightHeader()
	{
		return this.rightHeader;
	}

	public TabletHeaderPanelLeft getLeftHeader()
	{
		return this.leftHeader;
	}
}