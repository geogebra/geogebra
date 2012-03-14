module groebcri;

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


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Criteria for the Buchberger algorithm .
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

smacro procedure atleast2elementsin u;
% Test if u has at least a cadr element .
 u and cdr u;

symbolic procedure groebbuchcrit4(p1,p2,e);
% Buchberger criterion 4 . p1 and p2 are distributive
% polynomials . e is the least common multiple of
% the leading exponent vectors of the distributive
% polynomials p1 and p2 . groebBuchcrit4(p1,p2,e)returns a
% boolean expression . True,if the reduction of the
% distributive polynomials p1 and p2 is necessary else false .
% Orig:
%     e neq vevsum(vdpevlmon p1,vdpevlmon p2);
 << e;groebbuchcrit4t(vdpevlmon p1,vdpevlmon p2)>>;

symbolic procedure groebbuchcrit4t(e1,e2);
% Nonconstructive test of lcm(e1,e2)=e1 + e2;
% equivalent: no matches of nonzero elements .
 if null e1 or null e2 then nil else
  if(car e1 neq 0)and(car e2 neq 0)then t
   else groebbuchcrit4t(cdr e1,cdr e2);

symbolic procedure groebinvokecritbuch4(p,d2);
% Buchberger's criterion 4 is tested on the pair p and the list
% D2 of critical pairs is updated with respect to that crit .
% Result is the updated D2 .
begin scalar p1,p2,vev1,vev2,f1,f2,fd,b4;
 p1:=cadr p;p2:=caddr p;vev1:=vdpevlmon p1;vev2:=vdpevlmon p2;
 f1:=vdpgetprop(p1,'monfac);f2:=vdpgetprop(p2,'monfac);
                   % Discard known common factors first .
 if f1 and f2 then
 << fd:=vevmin(f1,f2);
    b4:=groebbuchcrit4t(vevdif(vev1,fd), vevdif(vev2,fd));
    if b4 and    % Is the body itself a common factor ?
             vevdif(vev1,f1)=vevdif(vev2,f2)
                         % Test if the polys reduced by their monom .
                         % factor are equal .
                     and groebbuchcrit4compatible(p1,f1,p2,f2)
                       then b4:=nil >>
  else b4:=groebbuchcrit4t(vev1,vev2);
 if b4 then d2:=append(d2,{p})else b4count!*:=b4count!* + 1;
 return d2 end;

symbolic procedure groebbuchcrit4compatible(p1,f1,p2,f2);
% p1,p2 polys,f1,f2 exponent vectors(monomials), which are known to
% be factors of their f;
% tests, if p1 / f1=p2 / f2 .
 if vdpzero!? p1 then vdpzero!? p2
  else if vdplbc p1=vdplbc p2 and
    groebbuchcrit4compatiblevev(vdpevlmon p1,f1,vdpevlmon p2,f2)
   then groebbuchcrit4compatible(vdpred p1,f1,vdpred p2,f2)
    else nil;

symbolic procedure groebbuchcrit4compatiblevev(vev1,f1,vev2,f2);
 if null vev1 then null vev2 else
  if(if f1 then car vev1 - car f1 else car vev1)=
  (if f2 then car vev2 - car f2 else car vev2)then
    groebbuchcrit4compatiblevev(cdr vev1,
     if f1 then cdr f1 else nil,cdr vev2,
     if f2 then cdr f2 else nil)else nil;

symbolic procedure groebinvokecritf d1;
% GroebInvokeCritF tests a list D1 of critical pairs . It cancels all
% critical pairs but one in D1 having the same lcm(i . e . car
% component)as car(D1). This only one is chosen,if possible,
% such that it doesn't satisfy groebBuchcrit4 .
% Version: moeller upgraded 5.7.87 .
begin scalar tp1,p2,active;
 tp1:=caar d1;active:=atleast2elementsin d1;
 while active do
 << p2:=cadr d1;
    if car p2=tp1 then
    << fcount!*:=fcount!* + 1;
       if not groebbuchcrit4t(cadr p2,caddr p2)then d1:=cdr d1
        else d1:=groedeletip(p2,d1);
       active:=atleast2elementsin d1 >>
     else active:=nil >>;
 return d1 end;

symbolic procedure groebinvokecritm(p1,d1);
% D1 is a list of critical pairs,p1 is a critical pair .
% Crit M tests,if the lcm of p1 divides one of the lcm's in D1 .
% If so,this object is eliminated .
% Result is the updated D1 .
<< for each p3 in d1 do if buchvevdivides!?(car p1,car p3)then
   << mcount!*:=mcount!* + 1;
      d1:=groedeletip(p3,d1)>>;    %  Criterion M .
    d1 >>;

symbolic procedure groebinvokecritb(fj,d);
% D is a list of critical pairs,fj is a polynomial .
% Crit B allows to eliminate a pair from D,if the leading monomial
% of fj divides the lcm of the pair,but the lcm of fj with each of
% the members of the pair is not the lcm of the pair itself .
% Result is the updated D .
<< for each p in d do
    if buchvevdivides!?(vdpevlmon fj,car p)and
     tt(fj,cadr p)neq car p and % Criterion B .
     tt(fj,caddr p)neq car p then
      << bcount!*:=bcount!* +1;d:=delete(p,d)>>;d >>;

endmodule;;end;
