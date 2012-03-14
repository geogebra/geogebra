module traverso;

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


% Buchberger algorithm base on "sugar" strategy;
% see Giovini-Mora-Niesi-Robbiano-Traverso:
% One sugar gube,please. ISSAC 91 proceddings,pp 49-54.

!*gtraverso!-sloppy:=t;

symbolic procedure gtraverso(g0,fact,abort1);
begin scalar g,d,s,h,p,!*gsugar;
 fact:=nil;abort1:=nil;!*gsugar:=t;
 g0:=for each fj in g0 join if not vdpzero!? fj then
  <<groebsavelterm fj;{gsetsugar(vdpenumerate vdpsimpcont fj,nil)}>>;
main_loop:
 if null g0 and null d then return gtraversofinal g;
 if g0 then <<h:=car g0;g0:=cdr g0;p:={nil,h,h}>>
  else
  <<p:=car d;d:=cdr d;
     s:=groebspolynom(cadr p,caddr p);
                  !*trgroeb and groebmess3(p,s);
      h:=groebsimpcontnormalform groebnormalform(s,g,' list);
      if vdpzero!? h then
      <<!*trgroeb and groebmess4(p,d);goto main_loop>>;
      if vevzero!? vdpevlmon h then % base 1 found
      <<   !*trgroeb and groebmess5(p,h);d:=g:=g0:=nil>>>>;
       h:=groebenumerate h;!*trgroeb and groebmess5(p,h);
       groebsavelterm h;
          % New pair list.
      d:=gtraversopairlist(h,g,d);
          % New basis.
      g:=nconc(g,{h});goto main_loop end;

symbolic procedure gtraversopairlist(gk,g,d);
% gk: new polynomial,g: current basis,d: old pair list.
begin scalar a,ev,r,n,nn,q;
    % Delete triange relations from old pair list.
 d:=gtraversopairsdiscard1(gk,d);
    % Build new pair list.
 ev:=vdpevlmon gk;
 for each p in g do if not groebbuchcrit4t(ev,a:=vdpevlmon p)
  then r:=vevlcm(ev,a).r
% One line added and one line changed 26.3.2001 (Melenk).
  else<<if null gmodule!* or gevcompatible1(a,ev,gmodule!*)
    then n:=groebmakepair(p,gk).n>>;
    % Delete from new pairs equivalents to coprime lcm.
  for each q in r do for each p in n do if car p = q then n:=delete(p,n);
    % Discard multiples: collect survivers in n.
  if !*gtraverso!-sloppy then !*gsugar:=nil;
  n:=groebcplistsort n;!*gsugar:=t;
  nn:=n;n:=nil;
  for each p in nn do
  <<q:=nil;
     for each r in n do q:=q or vevdivides!?(car r,car p);
     if not q then n:=groebcplistsortin(p,n)>>;
  return groebcplistmerge(d,reversip n)end;

symbolic procedure gtraversopairsdiscard1(gk,d);
% Crit B.
begin scalar gi,gj,tij,evk;
 evk:=vdpevlmon gk;
 for each pij in d do
 <<tij:=car pij;gi:=cadr pij;gj:=caddr pij;
    if vevstrictlydivides!?(tt(gi,gk),tij)
       and vevstrictlydivides!?(tt(gj,gk),tij)
        then d:=delete(pij,d)>>;return d end;

symbolic procedure vevstrictlydivides!?(ev1,ev2);
   not(ev1=ev2)and vevdivides!?(ev1,ev2);

symbolic procedure gtraversofinal g;
% Final reduction and sorting.
begin scalar r,p,!*gsugar;
 g:=vdplsort g; % Descending.
 while g do
 <<p:=car g;g:=cdr g;
    if not groebsearchinlist(vdpevlmon p,g)then
     r:=groebsimpcontnormalform groebnormalform(p,g,'list).r>>;
 return list reversip r end;

endmodule;;end;
