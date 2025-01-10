package org.geogebra.web.html5.util.sliderPanel;

import org.gwtproject.event.dom.client.HasChangeHandlers;
import org.gwtproject.event.dom.client.MouseDownHandler;
import org.gwtproject.event.dom.client.MouseMoveHandler;
import org.gwtproject.event.dom.client.MouseUpHandler;
import org.gwtproject.user.client.ui.HasValue;
import org.gwtproject.user.client.ui.IsWidget;

public interface SliderWI extends HasChangeHandlers, HasValue<Double>,
		MouseDownHandler, MouseUpHandler, MouseMoveHandler, IsWidget {

	void setMaximum(double max);

	void setMinimum(double min);

	void setStep(double step);

}
