package geogebra.common.main;

import java.util.ResourceBundle;

public abstract class AbstractApplication {
	public abstract ResourceBundle initAlgo2IntergeoBundle();
	public abstract ResourceBundle initAlgo2CommandBundle();
	public abstract String getCommand(String cmdName);
}
