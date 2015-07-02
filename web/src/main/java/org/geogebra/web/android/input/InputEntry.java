package org.geogebra.web.android.input;

import org.geogebra.common.util.StringUtil;
import org.geogebra.web.android.AppStub;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.StyleInjector;
import org.geogebra.web.html5.util.ScriptLoadCallback;
import org.geogebra.web.web.util.keyboardBase.TextFieldProcessing;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.RootPanel;

public class InputEntry implements EntryPoint, ScriptLoadCallback,
		OnEnterPressedListener {

	private AppStub app;
	private MathQuillInput mathQuillInput;

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

	public void enterPressed() {
		String input = mathQuillInput.getText();
		callEnterPressedCallback(input);
	}
	
	private native void callEnterPressedCallback(String text) /*-{
		$wnd.androidInput.onEnter(text);
	}-*/;

	private void createUserInterface() {
		String inputText = Location.getParameter("text");
		inputText = inputText == null ? "" : inputText;
		mathQuillInput = new MathQuillInput(inputText);
		exportProcessing(mathQuillInput.getProcessing());
		RootPanel.get().add(mathQuillInput);
		mathQuillInput.setFocus(true);
		mathQuillInput.setOnEnterPressedListener(this);

	}

	private void exportProcessing(TextFieldProcessing processing) {
		exportProcessingNative(processing);
	}

	private native void exportProcessingNative(TextFieldProcessing processing) /*-{
		$wnd.jsInput = {};
		$wnd.jsInput.onEnter = $entry(function() {
			processing.@org.geogebra.web.web.util.keyboardBase.TextFieldProcessing::onEnter()();
		});
		$wnd.jsInput.onBackspace = $entry(function() {
			processing.@org.geogebra.web.web.util.keyboardBase.TextFieldProcessing::onBackSpace()();
		});
		$wnd.jsInput.onArrow = $entry(function(arrowType) {
			processing.@org.geogebra.web.web.util.keyboardBase.TextFieldProcessing::onArrow(Lorg/geogebra/web/web/util/keyboardBase/TextFieldProcessing$ArrowType;)(@org.geogebra.web.web.util.keyboardBase.TextFieldProcessing.ArrowType::values()[arrowType]);
		});
		$wnd.jsInput.insertString = $entry(function(text) {
			processing.@org.geogebra.web.web.util.keyboardBase.TextFieldProcessing::insertString(Ljava/lang/String;)(text);
		});
		
		$wnd.jsInput.scrollCursorIntoView = $entry(function() {
			processing.@org.geogebra.web.web.util.keyboardBase.TextFieldProcessing::scrollCursorIntoView()();
		});
	}-*/;

}
