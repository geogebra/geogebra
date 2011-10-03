package geogebra.gui;


import geogebra.gui.GuiManager.NumberInputHandler;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.Transformable;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JPanel;

public class InputDialogDilate extends InputDialog {
		
	GeoPoint[] points;
	GeoElement[] selGeos;

	private Kernel kernel;
		
	public InputDialogDilate(Application app, String title, InputHandler handler,  GeoPoint[] points, GeoElement[] selGeos, Kernel kernel) {
		super(app.getFrame(), false);
		
		this.app = app;
		inputHandler = handler;
		
		this.points = points;
		this.selGeos = selGeos;
		this.kernel = kernel;

		createGUI(title, app.getPlain("Numeric"), false, DEFAULT_COLUMNS, 1, true, true, false, false, false, false, false);		
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(inputPanel, BorderLayout.CENTER);								
		getContentPane().add(centerPanel, BorderLayout.CENTER);		
		centerOnScreen();

	}

	/**
	 * Handles button clicks for dialog.
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
					setVisibleForTools(!processInput());
				} else if (source == btApply) {
					processInput();
				} else if (source == btCancel) {
					setVisibleForTools(false);
			} 
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			setVisibleForTools(false);
		}
	}
	
	private boolean processInput() {
		
		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		boolean success = inputHandler.processInput(inputPanel.getText());

		cons.setSuppressLabelCreation(oldVal);
		
		
		
		if (success) {
			NumberValue num = ((NumberInputHandler)inputHandler).getNum();

			if (selGeos.length > 0) {					
				// mirror all selected geos
				//GeoElement [] selGeos = getSelectedGeos();
				GeoPoint point = points[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i=0; i < selGeos.length; i++) {				
					if (selGeos[i] != point) {
						if ((selGeos[i] instanceof Transformable) || selGeos[i].isGeoList())
							ret.addAll(Arrays.asList(kernel.Dilate(null,  selGeos[i], num, point)));
					}
				}
				if (!ret.isEmpty()) {
					kernel.getApplication().getActiveEuclidianView().getEuclidianController().memorizeJustCreatedGeos(ret);
					app.storeUndoInfo();
				}
				return true;
			}
			
		}

		
		return false;
		
	}

	public void windowGainedFocus(WindowEvent arg0) {
		if (!isModal()) {
			app.setCurrentSelectionListener(null);
		}
		app.getGuiManager().setCurrentTextfield(this, true);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
