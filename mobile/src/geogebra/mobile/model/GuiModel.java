package geogebra.mobile.model;

import geogebra.mobile.gui.elements.toolbar.OptionsBarBackground;
import geogebra.mobile.gui.elements.toolbar.ToolBarButton;
import geogebra.mobile.utils.ToolBarCommand;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

/**
 * Organizes the visibility of the additional {@link OptionsBarBackground
 * toolBar} according to the {@link ToolBarButton active button}.
 * 
 * @author Thomas Krismayer
 * 
 */
public class GuiModel
{

	private ToolBarButton activeButton;
	private ButtonBar optionsBackground;
	private RoundPanel stylingBarBackground;

	private boolean optionsShown = false;
	private boolean stylingBarShown = false;

	public ToolBarCommand getCommand()
	{
		return this.activeButton == null ? null : this.activeButton.getCmd();
	}

	public void buttonClicked(ToolBarButton tbb)
	{
		closeOptions();
		setActive(tbb);
	}

	public void closeOptions()
	{
		if (this.optionsShown && this.optionsBackground != null)
		{
			RootPanel.get().remove(this.optionsBackground);
			this.optionsShown = false;
		}
	}

	public void closeStylingBar()
	{
		if (this.stylingBarShown && this.stylingBarBackground != null)
		{
			RootPanel.get().remove(this.stylingBarBackground);
			this.stylingBarShown = false;
		}
	}

	public void setActive(ToolBarButton toolBarButton)
	{
		if (this.activeButton != null)
		{
			this.activeButton.removeStyleName("button-active");
		}
		this.activeButton = toolBarButton;
		this.activeButton.addStyleName("button-active");
	}

	public void showOptions(ButtonBar options)
	{
		closeOptions();
		this.optionsBackground = options;
		RootPanel.get().add(options);
		this.optionsShown = true;
	}

	public void showStylingBar(RoundPanel stylingBar)
	{
		closeStylingBar();
		this.stylingBarBackground = stylingBar;
		RootPanel.get().add(this.stylingBarBackground);
		this.stylingBarShown = true;
	}

	public boolean optionsShown()
	{
		return this.optionsShown;
	}

	public boolean stylingShown()
	{
		return this.stylingBarShown;
	}
}
