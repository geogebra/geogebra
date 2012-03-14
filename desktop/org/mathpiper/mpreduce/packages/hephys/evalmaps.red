module evalmaps;  % Interaction with alg mode: variant without nonlocs;

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


  exports    strand!-alg!-top $
  imports    color!-strand,contract!-strand $

%------------------ AUXILIARY ROUTINES -----------------------------$

symbolic procedure permpl(u,v)$
if null u then t
else if car u = car v then permpl(cdr u,cdr v)
     else not permpl(cdr u,l!-subst1(car v,car u,cdr v))$

symbolic procedure repeatsp u$
if null u then nil
else (member(car u,cdr u) or repeatsp cdr u )$

symbolic procedure l!-subst1(new,old,l)$
if null l then nil
else if old = car l then new . cdr l
     else (car l) . l!-subst1(new,old,cdr l)$

%-------------------FORMING ANTISYMMETRIHERS -----------------------$

symbolic procedure propagator(u,v)$
if null u then      1
else if (repeatsp u) or (repeatsp v) then 0
     else 'plus . propag(u,permutations v,v)$

symbolic procedure propag(u,l,v)$
if null l then      nil
else (if permpl(v,car l) then 'times . prpg(u,car l)
      else list('minus,'times . prpg(u,car l) ) ) . propag(u,cdr l,v)$

