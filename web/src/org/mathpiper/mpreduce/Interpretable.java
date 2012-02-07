package org.mathpiper.mpreduce;

import com.google.gwt.core.client.Scheduler.RepeatingCommand;

public interface Interpretable {

	String evaluate(String send);

	RepeatingCommand getInitializationExecutor();

	String initialize();

	String version();

}
