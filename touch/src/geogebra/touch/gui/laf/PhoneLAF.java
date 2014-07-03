package geogebra.touch.gui.laf;

import geogebra.touch.TouchApp;
import geogebra.touch.gui.PhoneGUI;
import geogebra.touch.gui.elements.ggt.MaterialListElementT;

import com.google.gwt.user.client.Window;

public class PhoneLAF extends DefaultLAF {

	private final int PHONE_HEADER_HEIGHT = 43;

	public PhoneLAF(TouchApp app) {
		super(app);
	}

	@Override
	public int getCanvasHeight() {
		return Window.getClientHeight() - getHeaderHeight();
	}
	
	@Override
	public int getHeaderHeight() {
		return this.PHONE_HEADER_HEIGHT;
	}
	
	@Override
	public void unselectMaterials() {
		((PhoneGUI) this.gui).getBrowseViewPanel().unselectMaterials();
	}
	
	@Override
	public void rememberSelected(MaterialListElementT mat) {
		((PhoneGUI) this.gui).getBrowseViewPanel().rememberSelected(mat);
	}
}
