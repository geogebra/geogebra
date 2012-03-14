module defintd;

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


fluid '(mellincoef mellin!-coefficients!* mellin!-transforms!*);

% The following are needed by GAMMA.

load_package specfn,sfgamma;

symbolic procedure simpintgggg (u);

begin scalar ff1,ff2,alpha,var,chosen_num,coef,temp,const,result;

     u := defint_reform(u);
     const := car u;
     if const = 0 then result := nil . 1
     else
     << u := cdr u;
              if length u neq 4 then rederr  "Integration failed";
        if (car u) = 0 then ff1 := '(0 0 x)
        else ff1 := prepsq simp car u;
        if (cadr u) = 0 then ff2 := '(0 0 x)
        else  ff2 := prepsq simp cadr u;

        if (ff1 = 'UNKNOWN) then return simp 'unknown;
        if (ff2 = 'UNKNOWN) then return simp 'unknown;
        alpha := caddr u;
        var := cadddr u;

        if car ff1 = 'f31 or car ff1 = 'f32 then
                << put('f1,'g,spec_log(ff1)); MELLINCOEF :=1>>
        else
        << chosen_num := cadr ff1;
           put('f1,'g,getv(mellin!-transforms!*,chosen_num));
           coef := getv(mellin!-coefficients!*,chosen_num);
           if coef then MELLINCOEF:= coef else MELLINCOEF :=1>>;

        if car ff2 = 'f31 or car ff2 = 'f32 then
           put('f2,'g,spec_log(ff2))
        else
        << chosen_num := cadr ff2;
           put('f2,'g,getv(mellin!-transforms!*,chosen_num));
           coef := getv(mellin!-coefficients!*,chosen_num);
           if coef then MELLINCOEF:= coef * MELLINCOEF >>;

        temp :=  simp list('intgg,'f1 . cddr ff1,
                                                        'f2 . cddr ff2,alpha,var);

        temp := prepsq temp;

        if temp neq 'unknown then
            result := reval algebraic(const*temp)

        else result := temp;
         result := simp!* result; >>;
      return result;
end;

symbolic procedure spec_log(ls);

begin scalar n,num,denom,mellin;

  n := cadr ls;
  num := for i:= 0 :n collect 1;
  denom := for i:= 0 :n collect 0;

  if car ls = 'f31 then
    mellin := {{}, {n+1,0,n+1,n+1},num,denom, (-1)^n*factorial(n),'x}
   else mellin := {{}, {0,n+1,n+1,n+1},num,denom, factorial(n),'x};
return mellin;
end;

 % some rules which let the results look more convenient ...

algebraic <<

 for all z let sinh(z) = (exp (z) - exp(-z))/2;
 for all z let cosh(z) = (exp (z) + exp(-z))/2;

operator laplace2,Y_transform2,K_transform2,struveh_transform2,
               fourier_sin2,fourier_cos2;

gamma_rules :=

{ gamma(~n/2+1/2) => gamma(n)/(2^(n-1)*gamma(n/2))*gamma(1/2),
  gamma(~n/2+1) => n/2*gamma(1/2*n),
  gamma(3/4)*gamma(1/4) => pi*sqrt(2),
  gamma(~n)*~n/gamma(~n+1) => 1
};

let gamma_rules;

factorial_rules := {factorial(~a) => gamma(a+1) };
let factorial_rules;

>>;

% A function to calculate laplace transforms of given functions via
% integration of Meijer G-functions.

put('laplace_transform,'psopfn,'new_laplace);

symbolic procedure new_laplace(lst);

begin scalar new_lst;
lst := product_test(lst);
new_lst := {'laplace2,lst};
return defint_trans(new_lst);
end;

% A function to calculate hankel transforms of given functions via
% integration of Meijer G-functions.

put('hankel_transform,'psopfn,'new_hankel);

symbolic procedure new_hankel(lst);

begin scalar new_lst;
lst := product_test(lst);
new_lst := {'hankel2,lst};
return defint_trans(new_lst);
end;

% A function to calculate Y transforms of given functions via
% integration of Meijer G-functions.

put('Y_transform,'psopfn,'new_Y_transform);

symbolic procedure new_Y_transform(lst);

begin scalar new_lst;
lst := product_test(lst);
new_lst := {'Y_transform2,lst};
return defint_trans(new_lst);
end;

% A function to calculate K-transforms of given functions via
% integration of Meijer G-functions.

put('K_transform,'psopfn,'new_K_transform);

symbolic procedure new_K_transform(lst);

begin scalar new_lst;
lst := product_test(lst);
new_lst := {'K_transform2,lst};
return defint_trans(new_lst);
end;


% A function to calculate struveh transforms of given functions via
% integration of Meijer G-functions.

put('struveh_transform,'psopfn,'new_struveh);

symbolic procedure new_struveh(lst);

begin scalar new_lst,temp;
        lst := product_test(lst);
        new_lst := {'struveh2,lst};
        temp:=defint_trans(new_lst);
        return defint_trans(new_lst);
end;

% A function to calculate fourier sin transforms of given functions via
% integration of Meijer G-functions.

put('fourier_sin,'psopfn,'new_fourier_sin);

symbolic procedure new_fourier_sin(lst);

begin scalar new_lst;
lst := product_test(lst);
new_lst := {'fourier_sin2,lst};
return defint_trans(new_lst);
end;

% A function to calculate fourier cos transforms of given functions via
% integration of Meijer G-functions.

put('fourier_cos,'psopfn,'new_fourier_cos);

symbolic procedure new_fourier_cos(lst);

  begin scalar new_lst;
        lst := product_test(lst);
        new_lst := {'fourier_cos2,lst};
        return defint_trans(new_lst);
  end;

% A function to test whether the input is in a product form and if so
% to rearrange it into a list form.

% e.g. defint(x*cos(x)*sin(x),x) => defint(x,cos(x),sin(x),x);

symbolic procedure product_test(lst);

begin scalar temp;
product_tst := nil;
if listp car lst then
<< temp := caar lst;
   if temp = 'times and length cdar lst <= 3 then
   << lst := append(cdar lst,cdr lst); product_tst := t>>;
>>;
return lst;
end;

% A function to call the relevant transform's rule-set

symbolic procedure defint_trans(lst);

begin scalar type,temp_lst,new_lst,var,n1,n2,result;

% Set a test to indicate that the relevant conditions for validity
% should not be tested

        algebraic(transform_tst := t);

        spec_cond := '();
        type := car lst; % obtain the transform type
        temp_lst := cadr lst;
        var := nth(temp_lst,length temp_lst);
        new_lst := hyperbolic_test(temp_lst);

        if length temp_lst = 3 then
                << n1 := car new_lst;
                      n2 := cadr new_lst;
                      result := reval list(type,n1,n2,var)>>
         % Call the relevant rule-set

else if length temp_lst = 2 then
<< n1 := car new_lst;
   result := reval list(type,n1,var)>> % Call the relevant rule-set

        else if length temp_lst = 4 and product_tst = 't then
        << n1 := {'times,car new_lst,cadr new_lst};
           n2 := caddr new_lst;
           result := reval list(type,n1,n2,var)>>

        else << algebraic(transform_tst := nil);
                rederr "Wrong number of arguments">>;

return result;
end;

% A function to test for hyperbolic functions and rename them
% in order to avoid their transformation into combinations of
% the exponenetial function

%symbolic procedure hyperbolic_test(lst);
% begin scalar temp,new_lst,lth;
% lth := length lst;
% for i:=1 :lth do
% << temp := car lst;
%    if listp temp and (car temp = 'difference or car temp = 'plus) then
%        temp := hyperbolic_test(temp)
%    else if listp temp and car temp = 'sinh then car temp := 'mysinh
%    else if listp temp and car temp = 'cosh then car temp := 'mycosh;
%    new_lst := append(new_lst,{temp});
%   lst := cdr lst>>;
%return new_lst;
%end;

symbolic procedure hyperbolic_test lst;
   for each u in lst collect
      if atom u then u
       else if car u memq '(difference plus) then hyperbolic_test u
       else if car u eq 'sinh then 'mysinh . cdr u
       else if car u eq 'cosh then 'mycosh . cdr u
       else u;

endmodule;

end;



