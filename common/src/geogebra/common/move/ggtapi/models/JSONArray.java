package geogebra.common.move.ggtapi.models;

public class JSONArray extends JSONValue {

	@Override
	public String jsonToString() {
		 StringBuffer sb = new StringBuffer();
		    sb.append("[");
		    for (int i = 0, c = size(); i < c; i++) {
		      if (i > 0) {
		        sb.append(",");
		      }
		      sb.append(get(i));
		    }
		    sb.append("]");
		    return sb.toString();
	}

	private int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	private Object get(int i) {
		// TODO Auto-generated method stub
		return null;
	}

}
