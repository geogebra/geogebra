/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * 
 */
package edu.uci.ics.jung.algorithms.layout.util;

import edu.uci.ics.jung.algorithms.util.IterativeContext;

/**
 * 
 * Implementation of a relaxer thread for layouts.
 * Extracted from the {@code VisualizationModel} in previous
 * versions of JUNG.
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 */
public class VisRunner implements Relaxer, Runnable {
	
	protected boolean running;
	protected IterativeContext process;
	protected boolean stop;
	protected boolean manualSuspend;
	protected Thread thread;
	
	/**
	 * how long the relaxer thread pauses between iteration loops.
	 */
	protected long sleepTime = 100L;

	
	/**
	 * Creates an instance for the specified process.
	 */
	public VisRunner(IterativeContext process) {
		this.process = process;
	}

	/**
	 * @return the relaxerThreadSleepTime
	 */
	public long getSleepTime() {
		return sleepTime;
	}

	/**
	 * @param sleepTime the sleep time to set for this thread
	 */
	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}
	
	public void prerelax() {
		manualSuspend = true;
		long timeNow = System.currentTimeMillis();
		while (System.currentTimeMillis() - timeNow < 500 && !process.done()) {
			process.step();
		}
		manualSuspend = false;
	}

	public void pause() {
		manualSuspend = true;
	}

	public void relax() {
		// in case its running
		stop();
		stop = false;
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	
	/**
	 * Used for synchronization.
	 */
	public Object pauseObject = new String("PAUSE OBJECT");

	public void resume() {
		manualSuspend = false;
		if(running == false) {
			prerelax();
			relax();
		} else {
			synchronized(pauseObject) {
				pauseObject.notifyAll();
			}
		}
	}

	public synchronized void stop() {
		if(thread != null) {
			manualSuspend = false;
			stop = true;
			// interrupt the relaxer, in case it is paused or sleeping
			// this should ensure that visRunnerIsRunning gets set to false
			try { thread.interrupt(); }
			catch(Exception ex) {
				// the applet security manager may have prevented this.
				// just sleep for a second to let the thread stop on its own
				try { Thread.sleep(1000); }
				catch(InterruptedException ie) {} // ignore
			}
			synchronized (pauseObject) {
				pauseObject.notifyAll();
			}
		}
	}

	public void run() {
	    running = true;
	    try {
	        while (!process.done() && !stop) {
	            synchronized (pauseObject) {
	                while (manualSuspend && !stop) {
	                    try {
	                        pauseObject.wait();
	                    } catch (InterruptedException e) {
	                    	// ignore
	                    }
	                }
	            }
	            process.step();
	            
	            if (stop)
	                return;
	            
	            try {
	                Thread.sleep(sleepTime);
	            } catch (InterruptedException ie) {
	            	// ignore
	            }
	        }

	    } finally {
	        running = false;
	    }
	}
}
