package geogebra.gui.dialog;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.gui.InputHandler;
import geogebra.gui.dialog.handler.NumberInputHandler;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

public class InputDialogSegmentFixed extends InputDialog{
	
	private GeoPoint2 geoPoint1;

	private Kernel kernel;
	
	public InputDialogSegmentFixed(Application app, String title, InputHandler handler, GeoPoint2 point1, Kernel kernel) {
		super(app, app.getPlain("Length"), title, "", false, handler);
		
		geoPoint1 = point1;
		this.kernel = kernel;

	}

	/**
	 * Handles button clicks for dialog.
	 */
	@Override
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

		boolean ret = inputHandler.processInput(inputPanel.getText());

		cons.setSuppressLabelCreation(oldVal);

		if (ret) { 
			GeoElement[] segment = kernel.Segment(null, geoPoint1, ((NumberInputHandler)inputHandler).getNum());
			GeoElement[] onlysegment = { null };
			if (segment != null) {
				onlysegment[0] = segment[0];
				app.storeUndoInfo();
				kernel.getApplication().getActiveEuclidianView().getEuclidianController().memorizeJustCreatedGeos(onlysegment);
			}
		}

		return ret;
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!isModal()) {
			app.setCurrentSelectionListener(null);
		}
		app.getGuiManager().setCurrentTextfield(this, true);
	}
}
