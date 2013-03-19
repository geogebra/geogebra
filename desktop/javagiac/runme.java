package javagiac;
// -*- compile-command: "javac *.java" -*-
// This example illustrates how giac can be used from Java using SWIG.
// On linux type java runme, on mac os x with giac 32 bits, java -d32 runme

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class runme {
  static {
    try {
	System.out.println("Loading giac java interface");
        //System.load("c:\\cygwin\\usr\\local\\lib\\javagiac.dll");
        System.loadLibrary("javagiac");
    } catch (UnsatisfiedLinkError e) {
    	e.printStackTrace();
      System.err.println("Native code library failed to load. See the chapter on Dynamic Linking Problems in the SWIG Java documentation for help.\n" + e);
      System.exit(1);
    }
  }

  public static void main(String argv[]) 
  {
    context C=new context();
    String s=new String("");
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    System.out.print( "Enter an expression :" );
    try {
	s = br.readLine();
    } catch (IOException ioe) {
	System.out.println("IO error trying to read your name!");
	System.exit(1);
    }
    gen g=new gen(s,C);
    // gen g=new gen(10,12);
    // gen g=new gen("x**4-1",C);
    System.out.println( "Created gen of type : "+g.getType());
    if (g.getType()==gen_unary_types._SYMB.swigValue()){
	System.out.println( "g operator is "+g.operator_at(0,C).print(C));
	System.out.println( "g has "+giac._size(g,C).getVal()+" arguments");
	System.out.println( "First argument of g is "+g.operator_at(1,C).print(C));
	System.out.println( "XXX"+g.print(C));
    }
    gen h=giac._factor(g,C);

    System.out.println( "Factor: " + h.print(C) );

    h=giac.add(g,g);
    h=giac._simplify(h,C);
    
    
    
    System.out.println( "Value of h: " + h.print(C) );
    // h=new gen(giac.makevecteur(h,new gen(2)),(short)1);
    h=new gen(giac.makevecteur(h,new gen(2),h),(short)gen_comp_subtypes._SEQ__VECT.swigValue());
    System.out.println( "Value of h: " + h.print(C) );

    System.out.println( "Goodbye" );
  }
}
