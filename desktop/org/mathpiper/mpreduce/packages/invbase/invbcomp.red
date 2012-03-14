module invbcomp;

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


%----------------------------------------------------------------------
symbolic proceDURE C_ZERO();  nil$           %  REPRESENTATION OF ZERO
%----------------------------------------------------------------------
symbolic procedure CNEG(C);                  %  - C
negf c$
%----------------------------------------------------------------------
symbolic procedure CSUM(C1,C2);              %   C1 + C2
addf(c1,c2);
%----------------------------------------------------------------------
symbolic procedure CPROD(C1,C2);             %   C1 * C2
multf(c1,c2);
%----------------------------------------------------------------------
symbolic procedure CDIV(C1,C2);               %   C1/C2
numr resimp(c1 . c2);
%----------------------------------------------------------------------
symbolic procedure trass(id,value);  % tracing of assignments
<< terpri(); write id; write " = "; write value; terpri(); >>$
%----------------------------------------------------------------------
symbolic procedure leftzeros(u);  % u : list
if null u or car u neq 0 then 0 else 1 #+ leftzeros cdr u$
%----------------------------------------------------------------------
procedure class(jet);
if ord jet = 0 then 0 else 1
#+ leftzeros reverse (if ordering = 'lex then jet else cdr jet);
%----------------------------------------------------------------------
symbolic procedure ord(jet);
if ordering = 'lex then eval('plus . jet) else car jet$
%----------------------------------------------------------------------
symbolic procedure ljet(p); caar p$
%----------------------------------------------------------------------
symbolic procedure sub01(v,u);
%%% replace each x in u by < if x=v then 1 else 0 >
if u then (if (car u = v) then 1 else 0) . sub01(v,cdr u);
%----------------------------------------------------------------------
symbolic procedure !*v2j(v);
if ordering = 'lex then sub01(v,varlist) else (1 . sub01(v,varlist) );
%----------------------------------------------------------------------
symbolic procedure nonmult(cl);  % --> list of vjets
reverse cdr member(nth(reverse vjets,cl),reverse vjets);
%----------------------------------------------------------------------
symbolic procedure insert(x,gg);
begin scalar gg1;
   while gg and dless(cdr x,cdar gg) do
   << gg1 := car gg . gg1; gg := cdr gg >>;
   return append(reversip gg1, x . gg);
end;
%----------------------------------------------------------------------
symbolic procedure addnew(f,ind,ff);
%%% adds element f to set (with index ind), returns modified ff
<< putv(gv,ind,f); putv(bv,ind,t);
   if null f then ff
   else ff := insert(ind . ljet f, ff)
