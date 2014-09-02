package geogebra.html5.gui.laf;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.html5.euclidian.EuclidianControllerW;
import geogebra.html5.main.AppW;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public interface GLookAndFeelI {

	boolean isSmart();

	String getType();

	Button getSignInButton(App app);

	boolean undoRedoSupported();

	MainMenuI getMenuBar(AppW app);

	void setCloseMessage(Localization localization);

	boolean copyToClipboardSupported();

	Object getLoginListener();

	boolean registerHandlers(Widget evPanel, EuclidianControllerW euclidiancontroller);

}
