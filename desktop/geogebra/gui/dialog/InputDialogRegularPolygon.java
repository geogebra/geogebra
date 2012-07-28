package geogebra.gui.dialog;


import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

public class InputDialogRegularPolygon extends InputDialogD{
	private static final long serialVersionUID = 1L;
	private GeoPoint geoPoint1, geoPoint2;

	private Kernel kernel;
	
	public InputDialogRegularPolygon(AppD app, String title, InputHandler handler, GeoPoint point1, GeoPoint point2, Kernel kernel) {
		super(app, app.getPlain("Points"), title, "4", false, handler);
		
		geoPoint1 = point1;
		geoPoint2 = point2;
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
		
		return DialogManagerD.makeRegularPolygon(app, inputPanel.getText(), geoPoint1, geoPoint2);
		
		/*
		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		boolean ret = inputHandler.processInput(inputPanel.getText());

		cons.setSuppressLabelCreation(oldVal);

		if (ret) {
			GeoElement[] geos = kernel.RegularPolygon(null, geoPoint1, geoPoint2, ((NumberInputHandler)inputHandler).getNum());
			GeoElement[] onlypoly = { null };
			if (geos != null) {
				onlypoly[0] = geos[0];
				app.storeUndoInfo();
				kernel.getApplication().getActiveEuclidianView().getEuclidianController().memorizeJustCreatedGeos(onlypoly);
			}
		}

		return ret;
		*/
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!wrappedDialog.isModal()) {
			app.setCurrentSelectionListener(null);
		}
		app.getGuiManager().setCurrentTextfield(this, true);
	}
}