>>$
%----------------------------------------------------------------------
symbolic procedure dlesslex(D1,D2);
%%%%  RETURNS T IF D1 < D2 (lex), NIL OTHERWISE
IF NULL D1 THEN NIL ELSE
IF CAR D1 #> CAR D2 THEN NIL ELSE
IF CAR D1 #< CAR D2 THEN T ELSE dlesslex(CDR D1,CDR D2);
%----------------------------------------------------------------------
symbolic procedure dless(d1,d2);   % --> T if d1 < d2 , NIL otherwise
if ordering = 'lex then dlesslex(d1,d2) else
if car d1 #< car d2 then t else if car d1 #> car d2 then nil
else if ordering = 'glex then dlesslex(cdr d1,cdr d2)
else if ordering = 'grev then dlesslex(reverse cdr d2, reverse cdr d1);
%-----------------------------------------------------------------------
symbolic procedure DDMULT(D1,D2);
IF NULL D1 THEN NIL ELSE (CAR D1 #+ CAR D2) . DDMULT(CDR D1,CDR D2);
%-----------------------------------------------------------------------
symbolic procedure DQUOT(D2,D1);
%%%%  RETURNS D2-D1 IF D1 DIVIDES D2, NIL OTHERWISE
BEGIN SCALAR D3; INTEGER N;
L1:N:=CAR(D2)-CAR(D1);
   IF N #< 0 THEN RETURN NIL;
   D3:=N . D3;
   D1:=CDR D1; D2:=CDR D2;
   IF D1 THEN GOTO L1;
   RETURN REVERSIP D3;
end;
%-----------------------------------------------------------------------
symbolic procedure PCMULT(P,C);              %  P*C  (C IS NOT ZERO)
FOR EACH X IN P COLLECT CAR X.CPROD(C,CDR X);
%-----------------------------------------------------------------------
symbolic procedure pcdiv(p,c);               %  P/C  (division in ring)
for each x in p collect car x . cdiv(cdr x,c);
%-----------------------------------------------------------------------
symbolic procedure PDMULT(P,D);              %  P*< D >
FOR EACH X IN P COLLECT
 (FOR EACH Y IN PAIR(CAR X,D) COLLECT CAR(Y)#+CDR(Y)).CDR X$
%-----------------------------------------------------------------------
symbolic procedure PSUM(P1,P2);              %  P1 + P2
BEGIN SCALAR T1,T2,D2,C3,P3,SUM,RET;
   IF NULL P1 THEN SUM:=P2 ELSE
   IF NULL P2 THEN SUM:=P1 ELSE
   WHILE P2 AND NOT RET DO
   << T2:=CAR P2; D2:=CAR T2;
      WHILE P1 AND DLESS(D2,CAAR P1) DO
      << P3:=CAR(P1).P3; P1:=CDR P1 >>;
      IF NULL P1 THEN
      << SUM:=APPEND(REVERSE P3,P2); RET:=T >> ELSE
      << T1:=CAR P1;
         IF D2=CAR T1 THEN                     %%%%  NOW T1<=T2
         << C3:=CSUM(CDR T1,CDR T2);           %%%%  LIKE TERM
            IF C3 neq C_ZERO() THEN P3:=(D2.C3).P3;
            P1:=CDR P1;
            T1:=IF P1 THEN CAR P1;              %%%%  NEW T1
         >>
         ELSE P3:=T2.P3;                        %%%%  OLD T1
         P2:=CDR P2;                            %%%%  NEW T2
         IF NULL P2 THEN SUM:= APPEND(REVERSE P3,P1)
   >> >>;
   RETURN SUM
end;
%-----------------------------------------------------------------------
symbolic procedure PNEG(P);                  %  - P
FOR EACH X IN P COLLECT CAR(X).CNEG(CDR(X));
%-----------------------------------------------------------------------
symbolic procedure PDIF(P1,P2);              %  P1 - P2
PSUM(P1,PNEG P2);
%-----------------------------------------------------------------------
symbolic procedure DD(D1,D2);   %  uses fluid VJETS
begin scalar dq,lz;
   dq:=dquot(d2,d1);
   if not dq then return if dless(d1,d2) then 1  % D1 < D2
                                         else 0; % D1 > D2
   if ordering neq 'lex then dq:=cdr dq;
   lz := leftzeros dq;
   return
   if not nc and not(lz #< length varlist #- class d1)
      then 3   % D1 divides D2 (mult.)
   else if nc and not(lz #< length varlist #- nc)
      then 4   % D1 divides D2 in 1:nc classes and coincides in others
      else 2;  % D1 divides D2 (usual)
end;
%-----------------------------------------------------------------------
symbolic procedure dlcm(d1,d2);
if ordering='lex then for each x in pair(d1,d2) collect max(car x,cdr x)
else addgt( for each x in pair(cdr d1,cdr d2) collect max(car x,cdr x));
%-----------------------------------------------------------------------
symbolic procedure NF(H,GG,sw);
%%%%  H = NORMALIZED POLYNOMIAL
%%%%  GG = LIST OF KEYED LPP'S OF GG-SET
%%%%  RETURNS NORMAL FORM OF H WITH RESPECT TO GG-SET
%%%%  ===============================================
IF NULL GG THEN H ELSE
BEGIN SCALAR F,LPF,g,c,cf,cg,NF,G1,G2,U,nr; nr:=0;
   F:=H; G1:=GG;
NEXTLPF: IF NULL F THEN goto EXIT;
   LPF:=caar F;

%  diminish G1 so that LPF >= G1 (and might be reduced !)
%  ------------------------------------------------------
   WHILE NOT NULL G1 AND DLESS(LPF,CDAR G1) DO G1:=CDR G1;
   IF NULL G1 THEN goto EXIT;
   G2:=G1;                                     % NOW LPF >= G2

%  reduction of LPF
%  ----------------
   WHILE G2 AND DD(CDAR G2,LPF) #< sw + 2 DO G2:=CDR G2;

   IF NULL G2 THEN                             % LPF NOT REDUCED
   ( if redtails then << NF:=(LPF.CDAR F).NF; F:=CDR F >>
     else goto EXIT )
   ELSE                                        % REDUCTION OF LPF
   << G:=getv(gv,caar g2);
      C:=gcdf!*(cdar F, cdar G);
      cf:=cdiv(cdar f,c); cg:=cdiv(cdar g,c);
      f:=pcmult(f,cg); nf:=pcmult(nf,cg); g:=pcmult(g,cf);
      U:=PDMULT(CDR G, DQUOT(LPF,CDAR G2));
      if tred then
      << terpri();
         write "r e d u c t i o n :  ",lpf,"/",cdar g2;
         terpri();
      >>;
      if stars then write "*";
      nr := nr #+ 1;
      F:=PDIF(CDR F,U);
   >>;
   GOTO NEXTLPF;
EXIT:
   reductions := reductions #+ nr;
   nforms := nforms #+ 1;
   u:= gcdout append(reversip nf,f);
   if null u then zeros := zeros #+ 1;
   return u;
end;
%-----------------------------------------------------------------------
symbolic procedure gcdout(p);
   % cancel coeffs of P by their common factor.
if !*modular then p else
if null p then nil else if ord ljet p = 0 then p else
begin scalar c,p1;
   p1:=p; c:=cdar p1;
   while p1 and c neq 1 do << c:=gcdf!*(c,cdar p1); p1:=cdr p1 >>;
   return if c = 1 then p else pcdiv(p,c);
end;
%-----------------------------------------------------------------------
expr PROCEDURE NEWBASIS(gg,sw)$
%%%% SIDE EFFECT:   CHANGES CDR'S OF GV(K);
BEGIN SCALAR G1,G2;
   G1:=reverse GG;
   WHILE G1 DO
   << PUTV(GV,caar g1,NF(GETV(GV,caar g1),G2,sw));
      g2:=(car g1).g2; g1:=cdr g1;
   >>;
END$
%-----------------------------------------------------------------------
symbolic procedure !*f2di(f,varlist);
%%% f: st.f., varlist: kernel list --> f in distributive form
if null f then nil else
if domainp f then
((addgt for each v in varlist collect 0).(f)).nil else
psum(if member(mvar f,varlist) then
     pdmult(!*f2di(lc f,varlist),
            addgt for each v in varlist collect
            if v = mvar f then ldeg f else 0
           )
     else  pcmult(!*f2di(lc f,varlist),((lpow f.1).nil)),
     !*f2di(red f,varlist)  );
%-----------------------------------------------------------------------
symbolic procedure !*di2q0(p,varlist);
if null p then nil . 1 else
addsq( (lambda s,u;
        << for each x in u do
          if cdr x neq 0 then s:=multsq(s,((x.1).nil).1);
          s  >>
       ) (cdar p, pair(varlist,
                       if ordering='lex then ljet p else cdr ljet p)),
       !*di2q0(cdr p,varlist) );
%----------------------------------------------------------------------
symbolic procedure !*di2q(p,varlist);
!*di2q0(for each x in p collect car x . (cdr x . 1), varlist);
%----------------------------------------------------------------------
symbolic procedure show(str,p);  % p = poly in a special (dist.) form
if null p then (algebraic write str," := 0")
else algebraic write str," := ",
lisp prepsq !*di2q(list car p, varlist)," + ",
lisp prepsq !*di2q(cdr p, varlist);
%----------------------------------------------------------------------
LISP procedure ADDGT(U);
if ordering = 'lex then u else eval('plus.u) . u$
%-----------------------------------------------------------------------
symbolic procedure printsys(str,gg);
begin scalar i; i:=0;
   for each x in gg do
   << i:=i+1;
      algebraic write str,"(",lisp i,") := ",
      lisp prepsq !*di2q(list car getv(gv,car x), varlist)," + ",
      lisp prepsq !*di2q(cdr getv(gv,car x), varlist);
   >>;
end;
%-----------------------------------------------------------------------
symbolic procedure answer(gg);
<< if title then algebraic write "% ",lisp title;
   trass("% ORDERING",varlist); printsys("G",reverse gg);
>>$
%-----------------------------------------------------------------------
symbolic procedure wr(file,gg);
<< off nat,time; out file;
   write "algebraic$"; write "operator g$";
   answer(gg);
   write "end;"; shut file; on nat,time >>$
%-----------------------------------------------------------------------
symbolic procedure invtest!*();
begin scalar g,c; c:=t;
   if !*trinvbase then terpri();
   for each x in gg do
   if c then
   << g:=getv(gv, car x);
      for each vj in nonmult(class ljet g) do
      if c and nf(pdmult(g,vj),gg,1) then
      << c:=nil;
         if !*trinvbase then prin2t "INV - t e s t  f a i l e d"; >>;
   >>;
   if c and !*trinvbase then prin2t "I n v o l u t i v e  b a s i s";
   return c;
end;
%-----------------------------------------------------------------------
symbolic procedure redall(gg,ff,sw);
   % side effect : changes flag thirdway.
begin scalar rr,f,f1,lj,k,new;
   rr := ff; thirdway:=shortway:=nil; new:=t;
   while rr do
   << f:=car reverse rr; rr:=delete(f,rr);
      k:=car f; f1:=getv(gv,k);
      if path then
      << % write k,": ";
         if new then write ljet f1," ==> "
         else write ljet f1," --> ";
      >>;
      f:=putv(gv,k,nf(f1,gg,sw));
      lj:=if f then ljet f else 0;
      if path then
      << write lj; terpri() >>;
      if null f then  nil else
      if ord lj = 0 then conds := f . conds  else
      << if ljet f neq ljet f1 then shortway:=t;
         if not new and f neq f1 then thirdway:=t;
         for each x in gg do if dd(lj,cdr x) >= sw + 2  then
         << gg:=delete(x,gg); rr:=insert(x,rr);
            putv(bv,car x,t); %
         >>;
         gg:=insert(k.lj,gg); new:=nil;
   >> >>;
   return gg;
end;
%-----------------------------------------------------------------------
symbolic procedure remred(ff,sw);  % removes redundant elements
begin scalar gg,gg1,f,g,p;
   ff:=reverse ff;
   while ff do
   << f:=car ff; ff:=cdr ff;
      p:=t; gg1:=gg;
      while p and gg1 do
      << g:=car gg1; gg1:=cdr gg1;
         if dd(cdr g,cdr f) >= sw + 2  then p:=nil;
      >>;
      if p then gg := f . gg;
   >>;
   return gg;
end;
%-----------------------------------------------------------------------
symbolic procedure invbase!*();
begin scalar gg1,g,k,nm,f,thirdway,shortway,fin,p,p0,lb,r;
   if !*trinvbase then terpri();
   p:=maxord:=-1;
   if path then terpri();
   gg:=redall(nil,gg,1);
   newbasis(gg,1);
   lb:=0;
   for each x in gg do lb:=lb + ord cdr x;
   lb:=lb + length varlist - 1;
l: gg1 := reverse gg;
   while gg1 and null getv(bv,caar gg1) do gg1 := cdr gg1;
   if gg1 then
   << if cadar gg1 = cadar gg then
      << p0:=p;
         p:=cadar gg1;
         if !*trinvbase and p > p0 then
         << terpri();
            write "---------- ORDER = ",cadar gg," ----------";
            terpri(); terpri();
         >>;
         if p > lb then
         << gg:=redall(nil,gg,0);
            newbasis(gg,0);
            invtempbasis := 'list .
                for each x in gg
                   collect 'plus . for each m in getv(gv,car x)
                      collect prepsq !*di2q(list m,varlist);
            rederr "Maximum degree bound exceeded.";
         >>;
         maxord:=max(maxord,cadar gg);
         if cadar gg < maxord then fin:=t;
      >>;
      if fin then goto m;
      k := caar gg1;
      g := getv(gv,k); putv(bv,k,nil);
      nm := nonmult(class ljet g);
      for each vj in nm do
      << ng := ng + 1;
         f := pdmult(g,vj); putv(gv,ng,f); putv(bv,ng,t);
         gg := redall(gg,list(ng.ljet f),1);
         if thirdway then newbasis(gg,1) else
         if shortway then for each y in gg do if car y neq ng then
         putv(gv,car y,nf(getv(gv,car y),list(ng.ljet getv(gv,ng)),1));
      >>;
      go to l;
   >>;
   m: stat(); if p <= lb then dim gg;
end;
%-----------------------------------------------------------------------
symbolic procedure njets(n,q);   % number of jets of n vars and order q
combin(q,q+n-1);
%----------------------------------------------------------------------
symbolic procedure combin(m,n);    % number of combinations of m from n
if m>n then 0 else
begin integer i1,i2; i1:=i2:=1;
   for i:=n-m+1:n do i1:=i1*i; for i:=1:m do i2:=i2*i;
   return i1/i2;
end;
%----------------------------------------------------------------------
symbolic procedure dim(gg);
if !*trinvbase then
begin integer q,n,cl,s,y,dim,dp,mon;
   q:=cadar gg; n:=length varlist; dim:=0;
   for i:=1:n do putv(beta,i,0);
   for each x in gg do
   << cl:=class cdr x;
      for i:=cl step -1 until 1 do
      << y:=njets(cl-i+1,q-ord cdr x);
         putv(beta,i,getv(beta,i)+y);
   >> >>;
   terpri();
   for i:=1:n do
   << putv(alfa,i,combin(n-i,q+n-i)-getv(beta,i));
      if getv(alfa,i) neq 0 then dim := dim + 1;
      % write "a[",i,",",q,"]=",getv(alfa,i)," ";
   >>;
   terpri();
   terpri(); write "D i m e n s i o n  =  ",dim; terpri();
   if dim = 0 then nroots gg;
end;
%----------------------------------------------------------------------
symbolic procedure nroots(gg);
   % number of roots of zero-dimensional Ideal.
if gg then begin integer d;
   for each x in gg do d := d + car reverse x;
   terpri(); write "N u m b e r  o f  s o l u t i o n s  =  ",d;
   terpri();
end;
%----------------------------------------------------------------------
symbolic procedure stat();
if !*trinvbase then
<< terpri();
   write "reductions = ",reductions;
   write "  zeros = ",zeros;     write "  maxord = ",maxord;
   write "  order = ",cadar gg;  write "  length = ",length gg;
>>$
%----------------------------------------------------------------------
symbolic procedure !*g2lex(p);
   % works correctly only when ORDERING= lex.
   %%% p: poly in graduate form ---> lexicographic form
begin scalar p1;
   for each x in p do p1:=psum(p1,list(cdar x . cdr x));
   return p1;
end;
%----------------------------------------------------------------------
symbolic procedure lexorder(lst);
   % works correctly only when ORDERING = lex.
begin scalar lst1,lj;
   for each x in lst do
   << lj:=ljet putv(gv, car x, gcdout !*g2lex getv(gv,car x));
      lst1 := insert((car x).lj, lst1);
   >>;
   return lst1;
end;
%----------------------------------------------------------------------
symbolic procedure gi(gg,i);  % subsystem of GG of class = i
begin scalar ff;
   for each x in gg do if class cdr x = i then ff := x . ff;
   return ff;
end;
%----------------------------------------------------------------------
symbolic procedure monic(jet,cl);  % for lexicoraphy only
begin scalar u,n;
   jet:=reverse jet;
   n:=length varlist;
   for i:=1:n do if i neq cl then u:=nth(jet,i).u;
   return u = for each v in cdr varlist collect 0$
end;
%----------------------------------------------------------------------
symbolic procedure monicmember(gg,cl);
begin scalar p;
l: if null gg then return nil;
   if monic(cdar gg,cl) then return t;
   gg:=cdr gg;
   go to l;
end;
%----------------------------------------------------------------------
symbolic procedure montest(gg);
begin scalar p,n;
   p:=t;
   n:=length varlist;
   for i:=1:n do if not monicmember(gg,i) then << p:=nil; i:=n+1 >>;
   return p;
end;
%----------------------------------------------------------------------
symbolic procedure invlex!*();  % side effect: changes GG
begin scalar gi,n,gginv,ordering;
   n:=length varlist; gginv:=mkvect n;
   ordering:='lex;
   for i:=1:n do putv(gginv,i,lexorder gi(gg,i));
   gg:=nil;
   for i:=1:n do
   << nc:=i;
      if path then << trass("i",i); terpri() >>;
      gg:=redall(gg,getv(gginv,i),2);
      if montest gg then i:=n+1;
   >>;
   nc:=nil;
   gg:=remred(gg,0);
   newbasis(gg,0);
end;
%----------------------------------------------------------------------
symbolic procedure readsys(elist,vlist);
begin;
   varlist:=cdr vlist;
   ng:=reductions:=nforms:=zeros:=0;
   alfa:=mkvect length varlist;
   beta:=mkvect length varlist;
   gg:=nil;
   for each x in cdr elist do
   gg:=addnew(gcdout !*f2di(numr simp x, varlist), ng:=ng+1, gg);
   vjets:=for each v in varlist collect !*v2j(v);
end;
%-----------------------------------------------------------------------
lisp operator readsys$
%-----------------------------------------------------------------------

%        D E F A U L T  V A L U E S
% ======================================================================
  ordering:='grev$ redtails:=t$
  tred := path := stars := nil$
% ======================================================================

endmodule;

end;
