package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.serializer.TeXBuilder;
import com.himamis.retex.renderer.share.Box;
import com.himamis.retex.renderer.share.BoxConsumer;
import com.himamis.retex.renderer.share.BoxPosition;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;

public class SelectionBoxConsumer implements BoxConsumer {

	private final TeXBuilder texBuilder;

	private final MathComponent selectionParent;
	private final int selectionStartIndex;
	private final int selectionEndIndex;

	private double selectionBaseline;
	private double selectionX1 = Double.POSITIVE_INFINITY;
	private double selectionX2 = Double.NEGATIVE_INFINITY;
	private double selectionHeight = Double.NEGATIVE_INFINITY;
	private double selectionDepth = Double.NEGATIVE_INFINITY;

	SelectionBoxConsumer(TeXBuilder texBuilder, MathComponent selectionStart,
			MathComponent selectionEnd) {
		this.texBuilder = texBuilder;
		selectionParent = selectionStart == null ? null : selectionStart.getParent();
		selectionStartIndex = selectionStart == null ? 0 : selectionStart.getParentIndex();
		selectionEndIndex = selectionEnd == null ? 0 : selectionEnd.getParentIndex();
	}

	@Override
	public void handle(Box box, BoxPosition position) {
		MathComponent component = texBuilder.getComponent(box.getAtom());

		if (component != null && component == selectionParent) {
			selectionBaseline = position.baseline;
		}

		if (selectionParent == null
				|| (component != null
				&& component.getParent() == selectionParent
				&& selectionStartIndex <= component.getParentIndex()
				&& component.getParentIndex() <= selectionEndIndex)) {
			selectionX1 = Math.min(selectionX1, position.x);
			selectionX2 = Math.max(selectionX2, position.x + box.getWidth());

			selectionHeight = Math.max(position.scale, Math.max(box.getHeight(), selectionHeight));
			selectionDepth = Math.max(box.getDepth(), selectionDepth);
		}
	}

	public Rectangle2D getPosition() {
		return FactoryProvider.getInstance().getGeomFactory().createRectangle2D(
				selectionX1,
				selectionBaseline - selectionHeight,
				selectionX2 - selectionX1,
				selectionHeight + selectionDepth
		);
	}
}
