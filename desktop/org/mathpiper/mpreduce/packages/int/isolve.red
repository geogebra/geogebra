module isolve;   % Routines for solving the final reduction equation.

% Author: Mary Ann Moore and Arthur C. Norman.
% Modifications by: John P. Fitch.

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


fluid '(!*trint
        badpart
        ccount
        cmap
        cmatrix
        cval
        indexlist
        lhs!*
        lorder
        orderofelim
        power!-list!*
        pt
        rhs!*
        sillieslist
        tanlist
        ulist
        zlist);

global '(!*number!* !*statistics);

exports solve!-for!-u;

imports nth,findpivot,gcdf,int!-gensym1,mkvect,interr,multdfconst,
   !*multf!*,negdf,orddf,plusdf,printdf,printsf,printspreadc,printsq,
   quotf,putv,spreadc,subst4eliminatedcs,mknill,pnth,domainp,addf,
   invsq,multsq;

symbolic procedure uterm(powu,rhs!*);
% Finds the contribution from RHS!* of reduction equation, of the
% U-coefficient given by POWU. Result is in D.F.
   if null rhs!* then nil
   else begin    scalar coef,power;
      power:=addinds(powu,lpow rhs!*);
      coef:=evaluatecoeffts(numr lc rhs!*,powu);
      if null coef then return uterm(powu,red rhs!*);
      coef:=coef ./ denr lc rhs!*;
      return plusdf((power .* coef) .+ nil,uterm(powu,red rhs!*))
   end;

symbolic procedure solve!-for!-u(rhs!*,lhs!*,ulist);
   % Solves the reduction eqn LHS!*=RHS!*.  Returns list of U-coeffs
   % and their values (ULIST are those we have so far), and a list of
   % C-equations to be solved (CLIST are the eqns we have so far).
   begin
   top:
      if null lhs!* then return ulist
       else begin scalar u,lpowlhs;
         lpowlhs := lpow lhs!*;
         begin scalar ll,m1,chge;
           ll:=maxorder(power!-list!*,zlist,0);
           m1:=lorder;
           while m1 do << if car ll < car m1 then
                   << chge:=t; rplaca(m1,car ll) >>;
               ll:=cdr ll; m1:=cdr m1 >>;
           if !*trint and chge then <<
              princ
           "Maximum order for undetermined coefficients is reduced to ";
              printc lorder >>
         end;
         u:=pickupu(rhs!*,lpow lhs!*,t);
         if null u then
         << if !*trint then <<
                printc "***** Equation for a constant to be solved:";
                printsf numr lc lhs!*;
                printc "    = 0";
                printc " ">>;
             % Remove a zero constant from the lhs, rather than use
             % Gauss Elim;
           if gausselimn(numr lc lhs!*,lt lhs!*) then <<
                    lhs!*:=squashconstants(red lhs!*); u := t >>
           else lhs!*:=red lhs!* >>
         else
         << ulist:=(car u .
              subs2q multsq(coefdf(lhs!*,lpowlhs),invsq cdr u)).ulist;
                   % used to be !*multsq.  However, i^2 was not handled
                   % correctly.
           if !*statistics then !*number!*:=!*number!*+1;
            if !*trint then <<
                printc "A coefficient of numerator has been determined";
                   prin2 "***** U"; prin2 car u; prin2t " =";
                   printsq multsq(coefdf(lhs!*,lpowlhs),invsq cdr u);
                   printc " ">>;
            lhs!*:=plusdf(lhs!*,
                   negdf multdfconst(cdar ulist,uterm(car u,rhs!*)))>>;
         if !*trint and u
           then <<printc "Terms remaining are:"; printdf lhs!*;
                  printc " ">>
      end;
      go to top
   end;

symbolic procedure squashconstants(express);
  if null cmatrix then express 
   else begin scalar constlst,ii,xp,cl,subby,cmt,xx;
        constlst:=reverse cmap;
        cmt:=cmatrix;
xxx:    if null cmt then return express; % nothing to squash
        xx:=car cmt;            % Look at next row of Cmatrix
        cl:=constlst;           % and list of the names.
        ii:=1;                   % will become index of removed constant.
        while not getv(xx,ii) do
                << ii:=ii+1; cl:=cdr cl >>;
        subby:=caar cl;         %II is now index, and SUBBY the name.
        if member(subby,sillieslist) then
                <<cmt:=cdr cmt; go to xxx>>; %This loop must terminate.
                        % This is because at least one constant remains.
        xp:=prepsq !*f2q getv(xx,0);    % start to build up the answer.
        cl:=cdr cl;
        if not (ccount=ii) then for jj:=ii+1:ccount do <<
                if getv(xx,jj) then
                        xp:=list('plus,xp,
                                list('times,caar cl,
                                        prepsq !*f2q getv(xx,jj)));
                cl:=cdr cl >>;
        xp:=list('quotient,list('minus,xp),
                        prepsq !*f2q getv(xx,ii));
        if !*trint then <<
                prin2 "Replace constant "; prin2 subby;
                prin2 " by "; printsq simp xp >>;
        sillieslist:=subby . sillieslist;
        return subdf(express,xp,subby)
