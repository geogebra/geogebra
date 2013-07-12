package geogebra.touch.gui.elements.stylingbar;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.LineProperties;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.ArrowImageButton;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.euclidian.EuclidianViewM;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;
import geogebra.touch.gui.laf.DefaultIcons;
import geogebra.touch.model.GuiModel;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.OptionType;
import geogebra.touch.utils.StylingBarEntries;
import geogebra.touch.utils.ToolBarCommand;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class StylingBar extends DecoratorPanel
{
	private static DefaultIcons LafIcons = TouchEntryPoint.getLookAndFeel().getIcons();
	HorizontalPanel contentPanel;

	StandardImageButton[] button = new StandardImageButton[0];
	int colorButtonIndex;
	StandardImageButton showHide;

	EuclidianViewM euclidianView;
	TouchModel touchModel;
	GuiModel guiModel;

	private ToolBarCommand lastCommand;
	boolean visible = true;

	/**
	 * Initializes the {@link StylingBarButton StylingBarButtons}.
	 * 
	 * @param euclidianViewPanel
	 * 
	 * @param TouchModel
	 *          touchModel
	 * @param EuclidianViewM
	 *          view
	 */
	public StylingBar(TouchModel touchModel, EuclidianViewM view, final EuclidianViewPanel euclidianViewPanel)
	{
		this.setStyleName("stylebar");
		this.euclidianView = view;
		this.touchModel = touchModel;
		this.guiModel = touchModel.getGuiModel();

		this.contentPanel = new HorizontalPanel();

		// prevent that click- or touchEvents that do not hit a button reach the
		// EuclidianPanel
		this.contentPanel.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.stopPropagation();
			}
		}, ClickEvent.getType());
		this.contentPanel.addDomHandler(new TouchStartHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				event.stopPropagation();
			}
		}, TouchStartEvent.getType());
		this.contentPanel.addDomHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(MouseDownEvent event)
			{
				event.stopPropagation();
			}
		}, MouseDownEvent.getType());

		this.showHide = new ArrowImageButton(LafIcons.triangle_left());

		this.showHide.addHandler(TouchEntryPoint.getLookAndFeel().getStyleBarHandlerShowHide(this, euclidianViewPanel), TouchEntryPoint.getLookAndFeel()
		    .getStylBarEventType());

		this.getElement().getStyle().setBackgroundColor(GColor.WHITE.toString());

		EuclidianStyleBarStatic.lineStyleArray = EuclidianView.getLineTypes();
		if (this.guiModel.getCommand() != null)
		{
			rebuild(this.guiModel.getCommand().getStylingBarEntries());
		}
		this.lastCommand = this.guiModel.getCommand();

		this.setWidget(this.contentPanel);
	}

	/**
	 * 
	 * @param process
	 * @param svg
	 * @return a new StylingBarButton with an ClickHandler
	 */
	private StandardImageButton createStyleBarButton(final String process, SVGResource svg)
	{
		final StandardImageButton newButton = new StandardImageButton(svg);

		newButton.addHandler(TouchEntryPoint.getLookAndFeel().getStyleBarButtonHandler(this, newButton, process), TouchEntryPoint.getLookAndFeel()
		    .getStylBarEventType());

		return newButton;
	}

	private boolean rebuild(StylingBarEntries entry)
	{
		if (entry == null)
		{
			return false;
		}

		this.colorButtonIndex = -1;

		SVGResource[] resource = entry.getResources();
		String color = entry.getColor() != null ? entry.getColor().toString() : "";

		if (entry == StylingBarEntries.Move && this.touchModel.getTotalNumber() > 0)
		{
			color = this.touchModel.getSelectedGeos().get(0).getObjectColor().toString();
			if (this.touchModel.getSelectedGeos().get(0).getGeoElementForPropertiesDialog() instanceof GeoPointND)
			{
				resource = StylingBarEntries.Point.getResources();
			}
			else if (this.touchModel.getSelectedGeos().get(0).getGeoElementForPropertiesDialog() instanceof LineProperties)
			{
				resource = StylingBarEntries.Line.getResources();
			}
			else if (this.touchModel.getSelectedGeos().get(0).getGeoElementForPropertiesDialog() instanceof GeoPolygon)
			{
				resource = StylingBarEntries.Polygon.getResources();
			}
			else if (this.touchModel.getSelectedGeos().get(0).getGeoElementForPropertiesDialog() instanceof GeoAngle)
			{
				resource = StylingBarEntries.Angle.getResources();
			}
		}

		StandardImageButton[] b = new StandardImageButton[resource.length];

		for (int i = 0; i < resource.length; i++)
		{
			if (resource[i].equals(LafIcons.label()))
			{
				b[i] = new StandardImageButton(LafIcons.label());

				b[i].addHandler(TouchEntryPoint.getLookAndFeel().getOptionalButtonHandler(this, b[i], OptionType.CaptionStyle), TouchEntryPoint
				    .getLookAndFeel().getStylBarEventType());
			}
			else if (resource[i].equals(LafIcons.properties_default()))
			{
				b[i] = new StandardImageButton(LafIcons.properties_default());

				b[i].addHandler(TouchEntryPoint.getLookAndFeel().getOptionalButtonHandler(this, b[i], OptionType.LineStyle), TouchEntryPoint.getLookAndFeel()
				    .getStylBarEventType());
			}
			else if (resource[i].equals(LafIcons.color()))
			{
				b[i] = new StandardImageButton(LafIcons.color());
				b[i].getElement().getStyle().setBackgroundImage("initial");
				b[i].getElement().getStyle().setBackgroundColor(color);

				b[i].addHandler(TouchEntryPoint.getLookAndFeel().getOptionalButtonHandler(this, b[i], OptionType.Color), TouchEntryPoint.getLookAndFeel()
				    .getStylBarEventType());

				this.colorButtonIndex = i;
			}
			else if (resource[i].equals(LafIcons.show_or_hide_the_axes()))
			{
				b[i] = createStyleBarButton("showAxes", LafIcons.show_or_hide_the_axes());
			}
			else if (resource[i].equals(LafIcons.show_or_hide_the_grid()))
			{
				b[i] = createStyleBarButton("showGrid", LafIcons.show_or_hide_the_grid());
			}

			else
			{
				return false;
			}
		}

		this.contentPanel.clear();
		this.button = b;

		if (this.visible)
		{
			for (StandardImageButton imageButton : this.button)
			{
				this.contentPanel.add(imageButton);
			}
		}
		this.contentPanel.add(this.showHide);

		return true;
	}

	@Override
	public void clear()
	{
		this.colorButtonIndex = -1;
		this.contentPanel.clear();
		this.button = new StandardImageButton[0];
	}

	public void updateColor(String color)
	{
		if (this.button[this.colorButtonIndex] != null)
		{
			this.button[this.colorButtonIndex].getElement().getStyle().setBackgroundImage("initial");
			this.button[this.colorButtonIndex].getElement().getStyle().setBackgroundColor(color);
		}
	}

	public void rebuild()
	{
		if (this.lastCommand != null && this.guiModel.getCommand() != ToolBarCommand.Move_Mobile && this.lastCommand.equals(this.guiModel.getCommand()))
		{
			return;
		}

		this.setVisible(true);

		if (this.guiModel.getCommand() != null && !rebuild(this.guiModel.getCommand().getStylingBarEntries()))
		{
			clear();
			this.setVisible(false);
		}
		this.lastCommand = this.guiModel.getCommand();
	}

	public void onTouchStartShowHide(DomEvent<? extends EventHandler> event, EuclidianViewPanel euclidianViewPanel)
	{
		event.preventDefault();
		event.stopPropagation();

		if (StylingBar.this.visible)
		{
			// close all opened options before hiding the stylingbar
			StylingBar.this.guiModel.closeOptions();

			StylingBar.this.contentPanel.clear();
			StylingBar.this.contentPanel.add(StylingBar.this.showHide);
			StylingBar.this.visible = false;

			StylingBar.this.showHide.setStyleName("arrowRight");

			// Set stylebar transparent, when closed
			StylingBar.this.addStyleName("transparent");
		}
		else
		{
			StylingBar.this.contentPanel.clear();
			for (StandardImageButton b : StylingBar.this.button)
			{
				StylingBar.this.contentPanel.add(b);
			}
			StylingBar.this.contentPanel.add(StylingBar.this.showHide);
			StylingBar.this.visible = true;

			StylingBar.this.showHide.setStyleName("arrowLeft");

			// Set stylebar nontransparent, when open
			StylingBar.this.removeStyleName("transparent");

			// force repaint
			euclidianViewPanel.remove(StylingBar.this);
			euclidianViewPanel.add(StylingBar.this);
			euclidianViewPanel.setWidgetPosition(StylingBar.this, 0, 0);
		}
	}

	public void onTouchStartStyleBarButton(DomEvent<? extends EventHandler> event, StandardImageButton newButton, String process)
	{
		event.stopPropagation();

		StylingBar.this.guiModel.closeOptions();
		EuclidianStyleBarStatic.processSourceCommon(process, null, StylingBar.this.euclidianView);

		newButton.setActive(!newButton.isActive());
	}

	public void onTouchStartOptionalButton(DomEvent<? extends EventHandler> event, StandardImageButton eventSource, OptionType type)
	{
		event.preventDefault();
		if (StylingBar.this.guiModel.getOptionTypeShown().equals(type))
		{
			StylingBar.this.guiModel.closeOptions();
		}
		else if (type.equals(OptionType.Color))
		{
			ColorBarBackground colorBar = new ColorBarBackground(StylingBar.this, StylingBar.this.touchModel);

			// includes closeOptions()
			StylingBar.this.guiModel.showOption(new OptionsBox(colorBar), OptionType.Color, StylingBar.this.button[StylingBar.this.colorButtonIndex]);
		}
		else if (type.equals(OptionType.LineStyle))
		{
			StylingBar.this.guiModel.showOption(new OptionsBox(new LineStyleBar(StylingBar.this.touchModel, StylingBar.this)), OptionType.LineStyle,
			    eventSource);
		}
		else if (type.equals(OptionType.CaptionStyle))
		{
			StylingBar.this.guiModel.showOption(new OptionsBox(new CaptionBar(StylingBar.this.touchModel)), OptionType.CaptionStyle, eventSource);
		}
	}

	// TODO: use with SelectionManager
	// public void updateGeos(SelectionManager sel)
	// {
	// if (sel.getSelectedGeos().size() == 0)
	// {
	// rebuild(StylingBarEntries.Move);
	// }
	// else if (sel.getSelectedGeos().get(0).getGeoElementForPropertiesDialog()
	// instanceof GeoPointND)
	// {
	// rebuild(StylingBarEntries.Point);
	// }
	// else if (sel.getSelectedGeos().get(0).getGeoElementForPropertiesDialog()
	// instanceof LineProperties)
	// {
	// rebuild(StylingBarEntries.Line);
	// }
	// }
}
