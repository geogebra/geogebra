package geogebra.html5.gui.inputfield;

import geogebra.html5.awt.GFontW;

import java.text.ParseException;

import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RichTextArea;

/**
 * Extension of RichTextArea with additional JavaScript functionality and a
 * value change handler to listen for editing changes.
 * 
 * @author G. Sturr
 * 
 */
public class RichTextAreaEditor extends RichTextArea implements
        HasValueChangeHandlers<String>, HasValue<String>, TakesValue<String>,
        LeafValueEditor<String> {

	private boolean valueChangeHandlerInitialized;

	GFontW font;

	public RichTextAreaEditor(GFontW font) {

		this.font = font;

		// style and javascript functions must be set after the editor has been
		// initialized
		addInitializeHandler(new InitializeHandler() {
			public void onInitialize(InitializeEvent event) {
				setScript();
				setStyle();
			}
		});
	}

	protected void setScript() {

		Document document = IFrameElement.as(getElement()).getContentDocument();
		ScriptElement script = document.createScriptElement();
		script.setInnerText(getEditFunction());
		script.setType("text/javascript");
		script.setLang("javascript");
		Element head = document.getDocumentElement()
		        .getElementsByTagName("head").getItem(0);
		head.appendChild(script);
	}

	private String getEditFunction() {
		String s = "";

		// s = "function edit() { prompt(\"red\", \"blue\") }";

		s = "function edit(elem) { ";
		s += "var newValue = prompt(\"Edit\", elem.value); ";
		s += "if(newValue != null) elem.value = newValue ;";
		s += " } ";

		return s;
	}

	protected void setStyle() {

		String fontSize = font.getFontSize();
		String fontFamily = font.getFontFamily();

		Document document = IFrameElement.as(getElement()).getContentDocument();
		BodyElement body = document.getBody();
		body.setAttribute("style", "font-family:" + fontFamily + "; font-size:"
		        + fontSize + "pt");
		body.setAttribute("spellcheck", "false");
	}

	public BodyElement getBody() {
		Document document = IFrameElement.as(getElement()).getContentDocument();
		BodyElement body = document.getBody();
		return body;
	}

	public String getValue() {
		return getHTML();
	}

	public void setValue(String value) {
		if (value == null) {
			return;
		}
		SafeHtml html = SimpleHtmlSanitizer.sanitizeHtml(value);
		setHTML(html);
	}

	public void setValue(String value, boolean fireEvents) {
		SafeHtml html = SimpleHtmlSanitizer.sanitizeHtml(value);
		setHTML(html);
		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, getHTML(), value);
		}
	}

	public HandlerRegistration addValueChangeHandler(
	        final ValueChangeHandler<String> handler) {
		// Initialization code
		if (!valueChangeHandlerInitialized) {
			valueChangeHandlerInitialized = true;
			addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					ValueChangeEvent.fire(RichTextAreaEditor.this, getValue());
				}
			});
		}
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}

	public String getValueOrThrow() throws ParseException {
		String text = getHTML();
		if ("".equals(text)) {
			return null;
		}
		return text;
	}

	// workaround for ff bug that prevents disabling RichTextEditor
	@Override
	public void onBrowserEvent(final Event event) {
		if (isEnabled()) {
			super.onBrowserEvent(event);
		}
	}

	public native void insertText(String text, int pos) /*-{
		var elem = this.@com.google.gwt.user.client.ui.UIObject::getElement()();
		var refNode = elem.contentWindow.getSelection().getRangeAt(0).endContainer;
		refNode.insertData(pos, text);
	}-*/;

}
