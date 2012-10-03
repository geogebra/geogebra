package geogebra.plugin;

import geogebra.common.main.App;
import geogebra.usb.USBLogger;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.concord.framework.data.stream.DataListener;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.sensor.SensorDataProducer;

public class USBFunctions {
	USBLogger logger = null;
	private ScriptManagerD sm;
	
	public USBFunctions(ScriptManagerD sm) {
		this.sm = sm;
	}
	
	/**
	 * Registers a listener to listen for events from a data logger
	 * eg Go!Motion	
	 */
	public synchronized void registerLoggerListener(String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;		
				
		// init view
		sm.initJavaScript();
		
		// init map and view
		if (loggerListenerMap == null) {
			loggerListenerMap = new ArrayList<String>();			
		}
		
		if (logger == null) logger = new USBLogger(LoggerListener);
		
		// add map entry
		loggerListenerMap.add(JSFunctionName);		
		App.debug("registerLoggerListener: function: " + JSFunctionName);
		
		SensorDataProducer sDataProducer = logger.sDataProducer;
		
		if (sDataProducer != null)
			sDataProducer.start();
	}
	
	DataListener LoggerListener = new DataListener(){
		//private boolean listenersEnabled;

		public void dataReceived(DataStreamEvent dataEvent)
		{
			int numSamples = dataEvent.getNumSamples();
			float [] data = dataEvent.getData();
			if(numSamples > 0) {
				//System.out.println("" + numSamples + " " + data[0]);
				//System.out.flush();

				Object [] args = { data[0] };

				//if (listenersEnabled) 
				{
					int size = loggerListenerMap.size();
					for (int i=0; i < size; i++) {
						String jsFunction = (String) loggerListenerMap.get(i);
						App.debug(jsFunction);
						sm.callJavaScript(jsFunction, args);					
					}			
				}
			} 
			else {
				App.debug("no sample");
			}
		}

		public void dataStreamEvent(DataStreamEvent dataEvent)
		{				
			String eventString;
			int eventType = dataEvent.getType();
			
			if(eventType == 1001) return;
			
			switch(eventType) {
				case DataStreamEvent.DATA_DESC_CHANGED:
					eventString = "Description changed";
				break;
				default:
					eventString = "Unknown event type";					
			}
			
			System.out.println("Data Event: " + eventString); 
			
		
			
		}
	};
	private ArrayList<String> loggerListenerMap;
	
	/**
	 * Removes a previously set change listener for the given object.
	 * @see setChangeListener
	 */
	public synchronized void unregisterLoggerListener(String JSFunctionName) {
		if (loggerListenerMap != null) {
			
			loggerListenerMap.remove(JSFunctionName);		
			App.debug("unregisterLoggerListener for object: " + JSFunctionName);
			
			//Application.debug(loggerListenerMap.size()+"",1);
			
			// stop events from logging device
			if (loggerListenerMap.size() == 0 && logger != null) {
				final SensorDataProducer sDataProducer = logger.sDataProducer;
				
				if (sDataProducer != null) {
		            SwingUtilities.invokeLater( new Runnable(){ public void
		            	run() { 					
			            	App.debug("stopping logging");
							sDataProducer.stop();
		            	} });
				}
				
			}
			
		}
	}					

}
