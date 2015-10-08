package org.geogebra.web.html5.main;

import java.util.Date;
import java.util.LinkedList;

import org.geogebra.common.main.App;

import com.google.gwt.i18n.client.DateTimeFormat;

public class ExamEnvironment {
	private boolean supports3D, supportsCAS;
	long examStartTime = -1;
	private LinkedList<Long> cheatingTimes = null;
	private LinkedList<Boolean> cheatingEvents = null;;

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
			cheatingEvents.add(true);
			App.debug("STARTED CHEATING");
		}

	}

	public void stopCheating() {
		if (cheatingTimes == null || getStart() < 0) {
			return;
		}

		if (cheatingEvents.size() > 0
				&& cheatingEvents.get(cheatingEvents.size() - 1).booleanValue()) {
			cheatingTimes.add(System.currentTimeMillis());
			cheatingEvents.add(false);
			App.debug("STOPPED CHEATING");
		}
	}

	private void initLists() {
		if (cheatingTimes == null) {
			cheatingTimes = new LinkedList<Long>();
			cheatingEvents = new LinkedList<Boolean>();
		}

	}
	public boolean isCheating() {
		return cheatingTimes != null;
	}

	public String getLog() {
		StringBuilder sb = new StringBuilder();
		sb.append("Exam started");
		sb.append(' ');
		sb.append(DateTimeFormat.getFormat("dd. MM. yyyy, HH:mm:ss").format(
				new Date(examStartTime)));
		sb.append("\n");
		if(cheatingTimes != null){
			for(int i = 0; i < cheatingTimes.size(); i++){
				sb.append(timeToString(cheatingTimes.get(i)));
				sb.append(' ');
				sb.append(cheatingEvents.get(i) ? "CHEATING ALERT: application left"
						: "application entered again");
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public String timeToString(long timestamp) {
		if (examStartTime < 0) {
			return "0:00";
		}
		int secs = (int) ((timestamp - examStartTime) / 1000);
		int mins = secs / 60;
		secs -= mins * 60;
		String secsS = secs + "";
		if (secs < 10) {
			secsS = "0" + secsS;
		}
		return mins + ":" + secsS;
	}
}
