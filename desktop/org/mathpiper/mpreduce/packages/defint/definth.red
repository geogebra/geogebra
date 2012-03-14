module definth;

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%


% Modification by WN after the call to GFMSQ, resubsitute.

fluid '(mellin!-transforms!* mellin!-coefficients!*);

symbolic smacro procedure listsq(u);
 % u - list of PF.
 % value is list of SQ.
 for each uu in u collect simp!* uu;

algebraic <<

operator indefint2;

indefint2_rules :=

{ indefint2((~f1+~~f2)/~~f3,~x,~y) =>
              indefint2(f1/f3,x,y)+indefint2(f2/f3,x,y) when not(f2=0),

  indefint2(~n,~f1-~f2,~x,~y) =>
                                   indefint2(n,f1,x,y)-indefint2(n,f2,x,y),
  indefint2(~n,~f1+~f2,~x,~y) =>
                                   indefint2(n,f1,x,y)+indefint2(n,f2,x,y),

  indefint2(1/~x^(~~a),~f,~x,~y) => transf(defint_choose(f,x),-a,y,x),
  indefint2(~x^(~~b)*sqrt(~x),~f,~x,~y) =>
                 transf(defint_choose(f,x),b+1/2,y,x),
  indefint2(sqrt(~x),~f,~x,~y) =>
                         transf(defint_choose(f,x),1/2,y,x),
  indefint2(~x^(~~a),~f,~x,~y) => transf(defint_choose(f,x),a,y,x),

  indefint2(~b*~ff,~f,~x,~y) => b*indefint2(ff,f,x,y) when freeof (b,x),
  indefint2(~b/(~~c*~ff),~f,~x,~y) => b/c*indefint2(1/ff,f,x,y)
                when freeof (b,x) and freeof (c,x) and not(b=1 and c=1),
  indefint2(~ff/~b,~f,~x,~y) =>
     1/b*indefint2(ff,f,x,y) when freeof (b,x),

  indefint2(~b*~ff,~f,~x,~y) => b*indefint2(ff,f,x,y) when freeof (b,x),
  indefint2(~ff/~b,~f,~x,~y) =>
     1/b*indefint2(ff,f,x,y) when freeof (b,x),

  indefint2(~~b,~f,~x,~y) => b*indefint2(f,x,y)
                         when freeof (b,x) and not(b=1),
  indefint2(~f,~x,~y) => transf(defint_choose(f,x),0,y,x)
};

let indefint2_rules;

symbolic procedure simpinteg(u);

begin scalar ff1,alpha,y,var,chosen_num,coef,!*uncached;

!*uncached := t;
ff1 := prepsq simp car u;

if ff1 = 'UNKNOWN then return simp 'UNKNOWN;
alpha := cadr u;
y := caddr u;

