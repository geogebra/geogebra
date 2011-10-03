package org.mathpiper.builtin.javareflection;

import org.mathpiper.lisp.LispError;


/**
   Error routines.
**/

public class E {

  /** Throw an error message with an associated object. **/
  public static Object error(String message, Object x) throws Exception {
      //throw new SchemeException(message,x);
      LispError.raiseError(message, "", -2, null);
      return null;
  }
  

  public static Object error(String message) throws Exception{
    return  error(message,null);
  }

  /** Call error, complaining that we got the wrong type. **/
  public static Object typeError(String type, Object x) throws Exception{
    return error("expected object of type " + type + ", but got: ", x);
  }

  /** Print a warning. **/
  public static Object warn(String message) {
    //Scheme.currentEvaluator().getError().println("** WARNING: " + message);
    return message;
  }
  
   /** Print a warning. **/
  public static Object warn(String message, Object x) {
    return warn(message + shortStringify(x));
  }

  /** It's nice to get an error, but not one large enough to choke EMACS. **/
  public static String shortStringify(Object x) {
    //String s = U.stringify(x);
    //if (s.length() > 1000) return s.substring(0,1000) + "...";
    //return s; todo:tk.
      return "";
  }

}
