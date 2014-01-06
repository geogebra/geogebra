package geogebra.common.util;

public abstract class AsyncOperation {
	protected Object data=null;
	
	public abstract void callback(Object obj);
	
	public Object getData(){
		return data;
	}
	
	public void setData(Object d){
		data = d;
	}

}
