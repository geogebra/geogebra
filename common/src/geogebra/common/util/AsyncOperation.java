package geogebra.common.util;

public interface AsyncOperation {
	
	public void callback();
	
	public Object getData();
	
	public void setData(Object data);

}
