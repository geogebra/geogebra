package geogebra.web.asyncservices;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("handleOA")
public interface HandleOAuth2Service extends RemoteService {
	
	Boolean triggerLoginToGoogle();
	

}