symbolic procedure prpg(u,v)$
if null u then nil
else list('cons,car u,car v) . prpg(cdr u,cdr v)$

symbolic procedure line(x,y)$
propagator(cdr x,cdr y)$

%------------------ INTERFACE  WITH CVIT3 ---------------------------$

symbolic procedure strand!-alg!-top(strand,map_,edlst)$
begin
  scalar rlst$
      strand:=deletez1(strand,edlst)$
      rlst:=color!-strand(edlst,map_,1)$
      strand:=contract!-strand(strand,rlst) $
 %RINT STRAND$ TERPRI()$
 %RINT RLST$ TERPRI()$
 %RINT EDLST$ TERPRI()$
 return dstr!-to!-alg(strand,rlst,nil)
%ATHPRINT REVAL(W)$ RETURN W
end$


symbolic procedure mktails(side,rlst,dump)$
begin
  scalar pntr,newdump,w,z$
    if null side then return nil . dump$
    pntr:=side$
    newdump:=dump$
 while pntr do  <<  w:=mktails1(car pntr,rlst,newdump)$
                    newdump:=cdr w$
                    z:=sappend(car w,z)$
                    pntr:=cdr pntr >>$
    return z . newdump
end$

symbolic procedure mktails1(rname,rlst,dump)$
begin
 scalar color,prename,z$
       color:=getroad(rname,rlst)$
     if 0 = color then return nil . dump$
     if 0 = cdr rname then
      return (list replace_by_vector car rname) . dump$
 %   IF FREEIND CAR RNAME THEN RETURN (LIST CAR RNAME) . DUMP$
       z:=assoc(rname,dump)$
     if z then return
                if null cddr z then  cdr z . dump
                else (sreverse cdr z) . dump$
  %    PRENAME:=APPEND(EXPLODE CAR RNAME,EXPLODE CDR RNAME)$
       prename:=rname$
       z:= mkinds(prename,color)$
   return z . ((rname . z) . dump)
end$

symbolic procedure mkinds(prename,color)$
if color = 0 then nil
else
     begin
         scalar indx$
    %     INDX:=INTERN COMPRESS APPEND(PRENAME,EXPLODE COLOR)$
          indx:=                       prename . color $
       return indx . mkinds(prename,sub1 color)
     end$

symbolic procedure getroad(rname,rlst)$
if null rlst then 1 % ******EXT LEG IS ALWAYS SUPPOSET TO BE SIMPLE $
else if cdr rname = cdar rlst then
              cdr qassoc(car rname,caar rlst)
     else getroad(rname,cdr rlst) $


symbolic procedure qassoc(atm,alst)$
if null alst then nil
else if eq(atm,caar alst) then car alst
     else qassoc(atm,cdr alst)$

%------------- INTERACTION WITH RODIONOV ---------------------------$

symbolic procedure from!-rodionov x$
begin scalar strand,edges,edgelsts,map_,w$
    edges:=car x$
    map_:=cadr x$
    edgelsts:=cddr x$
    strand := map_!-to!-strand(edges,map_)$
    w:= for each edlst in edgelsts collect
             strand!-alg!-top(strand,map_,edlst)$
    return reval('plus . w )
end$

symbolic procedure top1 x$
mathprint from!-rodionov to_taranov x$

%----------------------- COMBINATORIAL COEFFITIENTS -----------------$

symbolic procedure f!^(n,m)$
if n<m then cviterr "Incorrect args of f!^"
else if n = m then 1
     else n*f!^(sub1 n,m)$

%% This exists in basic REDUCE these days -- JPff
%%symbolic procedure factorial n$
%%f!^(n,0)$

symbolic procedure mk!-coeff1(alist,rlst)$
if null alist then 1
else
   eval ('times .
   for each x in alist collect factorial getroad(car x,rlst) )$

%--------------- CONTRACTION OF DELTA'S -----------------------------$

symbolic procedure prop!-simp(l1,l2)$
prop!-simp1(l1,l2,nil,0,1)$

symbolic procedure prop!-simp1(l1,l2,s,lngth,sgn)$
if null l2 then list(lngth,sgn) . (l1 . sreverse s)
else
 (lambda z$ if null z then
                          prop!-simp1(l1,cdr l2,car l2 . s,lngth,sgn)
            else prop!-simp1(cdr z,cdr l2,s,add1 lngth,
         (car z)*sgn*(-1)**(length s)) )
      prop!-simp2(l1,car l2)$

symbolic procedure prop!-simp2(l,ind)$
begin
  scalar sign$
if sign:=index!-in(ind,l) then
      return ((-1)**(length(l)-length(sign))) . delete(ind,l)
 else return nil
end$

symbolic procedure mk!-contract!-coeff u$
if caar u = 0 then 1
else
    begin
      scalar numr,denr,pk,k$
              pk:=caar u$
               k:=length cadr u$
              numr:=constimes ((cadar u) .mk!-numr(ndim!*,k,k+pk))$
       %      denr:=f!^(pk+k,k)*(cadar u)$
         return                numr
    end$

symbolic procedure mk!-numr(n,k,p)$
if k=p then nil
else (if k=0 then n else list('difference,n,k)) . mk!-numr(n,add1 k,p)$

symbolic procedure mod!-index(term,dump)$
%-------------------------------------------------------------------
% MODYFIES INDECES OF "DUMP" VIA DELTAS IN "TERM"
% DELETES UTILIZED DELTAS FROM "TERM"
% RETURNS "TERM" . "DUMP"
%------------------------------------------------------------------$
begin
  scalar coeff,sign$
      coeff:=list 1$
      term:= if sign:= eq(car term,'minus) then cdadr term
             else cdr term$
  while term do << if free car term       then
                                coeff:=(car term) . coeff
                   else dump:=mod!-dump(cdar term,dump)$
                   term:=cdr term                         >>$
  return
       ( if sign then
              if null cdr coeff then (-1)
              else 'minus . list(constimes coeff)
        else if null cdr coeff then 1
             else constimes coeff ) . dump
end$

symbolic procedure dpropagator(l1,l2,dump)$
(lambda z$
     if z=0       then z
     else if z=1 then nil . dump
          else  for each trm in cdr z collect
                             mod!-index(trm,dump) )
   propagator(l1,l2)$

symbolic procedure dvertex!-to!-projector(svert,rlst,dump)$
begin
  scalar l1,l2,coeff,w$
       l1:=mktails(cadr svert,rlst,dump)$
       if repeatsp car l1 then return 0$
       l2:= mktails(caddr svert,rlst,cdr l1)$
       if repeatsp car l2 then return 0$
       dump:=cdr l2$
       w:=prop!-simp(car l1,sreverse car l2)$
       coeff:=mk!-contract!-coeff w$
 return coeff . dpropagator(cadr w,cddr w,dump)
end$

%SYMBOLIC PROCEDURE DSTR!-TO!-ALG(STRAND,RLST,DUMP)$
%IF NULL STRAND THEN LIST('RECIP,MK!-COEFF1(DUMP,RLST))
%ELSE
%  BEGIN
%    SCALAR VRTX$
%      VRTX:=DVERTEX!-TO!-PROJECTOR(CAR STRAND,RLST,DUMP)$
%      IF 0=VRTX THEN RETURN 0$
%      IF NULL CADR VRTX THEN RETURN
%      LIST('TIMES,CAR VRTX,DSTR!-TO!-ALG(CDR STRAND,RLST,CDDR VRTX))$
%
% RETURN LIST('TIMES,CAR VRTX,
%        'PLUS . (FOR EACH TRM IN CDR VRTX COLLECT
%      LIST('TIMES,CAR TRM,DSTR!-TO!-ALG(CDR STRAND,RLST,CDR TRM))) )

%===MODYFIED 4.07.89

remflag('(dstr!-to!-alg),'lose)$

symbolic procedure dstr!-to!-alg(strand,rlst,dump)$
%IF NULL STRAND THEN LIST('RECIP,MK!-COEFF1(DUMP,RLST))
if null strand then consrecip list(mk!-coeff1(dump,rlst))
else
  begin
    scalar vrtx$
      vrtx:=dvertex!-to!-projector(car strand,rlst,dump)$
      if 0=vrtx then return 0$
      if null cadr vrtx then return
         if 1 = car(vrtx) then
                           dstr!-to!-alg(cdr strand,rlst,cddr vrtx)
         else
           cvitimes2(car vrtx,
                     dstr!-to!-alg(cdr strand,rlst,cddr vrtx))$


      return
        cvitimes2(car vrtx,
                  consplus (for each trm in cdr vrtx collect
                            cvitimes2(car trm,
                                      dstr!-to!-alg(cdr strand,rlst,
                                                    cdr trm))))$
end$

flag('(dstr!-to!-alg),'lose)$

symbolic procedure cvitimes2(x,y)$
if (x=0) or (y=0) then 0
else if x = 1 then y
     else if y = 1 then x
          else list('times,x,y)$


symbolic procedure free dlt$
(freeind cadr dlt) and (freeind caddr dlt)$

symbolic procedure freeind ind$
atom ind $
%       AND
%LAGP(IND,'EXTRNL)$

symbolic procedure mod!-dump(l,dump)$
if not freeind car l then mod!-dump1(cadr l,car l,dump)
else mod!-dump1(car l,cadr l,dump)$

symbolic procedure mod!-dump1(new,old,dump)$
if null dump then nil
else ( (caar dump) . l!-subst(new,old,cdar dump) ) .
                    mod!-dump1(new,old,cdr dump)$

symbolic procedure l!-subst(new,old,l)$
if null l then nil
else if old = car l then new . l!-subst(new,old,cdr l)
     else car l . l!-subst(new,old,cdr l) $

endmodule;

end;
