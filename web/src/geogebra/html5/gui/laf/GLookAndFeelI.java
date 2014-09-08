package geogebra.html5.gui.laf;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.euclidian.EuclidianControllerW;
import geogebra.html5.main.AppW;
import geogebra.web.gui.browser.MaterialListElement;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public interface GLookAndFeelI {
	public static final int COMMAND_LINE_HEIGHT = 43;
	public static final int TOOLBAR_HEIGHT = 53;
	boolean isSmart();

	String getType();

	Button getSignInButton(App app);

	boolean undoRedoSupported();

	MainMenuI getMenuBar(AppW app);

	void setCloseMessage(Localization localization);

	boolean copyToClipboardSupported();

	Object getLoginListener();

	boolean registerHandlers(Widget evPanel, EuclidianControllerW euclidiancontroller);

	MaterialListElement getMaterialElement(Material mat, AppW app, boolean isLocal);

}
