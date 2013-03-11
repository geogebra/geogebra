package geogebra.touch.gui.elements.stylingbar;

import geogebra.touch.model.TouchModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.MSlider;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

public class LineStyleBar extends RoundPanel
{

	public LineStyleBar(final TouchModel touchModel)
	{
		this.addStyleName("StyleBarOptions");

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
				}
			}, ClickEvent.getType());
			add(lineStyle[i]);
		}

		MSlider slider = new MSlider();
		slider.setMax(10);
		if (touchModel.lastSelected() != null)
		{
			slider.setValue(Integer.valueOf(touchModel.lastSelected().getLineThickness() - 2));
		}

		slider.addValueChangeHandler(new ValueChangeHandler<Integer>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event)
			{
				StyleBarStatic.applyLineSize(touchModel.getSelectedGeos(), event.getValue().intValue() + 2);
				touchModel.getGuiModel().setLineSize(event.getValue().intValue() + 2);
				touchModel.storeOnClose();
			}
		});
		add(slider);

		RootPanel.get().add(this);
	}

}
