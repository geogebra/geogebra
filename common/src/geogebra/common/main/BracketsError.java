package geogebra.common.main;

public class BracketsError extends MyError {

	public BracketsError(App app, String errorName) {
		super(app, new String[]{"UnbalancedBrackets",errorName});
		
	}

}
