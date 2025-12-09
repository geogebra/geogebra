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

package org.geogebra.test;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;

public class TestEvent extends AbstractEvent {

	private final int x;
	private final int y;
	private String command;
	private String[] inputs;
	private PointerEventType type;
	private boolean rightClick;

	/**
	 * @param x
	 *            screen x-coord
	 * @param y
	 *            screen y-coord
	 */
	public TestEvent(int x, int y) {
		this(x, y, null, false);
	}

	/**
	 * @param x screen x-coord
	 * @param y screen y-coord
	 * @param type event type
	 */
	public TestEvent(int x, int y, PointerEventType type, boolean rightClick) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.rightClick = rightClick;
	}

	@Override
	public GPoint getPoint() {
		return new GPoint(getX(), getY());
	}

	@Override
	public boolean isAltDown() {
		return false;
	}

	@Override
	public boolean isShiftDown() {
		return false;
	}

	@Override
	public void release() {
		// OK
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public boolean isRightClick() {
		return rightClick;
	}

	@Override
	public boolean isControlDown() {
		return false;
	}

	@Override
	public int getClickCount() {
		return 0;
	}

	@Override
	public boolean isMetaDown() {
		return false;
	}

	@Override
	public boolean isMiddleClick() {
		return false;
	}

	@Override
	public boolean isPopupTrigger() {
		return false;
	}

	@Override
	public PointerEventType getType() {
		return type;
	}

	/**
	 * Builder method for user input events
	 * 
	 * @param inputTexts
	 *            emulated user inputs
	 * @return this
	 */
	public TestEvent withInput(String[] inputTexts) {
		inputs = inputTexts;
		return this;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String[] getInputs() {
		return inputs == null ? null : inputs.clone();
	}

}
