package geogebra.html5.move.ggtapi.operations;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.operations.OpenFromGGTOperation;

/**
 * @author gabor
 * Open From GGT operational class for Web
 */
public class OpenFromGGTOperationW extends OpenFromGGTOperation {

	private static final String CALLBACKURL = "";

	/**
	 * @param app Application
	 * Open from GGT operational class for web
	 */
	public OpenFromGGTOperationW(App app) {
	    super(app);
    }
	
	/**
	 * @return GGTURL for Web
	 */
	public String generateOpenFromGGTURL() {
		return super.generateOpenFromGGTURL(APP_TYPE.WEB) + CALLBACKURL;
	}

}
