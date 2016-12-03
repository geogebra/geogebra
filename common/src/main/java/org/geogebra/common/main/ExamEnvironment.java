package org.geogebra.common.main;

import java.util.Date;
import java.util.LinkedList;

import org.geogebra.common.kernel.commands.CmdGetTime;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.util.debug.Log;

//import com.google.gwt.i18n.client.DateTimeFormat;

public class ExamEnvironment {
	long examStartTime = -1;
	protected LinkedList<Long> cheatingTimes = null;

	protected enum CheatingEvent {
		WINDOWS_LEFT, WINDOW_ENTERED,
		AIRPLANE_MODE_OFF, AIRPLANE_MODE_ON,
		WIFI_ENABLED, WIFI_DISABLED,
		TASK_UNLOCKED, TASK_LOCKED,
		BLUETOOTH_ENABLED, BLUETOOTH_DISABLED
	}

	protected LinkedList<CheatingEvent> cheatingEvents = null;
	private long closed = -1;
	private long maybeCheating = -1;
	private boolean lastCheatingEventWindowWasLeft;

	private boolean hasGraph = false;

	public long getStart() {
		return examStartTime;
	}

	public void setStart(long time) {
		examStartTime = time;
		lastCheatingEventWindowWasLeft = false;
	}

	public void startCheating(String os) {
		maybeCheating = System.currentTimeMillis();
		checkCheating(os); // needed for ctr+win+down

	}

	public void checkCheating(String os) {
		
		boolean delay;
		// needed for GGB-1211
		if (os.contains("iOS")) {
			if (maybeCheating < System.currentTimeMillis()-100){
				delay = true;
			} else {
				delay = false;
			}
		} else {
			delay = true;
		}

		// if (maybeCheating > 0 && maybeCheating < System.currentTimeMillis() -
		// 100) {
		if (maybeCheating > 0 && delay) {

			maybeCheating = -1;
			if (getStart() > 0) {
				initLists();
				if (cheatingEvents.size() == 0 || !lastCheatingEventWindowWasLeft) {
					cheatingTimes.add(System.currentTimeMillis());
					cheatingEvents.add(CheatingEvent.WINDOWS_LEFT);
					lastCheatingEventWindowWasLeft = true;
					Log.debug("STARTED CHEATING");
				}

			}
		}
	}

	public void stopCheating() {
		maybeCheating = -1;
		if (cheatingTimes == null || getStart() < 0) {
			return;
		}

		if (cheatingEvents.size() > 0 && lastCheatingEventWindowWasLeft) {
			cheatingTimes.add(System.currentTimeMillis());
			cheatingEvents.add(CheatingEvent.WINDOW_ENTERED);
			lastCheatingEventWindowWasLeft = false;
			Log.debug("STOPPED CHEATING");
		}
	}

	protected void initLists() {
		if (cheatingTimes == null) {
			cheatingTimes = new LinkedList<Long>();
			cheatingEvents = new LinkedList<CheatingEvent>();
		}

	}

	public boolean isCheating() {
		return cheatingTimes != null;
	}



	private String getLocalizedTimeOnly(Localization loc, long time) {
		// eg "14:08:48"
		return CmdGetTime.buildLocalizedDate("\\H:\\i:\\s", new Date(time),
				loc);
	}

	private String getLocalizedDateOnly(Localization loc, long time) {
		// eg "Fri 23 October 2015"
		// don't use \\S for 23rd (not used in eg French)
		return CmdGetTime.buildLocalizedDate("\\D, \\j \\F \\Y",
				new Date(time), loc);
	}



