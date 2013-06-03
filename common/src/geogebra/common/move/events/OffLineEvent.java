package geogebra.common.move.events;

public abstract class OffLineEvent extends BaseEvent {

	/**
	 * @param name
	 * 
	 * Creates a new Offline event,
	 * if name is null, it will be like anonymus function
	 */
	public OffLineEvent(String name) {
		if (name != null) {
			this.name = name;
		}
	}
	

}
