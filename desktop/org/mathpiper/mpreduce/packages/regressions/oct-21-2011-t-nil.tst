operator fun;

for all t,f,nt,nf let fun( t,f,nt,nf) =
fun_t * t + fun_f * f + fun_nt * nt + fun_nf * nf + fun_0
$

x := fun( 1,b,c,d) ;
y := fun( a,b,c,d) ;

share t;

symbolic procedure ws(u); u;

symbolic procedure nil(u); u;

algebraic procedure nil(u); u;

for all t let fun(t,t^2)=t;

for all nil let fun(nil,nil) = nil;

showrules fun;

for all t clear fun(t,t^2);

showrules fun;

for all nil clear fun(nil,nil);

showrules fun;

fun(t);

fun(nil);

% test empty variable list in declaration

begin scalar; return 0 end;

% test binding nil or t

begin scalar nil; return nil end;

begin scalar t; return t end;

(lambda(t); 0)(a);

(lambda(nil,t); 0)(a,b);

algebraic procedure x1(t);t;

x1(1);

algebraic procedure x2(t); begin scalar nil; return (t+nil); end;

x2(a);

x2(1);

%% next line commented out sine it crashes CSL
%x2(nil);

algebraic procedure y1(t); begin integer nil; return (t+nil); end;

y1(1);

y1(nil);

algebraic procedure z1(t,u); begin t := t + u^2; return t; end;

z1(1,2);

z1(a,b);

t(u);

clear t;

algebraic procedure t(x); x;

t(u);

t(t);

t(nil);

algebraic procedure nil(x); x;

nil(u);

nil(t);

nil(nil);

end; 
