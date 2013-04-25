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
	
	public static int repaints, repaintTime, drags, dragTime;
	public static int moveEventsIgnored;
	public static long[] updateTimes = new long[20];
	
	/**
	 * @param label
	 * 
	 * see: https://developers.google.com/chrome-developer-tools/docs/console-api#consoleprofilelabel
	 */
	public abstract void profile(String label);
	
	/**
	 * see: https://developers.google.com/chrome-developer-tools/docs/console-api#consoleprofilelabel
	 */
	public abstract void profileEnd();
	
	
	/**
	 * @param label
	 * 
	 * see: https://developers.google.com/chrome-developer-tools/docs/console-api#consoleprofilelabel
	 */
	public abstract void time(String label);
	
	/**
	 * @param label
	 * 
	 * see: https://developers.google.com/chrome-developer-tools/docs/console-api#consoleprofilelabel	 * 
	 */
	public abstract void timeEnd();

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
