package geogebra.common.move.events;

/**
 * @author gabor
 * 
 * 
 *
 */
public abstract class OnLineEvent extends BaseEvent {
	
	/**
	 * @param name
	 * 
	 * Creates a new Online event,
	 * if name is null, it will be like anonymus function
	 */
	public OnLineEvent(String name) {
		if (name != null) {
			this.name = name;
		}
	}
	
	
	

	

}
