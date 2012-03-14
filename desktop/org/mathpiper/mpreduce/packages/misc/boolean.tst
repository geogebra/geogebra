% Test series for the boolean package.

boolean true;
boolean false;
boolean (true and false);
boolean (true or false);
boolean (x and true);
boolean (x and false);
boolean (x or true);
boolean (x or false);
boolean (not(x and y));
boolean (not(x or y));
boolean (x or y or(x and y));
boolean (x and y and (x or y));
boolean (x or (not x));
boolean (x and (not x));
boolean (x and y or not x);
boolean (a and b implies c and d);
boolean (a and b implies c and d, and);
boolean (a or b implies c or d);
boolean (a or b implies c or d, and,full); 

operator >;
fm:=boolean(x>v or not (u>v));
v:=10;
testbool fm;
x:=3;
testbool fm;
clear x;
x:=17;
testbool fm;
clear v,x;

end;
