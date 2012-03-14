%********************************************************************
%                                                                   *
%  The program APPLYSYM for applying point-symmetries which are,    *
%  for example computed by the program LIEPDE. It also can be used  *
%  for solving quasilinear first order PDEs using QUASILINPDE.      *
%                                                                   *
%  Author: Thomas Wolf                                              *
%  Date:   summer 1995                                              *
%                                                                   *
%********************************************************************


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

symbolic fluid '(print_ logoprint_ nfct_ fname_ time_ facint_
                 adjust_fnc safeint_ freeint_ odesolve_)$
lisp flag('(yesp),'boolean)$
lisp(logoprint_:=t)$
symbolic operator freeoflist$
symbolic operator termxread$

%----------------------------

symbolic fluid '(tr_as)$
lisp(tr_as:=t)$

algebraic procedure ApplySym(problem,Symtry);
%  problem ... {{equations},{functions},{variables}}
%  Symtry  ... {{xi_..=..,..,eta_..=..,..},{constants in first list}}
begin
  scalar genlist,con,e1,e2,h1,h2,h3,h4,h5,h6,h7,modus,u,v,xlist,ylist,n,
         cop1,cop2,symanz,oldsol,oldmodus,trafoprob,altlogo$
  backup_reduce_flags()$
  clear sy_,sym_;
  array sym_(length(second Symtry));
  symbolic put('sy_,'simpfn,'simpiden)$
  symbolic put('ff,'simpfn,'simpiden)$
  symbolic put('ffi,'simpfn,'simpiden)$

  ylist := maklist second problem;
  xlist := maklist  third problem;

  con:=second Symtry;
  symanz:=0;
  for each e1 in con do       % i.e. for all symmetries do:
  if freeoflist(e1,xlist) and
     freeoflist(e1,ylist) then        %------ no pseudo-Lie-symm.
  <<genlist:=sub(e1=1,first Symtry);  %------ calculate symmetry
    for each el2 in con do
    if el1 neq el2 then genlist:=sub(el2=0,genlist);
    symanz:=symanz+1;
    sym_(symanz):=genlist
  >>;

  repeat <<

    % Application
    oldmodus:=modus;
    repeat <<
      lisp <<terpri()$
        write"Do you want to find similarity and symmetry variables ",
             "(enter `1;')";terpri()$
        write"or generalize a special solution with new parameters  ",
             "(enter `2;')";terpri()$
        write"or exit the program                                   ",
             "(enter  `;')";terpri()
      >>;
      modus:=termxread()

    >> until (modus=1) or (modus=2) or (modus=nil);

    if modus neq nil then <<
      % Preparing a combination of symmetries
      if symanz=1 then genlist:=sym_(1)
                  else <<
        for n:=1:symanz do <<
          write"----------------------   The ",n,".  symmetry is:"$
          for each e2 in sym_(n) do
          if rhs e2 neq 0 then write e2
        >>;
        write"----------------------"$
        repeat
        <<lisp
          <<terpri()$
            write"Which single symmetry or linear combination of symmetries"$
            terpri()$write"do you want to apply? "$
            terpri()$write"Enter an expression with `sy_(i)' for the i'th ",
                          "symmetry. Terminate input with `$' or `;'."$
            terpri()
          >>$

          h1:=lisp(reval termxread());
          if h1 then <<
            for each h2 in xlist do if h1 then if df(h1,h2) neq 0 then h1:=nil;
            for each h2 in ylist do if h1 then if df(h1,h2) neq 0 then h1:=nil;
            if h1=nil then lisp <<
              terpri();write"The coefficients of the sy_(i) must be constant, ",
                            "i.e. numbers or constants";terpri()
            >>
          >>
        >> until h1;
        genlist:={};
        cop1:=sym_(1);
        while cop1 neq {} do <<
          h6:=lhs first cop1;cop1:=rest cop1;
          genlist:=cons(h6 = 0, genlist)
        >>;
        genlist:=reverse genlist;

        for h2:=1:symanz do <<
          h3:=coeffn(h1,sy_(h2),1);
          if h3 neq 0 then <<
            cop1:=genlist;cop2:=sym_(h2);
            genlist:={};
            while cop1 neq {} do <<
              h4:=first cop1; cop1:=rest cop1$
              h5:=first cop2; cop2:=rest cop2$
              h6:=lhs h4;
              genlist:=cons(h6 = rhs h4 + h3*(rhs h5),genlist)
            >>;
            genlist:=reverse genlist
          >>
        >>
      >>;

      write"The symmetry to be applied in the following is ";
      write genlist;

      write"Terminate the following input with `$' or `;'."$
      if modus=1 then <<
        write"Enter the name of the new dependent variable(s):";
        u:=termxread();
        write"Enter the name of the new independent variable(s):";
        v:=termxread();
        altlogo:=logoprint_;
        logoprint_:=nil;
        trafoprob:=similarity(problem,genlist,{},u,v);
        logoprint_:=altlogo
      >>         else <<
        if length h1 < 2 then <<
          lisp <<terpri()$
            write"What shall the name of the new constant parameter",
                 " be? ";terpri()>>$
          h2:=termxread()
        >>;
        repeat <<
          lisp <<terpri()$
            write"Enter the solution to be generalized in form of an ",
                 "expression, which vanishes";terpri()$
            write"or in form of an equation `... = ...' ";
            if oldsol=nil then write":" else <<
              terpri()$
              write"or enter semicolon `;' to work on the solution ",
                   "specified before:"
            >>;terpri()
          >>;
          h3:=termxread();
          if h3 neq nil then oldsol:=h3
        >> until oldsol neq nil;
        h3:=NewParam(oldsol,genlist,h2);
        if h3 neq nil then oldsol:=h3
      >>
    >>
  >> until modus=nil;
  clear sym_;
  recover_reduce_flags()$
  return if oldmodus=1 then trafoprob else
         if oldmodus=2 then oldsol    else nil
end$ % of ApplySym

%----------------------------

algebraic procedure NewParam(oldsol,genlist,u_)$
% u_ is the name of the new parameter
begin
  scalar h1,h2,h20,h3,h30,h4,vari,pde,e1,printold,clist,newsol,
         oldsol_ex,prev_depend;
  vari:=makepde(genlist,u_)$
  pde:=first vari;
  vari:=rest vari;

  % is oldsol an invariant?
  oldsol_ex:=equ_to_expr(oldsol)$      % oldsol as vanishing expression
  prev_depend:=storedepend(vari)$
  h2:=sub(u_=oldsol_ex,pde);
  if h2 neq 0 then <<
    h1:=solve(oldsol_ex,vari);
    if h1 neq {} then <<
      h1=first h1;
      for each h3 in h1 do
      if freeof(h3,arbcomplex) then h2:=sub(h3,h2)
    >>
  >>;
  restoredepend(prev_depend)$
  if 0=h2 then return lisp
  <<write"The special solution to be generalized is an invariant ",
         "with respect to";terpri()$
    write"this symmetry, therefore no generalization is possible.";
    terpri()$
    for each h1 in fargs u_ do nodepend u_,h1;
    algebraic oldsol
  >>;

  pde:=pde-1;
  h1:= quasilinpde1(pde,u_,vari)$
  if h1 neq {} then <<
    h1:=first h1;
    % h2 is expressing the constants in terms of u_ and the xlist,ylist
    clist:={};
    h2:= for each e1 in h1 collect
    <<h3:=lisp(newfct(fname_,nil,nfct_))$
      lisp(nfct_:=add1 nfct_)$
      clist:=cons(h3,clist);
      h3 = e1>>;
    h20:=sub(u_=0,h2);
    h3:=solve(h2,vari);
    if h3 neq {} then <<
      h3:=first h3;
      h30:=sub(h20,h3);
      write"The substitutions to generalize the solution are: "$
      for each h4 in h30 do write h4$
      newsol:=sub(h30,oldsol_ex);
%      newsol:=second dropredundant(newsol,
%                                   alle Konstanten und
%                                   Funktionen die nicht in vari sind,
%                                   vari)$
      lisp <<write"The new solution";
%       if length algebraic h >2 then write"s are:" else write" is:";
%       terpri()$
      >>;
      write"0 = ",newsol
    >>
  >>;
  for each h1 in fargs u_ do nodepend u_,h1;
  return newsol
end$ % of NewParam

%----------------------------

symbolic operator  einfachst$
symbolic procedure einfachst(a,x)$
% a is an algebraic list and the element where x appears, but
% appears simplest, is found
begin
 scalar el1,el2,hp;
 hp:=10000;
 a:=cdr a;
 while a do <<
  el2:=car a;a:=cdr a;
  if not freeof(el2,x)                                  and
     ((not el1                                   ) or
      (el2 = x                                   ) or
      <<if not polyp(el2,cons(x,nil)) then nil
                                      else
       <<coeff1(el2,x,nil)$
         if hipow!*<hp then <<hp:=hipow!*; t>>
                       else nil               >> >>    ) then el1:=el2
 >>;
 return el1
end$

%----------------------------

algebraic procedure TransDf(y,yslist,vlist,indxlist)$
begin
 scalar m,n,e1,dfy;
 return
 if indxlist={} then sub(yslist,y)
                else <<
  m:=first indxlist;
  n:=0;
  dfy:=TransDf(y,yslist,vlist,rest indxlist);
  for each e1 in vlist sum <<
   n:=n+1;
   df(dfy,e1)*Dv!/Dx(n,m)
  >>
 >>
end$ % of TransDf

%----------------------------

algebraic procedure TransDeriv(yik,yslist,vlist)$
begin
 scalar indxlist,y,l1,l2;
 indxlist:=lisp cons('LIST,combidif(yik))$
 return TransDf(first indxlist,yslist,vlist,rest indxlist)
end$ % of TransDeriv

%----------------------------

algebraic procedure DeTrafo(eqlist,yslist,xslist,ulist,vlist)$
% Transformations of all orders are performed (point-, contact-,...)
% but only x-derivatives of y's are transformed. To transform other
% any other derivatives, subdiff1 must be extended to include all other
% occuring x-derivatives.
begin
 scalar avar,nvar,detpd,n,m,ordr,e1,e2,e3,sb;
 m:=length(xslist); n:=length(yslist)+m;
 clear dyx!/duv,Dv!/Dx;
 matrix dyx!/duv(n,n);
 matrix Dv!/Dx(m,m);
 avar:=append(yslist,xslist);
 nvar:=append(ulist,vlist);
 n:=0;
 for each e1 in avar do <<
  n:=n+1;m:=0;
  for each e2 in nvar do <<
   m:=m+1;
   dyx!/duv(m,n):=df(rhs e1,e2)
  >>
 >>;
 detpd:=det(dyx!/duv);
%write"detpd=",detpd;
 if detpd=0 then return
 <<write"The proposed transformation is not regular!";{}>>;
 clear dyx!/duv;

 ordr:=0;
 for each e1 in eqlist do
 for each e2 in yslist do
 <<n:=totdeg(e1,lhs e2);
  if n>ordr then ordr:=n>>;

 sb:=subdif1(for each e1 in xslist collect lhs e1,
             for each e1 in yslist collect lhs e1,ordr);

 % computation of Dv/Dx:=(Dx/Dv)^(-1)
 n:=0;
 for each e1 in xslist do <<
  n:=n+1;m:=0;
  for each e2 in vlist do <<
   m:=m+1;
   Dv!/Dx(n,m):=total_alg_mode_deriv(rhs e1,e2)
             % it is assumed ulist does depend on vlist
  >>
 >>;
 Dv!/Dx:=Dv!/Dx**(-1);

 % Substitution of all derivatives
 for each e1 in sb do
 for each e2 in e1 do <<
  if not freeof(eqlist,lhs e2) then <<
   % which function is to be differentiated wrt. which variable
   eqlist:=sub(lhs e2=TransDeriv(rhs e2,yslist,vlist),eqlist)
  >>
 >>;
 clear  Dv!/Dx;
 return sub(xslist,sub(yslist,eqlist))$
end$ % of DeTrafo

%----------------------------

algebraic procedure grouping(el1,el2,xlist,ylist,nx,ny)$
begin scalar h,el3,xslist,yslist$
  %------- Grouping the new variables to ulist and vlist
  h:={};
  xslist:={};   % list of expressions to calculate new indep. var.
  yslist:={};   % list of expressions to calculate new   dep. var.
  %---- at first the obvious allocations
  for each el3 in el1 do %-- all similarity variables
  if freeoflist(el3,ylist) then xslist:=cons(el3,xslist) else
  if freeoflist(el3,xlist) then yslist:=cons(el3,yslist) else
  h:=cons(el3,h);
  %---- now the symmetry variable
  if freeoflist(el2,ylist) or (length(yslist) = ny) then
  xslist:=cons(el2,xslist)                          else
  if freeoflist(el2,xlist) or (length(xslist) = nx) then
  yslist:=cons(el2,yslist)                          else
  xslist:=cons(el2,xslist);
  %---- now the remaining cases
  for each el3 in h do
  if length(yslist) < ny then yslist:=cons(el3,yslist)
                         else xslist:=cons(el3,xslist);
  return {xslist,yslist}
end$ % of grouping

%----------------------------

algebraic procedure rename_u_(xslist,yslist,el2,u_,u,v)$
begin scalar i,vlist,ulist,el3,h,smv$
  %---- Renaming the u_ to ui in yslist and to vi in xslist
  i:=0;
  vlist:={};
  xslist:=for each el3 in xslist collect
  <<i:=i+1;
    if length xslist>1 then h:=mkid(v,i)
                       else h:=v;
    vlist:=cons(h,vlist);
    if el3=el2 then smv:=h;
    sub(u_=h,el3)
  >>;
  i:=0;
  ulist:={};
  yslist:=for each el3 in yslist collect
  <<i:=i+1;
    if length yslist>1 then h:=mkid(u,i)
                       else h:=u;
    ulist:=cons(h,ulist);
    if el3=el2 then smv:=h;
    sub(u_=h,el3)
  >>;
  return {xslist,yslist,reverse vlist,reverse ulist,smv}
end$ % of rename_u_

%----------------------------

algebraic procedure solve_for_old_var(xslist,yslist,xlist,ylist,nx,ny)$
begin scalar h1,h2$
  %---- Solve for old variables
  h1:=nil;
  h2:=solve(append(yslist,xslist),append(xlist,ylist));

  if h2={} then h1:=t
           else h2:=first h2; %--- possibly other solutions
  if LIST neq lisp(car algebraic h2) then h1:=t else
  if length(h2)<(nx+ny) then el2:=t;
  if h1 then repeat lisp
  <<write"The algebraic system ",append(xslist,yslist),
         " could not be solved for ",append(xlist,ylist),".";
    write"Please enter the solution in form of a list {",
         reval algebraic first xlist,"=...,...",
         reval algebraic first ylist,"=...,...} or enter a ",
         "semicolon ; to end this investigation:";
    algebraic(h2:=termxread())
  >> until h2=nil or ( lisp(pairp algebraic h2)    and
                     (LIST=lisp(car algebraic h2)) and
                     (length(h2)=(nx+ny))              )
        else
  <<lisp<<terpri()$
      write"The suggested solution of the algebraic system which will";
      terpri()$
      write"do the transformation is: ";
      terpri()
    >>;
    write h2;
    if yesp "Is the solution ok?" then else lisp <<
      write"Please enter the solution in form of a list {",
           reval algebraic first xlist,"=...,...",
           reval algebraic first ylist,"=...,...} or enter a ",
           "semicolon ; to end this investigation:";
      algebraic(h2:=termxread())
    >>
  >>;
  return h2
end$ % of solve_for_old_var$

%----------------------------

algebraic procedure switch_r_s(h2,smv,ylist,u,v)$
begin scalar xslist,yslist,el3,h$
  %---- Exchange of dependent and independent variables
  xslist:={};
  yslist:={};
  for each el3 in h2 do if freeof(ylist,lhs el3) then
  xslist:=cons(el3,xslist)                       else
  yslist:=cons(el3,yslist);
  lisp <<terpri()$
         write"In the intended transformation shown above",
              " the dependent ";terpri()$
         if length yslist>2 then
         write"variables are the ",reval algebraic u,"i and " else
         write"variable is ",reval algebraic u," and ";

         if length xslist>2 then
         write"the independent variables are the ",
              reval algebraic v,"i." else
         write"the independent variable is ",reval algebraic v,".";
         terpri()$
         write"The symmetry variable is ",reval algebraic smv,", i.e. the ",
              "transformed expression";terpri();
         write"will be free of ",reval algebraic smv,".";
  >>;
  h:=if yesp "Is this selection of dependent and independent variables ok?"
    then nil else <<
    lisp <<write"Please enter a list of substitutions. For example, to";
           terpri()$
           write"make the variable, which is so far call u1, to an";
           terpri()$
           write"independent variable v2 and the variable, which is ";
           terpri()$
           write"so far called v2, to an dependent variable u1, ";
           terpri()$
           write"enter: `{u1=v2, v2=u1};'">>;
    termxread()
  >>;
  if h and (h neq {}) then <<xslist:=sub(h,xslist);
                             yslist:=sub(h,yslist);
                             smv   :=sub(h,smv)>>;
  return {xslist,yslist,smv}
end$ % of switch_r_s

%----------------------------

algebraic procedure makepde(genlist,u_)$
begin scalar h,el2,el3,vari,bv;
  vari:={};
  return
  cons(
  num for each el2 in genlist sum
  <<h:=lhs el2;
    h:=lisp <<
      el3:=explode reval algebraic h;
      bv:=t;
      while bv do <<
        if car el3 ='!_ then bv:=nil;
        el3:=cdr el3
      >>;
      intern compress el3
    >>;
    depend u_,h;
    vari:=cons(h,vari);
    (rhs el2) * df(u_,h)
  >>,
  vari)
end$ % of makepde

%----------------------------

algebraic procedure totdeglist(eqlist,ylist)$
begin scalar n,ordr,e1,e2;
  ordr:=0;
  for each e1 in eqlist do
  for each e2 in ylist do
  <<n:=totdeg(e1,e2);
    if n>ordr then ordr:=n>>;
  return ordr
end$ % of totdeglist

%----------------------------

algebraic procedure similarity(problem,genlist,con,u,v)$

% con ... the free constants/functions in the general symmetry
% u ... the name of the new independent variables
% v ... the name of the new   dependent variables

begin scalar vari,pde,el1,el2,el3,el4,copgen,symvarfound,
             trans1,trans2,i,j,h,h2,n,denew,xlist,ylist,
             eqlist,ulist,vlist,nx,ny,xslist,yslist,smv,
             trafoprob;

  cpu:=lisp time()$ gc:=lisp gctime()$

  %--------- extracting input data
  eqlist:=maklist  first problem;
  ylist :=maklist second problem;  ny:=length ylist;
  xlist :=maklist  third problem;  nx:=length xlist;

  trafoprob:={problem,nil};  % to be returned if trafo not possible
  problem:=nil;

  eqlist:=for each el1 in eqlist collect equ_to_expr el1;
%  if length eqlist > 1 then eqlist:=desort eqlist;
  %--------- initial printout
  lisp(
  if tr_as then terpri());
  %--------- initializations
  ordr:=totdeglist(eqlist,ylist)$
  vari:=append(ylist,xlist);
  for each el1 in xlist do
  for each el2 in ylist do
  if not my_freeof(el2,el1) then nodepend el2,el1;

  lisp(
  if tr_as then <<
    write "The ODE/PDE (-system) under investigation is :";terpri()$
    for each el1 in cdr eqlist do algebraic write"0 = ",el1;
    terpri()$write "for the function(s) : ";
    fctprint( cdr reval algebraic ylist);write".";
    terpri()$terpri()
  >>);
  lisp(
  if tr_as then <<
    if length ylist >2 then  % not >1 because of alg. list in symb. mode
    write"It will be looked for new dependent variables ",u,"i "
                       else
    write"It will be looked for a new dependent variable ",u;
    terpri()$
    if length xlist >2 then
    write"and independent variables ",v,"i"
                       else
    write"and an independent variable ",v;
    write" such that the transformed";
    terpri()$
    write"de(-system) does not depend on ",u;
    if length ylist >2 then write"1";
    write" or ",v$
    if length xlist >2 then write"1";
    write".";
    terpri()
  >>);

%  for each el1 in con do       % i.e. for all symmetries do:
%  if freeoflist(el1,xlist) and
%     freeoflist(el1,ylist) then        %------ no pseudo-Lie-symm.
%  <<copgen:=sub(el1=1,genlist);        %------ calculate symmetry
%    for each el2 in con do
%    if el1 neq el2 then copgen:=sub(el2=0,copgen);

    copgen:=genlist;
%    write"The symmetry now under investigation is:";
%    for each el1 in copgen do write el1;

    %---------- formulate the PDE for the similarity variables
    pde:=first makepde(copgen,u_);
    %--------- find similarity variable
    trans2 :={};
    lisp<<terpri()$write"1. Determination of the similarity variable";
          if nx+ny>2 then write"s">>;
    trans1 := quasilinpde1(pde,u_,vari); % for the similarity variable
    if trans1 neq {} then
    <<
      %-------------- Determining the similarity variables ui_
      i:=0;
      trans1:=for each el1 in trans1 collect
      <<i:=i+1;
        h:=length(genlist)-1;
        if h=1 then   % one single ODE
        <<
%         write"In the following 1 similarity variable U_ has to be";
%         write"determined through 0 = ff where ff is an"$
%         write"arbitrary function of arguments given in the ",
%              "following list:"$
%         write el1;
          el2:=num(first el1 - second el1);
          if freeof(el2,u_) then
          el2:=num(first el1 - 2*second el1);
          lisp<<write"A suggestion for this function ff provides:"$
                terpri()>>$
          write"0 = ", el2$
          if yesp "Do you like this choice?" then {el2}
                                             else <<
            repeat <<
              lisp <<
                write"Put in an alternative expression which "$terpri()$
                write"- is functionally dependent only on elements of",
                     " ff given above and"$ terpri()$
                write"- depends on U_ and if set to zero determines U_"$
                terpri()>>$
              h:=termxread()$
            >> until not freeof(h,U_)$
            {h}
          >>
        >>     else   % a PDE or a system of DEs
        lisp
        <<
%         terpri()$
%         write"Now the similarity variables U_i, i=1,...",h,
%              " have to be"$terpri()$
%         write"determined through conditions 0 = ffi, i=1,...",h,
%              ", where ffi are"$terpri()$
%         write"arbitrary functions ";terpri()$
%         algebraic(write "ffi = ",
%         lisp( cons('ffi,cdr reval algebraic el1)));
%         terpri()$
%         write"such that the functional determinant of these ",
%               "expressions"$ terpri()$
%         write"including u_ from above taken w.r.t. ";
%         for each el3 in cdr algebraic ylist do write reval el3,",";
%         write reval cadr algebraic xlist;
%         for each el3 in cddr algebraic xlist do write ",",reval el3;
%         terpri()$
%         write"must not vanish."$terpri()$
          algebraic <<
            el2:=einfachst(el1,u_);
            h2:={};
            for each el3 in el1 do
            if el3 neq el2 then h2:=cons(num(el2-el3),h2)
          >>;
          write"A suggestion for these functions ffi in form of a list ",
               "{ff1,ff2,... } is: "$terpri()$
          deprint(cdr reval algebraic h2);
          if yesp "Do you like this choice?" then algebraic h2
                                             else <<
            write"Put in an alternative list of expression which"$
            terpri()$
            write"- are functionally dependent only on the above ",
                 "arguments and"$terpri()$
            write"- which if set to zero determine U_i, i.e."$terpri()$
            write"- the functional determinant of these expressions"$
            terpri()$
            write"  including U_ from above taken w.r.t. ",
            cdr reval algebraic append(ylist,xlist)$
            terpri()$
            write"  must not vanish."$terpri()$
            algebraic(h2):=termxread()
          >>
        >>
      >>$
      %--------- find symmetry variable
      pde:=pde-1;
      lisp<<terpri()$write"2. Determination of the symmetry variable">>;
      trans2 := quasilinpde1(pde,u_,vari);  % for the symmetry variable

      if (length xlist=1) and (trans2={}) then
      for each e1 in fargs u_ do nodepend u_,e1
                                          else
      <<% If no symmetry variable is found (trans2={}) then proceed
        % only if special solutions of PDEs are to be found with the
        % solution being only a function of the similarity variables
        if trans2={} then << % take any variable
          h:=reverse vari;  % reverse to have not a function as symvar
          while 1+sub(u_=first h,pde)=0 do h:=rest h; % no similarity var.
          for each e1 in fargs u_ do nodepend u_,e1;
          h:=first h;
          trans2:={u_ - h};
          lisp<<write"Because the correct symmetry variable was not ",
                     "found, the program will";terpri()$
                write"take ",reval algebraic h,
                     " instead with the consequence ",
                     "that not the whole transformed ";terpri()$
                write"PDE will be free of ",
                     reval algebraic h," but only those ",
                     "terms without ",
                     reval algebraic h,"-derivative";terpri()$
                write"which is still of use for finding special ",
                     reval algebraic h,"-independent solutions ";
                terpri()$
                write"of the PDE."
          >>
        >>           else <<
          %--------------- Determining an optimal symmetry variable
          for each e1 in fargs u_ do nodepend u_,e1;
          symvarfound:=t;
          i:=0;
          trans2:=for each el1 in trans2 collect
          <<i:=i+1;
            lisp
            <<
    %         terpri()$
    %         write"In the following the symmetry variable U_",
    %              " has to be"$terpri()$
    %         write"determined through a condition 0 = ff",
    %              ", where ff is"$terpri()$
    %         write"an arbitrary function ";terpri()$
    %         algebraic(write"ff = ",
    %         lisp( cons('ff,cdr reval algebraic el1)));terpri()$
              write"A suggestion for this function ff(..) yields:";terpri()
            >>;
            h:=einfachst(el1,u_);
            if lisp<<h2:=reval algebraic(num h);
                     (not pairp h2) or (car h2 neq 'PLUS)>> then
            h:=num(h+1);
    %       if h= first el1 then
    %       if freeof(num(h+second el1),u_) then h:=num(h+2*second el1)
    %                                       else h:=num(h+  second el1)
    %                       else
    %       if freeof(num(h+ first el1),u_) then h:=num(h+2* first el1)
    %                                       else h:=num(h+   first el1);
            write"0 = ",h$
            if yesp "Do you like this choice?" then h
                                               else <<
              repeat <<
                lisp <<
                  write"Put in an alternative expression which "$terpri()$
                  write"- is functionally dependent only on arguments of",
                       " ff given above and"$terpri()$
                  write"- depends on u_ and if set to zero determines u_"$
                  terpri()>>$
                h:=termxread()$
              >> until not freeof(h,u_)$
              h
            >>
          >>
        >>$
        %for each el1 in trans1 do
        %for each el2 in trans2 do

        el1:=first trans1;
        el2:=first trans2;

%        <<

          %------- Grouping the new variables to ulist and vlist

          yslist:=grouping(el1,el2,xlist,ylist,nx,ny)$
          xslist:= first yslist;
          yslist:=second yslist;

          %---- Renaming the u_ to ui in yslist and to vi in xslist
          smv:=rename_u_(xslist,yslist,el2,u_,u,v)$
          xslist:= first smv;
          yslist:=second smv;
          vlist := third smv;
          ulist:=part(smv,4);
          smv:=part(smv,5);     % the symmetry variable

          %---- Solve for old variables
          h2:=solve_for_old_var(xslist,yslist,xlist,ylist,nx,ny);

          if h2 neq nil then <<

            %---- Exchange of dependent and independent variables
            smv:=switch_r_s(h2,smv,ylist,u,v)$
            xslist:= first smv;
            yslist:=second smv;
            smv   :=third smv;

            %---- Doing the point transformation
            for each el3 in ulist do
            <<for each el4 in fargs el3 do nodepend el3,el4;
              for each el4 in vlist do
              %if el4 neq smv then % if new DEs without symm. var. smv
              depend el3,el4>>;

            for each el3 in ylist do
            for each el4 in xlist do depend el3,el4;

            eqlist:=DeTrafo(eqlist,yslist,xslist,ulist,vlist);
            lisp(
            if tr_as then <<
              terpri()$
              write"The transformed equation";
              if length(algebraic eqlist)>2 then write"s";
              if symvarfound then
              write" which should be free of ",reval algebraic smv,":"
                             else
              write" in which the terms without ",reval algebraic smv,
                   "-derivative are free of ",reval algebraic smv,":";
              terpri()
            >>);
            eqlist:=for each el3 in eqlist collect <<
              el3:=factorize num el3;
              for each el4 in el3 product
              if 0=totdeglist({el4},ulist) then 1
                                           else el4
            >>;
            lisp deprint(cdr reval algebraic eqlist);

            if (length(vlist)>1) and (not freeof(vlist,smv)) then <<
              vlist:=cons(smv,lisp(delete(reval algebraic smv,
                                          reval algebraic vlist)));
              if yesp
            "Shall the dependence on the symmetry variable be dropped?"
              then
              <<for each el3 in ulist do
                if not my_freeof(el3,smv) then nodepend el3,smv;
                vlist:=rest vlist>>;
              eqlist:=for each el3 in eqlist collect <<
                el3:=factorize num el3;
                for each el4 in el3 product
                if 0=totdeglist({el4},ulist) then 1
                                             else el4
              >>
            >>;
            trafoprob:={{eqlist,ulist,vlist},append(xslist,yslist)}
          >>
%        >>
      >>
    >>;
%  >>;
  for each el1 in xlist do
  for each el2 in ylist do
  depend el2,el1;

  clear ff,ffi;
  return trafoprob
end$ % of similarity

%----------------------------

algebraic procedure quasilinpde1(pde,u_,vari)$
begin scalar trans1,e1,e2,q;
  trans1 := quasilinpde(pde,u_,vari); % for the similarity variable
  if trans1={} then <<
    write"The program was not able to find the general solution ",
         "of the PDE: ",pde," for the function ",u_,".";
    lisp <<
      write"Please enter either only a semicolon if no solution ",
           "is known or enter "$terpri()$
      write"the solution of the PDE in form ",
           "of a list {A1,A2,...} where ";terpri()$
      write"the Ai are algebraic expressions in ",
           cdr reval algebraic cons(u_,vari);terpri()$
      write"such that any function ff(A1,A2,...) which is not ",
           "independent of `",u_;write"'";terpri()$
      write"determines a solution `",u_,"' of the PDE through 0=ff: "
    >>;
    trans1:={termxread()};
    if trans1={nil} then trans1:={}
  >>;
  return trans1
end$ % of quasilinpde1

end$

