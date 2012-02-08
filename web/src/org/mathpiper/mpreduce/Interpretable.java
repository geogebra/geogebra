package org.mathpiper.mpreduce;

import geogebra.common.cas.Evaluate;

import com.google.gwt.core.client.Scheduler.RepeatingCommand;

public interface Interpretable  extends Evaluate {

	String evaluate(String send);

	RepeatingCommand getInitializationExecutor();

	void initialize();

	String version();

}
