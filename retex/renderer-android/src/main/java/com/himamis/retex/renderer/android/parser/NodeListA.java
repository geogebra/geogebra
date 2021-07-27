package com.himamis.retex.renderer.android.parser;

import com.himamis.retex.renderer.share.platform.parser.Node;
import com.himamis.retex.renderer.share.platform.parser.NodeList;

public class NodeListA implements NodeList {
	
	private org.w3c.dom.NodeList impl;
	
	public NodeListA(org.w3c.dom.NodeList impl) {
		this.impl = impl;
	}

	public int getLength() {
		return impl.getLength();
	}

	public Node item(int index) {
		return new NodeA(impl.item(index));
	}

}
