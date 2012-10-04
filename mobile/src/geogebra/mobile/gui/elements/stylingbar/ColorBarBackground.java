package geogebra.mobile.gui.elements.stylingbar;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.mobile.model.MobileModel;
import geogebra.mobile.utils.StylingBarEntries;
import geogebra.web.gui.util.Slider;

import java.util.ArrayList;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

/**
 * Contains the {@link ColorBar}.
 */
public class ColorBarBackground extends VerticalPanel
{
	// private final AnimationHelper animationHelper;
	private ColorBar colorBar;

	public ColorBarBackground(StylingBar stylingBar, final MobileModel mobileModel)
	{
		this.addStyleName("colorBarBackground");
		this.colorBar = new ColorBar(stylingBar, mobileModel);
		this.add(this.colorBar);

		RoundPanel sliderPanel = new RoundPanel();
		sliderPanel.addStyleName("colorSliderPanel");
		
		Slider slider = new Slider();
		slider.setMinimum(0);
		slider.setMaximum(10);
		slider.setValue((int) (mobileModel.getLastAlpha() * 10));
		slider.setWidth("100%");

		slider.addValueChangeHandler(new ValueChangeHandler<Integer>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event)
			{
				mobileModel.getGuiModel().setAlpha(event.getValue() / 10f);

				final ArrayList<GeoElement> fillable = new ArrayList<GeoElement>();
				for (GeoElement geo : mobileModel.getSelectedGeos())
				{
					if (geo.isFillable())
					{
						fillable.add(geo);
					}
				}

				if (fillable.size() > 0 && StyleBarStatic.applyAlpha(fillable, event.getValue() / 10f))
				{
					fillable.get(0).updateRepaint();
				}
			}
		});

		// add slider only if there is at least one fillable element
		if (mobileModel.getLastAlpha() != -1 || mobileModel.getCommand().getStylingBarEntries() == StylingBarEntries.Polygon)
		{
			sliderPanel.add(slider);
			this.add(sliderPanel);
		}

		// TODO implement animationHelper
		// this.animationHelper = new AnimationHelper();
		// add(this.animationHelper);
	}
}
