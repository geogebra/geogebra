off exp; off mcd;
mrv_limit(e^x,x,infinity);

ex:=log(log(x)+log(log(x)))-log(log(x));
ex:=ex/(log(log(x)+log(log(log(x)))));
ex:=ex*log(x);

mrv_limit(e^-x,x,infinity);

mrv_limit(log(x),x,infinity);

mrv_limit(1/log(x),x,infinity);

a:=e^(1/x-e^-x)-e^(1/x);
a:=a/e^(-x);
 
mrv_limit(a,x,infinity) ;          % all of these are correct
mrv_limit(e^-x,x,infinity) ;
mrv_limit(log(x),x,infinity) ;
mrv_limit(1/log(x),x,infinity) ;

a:=e^(1/x-e^-x)-e^(1/x);
a:=a/e^(-x);
b:=e^x*(e^(1/x-e^-x)-e^(1/x));

%c:=e^x*(e^(1/x+e^(-x)+e^(-x^2))-e^(1/x-e^(-e^x)))
maxi1({e^(-x^2)},{e^x});

cc:= e^(log(log(x+e^(log(x)*log(log(x)))))/log(log(log(e^x+x+log(x)))));
 
b:=e^x*(e^(1/x-e^-x)-e^(1/x));

c:=e^x*(e^(1/x+e^(-x)+e^(-x^2))-e^(1/x-e^(-e^x)));

e^(log(log(x+e^(log(x)*log(log(x)))))/(log(log(log(e^x+x+log(x))))));

%% mrv_limit(ws,x,infinity);

aa:=e^(e^(e^x));
bb:=e^(e^(e^(x-e^(-e^x))));
ex1:=(e^x)*(e^((1/x)-e^(-x))-e^(1/x));  % returns -1 correct

ex2:=(e^x)*(e^((1/x)-e^(-x)-e^(-x^2))-e^((1/x)-e^(-e^x))); % returns infinity

ex3:=e^(e^(x-e^-x)/(1-1/x))-e^(e^x); % returns - infinity

ex4:=e^(e^((e^x)/(1-1/x)))-e^(e^((e^x)/(1-1/x-(log(x))^(-log(x)))));

ex5:=(e^(e^(e^(x+e^-x))))/(e^(e^(e^x)));

ex6:=(e^(e^(e^x)))/(e^(e^(e^(x-e^(-e^x)))));   

ex7:=(e^(e^(e^x)))/(e^(e^(e^(x-e^(e^x)))));       

ex8:=(e^(e^x))/(e^(e^(x-e^(-e^(e^x)))));

ex9:=((log(x)^2)*e^(sqrt(log(x))*((log(log(x)))^2)*e^((sqrt(log(log(x))))*(log(log(log(x)))^3))))/sqrt(x);

ex10:=((x*log(x))*(log(x*e^x-x^2))^2)/(log(log(x^2+2*e^(3*x^3*log(x)))));

misc1:=1/(e^(-x+e^-x))-e^x; % returns -1 correct

misc2:=(e^(1/x-e^-x)-e^(1/x))/(e^-x); % returns -1 correct

misc3:=e^(-log(x+e^-x)); % returns 0 correct

misc4:=e^(x-e^x); % returns 0 correct

% bb limit is infinity correct

mrv_limit(ex,x,infinity); %1

mrv_limit(ex1,x,infinity); % -1

%% mrv_limit(ex2,x,infinity); % -1

%% mrv_limit(b,x,infinity); % -1

mrv_limit(a,x,infinity); 

%% mrv_limit(ex3,x,infinity);

%% mrv_limit(ex4,x,infinity);

%% mrv_limit(ex5,x,infinity);  % 0

%% mrv_limit(ex6,x,infinity);

mrv_limit(misc1,x,infinity);

mrv_limit(misc2,x,infinity);

mrv_limit(misc3,x,infinity);

mrv_limit(misc4,x,infinity);

end;
