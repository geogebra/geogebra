depend y,x;
generic_function f(x,y);
df(f(),x);
df(f(x,y),x);
df(f(x,x**3),x);
df(f(x,z**3),x);
df(a*f(x,y),x);
dfp(a*f(x,y),x);
df(f(x,y),x,2);
df(dfp(f(x,y),x),x);
df(dfp(f(x,x**3),x),x);

% using a generic fucntion with commutative derivatives
generic_function u(x,y);
dfp_commute u(x,y);
df(u(x,y),x,x);

% explicitly declare 1st and second derivative commutative
generic_function v(x,y);
let dfp(v(~a,~b),{y,x}) => dfp(v(a,b),{x,y});
df(v(),x,2);

% substitute expressions for the arguments
w:=df(f(),x,2);
sub(x=0,y=x,w);

% composite generic functions
generic_function g(x,y);
generic_function h(y,z);
depend z,x;
w:=df(g()*h(),x);
sub(y=0,w);
% substituting g*h for f in a partial derivative of f,
% inheriting the arguments of f. Here no derivative of h
% appears because h does not depend of x.
sub(f=g*h,dfp(f(a,b),x));

% indexes.

% in the following total differential the partial
% derivatives wrt i and j do not appear because i and
% j do not depend of x.

generic_function m(i,j,x,y);
df(m(i,j,x,y),x);

% computation with a differential equation.

generic_function f(x,y);
operator y;
let df(y(~x),x) => f(x,y(x));

% some derivatives

df(y(x),x);
df(y(x),x,2);
df(y(x),x,3);
sub(x=22,ws);

% taylor expansion for y

load_package taylor;
taylor(y(x0+h),h,0,3);

clear w;

%------------------------ Runge Kutta -------------------------
% computing Runge Kutta formulas for ODE systems Y'=F(x,y(x));
% forms corresponding to Ralston Rabinowitz

load_package taylor;
operator alpha,beta,w,k;

% s= order of Runge Kutta formula

s:=3;  

generic_function f(x,y);
operator y;

% introduce ODE

let df(y(~x),x)=>f(x,y(x)); 

% formal series for solution

y1_form := taylor(y(x0+h),h,0,s);

% Runge-Kutta Ansatz:

let alpha(1)=>0;

for i:=1:s do
   let k(i) => h*f(x0 + alpha(i)*h, 
                  y(x0) + for j:=1:(i-1) sum beta(i,j)*k(j));
y1_ansatz:= y(x0) + for i:=1:s sum w(i)*k(i);

y1_ansatz := taylor(y1_ansatz,h,0,s);

% compute y1_form - y1_ans and collect coeffients of powers of h

y1_diff := num(taylortostandard(y1_ansatz)-taylortostandard(y1_form))$
cl := coeff(y1_diff,h);

% f_forms: forms of f and its derivatives which occur in cl

f_forms :=q := {f(x0,y(x0))}$
for i:=1:(s-1) do
  <<q:= for each r in q join {dfp(r,x),dfp(r,y)};
    f_forms := append(f_forms,q);
  >>;
f_forms;

% extract coefficients of the f_forms in cl

sys := cl$
for each fr in f_forms do
  sys:=for each c in sys join coeff(c,fr);
% and eliminate zeros
sys := for each c in sys join if c neq 0 then {c} else {};

end;
