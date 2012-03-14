module groebspa;

% % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % %
% Manipulation of subspaces .
% A subspace among the variables is described by an exponent vector
% with only zeroes and ones . It terminates with the last
% one . It may be null(nil).
%
% 24.9.2007 HM enable the call "vevunion(...,nil)".

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



symbolic procedure vevunion(e1,e2);
if null e2 then (e1 or vevunion2()) else
begin scalar x,y;y:=vevunion1(e1,e2);
 x:=reverse y;if car x = 1 then return y;
 while x and car x = 0 do x:=cdr x;return reversip x end;

symbolic procedure vevunion1(e1,e2);
 if vdpsubspacep(e1,e2)then e2 else
 if vdpsubspacep(e2,e1)then e1 else
 (if car e1 neq 0 or car e2 neq 0 then 1 . z else
  0 . z) where z=vevunion1(cdr e1,cdr e2);

symbolic procedure vevunion2;
 <<car y:=1;y>> where y=vevunion3 vdpvars!*;

symbolic procedure vevunion3 x;
  if null x then nil else (0 . vevunion3 cdr x);

symbolic procedure vdpsubspacep(e1,e2);
% Test if e1 describes a subspace from e2 .
 if null e1 then t else
 if null e2 then vdpspacenullp e1 else
 if car e1 > car e2 then nil else
 if e1 = e2 then t else vdpsubspacep(cdr e1,cdr e2);

symbolic procedure vdporthspacep(e1,e2);
% Test if e1 and e2 describe orthogonal spaces(no intersection).
 if null e1 or null e2 then t else
 if car e2 = 0 or car e1 = 0 then vdporthspacep(cdr e1,cdr e2)else nil;

symbolic procedure vdpspacenullp e1;
% Test if e1 describes an null space .
 if null e1 then t else
 if car e1 = 0 then vdpspacenullp cdr e1 else nil;

symbolic procedure vdpspace p;
% Determine the variables of the polynomial .
begin scalar x,y;
 if vdpzero!? p then return nil;
 x:=vdpgetprop(p,'subroom);
 if x then return x;
 x:=vevunion(nil,vdpevlmon p);
 y:=vdpred p;
 while not vdpzero!? y do
 <<x:=vevunion(x,vdpevlmon y);y:=vdpred y>>;
 vdpputprop(p,'subroom,x);
 return x end;

symbolic procedure vdpunivariate!? p;
 if vdpgetprop(p,'univariate)then t
  else begin scalar ev;integer n;
   ev:=vdpevlmon p;
   for each x in ev do
    if not(x = 0)then n:=n #+ 1;
    if not(n = 1)then return nil;
    ev:=vdpspace p;
    for each x in ev do
     if not(x = 0)then n:=n #+ 1;
     if not(n = 1)then return nil;
     vdpputprop(p,'univariate,t);
     return t end;

endmodule;;end;
