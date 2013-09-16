package geogebra.common.move.views;

import geogebra.common.move.events.BaseEvent;

/**
 * @author gabor
 * 
 * renderable class for success - error operations
 *
 */
public interface SuccessErrorRenderable {
	
	/**
	 * @param response from GGT
	 */
	public void success(BaseEvent event);
	
	/**
	 * @param resonse from GGT
	 */
	public void fail(BaseEvent event);

}
