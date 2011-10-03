package geogebra.gui.virtualkeyboard;

public class keys {
   private String UpperCase   = null;
   private String LowerCase   = null;

   
   /**
    * This method returns a ready keys, just hand him (LowerCase, UpperCase)
    *
    * @return keys
    */
   public keys setKeys(String LowerCase, String UpperCase) {
      this.LowerCase = LowerCase;
      this.UpperCase = UpperCase;
      return this;
   }

   /**
    * This method returns LowerCase
    *
    * @return java.lang.String
    */

   public String getUpperCase() {
      return this.UpperCase;
   }

   /**
    * This method sets UpperCase
    *
    * @return void
    */

   public void setUpperCase(String UpperCase) {
      this.UpperCase = UpperCase;
   }

   
   /**
    * This method returns LowerCase
    *
    * @return java.lang.String
    */

   public String getLowerCase() {
      return this.LowerCase;
   }

   /**
    * This method sets UpperCase
    *
    * @return void
    */

   public void setLowerCase(String LowerCase) {
      this.LowerCase = LowerCase;
   }
}
