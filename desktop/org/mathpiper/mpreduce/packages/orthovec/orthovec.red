module orthovec;  % 3-D vector calculus package.

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


create!-package('(orthovec),'(contrib avector));

%          %========================================%
%          %              ORTHOVEC                  %
%          %========================================%
%          %     A 3-D VECTOR CALCULUS PACKAGE      %
%          %     USING ORTHOGONAL CURVILINEAR       %
%          %            COORDINATES                 %
%          %                                        %
%          %   copyright James W Eastwood,          %
%          %             Culham Laboratory,         %
%          %             Abingdon, Oxon.            %
%          %                                        %
%          %             February 1987              %
%          %                                        %
%          %   This new version differs from the    %
%          %   original version published in CPC,   %
%          %   47(1987)139-147 in the following     %
%          %   respects:                            %
%          %                                        %
%          %   *.+.,etc replaced by +,-,*,/         %
%          %   *unary vector +,-,/ introduced       %
%          %   *vector component selector _         %
%          %   *general tidy up                     %
%          %   *L'Hopitals rule in Taylor series    %
%          %   *extended division definition        %
%          %   *algebraic output of lisp vectors    %
%          %   *exponentiation of vectors           %
%          %   *vector extension of depend          %
%          %                                        %
%          %            Version 2                   %
%          %        All rights reserved             %
%          %     copyright James W Eastwood         %
%          %             June 1990                  %
%          %                                        %
%          %   This is a preliminary version of     %
%          %   the NEW VERSION of ORTHOVEC which    %
%          %   will be available from the Computer  %
%          %   Physics Communications Program       %
%          %   Library, Dept. of Applied Maths and  %
%          %   Theoretical Physics, The Queen's     %
%          %   University of Belfast, Belfast       %
%          %   BT7 1NN, Northern Ireland.           %
%          %   See any copy of CPC for further      %
%          %   details of the library services.     %
%          %                                        %
%          %========================================%
%          %       REDUCE 3.4 is assumed            %
%          %========================================%
%
%
%
%-------------------------------------------------------------------
%                       INITIALISATION
%%

algebraic;

%select coordinate system
%========================
procedure vstart0;
  begin
    scalar ctype;
    write "Select Coordinate System by number";
    write "1] cartesian";
    write "2] cylindrical";
    write "3] spherical";
    write "4] general";
    write "5] others";
%remove previous settings
    clear u1,u2,u3,h1,h2,h3;
    depend h1,u1,u2,u3;
    depend h2,u1,u2,u3;
    depend h3,u1,u2,u3;
    nodepend h1,u1,u2,u3;
    nodepend h2,u1,u2,u3;
    nodepend h3,u1,u2,u3;
%select coordinate system
    ctype := symbolic read();
    if ctype=1 then << u1:=x;u2:=y;u3:=z;h1:=1;h2:=1;h3:=1 >>
    else if ctype=2 then << u1:=r;u2:=th;u3:=z;h1:=1;h2:=r;h3:=1 >>
    else if ctype=3 then << u1:=r;u2:=th;u3:=ph;h1:=1;h2:=r;h3:=r*sin(th) >>
    else if ctype=4 then <<
      depend h1,u1,u2,u3;
      depend h2,u1,u2,u3;
      depend h3,u1,u2,u3 >>
    else <<
      write "To define another coordinate system, give values ";
      write "to components u1,u2,u3 and give functional form or";
      write "DEPEND for scale factors h1,h2 and h3. For example,";
      write "to set up paraboloidal coords u,v,w type in:-";
      write "u1:=u;u2:=v;u3:=w;h1:=sqrt(u**2+v**2);h2:=h1;h3:=u*v;">>;
    write "coordinate type = ",ctype;
    write "coordinates = ",u1,",",u2,",",u3;
    write "scale factors = ",h1,",",h2,",",h3;
    return
  end$

let vstart=vstart0()$

%give access to lisp vector procedures
%=======================================

symbolic operator getv;

flag('(vectorp), 'direct);
flag('(vectorp), 'boolean);

%-------------------------------------------------------------------
%                      INPUT-OUTPUT
%
%set a new vector
%===================
symbolic procedure svec(c1,c2,c3);
  begin
