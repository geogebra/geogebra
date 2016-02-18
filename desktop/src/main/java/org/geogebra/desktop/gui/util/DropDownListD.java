package org.geogebra.desktop.gui.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.geogebra.common.gui.util.DropDownList;

public class DropDownListD extends DropDownList implements ActionListener

{
	private Timer timClick;
	private Timer timScroll;

	public DropDownListD(DropDownListener listener) {
		super(listener);
		timClick = new Timer(clickDelay, this);
		timScroll = new Timer(scrollDelay, this);
	}

	@Override
	protected void runScrollTimer() {
		timScroll.start();
	}

	public void stopScrollTimer() {
		timScroll.stop();
	}


	public void setTimerDelay(int timerDelay) {
		timScroll.setDelay(timerDelay);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timClick) {
			doRunClick();
		} else {
			doScroll();
		}
	}

	@Override
	protected void runClickTimer() {
		timClick.start();
	}

	public void stopClickTimer() {
		timClick.stop();
	}

	public boolean isClickTimerRunning() {
		return timClick.isRunning();
	}

	public boolean isScrollTimerRunning() {
		return timScroll.isRunning();
	}
}
