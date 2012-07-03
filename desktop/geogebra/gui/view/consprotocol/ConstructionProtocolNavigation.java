/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.gui.view.consprotocol;

import geogebra.common.gui.SetLabels;
import geogebra.common.main.App;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.ConstructionProtocolSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.main.AppD;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Navigation buttons for the construction protocol
 */
public class ConstructionProtocolNavigation extends JPanel implements ActionListener, SettingListener, SetLabels {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton btFirst, btPrev, btNext, btLast, btOpenWindow;
	/** Button for starting/stopping animation*/
	JButton btPlay;
	private JLabel lbSteps;
	/** Delay spinner */
	JSpinner spDelay;
	/** Delay in seconds */
	double playDelay = 2;	 
	private JPanel playPanel;
	/** Application */
	AppD app;
	/** Construction protocol view */
	ConstructionProtocolView prot;
	private boolean showPlayButton = true, 
					showConsProtButton = true;
	
	private AutomaticPlayer player;
	/** Indicates whether animation is on or off */
	boolean isPlaying;
	
	/**
	 * Creates a new navigation bar to step through the construction protocol.
	 * @param prot construction protocol view
	 */
	public ConstructionProtocolNavigation(ConstructionProtocolView prot) {
		this.prot = prot;			
		app = prot.getApplication();	
				
		SpinnerModel model =
	        new SpinnerNumberModel(2, //initial value
	                               0.25, //min
	                               10, //max
	                               0.25); //step
		spDelay = new JSpinner(model);	
		NumberEditor numEdit = new JSpinner.NumberEditor(spDelay, "#.##");
		DecimalFormat format = numEdit.getFormat();
		format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
		
		lbSteps = new JLabel();
		
		initGUI();
		
/*		//next 3 rows moved into EuclidianDockPanel.loadComponent
		//because it not neccessary for all Contruction protocol navigation issue
		ConstructionProtocolSettings cps = app.getSettings().getConstructionProtocol();
		settingsChanged(cps);
		cps.addListener(this);
		*/
	}
		
	/**
	 * @return whether play button is visible
	 */
	public boolean isPlayButtonVisible() {
		return showPlayButton;
	}
	
	/**
	 * @param flag true to make play button visible
	 */
	public void setPlayButtonVisible(boolean flag) {
		showPlayButton = flag;	
		playPanel.setVisible(flag);
	}
	
	/**
	 * @return whether button to show construction protocol is visible
	 */
	public boolean isConsProtButtonVisible() {
		return showConsProtButton;
	}	
	
	/**
	 * @param flag whether button to show construction protocol should be visible
	 */
	public void setConsProtButtonVisible(boolean flag) {		
		showConsProtButton = flag;	
		btOpenWindow.setVisible(flag);
	}
	
