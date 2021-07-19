package com.himamis.retex.renderer.android.parser;

import com.himamis.retex.renderer.share.platform.parser.Document;
import com.himamis.retex.renderer.share.platform.parser.Element;

public class DocumentA implements Document {
	
	private org.w3c.dom.Document impl;
	
	public DocumentA(org.w3c.dom.Document impl) {
		this.impl = impl;
	}

	public Element getDocumentElement() {
		return new ElementA(impl.getDocumentElement());
	}

}
