package geogebra.web.helper;

public interface AsyncOperation {
	
	public void callback();
	
	public Object getData();
	
	public void setData(Object data);

}
