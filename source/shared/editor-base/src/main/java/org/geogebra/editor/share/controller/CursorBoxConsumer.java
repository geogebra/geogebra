/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.controller;

import org.geogebra.editor.share.serializer.TeXBuilder;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.SequenceNode;

import com.himamis.retex.renderer.share.Box;
import com.himamis.retex.renderer.share.BoxConsumer;
import com.himamis.retex.renderer.share.BoxPosition;
import com.himamis.retex.renderer.share.OvalBox;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;

public class CursorBoxConsumer implements BoxConsumer {

	private final TeXBuilder texBuilder;

	private final Node argument;

	private final boolean beforeFirst;
	private final Node input;
	private BoxPosition cursorPosition;
	private double boxWidth;

	CursorBoxConsumer(TeXBuilder texBuilder, SequenceNode sequenceNode, int currentOffset,
			Node input) {
		this.texBuilder = texBuilder;
		this.input = input;
		beforeFirst = currentOffset == 0;
		Node argumentTmp = sequenceNode != null
				? sequenceNode.getChild(currentOffset == 0 ? 0 : currentOffset - 1)
				: null;
		argument = argumentTmp == null ? TeXBuilder.SELECTION : argumentTmp;
	}

	@Override
	public void handle(Box box, BoxPosition position) {
		Node node = texBuilder.getNode(box.getAtom());
		highlightInput(box, node, input);
		if (node == argument) {
			cursorPosition = position;
			boxWidth = beforeFirst ? 0 : box.getWidth();
		}
	}

	protected static void highlightInput(Box box, Node node, Node input) {
		if (node == input && input != null) {
			if (box instanceof OvalBox) {
				((OvalBox) box).setColor(FactoryProvider.getInstance().getGraphicsFactory()
						.createColor(TeXBuilder.INPUT_BORDER));
			}
		}
	}

	/**
	 * @return cursor rectangle
	 */
	public Rectangle2D getPosition() {
		if (cursorPosition == null) {
			return null;
		}

		return FactoryProvider.getInstance().getGeomFactory().createRectangle2D(
				cursorPosition.x + boxWidth,
				cursorPosition.baseline - cursorPosition.scale * 0.8,
				1,
				cursorPosition.scale
		);
	}
}
