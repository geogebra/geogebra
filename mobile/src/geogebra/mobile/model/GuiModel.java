package geogebra.mobile.model;

import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.mobile.gui.CommonResources;
import geogebra.mobile.gui.elements.StylingBar;
import geogebra.mobile.gui.elements.toolbar.OptionsBarBackground;
import geogebra.mobile.gui.elements.toolbar.ToolBarButton;
import geogebra.mobile.utils.ToolBarCommand;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RootPanel;
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
	private boolean optionsShown = false;
	private StylingBar stylingBar;
	private EuclidianView euclidianView;
	private ToolBarButton[] styleBarOption;

	public GuiModel()
	{
		this.styleBarOption = new ToolBarButton[3];
		this.styleBarOption[0] = new ToolBarButton(
				CommonResources.INSTANCE.label(), this);
		this.styleBarOption[1] = new ToolBarButton(
				CommonResources.INSTANCE.properties_defaults(), this);
		this.styleBarOption[1].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{

			}
		}, ClickEvent.getType());
	}

	public ToolBarCommand getCommand()
	{
		return this.activeButton == null ? null : this.activeButton.getCmd();
	}

	public void buttonClicked(ToolBarButton tbb)
	{
		closeOptions();
		setActive(tbb);
	}

	public void processSource(String string)
	{
		if (string.equals("pointCapture"))
		{
			// taken from EuclidianStyleBarStatic.processSourceCommon
			int mode = this.euclidianView.getPointCapturingMode();
			if (mode == 3 || mode == 0)
			{
				mode = 3 - mode; // swap 0 and 3
			}
			this.euclidianView.setPointCapturing(mode);
		} else
		{
			EuclidianStyleBarStatic.processSourceCommon(string, null,
					this.euclidianView);
		}
	}

	public void updateStylingBar(MobileModel model)
	{
		if (this.stylingBar == null)
		{
			return;
		}

		if (model == null || model.getTotalNumber() == 0)
		{
			this.stylingBar.clear();
			return;
		}

		ArrayList<ToolBarButton> commands = new ArrayList<ToolBarButton>();

		if (model.getElement(GeoLine.class) == null
				&& model.getElement(GeoVector.class) == null)
		{
			commands.add(this.styleBarOption[0]);
		}

		if (model.getElement(GeoPoint.class) == null)
		{
			commands.add(this.styleBarOption[1]);
		}

		if (model.getTotalNumber() == 0)
		{
			this.stylingBar.clear();
			return;
		}

		this.stylingBar.updateColor(model.lastSelected().getAlgebraColor()
				.toString());
		this.stylingBar.rebuild(commands.toArray(new ToolBarButton[commands
				.size()]));
	}

	public void closeOptions()
	{
		if (this.optionsShown && this.optionsBackground != null)
		{
			RootPanel.get().remove(this.optionsBackground);
			this.optionsShown = false;
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

		this.stylingBar.rebuild(toolBarButton.getCmd().getStylingBarEntries(),
				this);
	}

	public void showOptions(ButtonBar options)
	{
		closeOptions();
		this.optionsBackground = options;
		RootPanel.get().add(options);
		this.optionsShown = true;
	}

	public boolean optionsShown()
	{
		return this.optionsShown;
	}

	public void setStylingBar(StylingBar bar)
	{
		this.stylingBar = bar;
	}

	public void setEuclidianView(EuclidianView ec)
	{
		this.euclidianView = ec;
	}
}
