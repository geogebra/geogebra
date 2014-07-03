package geogebra.touch.gui.laf;

import geogebra.touch.TouchApp;
import geogebra.touch.gui.TouchGUI;
import geogebra.touch.gui.elements.ggt.MaterialListElementT;
import geogebra.touch.gui.euclidian.EuclidianViewT;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DefaultLAF implements LookAndFeel {

	protected final TouchGUI gui;
	protected final TouchApp app;


	public DefaultLAF(final TouchApp app) {
		this.app = app;
		this.gui = app.getTouchGui();
	}

	@Override
	public DefaultResources getIcons() {
		return DefaultResources.INSTANCE;
	}

	@Override
	public int getPaddingLeftOfDialog() {
		return 0;
	}



	@Override
	public boolean receivesDoubledEvents() {
		return false;
	}

	@Override
	public void setTitle(final String title) {
		// TODO Auto-generated method stub
	}

	@Override
	public void stateChanged(final boolean b) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateUndoSaveButtons() {
		// TODO Auto-generated method stub
	}

	public TouchApp getApp() {
		return this.app;
	}

	@Override
	public void attachExternalEvents(final EuclidianViewT euclidianViewT,
			final Element element) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean useClickHandlerForOpenClose() {
		return false;
	}

	@Override
	public void resetNativeHandlers() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPopupCenter(final PopupPanel panel) {
		panel.center();
	}

	@Override
	public void loadRTLStyles() {
		
	}

	@Override
	public void center(Widget title) {
		// assume it is centered by CSS
		
	}

	@Override
	public String getType() {
		return "touch";
	}

	@Override
	public boolean isRTL() {
		return this.gui.isRTL();
	}

	@Override
	public TouchGUI getGUI() {
		return this.gui;
	}

	@Override
	public int getCanvasHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeaderHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void unselectMaterials() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rememberSelected(MaterialListElementT mat) {
		// TODO Auto-generated method stub
		
	}
}
