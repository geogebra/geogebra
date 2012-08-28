/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui.dialog;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.DialogManager;
import geogebra.gui.properties.SliderPanel;
import geogebra.gui.view.algebra.InputPanelD;
import geogebra.main.AppD;

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
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;

public class SliderDialog extends JDialog 
			implements ActionListener, KeyListener, WindowListener
{
	
	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	private JButton btApply, btCancel;
	private JRadioButton rbNumber, rbAngle, rbInteger;
	private InputPanelD tfLabel;
	private JPanel optionPane;
	private JCheckBox cbRandom;
	
	private AppD app;
	private SliderPanel sliderPanel;
	
	private GeoElement geoResult;
	private GeoNumeric number;
	private GeoAngle angle;
			
	/**
	 * Creates a dialog to create a new GeoNumeric for a slider.
	 * @param x x-coordinate of slider in screen coords
	 * @param y x-coordinate of slider in screen coords
	 * @param app
	 */
	public SliderDialog(AppD app, int x, int y) {
		super(app.getFrame(), false);
		this.app = app;		
		addWindowListener(this);
		
		// create temp geos that may be returned as result
		Construction cons = app.getKernel().getConstruction();
		
		
		number = new GeoNumeric(cons);
		angle = new GeoAngle(cons);
		DialogManager.setSliderFromDefault(number,false);
		DialogManager.setSliderFromDefault(angle,true);
		number.setValue(1);
		angle.setValue(45 * Math.PI/180);
			
		number.setSliderLocation(x, y, true);
		angle.setSliderLocation(x, y, true);
		
		
				
		geoResult = null;

		createGUI();	
	}			
	
	private void createGUI() {
		setTitle(app.getPlain("Slider"));
		setResizable(false);		

		//Create components to be displayed			
		
		// radio buttons for number or angle
		ButtonGroup bg = new ButtonGroup();
		rbNumber = new JRadioButton(app.getPlain("Numeric"));		
		rbAngle = new JRadioButton(app.getPlain("Angle"));		
		rbInteger = new JRadioButton(app.getPlain("Integer"));		
		rbNumber.addActionListener(this);
		rbAngle.addActionListener(this);		
		rbInteger.addActionListener(this);		
		bg.add(rbNumber);
		bg.add(rbAngle);			
		bg.add(rbInteger);			
		rbNumber.setSelected(true);
		//JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
		radioPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 20));
		radioPanel.add(rbNumber);		
		radioPanel.add(rbAngle);			
		radioPanel.add(rbInteger);			
		
		// label textfield
		tfLabel = new InputPanelD(number.getDefaultLabel(), app, 1, 10, true);				
		tfLabel.getTextComponent().addKeyListener(this);				
		Border border =
			BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(app.getPlain("Name")),
				BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tfLabel.setBorder(border);
		
		cbRandom = new JCheckBox(app.getPlain("Random"));
		
		// put together label textfield and radioPanel
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(0,0));
		JPanel labelPanel = new JPanel(new BorderLayout(0,0));
		labelPanel.add(tfLabel, BorderLayout.NORTH);
		labelPanel.add(cbRandom, BorderLayout.SOUTH);
		topPanel.add(labelPanel, BorderLayout.CENTER);
		topPanel.add(radioPanel, BorderLayout.WEST);

		// slider panels		
		sliderPanel = new SliderPanel(app, null, true, false);			
		JPanel slPanel = new JPanel(new BorderLayout(0,0));		
		GeoElement [] geos = { number };
		slPanel.add(sliderPanel.update(geos), BorderLayout.CENTER);		
		
		// buttons
		btApply = new JButton(app.getPlain("Apply"));
		btApply.setActionCommand("Apply");
		btApply.addActionListener(this);
		btCancel = new JButton(app.getPlain("Cancel"));
		btCancel.setActionCommand("Cancel");
		btCancel.addActionListener(this);
		JPanel btPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btPanel.add(btApply);
		btPanel.add(btCancel);
	
		//Create the JOptionPane.
		optionPane = new JPanel(new BorderLayout(5,5));		
		optionPane.add(topPanel, BorderLayout.NORTH);
		optionPane.add(slPanel, BorderLayout.CENTER);
		optionPane.add(btPanel, BorderLayout.SOUTH);	
		optionPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));				
		
		//Make this dialog display it.
		setContentPane(optionPane);			
		pack();	
		setLocationRelativeTo(app.getFrame());	
	}
	
	public GeoElement getResult() {
		if (geoResult != null) {		
			// set label of geoResult
			String strLabel;
			String text = tfLabel.getText();
			try {								
				strLabel = app.getKernel().getAlgebraProcessor().
								parseLabel(text);
			} catch (Exception e) {
				strLabel = null;
			}			
			geoResult.setLabel(strLabel);
			
			// allow eg a=2 in the Name dialog to set the initial value
			if (strLabel != null && text.indexOf('=') > -1 && text.indexOf('=') == text.lastIndexOf('=')) {
				
				try {
					double val = Double.parseDouble(text.substring(text.indexOf('=')+1));
					
					GeoNumeric geoNum = ((GeoNumeric)geoResult);
					
					if (val > geoNum.getIntervalMax()) geoNum.setIntervalMax(val);
					else if (val < geoNum.getIntervalMin()) geoNum.setIntervalMin(val);
					
					geoNum.setValue(val);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return geoResult;
	}
		
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
				
		if (source == btApply) {				
			geoResult = rbAngle.isSelected() ? angle : number; 		
			getResult();
			geoResult.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			geoResult.setLabelVisible(true);
			geoResult.update();
			((GeoNumeric)geoResult).setRandom(cbRandom.isSelected());

			setVisible(false);
			
			app.storeUndoInfo();
		} 
		else if (source == btCancel) {						
			setVisible(false);
		} 
		else if (source == rbNumber || source == rbAngle || source == rbInteger) {
			GeoElement selGeo = rbAngle.isSelected() ? angle : number;			
			if (source == rbInteger) {
				number.setAnimationStep(1);
				number.setIntervalMin(1);
				number.setIntervalMax(30);
			} else if (source == rbNumber) {
				GeoNumeric num = app.getKernel().getDefaultNumber(false);
				number.setAnimationStep(num.getAnimationStep());
				number.setIntervalMin(num.getIntervalMin());
				number.setIntervalMax(num.getIntervalMax());
			}
			GeoElement [] geos = { selGeo };			
			sliderPanel.update(geos);			
			
			// update label text field
			tfLabel.setText(selGeo.getDefaultLabel(source == rbInteger));	
			setLabelFieldFocus();
		}		
	}

	private void setLabelFieldFocus() {	
		tfLabel.getTextComponent().requestFocus();
		tfLabel.selectText();	
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ENTER:		
				btApply.doClick();
				break;
				
			case KeyEvent.VK_ESCAPE:
				btCancel.doClick();
				e.consume();
				break;				
		}					
	}

	public void keyReleased(KeyEvent arg0) {		
	}

	public void keyTyped(KeyEvent arg0) {		
	}

	public void windowActivated(WindowEvent arg0) {		
	}

	public void windowClosed(WindowEvent arg0) {		
	}

	public void windowClosing(WindowEvent arg0) {		
	}

	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {		
		setLabelFieldFocus();
	}

	
			
}