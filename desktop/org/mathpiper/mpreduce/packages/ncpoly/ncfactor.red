module ncfactor;  % factorization for non-commutative polynomials.

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


% version 1.4: using the commutative factorizer as preprocessor.
% Oct 2001: using "sove", hoping, that the user did not switch off 'varopt'.

share nc_factor_time;    % time limit in milliseconds.

nc_factor_time:=0;

algebraic operator cc!*;

symbolic procedure nc_factorize u;
 begin scalar r,o,!*gsugar,comm,cr,cl;
   o:=apply1('torder,'(gradlex));
   nc!-gsetup();
   comm := nc_commfactors!* u;
   cl:=car comm; u:=cadr comm;cr:=caddr comm;
   if constant_exprp u then (if u neq 1 then cl:=u.cl)
      else
     r:=for each p in nc_factorize0(a2ncvdp u,nil,nil,nil,nil,nil)
          collect num vdp2a p;
   o:=apply1('torder,{o});
   return'list.append(cl,append(r,cr))end;

symbolic operator nc_factorize;

% copyd('nc_commfactors!*,'nc_commfactors);
symbolic procedure nc_commfactors u;
 begin scalar o,!*gsugar,comm,cr,cl;
   o:=apply1('torder,'(gradlex));
   nc!-gsetup();
   comm:=nc_commfactors!* u;
   cl:=car comm;u:=cadr comm;cr:=caddr comm;
   o:=apply1('torder,{o});
   return{'list,'list.cl,u,'list.cr}end;

symbolic operator nc_commfactors;

symbolic procedure nc_commfactors!* u;
 (begin scalar f,ff,uu,comm,l,crl,cll,!*ncg!-right,w;
   uu:=sublis(ncpi!-names!*,numr simp u);
   comm:=(fctrf reorder uu) where ncmp!*=nil;
   if null cddr comm and cdadr comm=1 then
   <<if !*trnc then writepri("no commutative factors found",'only);
     go to no_comm>>;
   l:=for each f in cdr comm join
     for i:=1:cdr f collect reval prepf car f;
   if !*trnc then writepri("testing commutative factors:",'only);
   uu:=a2ncvdp u;
   while l do
   <<f:=car l;l:=cdr l;
     if !*trnc then writepri(mkquote f,'first);
     !*ncg!-right:=right;
     if vdpzero!? cdr(w:=nc!-qremf(uu,ff:=a2ncvdp f))then
     <<if !*trnc then writepri(nc_dir(),'last);cll:=append(cll,{f});uu:=car w>>
     else
     if vdpzero!? cdr<<!*ncg!-right:=not right;w:=nc!-qremf(uu,ff)>>
       then<<if !*trnc then writepri(nc_dir(),'last);crl:=f.crl;uu:=car w>>
     else if !*trnc then writepri(" -- discarded",'last)>>;
  if null crl and null cll then go to no_comm;
  u:=vdp2a uu;
  if !*trnc then
    <<writepri("remaining noncom  part:",'first);writepri(mkquote u,'last)>>;
 no_comm:return {crl,u,cll};
 end)where right=!*ncg!-right;

symbolic procedure nc_dir();if !*ncg!-right then " right" else " left";

symbolic procedure oneside!-factor(w,m,all);
 % NOTE: we must perform a factorization based on left
 % division (m='l) for obtaining a right factor.
 begin scalar u,d,r,mx,o,!*gsugar;
    % preprocessing for psopfn.
  d:=r:=0;
  u:=reval car w;
  if cdr w then<<d:=reval car(w:=cdr w);if cdr w then r:=reval cadr w>>;
    % preparing for the altorithm.
  o:=apply1('torder,'(gradlex));
  nc!-gsetup();
  if r=0 or r='(list)then r:=nil else
  <<r:=cdr listeval(r,nil);
    r:=vdpevlmon a2vdp(if null cdr r then reval car r else
      'times.for each y in r collect reval y)>>;
  d:=reval d;
  if d=0 then d:=1000 else if not fixp d then<<mx:=vdpevlmon a2vdp d;d:=1000>>;
  r:=nc_factorize0(a2ncvdp u,m,d,r,mx,all);
  o:=apply1('torder,{o});
  return for each w in r collect num vdp2a w end;

put('left_factor,'psopfn,
     function (lambda(w);<<w:=oneside!-factor(w,'r,nil) or w;reval car w>>));

put('left_factors,'psopfn,
     function (lambda(w);'list. oneside!-factor(w,'r,t)));

put('right_factor,'psopfn,
     function (lambda(w);<<w:=oneside!-factor(w,'l,nil) or w;reval car w>>));

put('right_factors,'psopfn,
     function (lambda(w);'list.oneside!-factor(w,'l,t)));

algebraic procedure nc_factorize_all u;
  % Compute all possible factorizations based on successive
  % right factor extraction.
 begin scalar !*ncg!-right,d,f,w,wn,q,r,trnc,nc_factor_time!*;
   nc_factor_time!*:=lisp time();
   trnc:=lisp !*trnc;lisp(!*trnc:=nil);
   w:={{u}};r:={};lisp(!*ncg!-right:=nil);
 loop:if w={} then go to done;
   lisp(wn:='(list));
   for each c in w do
   <<lisp(q:= cadr c);
     f:=right_factors(q,{},{});
     if trnc then write "ncfctrall: Right factors of (",q,"): ",f;
     if f={} then r:=c.r;
     for each fc in f do
     <<d:=nc_divide(q,fc);
       if trnc then write "ncfctrall: Quotient (",q,") / (",fc,"): ",d;
       wn:=(first d.fc.rest c).wn>>>>;
   w:=wn; go to loop;
 done:lisp(!*trnc:=trnc);
   return r end;

symbolic procedure nc_factorize0(u,m,d,rs,mx,all);
 <<if not numberp nc_factor_time!* then nc_factor_time!*:=time();
   nc_factorize1(u,m,d,rs,mx,all)>>where nc_factor_time!*=nc_factor_time!*;

symbolic procedure nc_factorize1(u,m,d,rs,mx,all);
 % split all left(right) factor of u off.
 % u:  polynomial,
 % m:  mode: restriction for left or right factor:
 % d:  maximum degree restriction,
 % r:  variable set restriction (r is an exponent vector).
 % mx: maximum exponent for each variable (is an exponent vector).
 % all: true if we look for all right(left) factors.
 begin scalar ev,evl,evlx,f,ff,!*ncg!-right;
  nc_factorize_timecheck();
  mx:=if null mx then for each y in vdpvars!* collect 1000 else
    for each y in mx collect if y>0 then y else 1000;
  if !*trnc then<<prin2 "factorize ";vdpprint u>>;
  ev:=vdpevlmon u;
  if vevzero!? ev then return{u};
  d:=d or vevtdeg ev/2;
  evlx:=sort(nc_factorize1!-evl ev,function(lambda(x,y);vevcomp(x,y)<0));
  if m='r then go to r;
    % factors up to n
  evl:=evlx;
  while (null f or all) and evl and vevtdeg car evl<=d do
  <<if not vevzero!? car evl
      and car evl neq ev
           % testing support;
      and(null rs or vevmtest!?(car evl,rs))
           % testing maximal degrees;
      and vevmtest!?(mx,car evl)
    then f:=append(f,nc_factorize2(u,car evl,rs,mx,all));
   evl:=cdr evl>>;
  if f or m='l then go to c;
    % right factors up to tdg-n
  d:=vevtdeg ev -d;
r:!*ncg!-right:=t;
  evl:=evlx;
  while (null f or all)and evl and vevtdeg car evl<=d do
  <<if not vevzero!? car evl
        and car evl neq ev
           % testing support;
        and(null rs or vevmtest!?(car evl,rs))
           % testing maximal degrees;
      and vevmtest!?(mx,car evl)
     then f:=append(f,nc_factorize2(u,car evl,rs,mx,all));
    evl:=cdr evl>>;
c:if null f then return if m then nil else{u};
  if all then return f;
    % only one factor wanted?
  if m then return{cdr f};
  ff:=nc_factorize1(car f,nil,nil,nil,mx,all);
  return if !*ncg!-right then append({cdr f},ff)else append(ff,{cdr f})end;

symbolic procedure nc_factorize1!-evl u;
  % Collect all monomials dividing u.
   if null u then'(nil) else
   (for i:=0:car u join
     for each e in w collect i.e)where w=nc_factorize1!-evl cdr u;

algebraic operator ncc!@;

symbolic procedure nc_factorize2(u,ev,rs,mx,all);
  begin scalar ar,p,q,vl,r,s,so,sol,w,y;integer n;
   scalar !*bcsubs2;
   nc_factorize_timecheck();
   p:=a2dip 0;
   if !*trnc then
   <<prin2 if !*ncg!-right then "right " else "left ";
     prin2 "Ansatz for leading term > ";
     vdpprin2 vdpfmon(a2bc 1,ev);
     prin2 " < time so far:";
     prin2 (time()-nc_factor_time!*);
     prin2t "ms">>;
     % establish formal Ansatz.
   for each e in nc_factorize2evl(ev,rs,mx) do
   <<q:={'ncc!@,n:=n+1};p:=dipsum(p,dipfmon(a2vbc q,e))>>;
   w:=p;
   while not dipzero!? w do<<vl:=bc2a diplbc w.vl;w:=dipmred w>>;
   vl:=reversip vl;
   p:=dip2vdp p;
     %  prin2 "complete Ansatz:";vdpprint p;
     % pseudo division.
   r:=nc!-normalform(u,{p},nil,nil);
   nc_factorize_timecheck();
   while not vdpzero!? r do<<s:=vbc2a vdplbc r.s;r:=vdpred r>>;
   if !*trnc then
   <<prin2t "internal equation system:";writepri(mkquote('list.s),'only)>>;
     % solve system
     % 1. look for a free variable:
     %###### but that must be the leading variable!!!
   for each v in vl do if not smember(v,s) then so:=v;
   if !*trnc and so then<<prin2"free:";prin2t so>>;
   if so then sol:={(so.1).for each v in vl collect v.0};
   if null sol or all then sol:=append(sol,nc_factsolve(s,vl,all));
   if null sol then return nil;
   if !*trnc then
   <<prin2t "internal solutions:";
     for each s in so do
     <<for each q in s do
       <<writepri(mkquote car q,'first);
         writepri(mkquote " = ",nil);
         writepri(mkquote cdr q,'last)>>;
       prin2t "=====================================">>;
   % prin2 "check internal solution:";
   % for each e in s do writepri(mkquote aeval sublis(so,e),'only);
   >>;
coll:nc_factorize_timecheck();
   so:=car sol;sol:=cdr sol;
   y:=dip2vdp dippolish dipsubf(so,vdppoly p);
     % leading term preserved?
  % if vdpevlmon y neq vdpevlmon p then
   %  return nil;
   %  prin2 "computed factor:";vdpprint y;
   if vevzero!? vdpevlmon y then
      if not all then return nil else
      if sol then go to coll else go to done_all;
     % turn on bcsubs2 if there is an algebraic number.
   if smemq('expt,y) or smemq('sqrt,y) or smemq('root_of,y) then !*bcsubs2:=t;
   w:=nc!-qremf(u,y);
   if not vdpzero!? cdr w then
    <<prin2 "division failure";
      vdpprint u;prin2t "/";
      vdpprint y;prin2 "=> ";vdpprint car w;
      prin2 "rem: ";vdpprint cdr w;
       rederr "noncom factorize">>;
   if !*trnc then
   <<terpri();prin2 "splitting into > ";
     vdpprin2 car w;prin2t " < and";prin2 " > ";
     vdpprin2 y;prin2t " <";terpri()>>;
   ar:=y.ar;
   if all then if sol then go to coll else go to done_all;
done_one:return car w.y;
done_all:return ar end;

symbolic procedure nc_factsolve(s,vl,all);
  begin scalar v,sb,ns,so,soa,sol,nz,w,q,z,r,abort;
    % 1st phase: divide out leading term variable,
    % remove zero products, and terminate for explicitly
    % unsolvable system.
   v:=numr simp car vl;
   ns:=for each e in s collect numr simp e;
    % remove factors of leading coefficient,
    % remove trivial parts and propagate them into system.
   r:=t;
   while r do
   <<r:=nil; s:=ns; ns:=nil;
     for each e in s do if not abort then
     <<e:=absf numr subf(e,sb);
       while(q:=quotf(e,v))do e:=q;
       if null e then nil else
       if domainp e or not(mvar e member vl)then abort:=t else
       if null red e and domainp lc e then
       <<w:=mvar e;sb:=(w.0).sb;r:=t;vl:=delete(w,vl)>>
       else if not member(e,ns)then ns:=e.ns>>>>;
   if abort or null vl then return nil;
   nc_factorize_timecheck();
     % all equations solved, free variable(s) left
   if null ns and vl then
   <<sol:={for each x in vl collect x.1};go to done>>;
     % solve the system.
   s:=for each e in ns collect prepf e;
   if !*trnc then
    <<prin2 "solving ";
      prin2 length s;prin2 " polynomial equations for ";
      prin2 length vl;
      prin2t"variables";
      for each e in s do writepri(mkquote e,'only)>>;
        % modification HM 24.10.2001: introduction of the fluid variable
        % '*varoptt' and setting it 't' locally.
   w:=(cdr solveeval{'list.s,'list.vl} where dipvars!*=nil);
     % Select appropriate solution.
 loop:nc_factorize_timecheck();
   if null w then go to done;
   so:=cdr car w;w:=cdr w;soa:=nil;
   if smemq('i,so)and null !*complex then go to loop;
     % Insert values for non occurring variables.
   for each y in vl do if not smember(y,so)then<<soa:=(y.1).soa; nz:=t>>;
   for each y in so do
   <<z:=nc_factorize_unwrap(reval caddr y,soa);
     nz:=nz or z neq 0;
     soa:=(cadr y.z).soa>>;
     % don't accept solution with leading term 0.
   if not nz then go to loop;
   q:=assoc(car vl,soa);
   if null q or cdr q=0 then go to loop;
   % Make sure solutions are in lowest terms.
   soa:=for each j in soa collect(car j.sublis(soa,cdr j));
   sol:=soa.sol;
   if all then go to loop;
 done:sol:=for each s in sol collect append(sb,s);
   if !*trnc then
    <<prin2t "solutions:";
      for each w in sol do
       writepri(mkquote('list.
         for each s in w collect{'equal,car s,cdr s}),'only);
      prin2t "-------------------------">>;
   return sol end;

symbolic procedure dipsubf(a,u);
  % construct polynomial u with coefficients from a.
 if dipzero!? u then nil else
  <<q:=if q then cdr q else diplbc u;
    if q neq 0 then dipmoncomp(a2bc q,dipevlmon u,r) else r>>
      where q=assoc(bc2a diplbc u,a),r=dipsubf(a,dipmred u);

symbolic procedure dippolish p1;diprectoint(p1,diplcm p1);

symbolic procedure nc_factorize_unwrap(u,s);
   if atom u then u else
   if eqcar(u,'arbcomplex)then 1 else
   (if q then cdr q else
   for each x in u collect nc_factorize_unwrap(x,s))where q=assoc(u,s);

symbolic procedure nc_factorize2evl(ev,rs,mx);
  % make list of monomials below ev in gradlex ordering,
  % but only those which occur in rs (if that is non-nil)
  % and which have the maximal degress of mx.
 for each q in nc_factorize2!-evl1(min(evtdeg mx,evtdeg ev),length ev,rs)
  join if not vevcompless!?(ev,q) and vevmtest!?(mx,q)then{q};

symbolic procedure nc_factorize2!-evl1(n,m,rs);
% Collect all 'm' exponent vectors with total degree <='n'.
   if m=0 then'(nil)else
   for i:=0:(if null rs or car rs>0 then n else 0)join
     for each e in nc_factorize2!-evl1(n#-i,m#-1,if rs then cdr rs)
       collect i.e;

symbolic procedure nc_factorize_timecheck();
   if fixp nc_factor_time and nc_factor_time>0 and
     (time() - nc_factor_time!*) > nc_factor_time
       then rederr "time overflow in noncom. factorization";

endmodule;;end;
