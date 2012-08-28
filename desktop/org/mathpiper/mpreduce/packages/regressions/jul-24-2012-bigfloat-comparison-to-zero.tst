comment 

 Test for sf.net bug #3547914 and fix to greaterp!:

end;

on numval, rounded;

logb(2^(-100),2);
logb(2^(-101),2);
logb(3^(-33),3);

on roundbf; 

logb(2^(-100),2);
logb(2^(-101),2);
logb(3^(-33),3);

off roundbf;

logb(2^(-100),2);
logb(2^(-101),2);
logb(3^(-33),3);

end;
