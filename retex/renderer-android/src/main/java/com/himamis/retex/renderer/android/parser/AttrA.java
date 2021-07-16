package com.himamis.retex.renderer.android.parser;

import org.w3c.dom.Node;

import com.himamis.retex.renderer.share.platform.parser.Attr;

public class AttrA extends NodeA implements Attr {
	
	public org.w3c.dom.Attr impl;
	
	public AttrA(org.w3c.dom.Attr impl) {
		super((Node) impl);
		this.impl = impl;
	}

	public String getName() {
		return impl.getName();
	}

	public boolean isSpecified() {
		return impl.getSpecified();
	}

	public String getValue() {
		return impl.getValue();
	}

}
