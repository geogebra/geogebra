package javagiac;
// Command line interface to run Giac statements via JNI.
// Usage on command line:
// 1. Compile it: "javac *.java".
// 2. Run it: "cd ..; java -Djava.library.path=lib javagiac/minitest '1+2*(3+4)'

public class minitest {
  static {
    try {
	System.out.println("Loading giac java interface...");
		System.loadLibrary("javagiac64");
    } catch (UnsatisfiedLinkError e) {
    	e.printStackTrace();
      System.err.println("Native code library failed to load. See the chapter on Dynamic Linking Problems in the SWIG Java documentation for help.\n" + e);
      System.exit(1);
    }
  }

  public static void main(String argv[]) 
  {
    context C=new context();
    String s = new String(argv[0]);
    gen g=new gen(s,C);
    g=g.eval(1,C);
    System.out.println(g.print(C));
  }
}
