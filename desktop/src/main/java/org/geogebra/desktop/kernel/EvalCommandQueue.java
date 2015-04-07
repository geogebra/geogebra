package org.geogebra.desktop.kernel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import org.geogebra.common.plugin.GgbAPI;

/**
 * @author mrb
 * 
 *         Allows commands to be queued, see #106
 *
 */
public class EvalCommandQueue implements ActionListener {

	private final static int STANDARD_ANIMATION_TIME = 50; // millisecs

	private GgbAPI ggbAPI;
	private ArrayList<String> queue;
	private Timer timer;

	/**
	 * @param ggbAPI
	 *            ggbAPI
	 */
	public EvalCommandQueue(GgbAPI ggbAPI) {
		this.ggbAPI = ggbAPI;
		queue = new ArrayList<String>();

		timer = new Timer(STANDARD_ANIMATION_TIME, this);
	}

	/**
	 * Adds command to the queue
	 * 
	 * @param cmd
	 *            GeoGebra command to evaluate
	 */
	final public synchronized void addCommand(String cmd) {
		queue.add(cmd);

		timer.start();
	}

	/**
	 * sends all commands in the queue to evalCommand();
	 */
	final public synchronized void actionPerformed(ActionEvent e) {
		while (queue.size() > 0) {
			String cmd = queue.remove(0);
			ggbAPI.evalCommand(cmd);
			// AbstractApplication.debug("evaluating "+cmd);
		}

		timer.stop();
	}

}
