module simpfact;

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


%  Simplification for quotients containing factorials

%  Matthew Rebbeck ( while in placement at ZIB) - March 1994.

%  The new 'really' improved version! Simplifies plain factorials as
%  well as those raised to integer powers and 1/2.
%
%  Deals properly with the generalised factorial idea of simplifying
%  non integers, eg: (k+1/2)!/(k-1/2)! -> k+1/2.

algebraic <<
operator simplify_factorial1;
operator simplify_factorial;
operator int_simplify_factorial;

let simplify_factorial(~x) => simplify_factorial1(num x,den x);

let { simplify_factorial1(~x,~z) => int_simplify_factorial(x/z)};

let { simplify_factorial1 (~x + ~y,~z) =>
      simplify_factorial1 (x,z) + simplify_factorial1(y,z)};
>>;

symbolic procedure int_Simplify_factorial (u);
  begin
    scalar minus_num,minus_denom,test_expt;
    if not pairp u or car u neq 'quotient then u
    else
    <<
      %
      %  We firstly produce input of standard form.
      %
      if atom cadr u or atom caddr u then u
       else <<
        %
        %  Remove 'minus if there.
        %
        if car cadr u eq 'minus then
         << cadr u := cadr cadr u;
            minus_num := t; >>;
        if car caddr u eq 'minus then
         << caddr u := cadr caddr u;
            minus_denom := t; >>;

        if car cadr u eq 'factorial then cadr u := {'times,cadr u};
        if car caddr u eq 'factorial then caddr u := {'times,caddr u};
        if car cadr u eq 'oddexpt or car cadr u eq 'expt
        or car cadr u eq 'sqrt
         then cadr u := {'times,cadr u};
        if car caddr u eq 'oddexpt or car caddr u eq 'expt or
        car caddr u eq 'sqrt
         then caddr u := {'times,caddr u};
        %
        %  Test to see if input contains any 'expt's. If it does
        %  then they are converted to 'oddexpts and re converted
        %  at the end. If not (ie: either contains 'oddexpt's or
        %  no powers at all), then no conversion is done and the
        %  output is left in this oddexpt form.
        %
        if test_for_expt(cadr u) or test_for_expt(caddr u) then
        << test_expt := t;
           convert_to_oddexpt(cadr u);
           convert_to_oddexpt(caddr u); >>;

        if test_for_facts(cadr u,caddr u)
         then gothru_numerator(cadr u,caddr u);

        if minus_num then cadr u := {'minus,cadr u};
        if minus_denom then caddr u := {'minus,caddr u};

        cadr u := reval cadr u;
        caddr u := reval caddr u; >>;
        %
        %  Output converted back to 'expt form regardless of the form
        %  of the input. For this conversion to occur only if input
        %  is in 'expt form (perhaps useful with Wolfram's input)
        %  then uncomment next line...
        %if test_expt then
        u := algebraic sub(oddexpt=expt,u);
      >>;
      return u;
  end;

flag('(int_Simplify_factorial),'opfn);



symbolic procedure test_for_expt(input);
  %
  % Tests to see if 'expt occurs anywhere.
  %
  begin
    scalar found_expt,not_found;
    not_found := t;
    while input and not_found do
    <<
      if pairp car input and (caar input = 'expt or caar input = 'sqrt)
       then <<found_expt:=t; not_found:=nil;>>;
      input := cdr input;
    >>;
    return found_expt;
  end;

flag('(test_for_expt),'boolean);



symbolic procedure convert_to_oddexpt(input);
  %
  % Converts all expt's to standard form. ie: oddexpt(......,power).
  %
  begin
    while input do
    <<
      if pairp car input and caar input = 'expt
       then caar input := 'oddexpt;
      if pairp car input and caar input = 'sqrt then
      <<
        caar input := 'oddexpt;
        cdar input := {cadar input,{'quotient,1,2}};
      >>;
      input := cdr input;
    >>;
   end;



symbolic procedure gothru_numerator(num,denom);
  %
  % Go systematically through numerator, searching for factorials, and,
  % when  found,  comparing  with  denominator.  'change'  describes if
  % simplifications have been made or not (ie:change eq 0).
  %
  begin
    scalar change,orignum,origdenom;
    change := 0;
    orignum := num;
    origdenom := denom;
    %
    % while in numerator.
    %
    while num do
    <<
      if pairp car num and caar num eq 'oddexpt then
      <<
        if pairp cadar num and caadar num eq 'factorial then
        change := change + gothru_denominator(num,denom);
      >>
      else if pairp car num and caar num eq 'factorial then
      <<
        change := change + gothru_denominator(num,denom);
      >>;
      num := cdr num;
    >>;
    %
    %  If at end of numerator but simplifications have been made,
    %  then repeat.
    %
    if not num and not eqn(change,0) then
    <<
      if test_for_facts(orignum,origdenom)
       then gothru_numerator(orignum,origdenom);  %  Beginning.
    >>;
  end;



symbolic procedure gothru_denominator(num,denom);
  %
  % Systematically  goes  through  denominator  finding  factorials and
  % passing numerator and denom. factorials  into  oddexpt_test.  There
  % they  are  simplified  if  possible.  'Compared'  describes  if the
  % factorials were  simplified (ie: car test eq ok) or if  it was  not
  % possible.
  %
  begin
    scalar test,change;
    change := 0;

    while denom and change = 0 do
    <<
      if pairp car denom and caar denom eq 'oddexpt then
      <<
        if pairp cadar denom and caadar denom eq 'factorial then
        <<
          test := oddexpt_test(num,denom,change);
          change := change + test;
        >>;
      >>
      else if pairp car denom and caar denom eq 'factorial then
      <<
        test := oddexpt_test(num,denom,change);
        change := change + test;
      >>;
      denom := cdr denom;
    >>;
    return change;
  end;



symbolic procedure oddexpt_test(num,denom,change);
  %
  % Tests  which parts of quotient, (if any), are exponentials, passing
  % the quotient onto the relevant simplifying function.
  %
  begin
    scalar test;

    if caar num eq 'oddexpt and caar denom neq 'oddexpt then
    << test := compare_numoddexptfactorial(num,denom,change); >>
    else if caar num neq 'oddexpt and caar denom eq 'oddexpt then
    << test := compare_denomoddexptfactorial(num,denom,change); >>
    else if caar num eq 'oddexpt and caar denom eq 'oddexpt then
    << test := compare_bothoddexptfactorial(num,denom,change); >>
    else test := compare_factorial(num,denom,change);

    return test;
  end;



symbolic procedure compare_factorial (num,denom,change);
  %
  % Compares factorials, simplifying if possible.
  %
  begin
    scalar numsimp,denomsimp,diff;

    %  If both factorial arguments are of the same form.
    if numberp (reval list('difference,cadar (num),cadar(denom))) then
    <<
      change := change + 1;
      %  Difference between num. and denom. factorial arguments.
      diff :=(reval list('difference,cadar (num),cadar(denom)));
      %  If argument of num. factorial > argument of denom. factorial.
      if diff >0 then
      <<
        %  numsimp collects simplified numerator arguments.
        numsimp := for i := 1:diff collect reval {'plus,cadar denom,i};
        %  Remove num. factorial and replace with simplification.
        car num := 'times.numsimp;
        %  Remove denom. factorial.
        car denom := 1;
      >>
      else %  if diff <= 0 then
      <<
        diff := -diff;
        denomsimp := for i := 1:diff collect reval {'plus,cadar num,i};
        car denom := 'times.denomsimp;
        car num := 1;
      >>;
    >>;
    return change;
  end;



symbolic procedure compare_numoddexptfactorial (num,denom,change);
  %
  % Compares  factorials with oddexpt num., simplifying if possible.See
  % compare_factorial for more detailed comments.
  %
  begin
    scalar diff;

    if numberp (reval list('difference,car cdadar num,cadar denom))
    then
    <<
      %  New sqrt additions...
      if sqrt_test(num) then
      <<
        <<
          diff :=(reval list('difference, car cdadar num,cadar denom));
          change := change+1;
          if diff > 0 then simplify_sqrt1(num,denom,diff)
           else simplify_sqrt2(num,denom,diff);
        >>;
      >>
      %  If power is not integer or 1/2 then can't simplify.
      else if not_int_or_sqrt(num) then <<>>
      %  If oddexpt. of power 2.
      else if  eqn(caddar num-1,1) then
      <<
        %  Remove oddexpt.
        car num := car {cadar num};
        diff := (reval list('difference,cadar num,cadar denom));
        change :=  change +1;
        if diff > 0 then << simplify1(num,denom,diff); >>
        else simplify2(num,denom,diff);
      >>
      else
      <<
        %  Reduce oddexpt by one.
        car num := {caar num,cadar num,car cddar num -1};
        diff :=(reval list('difference,car cdadar num,cadar denom));
        change :=  change + 1;
        if diff >0 then << simplify1(num,denom,diff); >>
        else simplify2(cdar num,denom,diff);
      >>;
    >>;
    return change;
  end;



symbolic procedure simplify_sqrt1(num,denom,diff);
  begin
    scalar numsimp;
    numsimp := for i := 1:diff collect reval {'plus,cadar denom,i};
    cadar num := car{'times.numsimp};
    car denom := {'oddexpt,car denom,{'quotient,1,2}};
  end;



symbolic procedure simplify_sqrt2(num,denom,diff);
  begin
    scalar denomsimp;
    diff := -diff;
    denomsimp := for i := 1:diff collect reval {'plus,car cdadar num,i};
    car denom := reval {'times,car num,car{'times.denomsimp}};
    car num := 1;
  end;



symbolic procedure simplify1(num,denom,diff);
  begin
    scalar numsimp;
    numsimp := for i := 1:diff collect reval {'plus,cadar denom,i};
    cdr num := car{'times.numsimp}.cdr num;
    car denom := 1;
  end;



symbolic procedure simplify2(num,denom,diff);
  begin
    scalar denomsimp;
    diff := -diff;
    denomsimp := for i := 1:diff collect reval {'plus,cadar num,i};
    cdr denom := car{'times.denomsimp}.cdr denom;
    car denom := 1;
  end;



symbolic procedure compare_denomoddexptfactorial (num,denom,change);
  %
  % Compares factorials with oddexpt denom, simplifying if possible.See
  % compare_factorial and compare_numoddexptfactorial for more detailed
  % comments.
  %
  begin
    scalar change,diff;

    if numberp (reval list('difference, cadar num,car cdadar denom))
    then
    <<
      %  New sqrt additions...
      if sqrt_test(denom) then
      <<
        <<
          diff :=(reval list('difference, cadar num,car cdadar denom));
          change := change+1;
          if diff > 0 then simplify_sqrt3(num,denom,diff)
          else  %  if diff <= 0
          simplify_sqrt4(num,denom,diff);
        >>;
      >>
      else if not_int_or_sqrt(denom) then <<>>
      else if eqn(caddar denom-1,1) then
      <<
        car denom := car {cadar denom};
        diff := (reval list('difference,cadar num,cadar denom));
        change := change +1;
        if diff > 0 then simplify3(num,denom,diff)
        else  %  if diff <= 0 then
        simplify4(num,denom,diff);
      >>
      else
      <<
        car denom := {caar denom,cadar denom,car cddar denom -1};
        diff :=(reval list('difference, cadar num,car cdadar denom));
        change := change + 1;
        if diff >0 then simplify3(num,cdar denom,diff)
        else simplify4(num,denom,diff);
      >>;
    >>;
    return change;
  end;



symbolic procedure sqrt_test(input);
  %
  % tests if the expt power is 1/2. (boolean)
  %
  begin
    if caddar input = '(quotient 1 2) then return t
     else return nil;
  end;

flag('(sqrt_test),'boolean);



symbolic procedure not_int_or_sqrt(input);
  %
  % tests if the expt power is neither int or 1/2. (boolean)
  %
  begin
    if pairp caddar input and car caddar input = 'quotient
    and cdr caddar input neq '(1 2) then return t
     else return nil;
  end;

flag('(not_int_or_sqrt),'boolean);



symbolic procedure simplify_sqrt3(num,denom,diff);
  begin
    scalar numsimp;
    numsimp := for i := 1:diff collect reval {'plus,car cdadar denom,i};
    car num := reval{'times,car denom,car{'times.numsimp}};
    car denom := 1;
  end;



symbolic procedure simplify_sqrt4(num,denom,diff);
  begin
    scalar denomsimp;
    diff := -diff;
    denomsimp := for i := 1:diff collect reval {'plus,cadar num,i};
    if diff = 0 then car denom := 1
     else cadar denom := car{'times.denomsimp};
    car num := {'oddexpt,car num,{'quotient,1,2}};
  end;



symbolic procedure simplify3(num,denom,diff);
  begin
    scalar numsimp;
    numsimp := for i := 1:diff collect reval {'plus,cadar denom,i};
    cdr num := car{'times.numsimp}.cdr num;
    car num := 1;
  end;



symbolic procedure simplify4(num,denom,diff);
  begin
    scalar denomsimp;
    diff := -diff;
    denomsimp := for i := 1:diff collect reval {'plus,cadar num,i};
    cdr denom := car{'times.denomsimp}.cdr denom;
    car num := 1;
  end;



symbolic procedure compare_bothoddexptfactorial (num,denom,change);
  %
  % Compares factorials with both oddexpt num. & denom., simplifying if
  % possible. See previous compare_...... functions  for  more detailed
  % comments.
  %
  begin
    scalar change,diff;

    if numberp(reval list('difference,car cdadar num,car cdadar denom))
    then
    <<
      %  New sqrt additions...
      if sqrt_test(num) and sqrt_test(denom) then
      <<
        <<
          diff :=(reval list('difference, car cdadar num,car cdadar
                  denom));
          change := change+1;
          if diff > 0 then simplify_sqrt5(num,denom,diff)
           else  %  if diff <= 0
           simplify_sqrt6(num,denom,diff);
        >>;
      >>
      else if not_int_or_sqrt(num) or not_int_or_sqrt(denom) then <<>>
      %  If denom is sqrt but num is not.
      else if sqrt_test(denom) then
      <<
        diff := reval list('difference,cadr cadar num,cadr cadar denom);
        if diff > 0 then simplify_sqrt5(num,denom,diff)
         else  %  if diff <= 0 then
         simplify_sqrt6(num,denom,diff);
      >>
      %  If num is sqrt but denom is not.
      else if sqrt_test(num) then
      <<
        diff := reval list('difference,cadr cadar num,cadr cadar denom);
        if diff > 0 then simplify_sqrt7(num,denom,diff)
         else  %  if diff <= 0 then
         simplify_sqrt8(num,denom,diff);
      >>
      else if eqn(caddar num-1,1) and eqn(caddar denom-1,1) then
      <<
        car num := car {cadar num};
        car denom := car {cadar denom};
        diff := (reval list('difference,cadar num,cadar denom));
        change := change +1;
        if diff > 0 then simplify5(num,denom,diff)
         else  %  if diff <= 0 then
         simplify6(num,denom,diff);
      >>
      else if eqn(caddar num-1,1) and not eqn(caddar denom-1,1) then
      <<
        car num := car {cadar num};
        car denom := {caar denom,cadar denom,car cddar denom-1};
        diff := (reval list('difference,cadar num,car cdadar denom));
        change := change +1;
        if diff >0 then simplify5(num,cdar denom,diff)
         else  %  if diff <= 0 then
         simplify6(num,denom,diff);
      >>
      else if caddar num-1 neq 1 and caddar denom-1 eq 1 then
      <<
        car num := {caar num,cadar num,car cddar num-1};
        car denom := car {cadar denom};
        diff := (reval list('difference,car cdadar num,cadar denom));
        change := change +1;
        if diff >0 then simplify5(num,denom,diff)
        else simplify6(cdar num,denom,diff);
      >>
      else if caddar num-1 neq 1 and caddar denom-1 neq 1 then
      <<
        car num := {caar num,cadar num,car cddar num-1};
        car denom := {caar denom,cadar denom,car cddar denom-1};
        diff:=(reval list('difference,car cdadar num,car cdadar denom));
        change := change +1;
        if diff >0 then simplify5(num,cdar denom,diff)
        else simplify6(cdar num,denom,diff);
      >>;
    >>;
    return change;
  end;



symbolic procedure simplify_sqrt5(num,denom,diff);
  begin
    scalar numsimp;
    numsimp := for i := 1:diff collect reval {'plus,car cdadar denom,i};
    car num := {'times,{'oddexpt,cadar denom,{'plus,caddar num,
               {'minus,{'quotient,1,2}}}},{'oddexpt,car{'times.numsimp},
               caddar num}};
    car denom := 1;
  end;



symbolic procedure simplify_sqrt6(num,denom,diff);
  begin
    scalar denomsimp;
    diff := -diff;
    denomsimp := for i := 1:diff collect reval {'plus,car cdadar num,i};
    car denom := {'oddexpt,car{'times.denomsimp},{'quotient,1,2}};
    caddar num := {'plus,caddar num,{'minus,{'quotient,1,2}}};
  end;



symbolic procedure simplify_sqrt7(num,denom,diff);
  begin
    scalar numsimp;
    numsimp := for i := 1:diff collect reval {'plus,car cdadar denom,i};
    car num := {'oddexpt,car{'times.numsimp},{'quotient,1,2}};
    caddar denom := {'plus,caddar denom,{'minus,{'quotient,1,2}}};
  end;



symbolic procedure simplify_sqrt8(num,denom,diff);
  begin
    scalar denomsimp;
    diff := -diff;
    denomsimp := for i := 1:diff collect reval {'plus,car cdadar num,i};
    car denom:= {'times,{'oddexpt, cadar num,{'plus,caddar denom,
             {'minus,{'quotient,1,2}}}},{'oddexpt,car{'times.denomsimp},
              caddar denom}};
    car num := 1;
  end;



symbolic procedure simplify5(num,denom,diff);
  begin
    scalar numsimp;
    numsimp := for i := 1:diff collect reval {'plus,cadar denom,i};
    cdr num := car{'times.numsimp}.cdr num;
  end;



symbolic procedure simplify6(num,denom,diff);
  begin
    scalar denomsimp;
    diff := -diff;
    denomsimp := for i := 1:diff collect reval {'plus,cadar num,i};
    cdr denom := car{'times.denomsimp}.cdr denom;
  end;



symbolic procedure test_for_facts(num,denom);
  %
  % Systematically goes through numerator and then  denom. looking  for
  % factorials.
  % (boolean).
  %
  begin
    scalar test;
    if test_num(num) and test_denom(denom) then test := t;
    return test
  end;

flag('(test_for_facts),'boolean);



symbolic procedure test_num(num);
  %
  % Systematically goes through num., looking for  factorials.
  % (boolean).
  %
  begin
    scalar test;
    test := nil;
    if eqcar (num ,'times) or eqcar (num ,'oddexpt) then
     while num and not test do
     <<
       if pairp car num and caar num eq 'factorial then test := t
        else if pairp car num and caar num eq 'oddexpt
         then if pairp cadar num and caadar num eq 'factorial
          then test := t;
       num := cdr num;
     >>;
    return test;
  end;

flag ('(test_num),'boolean);



symbolic procedure test_denom(denom);
  %
  % Systematically goes through denominator,  looking  for  factorials.
  % (boolean).
  %
  begin
    scalar test;
    test := nil;
    if eqcar (denom ,'times) or eqcar (denom ,'oddexpt) then
    while denom and not test do
    <<
      if pairp car denom and caar denom eq 'factorial then test := t
       else if pairp car denom and caar denom eq 'oddexpt
        then if pairp cadar denom and caadar denom eq 'factorial
         then test := t;
       denom:= cdr denom;
     >>;
    return test;
  end;

flag ('(test_denom),'boolean);

endmodule;

end;

