/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.gui.accessibility;

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.util.sliderPanel.SliderW;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Overflow;
import org.gwtproject.dom.style.shared.Position;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.Widget;

/**
 * Creates widgets for navigating the construction with Voiceover (iOS) or
 * Talkback (Android)
 * 
 * @author Zbynek
 */
public class WidgetFactory {

	/**
	 * For sliders we want more restrictive hide than for other widgets
	 */
	private static void hideSlider(Widget ui) {
		Style style = ui.getElement().getStyle();
		style.setOpacity(.01);
		style.setPosition(Position.FIXED);
		style.setWidth(1, Unit.PX);
		style.setHeight(1, Unit.PX);
		style.setOverflow(Overflow.HIDDEN);
	}

	/**
	 * @param index  slider identifier in case listener has more sliders
	 * @param source listener
	 * @return slider
	 */
	public static SliderW makeSlider(final int index, final HasSliders source,
			BaseWidgetFactory factory) {
		final SliderW range = factory.newSlider(0, 100);
		hideSlider(range);
		range.getElement().addClassName("slider");
		range.addValueChangeHandler(event -> source.onValueChange(index, event.getValue()));
		range.getElement().setTabIndex(5000);
		return range;
	}

}
