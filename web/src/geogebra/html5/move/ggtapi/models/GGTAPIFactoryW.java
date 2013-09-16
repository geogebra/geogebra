package geogebra.html5.move.ggtapi.models;

import geogebra.common.move.ggtapi.GGTAPIFactory;
import geogebra.common.move.ggtapi.models.AuthenticationModel;

public class GGTAPIFactoryW extends GGTAPIFactory {

	public static AuthenticationModel createAuthenticationModel() {
		return new AuthenticationModelW();
	}
	
	
}
