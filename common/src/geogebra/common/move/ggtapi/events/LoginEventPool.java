package geogebra.common.move.ggtapi.events;

import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.events.BaseEventPool;
import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.operations.LoginOperation;
import geogebra.common.move.operations.BaseOperation;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author gabor
 *      Login event pool for storing login events
 *
 */
public class LoginEventPool extends BaseEventPool {

	/**
	 * @param op
	 * Creates a new loginEventPool for login Events
	 */
	public LoginEventPool(BaseOperation op) {
		super(op);
	}
	
	/**
	 * @param event LoginEvent
	 * Registers a new Login event
	 */
	public void onLogin(LoginEvent event) {
		if (eventList == null) {
			eventList = new ArrayList<BaseEvent>();
		}
		eventList.add(event);
	}
	
	/**
	 * @param name String
	 * removes an event with a given name,
	 * or the whole event array, if String is null
	 */
	public void offLogin(String name) {
		if (eventList != null) {
			if (name == null) {
				eventList = null;
			} else {
				Iterator<BaseEvent> it = eventList.iterator();
				while (it.hasNext()) {
					if (it.next().getName() == name) {
						eventList.remove(it.next());
					}
				}
			}
		}
	}
	
	/**
	 * @param response JSONObject
	 * 
	 * The response got back from GGT
	 * 
	 */
	public void trigger(JSONObject response) {
		if (response.get("error") == null) {
			((LoginOperation) operation).loginSuccess(response);
		} else {
			((LoginOperation) operation).loginError(response);
		}
	}
	

}
