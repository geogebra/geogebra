package org.concord.framework.startable;

public class StartableInfo {
	public boolean enabled = true;
	
	/**
	 * If this is false (the default), then buttons for controlling the startable should 
	 * always be enabled since they won't know if the startable changed it outside of the 
	 * buttons.
	 */
	public boolean sendsEvents;

	/**
	 * This will only have an effect if the sendEvents is true
	 */
	public boolean canResetWhileRunning = true;
	
	/**
	 * This will only have an effect if the sendEvents is true
	 */
	public boolean canRestartWithoutReset = true;
	
	public String startVerb;
	public String stopVerb;
	public String resetVerb;
	public String startStopVerb;

	/**
	 * This will be appended after the start, stop verb and a space. 
	 * So if the label is "X" then the action description will be "Start X"
	 */
	public String startStopLabel;

	
	/**
	 * This will be appended after the reset verb and a space. 
	 * So if the label is "X" then the action description will be "Reset X"
	 */
	public String resetLabel;

	public static final String DEFAULT_START_VERB = "Start";

	public static final String DEFAULT_STOP_VERB = "Stop";

	public static final String DEFAULT_RESET_VERB = "Reset";

	public static final String DEFAULT_START_STOP_VERB = "Start/Stop";
}