if smember('minus,y) then return simp 'UNKNOWN;
                       % until a fix is available
var := cadddr u;
chosen_num := cadr ff1;

if chosen_num = 0 then << coef := caddr ff1;
                          return simp reval
                                algebraic(coef*y**(alpha+1)/(alpha+1))>>
else
<< put('f1,'g,getv(mellin!-transforms!*,chosen_num));
   coef := getv(mellin!-coefficients!*,chosen_num);
   if coef then MELLINCOEF:= coef else MELLINCOEF :=1;

   return  simp list('new_mei,'f1 . cddr ff1,alpha,y,var)>>;
end$

put('new_mei,'simpfn,'new_meijer)$

symbolic procedure new_meijer(u);

begin scalar f,y,mellin,new_mellin,m,n,p,q,old_num,old_denom,temp,a1,
b1,a2,b2,alpha,num,denom,n1,temp1,temp2,coeff,v,var,new_var,new_y,
new_v,k;

f := prepsq simp car u;
y := caddr u;

mellin := bastab(car f,cddr f);

temp := car cddddr mellin;
var := cadr f;

if not idp VAR then RETURN error(99,'FAIL); % something is rotten, if not...
                                   % better give up

temp := reval algebraic(sub(x=var,temp));

mellin := {car mellin,cadr mellin,caddr mellin,cadddr mellin,temp};

temp := reduce_var(cadr u,mellin,var);

alpha := simp!* car temp;
new_mellin := cdr temp;

if car cddddr new_mellin neq car cddddr mellin then
        << k := car cddddr mellin;
           y := reval algebraic(sub(var=y,k));
           new_y := simp y>>
else
<< new_var := car cddddr new_mellin;
   new_y := simp reval algebraic(sub(x=y,new_var))>>;

n1 := addsq(alpha,'(1 . 1));

temp1 := {'expt,y,prepsq n1};
temp2  := cadddr new_mellin;

coeff := simp!* reval algebraic(temp1*temp2);

m := caar new_mellin;
n := cadar new_mellin;
p := caddar new_mellin;
q := car cdddar new_mellin;

old_num := cadr new_mellin;
old_denom := caddr new_mellin;


for i:=1 :n do
<< if old_num = nil then a1 := append(a1,{simp!* old_num })
   else <<  a1 := append(a1,{simp!* car old_num});
            old_num := cdr old_num>>;
>>;

for j:=1 :m do

<< if old_denom = nil then b1 := append(b1,{simp!*  old_denom })
   else <<  b1 := append(b1,{simp!* car old_denom});
            old_denom := cdr old_denom>>;
>>;

a2 := listsq old_num;
b2 := listsq old_denom;

if a1 = nil and a2 = nil then
    num := list({negsq alpha})
else if a2 = nil then num := list(append(a1,{negsq alpha}))
else
<< num := append(a1,{negsq alpha}); num := append({num},a2)>>;

if b1 = nil and b2 = nil then
    denom := list({subtrsq(negsq alpha,'(1 . 1))})
else if b2 = nil then
    denom := list(b1,subtrsq(negsq alpha,'(1 . 1)))
else
<< denom := list(b1,subtrsq(negsq alpha,'(1 . 1)));
   denom := append(denom,b2)>>;

v := gfmsq(num,denom,new_y);

if v = 'fail then return simp 'fail
else v := prepsq subsq(v,list(prepsq new_y . y));  % WN

if eqcar(v,'meijerg) then new_v := v else new_v := simp v;
return multsq(new_v,coeff);
end$


symbolic procedure reduce_var(u,v,var1);

% Reduce Meijer G functions of powers of x to x

begin scalar var,m,n,coef,alpha,beta,alpha1,alpha2,expt_flag,k,temp1,
            temp2,const,new_k;

var := car cddddr(v);
beta := 1;

% If the Meijer G-function is is a function of a variable which is not
% raised to a power then return initial function

if length var = 0 then return u . v
else
<< k := u; coef := cadddr v;
   for each i in var do
   << if listp i then
      << if car i = 'expt then
         << alpha := caddr i; expt_flag := 't>>

         else if car i = 'sqrt then
         << beta := 2; alpha := 1; expt_flag := 't>>

         else if car i = 'times then
         << temp1 := cadr i; temp2 := caddr i;

            if listp temp1 then
            << if  car temp1 = 'sqrt then
               << beta := 2; alpha1 := 1; expt_flag := 't>>

               else if car temp1 = 'expt and listp caddr temp1 then
                  << beta := cadr cdaddr temp1;
                     alpha1 := car cdaddr temp1;
                     expt_flag := 't>>;
            >>;

            if listp temp2 and car temp2 = 'expt then
            << alpha2 := caddr temp2; expt_flag := 't>>;

            if alpha1 neq 'nil then

     alpha := reval algebraic(alpha1 + beta*alpha2)
            else alpha := alpha2;
         >>;
      >>
      else
      << if i = 'expt then << alpha := caddr var; expt_flag := 't>>;
      >>;
   >>;

% If the Meijer G-function is is a function of a variable which is not
% raised to a power then return initial function

   if expt_flag = nil then return u . v

% Otherwise reduce the power by using the following formula :-
%
%   y                            (c*y)^(m/n)
%  /                                 /
%  |                           n     |
%  |t^k*F((c*t)^(m/n))dt = --------- |z^[((k + 1)*n - m)/m]*F(z)dz
%  |                       m*c^(k+1) |
%  /                                 /
% 0                                 0

   else

   << if listp alpha then << m := cadr alpha; n := caddr alpha;
                             n := reval algebraic(beta*n)>>

      else << m := alpha; n := beta>>;

      const := reval algebraic(sub(var1=1,var));
      const := reval algebraic(1/(const^(n/m)));

      new_k := reval algebraic(((k + 1)*n - m)/m);
      coef := reval algebraic((n/m)*coef*(const)^(k+1));

      var := reval algebraic(var^(n/m));
      return {new_k,car v,cadr v, caddr v,coef,var}>>;
>>;
end$

>>;

endmodule;
end;
