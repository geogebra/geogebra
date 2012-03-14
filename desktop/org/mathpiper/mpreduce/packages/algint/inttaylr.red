module inttaylr;

% Author: James H. Davenport.

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


fluid '(const taylorasslist taylorvariable);

exports taylorform,taylorformp,taylorevaluate,return0,taylorplus,
         initialtaylorplus,taylorminus,initialtaylorminus,
         tayloroptminus,tayloroptplus,taylorctimes,initialtaylortimes,
         tayloroptctimes,taylorsqrtx,initialtaylorsqrtx,
         taylorquotient,initialtaylorquotient,taylorformersqrt,
         taylorbtimes,taylorformertimes,taylorformerexpt;

 symbolic procedure taylorform sq;
 if involvesf(denr sq,taylorvariable)
   then taylorformp list('quotient,tayprepf numr sq,tayprepf denr sq)
   else if 1 iequal denr sq
     then taylorformp tayprepf numr sq
     else taylorformp list('constanttimes,
                           tayprepf numr sq,
                           mk!*sq(1 ./ (denr sq)));
 % get division by a constant right.


 symbolic procedure taylorformp pf;
 if null pf
   then nil
   else if not dependsp(pf,taylorvariable)
     then taylorconst simp pf
     else begin
       scalar fn,initial,args,n;
       if atom pf
         then if pf eq taylorvariable
           then return taylorformp list ('expt,pf,1)
           else interr "False atom in taylorformp";
       % get 'x right as reduce shorthand for x**1.
       if taylorp pf
         then return pf;
       % cope with pre-expressed cases.
       % ***store-hack-1***
       % remove the (car pf eq 'sqrt) if more store is available.
       if (car pf eq 'sqrt) and
          (fn:=assoc(pf,taylorasslist))
         then go to lookupok;
       % look it up first.
       fn:=get(car pf,'taylorformer);
       if null fn
         then go to ordinary;
       fn:=apply1(fn,cdr pf);
       % ***store-hack-1***
       % remove the test if more store is available.
       if car pf eq 'sqrt
         then taylorasslist:=(pf.fn).taylorasslist;
       return fn;
       % cope with the special cases.
     ordinary:
       args := for each j in cdr pf collect taylorformp j;
       fn:=get(car pf,'tayloropt);
       if null fn
         then go to nooptimisation;
       fn:=apply1(fn,args);
       if fn
         then go to ananswer;
       % an optimisation has been made.
     nooptimisation:
       fn:=get(car pf,'taylorfunction);
       if null fn
         then interr "No Taylor function provided";
       fn:=fn.args;
       % fn is now the "how to compute" code.
       initial:=get(car pf,'initialtaylorfunction);
       if null initial
         then interr "No initial Taylor function";
       initial:=lispapply(initial,
                      list for each u in cdr fn collect firstterm u);
       % the first term in the expansion, or so we hope.
       n:=car initial;
       fn:=list(fn,n.n,initial);
       while null numr cdr initial do <<
             n:=n+1;
             if !*tra then lprim list("Increasing accuracy to",n);
             initial:=n.taylorevaluate(fn,n);
             fn:=list(car fn,n.n,initial);
             >>;
     ananswer:
       % ***store-hack-1***
       % uncomment this if more store is available;
       % taylorasslist:=(pf.fn).taylorasslist;
       return fn;
     lookupok:
       % These PRINT statements can be enabled in order to test the
       % efficacy of the association list
 %      printc "Taylor lookup succeeded";
 %      superprint car fn;
 %      printc length taylorasslist;
       return cdr fn
       end;


 symbolic procedure taylorevaluate(texpr,n);
 if n<taylorfirst texpr
   then nil ./ 1
   else if n>taylorlast texpr
     then tayloreval2(texpr,n)
     else begin
       scalar u;
       u:=assoc(n,taylorlist texpr);
       if u
         then return cdr u
         else return tayloreval2(texpr,n)
       end;


 symbolic procedure tayloreval2(texpr,n);
 begin
   scalar u;
   % actually evaluates from scratch.
   u:=apply3(taylorfunction texpr,n,texpr,cdr taylordefn texpr);
   if 'return0 eq taylorfunction texpr
     then return u;
   % no need to update with trivial zeroes.
   rplacd(cdr texpr,(n.u).taylorlist texpr);
   % update the association list.
   if n>taylorlast texpr
     then rplacd(taylornumbers texpr,n);
   % update the first/last pointer.
   return u
   end;


 symbolic procedure taylorconst sq;
 list('return0 . nil,0 . 0,0 . sq);


 symbolic procedure return0 (a,b,c);
 nil ./ 1;

 flag('(return0),'taylor);


 symbolic procedure firstterm texpr;
 begin
   scalar n,i;
   i:=taylorfirst texpr;
 trynext:
   n:=taylorevaluate(texpr,i);
   if numr n
     then return i.n;
   if i > 50
     then interr "Potentially zero Taylor series";
   i:=iadd1 i;
   rplaca(taylornumbers texpr,i);
   go to trynext
   end;


 symbolic procedure tayloroneterm u;
 % See if a Taylor expression has only one term.
  'return0 eq taylorfunction u and taylorfirst u=taylorlast u;


 % ***store-hack-1***;
 % uncomment this procedure if more store is available;
 % there is a smacro for this at the start of the file
 % for use if no store can be spared;
 %symbolic procedure tayshorten(save);
 %begin
 %  scalar z;
 %  % shortens the association list back to save,
 %    removing all the non-sqrts from it;
 %  while taylorasslist neq save do <<
 %    if caar taylorasslist eq 'sqrt
 %      then z:=(car taylorasslist).z;
 %    taylorasslist:=cdr taylorasslist >>;
 %  taylorasslist:=nconc(z,taylorasslist);
 %  return nil
 %  end;


 symbolic procedure tayprepf sf;
 if atom sf
   then sf
   else if atom mvar sf
     then taylorpoly makemainvar(sf,taylorvariable)
     else if null red sf
       then tayprept lt sf
       else list('plus,tayprept lt sf,tayprepf red sf);


 symbolic procedure tayprept term;
 if tdeg term = 1
   then if tc term = 1
     then tvar term
     else list('times,tvar term,tayprepf tc term)
   else if tc term = 1
     then list ('expt,tvar term,tdeg term)
     else list('times,list('expt,tvar term,tdeg term),
                    tayprepf tc term);


 symbolic procedure taylorpoly sf;
 % SF is a poly with MVAR = TAYLORVARIABLE.
 begin
   scalar tmax,tmin,u;
   tmax:=tmin:=ldeg sf;
   while sf do
     if atom sf or (mvar sf neq taylorvariable)
       then <<
         tmin:=0;
         u:=(0 . !*f2q sf).u;
         sf:=nil >>
       else <<
         u:=((tmin:=ldeg sf) . !*f2q lc sf) . u;
         sf:=red sf >>;
   return (list 'return0) . ((tmin.tmax).u)
   end;


 symbolic procedure taylorplus(n,texpr,args);
 mapply(function !*addsq,
        for each u in args collect taylorevaluate(u,n));


 symbolic procedure initialtaylorplus slist;
 begin
   scalar n,numlst;
   n:=mapply(function min2,mapovercar slist);
   % the least of the degrees.
   numlst:=nil;
   while slist do <<
     if caar slist iequal n
       then numlst:=(cdar slist).numlst;
     slist:=cdr slist >>;
   return n.mapply(function !*addsq,numlst)
   end;


 put ('plus,'taylorfunction,'taylorplus);
 put ('plus,'initialtaylorfunction,'initialtaylorplus);


 symbolic procedure taylorminus(n,texpr,args);
 negsq taylorevaluate(car args,n);


 symbolic procedure initialtaylorminus slist;
 (caar slist).(negsq cdar slist);


 put('minus,'taylorfunction,'taylorminus);
 put('minus,'initialtaylorfunction,'initialtaylorminus);


 flag('(taylorplus taylorminus),'taylor);


 symbolic procedure tayloroptminus(u);
 if 'return0 eq taylorfunction car u
   then taylormake(taylordefn car u,
                   taylornumbers car u,
                   taylorneglist taylorlist car u)
   else if 'taylorctimes eq taylorfunction car u
     then begin
       scalar const;
       u:=car u;
       const:=caddr taylordefn u;
       % the item to be negated.
       const:=taylormake(taylordefn const,
                         taylornumbers const,
                         taylorneglist taylorlist const);
       return taylormake(list(taylorfunction u,
                              argof taylordefn u,
                              const),
                         taylornumbers u,
                         taylorneglist taylorlist u)
       end
     else nil;
 put('minus,'tayloropt,'tayloroptminus);


 symbolic procedure taylorneglist u;
    for each v in u collect (car v . negsq cdr v);


 symbolic procedure tayloroptplus args;
 begin
   scalar ret,hard,u;
   u:=args;
   while u do <<
     if 'return0 eq taylorfunction car u
       then ret:=(car u).ret
       else hard:=(car u).hard;
     u:=cdr u >>;
   if null ret or
       null cdr ret
     then return nil;
   ret:=mapply(function joinret,ret);
   if null hard
     then return ret;
   rplaca(args,ret);
   rplacd(args,hard);
    return nil
   end;
 put('plus,'tayloropt,'tayloroptplus);


 symbolic procedure joinret(u,v);
 begin
   scalar nums,a,b,al;
   nums:=(min2(taylorfirst u,taylorfirst v).
          max2(taylorlast u,taylorlast v));
   al:=nil;
   u:=taylorlist u;
   v:=taylorlist v;
   for i:=(car nums) step 1 until (cdr nums) do <<
     a:=assoc(i,u);
     b:=assoc(i,v);
     if a
       then if b
         then al:=(i.!*addsq(cdr a,cdr b)).al
         else al:=a.al
       else if b
         then al:=b.al  >>;
   return taylormake(list 'return0,nums,al)
   end;




 % the operator constanttimes
 % has two arguments (actually a list)
 % 1) a form dependent on the taylorvariable
 % 2) a form which is not.


 % the operator binarytimes has two arguments (actually a list)
   % but behaves like times otherwise.


 symbolic procedure taylorctimes(n,texpr,args);
 !*multsq(taylorevaluate(car args,n-(taylorfirst cadr args)),
        taylorevaluate(cadr args,taylorfirst cadr args));


 symbolic procedure initialtaylortimes slist;
 % Multiply the variable by the constant.
 ((caar slist)+(caadr slist)). !*multsq(cdar slist,cdadr slist);


 symbolic procedure tayloroptctimes u;
 if 'taylorctimes eq taylorfunction car u
   then begin
     scalar reala,const,iconst,degg;
     % we have nested multiplication.
     reala:=argof taylordefn car u;
     % the thing to be multiplied by the two constants.
     const:=car taylorlist cadr u;
     %the actual outer constant: deg.sq.
     iconst:=caddr taylordefn car u;
     %the inner constant.
     degg:=(taylorfirst iconst)+(car const);
     iconst:=list(taylordefn iconst,
                   degg.degg,
                   degg.!*multsq(cdar taylorlist iconst,cdr const));
     return list('taylorctimes,reala,iconst).
                 ((((taylorfirst car u) + (car const)).
                         ((taylorlast car u) + (car const))).
                  for each j in taylorlist car u collect multconst j)
     end
   else if 'return0 eq taylorfunction car u
     then begin
       scalar const;
       const:=car taylorlist cadr u;
       % the actual constant:deg.sq.
       u:=car u;
       return (taylordefn u).
                   ((((taylorfirst u)+car const).
                         ((taylorlast u)+car const)).
                   for each j in taylorlist u collect multconst j)
       end
     else nil;


 symbolic procedure multconst v;
 % Multiplies v by const in deg.sq form.
 ((car v)+(car const)) . !*multsq(cdr v,cdr const);


 put('constanttimes,'tayloropt,'tayloroptctimes);
 put('constanttimes,'simpfn,'simptimes);
 put('constanttimes,'taylorfunction,'taylorctimes);
 put('constanttimes,'initialtaylorfunction,'initialtaylortimes);


 symbolic procedure taylorbtimes(n,texpr,args);
 begin
   scalar answer,n1,n2;
   answer:= nil ./ 1;
   n1:=car firstterm car args;
   % the first term in one argument.
   n2:=car firstterm cadr args;
   % the first term in the other.
   for i:=n1 step 1 until (n-n2) do
     answer:=!*addsq(answer,!*multsq(taylorevaluate(cadr args,n-i),
                                       taylorevaluate(car args,i)));
   return answer
   end;




 put('binarytimes,'taylorfunction,'taylorbtimes);
 put('binarytimes,'initialtaylorfunction,'initialtaylortimes);
 put('binarytimes,'simpfn,'simptimes);


symbolic procedure taylorformertimes arglist;
begin
  scalar const,var,degg,wsqrt,negcount;  % u;
  negcount:=0;
  degg:=0;% the deggrees of any solitary x we may meet.
  const:=nil;
  var:=nil;
  wsqrt:=nil;
  while arglist do <<
    if dependsp(car arglist,taylorvariable)
      then if and(eqcar(car arglist,'expt),
                        cadar arglist eq taylorvariable,
                        numberp caddar arglist)
        then degg:=degg+caddar arglist
% removed JHD 21.8.86 - while it is anoptimisation,
% it runs the risk of proving that -1 = +1 by ignoring the
% number of "i" needed - despite the attempts we went to.
%        else if eqcar(car arglist,'sqrt)
%          then <<
%            u:=argof car arglist;
%            wsqrt := u . wsqrt;
%            if minusq cdr firstterm taylorformp u
%              then negcount:=1+negcount >>
          else if car arglist eq taylorvariable
            then degg:=degg + 1
            else var:=(car arglist).var
      else const:=(car arglist).const;
    arglist:=cdr arglist >>;
  if wsqrt
    then if cdr wsqrt
      then var:=list('sqrt,prepsq simptimes wsqrt).var
      else var:=('sqrt.wsqrt).var;
  if var
    then var:=mapply(function (lambda u,v;
                               list('binarytimes,u,v)),var);
  % insert binary multiplications.
  negcount:=negcount/2;
  if onep cdr divide(negcount,2)
    then const:= (-1).const;
  % we had an odd number of (-1) from i*i.
  if const or (degg neq 0)
    then <<
      if const
        then const:=simptimes const
        else const:=1 ./ 1;
      const:=taylormake(list 'return0,degg.degg,list(degg.const));
      if null var
        then var:=const
        else var:=list('constanttimes,var,const) >>;
  return taylorformp var
  end;

put('times,'taylorformer,'taylorformertimes);




flag('(taylorbtimes taylorctimes taylorquotient),'taylor);
symbolic procedure taylorformerexpt arglist;
begin
  scalar base,expon;
  base:=car arglist;
  expon:=simpcar cdr arglist;
  if (denr expon neq 1) or
     (not numberp numr expon)
    then interr "Hard exponent";
  expon:=numr expon;
  if base neq taylorvariable
    then interr "Hard base";
  return list('return0 . nil,expon.expon,expon.(1 ./ 1))
  end;
put ('expt,'taylorformer,'taylorformerexpt);


symbolic procedure initialtaylorquotient slist;
(caar slist - caadr slist). !*multsq(cdar slist,!*invsq cdadr slist);


symbolic procedure taylorquotient(n,texpr,args);
begin
  % problem is texpr=b/c or c*texpr=b.
  scalar sofar,b,c,cfirst;
  b:=car args;
  c:=cadr args;
  cfirst:=taylorfirst c;
  sofar:=taylorevaluate(b,n+cfirst);
  for i:=taylorfirst texpr step 1 until n-1 do
    sofar:=!*addsq(sofar,!*multsq(taylorevaluate(texpr,i),
                              negsq taylorevaluate(c,n+cfirst-i)));
  return !*multsq(sofar,!*invsq taylorevaluate(c,cfirst))
  end;


put('quotient,'taylorfunction,'taylorquotient);
put('quotient,'initialtaylorfunction,'initialtaylorquotient);


% symbolic procedure minusq sq;
%    if null sq then nil
%     else if minusf numr sq then not minusf denr sq
%     else minusf denr sq;

% This is wrapped round TAYLORFORMERSQRT2 in order to
% remove the innards of the SQRT from the asslist.
% note the precautions for nested SQRTs.

symbolic procedure taylorformersqrt arglist;
% ***store-hack-1***;
% Uncomment these lines if more store is available.
%begin
%  scalar z;
%  z:=taylorasslist;
%  if sqrtsintree(car arglist,taylorvariable)
%    then return taylorformersqrt2 arglist;
%  arglist:=taylorformersqrt2 arglist;
%  taylorasslist:=z;
%  return arglist
%  end;
%
%
%symbolic procedure taylorformersqrt2 arglist;
begin
  scalar f,realargs,ff,realsqrt;
  realargs:=taylorformp carx(arglist,'taylorformersqrt2);
  f:=firstterm realargs;
  if not evenp car f
    then interr "Extra sqrt substitution needed";
  if and(0 iequal car f,
         1 iequal numr cdr f,
         1 iequal denr cdr f)
    then return taylorformp list('sqrtx,realargs);
  % if it starts with 1 already then it is easy.
  ff:=- car f;
  ff:=list(list 'return0,ff.ff,ff.(!*invsq cdr f));
  % ff is the leading term in the expansion of realargs.
  realsqrt:=list('sqrtx,list('constanttimes,realargs,ff));
  ff:=(car f)/2;
  return taylorformp list('constanttimes,
                          realsqrt,
                          list(list 'return0,
                               ff.ff,
                               ff.(simpsqrtsq cdr f)))
  end;


put('sqrt,'taylorformer,'taylorformersqrt);


symbolic procedure initialtaylorsqrtx slist;
0 . (1 ./ 1);
% sqrt(1+ ...) = 1+....


symbolic procedure taylorsqrtx(n,texpr,args);
begin scalar sofar;
  sofar:=taylorevaluate(car args,n);
  % (1+.....+a(n)*x**n)**2
  % = ....+x**n*(2*a(n)+sum(0<i<n,a(i)*a(n-i))).
  % So a(n)=(coeff(x**n)-sum) /2.
  for i:=1 step 1 until (n-1) do
    sofar:=!*addsq(sofar,negsq !*multsq(taylorevaluate(texpr,i),
                                    taylorevaluate(texpr,n-i)));
  return multsq(sofar,1 ./ 2)
  end;


flag('(taylorsqrtx),'taylor);
put('sqrtx,'taylorfunction,'taylorsqrtx);
put('sqrtx,'initialtaylorfunction,'initialtaylorsqrtx);

endmodule;

end;
