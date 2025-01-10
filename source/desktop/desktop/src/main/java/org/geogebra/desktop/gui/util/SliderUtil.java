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
