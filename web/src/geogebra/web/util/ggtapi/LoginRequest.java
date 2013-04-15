package geogebra.web.util.ggtapi;

/**
 * @author gabor
 * Handles LoginRequests to GGTApi
 *
 */
public class LoginRequest implements Request {

	/**
	 * @param userName userName
	 * @param password pwd
	 */
	public LoginRequest(String userName, String password) {
	    // TODO Auto-generated constructor stub
    }

	public String toJSONString() {
	    return "{}";
    }

}
