package geogebra.common.main;

public class BracketsError extends MyError {

	public BracketsError(Localization loc, String errorName) {
		super(loc, new String[]{"UnbalancedBrackets",errorName});
		
	}

}
