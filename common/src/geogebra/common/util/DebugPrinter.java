package geogebra.common.util;

public interface DebugPrinter {
	void print(String s,String info, int level);
	void getTimeInfo(StringBuilder sb);
	void getMemoryInfo(StringBuilder sb);
}