	/**
	 * NEW LOG DIALOG
	 */
	public String getLog(Localization loc, Settings settings) {
		StringBuilder sb = new StringBuilder();

		// Deactivated Views
		boolean supportsCAS = settings.getCasSettings().isEnabled();
		boolean supports3D = settings.getEuclidian(-1).isEnabled();

		if (!hasGraph) {
			if (supportsCAS == false || supports3D == false) {
				sb.append(loc.getMenu("exam_views_deactivated") + ":");
				sb.append(' ');
			}
			if (supportsCAS == false) {
				sb.append(loc.getMenu("Perspective.CAS"));
			}
			if (supportsCAS == false && supports3D == false) {
				sb.append("," + ' ');
			}
			if (supports3D == false) {
				sb.append(loc.getMenu("Perspective.3DGraphics"));
			}
			sb.append("<br>");
		}

		// Exam Start Date
		sb.append(loc.getMenu("exam_start_date") + ":");
		sb.append(' ');
		sb.append(getLocalizedDateOnly(loc, examStartTime));
		sb.append("<br>");

		// Exam Start Time
		sb.append(loc.getMenu("exam_start_time") + ":");
		sb.append(' ');
		sb.append(getLocalizedTimeOnly(loc, examStartTime));
		sb.append("<br>");

		// Exam End Time
		if (closed > 0) {
			sb.append(loc.getMenu("exam_end_time") + ":");
			sb.append(' ');
			sb.append(getLocalizedTimeOnly(loc, closed));
			sb.append("<br>");
		}

		sb.append("<hr>");
		sb.append("<br>");

		// Log times

		sb.append("0:00");
		sb.append(' ');
		sb.append(loc.getMenu("exam_started"));
		sb.append("<br>");

		if (cheatingTimes != null) {
			for (int i = 0; i < cheatingTimes.size(); i++) {
				sb.append(timeToString(cheatingTimes.get(i)));
				sb.append(' ');
				sb.append(getCheatingString(cheatingEvents.get(i), loc));
				sb.append("<br>");
			}
		}
		if (closed > 0) {
			sb.append(timeToString(closed)); // get exit timestamp
			sb.append(' ');
			sb.append(loc.getMenu("exam_ended"));
		}
		return sb.toString();
	}

	static private String getCheatingString(CheatingEvent cheatingEvent, Localization loc) {
		switch (cheatingEvent) {
			case WINDOWS_LEFT: // CHEATING ALERT: exam left
				return loc.getMenu("exam_log_window_left");
			case WINDOW_ENTERED: // exam active again
				return loc.getMenu("exam_log_window_entered");
			case AIRPLANE_MODE_OFF:
				return loc.getMenu("exam_log_airplane_mode_off");
			case AIRPLANE_MODE_ON:
				return loc.getMenu("exam_log_airplane_mode_on");
			case WIFI_DISABLED:
				return loc.getMenu("exam_log_wifi_disabled");
			case WIFI_ENABLED:
				return loc.getMenu("exam_log_wifi_enabled");
			case TASK_LOCKED:
				return loc.getMenu("exam_log_pin");
			case TASK_UNLOCKED:
				return loc.getMenu("exam_log_unpin");
			case BLUETOOTH_ENABLED:
				return loc.getMenu("exam_log_bluetooth_enabled");
			case BLUETOOTH_DISABLED:
				return loc.getMenu("exam_log_bluetooth_disabled");

		}
		return "";
	}

	public boolean getHasGraph() {
		return hasGraph;
	}

	public void setHasGraph(boolean hasGraph) {
		this.hasGraph = hasGraph;
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

	public void exit() {
		this.closed = System.currentTimeMillis();
	}

	public String getSyntax(String cmdInt, Localization loc,
			Settings settings) {
		if (settings.getCasSettings().isEnabled()) {
			return loc.getCommandSyntax(cmdInt);
		}
		Commands cmd = null;
		try {
			cmd = Commands.valueOf(cmdInt);

		} catch (Exception e) {
			// macro or error
		}
		if (cmd == null) {
			return loc.getCommandSyntax(cmdInt);
		}
		// IntegralBetween gives all syntaxes. Typing Integral or NIntegral
		// gives suggestions for NIntegral
		switch (cmd) {
		case Integral:
		case NIntegral:
			return loc.getCommandSyntaxCAS("NIntegral");
		case LocusEquation:
		case Envelope:
		case TrigSimplify:
		case Expand:
		case Factor:
		case IFactor:
		case Simplify:
		case SurdText:
		case ParametricDerivative:
		case Derivative:
		case TrigExpand:
		case TrigCombine:
		case Limit:
		case LimitBelow:
		case LimitAbove:
		case Degree:
		case Coefficients:
		case PartialFractions:
		case SolveODE:
		case ImplicitDerivative:
		case NextPrime:
		case PreviousPrime:
			return null;
		default:
			return loc.getCommandSyntax(cmdInt);
		}

	}

}
