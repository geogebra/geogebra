/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
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
