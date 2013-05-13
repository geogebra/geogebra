package geogebra.touch.gui.elements.stylingbar;

import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.ToolBarCommand;
import geogebra.web.gui.util.Slider;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LineStyleBar extends PopupPanel
{
	public static final int SLIDER_MIN = 1; 
	public static final int SLIDER_MAX = 12; 
	
	private VerticalPanel contentPanel;

	public LineStyleBar(final TouchModel touchModel, final StylingBar stylingBar)
	{
		this.addStyleName("StyleBarOptions");
		this.contentPanel = new VerticalPanel();

		Button[] lineStyle = new Button[5];

		for (int i = 0; i < lineStyle.length; i++)
		{
			final int index = i;
			lineStyle[i] = new Button("style " + (i + 1));
			lineStyle[i].addDomHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					StyleBarStatic.applyLineStyle(touchModel.getSelectedGeos(), index);
					touchModel.getGuiModel().setLineStyle(index);
					touchModel.storeOnClose();
					
					if(touchModel.getCommand().equals(ToolBarCommand.Pen) || 
							touchModel.getCommand().equals(ToolBarCommand.FreehandShape))
					{
						stylingBar.euclidianView.getEuclidianController().getPen().setPenLineStyle(index);
					}
				}
			}, ClickEvent.getType());
			this.contentPanel.add(lineStyle[i]);
		}

		Slider slider = new Slider();

		slider.setMinimum(SLIDER_MIN);
		slider.setMaximum(SLIDER_MAX);
		
		if (touchModel.lastSelected() != null)
		{
			slider.setValue(Integer.valueOf(touchModel.lastSelected().getLineThickness()));
		} else if(touchModel.getCommand().equals(ToolBarCommand.Pen) || 
				touchModel.getCommand().equals(ToolBarCommand.FreehandShape))
		{
			slider.setValue(new Integer(stylingBar.euclidianView.getEuclidianController().getPen().getPenSize()));
		}

		slider.addValueChangeHandler(new ValueChangeHandler<Integer>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event)
			{
				StyleBarStatic.applyLineSize(touchModel.getSelectedGeos(), event.getValue().intValue());
				touchModel.getGuiModel().setLineSize(event.getValue().intValue());
				touchModel.storeOnClose();
				
				if(touchModel.getCommand().equals(ToolBarCommand.Pen) || 
						touchModel.getCommand().equals(ToolBarCommand.FreehandShape))
				{
					stylingBar.euclidianView.getEuclidianController().getPen().setPenSize(event.getValue().intValue());
				}
			}
		});
		this.contentPanel.add(slider);

		this.setWidget(this.contentPanel);
	}

}
