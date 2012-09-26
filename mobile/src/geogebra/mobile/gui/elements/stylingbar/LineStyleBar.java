package geogebra.mobile.gui.elements.stylingbar;

import geogebra.mobile.model.MobileModel;
import geogebra.web.gui.util.Slider;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

public class LineStyleBar extends RoundPanel
{

	public LineStyleBar(final MobileModel mobileModel)
	{
		addStyleName("StyleBarOptions");

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
					StyleBarStatic.applyLineStyle(mobileModel.getSelectedGeos(), index);
					mobileModel.getGuiModel().setLineStyle(index);
				}
			}, ClickEvent.getType());
			add(lineStyle[i]);
		}

		Slider slider = new Slider();
		slider.setMaximum(12);
		if (mobileModel.lastSelected() != null)
		{
			slider.setValue(mobileModel.lastSelected().getLineThickness() - 2);
		}

		slider.addValueChangeHandler(new ValueChangeHandler<Integer>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event)
			{
				StyleBarStatic.applyLineSize(mobileModel.getSelectedGeos(), event.getValue() + 2);
				mobileModel.getGuiModel().setLineSize(event.getValue() + 2);
			}
		});
		add(slider);

		RootPanel.get().add(this);
	}

}
