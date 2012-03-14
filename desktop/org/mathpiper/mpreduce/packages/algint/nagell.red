module nagell;

% Author: James H. Davenport.

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


fluid '(!*tra !*trmin intvar);

exports lutz!-nagell;

symbolic procedure lutz!-nagell(divisor);
begin
  scalar ans,places,mults,save!*tra;
  for each u in divisor do <<
    places:=(car u).places;
    mults :=(cdr u).mults >>;
  ans:=lutz!-nagell!-2(places,mults);
  if ans eq 'infinite
     then return 'provably!-impossible;
  save!*tra:=!*tra;
  if !*trmin
    then !*tra:=nil;
  ans:=coates!-multiple(places,mults,ans);
  !*tra:=save!*tra;
  return ans
  end;


symbolic procedure lutz!-nagell!-2(places,mults);
begin
  scalar wst,x,y,equation,point,a;
  wst:=weierstrass!-form getsqrtsfromplaces places;
  x:=car wst;
  y:=cadr wst;
  equation:=caddr wst;
  equation:=!*q2f !*multsq(equation,equation);
  equation:=makemainvar(equation,intvar);
  if ldeg equation = 3
    then equation:=red equation
    else interr "Equation not of correct form";
  if mvar equation eq intvar
    then if ldeg equation = 1
      then <<
        a:=(lc equation) ./ 1;
        equation:=red equation >>
      else interr "Equation should not have a x**2 term"
    else a:=nil ./ 1;
  equation:= a . (equation ./ 1);
  places:=for each u in places collect
        wst!-convert(u,x,y);
  point:=elliptic!-sum(places,mults,equation);
  a:=lutz!-nagell!-bound(point,equation);
  if !*tra or !*trmin then <<
    princ "Point actually is of order ";
    printc a >>;
  return a
  end;


symbolic procedure wst!-convert(place,x,y);
begin
  x:=subzero(xsubstitutesq(x,place),intvar);
  y:=subzero(xsubstitutesq(y,place),intvar);
  return x.y
  end;


symbolic procedure elliptic!-sum(places,mults,equation);
begin
  scalar point;
  point:=elliptic!-multiply(car places,car mults,equation);
  places:=cdr places;
  mults:=cdr mults;
  while places do <<
    point:=elliptic!-add(point,
             elliptic!-multiply(car places,car mults,
                        equation),
                         equation);
    places:=cdr places;
    mults:=cdr mults >>;
  return point
  end;


symbolic procedure elliptic!-multiply(point,n,equation);
if n < 0
  then elliptic!-multiply( (car point) . (negsq cdr point),
                           -n,
                           equation)
  else if n = 0
    then interr "N=0 in elliptic!-multiply"
    else if n = 1
      then point
      else begin
        scalar q,r;
        q:=divide(n,2);
        r:=cdr q;
        q:=car q;
    q:=elliptic!-multiply(elliptic!-add(point,point,equation),q,
                        equation);
        if r = 0
          then return q
      else return elliptic!-add(point,q,equation)
        end;


symbolic procedure elliptic!-add(p1,p2,equation);
begin
  scalar x1,x2,y1,y2,x3,y3,inf,a,b,lhs,rhs;
  a:=car equation;
  b:=cdr equation;
  inf:=!*kk2q 'infinite;
  x1:=car p1;
  y1:=cdr p1;
  x2:=car p2;
  y2:=cdr p2;
  if x1 = x2
    then if y1 = y2
      then <<
    % this is the doubling case.
    x3:= multsq(4 ./ 1,
                !*addsq(b,!*multsq(x1,!*addsq(a, !*exptsq(x1,2)))));
    if null numr x3 then return inf . inf;
    % We doubled a point and got infinity.
    x3:=!*multsq(!*addsq(!*addsq(!*multsq(a,a),
                     !*exptsq(x1,4)),
                 !*addsq(multsq(-8 ./ 1,!*multsq(x1,b)),
                     !*multsq(!*multsq(x1,x1),
                                              multsq(-2 ./ 1,a)))),
             !*invsq x3);
    y3:=!*addsq(y1,!*multsq(!*multsq(!*addsq(x3,negsq x1),
                     !*addsq(a,multsq(3 ./ 1,
                             !*multsq(x1,x1)))),
                 !*invsq multsq(2 ./ 1,
                                                y1))) >>
      else x3:=(y3:=inf)
    else if x1 = inf
      then <<
        x3:=x2;
        y3:=y2 >>
      else if x2 = inf
        then <<
          x3:=x1;
          y3:=y1 >>
        else <<
      x3:=!*multsq(!*addsq(!*multsq(a,!*addsq(x1,x2)),
                   !*addsq(multsq(2 ./ 1,b),
                       !*addsq(!*multsq(!*multsq(x1,x2),
                            !*addsq(x1,x2)),
                                               multsq(-2 ./ 1,
                            !*multsq(y1,y2))))),
               !*invsq !*exptsq(!*addsq(x1,negsq x2),2));
      y3:=!*multsq(!*addsq(!*multsq(!*addsq(y2,negsq y1),x3),
                   !*addsq(!*multsq(x2,y1),
                       !*multsq(x1,negsq y2))),
               !*invsq !*addsq(x1,negsq x2)) >>;
  if x3 = inf
    then return x3.y3;
  lhs:=!*multsq(y3,y3);
  rhs:=!*addsq(b,!*multsq(x3,!*addsq(a,!*multsq(x3,x3))));
  if numr !*addsq(lhs,negsq rhs) % We can't just compare them
                  % since they're algebraic numbers.
                  % JHD Jan 14th. 1987.
    then <<
      prin2t "Point defined by X and Y as follows:";
      printsq x3;
      printsq y3;
      prin2t "on the curve defined by A and B as follows:";
      printsq a;
      printsq b;
      prin2t "gives a consistency check between:";
      printsq lhs;
      printsq rhs;
      interr "Consistency check failed in elliptic!-add" >>;
  return x3.y3
  end;

