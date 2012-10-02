package geogebra;

import geogebra.common.GeoGebraConstants;

import java.awt.Color;

import javax.swing.JApplet;

/**
 * Applet to load all GeoGebra jar files in the background.
 */
public class GeoGebraAppletPreloader extends JApplet {
	private static final long serialVersionUID = 1L;
	@Override
	public void init() {
		setBackground(Color.white);
		System.out.println("GeoGebraAppletPreloader " + GeoGebraConstants.VERSION_STRING + " started");
		loadAllJarFiles(true);
	}
	
	/**
	 * Loads all jar files in a background task. 
	 * @param loadJavaScriptJAR true to load geogebra_javascript.jar
	 */
	public static void loadAllJarFiles(final boolean loadJavaScriptJAR) {
		Thread jarLoader = new Thread() {
			@Override
			public void run() {
				// touch on file in all jar files to force loading

				// load main jar
				System.out.print("loading geogebra_main.jar... ");
				System.out.flush();
				try {
					geogebra.main.AppD.class.getClass();
					System.out.println("done");
				} catch (Exception e) {
					System.out.println("failed");
				} catch (Throwable e) {
					System.out.println("failed");
				}
				System.out.flush();
				
				// load main jar
				System.out.print("loading geogebra_algos.jar... ");
				System.out.flush();
				try {
					geogebra.common.kernel.discrete.AlgoVoronoi.class.getClass();
					System.out.println("done");
				} catch (Exception e) {
					System.out.println("failed");
				} catch (Throwable e) {
					System.out.println("failed");
				}
				System.out.flush();
				
				// load cas jar
				System.out.print("loading geogebra_cas.jar... ");
				System.out.flush();
				try {
					geogebra.common.cas.GeoGebraCAS.class.getClass();
					System.out.println("done");
				} catch (Exception e) {
					System.out.println("failed");
				} catch (Throwable e) {
					System.out.println("failed");
				}
				System.out.flush();
				
				// load gui jar
				System.out.print("loading geogebra_gui.jar... ");
				System.out.flush();
				try {
					geogebra.gui.GuiManagerD.class.getClass();
					System.out.println("done");
				} catch (Exception e) {
					System.out.println("failed");
				} catch (Throwable e) {
					System.out.println("failed");
				}
				System.out.flush();
				
				// force loading properties
				System.out.print("loading geogebra_properties.jar... ");
				System.out.flush();
				try {
					Object url = GeoGebraAppletPreloader.class.getResource("/geogebra/properties/plain.properties"); 
					if (url != null)
						System.out.println("done");
					else
						System.out.println("not found");
				} catch (Exception e) {
					System.out.println("not found");
				} catch (Throwable e) {
					System.out.println("not found");
				}
				System.out.flush();
				
				// load export jar
				System.out.print("loading geogebra_export.jar... ");
				System.out.flush();
				try {
					geogebra.export.WorksheetExportDialog.class.getClass();
					System.out.println("done");
				} catch (Exception e) {
					System.out.println("failed");
				} catch (Throwable e) {
					System.out.println("failed");
				}
				System.out.flush();
				
				// load jlatexmath jar
				System.out.print("loading jlatexmath.jar... ");
				System.out.flush();
				try {
					org.scilab.forge.jlatexmath.TeXFormula.class.getClass();
					System.out.println("done");
				} catch (Exception e) {
					System.out.println("failed");
				} catch (Throwable e) {
					System.out.println("failed");
				}
				System.out.flush();
				
				// load jlm_greek jar
				System.out.print("loading jlm_greek.jar... ");
				System.out.flush();
				try {
					org.scilab.forge.jlatexmath.greek.GreekRegistration.class.getClass();
					System.out.println("done");
				} catch (Exception e) {
					System.out.println("failed");
				} catch (Throwable e) {
					System.out.println("failed");
				}
				System.out.flush();
				
				// load jlm_cyrillic jar
				System.out.print("loading jlm_cyrillic.jar... ");
				System.out.flush();
				try {
					org.scilab.forge.jlatexmath.cyrillic.CyrillicRegistration.class.getClass();
					System.out.println("done");
				} catch (Exception e) {
					System.out.println("failed");
				} catch (Throwable e) {
					System.out.println("failed");
				}
				System.out.flush();
				
				// load usb jar
				System.out.print("loading geogebra_usb.jar... ");
				System.out.flush();
				try {
					geogebra.usb.USBLogger.class.getClass();
					System.out.println("done");
				} catch (Exception e) {
					System.out.println("failed");
				} catch (Throwable e) {
					System.out.println("failed");
				}
				System.out.flush();
				
				System.out.print("loading jython.jar... ");
				System.out.flush();
				try {
					geogebra.plugin.jython.PythonBridgeD.class.getClass();
					System.out.println("done");
				} catch (Exception e) {
					System.out.println("failed");
				} catch (Throwable e) {
					System.out.println("failed");
				}
				System.out.flush();
				
				if (loadJavaScriptJAR) {
					// load javascript jar
					System.out.print("loading geogebra_javascript.jar... ");
					System.out.flush();
					try {
						org.mozilla.javascript.Context.class.getClass();
						System.out.println("done");
					} catch (Exception e) {
						System.out.println("failed");
					} catch (Throwable e) {
						System.out.println("failed");
					}
					System.out.flush();
				}
			}
		};
		jarLoader.start();
	}
}
