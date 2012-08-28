
% The following rule triggered a "non-numeric argument in arithmetic" error
% in subf1.

frule := { int((~~a+~~b*~x)^~m*~f^((~~c+~~d*x)^~n),x) =>
   (a+b*x)^(m+1)*f^((c+d*x)^n)/(b*(m+1))
   - d*n*log(f)/(b*(m+1))*int((a+b*x)^(m+1)*f^((c+d*x)^n)*(c+d*x)^(n-1),x)  when fixp n and numberp m and n>1 and m<-1};

let frule;

clearrules frule;

end;

