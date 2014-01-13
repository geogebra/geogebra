package geogebra.html5.cas.giac;

import geogebra.html5.event.CustomEvent;

import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONObject;

/**
 * @author gabor
 * 
 * Class for loading and handling PNAcl compiled giac for chrome extensions.
 *
 */
public class PNaCl {

	private static final String DEFAULT_MIME_TYPE = "application/x-nacl" ;

	public static boolean isEnabled() {
	   return false;
    }

	public void initialize() {
	    // TODO Auto-generated method stub
	    
    }
	
	private boolean isHostToolchain(String tool) {
	    return tool.equals("win") || tool.equals("linux") || tool.equals("mac");
	  }
	
	/**
	   * Create the Native Client <embed> element as a child of the DOM element
	   * named "listener".
	   *
	   * @param name The name of the example.
	   * @param tool The name of the toolchain, e.g. "glibc", "newlib" etc.
	   * @param path Directory name where .nmf file can be found.
	   * @param width The width to create the plugin.
	   * @param height The height to create the plugin.
	   * @param attrs Dictionary of attributes to set on the module.
	   */
	  private void createNaClModule(String name, String tool, String path, int width, int height, JSONObject attrs) {
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
	    	  moduleEl.setAttribute(key, attrs.get(key).toString());
	      }
	    }

	    String mimetype = DEFAULT_MIME_TYPE;
	    moduleEl.setAttribute("type", mimetype);

	    // The <EMBED> element is wrapped inside a <DIV>, which has both a 'load'
	    // and a 'message' event listener attached.  This wrapping method is used
	    // instead of attaching the event listeners directly to the <EMBED> element
	    // to ensure that the listeners are active before the NaCl module 'load'
	    // event fires.
	    Element listenerDiv = Document.get().createDivElement();
	    Document.get().getBody().appendChild(listenerDiv);
	    listenerDiv.appendChild(moduleEl);

	    // Host plugins don't send a moduleDidLoad message. We'll fake it here.
	    boolean isHost = isHostToolchain(tool);
	    if (isHost) {
	      Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
			
			public boolean execute() {
				moduleEl.setAttribute("readyState", "1");
		        moduleEl.dispatchEvent(CustomEvent.getNativeEvent("loadstart"));
		        moduleEl.setAttribute("readyState", "4");
		        moduleEl.dispatchEvent(CustomEvent.getNativeEvent("load"));
		        moduleEl.dispatchEvent(CustomEvent.getNativeEvent("loadend"));
				return false;
			}
		}, 100);
	  }
	  }


}
