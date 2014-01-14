package geogebra.common.util;

import java.util.HashMap;

public abstract class AsyncOperation {
	protected Object data=null;
	protected HashMap<String, Object> properties = new HashMap<String, Object>();
	
	
	public abstract void callback(Object obj);
	
	public Object getData(){
		return data;
	}
	
	public void setData(Object d){
		data = d;
	}
	
	
	public void setProperty(String propertyName, Object prop){
		this.properties.put("propertyName", prop);
	}
	
	public Object getProperty(String propertyName){
		return this.properties.get("propertyName");
	}

}
