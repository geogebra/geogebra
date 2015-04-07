package org.geogebra.desktop.gui.nssavepanel;

public class NSSavePanel {
	public static boolean loaded = false;
	static {
		try {
			System.loadLibrary("nssavepanel");
			loaded = true;
		} catch (UnsatisfiedLinkError err) {
			throw new RuntimeException(err);
		}
	}

	public native String saveDialog(String title, String extension);

}