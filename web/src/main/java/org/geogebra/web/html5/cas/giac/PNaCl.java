package org.geogebra.web.html5.cas.giac;

import java.util.HashMap;
import java.util.Set;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.event.CustomEvent;
import org.geogebra.web.html5.util.EmbedElement;
import org.geogebra.web.html5.util.JSON;
import org.geogebra.web.html5.util.JavaScriptEventHandler;
import org.geogebra.web.html5.util.URL;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

/**
 * @author gabor
 * 
 *         Class for loading and handling PNAcl compiled giac for chrome
 *         extensions.
 *
 */
public class PNaCl {

	private static final String DEFAULT_MIME_TYPE = "application/x-nacl";
	private static final String data_name = "giac";
	private static final String data_tools = "pnacl newlib glibc linux";
	private static final String data_configs = "giac_nacl/pnacl/Debug giac_nacl/pnacl/Release";
	private static final String data_path = "{config}";

	private static enum STATUS {
		CREATING_EMBED, CRASHED, EXITED, RUNNING, PAGE_LOADED, DOES_NOT_SUPPORT_NACL_OR_NACL_DISABLED, WAITING
	};

	private static PNaCl INSTANCE = null;

	private STATUS currentStatus = STATUS.PAGE_LOADED;

	public static boolean isEnabled() {
		return isBrowserSupportNaCl() && isNaClEnabled();
	}

	private static boolean isNaClEnabled() {
		// hack for now
		return URL.getQueryParameterAsString("nacl") != null
		        && URL.getQueryParameterAsString("nacl").equals("true");
	}

	private native static boolean isBrowserSupportNaCl() /*-{
		return ($wnd.navigator.mimeTypes !== undefined)
				&& ($wnd.navigator.mimeTypes[@org.geogebra.web.html5.cas.giac.PNaCl::DEFAULT_MIME_TYPE] !== undefined);
	}-*/;

	/**
	 * the naCl module
	 */
	EmbedElement naclModule;

	/**
	 * Initializing CAS
	 */
	public void initialize() {
		DOMContentLoaded();
	}

	private boolean isHostToolchain(String tool) {
		return tool.equals("win") || tool.equals("linux") || tool.equals("mac");
	}

