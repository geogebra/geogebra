
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


%ammend the output of ratint, converting complex logs to real arctans, and
%providing alternative forms for the answer

% want a procedure to convert the log sum notation to a sum involving
% radicals. We typically get something like
%
%  log(x+1)+log_sum(alpha,alpha^2+alpha+1,0,alpha*log(alpha*2+x*alpha),x)
%
% as our output from the ratint program,
% and we want to sub alpha into the log expression and sum.
% other possibilies include transforming the logarithmic expression (which
% often contains complex logarithms, to real arctangents.
% This could be important if definite integration is the goal
% Example
% in symbolic mode, we have
%
%  exp: (log_sum alpha (plus (expt alpha 2) alpha 1) 0
%         (times (log (plus (times alpha x) 1)) alpha) x)

% need a procedure to get rid of parts of the expression not involving
% log_sum

algebraic <<
logcomplex:=
  {
    log(~x+i) =>
      log(sqrt(x^2+1))+i*atan((1/sqrt(x^2+1))+i*(x/(sqrt(x^2+1)))), %when
               %repart(x)=x,
    log(~x- i) =>
       log(sqrt(x^2+1))-i*atan(1/sqrt(x^2+1)+i*(x/(sqrt(x^2+1)))),
                %when repart(x)=x,
    log(~x+i*~y) =>
    log(sqrt(x*x+y*y))+i*atan(y/sqrt(x*x+y*y)+i*x/(sqrt(x*x+y*y))),% when
     %repart(x)=x and repart(y)=y,
    log(~x-i*~y) =>
     log(sqrt(x*x+y*y))-i*atan(y/(sqrt(x*x+y*y))+i*(x/(sqrt(x*x+y*y)))), % when
         % repart(x)=x and repart(y)=y,
     log(~x/~y) => log(x)-log(y), %when repart(y)=y,
     log(sqrt ~x) => (log x)/2,
     log(-1) => i*pi,
     log(-i) => -i*pi/2,
     log(i) => i*pi/2,
     log(-~x) => i*pi+log(x), % when repart(x)=x and numberp x and x>0,
     log(-i*~x) => -i*pi/2+log(x),
       %when repart(x)=x and numberp x and x>0,
   log(i*~x) => i*pi/2 +log(x) %when repart(x)=x and numberp x and x>0
   }$
 >>;

%algebraic let logcomplex;

 %letrules logcomplex

symbolic procedure evalplus(a,b);
if numberp a and numberp b then a+b
  else prepsq simp!* aeval {'plus,a,b};

procedure my_plus(a,b); lisp evalplus(a,b);

symbolic procedure evalmax(a,b);
  if numberp a and numberp b then max(a,b)
   else if evalgreaterp(a,b) then a else b;

procedure my_max(a,b); lisp evalmax(a,b);


%remove(log_sum(alpha,alpha^2+alpha+1,0,alpha*log(alpha*2+x*alpha))+log(1+x));

symbolic procedure convert(exp);
begin scalar temp,solution, answer;
if(freeof(exp,'log_sum)) then return exp else
<<
   if(null exp) then return nil else
   <<
      if(car exp='log_sum) then
      <<
        temp:=caddr exp;
        if(not freeof(temp,'beta)) then
        <<
        temp:=subeval(list (list('equal,'beta,'alpha), temp));
        exp:=subeval(list (list('equal,'beta,'alpha),exp));
        >>;
        temp:=reval temp; exp:=reval exp;
        %temp:=sub(beta=alpha,temp) % so temp now depends on alpha
        if(deg(temp,'alpha))>2 then
        << %write "degree of", reval exp, " is >2";
           rederr "cannot convert to radicals, degree > 2";
        >>
        else
         <<
         % temp is of the form alpha^2+alpha+1 or something similar

         if(deg(temp,'alpha)=2) then % solve the quadratic eqn, obtain
                                    % radicals, and substitute
            << %temp:=reval temp
               solution:= algebraic solve(temp=0,alpha);
               write reval cadr solution;
               %now have a solution list, with two elements

    answer:=subeval(list (reval cadr solution, algebraic reval part(exp,4)));
    answer:=list('plus, answer,subeval(list (reval caddr solution,
                        algebraic reval part(exp,4))));
    %answer:=car answer;
    answer:=reval answer;
             >>
          >>
        >>
       else <<
       if null cdr exp then return convert(car exp) else
      return convert (cdr exp);
            >>
    >>
 >>;
return answer;
end;

procedure conv(exp); lisp convert(exp);
%conv(log(x+1)+log_sum(alpha,alpha^2+alpha-1,0,alpha*log(alpha*x^2+alpha*x-1),x%));

% procedure to separate real and imaginary parts in a log sum expression
symbolic procedure separate_real(exp);
begin scalar re, im, ans;
    re:=algebraic repart(exp);
    im:=algebraic impart(exp);
   ans:= 'list.list(re,im);
   ans:=reval ans;
   return ans;
end;

procedure sep(exp); lisp separate_real(exp);

% input an expression with complex logs, output real arctangents
symbolic procedure convert_log(exp,var);
begin scalar sepp,re,im, answer;

algebraic repart(var):=var; algebraic impart(var):=0;
   if(car exp='log) then <<
        sepp:=separate_real(exp); %now have a list of re and im parts
                                   % can pass these to logtoatan function now
   re:=car exp; im:=cadr exp;
   answer:=logtotan1(re,im,var);
   return answer;
   >> else nil;
end;

procedure convertlog(exp,var); lisp convert_log(exp,var);

% now need an assume real facility, so extract terms dependent on i only
%
% This algorithm transforms some complex logarithmic expressions to real
% arctangents.
%
% the algorithm apppears in Manuel Bronsteins book Symbolic Integration I :
%  Transendental Functions, published by Springer Verlag, 1997
%
% Given a field K of characteristic 0 such that sqrt(-1) is not in K and A,
% B are in K[x] with B neq 0, this returns a sum f of real arctangents of
% polynomials in K[x] such that
%
%      df   = d         (A+i*B)
%      --     -- i* log ------
%      dx     dx        (A-i*B)
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

algebraic;
expr procedure logtoAtan(exp1,exp2,x);
begin scalar D,C,G, temp, rem;
rem:=pseudorem(exp1,exp2,x);
if(part(rem,1)=0) then return (2*atan(exp1/exp2))
%if(numberp(exp1/exp2)) then return (2*atan(exp1/exp2))
 else <<
      if(deg(exp1,x)<deg(exp2,x)) then return logtoAtan(-exp2,exp1,x)
      else <<
      temp:=gcd_ex(exp2,-exp1,x);
      D:=part(temp,1);
      C:=part(temp,2);
      G:=exp2*D-exp1*C;
      return(2*atan((exp1*D+exp2*C)/G)+ logtoAtan(D,C,x));
            >>;
      >>;
 end;

%log_sum(alpha,alpha^2+alpha+1,0,alpha*log(alpha*2+x*alpha),x);
%trst convert;
%conv(ws);
%conv(log_sum(beta,beta^2+beta-4,0,beta*log(beta*x^2+beta^2+1),x));

%------------------------------------------------------------------------------
end;
