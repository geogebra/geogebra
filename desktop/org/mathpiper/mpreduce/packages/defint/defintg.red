module defintg;

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


fluid '(!*precise);

symbolic procedure print_conditions;

<< if spec_cond neq nil then mathprint ('or . spec_cond) else
            rederr "Conditions not valid";
        spec_cond := nil;
>>;

symbolic operator print_conditions;

symbolic procedure defint_reform(n);

% A function to rearrange the input to the integration process by
% expanding out multiple powers of the exponential function i.e.
%
%     2               2
%    x + x + 1       x     x
%   e            => e  * e  * e
%

begin scalar n,var,vble,const,result,reform_test,temp_result,
                reform_lst,lst,new_lst,res,coef,new_coef;

% test if integral needs to be reformed

on exp;
coef := 1;

var := caddar n;
const := caddr n;
vble := cadddr n;

% test to see if any part of the integral needs reforming

for each i in n do

<< if eqcar(i,'defint_choose) then

% test for integrals of a single function multiplied by a constant

   << if i neq '(defint_choose e x) and numberp cadr i
                and cadr i neq 0 then

      << new_coef := cadr i;
         coef := reval algebraic(coef*new_coef);
         n := const_case(n)>>

% special case for integration of 0

      else if i = '(defint_choose 0 x) then coef := 0

% test for special case of integral of e
      else if i = '(defint_choose e x) then
           coef := reval algebraic(e*coef)

      else if eqcar(cadr i,'expt) then

      << reform_test := 't;
% Form a list of the functions which must be reformed
         reform_lst := append(reform_lst,{i})>>

      else if eqcar(cadr i, 'quotient)

% don't reform special compound functions which are represented as a
% single Meijer G-function

                and (listp cadadr i and car cadadr i neq 'm_chebyshevt
                        or not listp cadadr i) then

      << reform_test := 't;
% Form a list of the functions which must be reformed
         reform_lst := append(reform_lst,{i})>>

      else if eqcar(cadr i, 'times) then

      << if listp car cddadr i
                  and (caar cddadr i = 'm_chebyshevu
                  or caar cddadr i = 'm_jacobip

% do not reform functions containing the heaviside function

                  or car cadadr i = 'heaviside)
     then
          lst := append(lst,{i})  % A list of the functions which do
                                % not need reforming

         else if listp cdr cddadr i and cdr cddadr i neq 'nil
                        and listp cadr cddadr i
                           and caadr cddadr i = 'm_gegenbauerp
     then
          lst := append(lst,{i})  % A list of the functions which do
                                % not need reforming
         else << reform_test := 't;
% Form a list of the functions which must be reformed
            reform_lst := append(reform_lst,{i});>>
      >>
      else lst := append(lst,{i});  % A list of the functions which do
                                    % not need reforming
   >>;
>>;

if reform_test = nil then << n := coef . n; return n>>
else

<< for each i in reform_lst do

   << new_lst := cadr i;
      if car new_lst = 'expt and cadr new_lst = 'e then
          res := reform_expt(new_lst,var)
      else if car new_lst = 'times then
          res := reform_const(new_lst,var)
      else if car new_lst = 'quotient and cadr new_lst = 1 then
          res := reform_denom(new_lst,var)
      else if car new_lst = 'quotient then
          res := reform_quot(new_lst,var);
      new_coef := car res;
      coef := reval algebraic(coef*new_coef);
      res := cdr res;
      temp_result := append(temp_result,res);
   >>;

   temp_result := coef . temp_result;
   result := append(temp_result,lst);

if lst = nil and length result = 2 then result := append(result,{0});
   result := append(result,{const});
   result := append(result,{vble});
   return result;
>>;

end;


% A function to rearrange the integral if it contains exponentials of
% only positive numbers and there is no constant term

symbolic procedure reform_expt(n,var);

begin scalar temp,coef,lst;

% test for exponentials which do not need reforming i.e. e^x

if not listp n then
<< lst := {{'defint_choose,n,var}}; lst := 1 . lst>>

else if listp caddr n neq t then

<< if numberp caddr n then coef := n . coef
         else lst := {{'defint_choose,n,var}}; >>

else if car caddr n eq 'quotient and eqcar(cadr caddr n,'plus)
        and not depends(caddr caddr n,var)
   then 
     % quotient of sum by constant
     n := 'plus .
         for each term in cdr cadr caddr n collect {'quotient,term,caddr caddr n}

else if not(caaddr n eq 'plus) then lst := {{'defint_choose,n,var}}

else
<<  temp := cdaddr n;
   for each i in temp do
   << if not depends(i,var) then coef := {'expt,'e,i} . coef
       else lst := ({'defint_choose,{'expt,'e,i},var} . lst)>>;
>>;
if null coef then coef := 1
 else if null cdr coef then coef := car coef
 else coef := 'times . reverse coef;
lst := coef . lst;
return lst;
end;


% A function to rearrange the integral if the exponential is multiplied
% by a constant term

symbolic procedure reform_const(n,var);

begin scalar temp,coef,lst,temp1;

temp := n;

coef := caddr temp;
temp := cadr temp;

if temp neq nil and car temp = 'expt and (atom caddr temp or
                        caaddr temp neq 'plus) then
<< lst := {{'defint_choose,{'expt,'e,caddr temp},var}}>>
else
<< temp1 := cdaddr temp;
   for each i in temp1 do
   << lst := ({'defint_choose,{'expt,'e,car temp1},var} . lst);
      temp1 := cdr temp1>>;
>>;
if coef neq nil then lst := coef . lst else lst := 1 . lst;
return lst;
end;

% A function to rearrange the integral if all the exponential powers
% are negative powers

symbolic procedure reform_denom(n,var);
   begin scalar temp,coef,lst,temp1;
      % argument n is of the form (quotient 1 some_expr), extract some_expr
      temp := caddr n;
      if not depends(temp,var) then return (n . nil);
      % if the function contains e^n where n is a number than this can
      % be taken outside the integral as a constant.
      if not(eqcar(temp,'expt) or eqcar(temp,'times))
                        then return list(1,list('defint_choose,n,var));
      % case product
      if eqcar(temp,'times) then <<
              for each fctr in cdr temp do
                 if depends(fctr, var) then temp1 := fctr . temp1
                  else coef := fctr . coef;
	      temp := if cdr temp1 then 'times . reverse temp1 else car temp1 >>;
      if eqcar(temp,'expt) and not (cadr temp eq 'e)
        then if depends(cadr temp, var)
               then lst := {{'defint_choose,{'quotient,1,temp}},var}
              else temp := {'expt,'e,{'times,{'log,cadr temp},caddr temp}};
      % test for a single occurrence of e.
      if temp and eqcar(caddr temp ,'quotient)
        then if eqcar(cadr caddr temp,'plus) and not depends(caddr caddr temp,var)
              then temp := {'expt,cadr temp,
                            'plus . for each term in cdr cadr caddr temp collect
                                   {'quotient,term,caddr caddr temp}}
              else if listp car cdaddr temp and listp cadr cdaddr temp then
      << off mcd; temp:= {'expt,'e,quotient_case(reval temp)}; on mcd>>;
      if temp and car temp eq 'expt and (atom caddr temp or
                              caaddr temp neq 'plus) then
      <<lst := {{'defint_choose,
                   {'quotient,1,{'expt,'e,caddr temp}},var}}>>
      % else if there are multiple occurrences of e
      else if pairp caddr temp then
%      else if listp temp and length temp > 2 and pairp caddr temp then
      << temp1 := cdaddr temp;
         for each i in temp1 do
           if not depends(i,var) then coef := {'expt,'e,i} . coef
            else lst:=({'defint_choose,{'quotient,1,{'expt,'e,i}},var} . lst)>>;
      if coef then
         if not null cdr coef then coef := 'times . reverse coef
          else coef := car coef;
  a:  return if coef then lst := ({'quotient,1,coef} . lst)
              else lst := 1 . lst
   end;

% A function to rearrange the integral if the exponential consists of
% both positive and negative powers

symbolic procedure reform_quot(n,var);

begin scalar num,denom,num_coef,denom_coef,lst,num1,denom1;

num := cadr n;
denom := caddr n;

% Check for constants

if fixp num or atom num then << num_coef := num; num := nil>>

else if num = 'e or fixp caddr num then
        << num_coef := num; num := nil>>

else if car num  = 'times then
        << for each fctr in cdr num do
             if depends(fctr,var) then num1 := fctr . num1
              else num_coef := fctr . num_coef;
           if null num_coef then num_coef := 1
            else if cdr num_coef then num_coef := 'times . reverse num_coef
            else num_coef := car num_coef;
           if num then
              num := if cdr num then 'times . reverse num else car num >>;

if fixp denom or atom denom then
        << denom_coef := denom; denom := nil>>

else if denom = 'e or fixp caddr denom then
           << denom_coef := denom; denom := nil>>

else if car denom  = 'times then 
        << for each fctr in cdr denom do
             if depends(fctr,var) then denom1 := fctr . denom1
              else denom_coef := fctr . denom_coef; 
           if denom_coef then
              denom_coef := if cdr denom_coef then 'times . reverse denom_coef
                             else car denom_coef;
           if denom then
              denom := if cdr denom1 then 'times . reverse denom1 else car denom1>>;
   
if denom and car denom = 'expt and (atom caddr denom or
                        caaddr denom neq 'plus) then
     lst := {{'defint_choose,{'quotient,1,
                {'expt,'e,caddr denom}},var}}

else if denom then

<< denom1 := cdaddr denom;
%  for each i in denom1 do
%  << lst := ({'defint_choose,{'quotient,1,
%               {'expt,'e,car denom1}},var} . lst);
%     denom1 := cdr denom1>>;
   for each i in denom1 do
     lst := ({'defint_choose,{'quotient,1,
                {'expt,'e,i}},var} . lst)>>;

if not atom num and car num = 'expt and (atom caddr num or
                        caaddr num neq 'plus) then

    lst := {'defint_choose,{'expt,'e,caddr num},var} . lst

else if not atom num then

<< num1 := cdaddr num;
   for each i in num1 do
   << lst := ({'defint_choose,{'expt,'e,car num1},var} . lst);
      num1 := cdr num1>>;
>>;

if null denom_coef then lst := (num_coef . lst)

else 
    lst := ({'quotient,num_coef,denom_coef} . lst);

return lst;
end;

symbolic procedure const_case(n);

begin scalar n,new_n;
for i := 0 :length n do
<< if not listp car n or listp car n and not numberp cadar n then
       new_n := append(new_n,{car n}); n := cdr n>>;

new_n := append(new_n,{0});
new_n := append(new_n,n);
return new_n;
end;

symbolic procedure quotient_case(n);

begin scalar lst,new_lst;

lst := cdaddr n;
new_lst := {caaddr n};

for each i in lst do
<< if caddr i < 0 then
   << caddr i := minus caddr i;
      i := {car i,cadr i, {'minus,caddr i}}>>;
  new_lst := append(new_lst,{i});
>>;
return new_lst;
end;

put('transf,'simpfn,'simpinteg);
% put('indefint,'psopfn,'new_indefint);

symbolic procedure new_indefint(lst);
   begin scalar var,y,n1,n2,result,!*precise;
      if eqcar(car lst,'times)
        then return new_indefint append(cdar lst,cdr lst);
      result := 'unknown;  %%%%%% This line is new %%%%%%%
      var := nth(lst,length lst - 1);
      y := nth(lst,length lst);
      lst := hyperbolic_test(lst);
      if length lst = 4 then << n1 := car lst; n2 := cadr lst;
              result := reval algebraic indefint2(n1,n2,var,y)>>
      else if length lst = 3 then << n1 := car lst;
              result := reval algebraic indefint2(n1,var,y)>>;
      return result
   end;

endmodule;

end;
