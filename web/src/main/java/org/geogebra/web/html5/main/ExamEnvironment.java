package org.geogebra.web.html5.main;

import java.util.LinkedList;

public class ExamEnvironment {
	private boolean supports3D, supportsCAS;
	long examStartTime = -1;
	private LinkedList<Long> cheatingTimes = null;
	private LinkedList<Boolean> cheatingEvent = null;;

	public long getStart() {
		return examStartTime;
	}
	public boolean is3DAllowed() {
		return supports3D;
	}

	public void set3DAllowed(boolean supports3d) {
		supports3D = supports3d;
	}

	public boolean isCASAllowed() {
		return supportsCAS;
	}

	public void setCASAllowed(boolean supportsCAS) {
		this.supportsCAS = supportsCAS;
	}

	public void setStart(long time) {
		examStartTime = time;

	}

	public void startCheating() {
		if (getStart() > 0) {
			initLists();
			cheatingTimes.add(System.currentTimeMillis());
		}

	}

	private void initLists() {
		if (cheatingTimes == null) {
			cheatingTimes = new LinkedList<Long>();
			cheatingEvent = new LinkedList<Boolean>();
		}

	}
	public boolean isCheating() {
		return cheatingTimes != null;
	}
}
