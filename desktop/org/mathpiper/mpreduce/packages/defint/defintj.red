module defintj;

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


flag('(mylessp),'boolean);

algebraic procedure mylessp(a,b);

%
% Test the validity of the following :-
%
%      a < b*pi
%

begin scalar !*rounded,mydiff;

if transform_tst neq 't then

<< on rounded;
%   if a < b*pi then << off rounded; return t>>
   mydiff := a - b*pi;
   if numberp mydiff and mydiff < 0  then << off rounded; return t>>
   else << off rounded; return nil>>;
>>
else << transform_mylessp(); return t>>;
end;


symbolic procedure transform_mylessp();

  begin scalar temp,cond_test;

  temp := lispeval '(list 'lessp (list 'mod (list 'arg 'eta))
                                (list 'times 'pi 'delta));
  if listp spec_cond then
        for each i in spec_cond do  if i = temp then cond_test := t;
  if cond_test neq t then spec_cond := temp . spec_cond;
end;


symbolic operator transform_mylessp;

flag('(arg_test),'boolean);

algebraic procedure arg_test(a,b);

%
% Test the validity of the following :-
%
%      a = (b + 2*k)*pi    k arbitrary integer
%

begin scalar temp;

if transform_tst neq t then
<< temp := (a - b*pi)/(2*pi); temp := symbolic (fixp temp);
   if temp = t  then return t else return nil>>
else << transform_arg_test(); return t>>;
end;

symbolic procedure transform_arg_test();

begin scalar temp,cond_test;

temp := lispeval '(list 'equal (list 'arg 'eta) (list 'times
                        (list 'plus 'delta (list 'times 2 'k)) 'pi));

if listp spec_cond then
 for each i in spec_cond do  if i = temp then cond_test := t;
if cond_test neq t then spec_cond := temp . spec_cond;
end;

symbolic operator transform_arg_test;

flag('(arg_test1),'boolean);

algebraic procedure arg_test1(a,b);

%
% Test the validity of the following :-
%
%      a = (b - 2*k)*pi    k = 0,1,....,[b/2]
%

begin scalar temp,int_test;

temp := (a - b*pi)/(-2*pi);
int_test := symbolic (fixp temp);

if int_test = t and  temp <= b/2 and temp >= 0 then return t
else return nil;
end;

flag('(arg_test2),'boolean);

algebraic procedure arg_test2(a,b);

% Test the validity of the following :-
%
%      a = b*pi     b > 0

if transform_tst neq t then
        if a/(b*pi) = 1 and b > 0 then t else nil
else
<< transform_arg_test2(); t>>;

symbolic procedure transform_arg_test2();

begin scalar temp,cond_test;

temp := lispeval '(list 'equal (list 'mod (list 'arg 'eta))
                                (list 'times 'pi 'delta));
if pairp spec_cond then

<< for each i in spec_cond do
   << if i = temp then cond_test := 't>>; >>;

if cond_test neq 't then spec_cond := temp . spec_cond;
end;

symbolic operator transform_arg_test2;

flag('(arg_test3),'boolean);

algebraic procedure arg_test3(a,b);

%
% Test the validity of the following :-
%
%      a = (b + 2*k)*pi     k >= 0 or k <= -(1 + b)  k an integer
%

begin scalar temp,int_test;
if transform_tst neq 't then

<< temp := (a - b*pi)/(2*pi);
   int_test := symbolic (fixp temp);

   if int_test = 't and (temp >= 0 or temp <= -(1+b)) then
       return 't else return nil>>
else << transform_arg_test3(); return 't>>;
end;

flag('(arg_test3a),'boolean);

algebraic procedure arg_test3a(a,b);

% Test the validity of the following :-
%
%      a = b*pi     b >= 0

if transform_tst neq t then

<< if a - b*pi = 0 then t else nil>>
else << transform_arg_test3(); t>>;

symbolic procedure transform_arg_test3();

begin scalar temp,cond_test;

temp := lispeval '(list 'equal (list 'arg 'eta) (list 'plus 'm
   (list 'difference 'n (list 'times (list 'quotient 1 2)
   (list 'plus 'p 'q) 'pi))));

if listp spec_cond then
 for each i in spec_cond do if i = temp then cond_test := t;
if cond_test neq t then spec_cond := temp . spec_cond;
end;

symbolic operator transform_arg_test3;

flag('(arg_test4),'boolean);

algebraic procedure arg_test4(a,b);

% Test the validity of the following :-
%
%      (b + 2*k - 1)*pi < a < (b + 2*k)*pi     k arbitrary integer

begin scalar l1,l2,new_l1,new_l2;

l1 := (a - b*pi)/(2*pi);
new_l1 := ceiling(l1);

if new_l1 = l1 then new_l1 := new_l1 + 1;

l2 := (a - b*pi + pi)/(2*pi);
new_l2 := floor(l2);
if new_l2 = l2 then new_l2 := new_l2 - 1;
if new_l1 = new_l2 then return 't else return nil;
end;

flag('(arg_test5),'boolean);

algebraic procedure arg_test5(a,b,xi);

% Test the validity of the following :-
%
%      (b + 2*k)*pi <= a < (b + 2*k + 1)*pi    -xi < k < 0 k an integer

begin scalar l1,l2,new_l2;

l1 := floor((a - b*pi)/(2*pi));
l2 := (a - b*pi - pi)/(2*pi);
new_l2 := ceiling(l2);
if l1 = new_l2 and l1 < 0 and -xi < l1 then return t else return nil;
end;

flag('(arg_test6),'boolean);

algebraic procedure arg_test6(a,b,xi);

% Test the validity of the following :-
%
%      a = (b + 2*k - 1)*pi     1-xi < k < 1   k an integer

begin scalar l,int_test;

l := (a - b*pi + pi)/(2*pi);
int_test := symbolic (fixp l);
if int_test = t and l < 1 and l > 1 - xi then return t else return nil;
end;

flag('(arg_test6a),'boolean);

algebraic procedure arg_test6a(a,b,xi);

% Test the validity of the following :-
%
%      a = (b + 2*k - 1)*pi     1-xi <= k <= 0

begin scalar l,int_test;

l := (a - b*pi + pi)/(2*pi);
int_test := symbolic (fixp l);
if l <= 0 and l >= 1 - xi then return t else return nil;
end;

flag('(arg_test7),'boolean);

algebraic procedure arg_test7(a,b,xi);

% Test the validity of the following :-
%
%      a = (b + 2*k)*pi     k >= 0 or k <= -xi   k an integer

begin scalar temp,int_test;

temp := (a - b*pi)/(2*pi);
int_test := symbolic (fixp temp);
if int_test=t and (temp >= 0 or temp <= -xi) then return t
else return nil;
end;

flag('(arg_test8),'boolean);

algebraic procedure arg_test8(a,b);

% Test the validity of the following :-
%
%      a = (b + 2*k - 1)*pi     k arbitrary integer

begin scalar temp,int_test;

temp := (a - b*pi + pi)/(2*pi);
int_test := symbolic (fixp temp);
if int_test = t then return t else return nil;
end;

flag('(arg_test8a),'boolean);

algebraic procedure arg_test8a(a,b,xi);

% Test the validity of the following :-
%
%      a = (b + 2*k - 1)*pi     k >= 1 k <= 1 - xi  k an integer

begin scalar temp,int_test;

temp := (a - b*pi + pi)/(2*pi);
int_test := symbolic (fixp temp);
if int_test = t and (temp >= 1 or temp <= 1 - xi) then return t
else return nil
end;

flag('(arg_test9),'boolean);

algebraic procedure arg_test9(a,b);

% Test the validity of the following :-
%
%      (b + 2*k - 2)*pi < a < (b + 2*k)*pi     k arbitrary

begin scalar l1,l2,new_l1,new_l2;

l1 := (a - b*pi)/(2*pi);
new_l1 := ceiling(l1);
if new_l1 = l1 then new_l1 := new_l1 + 1;
l2 := (a - b*pi + 2*pi)/(2*pi);
new_l2 := floor(l2);
if new_l2 = l2 then new_l2 := new_l2 - 1;
if new_l1 = new_l2 then return t else return nil;
end;

flag('(arg_test9a),'boolean);

algebraic procedure arg_test9a(a,b);

% Test the validity of the following :-
%
%      (b + 2*k - 2)*pi < a < (b + 2*k)*pi     1 - b <= k <= 0
%                                                       k arbitrary

begin scalar l1,l2,new_l1,new_l2;

l1 := (a - b*pi)/(2*pi);
new_l1 := ceiling(l1);
if new_l1 = l1 then new_l1 := new_l1 + 1;
l2 := (a - b*pi + 2*pi)/(2*pi);
new_l2 := floor(l2);
if new_l2 = l2 then new_l2 := new_l2 - 1;
if new_l1 = new_l2 and (1 - b <= new_l1 or new_l1 <= 0) then
    return t else return nil;
end;

symbolic procedure transform_test2(n1,n2);

begin scalar lst,temp,cond_test;

if transform_tst neq t then return t else
<< if n1 then temp := lispeval cdr assoc(n1,transform_lst) . temp;
   if n2 then temp := lispeval cdr assoc(n2,transform_lst) . temp;

   temp := 'and . temp;
   for each j in spec_cond do  if j = temp then cond_test := t;
   if cond_test neq t then spec_cond := temp . spec_cond;
   return nil;
>>;
end;

symbolic operator transform_test2;

endmodule;
end;



