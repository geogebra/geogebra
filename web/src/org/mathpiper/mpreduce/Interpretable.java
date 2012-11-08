package org.mathpiper.mpreduce;

import geogebra.common.cas.Evaluate;
import geogebra.common.main.App;

import com.google.gwt.core.client.Scheduler.RepeatingCommand;

public interface Interpretable  extends Evaluate {

	String evaluate(String send);

	RepeatingCommand getInitializationExecutor(App app);

	void initialize();

	String version();

}
