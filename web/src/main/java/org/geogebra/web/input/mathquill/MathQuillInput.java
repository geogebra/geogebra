package org.geogebra.web.input.mathquill;

import org.geogebra.web.web.util.keyboardBase.TextFieldProcessable;
import org.geogebra.web.web.util.keyboardBase.TextFieldProcessing;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class MathQuillInput extends Composite implements TextFieldProcessable {

	private String initText;
	private JavaScriptObject mathQuillElement;
	private FlowPanel flowPanel;
	private Element flowPanelElement;

	public MathQuillInput() {
		this("");
	}

	public MathQuillInput(String initText) {
		this.initText = initText;

		flowPanel = new FlowPanel();
		flowPanelElement = flowPanel.getElement();

		initWidget(flowPanel);
	}

	public TextFieldProcessing getProcessing() {
		return new MathQuillInputProcessor(this);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		nativeCreateElement();
	}

	private native void nativeCreateElement() /*-{
		var that = this;
		var panel = that.@org.geogebra.web.input.mathquill.MathQuillInput::flowPanelElement;
		var string = that.@org.geogebra.web.input.mathquill.MathQuillInput::initText;
		var mqElement = $wnd.$ggbQuery('<span>' + string + '</span>');
		mqElement.appendTo(panel).mathquillggb('editable');
		that.@org.geogebra.web.input.mathquill.MathQuillInput::setMathQuillElement(Lcom/google/gwt/core/client/JavaScriptObject;)(mqElement);
	}-*/;

	private void setMathQuillElement(JavaScriptObject mathQuillElement) {
		this.mathQuillElement = mathQuillElement;
	}

	public native void triggerKeyEvent(String eventType, int keyOrCharCode,
			boolean altk, boolean ctrlk, boolean shiftk) /*-{
		var that = this;
		var mqElement = that.@org.geogebra.web.input.mathquill.MathQuillInput::mathQuillElement;
		var textarea = mqElement.find('textarea');
		var evt = $wnd.$ggbQuery.Event(eventType, {
			keyCode : keyOrCharCode,
			which : keyOrCharCode,
			altKey : altk,
			ctrlKey : ctrlk,
			shiftKey : shiftk
		});
		textarea.trigger(evt);
	}-*/;

	public native void triggerCharEvent(String eventType, int keyOrCharCode,
			boolean altk, boolean ctrlk, boolean shiftk) /*-{
		var that = this;
		var mqElement = that.@org.geogebra.web.input.mathquill.MathQuillInput::mathQuillElement;
		var textarea = mqElement.find('textarea');
		textarea.val(String.fromCharCode(keyOrCharCode));
		var evt = $wnd.$ggbQuery.Event(eventType, {
			charCode : keyOrCharCode,
			which : keyOrCharCode,
			altKey : altk,
			ctrlKey : ctrlk,
			shiftKey : shiftk
		});
		textarea.trigger(evt);
	}-*/;

	public void keypress(int keyOrCharCode, boolean altk, boolean ctrlk,
			boolean shiftk) {
		triggerCharEvent("keypress", keyOrCharCode, altk, ctrlk, shiftk);
	}

	public void keydown(int keyOrCharCode, boolean altk, boolean ctrlk,
			boolean shiftk) {
		triggerKeyEvent("keydown", keyOrCharCode, altk, ctrlk, shiftk);
	}

	public void keyup(int keyOrCharCode, boolean altk, boolean ctrlk,
			boolean shiftk) {
		triggerKeyEvent("keyup", keyOrCharCode, altk, ctrlk, shiftk);
	}

	public String getText() {
		return getTextNative();
	}
	
	private native String getTextNative() /*-{
		var that = this;
		var mqElement = that.@org.geogebra.web.input.mathquill.MathQuillInput::mathQuillElement;
		return mqElement.mathquillggb('text');
	}-*/;

	public void insertString(String string) {
		insertStringNative(string);
	}
	
	private native void insertStringNative(String string) /*-{
		var that = this;
		var mqElement = that.@org.geogebra.web.input.mathquill.MathQuillInput::mathQuillElement;
		mqElement.mathquillggb('replace', string, '', false)
	}-*/;
	
	public void setFocus(boolean focus) {

	}
}
