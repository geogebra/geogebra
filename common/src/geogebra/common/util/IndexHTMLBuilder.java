package geogebra.common.util;


public class IndexHTMLBuilder {
	private StringBuilder sb;
	private boolean needsTag;
	public IndexHTMLBuilder(boolean addTag){
		this.sb = new StringBuilder();
		if(addTag){
			this.needsTag = true;
			sb.append("<html>");
		}
	}
	public void append(String s){
		sb.append(s);
	}
	public void startIndex(){
		sb.append("<sub><font size=\"-1\">");
	}
	public void endIndex(){
		sb.append("</font></sub>");
	}
	public String toString(){
		
		if(needsTag){
			needsTag = false;
			sb.append("</html>");
		}
		return sb.toString();
	}
	public void clear() {
		sb.setLength(needsTag ? "<html>".length() : 0);
	}
}
