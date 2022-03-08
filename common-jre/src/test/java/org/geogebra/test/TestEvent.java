package org.geogebra.test;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;

public class TestEvent extends AbstractEvent {

	private int x;
	private int y;
	private String command;
	private String[] inputs;
	private PointerEventType type;

	/**
	 * @param x
	 *            screen x-coord
	 * @param y
	 *            screen y-coord
	 */
	public TestEvent(int x, int y) {
		this(x, y, null);
	}

	/**
	 * @param x screen x-coord
	 * @param y screen y-coord
	 * @param type event type
	 */
	public TestEvent(int x, int y, PointerEventType type) {
		this.x = x;
		this.y = y;
		this.type = type;
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
		return false;
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
