package geogebra.common.plugin;

public class ScriptError extends Exception {
	private Event event;
	private String message;
	
	public ScriptError(String message) {
		super();
		this.message = message;
	}
	
	
}