% The original version of this had been coded as an algebraic mode
% procedure, and the result was that a Standrad Quotient was
% created with the new vector in it at the time mkvect was used -
% i.e. when the vector had none of its contents filled in. This
% resulted in a record being lodged in the alglist!* hash table
% based on that EMPTY vector, and caused confusion later when
% an attempt to find it again was made. In general there will be
% a certain amount of pain with the alglist!* cache is anything
% ever gets updated in place. But the re-work in this package that
% makes the code "less imperative" and that packages the only used
% of putv in this one procedure seem to me to be a good thing in
% general...   ACN Aug 2011.
    scalar a;
    a := mkvect(2);
    putv(a,0,c1);
    putv(a,1,c2);
    putv(a,2,c3);
    return aeval a
  end$

flag('(svec), 'opfn);

%output a vector
%===============
procedure vout(v);
  begin;
    if vectorp(v) then
      for j:=0:2 do write "[",j+1,"] ",getv(v,j)
    else write v;
    return v
  end$

%-------------------------------------------------------------------
%               REDEFINITION OF SOME STANDARD PROCEDURES
%
% Vector extension of standard definitions of depend and nodepend.

remflag('(depend nodepend),'lose);   % We must use these definitions.

symbolic procedure depend u;
  begin
    scalar v,w;
    v:= !*a2k car u;
    for each x in cdr u do
      if vectorp(v) then
        for ic:=0:upbv(v) do <<
          if atom(w:=getv(v,ic)) and not numberp(w) then
            depend1(w,x,t) >>
      else depend1(car u,x,t)
  end$

symbolic procedure nodepend u;
  begin
    scalar v,w;
    rmsubs();
    v:= !*a2k car u;
    for each x in cdr u do
      if vectorp(v) then
        for ic:=0:upbv(v) do <<
          if atom(w:=getv(v,ic)) and not numberp(w) then
            depend1(w,x,nil)>>
      else depend1(car u,x,nil)
  end$

%
%-------------------------------------------------------------------
%                      ALGEBRAIC OPERATIONS
%
%define symbols for vector algebra
%=====================================
newtok '(( !+ ) vectoradd);
newtok '(( !- ) vectordifference);
newtok '((!> !< ) vectorcross);
newtok '(( !* ) vectortimes);
newtok '(( !/ ) vectorquotient);
newtok '(( !_ ) vectorcomponent);
newtok '(( !^ ) vectorexpt);
%
%define operators
%================
operator vectorminus,vectorplus,vectorrecip;
infix vectoradd,vectordifference,vectorcross,vectorexpt,
      vectorcomponent,vectortimes,vectorquotient,dotgrad;
precedence vectoradd,<;
precedence vectordifference,vectoradd;
precedence dotgrad,vectordifference;
precedence vectortimes,dotgrad;
precedence vectorcross,vectortimes;
precedence vectorquotient,vectorcross;
precedence vectorexpt,vectorquotient;
precedence vectorcomponent,vectorexpt;

deflist( '(
   (vectordifference vectorminus)
   (vectoradd vectorplus)
   (vectorquotient vectorrecip)
   (vectorrecip vectorrecip)
 ), 'unary)$

