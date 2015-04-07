package edu.uci.ics.jung.algorithms.layout.util;

/**
 * Interface for operating the relax iterations on a layout.
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 */
public interface Relaxer {
	
	/**
	 * Execute a loop of steps in a new Thread,
	 * firing an event after each step.
	 */
	void relax();
	
	/**
	 * Execute a loop of steps in the calling
	 * thread, firing no events.
	 */
	void prerelax();
	
	/**
	 * Make the relaxer thread wait.
	 */
	void pause();
	
	/**
	 * Make the relaxer thread resume.
	 *
	 */
	void resume();
	
	/**
	 * Set flags to stop the relaxer thread.
	 */
	void stop();

	/**
	 * Sets the sleep time.
	 */
	void setSleepTime(long i);
}
