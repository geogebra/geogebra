module decrep;  % Periodic Decimal Representation.

% Author: Lisa Temme

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


% Date:   August 1995.

algebraic;

% Procedure to check if an argument is a list.

procedure paarp(x); lisp eqcar(x,'list);

procedure tidy(u);
   % tidy {wholepart, {non_recursive_decimal_part},
   %    {recursive_decimal_part}}
   % to {{num_non_per_part, den_non_per_part}, {recursive_decimal_part}}
  begin
   scalar a, b, b1, c, num_non_per_part, den_non_per_part;

    a := part(u,1);
    b := part(u,2);
    c := part(u,3);  %recursive part

    if length(b)=0 then << b1 := 0 >>
    else b1 := digits2number(b);

%%match -ve value
    if a<0
    then num_non_per_part :=a*(10^length(b)) - b1
    else num_non_per_part :=a*(10^length(b)) + b1;

    den_non_per_part := 10^length(b);

    return list(list(num_non_per_part, den_non_per_part), c)
  end;

%******************

procedure digits2number(x);
%%convert an list of digits to a number
  begin
     scalar j, number, !*rounded, dmode!*;

     if x={} OR NOT(paarp x)  %check for empty list OR non-list
     then rederr
     "argument of digits2number should be list of non-negative digits";

     number := part(x,1);

     for j:=1:(length(x)-1) do
     <<
        if (numberp(part(x,j)) and part(x,j)>0 and part(x,j)<10)
           OR part(x,j)=0
        then number := number*10 + part(x,j+1)
        else rederr
       "argument of digits2number should be list of non-negative digits"
     >>;
     return number
  end;


procedure number2digits(n);
%%convert a number to a list of digits
  begin
    scalar number, list_of_ints, tmp, next_number, flg,
           !*rounded, dmode!*;

    flg := 0;

%%check for integer argument
    if NOT(fixp n) then
         rederr "argument must be an integer";

%%match -ve Input => -ve Output
    if n<0 then << flg:=1; n:=-n >>;

%%match zero Input => zero Output
    if n=0 then return {0}
           else list_of_ints := {};

    tmp := n;
    while tmp>0 do
       <<  next_number := remainder(tmp,10);
           list_of_ints := next_number.list_of_ints;
            tmp := (tmp - next_number)/10
       >>;

%%match -ve Input => -ve Output
    if flg=1
    then return append(list(- first list_of_ints), rest list_of_ints)
    else return list_of_ints;

%    if flg=1 then return list("-",list_of_ints)
%             else return list_of_ints;
  end;

%***********************
operator periodic;

procedure rational2periodic(xx);
%%gives periodic decimal representation of integer division
 %%check to see if rounded switch is off
  if lisp !*rounded
  then rederr "operator to be used in off rounded mode"
  else
  <<begin
      scalar n, m, z, n_repr, answer, numerator, wholepart,
             nexttryresult, result, numb, remd, negflg,
             !*rounded, dmode!*;

%%check for rational number argument
      if NOT(numberp xx)
      then rederr "argument must be a rational number";

      n := num xx;
      m := den xx;

%%match -ve Input => -ve Output
      negflg := 0;
      if xx<0
      then
        << n_repr := append(list(- first number2digits(n)),
                                    rest number2digits(n));
           negflg := negflg + 1
        >>
      else n_repr := number2digits(n);

%%calculate before decimal point
    answer := {};
    numerator := first(n_repr);
    while length(n_repr) >1 do
    <<
      n_repr := rest n_repr;
      answer := ((numerator - remainder(numerator, m))/m).answer;
      numerator := remainder(numerator, m) * 10 + first n_repr
    >>;
    answer := ((numerator - remainder(numerator, m))/m).answer ;
    wholepart := digits2number(reverse(answer));

%%calculate first decimal digit
    numerator := remainder(numerator,m)*10;
    numb := (numerator - remainder(numerator, m))/m;
    remd := remainder(numerator, m);
    z := {};
    numerator := remd*10;

%%calculate decimal part & check for recursion
    while length(nexttry(numb, remd, z)) neq 2
    do
    << z := {numb, remd}.z;
       numb := (numerator - remainder(numerator, m))/m;
       remd := remainder(numerator,m);
       numerator   := remd*10;
    >>;

%%nexttry returns either {} or {decimal_ans, recurrence}
    nexttryresult := nexttry(numb, remd, z);

%%put result in form
%% { {numerator non-periodic part, denominator non-periodic part},
%%                                                       {period} }

    %%match -ve Input => -ve Output
    if negflg neq 0
    then
      << if wholepart=0
         then
           << partresult := tidy(list(wholepart,
                                       first(nexttryresult),
                                       second(nexttryresult)));

              if length(first(nexttryresult))=0
              then
                << result := list(first  first partresult,
                                  - second first partresult).
                             rest partresult;
                >>
              else
                << result := list(-first  first partresult,
                                   second first partresult).
                             rest partresult
                >>;
           >>
         else
           << partresult := tidy(list(wholepart,
                                       first(nexttryresult),
                                       second(nexttryresult)));

              result := list(-first  first partresult,
                              second first partresult).
                        rest partresult
           >>;
      >>
    else
      << result := tidy(list(wholepart,
                             first(nexttryresult),
                             second(nexttryresult)))
      >>;

%return result;
    return periodic(first result, second result);
  end
  >>;


procedure nexttry(x,y,z);
%%compare {x,y} with z (the list of previous ordered pairs {x,y})
  begin
   scalar recurrence, decimal_ans, num_rem, ans, h, k, j,
          !*rounded, dmode!*; %added dmode!* here

     recurrence :={};
     decimal_ans := {};
     num_rem := {x,y};
     ans := {};
     h:=0;
     k := length(z);

%%look through z to see if {x,y} has already occured
     while (k>0 and h=0) do
     <<
        if num_rem = part(z,k) then
        <<
           for j := 1:k do recurrence  := first(part(z,j)).recurrence;
           for j := k+1:length(z)
                        do decimal_ans := first(part(z,j)).decimal_ans;
           h:=1;
        >>;
     k := k-1;
     if h=1 then ans := list(decimal_ans,recurrence)
     >>;

%%return list(decimal_ans,recurrence)
    return ans
  end;



operator periodic2rational;

%% Ruleset to allow two types of periodic input.
per2ratRULES :=
{
    periodic2rational(periodic(~x,~y)) => periodic2rational(x,y)
                                          when paarp x and length x=2
                                                       and paarp y,

    periodic2rational(~x,~y) => per2rat(x,y)
                                when paarp x and length x=2
                                             and paarp y

};
let per2ratRULES;



%% Procedure to convert a periodic representation to a rational one.
procedure per2rat(ab,c);
 %%check to see if rounded switch is off
  if lisp !*rounded
  then rederr "operator to be used in off rounded mode"
  else
  <<begin
      scalar a, b, number_c, power, fract;

      a := first ab;
      b := second ab;

      if a<0 then << a:=-a; b:=-b>>;

      if NOT(fixp b) OR
         ( (remainder(b,10) neq 0)  AND (b neq 1) AND (b neq -1) )
      then rederr "denominator must be 1, -1 or a multiple of 10";

      if length c = 0 then number_c = 0
      else number_c := digits2number c;

      power := length c;

      fract := a/b + 1/b*(number_c/10^power*(1/(1-1/10^power)));

      return fract

    end
  >>;

% printers

symbolic procedure print_periodic (u);

   if not(!*nat) or
        (length caddr u + 10) > (linelength nil) then 'failed else

   begin scalar oo,x,intpart,intstring,l1,l2,perio,minussign;

    intpart := cdr cadr u;
    if cadr intpart= (-1) then
        << minussign := t; intpart := list (car intpart, 1)>>;
    if car intpart < 0 then
        << minussign := t;
           intpart := list (-(car intpart),cadr intpart)>>;
    intstring :=  explode car intpart;
    l1 := length intstring;
    l2 := length explode cadr intpart;

    perio   := cdr caddr u;
    ycoord!* := ycoord!* +1;
    oo := posn!*;
    ymax!* := max(ymax!*,ycoord!*);
    x:= max(l1,l2);
    if minussign then x  := x + 1;
    for i:=0:x do prin2!* " ";
    x := for each q in perio sum length explode q;
    if not(caddr u = '(list 0)) then
            for i:=1:x do prin2!* "_";

    posn!* := oo;
    ycoord!* := ycoord!* -1;
    if minussign then prin2!* "-";

    if l1 < l2 then <<l2 := l2 -1; prin2!* "0.">> else
      while l1 > 0 do <<
        prin2!* car intstring;
        intstring := cdr intstring;
        l1 := l1 -1;
        if l1 < l2 then << l1 := 0; l2 := l2 -1;  prin2!* ".">>;
        >>;
    while l2 > 0 do <<
        if intstring then <<prin2!* car intstring;
                            intstring := cdr intstring;>>
                else prin2!* '!0;
        l2 := l2 -1 >>;

    if not(caddr u = '(list 0)) then for each q in perio do prin2!* q;
    return t;
end;

put('periodic,'prifn,'print_periodic);

endmodule;

end;

