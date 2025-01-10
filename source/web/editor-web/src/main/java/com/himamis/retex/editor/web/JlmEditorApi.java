package com.himamis.retex.editor.web;

import org.gwtproject.dom.client.Element;

import com.himamis.retex.renderer.web.JlmApi;

import jsinterop.annotations.JsType;

@JsType
public class JlmEditorApi extends JlmApi {

	@SuppressWarnings("unusable-by-js")
	public JlmEditorApi(JlmEditorLib library) {
		super(library);
	}

	public void edit(Element element) {
		((JlmEditorLib) this.library).edit(element);
	}

}
