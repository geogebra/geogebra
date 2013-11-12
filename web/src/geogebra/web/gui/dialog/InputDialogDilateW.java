package geogebra.web.gui.dialog;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.handler.NumberInputHandler;
import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.DialogManager;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InputDialogDilateW extends InputDialogW {
	GeoPointND[] points;
	GeoElement[] selGeos;

	private Kernel kernel;
	
	private EuclidianController ec;

	public InputDialogDilateW(AppW app, String title, InputHandler handler,
			GeoPointND[] points, GeoElement[] selGeos, Kernel kernel, EuclidianController ec) {
		super(false);

		this.app = app;
		inputHandler = handler;

		this.points = points;
		this.selGeos = selGeos;
		this.kernel = kernel;
		
		this.ec = ec;

		createGUI(title, app.getMenu("Dilate.Factor"), false, DEFAULT_COLUMNS,
				1, true, false, false, false, DialogType.GeoGebraEditor);
		
		FlowPanel centerPanel = new FlowPanel();
		centerPanel.add(inputPanel);
		((VerticalPanel) wrappedPopup.getWidget()).insert(centerPanel, 0);
		
		wrappedPopup.center();
	}

	/**
	 * Handles button clicks for dialog.
	 */
	@Override
	public void onClick(ClickEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				setVisible(!processInput());
			} else if (source == btApply) {
				processInput();
			} else if (source == btCancel) {
				setVisible(false);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisible(false);
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
			return DialogManager.doDilate(kernel,
					((NumberInputHandler) inputHandler).getNum(), points,
					selGeos, ec);
		}

		return false;

		/*
		 * 
		 * // avoid labeling of num Construction cons =
		 * kernel.getConstruction(); boolean oldVal =
		 * cons.isSuppressLabelsActive(); cons.setSuppressLabelCreation(true);
		 * 
		 * boolean success = inputHandler.processInput(inputPanel.getText());
		 * 
		 * cons.setSuppressLabelCreation(oldVal);
		 * 
		 * if (success) { NumberValue num =
		 * ((NumberInputHandler)inputHandler).getNum();
		 * 
		 * if (selGeos.length > 0) { // mirror all selected geos //GeoElement []
		 * selGeos = getSelectedGeos(); GeoPoint2 point = points[0];
		 * ArrayList<GeoElement> ret = new ArrayList<GeoElement>(); for (int
		 * i=0; i < selGeos.length; i++) { if (selGeos[i] != point) { if
		 * ((selGeos[i] instanceof Transformable) || selGeos[i].isGeoList())
		 * ret.addAll(Arrays.asList(kernel.Dilate(null, selGeos[i], num,
		 * point))); } } if (!ret.isEmpty()) {
		 * kernel.getApplication().getActiveEuclidianView
		 * ().getEuclidianController().memorizeJustCreatedGeos(ret);
		 * app.storeUndoInfo(); } return true; } } return false;
		 */

	}

}