end;

symbolic procedure checku(ulst,u);
   % Checks that U is not already in ULST - ie. that this u-coeff
   % has not already been given a value.
   ulst and (car u = caar ulst or checku(cdr ulst,u));

symbolic procedure checku1(powu,rhs!*);
% Checks that use of a particular U-term will not cause trouble
% by introducing negative exponents into lhs when it is used.
    begin
    top:
        if null rhs!* then return nil;
        if negind(powu,lpow rhs!*) then
          if not null evaluatecoeffts(numr lc rhs!*,powu) then return t;
        rhs!*:=red rhs!*;
        go to top
    end;

symbolic procedure negind(pu,pr);
% Check if substituting index values in power gives rise to -ve
% exponents.
    pu and ((car pu+caar pr)<0 or negind(cdr pu,cdr pr));

symbolic procedure evaluatecoeffts(coefft,indlist);
% Substitutes the values of the i,j,k,...'s that appear in the S.F.
% COEFFT (=coefficient of r.h.s. of reduction equation). Result is S.F.
   if null coefft or domainp coefft then
      if coefft=0 then nil else coefft
   else begin    scalar temp;
      if mvar coefft member indexlist then
         temp:=valuecoefft(mvar coefft,indlist,indexlist)
      else temp:=!*p2f lpow coefft;
      temp:=!*multf(temp,evaluatecoeffts(lc coefft,indlist));
      return addf(temp,evaluatecoeffts(red coefft,indlist))
   end;

symbolic procedure valuecoefft(var,indvalues,indlist);
% Finds the value of VAR, which should be in INDLIST, given INDVALUES,
% the corresponding values of INDLIST variables.
   if null indlist then interr "Valuecoefft - no value"
   else if var eq car indlist then
      if car indvalues=0 then nil
      else car indvalues
   else valuecoefft(var,cdr indvalues,cdr indlist);

symbolic procedure addinds(powu,powrhs);
% Adds indices in POWU to those in POWRHS. Result is LPOW of D.F.
   if null powu then if null powrhs then nil
      else interr "Powrhs too long"
   else if null powrhs then interr "Powu too long"
   else (car powu + caar powrhs).addinds(cdr powu,cdr powrhs);


symbolic procedure pickupu(rhs!*,powlhs,flg);
% Picks up the 'lowest' U coefficient from RHS!* if it exists and
% returns it in the form of LT of D.F..
% Returns NIL if no legal term in RHS!* can be found.
% POWLHS is the power we want to match (LPOW of D.F).
% and COEFFU is the list of previous coefficients that must be zero.
 begin scalar coeffu,u;
    pt:=rhs!*;
top:
    if null pt then return nil; %no term found - failed.
    u:=nextu(lt pt,powlhs); %check this term...
    if null u then go to notthisone;
    if not testord(car u,lorder) then go to neverthisone;
    if not checkcoeffts(coeffu,car u) then go to notthisone;
    %that inhibited clobbering things already passed over.
    if checku(ulist,u) then go to notthisone;
    %that avoided redefining a u value.
    if checku1(car u,rhs!*) then go to neverthisone;
    %avoid introduction of negative exponents.
    if flg then
        u:=patchuptan(list u,powlhs,red pt,rhs!*);
    return u;
neverthisone:
    coeffu:=(lc pt) . coeffu;
notthisone:
    pt:=red pt;
    go to top
 end;

