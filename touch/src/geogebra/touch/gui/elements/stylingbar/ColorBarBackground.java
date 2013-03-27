package geogebra.touch.gui.elements.stylingbar;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.StylingBarEntries;
import geogebra.web.gui.util.Slider;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Contains the {@link ColorBar}.
 */
public class ColorBarBackground extends VerticalPanel
{
	// private final AnimationHelper animationHelper;
	private ColorBar colorBar;

	public ColorBarBackground(StylingBar stylingBar, final TouchModel touchModel)
	{
		this.colorBar = new ColorBar(stylingBar, touchModel);
		this.add(this.colorBar);

		LayoutPanel sliderPanel = new LayoutPanel();

		Slider slider = new Slider();
		slider.setMinimum(0);
		slider.setMaximum(10);
		slider.setValue(Integer.valueOf((int) (touchModel.getLastAlpha() * 10)));
		slider.setWidth("100%");

		slider.addValueChangeHandler(new ValueChangeHandler<Integer>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event)
			{
				touchModel.getGuiModel().setAlpha(event.getValue().intValue() / 10f);

				final List<GeoElement> fillable = new ArrayList<GeoElement>();
				for (GeoElement geo : touchModel.getSelectedGeos())
				{
					if (geo.isFillable())
					{
						fillable.add(geo);
					}
				}

				if (fillable.size() > 0 && StyleBarStatic.applyAlpha(fillable, event.getValue().intValue() / 10f))
				{
					fillable.get(0).updateRepaint();
					touchModel.storeOnClose();
				}
			}
		});

		// add slider only if there is at least one fillable element
		if (touchModel.getLastAlpha() != -1 || touchModel.getCommand().getStylingBarEntries() == StylingBarEntries.Polygon)
		{
			sliderPanel.add(slider);
			this.add(sliderPanel);
		}

		// TODO implement animationHelper
		// this.animationHelper = new AnimationHelper();
		// add(this.animationHelper);
	}
}
