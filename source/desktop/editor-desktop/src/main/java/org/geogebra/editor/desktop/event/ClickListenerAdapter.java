/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.editor.desktop.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.geogebra.editor.desktop.MathFieldD;
import org.geogebra.editor.share.event.ClickListener;

public class ClickListenerAdapter extends MouseAdapter {

	private final MathFieldD mathField;
	private final ClickListener clickListener;

	/**
	 * @param field math field component
	 * @param clickListener xross-platform listener
	 */
	public ClickListenerAdapter(MathFieldD field,ClickListener clickListener) {
		this.clickListener = clickListener;
		this.mathField = field;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		clickListener.onPointerDown(getX(e), e.getY());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		clickListener.onPointerMove(getX(e), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		clickListener.onPointerUp(getX(e), e.getY());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
			clickListener.onLongPress(getX(e), e.getY());
		}
	}

	private int getX(MouseEvent e) {
		return e.getX() + mathField.getScrollX();
	}
}
