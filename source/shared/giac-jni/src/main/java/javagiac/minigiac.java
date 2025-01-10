package javagiac;
// Command line interface to run Giac statements via JNI as a filter.

import java.util.Scanner;

public class minigiac {
  static {
    try {
	System.loadLibrary("javagiac");
    } catch (UnsatisfiedLinkError e) {
    	e.printStackTrace();
        System.exit(1);
    }
  }

  public static void main(String [] args) throws java.io.IOException
  {
    context C=new context();
    int n = 1;
    gen g;

    String line;
    Scanner stdin = new Scanner(System.in);
    while(stdin.hasNextLine() && ! (line = stdin.nextLine()).equals( "" )) {
	System.out.println(n + ">> " + line);
	g=new gen(line,C).eval(1,C);
	System.out.println(n + "<< " + g.print(C));
	n++;
        }
    stdin.close();
    }
}