symbolic procedure infinitep u;
   kernp u and (mvar numr u eq 'infinite);

symbolic procedure lutz!-nagell!-bound(point,equation);
begin
  scalar x,y,a,b,lutz!-alist,n,point2,p,l,ans;
    % THE LUTZ!-ALIST is an association list of elements of the form
    % [X-value].([Y-value].[value of N for this point])
    % See thesis, chapter 7, algorithm LUTZ!-NAGELL, step [1].
  x:=car point;
  y:=cdr point;
  if !*tra or !*trmin then <<
    printc "Point to have torsion investigated is";
    printsq x;
    printsq y >>;
  a:=car equation;
  b:=cdr equation;
  if denr y neq 1 then <<
    l:=denr y;
    % we can in fact make l an item whose cube is > denr y.
    y:=!*multsq(y,!*exptf(l,3) ./ 1);
    x:=!*multsq(x,!*exptf(l,2) ./ 1);
    a:=!*multsq(a,!*exptf(l,4) ./ 1);
    b:=!*multsq(b,!*exptf(l,6) ./ 1) >>;
  if denr x neq 1 then <<
    l:=denr x;
    % we can in fact make l an item whose square is > denr x.
    y:=!*multsq(y,!*exptf(l,3) ./ 1);
    x:=!*multsq(x,!*exptf(l,2) ./ 1);
    a:=!*multsq(a,!*exptf(l,4) ./ 1);
    b:=!*multsq(b,!*exptf(l,6) ./ 1) >>;
  % we now have integral co-ordinates for x,y.
  lutz!-alist:=list (x . (y . 0));
  if (x neq car point) and (!*tra or !*trmin) then <<
    printc "Point made integral as ";
    printsq x;
    printsq y;
    printc "on the curve with coefficients";
    printsq a;
    printsq b >>;
  point:=x.y;
  equation:=a.b;
  n:=0;
loop:
  n:=n+1;
  point2:=elliptic!-multiply(x.y,2,equation);
  x:=car point2;
  y:=cdr point2;
  if infinitep x
    then return 2**n;
  if denr x neq 1
    then go to special!-denr;
  if a:=assoc(x,lutz!-alist)
    then if y = cadr a
      then return (ans:=lutz!-reduce(point,equation,2**n-2**(cddr a)))
      else if null numr !*addsq(y,cadr a)
    then return (ans:=lutz!-reduce(point,equation,2**n+2**(cddr a)))
    else interr "Cannot have 3 points here";
  lutz!-alist:=(x.(y.n)).lutz!-alist;
  if ans
    then return ans;
  go to loop;
special!-denr:
  p:=denr x;
  if not primep p
    then return 'infinite;
  n:=1;
  n:=1;
loop2:
  point:=elliptic!-multiply(point,p,equation);
  n:=n*p;
  if infinitep car point
    then return n;
  if quotf(p,denr car point)
    then go to loop2;
  return 'infinite
  end;


symbolic procedure lutz!-reduce(point,equation,power);
begin
  scalar n;
  if !*tra or !*trmin then <<
    princ "Point is of order dividing ";
    printc power >>;
  n:=1;
  while evenp power do <<
    power:=power/2;
    n:=n*2;
    point:=elliptic!-add(point,point,equation) >>;
    % we know that all the powers of 2 must appear in the answer.
  if power = 1
    then return n;
  if primep power
    then return n*power;
  return n*lutz!-reduce2(point,equation,power,3)
  end;



symbolic procedure lutz!-reduce2(point,equation,power,prime);
if power = 1
  then if infinitep car point
    then 1
    else nil
  else if infinitep car point
    then power
    else begin
      scalar n,prime2,u,ans;
      n:=0;
      while cdr divide(power,prime)=0 do <<
        n:=n+1;
        power:=power/prime >>;
      prime2:=nextprime prime;
      for i:=0:n do <<
    u:=lutz!-reduce2(point,equation,power,prime2);
        if u
          then <<
              ans:=u*prime**i;
              i:=n >>
         else <<
          power:=power*prime;
      point:=elliptic!-multiply(point,prime,equation) >> >>;
      if ans
        then return ans
        else return nil
      end;

endmodule;

end;
