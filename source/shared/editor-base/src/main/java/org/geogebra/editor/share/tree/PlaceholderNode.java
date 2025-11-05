/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.tree;

import org.geogebra.editor.share.tree.inspect.Inspecting;
import org.geogebra.editor.share.tree.traverse.Traversing;

public class PlaceholderNode extends Node {

	private final String content;

	public PlaceholderNode(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	@Override
	public boolean inspect(Inspecting inspecting) {
		return inspecting.check(this);
	}

	@Override
	public Node traverse(Traversing traversing) {
		return traversing.process(this);
	}
}
