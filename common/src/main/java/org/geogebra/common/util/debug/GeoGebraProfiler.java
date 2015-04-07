package org.geogebra.common.util.debug;

import org.geogebra.common.main.App;

/**
 * @author gabor
 * 
 *         Uses console.profile where possible. Abstract implementation, because
 *         of Common usages.
 *
 */
public abstract class GeoGebraProfiler {

	private static GeoGebraProfiler instance = null;

	public static int repaints, repaintTime, drags, dragTime;
	public static int moveEventsIgnored;

	private static int algebra, event, hits, cascades;

	private static long algebraTime, eventTime, hitTime, cascadeTime;

	/**
	 */
	public abstract void profile();

	/**
	 * see:
	 * https://developers.google.com/chrome-developer-tools/docs/console-api
	 * #consoleprofileend
	 */
	public abstract void profileEnd();

	/**
	 * @param label
	 * 
	 *            see:
	 *            https://developers.google.com/chrome-developer-tools/docs/
	 *            console-api#consoletimelabel
	 */
	public abstract void time(String label);

	/**
	 * @param label
	 * 
	 *            see:
	 *            https://developers.google.com/chrome-developer-tools/docs/
	 *            console-api#consoletimeend
	 */
	public abstract void timeEnd(String label);

	/**
	 * see
	 * https://developers.google.com/chrome-developer-tools/docs/console-api#
	 * consoletrace
	 */
	public abstract void trace();

	/**
	 * @return GeoGebraProfiler(Web/Desktop) instance
	 */
	public static GeoGebraProfiler getInstance() {
		if (instance == null) {
			instance = new SilentProfiler();
			App.error("trying to profile without profiler");
		}
		return instance;
	}

	/**
	 * @param inst
	 *            GeoGebraProfiler inst from Web or Desktop
	 */
	public static void init(GeoGebraProfiler inst) {
		instance = inst;
	}

	public static void addRepaint(long time) {
		repaints++;
		repaintTime += time;
		if (repaints % 100 == 0) {
			App.debug("Profile Repaint: " + repaints + " x "
					+ (repaintTime / repaints) + " = " + repaintTime);
			int realDrags = drags - moveEventsIgnored;
			if (realDrags > 0) {
				App.debug("Profile Drag: " + realDrags + " x "
						+ (dragTime / realDrags) + " = " + dragTime + ","
						+ moveEventsIgnored + " ignored");
			}
			if (hits > 0) {
				App.debug("Profile Hits: " + hits + " x " + (hitTime / hits)
						+ " = " + hitTime);
			}
			if (cascades > 0) {
				App.debug("Profile Cascades: " + cascades + " x "
						+ (cascadeTime / cascades) + " = " + cascadeTime);
			}
			if (algebra > 0) {
				App.debug("Profile Algebra: " + algebra + " x "
						+ (algebraTime / algebra) + " = " + algebraTime);
			}
			if (event > 0) {
				App.debug("Profile EventDispatcher: " + event + " x "
						+ (eventTime / event) + " = " + eventTime);
			}
		}

	}

	public static void addHit(long l) {
		hitTime += l;
		hits++;

	}

	public static void addUpdateCascade(long l) {
		cascades++;
		cascadeTime += l;

	}

	public static void addAlgebra(long l) {
		algebra++;
		algebraTime += l;
	}

	public static void addEvent(long l) {
		event++;
		eventTime += l;
	}
}
