package geogebra;

// Wrapper for backward compatibility for auto-updating under Windows.
// TODO: Remove it in GeoGebra 5.2

class GeoGebra3D {
	public static void main(String[] cmdArgs) {	
		org.geogebra.desktop.GeoGebra3D.main(cmdArgs);
	}
}
