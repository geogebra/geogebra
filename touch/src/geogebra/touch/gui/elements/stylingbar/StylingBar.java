package geogebra.touch.gui.elements.stylingbar;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.touch.gui.CommonResources;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.euclidian.EuclidianViewM;
import geogebra.touch.model.GuiModel;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.OptionType;
import geogebra.touch.utils.StylingBarEntries;

import java.util.ArrayList;
import java.util.Arrays;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The StylingBar includes the standard buttons (grid, axes, point capturing)
 * and the optional buttons, which are only shown if needed (e.g. color,
 * lineStyle).
 * 
 * @author Thomas Krismayer
 * 
 */
public class StylingBar extends DecoratorPanel
{
	private VerticalPanel contentPanel;

	private StandardImageButton[] tempButtons = new StandardImageButton[0];
	StandardImageButton[] optionalButtons;
	StandardImageButton[] standardButtons;
	StandardImageButton colorButton;

	boolean[] active;

	EuclidianViewM euclidianView;
	TouchModel touchModel;
	final GuiModel guiModel;

	/**
	 * Initializes the {@link StylingBarButton StylingBarButtons}.
	 * 
	 * @param TouchModel
	 *          touchModel
	 * @param EuclidianViewM
	 *          view
	 */
	public StylingBar(TouchModel touchModel, EuclidianViewM view)
	{
		this.euclidianView = view;
		this.touchModel = touchModel;
		this.guiModel = touchModel.getGuiModel();

		this.contentPanel = new VerticalPanel();

		this.getElement().getStyle().setBackgroundColor(GColor.WHITE.toString());

		EuclidianStyleBarStatic.lineStyleArray = EuclidianView.getLineTypes();

		createStandardButtons();
		createOptionalButtons();

		this.setWidget(this.contentPanel);
	}

	/**
	 * Initializes the standardButtons which are always shown (ShowGrid, ShowAxes
	 * & PointCapture).
	 */
	private void createStandardButtons()
	{
		this.standardButtons = new StandardImageButton[2];
		this.active = new boolean[] { true, false, true };

		this.standardButtons[0] = createStyleBarButton("showAxes", CommonResources.INSTANCE.show_or_hide_the_axes(), 0);
		this.standardButtons[1] = createStyleBarButton("showGrid", CommonResources.INSTANCE.show_or_hide_the_grid(), 1);

		// add the standardButtons to the verticalPanel
		for (int i = 0; i < this.standardButtons.length; i++)
		{
			this.contentPanel.add(this.standardButtons[i]);
		}
	}

	/**
	 * Initializes the optional buttons, which are only shown if its necessary.
	 */
	private void createOptionalButtons()
	{
		// optional buttons
		this.optionalButtons = new StandardImageButton[2];
		this.optionalButtons[0] = new StandardImageButton(CommonResources.INSTANCE.label());
		this.optionalButtons[0].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if (StylingBar.this.guiModel.getOptionTypeShown() == OptionType.CaptionStyle)
				{
					StylingBar.this.guiModel.closeOptions();
				}
				else
				{
					StylingBar.this.guiModel
					    .showOption(new CaptionBar(StylingBar.this.touchModel), OptionType.CaptionStyle, StylingBar.this.optionalButtons[0]);
				}
			}
		}, ClickEvent.getType());

		this.optionalButtons[1] = new StandardImageButton(CommonResources.INSTANCE.properties_defaults());

		this.optionalButtons[1].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if (StylingBar.this.guiModel.getOptionTypeShown() == OptionType.LineStyle)
				{
					StylingBar.this.guiModel.closeOptions();
				}
				else
				{
					StylingBar.this.guiModel.showOption(new LineStyleBar(StylingBar.this.touchModel), OptionType.LineStyle, StylingBar.this.optionalButtons[1]);
				}
			}
		}, ClickEvent.getType());

		this.colorButton = new StandardImageButton(CommonResources.INSTANCE.colour());
		this.colorButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				if (StylingBar.this.guiModel.getOptionTypeShown() == OptionType.Color)
				{
					StylingBar.this.guiModel.closeOptions();
				}
				else
				{
					ColorBarBackground colorBar = new ColorBarBackground(StylingBar.this, StylingBar.this.touchModel);
					StylingBar.this.guiModel.showOption(colorBar, OptionType.Color, StylingBar.this.colorButton); // includes
					// closeOptions()
				}
			}
		}, ClickEvent.getType());
	}

	/**
	 * 
	 * @param process
	 * @param svg
	 * @param number
	 * @return a new StylingBarButton with an ClickHandler
	 */
	private StandardImageButton createStyleBarButton(final String process, SVGResource svg, final int number)
	{
		StandardImageButton newButton = new StandardImageButton(svg);
		newButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();

				StylingBar.this.guiModel.closeOptions();
				EuclidianStyleBarStatic.processSourceCommon(process, null, StylingBar.this.euclidianView);

				if (StylingBar.this.active[number])
				{
					StylingBar.this.active[number] = false;
				}
				else
				{
					StylingBar.this.active[number] = true;
				}
			}
		}, ClickEvent.getType());

		return newButton;
	}

	/**
	 * 
	 * @param commands
	 *          Buttons to add to the StylingBar; in case of an empty Array only
	 *          the ColorButton will be added; in case of null no further Button
	 *          will be added at all
	 */
	public void rebuild(StandardImageButton[] commands)
	{
		if (commands == null)
		{
			clear();
			return;
		}

		if (Arrays.equals(this.tempButtons, commands) && commands.length != 0)
		{
			return;
		}

		for (StandardImageButton b : this.tempButtons)
		{
			this.contentPanel.remove(b);
		}

		this.contentPanel.add(this.colorButton);

		this.tempButtons = commands;

		for (StandardImageButton b : this.tempButtons)
		{
			this.contentPanel.add(b);
		}
	}

	public void rebuild(SVGResource[] resource)
	{
		ArrayList<StandardImageButton> buttons = new ArrayList<StandardImageButton>();
		for (SVGResource svg : resource)
		{
			for (StandardImageButton b : this.optionalButtons)
			{
				if (svg.equals(b.getIcon()))
				{
					buttons.add(b);
				}
			}
		}
		rebuild(buttons.toArray(new StandardImageButton[buttons.size()]));
	}

	/**
	 * 
	 * @param entries
	 *          the
	 */
	public void rebuild(StylingBarEntries entries)
	{
		if (entries == null)
		{
			clear();
		}
		else
		{
			rebuild(entries.getResources());
			updateColor(entries.getColor().toString());
		}
	}

	@Override
	public void clear()
	{
		this.remove(this.colorButton);

		for (StandardImageButton b : this.tempButtons)
		{
			this.remove(b);
		}
		this.tempButtons = new StandardImageButton[0];
	}

	public void updateColor(String color)
	{
		this.colorButton.getElement().getStyle().setBackgroundImage("initial");
		this.colorButton.getElement().getStyle().setBackgroundColor(color);
	}
}
