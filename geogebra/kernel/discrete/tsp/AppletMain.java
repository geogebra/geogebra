package geogebra.kernel.discrete.tsp;

import geogebra.kernel.discrete.tsp.controller.Controller;
import geogebra.kernel.discrete.tsp.gui.DemoPanel;
import geogebra.kernel.discrete.tsp.gui.StatusBar;
import geogebra.kernel.discrete.tsp.method.GraphDemonstration;
import geogebra.kernel.discrete.tsp.method.tsp.BranchBound;
import geogebra.kernel.discrete.tsp.method.tsp.Opt3;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * ã‚¢ãƒ—ãƒ¬ãƒƒãƒˆã‚’èµ·å‹•ã�™ã‚‹ã�Ÿã‚�ã�®ã‚¯ãƒ©ã‚¹
 * @author ma38su
 */
public class AppletMain extends JApplet {
	@Override
	public void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		Opt3 opt3 = new Opt3();
		final GraphDemonstration construction = new BranchBound(100, opt3);

		final Observable observable = new Observable() {
			@Override
			public void notifyObservers(Object arg) {
				super.setChanged();
				super.notifyObservers(arg);
			}
		};

		final DemoPanel panel = new DemoPanel(observable);
		
		this.setLayout(new BorderLayout());

		Controller controller = new Controller(panel);
		panel.addMouseListener(controller);
		this.add(panel, BorderLayout.CENTER);

		JPanel subPanel = new JPanel(new BorderLayout());
		final StatusBar statusbar = new StatusBar();
		observable.addObserver(statusbar);
		
		subPanel.add(statusbar, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		subPanel.add(buttonPanel, BorderLayout.EAST);

		final JButton startButton = new JButton("Run");
		buttonPanel.add(startButton);
		final ActionListener calc = new ActionListener() {
			private Thread thread;
			public void actionPerformed(ActionEvent e) {
				panel.setCost(0D);
				startButton.setText("Stop");
				Thread thread = new Thread() {
					@Override
					public void run() {
						construction.method(panel);
						startButton.setText("Run");
					}
				};
				if (this.thread == null) {
					this.thread = thread;
					this.thread.start();
				} else {
					synchronized (this.thread) {
						if (this.thread.isAlive()) {
							this.thread.stop();
						}
						this.thread = thread;
						this.thread.start();
					}
				}
			}
		};
		startButton.addActionListener(calc);

		JButton clearButton = new JButton("Clear");
		buttonPanel.add(clearButton);
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.clear();
			}
		});
		panel.setInterval(100);
		this.add(subPanel, BorderLayout.SOUTH);
	}
}