	/**
	 * Create the Native Client <embed> element as a child of the DOM element
	 * named "listener".
	 *
	 * @param name
	 *            The name of the example.
	 * @param tool
	 *            The name of the toolchain, e.g. "glibc", "newlib" etc.
	 * @param path
	 *            Directory name where .nmf file can be found.
	 * @param width
	 *            The width to create the plugin.
	 * @param height
	 *            The height to create the plugin.
	 * @param attrs
	 *            Dictionary of attributes to set on the module.
	 */
	private void createNaClModule(String name, String tool, String path,
	        int width, int height, HashMap<String, String> attrs) {
		final Element moduleEl = Document.get().createElement("embed");
		moduleEl.setAttribute("name", "nacl_module");
		moduleEl.setAttribute("id", "nacl_module");
		moduleEl.setAttribute("width", String.valueOf(width));
		moduleEl.setAttribute("height", String.valueOf(height));
		moduleEl.setAttribute("path", path);
		moduleEl.setAttribute("src", path + '/' + name + ".nmf");

		// Add any optional arguments
		if (attrs != null) {
			Set<String> keys = attrs.keySet();
			for (String key : keys) {
				moduleEl.setAttribute(key, attrs.get(key));
			}
		}

		String mimetype = DEFAULT_MIME_TYPE;
		moduleEl.setAttribute("type", mimetype);

		// The <EMBED> element is wrapped inside a <DIV>, which has both a
		// 'load'
		// and a 'message' event listener attached. This wrapping method is used
		// instead of attaching the event listeners directly to the <EMBED>
		// element
		// to ensure that the listeners are active before the NaCl module 'load'
		// event fires.
		Element listenerDiv = Document.get().createDivElement();
		listenerDiv.setAttribute("id", "listener");
		Document.get().getBody().appendChild(listenerDiv);
		attachDefaultListeners();
		listenerDiv.appendChild(moduleEl);

		// Host plugins don't send a moduleDidLoad message. We'll fake it here.
		boolean isHost = isHostToolchain(tool);
		if (isHost) {
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

				public boolean execute() {
					moduleEl.setAttribute("readyState", "1");
					moduleEl.dispatchEvent(CustomEvent
					        .getNativeEvent("loadstart"));
					moduleEl.setAttribute("readyState", "4");
					moduleEl.dispatchEvent(CustomEvent.getNativeEvent("load"));
					moduleEl.dispatchEvent(CustomEvent
					        .getNativeEvent("loadend"));
					return false;
				}
			}, 100);
		}
	}

	/**
	 * Add the default "load" and "message" event listeners to the element with
	 * id "listener".
	 *
	 * The "load" event is sent when the module is successfully loaded. The
	 * "message" event is sent when the naclModule posts a message using
	 * PPB_Messaging.PostMessage() (in C) or pp::Instance().PostMessage() (in
	 * C++).
	 */
	private void attachDefaultListeners() {
		Element listenerDiv = Document.get().getElementById("listener");
		CustomEvent
		        .addEventListener("load", listenerDiv, moduleDidLoad(), true);
		CustomEvent.addEventListener("message", listenerDiv, handleMessage(),
		        true);
		CustomEvent.addEventListener("crash", listenerDiv, handleCrash(), true);
		// if (typeof window.attachListeners !== 'undefined') {
		// window.attachListeners();
		// }
	}

	private JavaScriptEventHandler handleCrash() {
		return new JavaScriptEventHandler() {

			public void execute(JavaScriptObject event) {
				// todo check if this is good or not
				if (naclModule.getAttribute("exitStatus") == "-1") {
					updateStatus("CRASHED");
				} else {
					updateStatus("EXITED ["
					        + naclModule.getAttribute("exitStatus") + "]");
				}
				// if (typeof window.handleCrash !== 'undefined') {
				// window.handleCrash(common.naclModule.lastError);
				// }
			}
		};
	}

	private JavaScriptEventHandler handleMessage() {
		// debug for now, but later pass it to GGB;
		return new JavaScriptEventHandler() {

			public void execute(JavaScriptObject event) {
				App.debug(JSON.get(event, "data"));
			}
		};
	}

	/**
	 * Called when the NaCl module is loaded.
	 *
	 * This event listener is registered in attachDefaultListeners above.
	 * 
	 * @return The javascript event handler
	 */
	JavaScriptEventHandler moduleDidLoad() {
		return new JavaScriptEventHandler() {

			public void execute(JavaScriptObject event) {
				naclModule = EmbedElement.as(Document.get().getElementById(
				        "nacl_module"));
				updateStatus("RUNNING");
			}

		};

		// if (typeof window.moduleDidLoad !== 'undefined') {
		// window.moduleDidLoad();
		// }
	}

	private static void updateStatus(String msg) {
		App.debug(msg);
	}

	private void DOMContentLoaded() {
		String[] toolchains = data_tools.split(" ");
		String[] configs = data_configs.split(" ");

		HashMap<String, String> attrs = new HashMap();

		String tc = toolchains[0];
		String config = configs[0];
		String pathFormat = data_path;
		String path = pathFormat.replace("{tc}", tc)
		        .replace("{config}", config);

		boolean isRelease = path.toLowerCase().indexOf("release") != -1;

		loadFunction(data_name, tc, path, 0, 0, attrs);
	}

	private void loadFunction(String name, String tool, String path, int width,
	        int height, HashMap<String, String> attrs) {
		// If the page loads before the Native Client module loads, then set the
		// status message indicating that the module is still loading.
		// Otherwise,
		// do not change the status message.
		updateStatus("Page loaded.");
		if (!isBrowserSupportNaCl()) {
			updateStatus("Browser does not support NaCl (" + tool
			        + "), or NaCl is disabled");
		} else if (naclModule == null) {
			updateStatus("Creating embed: " + tool);

			// We use a non-zero sized embed to give Chrome space to place the
			// bad
			// plug-in graphic, if there is a problem.
			// attachDefaultListeners(); //called in createNaclModule
			createNaClModule(name, tool, path, width, height, attrs);
		} else {
			// It's possible that the Native Client module onload event fired
			// before the page's onload event. In this case, the status message
			// will reflect 'SUCCESS', but won't be displayed. This call will
			// display the current message.
			updateStatus("Waiting.");
		}
	}

	/**
	 * @return the instance of PNACL;
	 */
	public static PNaCl get() {
		if (INSTANCE == null) {
			INSTANCE = new PNaCl();
		}
		return INSTANCE;
	}

	/**
	 * @param msg
	 *            post Message to to embed element
	 */
	public void postMessage(String msg) {
		naclModule.postMessage(msg);
	};

	/* JUST for debug */

	public static native void exportPNaCltoConsole() /*-{
		$wnd.giacPNaClInit = $entry(@org.geogebra.web.html5.cas.giac.PNaCl::initPNaClFromConsole());
	}-*/;

	public static void sendToPNaClCas(String msg) {
		PNaCl.get().postMessage(msg);
	}

	public static void initPNaClFromConsole() {
		PNaCl.get().initialize();
		PNaCl.get();
		PNaCl.exportPostMessageToConsole();
	}

	private static native void exportPostMessageToConsole() /*-{
	                                                        $wnd.giacPNaCl_postMessage = $entry(@org.geogebra.web.html5.cas.giac.PNaCl::sendToPNaClCas(Ljava/lang/String;));
	                                                        }-*/;

}
