module matrext;

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


% This module defines additional utility functions for manipulating
% matrices.  Coercions to BAG and LIST structures are defined.

symbolic procedure natnumlis u;
   % True if U is a list of natural numbers.
   % Taken from MATR.RED for bootstrap purpose.
null u or numberp car u and fixp car u and car u>0 and natnumlis cdr u;


symbolic procedure mkidm(u,j);
% This function allows us to RELATE TWO MATRICES by concatanation of
% characters. u AND uj should BOTH be matrices.
%  matsm cadr get(mkid!:(u,j),'avalue) ;
  mkid(u,j);

 flag('(mkidm),'opfn);
 flag('(mkidm),'noval);

symbolic  procedure baglmat (u,op);
% this procedure maps U into the matrix whose name is OP;
% it cannot REDEFINE the matrix OP.
% This is to avoid accidental redefinition of a previous matrix;
if getrtype op  then rederr list(op,"should be an identifier")
else
 begin scalar x,y;
  if atom op then if not (y:=gettype op) then put(op,'rtype,'matrix)
                  else typerr(list(y,op),"matrix");
  if rdepth list u neq 2 then rederr("depth of list or bag must be 2");
      x:=cdr u;
      x:= for each j in x collect for each k in cdr j collect k;
    put(op,'avalue,list('matrix,'mat . x));
return t end;

flag('(baglmat),'opfn);

symbolic procedure rcoercemat u;
% Transforms a matrix into a bag or list. Argument is a list (mat,idp).
% idp is the name to  be given to the line or column vectors.
% The idp-envelope of the bag is the same as the one of the one of the
% subbags$
begin scalar x,prf;
 x:=reval car u;
if getrtype x neq 'matrix then rederr list(x,"should be a matrix");
 prf:= cadr u;
if car x neq 'mat then typerr(x,"matrix") else
 if prf neq 'list then  <<prf:=reval prf; simpbagprop list(prf,t)>>;
 x:=cdr x;
 x:= for each j in x collect (prf .  j);
return prf . x end;

put('coercemat,'psopfn,'rcoercemat);

put('rcoercemat,'number!_of!_args,2);

symbolic procedure n!-1zero(n,k)$
if n=0 then nil else
if k=1 then 1 . nzero(n-1) else
if k=n then  append(nzero(n-1) , (1 . nil))  else
append(nzero(k-1), (1 . nzero(n-k)))$

symbolic procedure unitmat u$
% It creates unit matrices. The argument is of the form A(2),B(5)....$
begin scalar l,sy,x,aa$
for each s in u do
<< if atom s or length (l:= revlis cdr s) neq 1 or not natnumlis l
      then errpri2(s,'hold) else
<<aa:=nil;sy:=car s; x:=gettype sy; if not null x then if x eq 'matrix
                                    then lprim list(x,sy,"redefined")
                                    else typerr(list(x,sy),"matrix");
         l:=car l; for n:=1:l do aa:=n!-1zero(l,l-n+1) . aa$
        put(sy,'rtype,'matrix);
        put(sy,'avalue,list('matrix,'mat . aa))>>>>;
 end$

put('unitmat,'stat,'rlis);

symbolic procedure  submat (u,nl,nc);
% Allows to extract from the matrix M the matrix obtained when
% the row NL and the column NC have been dropped.
% When NL and NC are out of range gives a copy of M;
if getrtype u neq 'matrix then rederr list(u,"should be a matrix")
else
begin scalar x;
x:=  matsm  u;
    if and(nl=0,nc=0) then return  x else
    if nl neq 0 then x:=remove(x,nl)$
    if nc neq 0 then
         x:=for each j in x collect remove(j,nc);
    return x end;

put('submat,'rtypefn,'getrtypecar);

flag('(submat),'matflg);

symbolic procedure matsubr(m,bgl,nr)$
if getrtype m neq 'matrix then rederr list(m,"should be a matrix")
else
begin scalar x,y,res; integer xl;
% It allows to replace row NR of the matrix M by the bag or list BGL;
y:=reval bgl;
 if not baglistp y  then typerr(y,"bag or list") else
 if nr leq 0 then rederr " THIRD ARG. MUST BE POSITIVE"
 else
    x:=matsm m$ xl:=length x$
   if length( y:=cdr y) neq xl then  rederr " MATRIX MISMATCH"$
    y:= for each j in y collect simp j;
   if nr-xl >0 then rederr " row number is out of range";
    while (nr:=nr-1) >0
              do <<res:=car x . res$ x:=cdr x >>;
           rplaca(x,y) ;
           res:=append(  reverse res, x) ;
    return  res   end;

put('matsubr,'rtypefn,'getrtypecar);

flag('(matsubr),'matflg);


symbolic procedure matsubc(m,bgl,nc)$
if getrtype m neq 'matrix then rederr list(m,"should be a matrix")
else
begin scalar x,y,res; integer xl;
%It allows to replace column NC of the matrix M by the bag or list BGL
y:=reval bgl;
 if not baglistp y  then typerr(y,"bag or list") else
 if nc leq 0 then rederr " THIRD ARG. MUST BE POSITIVE"
 else
    x:=tp1 matsm m$ xl:=length x$
   if length( y:=cdr y) neq xl then  rederr " MATRIX MISMATCH"$
    y:= for each j in y collect simp j;
   if nc-xl >0 then rederr " column  number is out of range";
    while (nc:=nc-1) >0
              do <<res:=car x . res$ x:=cdr x >>;
           rplaca(x,y) ;
           res:=tp1 append(  reverse res, x) ;
    return  res   end;

put('matsubc,'rtypefn,'getrtypecar);

flag('(matsubc),'matflg);

symbolic procedure rmatextr u$
% This function allows to extract row N from matrix A and
% to place it inside a bag whose name is LN$
begin scalar x,y; integer n,nl;
x:= matsm car u; y:= reval cadr u; n:=reval caddr u;
if  not fixp n then
rederr "Arguments are: matrix, vector name, line number" else
if not baglistp list y  then  simpbagprop list(y, t)$
nl:=length x;
if n<= 0  or n>nl then return nil$
while n>1 do <<x:=cdr x$ n:=n-1>>$
if null x then return nil$
return x:=y . ( for each j in car x  collect prepsq j) end$

symbolic procedure rmatextc u$
% This function allows to extract column N from matrix A and
% to place it inside a bag whose name is LN$
begin scalar x,y; integer n,nc;
x:= tp1 matsm car u; y:= reval cadr u; n:=reval caddr u;
if  not fixp n then
rederr "Arguments are: matrix, vector name, line number" else
if not baglistp list y  then  simpbagprop list(y, t)$
nc:=length x;
if n<= 0  or n>nc then return nil$
while n>1 do <<x:=cdr x$ n:=n-1>>$
if null x then return nil$
return x:=y . ( for each j in car x  collect prepsq j) end$

put('matextr,'psopfn,'rmatextr);

put('matextc,'psopfn,'rmatextc);

symbolic procedure  hconcmat(u,v)$
% Gives the horizontal concatenation of matrices U and V$
  hconcmat!:(matsm u,matsm v );

symbolic procedure hconcmat!:(u,v)$
if null u then v else if null v then u else
append(car u,car v) . hconcmat!:(cdr u,cdr v)$

symbolic put('hconcmat,'rtypefn,'getrtypecar);
symbolic flag('(hconcmat),'matflg);

symbolic procedure vconcmat (u,v)$
% Gives the vertical concatenation of matrices U and V$
 append(matsm u,matsm v);

put('vconcmat,'rtypefn,'getrtypecar);

flag('(vconcmat),'matflg);

symbolic procedure tprodl(u,v)$
begin scalar aa,ul$
l1: if null u then return aa$
    ul:=car u$
    ul:=multsm(ul,v)$
    aa:=hconcmat!:(aa,ul)$
    u:=cdr u$
    go to l1$
    end$

symbolic procedure tpmat(u,v)$
% Constructs the direct product of two matrices;
if null gettype u  then multsm(simp u,matsm v) else
if null gettype v then multsm(simp v,matsm u) else
begin scalar aa,uu,vv$
    uu:=matsm u$ vv:=matsm v$
    for each x in uu do aa:=append (aa,tprodl(x,vv))$
return aa end;

infix tpmat$

put('tpmat,'rtypefn, 'getrtypecar);

flag('(tpmat),'matflg)$

algebraic procedure hermat (m,hm);
% hm must be an identifier with NO value. Returns the
% Hermitiam Conjugate matrix.
begin scalar ml,ll; %ll:=length M;
m:=tp m;
ml:=coercemat(m,list);
ll:=list(length first ml,length ml);
ml:=for j:=1: first ll collect for k:=1:second ll collect
        sub(i=-i,(ml.j).k);
baglmat(ml,hm);
return hm end;

symbolic procedure seteltmat(m,elt,i,j);
% Sets the matrix element (i,j) to elt. Returns the modified matrix.
begin scalar res;res:=matsm m;
rplaca(pnth(nth(res,i),j),simp elt);
return res end;

put('seteltmat,'rtypefn,'getrtypecar);
flag('(seteltmat),'matflg);

symbolic procedure simpgetelt u;
% Gets the matrix element (i,j). Returns the element.
begin scalar mm;
mm:=matsm car u;
return nth(nth(mm,cadr u),caddr u) end;

put('geteltmat, 'simpfn,'simpgetelt);

endmodule;

end;
