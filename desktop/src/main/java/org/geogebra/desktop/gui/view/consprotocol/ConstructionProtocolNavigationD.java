/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package org.geogebra.desktop.gui.view.consprotocol;

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

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.menubar.GeoGebraMenuBar;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Navigation buttons for the construction protocol
 */
public class ConstructionProtocolNavigationD extends
		ConstructionProtocolNavigation implements ActionListener,
		SettingListener, SetLabels {

	private JButton btFirst, btPrev, btNext, btLast, btOpenWindow;
	/** Button for starting/stopping animation*/
	JButton btPlay;
	private JLabel lbSteps;
	/** Delay spinner */
	JSpinner spDelay;
	/** Application */
	AppD app;
	/** Construction protocol view */
	ConstructionProtocolViewD prot;
	private AutomaticPlayer player;
	/**
	 * ConstructionProtocolNavigation panel
	 */
	private JPanel implPanel;
	private LocalizationD loc;
	
	private JPanel playPanel;

	/**
	 * Creates a new navigation bar to step through the construction protocol.
	 * @param app application
	 */
	public ConstructionProtocolNavigationD(AppD app, int viewID) {
		implPanel = new JPanel();
		this.app = app;
		this.loc = app.getLocalization();
		this.viewID = viewID;
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
		
		// done when needed, later
		//initGUI();
		
/*		//next 3 rows moved into EuclidianDockPanel.loadComponent
		//because it not neccessary for all Contruction protocol navigation issue
		ConstructionProtocolSettings cps = app.getSettings().getConstructionProtocol();
		settingsChanged(cps);
		cps.addListener(this);
		*/
	}
	
	/**
	 * @return underlying JPanel implementation
	 */
	public JPanel getImpl(){
		return implPanel;
	}
		
	/**
	 * @param flag whether button to show construction protocol should be visible
	 */
	@Override
	public void setConsProtButtonVisible(boolean flag) {
		showConsProtButton = flag;	
		if (btOpenWindow != null) {
			btOpenWindow.setVisible(isConsProtButtonVisible());
		}
	}
	
	/**
	 * Changes animation delay
	 * @param delay delay in seconds
	 */
	@Override
	public void setPlayDelay(double delay) {
		playDelay = delay;
		
		try {
			spDelay.setValue(Double.valueOf(playDelay));
		} catch (Exception e) {
			spDelay.setValue(Integer.valueOf((int) Math.round(playDelay)));
			
		}
	}	

	/**
	 * Initializes all components, sets labels
	 */
	public void initGUI() {

		implPanel.removeAll();	
					
		btFirst = new JButton(app.getScaledIcon(GuiResourcesD.NAV_SKIPBACK64));
		btLast = new JButton(app.getScaledIcon(GuiResourcesD.NAV_SKIPFORWARD64));
		btPrev = new JButton(app.getScaledIcon(GuiResourcesD.NAV_REWIND64));
		btNext = new JButton(app.getScaledIcon(GuiResourcesD.NAV_FASTFORWARD64));
				
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
		playPanel.add(Box.createRigidArea(new Dimension(20, 10)));
		btPlay = new JButton();
		btPlay.setIcon(new ImageIcon(app.getPlayImage()));
		btPlay.addActionListener(this); 	
											
		spDelay.addChangeListener(new ChangeListener() {
			@Override
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
		btOpenWindow.setIcon(app
				.getScaledIcon(GuiResourcesD.MENU_VIEW_CONSTRUCTION_PROTOCOL));
		btOpenWindow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//app.getGuiManager().showConstructionProtocol();
				if(!app.getGuiManager().showView(App.VIEW_CONSTRUCTION_PROTOCOL))
					app.getGuiManager().setShowView(true, App.VIEW_CONSTRUCTION_PROTOCOL);
				
				// Checkbox of Construction protocol view will be checked in view menu
				((GeoGebraMenuBar)(((GuiManagerD) app.getGuiManager()).getMenuBar())).updateCPView(true);
			}				
		});
		btOpenWindow.setVisible(isConsProtButtonVisible());
		
		// add panels together to center
		implPanel.setLayout(new BoxLayout(this.implPanel, BoxLayout.LINE_AXIS));		
		implPanel.add(leftPanel);
		implPanel.add(playPanel);
		implPanel.add(btOpenWindow);
		implPanel.add(Box.createRigidArea(new Dimension(20,10)));
								
		setLabels();
		setPlayDelay(playDelay);
		update();
	}
	
	@Override
	public void setLabels() {
		if (btPlay != null) {
			btPlay.setText(loc.getPlain("Play"));
		}
		if (btOpenWindow != null) {
			btOpenWindow.setToolTipText(loc.getPlainTooltip("ConstructionProtocol"));
		}
	}
	

	/**
	 * Updates the texts that show the current construction step and
	 * the number of construction steps.	
	 */
	@Override
	public void update() {	
		if (prot != null) {
			int currentStep = prot.getCurrentStepNumber();
			int stepNumber  = prot.getLastStepNumber();
			lbSteps.setText(currentStep + " / " + stepNumber);	
		}
	}
	
	@Override
	public void register(ConstructionProtocolView constructionProtocolView) {
		if (prot == null) { 
			initGUI(); 
		}

		if (constructionProtocolView instanceof ConstructionProtocolViewD) {
			prot = (ConstructionProtocolViewD) constructionProtocolView;
			prot.registerNavigationBar(this);
		}
	}
	
	/**
	 * Unregisters this navigation bar from its protocol.
	 */
	public void unregister() {
		prot.unregisterNavigationBar(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		implPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));		
		
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
			
		if (prot.getCpPanel().isVisible()) 
			prot.scrollToConstructionStep();
				
		implPanel.setCursor(Cursor.getDefaultCursor());		
	}
	/**
	 * Make all components enabled / disabled
	 * @param flag whether components should be enabled
	 */
	void setComponentsEnabled(boolean flag) {
		Component comps[] = implPanel.getComponents();
		for (int i=0; i < comps.length; i++) {
			comps[i].setEnabled(flag);
		}
		btPlay.setEnabled(true);	
		lbSteps.setEnabled(true);
	}	
	
	@Override
	public void setButtonPlay() {
		btPlay.setIcon(new ImageIcon(app.getPlayImage()));
		btPlay.setText(loc.getPlain("Play"));
	}

	@Override
	public void setButtonPause() {
		btPlay.setIcon(new ImageIcon(app.getPauseImage()));
		btPlay.setText(loc.getPlain("Pause"));
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
			app.setNavBarButtonPause();
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
			app.setNavBarButtonPlay();
			setComponentsEnabled(true);
			app.setDefaultCursor();
        }

		@Override
		public synchronized void actionPerformed(ActionEvent e) {
        	prot.nextStep();        	
        	if (prot.getCurrentStepNumber() == prot.getLastStepNumber()) {
        		stopAnimation();
        	}
        }       
    }

	@Override
	public void settingsChanged(AbstractSettings settings) {
		ConstructionProtocolSettings cps = (ConstructionProtocolSettings)settings;
		setPlayButtonVisible(cps.showPlayButton());
		setPlayDelay(cps.getPlayDelay());
		setConsProtButtonVisible(cps.showConsProtButton());
		update();
		
	}

	@Override
	public void setVisible(boolean visible) {
		getImpl().setVisible(visible);
	}

	public void updateIcons() {
		if (btFirst == null) {
			return;
		}
		btFirst.setIcon(app.getScaledIcon(GuiResourcesD.NAV_SKIPBACK64));
		btLast.setIcon(app.getScaledIcon(GuiResourcesD.NAV_SKIPFORWARD64));
		btPrev.setIcon(app.getScaledIcon(GuiResourcesD.NAV_REWIND64));
		btNext.setIcon(app.getScaledIcon(GuiResourcesD.NAV_FASTFORWARD64));
		btOpenWindow.setIcon(app
				.getScaledIcon(GuiResourcesD.MENU_VIEW_CONSTRUCTION_PROTOCOL));
		lbSteps.setFont(app.getPlainFont());
		update();

	}

	@Override
	protected void setPlayPanelVisible(boolean flag) {
		if (playPanel != null) {
			this.playPanel.setVisible(flag);
		}

	}	
}
