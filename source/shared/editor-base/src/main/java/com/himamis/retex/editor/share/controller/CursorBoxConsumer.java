package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.TeXBuilder;
import com.himamis.retex.renderer.share.Box;
import com.himamis.retex.renderer.share.BoxConsumer;
import com.himamis.retex.renderer.share.BoxPosition;
import com.himamis.retex.renderer.share.OvalBox;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;

public class CursorBoxConsumer implements BoxConsumer {

	private final TeXBuilder texBuilder;

	private final MathComponent argument;

	private final boolean beforeFirst;
	private final MathComponent input;
	private BoxPosition cursorPosition;
	private double boxWidth;

	CursorBoxConsumer(TeXBuilder texBuilder, MathSequence currentField, int currentOffset,
			MathComponent input) {
		this.texBuilder = texBuilder;
		this.input = input;
		beforeFirst = currentOffset == 0;
		MathComponent argumentTmp = currentField != null
				? currentField.getArgument(currentOffset == 0 ? 0 : currentOffset - 1)
				: null;
		argument = argumentTmp == null ? TeXBuilder.SELECTION : argumentTmp;
	}

	@Override
	public void handle(Box box, BoxPosition position) {
		MathComponent component = texBuilder.getComponent(box.getAtom());
		highlightInput(box, component, input);
		if (component == argument) {
			cursorPosition = position;
			boxWidth = beforeFirst ? 0 : box.getWidth();
		}
	}

	protected static void highlightInput(Box box, MathComponent component, MathComponent input) {
		if (component == input && input != null) {
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