symbolic procedure patchuptan(u,powlhs,rpt,rhs!*);
        begin
            scalar uu,cc,dd,tanlist,redu,redu1,mesgiven,needsquash;
            pt:=rpt;
            while pt do <<
                if (uu:=pickupu(pt,powlhs,nil))
                        and testord(car uu,lorder) then <<
                                % Nasty found, patch it up.
                    cc:=(int!-gensym1 'c . caar u) . cc;
                                % CC is an alist of constants.
                    if !*trint then <<
                        if not mesgiven then << %% Changed by JPff
                          prin2t
                         "*** Introduce new constants for coefficients";
                          mesgiven := t >>;
                        prin2 "***** U";
                        prin2 caar u;
                        prin2t " =";
                        print caar cc >>;
                    redu:=plusdf(redu,
                        multdfconst(!*k2q caar cc,uterm(caar u,rhs!*)));
                    u:=uu.u
                >>;
                if pt then pt:=red pt >>;
            redu1:=redu;
            while redu1 do begin scalar xx; xx:=car redu1;
                if !*trint
                  then << prin2 "Introduced terms: ";
                          prin2 car xx; princ "*(";
                          printsq cdr xx; printc ")">>;
                if (not testord(car xx,lorder)) then <<
                    if !*trint then printc "  =  0";
                    if dd:=killsingles(cadr xx,cc) then <<
                        redu:=subdf(redu,0,car dd);
                        redu1:=subdf(redu1,0,car dd);
                        ulist:=((cdr dd).(nil ./ 1)).ulist;
                        u:=rmve(u,cdr dd);
                        cc:=purgeconst(cc,dd) >>
                    else <<
                        needsquash := t;
                        redu1 :=cdr redu1
                    >>
                >>
                else redu1:=cdr redu1  end;
            for each xx in redu do <<
                if (not testord(car xx,lorder)) then <<
                    while cc do <<
                                addctomap(caar cc);
                                ulist:=((cdar cc).(!*k2q caar cc))
                                          . ulist;
                                if !*statistics
                                  then !*number!*:=!*number!*+1;
                                cc:=cdr cc >>;
                        gausselimn(numr lc redu,lt redu)>> >>;
            if redu then << while cc do << addctomap(caar cc);
                        ulist:=((cdar cc).(!*k2q caar cc)).ulist;
                        if !*statistics then !*number!*:=!*number!*+1;
                        cc:=cdr cc >>;
                lhs!*:=plusdf(lhs!*,negdf redu);
                if needsquash then lhs!*:=squashconstants(lhs!*) >>;
        return car u
end;

symbolic procedure killsingles(xx,cc);
  if atom xx then nil
  else if not (cdr xx eq nil) then nil
  else begin scalar dd;
    dd:=assoc(caaar xx,cc);
    if dd then return dd;
    return killsingles(cdar xx,cc)
end;

symbolic procedure rmve(l,x);
   if caar l=x then cdr l else cons(car l,rmve(cdr l,x));

symbolic procedure subdf(a,b,c);
% Substitute b for c into the df a. Used to get rid of silly constants
% introduced.
if a=nil then nil else
  begin scalar x;
    x:=subs2q subf(numr lc a,list (c . b)) ;
    if x=(nil . 1) then return subdf(red a,b,c)
        else return plusdf(
                list ((lpow a).((car x).!*multf(cdr x,denr lc a))),
                subdf(red a,b,c))
end;

symbolic procedure testord(a,b);
% Test order of two DF's in recursive fashion.
  if null a then t
    else if car a leq car b then testord(cdr a,cdr b)
    else nil;

symbolic procedure tansfrom(rhs,z,indexlist,n);
% We notice that in all bad cases we have (j-num)tan**j...;
% Extract the num to get list of all maxima;
if null z then nil else
begin scalar zz,r, rr, ans;
    r:=rhs;
    zz := car z;
    ans := 0;
    if not(atom zz) and car zz = 'tan then
      while  r do <<
        rr:=caar r;  % The list of powers;
        for i:=1:n do rr:=cdr rr;
        if fixp caar rr then
                ans := max(ans,tanextract(car indexlist,prepsq cdar r));
        r:=cdr r;
      >>;
    return cons(ans,tansfrom(rhs, cdr z,cdr indexlist,n+1))
end;

symbolic procedure tanextract(var, exp);
% Find the value of the variable which makes the expression vanish.
% The coefficients must be linear.
begin scalar ans, c0, c1;
  ans := cdr coeff1(exp,var,nil);
  if length ans = 2 and
     not(car ans = 0) then <<
        c0 := car ans; c1 := cadr ans;
        if eqcar(c0,'!*sq) then c0 := cadr c0 else c0 := c0 ./ 1;
        if eqcar(c1,'!*sq) then c1 := cadr c1 else c1 := c1 ./ 1;
        ans := multsq(c0, invsq c1);
        if atom ans then return 0;
        if (cdr ans = 1) and fixp (car ans) then return -(car ans);
        return 0 >>;
  return 0;
end;

symbolic procedure coefdf(y,u);
  if y=nil then nil
  else if lpow y=u then lc y
  else coefdf(red y,u);


symbolic procedure purgeconst(a,b);
% Remove a constant from and expression. May be the same as DELETE?
  if null a then nil
  else if car a=b then purgeconst(cdr a,b)
  else cons(car a,purgeconst(cdr a,b));

symbolic procedure maxorder(minpowers,z,n);
% Find a limit on the order of terms, this is ad hoc;
  if null z then nil
    else if eqcar(car z,'sqrt) then
        cons(1,maxorder(cdr minpowers,cdr z,n+1))
    else if (atom car z) or (caar z neq 'tan) then
        cons(maxfrom(lhs!*,n)+1,maxorder(cdr minpowers,cdr z,n+1))
    else cons(max(car minpowers, maxfrom(lhs!*,n)),
              maxorder(cdr minpowers,cdr z,n+1));

symbolic procedure maxfrom(l,n); maxfrom1(l,n+1,0);

symbolic procedure maxfrom1(l,n,v);
   % Largest order in the nth variable.
   if null l then v
    else <<v := max(nth(caar l,n),v); maxfrom1(cdr l,n,v)>>;

symbolic procedure addctomap cc;
begin
    scalar ncval;
    ccount:=ccount+1;
    ncval:=mkvect(ccount);
    for i:=0:(ccount-1) do putv(ncval,i,getv(cval,i));
    putv(ncval,ccount,nil ./ 1);
    cval:=ncval;
    cmap:=(cc . ccount).cmap;
    if !*trint then << prin2 "Constant map changed to "; print cmap >>;
    cmatrix := for each j in cmatrix collect addtovector j
end;

symbolic procedure addtovector v;
    begin scalar vv;
        vv:=mkvect(ccount);
        for i:=0:(ccount-1) do putv(vv,i,getv(v,i));
        putv(vv,ccount,nil);
        return vv
    end;

symbolic procedure checkcoeffts(cl,indv);
% checks to see that the coefficients in CL (coefficient list - S.Q.s)
% are zero when the i,j,k,... are given values in INDV (LPOW of
% D.F.). if so the result is true else NIL=false.
    if null cl then t
    else begin    scalar res;
        res:=evaluatecoeffts(numr car cl,indv);
        if not(null res or res=0) then return nil
        else return checkcoeffts(cdr cl,indv)
    end;

symbolic procedure nextu(ltrhs,powlhs);
% picks out the appropriate U coefficients for term: LTRHS to match the
% powers of the z-variables given in POWLHS (= exponent list of D.F.).
% return this coefficient in form LT of D.F. If U coefficient does
% not exist then result is NIL. If it is multiplied by a zero then
% result is NIL.
   if null ltrhs then nil
   else begin    scalar indlist,ucoefft;
      indlist:=subtractinds(powlhs,car ltrhs,nil);
      if null indlist then return nil;
      ucoefft:=evaluatecoeffts(numr cdr ltrhs,indlist);
      if null ucoefft or ucoefft=0 then return nil;
      return indlist .* (ucoefft ./ denr cdr ltrhs)
   end;

symbolic procedure subtractinds(powlhs,l,sofar);
% subtract the indices in list L from those in POWLHS to find
% appropriate values for i,j,k,... when equating coefficients of terms
% on lhs of reduction eqn. SOFAR is the resulting value list we have
% constructed so far. if any i,j,k,... value is -ve then result is NIL.
    if null l then reversip sofar
    else if ((car powlhs)-(caar l))<0 then nil
    else subtractinds(cdr powlhs,cdr l,
        ((car powlhs)-(caar l)) . sofar);

symbolic procedure gausselimn(equation,tokill);
% Performs Gaussian elimination on the matrix for the c-equations
% as each c-equation is found. EQUATION is the next one to deal with.
   begin         scalar newrow,pivot;
      if ccount=0 then go to noway; % failure.
      newrow:=mkvect(ccount);
      spreadc(equation,newrow,1);
      subst4eliminatedcs(newrow,reverse orderofelim,reverse cmatrix);
      newrow:=makeprim newrow; % remove hcf from new equation.
      pivot:=findpivot newrow;
      if null pivot then go to nopivotfound;
      orderofelim:=pivot . orderofelim;
      cmatrix:=newrow . cmatrix;
%      if !*trint then printspreadc newrow;
      return t;
 nopivotfound:
      if null getv(newrow,0) then <<
        if !*trint then printc "This equation adds no new information";
        return nil>>; % equation was 0=0.
 noway:
      badpart:=tokill . badpart; % non-integrable term.
      if !*trint then
        <<printc "Inconsistency in equations for constants,";
          printc "  so non integrable">>;
      return nil
   end;

symbolic procedure makeprim row;
    begin scalar g;
        g:=getv(row,0);
        for i:=1:ccount do g:=gcdf(g,getv(row,i));
        if g neq 1 then
           for i:=0:ccount do putv(row,i,quotf(getv(row,i),g));
        for i := 0:ccount do
          <<g := getv(row,i);
            if g and not domainp g
              then putv(row,i,numr resimp((rootextractf g) ./ 1))>>;
        return row
    end;

endmodule;

end;
