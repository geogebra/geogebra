/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;

import org.geogebra.common.euclidian.smallscreen.AdjustSlider;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.desktop.gui.properties.SliderPanelD;
import org.geogebra.desktop.gui.view.algebra.InputPanelD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

/**
 * Dialog for slider creation
 */
public class SliderDialogD extends Dialog
		implements ActionListener, KeyListener, WindowListener {

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	private JButton btOK, btCancel;
	private JRadioButton rbNumber, rbAngle, rbInteger;
	private InputPanelD tfLabel;
	private JPanel optionPane;
	private JCheckBox cbRandom;

	private AppD app;
	private SliderPanelD sliderPanel;

	private GeoElement geoResult;
	private GeoNumeric number;
	private GeoAngle angle;
	private LocalizationD loc;

	/**
	 * Creates a dialog to create a new GeoNumeric for a slider.
	 * 
	 * @param x
	 *            x-coordinate of slider in screen coords
	 * @param y
	 *            x-coordinate of slider in screen coords
	 * @param app
	 *            application
	 */
	public SliderDialogD(AppD app, int x, int y) {
		super(app.getFrame(), false);
		this.app = app;
		this.loc = app.getLocalization();
		addWindowListener(this);

		// create temp geos that may be returned as result
		Construction cons = app.getKernel().getConstruction();

		number = new GeoNumeric(cons);
		angle = new GeoAngle(cons);

		// allow outside range 0-360
		angle.setAngleStyle(AngleStyle.UNBOUNDED);

		GeoNumeric.setSliderFromDefault(number, false);
		GeoNumeric.setSliderFromDefault(angle, true);
		number.setValue(1);
		angle.setValue(45 * Math.PI / 180);

		number.setSliderLocation(x, y, true);
		angle.setSliderLocation(x, y, true);

		geoResult = null;

		createGUI();
	}

	private void createGUI() {
		setTitle(loc.getMenu("Slider"));
		setResizable(false);

		// Create components to be displayed

		// radio buttons for number or angle
		ButtonGroup bg = new ButtonGroup();
		rbNumber = new JRadioButton(loc.getMenu("Numeric"));
		rbAngle = new JRadioButton(loc.getMenu("Angle"));
		rbInteger = new JRadioButton(loc.getMenu("Integer"));
		rbNumber.addActionListener(this);
		rbAngle.addActionListener(this);
		rbInteger.addActionListener(this);
		bg.add(rbNumber);
		bg.add(rbAngle);
		bg.add(rbInteger);
		rbNumber.setSelected(true);
		// JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5,
		// 5));
		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
		radioPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 20));
		radioPanel.add(rbNumber);
		radioPanel.add(rbAngle);
		radioPanel.add(rbInteger);

		// label textfield
		tfLabel = new InputPanelD(number.getDefaultLabel(), app, 1, 10, true);
		tfLabel.getTextComponent().addKeyListener(this);
		Border border = BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(loc.getMenu("Name")),
				BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tfLabel.setBorder(border);

		cbRandom = new JCheckBox(loc.getMenu("Random"));

		// put together label textfield and radioPanel
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(0, 0));
		JPanel labelPanel = new JPanel(new BorderLayout(0, 0));
		labelPanel.add(tfLabel, BorderLayout.NORTH);
		labelPanel.add(cbRandom, BorderLayout.SOUTH);
		topPanel.add(labelPanel, BorderLayout.CENTER);
		topPanel.add(radioPanel, app.getLocalization().borderWest());

		// slider panels
		sliderPanel = new SliderPanelD(app, null, true, false);
		JPanel slPanel = new JPanel(new BorderLayout(0, 0));
		GeoElement[] geos = { number };
		slPanel.add(sliderPanel.updatePanel(geos), BorderLayout.CENTER);

		// buttons
		btOK = new JButton(loc.getMenu("OK"));
		btOK.setActionCommand("OK");
		btOK.addActionListener(this);
		btCancel = new JButton(loc.getMenu("Cancel"));
		btCancel.setActionCommand("Cancel");
		btCancel.addActionListener(this);
		JPanel btPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btPanel.add(btOK);
		btPanel.add(btCancel);

		// Create the JOptionPane.
		optionPane = new JPanel(new BorderLayout(5, 5));
		optionPane.add(topPanel, BorderLayout.NORTH);
		optionPane.add(slPanel, BorderLayout.CENTER);
		optionPane.add(btPanel, BorderLayout.SOUTH);
		optionPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Make this dialog display it.
		setContentPane(optionPane);

		app.setComponentOrientation(this);

		pack();
		setLocationRelativeTo(app.getFrame());
	}

	/**
	 * @return resulting slider
	 */
	public GeoElement getResult() {
		if (geoResult != null) {
			// set label of geoResult
			String strLabel;
			String text = tfLabel.getText();
			try {
				strLabel = app.getKernel().getAlgebraProcessor()
						.parseLabel(text);
			} catch (Exception e) {
				strLabel = null;
			}
			geoResult.setLabel(strLabel);

			// allow eg a=2 in the Name dialog to set the initial value
			if (strLabel != null && text.indexOf('=') > -1
					&& text.indexOf('=') == text.lastIndexOf('=')) {

				try {
					double val = Double
							.parseDouble(text.substring(text.indexOf('=') + 1));

					GeoNumeric geoNum = ((GeoNumeric) geoResult);

					if (val > geoNum.getIntervalMax()) {
						geoNum.setIntervalMax(val);
					} else if (val < geoNum.getIntervalMin()) {
						geoNum.setIntervalMin(val);
					}

					geoNum.setValue(val);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return geoResult;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == btOK) {
			geoResult = rbAngle.isSelected() ? angle : number;
			getResult();
			geoResult.setLabelMode(GeoElementND.LABEL_NAME_VALUE);
			geoResult.setLabelVisible(true);
			geoResult.update();
			if (!rbAngle.isSelected()) {
				AdjustSlider.ensureOnScreen((GeoNumeric) geoResult,
						app.getActiveEuclidianView());
			}

			((GeoNumeric) geoResult).setRandom(cbRandom.isSelected());

			setVisible(false);

			app.storeUndoInfo();
		} else if (source == btCancel) {
			setVisible(false);
		} else if (source == rbNumber || source == rbAngle
				|| source == rbInteger) {
			GeoNumeric selGeo = rbAngle.isSelected() ? angle : number;
			if (source == rbInteger) {
				number.setAutoStep(false);
				number.setAnimationStep(1);
				number.setIntervalMin(1);
				number.setIntervalMax(30);
			} else if (source == rbNumber) {
				GeoNumeric num = app.getKernel().getAlgoDispatcher()
						.getDefaultNumber(false);
				number.setAutoStep(num.isAutoStep());
				number.setAnimationStep(num.getAnimationStep());

				number.setIntervalMin(num.getIntervalMin());
				number.setIntervalMax(num.getIntervalMax());
			}
			GeoElement[] geos = { selGeo };
			sliderPanel.updatePanel(geos);

			// update label text field
			String sliderLabel = source == rbInteger
					? selGeo.getLabelManager().getNextIntegerLabel()
					: selGeo.getDefaultLabel();
			tfLabel.setText(sliderLabel);
			setLabelFieldFocus();
		}
	}

	private void setLabelFieldFocus() {
		tfLabel.getTextComponent().requestFocus();
		tfLabel.selectText();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		default:
			// do nothing
			break;
		case KeyEvent.VK_ENTER:
			btOK.doClick();
			break;

		case KeyEvent.VK_ESCAPE:
			btCancel.doClick();
			e.consume();
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// only key press is important
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// only key press is important
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// only window opened is important
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// only window opened is important
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// only window opened is important
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// only window opened is important
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// only window opened is important
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// only window opened is important
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		setLabelFieldFocus();
	}

}