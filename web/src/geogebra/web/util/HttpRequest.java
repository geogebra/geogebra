package geogebra.web.util;
import geogebra.common.util.AbstractHttpRequest;
import com.google.gwt.http.client.*;

public class HttpRequest extends AbstractHttpRequest {
	String answer;
	
	/* The following code has been copied mostly from
	 * http://code.google.com/intl/hu-HU/webtoolkit/doc/latest/DevGuideServerCommunication.html#DevGuideHttpRequests
	 */
    public String getResponse(String url) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
		
		try {
			  Request request = builder.sendRequest(null, new RequestCallback() {
			    public void onError(Request request, Throwable exception) {
			       // Couldn't connect to server (could be timeout, SOP violation, etc.)
			    }

			    public void onResponseReceived(Request request, Response response) {
			      if (200 == response.getStatusCode()) {
			          // Process the response in response.getText()
			    	  answer = response.getText();
			      } else {
			        // Handle the error.  Can get the status text from response.getStatusText()
			      }
			    }
			  });
			} catch (RequestException e) {
			  // Couldn't connect to server
			}
		
	    return answer;
    }

}

