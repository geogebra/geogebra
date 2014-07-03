package geogebra.touch.gui.laf;

import geogebra.common.main.SavedStateListener;
import geogebra.touch.gui.TouchGUI;
import geogebra.touch.gui.elements.ggt.MaterialListElementT;
import geogebra.touch.gui.euclidian.EuclidianViewT;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public interface LookAndFeel extends SavedStateListener {
	
	public DefaultResources getIcons();

	public int getPaddingLeftOfDialog();

	public boolean receivesDoubledEvents();

	public void setTitle(String title);

	public void updateUndoSaveButtons();

	public void attachExternalEvents(EuclidianViewT euclidianViewT,
			Element element);

	public boolean useClickHandlerForOpenClose();

	public void resetNativeHandlers();

	public void setPopupCenter(PopupPanel panel);
	
	public boolean isRTL();

	public void loadRTLStyles();

	public void center(Widget title);

	public TouchGUI getGUI();
	
	public void unselectMaterials();
	
	public void rememberSelected(MaterialListElementT mat);
	
	public String getType();
	
	public int getCanvasHeight();
	
	public int getHeaderHeight();
	
}
