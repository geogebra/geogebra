package org.geogebra.web.android.keyboard;

import org.geogebra.common.util.StringUtil;
import org.geogebra.web.android.AppStub;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.StyleInjector;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.util.ScriptLoadCallback;
import org.geogebra.web.web.util.keyboardBase.OnScreenKeyBoardBase;
import org.geogebra.web.web.util.keyboardBase.TextFieldProcessing;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.RootPanel;

public class KeyboardEntry implements EntryPoint, ScriptLoadCallback {

	private AppStub app;

	public void onModuleLoad() {
		injectKeyboardStyles();
		createFactories();

		app = new AppStub();

		String language = Location.getParameter("language");
		language = language == null || "".equals(language) ? "en" : language;
		app.setLanguage(language, this);
	}

	private void injectKeyboardStyles() {
		StyleInjector.inject(GuiResourcesSimple.INSTANCE.modernStyle()
				.getText());
	}

	private void createFactories() {
		StringUtil.prototype = new StringUtil();
	}

	public void onLoad() {
		MathKeyboardListener textField = new MathKeyboardListenerStub();
		UpdateKeyboardListenerStub listener = new UpdateKeyboardListenerStub();
		TextFieldProcessing processing = new KeyboardListener();
		OnScreenKeyBoardBase oskb = OnScreenKeyBoardBase.getInstance(textField,
				listener, app);
		oskb.setProcessing(processing);
		oskb.show();

		RootPanel.get().add(oskb);
	}
}
