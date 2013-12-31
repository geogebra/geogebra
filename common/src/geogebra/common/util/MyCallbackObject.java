package geogebra.common.util;

public abstract class MyCallbackObject {
	public abstract void process(Object ret);

	public void setField(){
		//overridden in inherited classes if needed
	};
}
