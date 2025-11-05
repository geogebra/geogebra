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

import com.himamis.retex.renderer.share.Box;
import com.himamis.retex.renderer.share.BoxConsumer;
import com.himamis.retex.renderer.share.BoxPosition;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;

public class SelectionBoxConsumer implements BoxConsumer {

	private final TeXBuilder texBuilder;

	private final Node selectionParent;
	private final int selectionStartIndex;
	private final int selectionEndIndex;
	private final Node input;

	private Double selectionBaseline;
	private double selectionX1 = Double.POSITIVE_INFINITY;
	private double selectionX2 = Double.NEGATIVE_INFINITY;
	private double selectionHeight = Double.NEGATIVE_INFINITY;
	private double selectionDepth = Double.NEGATIVE_INFINITY;

	SelectionBoxConsumer(TeXBuilder texBuilder, Node selectionStart,
			Node selectionEnd, Node input) {
		this.texBuilder = texBuilder;
		this.input = input;
		selectionParent = selectionStart == null ? null : selectionStart.getParent();
		selectionStartIndex = selectionStart == null ? 0 : selectionStart.getParentIndex();
		selectionEndIndex = selectionEnd == null ? 0 : selectionEnd.getParentIndex();
	}

	@Override
	public void handle(Box box, BoxPosition position) {
		Node node = texBuilder.getNode(box.getAtom());
		CursorBoxConsumer.highlightInput(box, node, input);
		if (selectionParent == null
				|| isBetween(node) || node != null && isBetween(node.getParent())) {
			if (selectionBaseline == null) {
				selectionBaseline = position.baseline;
			}

			selectionX1 = Math.min(selectionX1, position.x);
			selectionX2 = Math.max(selectionX2, position.x + box.getWidth());

			selectionHeight = Math.max(position.scale, Math.max(box.getHeight(), selectionHeight));
			selectionDepth = Math.max(box.getDepth(), selectionDepth);
		}
	}

	private boolean isBetween(Node node) {
		return node != null
				&& node.getParent() == selectionParent
				&& selectionStartIndex <= node.getParentIndex()
				&& node.getParentIndex() <= selectionEndIndex;
	}

	/**
	 * @return selection rectangle
	 */
	public Rectangle2D getPosition() {
		return FactoryProvider.getInstance().getGeomFactory().createRectangle2D(
				selectionX1,
				(selectionBaseline == null ? 0 : selectionBaseline) - selectionHeight,
				selectionX2 - selectionX1,
				(selectionHeight + selectionDepth) * 1.2
		);
	}
}
