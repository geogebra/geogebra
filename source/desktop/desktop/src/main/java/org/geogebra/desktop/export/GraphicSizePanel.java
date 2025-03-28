/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package org.geogebra.desktop.export;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.geogebra.common.main.Localization;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.main.AppD;

/**
 * Panel with fields to enter width and height of graphic file to be exported.
 * The ratio of init width and height is kept constant. Use methods
 * getSelectedWidth() and getSelectedHeight() to retrieve the searched values.
 * 
 * @author Markus Hohenwarter
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 */
public class GraphicSizePanel extends JPanel
		implements ActionListener, FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int MIN = 10;

	private static final int MAX = 5000;

	private int width;
	private int height;

	private double ratio;

	private final JTextField tfWidth;
	private final JTextField tfHeight;

	private boolean keepRatio;

	public GraphicSizePanel(AppD app, int width, int height) {
		this(app, width, height, true);
	}

	/**
	 * @param app application
	 * @param width width
	 * @param height height
	 * @param keepRatio whether to keep x:y ratio
	 */
	public GraphicSizePanel(AppD app, int width, int height,
			boolean keepRatio) {
		setLayout(new FlowLayout(5));
		tfWidth = new MyTextFieldD(app, 5);
		tfHeight = new MyTextFieldD(app, 5);
		Localization loc = app.getLocalization();
		add(new JLabel(loc.getMenu("Width") + ":"));
		add(tfWidth);
		add(new JLabel(loc.getMenu("Height") + ":"));
		add(tfHeight);

		tfWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		tfHeight.setHorizontalAlignment(SwingConstants.RIGHT);

		setValues(width, height, keepRatio);

		tfWidth.addActionListener(this);
		tfHeight.addActionListener(this);
		tfWidth.addFocusListener(this);
		tfHeight.addFocusListener(this);
	}

	private void setValues(int width, int height, boolean keepRatio) {
		this.width = width;
		this.height = height;
		this.keepRatio = keepRatio;
		ratio = (double) width / height;

		tfWidth.setText("" + width);
		tfHeight.setText("" + height);
	}

	@Override
	public void setEnabled(boolean flag) {
		Object[] comp = getComponents();
		for (Object o : comp) {
			((JComponent) o).setEnabled(flag);
		}
	}

	private void updateValues(Object src) {
		if (src == tfWidth) {
			try {
				int newValue = Integer.parseInt(tfWidth.getText());
				if (MIN <= newValue && newValue <= MAX) {
					width = newValue;
				}
			} catch (Exception e) {
			}
			tfWidth.setText("" + width);
			if (keepRatio) {
				height = (int) Math.floor(width / ratio);
				tfHeight.setText("" + height);
			}
		} else if (src == tfHeight) {
			try {
				int newValue = Integer.parseInt(tfHeight.getText());
				if (MIN <= newValue && newValue <= MAX) {
					height = newValue;
				}
			} catch (Exception e) {
			}
			tfHeight.setText("" + height);
			if (keepRatio) {
				width = (int) Math.floor(height * ratio);
				tfWidth.setText("" + width);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		updateValues(ev.getSource());
	}

	@Override
	public void focusGained(FocusEvent ev) {
		//
	}

	@Override
	public void focusLost(FocusEvent ev) {
		updateValues(ev.getSource());
	}

}