deflist('((vectorminus vectorplus) (vectorrecip vectortimes)),
                 'alt)$

%extract component of a vector
%=============================
procedure vectorcomponent(v,ic);
  if vectorp(v) then
    if ic=1 or ic=2 or ic=3 then getv(v,ic-1)
    else rerror(orthovec,1,"Incorrect component number")
  else rerror(orthovec,2,"Not a vector")$

%
%add vector or scalar pair v1 and v2
%===================================
procedure vectoradd(v1,v2);
% It was unexpected to me that it is important to use "PLUS" rather than
% "+" here - however "+" maps into vectoradd in a way that ends up VERY
% unhelpful. Specifically it leads to infinite recursion.
  if vectorp(v1) and vectorp(v2) then
    svec(plus(getv(v1, 0), getv(v2, 0)),
         plus(getv(v1, 1), getv(v2, 1)),
         plus(getv(v1, 2), getv(v2, 2)))
  else if not(vectorp(v1)) and not(vectorp(v2)) then plus(v1, v2)
  else rerror(orthovec,3,"Incorrect args to vector add");

%unary plus
%==========
procedure vectorplus(v);v$
%
%negate vector or scalar v
%=========================
procedure vectorminus(v);
  if vectorp(v) then
    svec(minus getv(v,0), minus getv(v,1), minus getv(v, 2))
  else minus v;

%scalar or vector subtraction
%============================
procedure vectordifference(v1,v2);
  vectoradd(v1, vectorminus(v2))$

%dot product or scalar times
%===========================
procedure vectortimes(v1,v2);
  if vectorp(v1) and vectorp(v2) then
    for ic:=0:2 sum times(getv(v1,ic), getv(v2,ic))
  else if not(vectorp(v1)) and not(vectorp(v2)) then times(v1,v2)
  else if vectorp(v1) and not(vectorp(v2)) then
    svec(times(getv(v1,0),v2), times(getv(v1,1),v2), times(getv(v1,2),v2))
  else
    svec(times(getv(v2,0),v1), times(getv(v2,1),v1), times(getv(v2,2),v1));

%vector cross product
%====================
procedure vectorcross(v1,v2);
  if vectorp(v1) and vectorp(v2) then
    svec(plus(times(getv(v1,1),getv(v2,2)), minus times(getv(v1,2),getv(v2,1))),
         plus(times(getv(v1,2),getv(v2,0)), minus times(getv(v1,0),getv(v2,2))),
         plus(times(getv(v1,0),getv(v2,1)), minus times(getv(v1,1),getv(v2,0))))
  else rerror(orthovec,4,"Incorrect args to vector cross product");

%vector division
%===============
procedure vectorquotient(v1,v2);
  if vectorp(v1) then
    if vectorp(v2) then quotient (vectortimes(v1,v2),vectortimes(v2,v2))
    else vectortimes(v1, recip(v2))
  else if vectorp(v2) then vectortimes(v1, vectortimes(v2, recip(times(v2,v2))))
  else quotient(v1,v2)$

procedure vectorrecip(v);
  if vectorp(v) then vectortimes(v, recip(vectortimes(v,v)))
  else recip(v)$

%length of vector
%================
procedure vmod(v);
  sqrt(vectortimes(v, v))$

%vector exponentiation
%=====================
procedure vectorexpt(v,n);
  if vectorp(v) then expt(vmod(v), n)
  else expt(v, n)$

%-------------------------------------------------------------------
%                      DIFFERENTIAL OPERATIONS
%

%div
%===
procedure div(v);
  if vectorp(v) then
    vectorquotient(
      vectorquotient(
        vectorquotient(
          vectoradd(df(times(h2,h3,getv(v,0)),u1),
            vectoradd(df(times(h3,h1,getv(v,1)),u2),
              df(times(h1,h2,getv(v,2)),u3))), h1), h2), h3)
  else rerror(orthovec,5,"Incorrect arguments to div")$

%grad
%====
procedure grad(s);
  if not vectorp(s) then
    svec(quotient(df(s,u1),h1),
         quotient(df(s,u2),h2),
         quotient(df(s,u3),h3))
    else rerror(orthovec,6,"Incorrect argument to grad")$

%curl
%====
procedure curl(v);
  if vectorp(v) then
    svec(quotient(quotient(plus(df(times(h3,getv(v,2)),u2),
                                minus df(times(h2,getv(v,1)),u3)),h2),h3),
         quotient(quotient(plus(df(times(h1,getv(v,0)),u3),
                                minus df(times(h3,getv(v,2)),u1)),h3),h1),
         quotient(quotient(plus(df(times(h2,getv(v,1)),u1),
                                minus df(times(h1,getv(v,0)),u2)),h1),h2))
  else rerror(orthovec,7,"Incorrect argument to curl");

%laplacian
%=========
procedure delsq(v);
  if vectorp(v) then
    vectoradd(grad(div(v)), vectorminus curl(curl(v)))
  else div(grad(v))$

%differentiation
%===============
procedure vdf(v,x);
  if vectorp(x) then
    rerror(orthovec,8,"Second argument to VDF must be scalar")
  else if vectorp(v) then
    svec(vdf(getv(v,0),x), vdf(getv(v,1),x), vdf(getv(v,2),x))
  else df(v,x);

%v1.grad(v2)
%===========
procedure dotgrad(v1,v2);
  if vectorp(v1) then
    if vectorp(v2) then
      vectortimes(
        quotient(1,2),
        vectoradd(grad(vectortimes(v1, v2)),
          vectoradd(vectortimes(v1, div(v2)),
            vectoradd(vectorminus vectortimes(div(v1), v2),
              vectorminus (vectoradd(
                curl(v1 >< v2),
                  vectoradd( v1 >< curl(v2),
                    vectorminus (curl(v1) >< v2))))))))
    else times(v1, grad(v2))
  else rerror(orthovec,9,"Incorrect arguments to dotgrad")$

%3-D Vector Taylor Expansion about vector point
%==============================================
procedure vtaylor(vex,vx,vpt,vorder);
%note: expression vex, variable vx, point vpt and order vorder
%      are any legal mixture of vectors and scalars
  if vectorp(vex) then
     svec(vptaylor(getv(vex,0),vx,vpt,vorder),
          vptaylor(getv(vex,1),vx,vpt,vorder),
          vptaylor(getv(vex,2),vx,vpt,vorder))
  else vptaylor(vex,vx,vpt,vorder);

%Scalar Taylor expansion about vector point
%==========================================
procedure vptaylor(sex,vx,vpt,vorder);
%vector variable
  if vectorp(vx) then
    if vectorp(vpt) then
%vector order
      if vectorp(vorder) then
        taylor(
          taylor(
            taylor(sex, getv(vx,0), getv(vpt,0), getv(vorder,0)),
            getv(vx,1), getv(vpt,1), getv(vorder,1)),
          getv(vx,2), getv(vpt,2), getv(vorder,2))
      else
        taylor(
          taylor(
            taylor(sex, getv(vx,0), getv(vpt,0), vorder),
            getv(vx,1), getv(vpt,1), vorder),
         getv(vx,2), getv(vpt,2), vorder)
    else rerror(orthovec,10,"VTAYLOR: vector VX mismatches scalar VPT")
%scalar variable
  else if vectorp(vpt) then
    rerror(orthovec,11,"VTAYLOR: scalar VX mismatches vector VPT")
  else if vectorp(vorder) then
    rerror(orthovec,12,"VTAYLOR: scalar VX mismatches vector VORDER")
  else taylor(sex,vx,vpt,vorder)$

%Scalar Taylor expansion of ex wrt x about point pt to order n
%=============================================================
procedure taylor(ex,x,pt,n);
  begin
    scalar term,series,dx,mfac;
    if numberp n then <<
      mfac:=1;
      dx:=x-pt;
      term:=ex;
      series:= plus(limit(ex,x,pt),
        for k:=1:n sum
          times(limit(term:=df(term,x),x,pt),
                mfac:=quotient(times(mfac,dx),k))) >>
    else rerror(orthovec,13,
               "Truncation orders of Taylor series must be integers");
    return series
  end$
%
%limiting value of exression ex as x tends to pt
%===============================================
procedure limit(ex,x,pt);
  begin
    scalar denex,numex;
%polynomial
    if (denex:=den(ex))=1 then return sub(x=pt,ex)
%zero denom rational
    else if sub(x=pt,denex)=0 then <<
%l'hopital's rule
      if sub(x=pt,(numex:=num(ex)))=0 then
        return limit(quotient(df(numex,x), df(denex,x)),x,pt)
%singular
      else rerror(orthovec,14,"Singular coefficient found by LIMIT")>>
%nonzero denom rational
    else return sub(x=pt,ex);
  end$
%
%-------------------------------------------------------------------
%                      INTEGRAL OPERATIONS
%
% Vector Integral
%================
procedure vint(v,x);
  if vectorp(x) then
    rerror(orthovec,15,"Second argument to VINT must be scalar")
  else if vectorp(v) then
    svec(int(getv(v,0), x),
         int(getv(v,1), x),
         int(getv(v,2), x))
    else int(v,x);

%Definite Vector Integral
%========================
procedure dvint(v,x,xlb,xub);
  if vectorp(xlb) or vectorp(xub) then
    rerror(orthovec,16,"Limits to DVINT must be scalar")
  else if vectorp(v) then begin
    scalar i0, i1, i2;
    i0 := int(getv(v, 0), x);
    i1 := int(getv(v, 1), x);
    i2 := int(getv(v, 2), x);
    return svec(plus(sub(x=xub,i0), minus sub(x=xlb,i0)),
                plus(sub(x=xub,i1), minus sub(x=xlb,i1)),
                plus(sub(x=xub,i2), minus sub(x=xlb,i2)))
    end
  else begin
    scalar ii;
    ii := int(v, x);
    return plus(sub(x=xub,ii), minus sub(x=xlb,ii))
  end;

%Volume Integral
%===============
procedure volint(v);
  if vectorp(v) then
    svec(volint getv(v, 0),
         volint getv(v, 1),
         volint getv(v, 2))
  else int( int( int(times(v,h1,h2,h3),u1),u2),u3);

%Definite Volume Integral
%========================
procedure dvolint(v,vlb,vub,n);
  if vectorp(vlb) and vectorp(vub) then
    begin
      scalar ii;
      ii := times(times(h1,h2,h3), v);
      if n=1 then return
        dvint(dvint(dvint(ii,
              u1,getv(vlb,0),getv(vub,0)),
            u2,getv(vlb,1),getv(vub,1)),
          u3,getv(vlb,2),getv(vub,2) )
      else if n=2 then return
        dvint(dvint(dvint(ii,
              u3,getv(vlb,2),getv(vub,2)),
            u1,getv(vlb,0),getv(vub,0)),
          u2,getv(vlb,1),getv(vub,1) )
      else if n=3 then return
        dvint(dvint(dvint(ii,
              u2,getv(vlb,1),getv(vub,1)),
            u3,getv(vlb,2),getv(vub,2)),
          u1,getv(vlb,0),getv(vub,0) )
      else if n=4 then return
        dvint(dvint(dvint(ii,
              u1,getv(vlb,0),getv(vub,0)),
            u3,getv(vlb,2),getv(vub,2)),
          u2,getv(vlb,1),getv(vub,1) )
      else if n=5 then return
        dvint(dvint(dvint(ii,
              u2,getv(vlb,1),getv(vub,1)),
            u1,getv(vlb,0),getv(vub,0)),
          u3,getv(vlb,2),getv(vub,2) )
      else return
        dvint(dvint(dvint(ii,
              u3,getv(vlb,2),getv(vub,2)),
            u2,getv(vlb,1),getv(vub,1)),
          u1,getv(vlb,0),getv(vub,0))
    end
  else rerror(orthovec,17,"Bounds to DVOLINT must be vectors");

%Scalar Line Integral
%====================
procedure lineint(v,vline,tt);
  if vectorp(v) and vectorp(vline) and not vectorp(tt) then
    int(sub(u1=getv(vline,0), u2=getv(vline,1), u3=getv(vline,2),
            plus(times(getv(v,0), df(getv(vline,0),tt),  h1),
                 times(getv(v,1), df(getv(vline,1),tt),  h2),
                 times(getv(v,2), df(getv(vline,2),tt),  h3))) , tt)
  else rerror(orthovec,18,"Incorrect arguments to LINEINT")$

%Definite Scalar Line Integral
%=============================
procedure dlineint(v,vline,tt,tlb,tub);
  if vectorp(tlb) or vectorp(tub) then
    rerror(orthovec,19,"Limits to DLINEINT must be scalar")
  else
    begin
      scalar ii;
      ii := lineint(v,vline,tt);
      return plus(sub(tt=tub,ii), minus sub(tt=tlb,ii))
    end;

%
%-------------------------------------------------------------------
%        SET DEFAULT COORDINATES TO CARTESIAN
%
% write "Cartesian coordinates selected by default";
% write "If you wish to change this then type VSTART";
% write "and follow the instructions given.";
% write "u1,u2,u3 are reserved for coordinate names";
% write "h1,h2,h3 are reserved for scale factor names";
ctype:=1$u1:=x$u2:=y$u3:=z$h1:=1$h2:=1$h3:=1$
% write "coordinate type = ",ctype;
% write "coordinates = ",u1,",",u2,",",u3;
% write "scale factors = ",h1,",",h2,",",h3;

%-------------------------------------------------------------------

endmodule;

end;
