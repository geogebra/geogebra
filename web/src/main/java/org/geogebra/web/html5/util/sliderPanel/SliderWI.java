package org.geogebra.web.html5.util.sliderPanel;

import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

public interface SliderWI extends HasChangeHandlers, HasValue<Double>,
		MouseDownHandler, MouseUpHandler, MouseMoveHandler, IsWidget {

	void setMaximum(double max);

	void setMinimum(double min);

	void setStep(double step);

}
