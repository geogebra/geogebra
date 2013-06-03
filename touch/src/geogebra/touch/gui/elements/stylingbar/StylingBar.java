package geogebra.touch.gui.elements.stylingbar;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.touch.gui.CommonResources;
import geogebra.touch.gui.elements.ArrowImageButton;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.euclidian.EuclidianViewM;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;
import geogebra.touch.model.GuiModel;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.OptionType;
import geogebra.touch.utils.StylingBarEntries;
import geogebra.touch.utils.ToolBarCommand;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class StylingBar extends DecoratorPanel
{
	HorizontalPanel contentPanel;

	StandardImageButton[] button = new StandardImageButton[0];
	int colorButtonIndex;
	StandardImageButton showHide; 

	EuclidianViewM euclidianView;
	TouchModel touchModel;
	GuiModel guiModel;
	
	private ToolBarCommand lastCommand;
	boolean visible = true; 

	private abstract class StylingBarClickHandler implements ClickHandler{
		protected int index; 
		
		StylingBarClickHandler(int i){
			this.index = i; 
		}		
	}
	
	/**
	 * Initializes the {@link StylingBarButton StylingBarButtons}.
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
		
		this.showHide = new ArrowImageButton(CommonResources.INSTANCE.triangle_left()); 
		this.showHide.addDomHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault(); 
				
				if(StylingBar.this.visible){
					StylingBar.this.contentPanel.clear(); 
					StylingBar.this.contentPanel.add(StylingBar.this.showHide); 
					StylingBar.this.visible = false; 
					
					StylingBar.this.showHide.setStyleName("arrowRight");
				}else {
					StylingBar.this.contentPanel.clear(); 
					for(StandardImageButton b : StylingBar.this.button){
						StylingBar.this.contentPanel.add(b); 
					}
					StylingBar.this.contentPanel.add(StylingBar.this.showHide); 
					StylingBar.this.visible = true; 
					
					StylingBar.this.showHide.setStyleName("arrowLeft");
					
					// force repaint
					euclidianViewPanel.remove(StylingBar.this); 
					euclidianViewPanel.add(StylingBar.this);
					euclidianViewPanel.setWidgetPosition(StylingBar.this, 0, 0);
				}
			}
		}, ClickEvent.getType()); 

		this.getElement().getStyle().setBackgroundColor(GColor.WHITE.toString());

		EuclidianStyleBarStatic.lineStyleArray = EuclidianView.getLineTypes();

		rebuild(this.guiModel.getCommand()); 
		this.lastCommand = this.guiModel.getCommand(); 

		this.setWidget(this.contentPanel);
	}

	/**
	 * 
	 * @param process
	 * @param svg
	 * @param number
	 * @return a new StylingBarButton with an ClickHandler
	 */
	private StandardImageButton createStyleBarButton(final String process, SVGResource svg)
	{
		final StandardImageButton newButton = new StandardImageButton(svg);
		newButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				StylingBar.this.guiModel.closeOptions();
				EuclidianStyleBarStatic.processSourceCommon(process, null, StylingBar.this.euclidianView);

				newButton.setActive(!newButton.isActive());
			}
		}, ClickEvent.getType());

		return newButton;
	}

	private boolean rebuild(ToolBarCommand command)
	{
		if (command == null || command.getStylingBarEntries() == null)
		{
			return false; 
		}

		StylingBarEntries entry = command.getStylingBarEntries();
		
		this.colorButtonIndex = -1; 
		
		SVGResource[] resource = entry.getResources(); 
		StandardImageButton[] b = new StandardImageButton[resource.length]; 
		
		for(int i = 0; i < resource.length; i++)
		{
			if(resource[i].equals(CommonResources.INSTANCE.label()))
			{
				b[i] = new StandardImageButton(CommonResources.INSTANCE.label());
				b[i].addDomHandler(new StylingBarClickHandler(i)
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
							    .showOption(new CaptionBar(StylingBar.this.touchModel), OptionType.CaptionStyle, StylingBar.this.button[this.index]);
						}
					}
				}, ClickEvent.getType());
			}
			else if(resource[i].equals(CommonResources.INSTANCE.properties_default()))
			{
				b[i] = new StandardImageButton(CommonResources.INSTANCE.properties_default());
				b[i].addDomHandler(new StylingBarClickHandler(i)
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
							StylingBar.this.guiModel.showOption(new LineStyleBar(StylingBar.this.touchModel, StylingBar.this), OptionType.LineStyle, StylingBar.this.button[this.index]);
						}
					}
				}, ClickEvent.getType());
			}
			else if(resource[i].equals(CommonResources.INSTANCE.color()))
			{
				b[i] = new StandardImageButton(CommonResources.INSTANCE.color());
				b[i].getElement().getStyle().setBackgroundImage("initial");
				b[i].getElement().getStyle().setBackgroundColor(entry.getColor().toString());
				b[i].addDomHandler(new ClickHandler()
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
								
								// includes closeOptions()
								StylingBar.this.guiModel.showOption(colorBar, OptionType.Color, StylingBar.this.button[StylingBar.this.colorButtonIndex]); 
							}
						}
					}, ClickEvent.getType());
				this.colorButtonIndex = i; 
			}
			else if(resource[i].equals(CommonResources.INSTANCE.show_or_hide_the_axes()))
			{
				b[i] = createStyleBarButton("showAxes", CommonResources.INSTANCE.show_or_hide_the_axes());
			}
			else if(resource[i].equals(CommonResources.INSTANCE.show_or_hide_the_grid()))
			{
				b[i] = createStyleBarButton("showGrid", CommonResources.INSTANCE.show_or_hide_the_grid());
			}
			
			else{
				return false; 
			}
		}
		
		this.contentPanel.clear(); 
		this.button = b; 
		
		if(this.visible)
		{
			for(StandardImageButton imageButton : this.button)
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
		if(this.button[this.colorButtonIndex] != null)
		{
			this.button[this.colorButtonIndex].getElement().getStyle().setBackgroundImage("initial");
			this.button[this.colorButtonIndex].getElement().getStyle().setBackgroundColor(color);
		}
	}

	public void rebuild()
	{
		if(this.lastCommand != null && this.lastCommand.equals(this.guiModel.getCommand())){
			return; 
		}
		
		this.setVisible(true); 
		
		if(!rebuild(this.guiModel.getCommand()))
		{
			clear(); 
			this.setVisible(false); 
		}
		this.lastCommand = this.guiModel.getCommand(); 
	}
}
