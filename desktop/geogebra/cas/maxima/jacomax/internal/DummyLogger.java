package geogebra.cas.maxima.jacomax.internal;

import geogebra.cas.maxima.jacomax.MaximaConfiguration;
import geogebra.common.main.AbstractApplication;
import geogebra.main.Application;

import java.io.File;
import java.io.InputStream;
import java.util.List;



public class DummyLogger {

	final static boolean trace = false;
	
	public void trace(String string) {
		if (trace) AbstractApplication.debug(string);
		
	}

	public void info(String string, Exception e) {
		if (trace) AbstractApplication.debug(string);
		
	}

	public void info(String string) {
		if (trace) AbstractApplication.debug(string);
		
	}

	public void debug(String string) {
		if (trace) AbstractApplication.debug(string);
		
	}

	public void trace(String string, String string2) {
		if (trace) AbstractApplication.debug(string+" "+string2);
		
	}

	public boolean isTraceEnabled() {
		return trace;
	}

	public void trace(String string, int bytesReadFromCallInput) {
		if (trace) AbstractApplication.debug(string);
		
	}

	public void debug(String string, Integer valueOf) {
		if (trace) AbstractApplication.debug(string+" "+valueOf);
		
	}

	public void trace(String string, boolean maximaStdinFinished, Object object) {
		if (trace) AbstractApplication.debug(string);
		
	}

	public void trace(String string, Object[] objects) {
		if (trace) AbstractApplication.debug(string);
		
	}

	public void debug(String string, File programFilesFolder) {
		if (trace) AbstractApplication.debug(string);
		
	}

	public void info(String string, MaximaConfiguration result) {
		if (trace) AbstractApplication.debug(string);
		
	}

	public void debug(String string, String location) {
		if (trace) AbstractApplication.debug(string+" "+location);
		
	}

	public void warn(String string) {
		if (trace) AbstractApplication.debug(string);
		
	}

	public void info(String string, String path) {
		if (trace) AbstractApplication.debug(string+" "+path);
		
	}

	public void info(String string, InputStream propertiesStream) {
		if (trace) AbstractApplication.debug(string);
		
	}

	public void info(String string, String callInput, int callTimeout) {
		if (trace) AbstractApplication.debug(string+" "+callInput+" "+callTimeout);
		
	}

	public void debug(String string, String maximaInput,
			String callTerminatorOutput) {
		if (trace) AbstractApplication.debug(string);
		
	}

	public boolean isInfoEnabled() {
		return trace;
	}

	public void info(String string, List<String> maximaCommandArray,
			String string2) {
		if (trace) AbstractApplication.debug(string+" "+string2);
		
	}

}
