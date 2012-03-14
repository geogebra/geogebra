
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Neil Langmead   November 1996   ZIB Berlin
% routines to evaluate trigonometric integrals
%
% main routine is trigint
% substitution variable is always u
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

module trigint;

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


create!-package ('(trigint),nil);

global '(!*tracetrig);

switch tracetrig;

off tracetrig; % off by default;
algebraic;
load_package limits;
on usetaylor;
load_package misc;
% need a knowledge base of the substitutions

expr procedure sub_a(exp,var);
 sub({sin(var)=2*u/(1+u^2), cos(var)=(1-u^2)/(1+u^2)},exp);

expr procedure sub_b(exp,var);
sub({sin(var)=(u^2-1)/(u^2+1),
      cos(var)=2*u/(u^2+1)},exp);

expr procedure sub_c(exp,var);
sub({sin(var)=2*u/(1+u^2), cos(var)=(u^2-1)/(1+u^2)},exp);

expr procedure sub_d(exp,var);
sub({sin(var)=u/(sqrt(1+u^2)), cos(var)=1/(sqrt(1+u^2))},exp);

% applying the substitutions to their integrals

expr procedure apply_a(exp,var);
begin scalar answer, result;
   answer:=sub_a(exp,var);
   answer:=answer* 2/(1+u^2);
   result:=int(answer,u);
   result:=sub({u=tan(var/2)},result);
   result:=result+trigint_k(result,var,pi)*floor((var-pi)/(2*pi));
   return result;
end;

expr procedure apply_b(exp,var);
begin scalar answer, result;
  answer:=sub_b(exp,var); answer:=answer*2/(1+u^2);
  result:=int(answer,u); result:= sub({u=tan(var/2+pi/4)}, result);
  result:=result+trigint_k(result,var,pi/2)*floor((var-pi/2)/(2*pi));
return result;
end;

expr procedure apply_c(exp, var);
begin scalar answer, result;
 answer:=sub_c(exp,var); answer:= answer*(-2/(1+u^2));
 result:=int(answer,u); result:=sub({u=1/(tan(var/2))},result);
 result:=result+trigint_k(result,var,0)*floor(var/(2*pi));
 return result;
end;

expr procedure apply_d(exp, var);
begin scalar answer, result;
  answer:=sub_d(exp,var);
  answer:=answer*(1/(1+u^2));
  result:=int(answer,u); result:= sub({u=tan(var)},result);
  result:=result+trigint_k(result,var,pi/2)*floor((var-pi/2)/pi);
return result;
end;

% some trigonometric substitutions which occur very frequently

trig_rules:= {
                (sec(~x))^2 => 1/(cos(x))^2,
                %tan(~x) => sin(x)/cos(x),
                (tan(~x))^2  => (sec(x))^2-1
               %2*sin(~x/2)*cos(~x/2) => sin(x)
               %(cos(~x))^2 => 1-(sin(x))^2 % (sin(~x))^2 => 1-(cos(x))^2
              };

%let trig_rules;

%for all x let sin(x)^2+cos(x)^2=1,
 % 2*sin(x/2)*cos(x/2)=sin(x);

% procedure to see if integral is returned unevaluated
expr procedure unevalp(exp);
begin scalar finished, k; k:=0; finished:=0;
while ( finished=0 and k<=arglength(exp)) do
<<
  if(part(exp,k)=int) then finished:=1 else k:=k+1;
>>;
if (finished=1) then return t else nil;
end;


% procedure to evaluate K

expr procedure trigint_k(exp,var,val);
limit!-(exp,var,val)-limit!+(exp,var,val);

% two routines to see if we have either unevaluated limits or ints in our
% result. If we have, then try a different substitution

expr procedure uneval_int(exp);
begin scalar temp;
   if( not freeof(exp,int)) then return temp:=t else temp:=nil;
return temp;
end;

expr procedure uneval_lim(exp);
begin scalar temp;
    if(not freeof(exp,limit!-)) then return t else
     <<
       if(not freeof(exp,limit!+)) then return t else nil;
     >>;
end;

expr procedure fail_a(exp,var);
begin scalar temp;
   temp:=apply_a(exp,var);
   if(uneval_lim(temp)) then return t else
   <<
     if(uneval_int(temp)) then return t
     else return nil;
   >>;
end;

expr procedure fail_b(exp,var);
begin scalar temp;
     temp:=apply_b(exp,var);
     %temp:=temp+trigint_k(temp,var,pi/2)*floor((var-pi/2)/(2*pi));
     if(uneval_lim(temp)) then return t else
     <<
     if(uneval_int(temp)) then return t else return nil;
     >>;
end;

expr procedure fail_c(exp,var);
begin scalar temp;
    temp:=apply_c(exp,var);
    %temp:=temp+trigint_k(temp,var,0)*floor(var/(pi));
    if(uneval_lim(temp)) then return t else
    <<
      if(uneval_int(temp)) then return t else return nil;
    >>;
end;

expr procedure fail_d(exp,var);
  begin scalar temp;
        temp:=apply_d(exp,var);
        %temp:=temp+trigint_k(temp,var,pi/2)*floor((var-pi/2)/pi);
  if(uneval_lim(temp)) then return t else
   <<
     if(uneval_int(temp)) then return t else return nil;
   >>;
end;

expr procedure fail(exp);
if(uneval_lim(exp)) then t else
<< if(uneval_int(exp)) then t else nil; >>;


let log(-1) => i*pi; % really important. If further log rules are needed,
                     % take a look at the ratint package, and the module
                     % convert, which contains an extensive list of such
                     % rules

expr procedure trigint(exp,var);
begin scalar answer, answer_1, answer_2, answer_3, answer_4, result;

%off mesgs; % off by default
% check for correct input

if(freeof(exp,sin) and freeof(exp,cos) and freeof(exp,tan)) then <<
if(lisp !*tracetrig) then
write "expression free of sin, cos tan, proceeding with standard integration";
 return int(exp,x); >>;
on usetaylor;


if freeof(exp,sin(var)) then % we use substitution (a)
   <<
     answer:=apply_a(exp,var);
     %answer:=answer+trigint_k(answer,var,pi)*floor((var-pi)/(2*pi));
     if(fail(answer)) then % system can't evaluate after subs
     <<
       if(lisp !*tracetrig) then
       write "system can't integrate after substitution A,
              trying again";
       answer_2:=apply_b(exp,var);
       if(fail(answer_2)) then
        <<
          if(lisp !*tracetrig) then write "trying again with substitution B";
          answer_3:=apply_c(exp,var);
          if(fail(answer_3)) then
         <<
            if(lisp !*tracetrig) then
                    write "and again with substitution C";
                    answer_4:=apply_d(exp,var);
                    if(fail(answer_4)) then

                    <<
                if(lisp !*tracetrig) then
                write "failed in all attempts, system cannot integrate";
                        return answer;
                    >> else return answer_4;
                  >> else return answer_3;
         >> else return answer_2;
       >> else return answer;
     %let trig_rules;
     %if(unevalp(answer)) then rederr "system cannot integrate after subs"
     %else nil;
     >>
else
 % we use substitution b,c or d
   <<
       if(freeof(exp,cos(var))) then % use substitution b
        <<
          answer:=apply_b(exp,var);
          if(fail(answer)) then
          <<
if(lisp !*tracetrig) then write "failed with substitution B: system could not
                                integrate after subs, trying A";

answer_2:=apply_a(exp,var);
            if(fail(answer_2)) then
            <<
if(lisp !*tracetrig) then write "failed with A: trying C now";

answer_3:=apply_c(exp,var);
              if(fail(answer_3)) then
                 <<
if(lisp !*tracetrig) then write "failed with C: trying D now";
answer_4:=apply_d(exp,var);
        if(lisp !*tracetrig) then
                   write "trying all possible substitutions";
                   if(fail(answer_4)) then rederr "system can't integrate after
                                                 subs"
                   else return answer_4;
                  >> else return answer_3;
              >> else return answer_2;
           >> else return answer;
         >>

      else <<
      % now describe situations best for (c) and (d) G and R sect 2.504
      if(sub({sin(var)=-sin(var),cos(var)=-cos(var)},exp)=exp) then
                       % d is the best sub in this case
        <<
if(lisp !*tracetrig) then write
"using heuristics: G & R section 2.504 to integrate ";

answer:=apply_d(exp,var);
           if(fail(answer)) then
           <<
     if(lisp !*tracetrig) then write "subs D failed, trying now with A";

              answer_2:=apply_a(exp,var);
              if(fail(answer_2)) then
             <<
if(lisp !*tracetrig) then write "subs B falied, trying with sub C";
answer_3:=apply_b(exp,var);
                if(fail(answer_3)) then
               <<
if(lisp !*tracetrig) then write "sub C falied, trying sub D";
answer_4:=apply_c(exp,var);
                  if(fail(answer_4)) then rederr "can't integrate after subs"
                  else return answer_4;
               >> else return answer_3;
             >> else return answer_2;
           >> else return answer;
         >>
       else <<  % no guidelines, try each substitution in turn, and return the
                % best possible answer, if there is one
               answer:=apply_a(exp,var);
               if(fail(answer)) then
               <<
if(lisp !*tracetrig) then write "not using heuristics,
         attempting subs in order: trying A";
answer_2:=apply_b(exp,var);
                   if(fail(answer_2)) then
                 <<
if(lisp !*tracetrig) then write "A failed, trying B";
answer_3:=apply_c(exp,var);
                     if(fail(answer_3)) then
                   << answer_4:=apply_d(exp,var);
                       if(fail(answer_4)) then rederr "can't do it"
                        else return answer_4;
                   >> else return answer_3;
                  >> else return answer_2;
               >> else return answer;
             >>;
        >>; % for the else just before the comment on G and R
    >>;
return answer;
end;

endmodule;

end;




