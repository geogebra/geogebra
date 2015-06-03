package org.geogebra.web.input;

import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.StyleInjector;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.util.ScriptLoadCallback;
import org.geogebra.web.input.mathquill.MathQuillInput;
import org.geogebra.web.web.util.keyboardBase.OnScreenKeyBoardBase;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class AndroidKeyboard implements EntryPoint, ScriptLoadCallback {

	private AppStub app;

	public void onModuleLoad() {
		injectResources();
		createFactories();

		app = new AppStub();

		String language = Location.getParameter("language");
		language = language == null || "".equals(language) ? "en" : language;
		app.setLanguage(language, this);
	}

	private void injectResources() {
		StyleInjector.inject(GuiResourcesSimple.INSTANCE.modernStyle()
				.getText());
		StyleInjector.inject(GuiResourcesSimple.INSTANCE.mathquillggbCss()
				.getText());
		ScriptInjector
				.fromString(GuiResourcesSimple.INSTANCE.jQueryJs().getText())
				.setRemoveTag(false).setWindow(ScriptInjector.TOP_WINDOW)
				.inject();
		ScriptInjector
				.fromString(
						GuiResourcesSimple.INSTANCE.mathquillggbJs().getText())
				.setRemoveTag(false).setWindow(ScriptInjector.TOP_WINDOW)
				.inject();
	}

	private void createFactories() {
		StringUtil.prototype = new StringUtil();
	}

	/**
	 * Language script load callback
	 */
	public void onLoad() {
		createUserInterface();
	}

	private void createUserInterface() {
		DockLayoutPanel layoutPanel = new DockLayoutPanel(Unit.PX);

		MathKeyboardListener textField = new MathKeyboardListenerStub();
		UpdateKeyboardListenerStub listener = new UpdateKeyboardListenerStub();
		OnScreenKeyBoardBase oskb = OnScreenKeyBoardBase.getInstance(textField,
				listener, app);
		oskb.show();

		MathQuillInput input = new MathQuillInput(Location.getParameter("text"));
		oskb.setProcessing(input.getProcessing());

		int oskbOffsetHeight = oskb.getOffsetHeight();
		int mathQuillInputheight = Window.getClientHeight() - oskbOffsetHeight;

		layoutPanel.addSouth(oskb, oskbOffsetHeight);
		layoutPanel.addNorth(input, mathQuillInputheight);

		RootLayoutPanel.get().add(layoutPanel);
	}

}
