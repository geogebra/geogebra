package geogebra.common.util.debug;

import geogebra.common.main.App;

/**
 * @author gabor
 * 
 * Uses console.profile where possible.
 * Abstract implementation, because of Common usages.
 *
 */
public abstract class GeoGebraProfiler {
	

	private static GeoGebraProfiler instance = null;
	
	public static int repaints, repaintTime, drags, dragTime, hitTime, hits, cascades, cascadeTime;
	public static int moveEventsIgnored;

	

	/**
	 */
	public abstract void profile();
	
	/**
	 * see: https://developers.google.com/chrome-developer-tools/docs/console-api#consoleprofileend
	 */
	public abstract void profileEnd();
	
	
	/**
	 * @param label
	 * 
	 * see: https://developers.google.com/chrome-developer-tools/docs/console-api#consoletimelabel
	 */
	public abstract void time(String label);
	
	/**
	 * @param label
	 * 
	 * see: https://developers.google.com/chrome-developer-tools/docs/console-api#consoletimeend
	 */
	public abstract void timeEnd(String label);
	
	/**
	 * see https://developers.google.com/chrome-developer-tools/docs/console-api#consoletrace
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
	 * @param inst GeoGebraProfiler inst from Web or Desktop
	 */
	public static void init(GeoGebraProfiler inst) {
		instance = inst;
	}

	public static void addRepaint(long time) {
		repaints++;
		repaintTime += time;
		if (repaints % 100 == 0)
		{
			App.debug("Repaint: " + repaints + " x " + (repaintTime / repaints) + " = " + repaintTime);
			int realDrags = drags - moveEventsIgnored;
			if(realDrags > 0){
				App.debug("   Drag: " + realDrags + " x " + (dragTime / realDrags) + " = " + dragTime + "," + moveEventsIgnored + " ignored");
			}
			App.debug("Hits: " + hits + " x " + (hits == 0 ? hitTime : (hitTime / hits)) + " = " + hitTime);
			App.debug("Cascades: " + cascades + " x " + (cascades == 0 ? cascadeTime : (cascadeTime / cascades)) + " = " + cascadeTime);
			StringBuilder sb = new StringBuilder("  Views: ");
			
			App.debug(sb.toString());
			
				
			/*repaints = 0;
			drags = 0;
			repaintTime = 0;
			dragTime = 0;
			moveEventsIgnored = 0;
			hitTime = 0;
			hits = 0;*/
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
}
