package geogebra.common.move.ggtapi.models;

/**
 * @author gabor
 * Handles LoginRequests to GGTApi
 *
 */
public class LoginRequest implements Request {

	private String userName;
	private String password;

	/**
	 * @param userName userName
	 * @param password pwd
	 */
	public LoginRequest(String userName, String password) {
		this.userName = userName;
		this.password = password;
    
	}

	public String toJSONString() {
	    return "{" +
	    		"" +
	    		"" +
	    		"" +
	    		"}";
    }

}
