package geogebra.touch.model;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.Test;
import geogebra.touch.gui.CommonResources;
import geogebra.touch.gui.elements.stylingbar.StyleBarStatic;
import geogebra.touch.gui.elements.stylingbar.StylingBar;
import geogebra.touch.gui.elements.toolbar.ToolBarButton;
import geogebra.touch.utils.OptionType;
import geogebra.touch.utils.ToolBarCommand;

import java.util.ArrayList;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Organizes the visibility of the additional {@link OptionsBar
 * toolBar} according to the {@link ToolBarButton active button}.
 * 
 * @author Thomas Krismayer
 * 
 */
public class GuiModel
{

	private TouchModel touchModel;
	private ToolBarButton activeButton;
	private StylingBar stylingBar;
	private EuclidianView euclidianView;
	private Widget option;

	private OptionType styleBarOptionShown = OptionType.Non;

	private GColor color;
	private float alpha = -1f;
	private int lineStyle = -1;
	private int lineSize = -1;
	private int captionMode = -1;

	/**
	 * @param model
	 *          if it is not intended to use a TouchModel, model can be null
	 */
	public GuiModel(TouchModel model)
	{
		this.touchModel = model;
	}

	public ToolBarCommand getCommand()
	{
		return this.activeButton == null ? null : this.activeButton.getCmd();
	}

	public void buttonClicked(ToolBarButton tbb)
	{
		closeOptions();
		setActive(tbb);

		if (this.touchModel != null)
		{
			this.touchModel.resetSelection();
			this.touchModel.repaint();
		}
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
		}
		else
		{
			EuclidianStyleBarStatic.processSourceCommon(string, null, this.euclidianView);
		}
	}

	public void updateStylingBar(TouchModel model)
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

		ArrayList<SVGResource> commands = new ArrayList<SVGResource>();

		if (model.getNumberOf(Test.GEOPOINT) == model.getTotalNumber())
		{
			commands.add(CommonResources.INSTANCE.label());
		}

		if (model.getElement(Test.GEOPOINT) == null)
		{
			commands.add(CommonResources.INSTANCE.properties_defaults());
		}

		if (model.getTotalNumber() == 0)
		{
			this.stylingBar.clear();
			return;
		}

		this.stylingBar.updateColor(model.lastSelected().getAlgebraColor().toString());
		this.stylingBar.rebuild(commands.toArray(new SVGResource[commands.size()]));
	}

	public void closeOptions()
	{
		if (this.option != null)
		{
			RootPanel.get().remove(this.option);
			this.styleBarOptionShown = OptionType.Non;

			if (this.touchModel != null)
			{
				this.touchModel.optionsClosed();
			}
		}
		
		// activeButton looses style otherwise
		this.activeButton.addStyleDependentName("active");
	}

	public void setActive(ToolBarButton toolBarButton)
	{
		if (this.activeButton != null && this.activeButton != toolBarButton)
		{
			this.activeButton.removeStyleDependentName("active");
		}
		this.activeButton = toolBarButton;
		this.activeButton.addStyleDependentName("active");

		if (this.touchModel != null)
		{
			this.touchModel.setCommand(toolBarButton.getCmd());
		}

		this.stylingBar.rebuild(toolBarButton.getCmd().getStylingBarEntries());
	}

	public void showOption(Widget newOption, OptionType type)
	{
		closeOptions();
		this.option = newOption;
		RootPanel.get().add(newOption);
		this.styleBarOptionShown = type;
	}

	public OptionType getOptionTypeShown()
	{
		return this.styleBarOptionShown;
	}

	public void setStylingBar(StylingBar bar)
	{
		this.stylingBar = bar;
	}

	public void setEuclidianView(EuclidianView ec)
	{
		this.euclidianView = ec;
	}

	public void resetStyle()
	{
		this.color = null;
		this.alpha = -1f;
		this.lineStyle = -1;
		this.lineSize = -1;
		this.captionMode = -1;
	}

	public void appendStyle(ArrayList<GeoElement> elements)
	{
		if (this.color != null)
		{
			StyleBarStatic.applyColor(elements, this.color);
		}
		if (this.alpha >= 0) // != -1f
		{
			StyleBarStatic.applyAlpha(elements, this.alpha);
		}
		if (this.lineStyle != -1)
		{
			StyleBarStatic.applyLineStyle(elements, this.lineStyle);
		}
		if (this.lineSize != -1)
		{
			StyleBarStatic.applyLineSize(elements, this.lineSize);
		}
		if (this.captionMode != -1)
		{
			EuclidianStyleBarStatic.applyCaptionStyle(elements, -1 , this.captionMode); 
			// second argument (-1):  anything other than 0
		}
	}

	public void setColor(GColor c)
	{
		this.color = c;
	}

	public void setAlpha(float a)
	{
		this.alpha = a;
	}

	public void setLineStyle(int i)
	{
		this.lineStyle = i;
	}

	public void setLineSize(int i)
	{
		this.lineSize = i;
	}

	public void setCaptionMode(int i)
	{
		this.captionMode = i;
	}
}