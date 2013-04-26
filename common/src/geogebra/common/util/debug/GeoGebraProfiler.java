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
	
	public static int repaints, repaintTime, drags, dragTime;
	public static int moveEventsIgnored;
	public static long[] updateTimes = new long[20];
	

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
			throw new Error("Profiler instance must be created first from Web or Desktop");
		}
		return instance;
	}
	
	/**
	 * @param inst GeoGebraProfiler inst from Web or Desktop
	 */
	public static void init(GeoGebraProfiler inst) {
		instance = inst;
	}

	public static void addRepaint(long l) {
		repaints++;
		repaintTime += (System.currentTimeMillis() - l);
		if (repaints % 100 == 0)
		{
			App.debug("Repaint: " + repaints + " x " + (repaintTime / repaints) + " = " + repaintTime);
			int realDrags = drags - moveEventsIgnored;
			if(realDrags > 0){
				App.debug("   Drag: " + realDrags + " x " + (dragTime / realDrags) + " = " + dragTime + "," + moveEventsIgnored + " ignored");
			}
			StringBuilder sb = new StringBuilder("  Views: ");
			for(int i=0; i<5; i++){
				sb.append(updateTimes[i]);
				sb.append(',');
				updateTimes[i]=0;
			}
			App.debug(sb.toString());
			
				
			repaints = 0;
			drags = 0;
			repaintTime = 0;
			dragTime = 0;
			moveEventsIgnored = 0;
		}
		
	}
}
