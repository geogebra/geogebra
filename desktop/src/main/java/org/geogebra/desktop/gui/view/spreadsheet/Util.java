
package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.Component;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JOptionPane;

public class Util {

	public static void handleException(Component component, String message) {
		try {
			throw new RuntimeException(message);
		} catch (RuntimeException ex) {
			handleException(component, ex);
		}
	}

	public static void handleException(Component component, Exception ex) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream printOut = new PrintStream(output);
		ex.printStackTrace(printOut);
		JOptionPane.showMessageDialog(component, output.toString(), "Exception", JOptionPane.ERROR_MESSAGE);
	}


	public static final void memoryWatch(){
		
		long heapSize = Runtime.getRuntime().totalMemory();
		long heapMaxSize = Runtime.getRuntime().maxMemory();
		long heapFreeSize = Runtime.getRuntime().freeMemory();
		long usedMem = heapSize - heapFreeSize;

		System.out.println("max: " + heapMaxSize/1e6 + "  allocated: " + heapSize/1e6 + "  used: " + usedMem/1e6);
				
	}

	
	
}


