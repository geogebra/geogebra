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

package org.geogebra.desktop.gui.util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.JSlider;

public class SliderUtil {

	/**
	 * @param mySlider slider
	 * @param handler handler for actual change
	 */
	public static void addValueChangeListener(JSlider mySlider, Consumer<Integer> handler) {
		mySlider.addMouseListener(new MouseAdapter() {
			int dragStartValue;

			@Override
			public void mouseReleased(MouseEvent e) {
				if (mySlider.getValue() != dragStartValue) {
					handler.accept(mySlider.getValue());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				dragStartValue = mySlider.getValue();
			}
		});
	}
}
