package geogebra.mobile.gui.elements.stylingbar;

import geogebra.mobile.gui.CommonResources;
import geogebra.mobile.model.GuiModel;
import geogebra.mobile.utils.StylingBarEntries;

import java.util.ArrayList;
import java.util.Arrays;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class StylingBar extends RoundPanel
{
	private VerticalPanel base = new VerticalPanel();
	private StylingBarButton colorButton;
	private StylingBarButton[] tempButtons = new StylingBarButton[0];
	private StylingBarButton[] option;

	public StylingBar(final GuiModel guiModel)
	{
		this.addStyleName("stylingbar");

		// default: close ToolBarOptions on click
		ClickHandler defaultHandler = new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
			}
		};

		// optional buttons

		this.option = new StylingBarButton[2];
		this.option[0] = new StylingBarButton(CommonResources.INSTANCE.label(), defaultHandler);
		this.option[1] = new StylingBarButton(CommonResources.INSTANCE.properties_defaults(), defaultHandler);
		this.option[1].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{

			}
		}, ClickEvent.getType());

		// buttons which are always shown

		final StylingBarButton[] button = new StylingBarButton[3];
		button[0] = new StylingBarButton(CommonResources.INSTANCE.show_or_hide_the_axes(), new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
				guiModel.processSource("showAxes");
				if (button[0].getStyleName().endsWith("button-active"))
				{
					button[0].removeStyleName("button-active");
				}
				else
				{
					button[0].addStyleName("button-active");
				}
			}
		});
		button[0].addStyleName("button-active");

		button[1] = new StylingBarButton(CommonResources.INSTANCE.show_or_hide_the_grid(), new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
				guiModel.processSource("showGrid");
				if (button[1].getStyleName().endsWith("button-active"))
				{
					button[1].removeStyleName("button-active");
				}
				else
				{
					button[1].addStyleName("button-active");
				}
			}
		});

		button[2] = new StylingBarButton(CommonResources.INSTANCE.point_capturing(), new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
				guiModel.processSource("pointCapture");
				if (button[2].getStyleName().endsWith("button-active"))
				{
					button[2].removeStyleName("button-active");
				}
				else
				{
					button[2].addStyleName("button-active");
				}
			}
		});
		button[2].addStyleName("button-active");

		for (int i = 0; i < button.length; i++)
		{
			this.base.add(button[i]);
		}
		add(this.base);

		this.colorButton = new StylingBarButton(CommonResources.INSTANCE.colour(), defaultHandler);
		this.colorButton.addStyleName("button-active");
		this.colorButton.addDomHandler(new ClickHandler()
		{

			@Override
      public void onClick(ClickEvent event)
      {
				guiModel.closeOptions();
				ColorBarBackground colorBar = new ColorBarBackground();
				guiModel.showColorBar(colorBar);
								
				//guiModel.processSource("pointCapture");
				if (StylingBar.this.colorButton.getStyleName().endsWith("button-active"))
				{
					StylingBar.this.colorButton.removeStyleName("button-active");
				} else
				{
					StylingBar.this.colorButton.addStyleName("button-active");
				}
      }
			
		}, ClickEvent.getType());
	}

	/**
	 * 
	 * @param commands
	 *          Buttons to add to the StylingBar; in case of an empty Array only
	 *          the ColorButton will be added; in case of null no further Button
	 *          will be added at all
	 */
	public void rebuild(StylingBarButton[] commands)
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

		for (StylingBarButton b : this.tempButtons)
		{
			this.base.remove(b);
		}

		this.base.add(this.colorButton);
		this.tempButtons = commands;
		for (StylingBarButton b : this.tempButtons)
		{
			this.base.add(b);
		}
	}

	public void rebuild(SVGResource[] resource)
	{
		ArrayList<StylingBarButton> buttons = new ArrayList<StylingBarButton>();
		for (SVGResource svg : resource)
		{
			for (StylingBarButton b : this.option)
			{
				if (svg.equals(b.getIcon()))
				{
					buttons.add(b);
				}
			}
		}
		rebuild(buttons.toArray(new StylingBarButton[buttons.size()]));
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
		this.base.remove(this.colorButton);
		for (StylingBarButton b : this.tempButtons)
		{
			this.base.remove(b);
		}
		this.tempButtons = new StylingBarButton[0];
	}

	public void updateColor(String color)
	{
		this.colorButton.getElement().getStyle().setBackgroundColor(color);
	}
}
