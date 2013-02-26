/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.inputbar;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.SetLabels;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;
import geogebra.gui.GuiManagerD;
import geogebra.gui.inputfield.AutoCompleteTextFieldD;
import geogebra.gui.view.algebra.AlgebraInputDropTargetListener;
import geogebra.gui.view.algebra.InputPanelD;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

/**
 * @author Markus Hohenwarter
 */
public class AlgebraInput extends  JPanel implements ActionListener, KeyListener, FocusListener, SetLabels, MouseListener {
	private static final long serialVersionUID = 1L;


	private AppD app;

	// autocompletion text field
	private AutoCompleteTextFieldD inputField;

	private JLabel inputLabel;
	private JToggleButton btnHelpToggle;
	private InputPanelD inputPanel;


	/***********************************************************
	 * creates new AlgebraInput
	 * @param app 
	 */
	public AlgebraInput(AppD app) {		
		this.app = app;		

		app.removeTraversableKeys(this);

		initGUI();
		
		addMouseListener(this);
	}


	public void initGUI() {
		removeAll();
		inputLabel = new JLabel(); 
		inputPanel = new InputPanelD(null, app, 30, true);

		// create and set up the input field
		inputField = (AutoCompleteTextFieldD) inputPanel.getTextComponent();			
		inputField.setEditable(true);						
		inputField.addKeyListener(this);
		inputField.addFocusListener(this);
		
		// enable a history popup and embedded button 
		inputField.addHistoryPopup(app.showInputTop());
		
		// enable drops
		inputField.setDragEnabled(true);
		inputField.setDropTarget(new DropTarget(this,
				new AlgebraInputDropTargetListener(app, inputField)));
		
		updateFonts();


		// create toggle button to hide/show the input help panel
		btnHelpToggle = new JToggleButton(){
		      public Point getToolTipLocation(MouseEvent e) {
		    	  // make sure tooltip doesn't cover button (when window maximized)
		          return new Point(0, (int) -this.getSize().getHeight() / 2);
		        }
		      };
		
		//btnHelpToggle.setIcon(app.getImageIcon("inputhelp_left_16x16.png"));
		//btnHelpToggle.setSelectedIcon(app.getImageIcon("inputhelp_right_16x16.png"));
		
		btnHelpToggle.setIcon(app.getImageIcon("inputhelp_left_18x18.png"));
		btnHelpToggle.setSelectedIcon(app.getImageIcon("inputhelp_right_18x18.png"));
		
		//btnHelpToggle.setIcon(app.getImageIcon("inputhelp_left_20x20.png"));
		//btnHelpToggle.setSelectedIcon(app.getImageIcon("inputhelp_right_20x20.png"));
		
		btnHelpToggle.addActionListener(this);
		btnHelpToggle.setFocusable(false);
		btnHelpToggle.setContentAreaFilled(false);   
		btnHelpToggle.setBorderPainted(false);


		// create sub-panels				 		
		JPanel labelPanel = new JPanel(new BorderLayout());
		
		labelPanel.add(inputLabel, app.borderEast());

		JPanel eastPanel = new JPanel(new BorderLayout());
		if (app.showInputHelpToggle()) {
			eastPanel.add(btnHelpToggle, app.borderWest());
		}
		
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0,10, 0, 2));
		eastPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
		inputPanel.setBorder(BorderFactory.createEmptyBorder(2,0,2,0));
		
		setLayout(new BorderLayout(0,0));
		add(labelPanel, app.borderWest());
		add(inputPanel, BorderLayout.CENTER);
		add(eastPanel, app.borderEast());

		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, SystemColor.controlShadow));
		setLabels();
	}

	@Override
	public boolean requestFocusInWindow() { 
		return inputField.requestFocusInWindow();
	}

	@Override
	public void requestFocus() {
		requestFocusInWindow();
	}

	@Override
	public boolean hasFocus() {
		return inputField.hasFocus();
	}

	public void clear() {
		inputField.setText(null);
	}		

	public AutoCompleteTextFieldD getTextField() {
		return inputField;
	}

	public void updateOrientation(boolean showInputTop){
		inputField.setOpenSymbolTableUpwards(!showInputTop);
	}
	
	
	/**
	 * updates labels according to current locale
	 */
	public void setLabels() {
		if (inputLabel != null)
			inputLabel.setText( app.getPlain("InputLabel") + ":");

		if(btnHelpToggle!=null)
			btnHelpToggle.setToolTipText(app.getMenu("InputHelp"));
	
		inputField.setDictionary(app.getCommandDictionary());
	}	


	public void updateFonts() {
		inputField.setFont(app.getBoldFont());		
		inputLabel.setFont(app.getPlainFont());
		inputField.setPopupsFont(app.getPlainFont());
		
		//update the help panel
		if (((GuiManagerD) app.getGuiManager()).hasInputHelpPanel())
		{
			InputBarHelpPanel helpPanel = (InputBarHelpPanel) ((GuiManagerD) app.getGuiManager()).getInputHelpPanel();
			helpPanel.updateFonts();
		}
	}    

	//	/**
	//	 * Inserts string at current position of the input textfield and gives focus
	//	 * to the input textfield.
	//	 * @param str: inserted string
	//	 */
	//	public void insertString(String str) {
	//		inputField.replaceSelection(str);
	//	}

	/**
	 * Sets the content of the input textfield and gives focus
	 * to the input textfield.
	 * @param str 
	 */
	public void replaceString(String str) {
		inputField.setText(str);
	}

	// see actionPerformed
	public void insertCommand(String cmd) {
		if (cmd == null) return;

		int pos = inputField.getCaretPosition();
		String oldText = inputField.getText();
		String newText = 
			oldText.substring(0, pos) + 
			cmd + "[]" +
			oldText.substring(pos);			 			

		inputField.setText(newText);
		inputField.setCaretPosition(pos + cmd.length() + 1);		
		inputField.requestFocus();
	}

	public void insertString(String str) {
		if (str == null) return;

		int pos = inputField.getCaretPosition();
		String oldText = inputField.getText();
		String newText = 
			oldText.substring(0, pos) + str +
			oldText.substring(pos);			 			

		inputField.setText(newText);
		inputField.setCaretPosition(pos + str.length());		
		inputField.requestFocus();
	}

	/**
	 * action listener implementation for input help panel toggle button
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == btnHelpToggle) {

			// ==========================================
			// hidden switch to toggle native/crossPlatform LAF
			if (AppD.getControlDown() && AppD.getShiftDown()) {
				AppD.toggleCrossPlatformLAF();
				SwingUtilities.updateComponentTreeUI(app.getFrame());
				app.getFrame().pack();
				return;
			}
			// =========================================
			
			if (btnHelpToggle.isSelected()) {
				InputBarHelpPanel helpPanel = (InputBarHelpPanel) ((GuiManagerD) app
						.getGuiManager()).getInputHelpPanel();
				helpPanel.setLabels();
				helpPanel.setCommands();
				app.setShowInputHelpPanel(true);
			}else{
				app.setShowInputHelpPanel(false);
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		// the input field may have consumed this event
		// for auto completion
		if (e.isConsumed()) return;

		int keyCode = e.getKeyCode();

		switch (keyCode) {
		case KeyEvent.VK_A:	
		case KeyEvent.VK_C:	
		case KeyEvent.VK_X:
		case KeyEvent.VK_V:
			// make sure eg Ctrl-A not passed on
			return;
		case KeyEvent.VK_ENTER:	
			app.getKernel().clearJustCreatedGeosInViews();
			String input = inputField.getText();					   
			if (input == null || input.length() == 0)
			{
				app.getActiveEuclidianView().requestFocus(); // Michael Borcherds 2008-05-12
				return;
			}

			app.setScrollToShow(true);
			GeoElement[] geos;
			try {
				{
					geos = app.getKernel().getAlgebraProcessor().processAlgebraCommandNoExceptionHandling( input, true, false, true );

					// need label if we type just eg
					// lnx
					if (geos.length == 1 && !geos[0].labelSet) {
						geos[0].setLabel(geos[0].getDefaultLabel());
					}

					//set first outputs (same geo class) as selected geos (for properties view)					
					if(geos.length>0){
						ArrayList<GeoElement> list = new ArrayList<GeoElement>();
						//add first output
						GeoElement geo = geos[0];
						list.add(geo);
						GeoClass c = geo.getGeoClassType();
						int i = 1;
						//add following outputs until geo class changes
						while (i<geos.length){
							geo = geos[i];
							if (geo.getGeoClassType() == c){
								list.add(geo);
								i++;
							}else{
								i = geos.length;
							}
						}
						app.getSelectionManager().setSelectedGeos(list);
					}

				}
			} catch (Exception ee) {
				inputField.showError(ee);
				return;
			}
			catch (MyError ee) {
				inputField.showError(ee);
				return;
			}


			// create texts in the middle of the visible view
			// we must check that size of geos is not 0 (ZoomIn, ZoomOut, ...)
			if (geos.length > 0 && geos[0] != null && geos[0].isGeoText()) {
				GeoText text = (GeoText)geos[0];
				if (!text.isTextCommand() && text.getStartPoint() == null) {

					Construction cons = text.getConstruction();
					EuclidianView ev = app.getActiveEuclidianView();

					boolean oldSuppressLabelsStatus = cons.isSuppressLabelsActive();
					cons.setSuppressLabelCreation(true);
					GeoPoint p = new GeoPoint(text.getConstruction(), null, ( ev.getXmin() + ev.getXmax() ) / 2, ( ev.getYmin() + ev.getYmax() ) / 2, 1.0);
					cons.setSuppressLabelCreation(oldSuppressLabelsStatus);

					try {
						text.setStartPoint(p);
						text.update();
					} catch (CircularDefinitionException e1) {
						e1.printStackTrace();
					}
				}
			}

			app.setScrollToShow(false);


			inputField.addToHistory(input);
			inputField.setText(null);  							  			   

			break;
		default:
			app.getGlobalKeyDispatcher().handleGeneralKeys(e); // handle eg ctrl-tab
		}
	}

	public void keyReleased(KeyEvent e) {
		//
	}

	public void keyTyped(KeyEvent e) {	
		//
	}

	public void focusGained(FocusEvent arg0) {
		//app.clearSelectedGeos();
	}

	public void focusLost(FocusEvent arg0) {
		//
	}


	public void mouseClicked(MouseEvent e) {
		// 
		
	}


	public void mousePressed(MouseEvent e) {
		// 
		
	}


	public void mouseReleased(MouseEvent e) {
		// 
		
	}


	public void mouseEntered(MouseEvent e) {
		// make sure tooltips from Tool Bar don't get in the way 
		setToolTipText("");
	}


	public void mouseExited(MouseEvent e) {
		// 
		
	}	 
}