	/**
	 * Returns delay between frames of automatic construction protocol
	 * playing in seconds.
	 * @return delay in seconds
	 */
	public double getPlayDelay() {
		return playDelay;
	}
	/**
	 * Changes animation delay
	 * @param delay delay in seconds
	 */
	public void setPlayDelay(double delay) {
		playDelay = delay;
		
		try {
			spDelay.setValue(new Double(playDelay));
		} catch (Exception e) {
			spDelay.setValue(new Integer((int) Math.round(playDelay)));
			
		}
	}	
	/**
	 * Initializes all components, sets labels
	 */
	public void initGUI() {
		removeAll();	
					
		btFirst = new JButton(app.getImageIcon("nav_skipback.png"));
		btLast = new JButton(app.getImageIcon("nav_skipforward.png"));		
		btPrev = new JButton(app.getImageIcon("nav_rewind.png"));		
		btNext = new JButton(app.getImageIcon("nav_fastforward.png"));				
				
		btFirst.addActionListener(this);
		btLast.addActionListener(this);		
		btPrev.addActionListener(this); 
		btNext.addActionListener(this); 			
		
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));	
		leftPanel.add(btFirst);
		leftPanel.add(btPrev);
		leftPanel.add(lbSteps);			
		leftPanel.add(btNext);
		leftPanel.add(btLast);
		
		playPanel = new JPanel();
		playPanel.setVisible(showPlayButton);
		playPanel.add(Box.createRigidArea(new Dimension(20,10)));
		btPlay = new JButton();
		btPlay.setIcon(new ImageIcon(app.getPlayImage()));
		btPlay.addActionListener(this); 	
											
		spDelay.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				try {
					playDelay = Double.parseDouble(spDelay.getValue().toString());
				} catch (Exception ex) {
					playDelay = 2;
				}
			}			
		});
					
		playPanel.add(btPlay);
		playPanel.add(spDelay);	
		playPanel.add(new JLabel("s"));		
		
				
		btOpenWindow = new JButton();
		btOpenWindow.setIcon(app.getImageIcon("table.gif"));			
		btOpenWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//app.getGuiManager().showConstructionProtocol();
				if(!app.getGuiManager().showView(App.VIEW_CONSTRUCTION_PROTOCOL))
					app.getGuiManager().setShowView(true, App.VIEW_CONSTRUCTION_PROTOCOL);
			}				
		});
		
		// add panels together to center
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));		
		add(leftPanel);
		add(playPanel);
		add(btOpenWindow);
		add(Box.createRigidArea(new Dimension(20,10)));
								
		setLabels();
		setPlayDelay(playDelay);
		update();
	}
	
	public void setLabels() {
		if (btPlay != null)
			btPlay.setText(app.getPlain("Play"));
		if (btOpenWindow != null)
			btOpenWindow.setToolTipText(app.getPlainTooltip("ConstructionProtocol"));
	}
	
	/**
	 * Updates the texts that show the current construction step and
	 * the number of construction steps.	
	 */
	public void update() {	
		int currentStep = prot.getCurrentStepNumber();
		int stepNumber  = prot.getLastStepNumber();
		lbSteps.setText(currentStep + " / " + stepNumber);	
	}
	
	/**
	 * Registers this navigation bar at its protocol
	 * to be informed about updates.
	 */
	public void register() {
		prot.registerNavigationBar(this);
		update();
	}
	
	/**
	 * Unregisters this navigation bar from its protocol.
	 */
	public void unregister() {
		prot.unregisterNavigationBar(this);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));		
		
		if (source == btFirst) {
			prot.firstStep();		
		} 
		else if (source == btLast) {			
			prot.lastStep();
		}
		else if (source == btPrev) {
			prot.previousStep();
		}
		else if (source == btNext) {
			prot.nextStep();
		}
		else if (source == btPlay) {						
			if (isPlaying) {				
				player.stopAnimation();
			} else {									
				player = new AutomaticPlayer(playDelay);
				player.startAnimation();
			}									
		}	
			
		if (prot.isVisible()) 
			prot.scrollToConstructionStep();
				
		setCursor(Cursor.getDefaultCursor());		
	}
	/**
	 * Make all components enabled / disabled
	 * @param flag whether components should be enabled
	 */
	void setComponentsEnabled(boolean flag) {
		Component comps[] = getComponents();
		for (int i=0; i < comps.length; i++) {
			comps[i].setEnabled(flag);
		}
		btPlay.setEnabled(true);	
		lbSteps.setEnabled(true);
	}	
	
	/**
	 * Steps through the construction automatically.
	 */
	private class AutomaticPlayer implements ActionListener {             
        private Timer timer; // for animation                     
        
        /**
         * Creates a new player to step through the construction
         * automatically.
         * @param delay in seconds between steps
         */
        public AutomaticPlayer(double delay) {
        	 timer = new Timer((int) (delay * 1000), this);        	         	        	
        }      

        public synchronized void startAnimation() {    
        	// dispatch events to play button
			app.startDispatchingEventsTo(btPlay);
			isPlaying = true;
			btPlay.setIcon(new ImageIcon(app.getPauseImage()));
			btPlay.setText(app.getPlain("Pause"));
			setComponentsEnabled(false);
			app.setWaitCursor();
			
			if (prot.getCurrentStepNumber() == prot.getLastStepNumber()) {
        		prot.firstStep();
        	}
			
            timer.start();
        }

        public synchronized void stopAnimation() {
            timer.stop();                   
            
            // unblock application events
			app.stopDispatchingEvents();
			isPlaying = false;
			btPlay.setIcon(new ImageIcon(app.getPlayImage()));
			btPlay.setText(app.getPlain("Play"));
			setComponentsEnabled(true);
			app.setDefaultCursor();
        }

        public synchronized void actionPerformed(ActionEvent e) {        	        	
        	prot.nextStep();        	
        	if (prot.getCurrentStepNumber() == prot.getLastStepNumber()) {
        		stopAnimation();
        	}
        }       
    }

	public void settingsChanged(AbstractSettings settings) {
		ConstructionProtocolSettings cps = (ConstructionProtocolSettings)settings;
		setPlayButtonVisible(cps.showPlayButton());
		setPlayDelay(cps.getPlayDelay());
		setConsProtButtonVisible(cps.showConstructionProtocol());
		update();
		
	}	
}
