module ncgroeb; % Groebner for noncommutative one sided ideals.

% Author: H. Melenk, ZIB Berlin, J. Apel, University of Leipzig.

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


% Following Carlo Traverso's model.

switch gsugar;

symbolic procedure nc!-groebeval u;
  begin scalar g;
    nc!-gsetup();
    u:=car u;
    g:=for each p in cdr listeval(u,nil) collect a2ncvdp reval p;
    g:=nc!-traverso g;
    return 'list.for each w in g collect vdp2a w end;

put('nc_groebner,'psopfn,'nc!-groebeval);

symbolic procedure nc!-preduce u;
  begin scalar g,p,!*gsugar;
    nc!-gsetup();
    g:=for each p in cdr listeval(cadr u,nil) collect a2ncvdp reval p;
    p:=a2ncvdp reval car u;
    p:=nc!-normalform(p,g,nil,nil);
    return vdp2a p end;

put('nc_preduce,'psopfn,'nc!-preduce);

symbolic procedure nc!-div u;
  begin scalar g,p,!*gsugar;
    nc!-gsetup();
    g:=a2ncvdp reval cadr u;
    p:=a2ncvdp reval car u;
    p:=nc!-qremf(p,g);
    return{'list,vdp2a car p,vdp2a cdr p}end;

put('nc_divide,'psopfn,'nc!-div);

symbolic procedure nc!-gsetup();
 <<    factortime!*:=0;
       groetime!*:=time();
       vdpinit2 ncdipvars!*;
       vdponepol(); % we construct dynamically
       hcount!*:=mcount!*:=fcount!*:=pcount!*:=0;
       bcount!*:=b4count!*:=hzerocount!*:=0;
       basecount!*:=0;!*gcd:=t;glterms:=list('list);
       groecontcount!*:=10;
       !*nc!-traverso!-sloppy:=!*vdpinteger:=t;
       if null ncdipbase!* then
        rederr "non-commutative ideal initialization missing">>;

!*gsugar:=t;

symbolic procedure nc!-traverso g0;
  begin scalar g,d,s,h,p;
  g0:=for each fj in g0 collect gsetsugar(vdpenumerate vdpsimpcont fj,nil);
main_loop:if null g0 and null d then return nc!-traverso!-final g;
    if g0 then<<h:=car g0;g0:=cdr g0;p:={nil,h,h}>>
       else
         <<p:=car d;
           d:=cdr d;
           s:=nc!-spoly (cadr p, caddr p);
                  !*trgroeb and groebmess3 (p,s);
           h:=groebsimpcontnormalform nc!-normalform(s,g,'list,t);
           if vdpzero!? h then
           <<!*trgroeb and groebmess4(p,d);go to main_loop>>;
            if vevzero!? vdpevlmon h then % base 1 found
                  <<   !*trgroeb and groebmess5(p,h);
                       d:=g:=g0:=nil>>>>;
         h:=groebenumerate h;!*trgroeb and groebmess5(p,h);
          % new pair list
         d:=nc!-traverso!-pairlist(h,g,d);
          % new basis
         g:=nconc(g,{h});
         go to main_loop end;

symbolic procedure nc!-traverso!-pairlist(gk,g,d);
  % gk: new polynomial,
  % g:  current basis,
  % d:  old pair list.
  begin scalar ev,r,n,nn,q;
     % delete triange relations from old pair list.
    d:=nc!-traverso!-pairs!-discard1(gk,d);
     % build new pair list.
    ev:=vdpevlmon gk;
    for each p in g do n:=groebmakepair(p,gk).n;
     % discard multiples: collect survivers in n
    <<if !*nc!-traverso!-sloppy then !*gsugar:=nil;
      n:=groebcplistsort n>>where !*gsugar=!*gsugar;
    nn:=n;n:=nil;
    for each p in nn do
    <<q:=nil;
      for each r in n do
        q:=q or vevdivides!?(car r,car p);
      if not q then n:=groebcplistsortin(p,n)>>;
    return groebcplistmerge(d,reversip n) end;

symbolic procedure nc!-traverso!-pairs!-discard1(gk,d);
  % crit B
  begin scalar gi,gj,tij,evk;
   evk:=vdpevlmon gk;
   for each pij in d do
   <<tij:=car pij;gi:=cadr pij;gj:=caddr pij;
    if vevstrictlydivides!?(tt(gi,gk),tij)
       and vevstrictlydivides!?(tt(gj,gk),tij)
      then d:=delete(pij,d)>>;
   return d end;

symbolic procedure vevstrictlydivides!?(ev1,ev2);
   not(ev1=ev2)and vevdivides!?(ev1,ev2);

symbolic procedure nc!-traverso!-final g;
  % final reduction and sorting;
  begin scalar r,p,!*gsugar;
   g:=vdplsort g; % descending
   while g do
   <<p:=car g;g:=cdr g;
     if not groebsearchinlist(vdpevlmon p,g) then
       r:=groebsimpcontnormalform nc!-normalform(p,g,'list,t).r>>;
   return reversip r end;

symbolic procedure nc!-fullprint(comm,cu,u,tu,cv,v,tv,r);
  <<terpri();prin2 "COMPUTE ";prin2t comm;
    vdpprin2 cu;prin2 " * P(";prin2 vdpnumber u; prin2 ")=> ";
    vdpprint tu;
    vdpprin2 cv;prin2 " * P("; prin2 vdpnumber v; prin2 ")=> ";
    vdpprint tv;
    prin2t "               ====>";
    vdpprint r;
    prin2t " - - - - - - -">>;

symbolic procedure nc!-spoly(u,v);
 % Compute S-polynomial.
  begin scalar cu,cv,tu,tv,bl,l,r;
    l:=vev!-cofac(vdpevlmon u,vdpevlmon v);
    bl:=vbc!-cofac(vdplbc u,vdplbc v);
    cu:=vdpfmon(car bl, car l);
    cv:=vdpfmon(cdr bl, cdr l);
    if !*ncg!-right then <<tu:=vdp!-nc!-prod(u,cu);tv:=vdp!-nc!-prod(v,cv)>>
    else <<tu:=vdp!-nc!-prod(cu,u);tv:=vdp!-nc!-prod(cv,v)>>;
    nccof!*:=cu.cv;
    r:=vdpdif(tu,tv);
    if !*trgroebfull then nc!-fullprint("S polynomial:",cu,u,tu,cv,v,tv,r);
    return r end;

symbolic procedure nc!-qremf(u,v);
 % compute (u/v, remainder(u,v)).
  begin scalar ev,cv,q;
   q:=a2vdp 0;
   if vdpzero!? u then return q.q;
   ev:=vdpevlmon v;cv:=vdplbc v;
   while not vdpzero!? u and vevdivides!?(ev,vdpevlmon u) do
   <<u:=nc!-reduce1(u,vdplbc u,vdpevlmon u, v);
     q:=if !*ncg!-right then vdp!-nc!-prod(q,car nccof!*)
                        else vdp!-nc!-prod(car nccof!*,q);
     q:=vdpsum(q,cdr nccof!*)>>;
   return q.u end;

symbolic procedure nc!-reduce1(u,bu,eu,v);
 % Compute u - w*v such that monomial (bu*x^eu) in u is deleted.
  begin scalar cu,cv,tu,tv,bl,l,r;
    l:=vev!-cofac(eu,vdpevlmon v);
    bl:=vbc!-cofac(bu,vdplbc v);
    cu:=vdpfmon(car bl,car l);
    cv:=vdpfmon(cdr bl,cdr l);
    if !*ncg!-right then
    <<tu:=vdp!-nc!-prod(u,cu);tv:=vdp!-nc!-prod(v,cv)>>
    else <<tu:=vdp!-nc!-prod(cu,u);tv:=vdp!-nc!-prod(cv,v)>>;
    nccof!*:=cu.cv;
    r:=vdpdif(tu,tv);
    if !*trgroebfull then
      nc!-fullprint("Reduction step:",cu,u,tu,cv,v,tv,r);
     %%%% if null yesp "cont" then rederr "abort";
    return r end;

symbolic procedure nc!-normalform(s,g,mode,cmode);
<<mode:=nil;nc!-normalform2(s,g,cmode)>>;

symbolic procedure nc!-normalform2(s,g,cmode);
 % Normal form 2: full reduction.
  begin scalar g0,ev,f,s1,b;
loop:s1:=s;
        % unwind to last reduction point.
     if ev then while vevcomp(vdpevlmon s1,ev)>0 do s1:=vdpred s1;
loop2:if vdpzero!? s1 then return s;
     ev:=vdpevlmon s1;b:=vdplbc s1;
     g0:=g;f:=nil;
     while null f and g0 do
      if vevdivides!?(vdpevlmon car g0,ev) then f:=car g0 else g0:=cdr g0;
     if null f then<<s1:=vdpred s1;go to loop2>>;
     s:=nc!-reduce1(s,b,ev,f);
     if !*trgroebs then<<prin2 "//";prin2 vdpnumber f>>;
     if cmode then s:=groebsimpcontnormalform s;
     go to loop end;

endmodule;;end;
