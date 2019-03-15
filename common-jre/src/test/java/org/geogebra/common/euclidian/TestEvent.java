package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;

public class TestEvent extends AbstractEvent {

	private int x;
	private int y;
	public String command;
	public String[] inputs;

	public TestEvent(int i, int j) {
		x = i;
		y = j;
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
		// TODO Auto-generated method stub
		return y;
	}

	@Override
	public boolean isRightClick() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isControlDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getClickCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isMetaDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMiddleClick() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPopupTrigger() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PointerEventType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public TestEvent withInput(String[] string) {
		this.inputs = string;
		return this;
	}

}
