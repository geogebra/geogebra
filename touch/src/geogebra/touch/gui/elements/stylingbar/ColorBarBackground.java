package geogebra.touch.gui.elements.stylingbar;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.html5.gui.util.Slider;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.StylingBarEntries;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Contains the {@link ColorBar}.
 */
public class ColorBarBackground extends PopupPanel
{
	// private final AnimationHelper animationHelper;
	private VerticalPanel contentPanel;
	private ColorBar colorBar;
	private Slider slider;

	// FIXME create the popup beforehand, hide or show the slider depending on
	// criteria
	public ColorBarBackground(StylingBar stylingBar, final TouchModel touchModel)
	{
		this.contentPanel = new VerticalPanel();
		
		this.colorBar = new ColorBar(stylingBar, touchModel);
		this.contentPanel.add(this.colorBar);

		this.slider = new Slider();
		this.slider.setMinimum(0);
		this.slider.setMaximum(10);
		this.slider.setValue(Integer.valueOf((int) (touchModel.getLastAlpha() * 10)));
		this.slider.setWidth("100%");

		this.slider.addValueChangeHandler(new ValueChangeHandler<Integer>()
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
			this.contentPanel.add(this.slider);
		}

		// TODO implement animationHelper
		// this.animationHelper = new AnimationHelper();
		// add(this.animationHelper);
		this.setWidget(this.contentPanel);
	}
}
