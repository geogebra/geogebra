module multihen;   %  Hensel construction for the multivariate case.
                   %  (This version is highly recursive.)

% Authors: A. C. Norman and P. M. A. Moore, 1979.

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


fluid '(!*overshoot
        !*trfac
        alphavec
        bad!-case
        factor!-level
        factor!-trace!-list
        fhatvec
        hensel!-growth!-size
        max!-unknowns
        number!-of!-factors
        number!-of!-unknowns
        predictions);


symbolic procedure find!-multivariate!-factors!-mod!-p(poly,
    best!-factors,variable!-set);
    % All arithmetic is done mod p, best-factors is overwritten.
    if null variable!-set then best!-factors
    else (lambda factor!-level; begin
    scalar growth!-factor,b0s,res,v,
           bhat0s,w,degbd,first!-time,redpoly,
           predicted!-forms,number!-of!-unknowns,solve!-count,
           correction!-vectors,soln!-matrices,max!-unknowns,
           unknowns!-count!-list,poly!-remaining,
           prediction!-results,one!-prediction!-failed;
    v:=car variable!-set;
    degbd:=get!-degree!-bound car v;
    first!-time:=t;
    growth!-factor:=make!-growth!-factor v;
    poly!-remaining:=poly;
    prediction!-results:=mkvect number!-of!-factors;
    find!-msg1(best!-factors,growth!-factor,poly);
    b0s:=reduce!-vec!-by!-one!-var!-mod!-p(best!-factors,
                    v,number!-of!-factors);
            % The above made a copy of the vector.
    for i:=1:number!-of!-factors do
      putv(best!-factors,i,
        difference!-mod!-p(getv(best!-factors,i),getv(b0s,i)));
    redpoly:=evaluate!-mod!-p(poly,car v,cdr v);
    find!-msg2(v,variable!-set);
    find!-multivariate!-factors!-mod!-p(redpoly,b0s,cdr variable!-set);
            % answers in b0s.
    if bad!-case then return;
    for i:=1:number!-of!-factors do
      putv(best!-factors,i,
        plus!-mod!-p(getv(b0s,i),getv(best!-factors,i)));
    find!-msg3(best!-factors,v);
    res:=diff!-over!-k!-mod!-p(
        difference!-mod!-p(poly,
          times!-vector!-mod!-p(best!-factors,number!-of!-factors)),
        1,car v);
            % RES is the residue and must eventually be reduced to zero.
    factor!-trace << printsf res; terpri!*(nil) >>;
    if not polyzerop res and
      cdr variable!-set and not zerop cdr v then <<
      predicted!-forms:=make!-bivariate!-vec!-mod!-p(best!-factors,
        cdr variable!-set,car v,number!-of!-factors);
      find!-multivariate!-factors!-mod!-p(
        make!-bivariate!-mod!-p(poly,cdr variable!-set,car v),
        predicted!-forms,list v);
            % Answers in PREDICTED!-FORMS.
      find!-msg4(predicted!-forms,v);
      make!-predicted!-forms(predicted!-forms,car v);
            % Sets max!-unknowns and number!-of!-unknowns.
      find!-msg5();
      unknowns!-count!-list:=number!-of!-unknowns;
      while unknowns!-count!-list and
         (car (w:=car unknowns!-count!-list))=1 do
        begin scalar i,r;
          unknowns!-count!-list:=cdr unknowns!-count!-list;
          i:=cdr w;
          w:=quotient!-mod!-p(poly!-remaining,r:=getv(best!-factors,i));
          if didntgo w or
            not polyzerop difference!-mod!-p(poly!-remaining,
            times!-mod!-p(w,r)) then
            if one!-prediction!-failed then <<
              factor!-trace printstr "Predictions are no good";
              max!-unknowns:=nil >>
            else <<
              factor!-trace <<
                prin2!* "Guess for f(";
                prin2!* i;
                printstr ") was bad." >>;
              one!-prediction!-failed:=i >>
          else <<
            putv(prediction!-results,i,r);
            factor!-trace <<
              prin2!* "Prediction for f("; prin2!* i;
              prin2!* ") worked: ";
              printsf r >>;
            poly!-remaining:=w >>
        end;
      w:=length unknowns!-count!-list;
      if w=1 and not one!-prediction!-failed then <<
        putv(best!-factors,cdar unknowns!-count!-list,poly!-remaining);
        go to exit >>
      else if w=0 and one!-prediction!-failed then <<
        putv(best!-factors,one!-prediction!-failed,poly!-remaining);
        go to exit >>;
      solve!-count:=1;
      if max!-unknowns then
        correction!-vectors:=
           make!-correction!-vectors(best!-factors,max!-unknowns) >>;
    bhat0s:=make!-multivariate!-hatvec!-mod!-p(b0s,number!-of!-factors);
    return multihen1(list(res,
                             growth!-factor,
                             first!-time,
                             bhat0s,
                             b0s,
                             variable!-set,
                             solve!-count,
                             correction!-vectors,
                             unknowns!-count!-list,
                             best!-factors,
                             v,
                             degbd,
                             soln!-matrices,
                             predicted!-forms,
                             poly!-remaining,
                             prediction!-results,
                             one!-prediction!-failed),
                             nil);
exit:
      multihen!-exit(first!-time,best!-factors,nil);
  end) (factor!-level+1);

symbolic procedure multihen1(u,zz);
   begin scalar res,test!-prediction,growth!-factor,first!-time,hat0s,
            x0s,variable!-set,solve!-count,correction!-vectors,
            unknowns!-count!-list,correction!-factor,frvec,v,
            degbd,soln!-matrices,predicted!-forms,poly!-remaining,
            fvec,previous!-prediction!-holds,
            prediction!-results,one!-prediction!-failed,
            bool,d,x1,k,kk,substres,w;
      res := car u; u := cdr u;
      growth!-factor := car u; u := cdr u;
      first!-time := car u; u := cdr u;
      hat0s := car u; u := cdr u;
      x0s := car u; u := cdr u;
      variable!-set := car u; u := cdr u;
      solve!-count := car u; u := cdr u;
      correction!-vectors := car u; u := cdr u;
      unknowns!-count!-list := car u; u := cdr u;
      frvec := car u; u := cdr u;
      v := car u; u := cdr u;
      degbd := car u; u := cdr u;
      soln!-matrices := car u; u := cdr u;
      predicted!-forms := car u; u := cdr u;
      poly!-remaining := car u; u := cdr u;
      prediction!-results := car u; u := cdr u;
      if zz then <<fvec := car u; u := cdr u;
                   previous!-prediction!-holds := car u; u := cdr u>>;
      one!-prediction!-failed := car u;
      correction!-factor:=growth!-factor;
            % Next power of growth-factor we are adding to the factors.
      x1:=mkvect number!-of!-factors;
      k:=1;
      kk:=0;
temploop:
    bool := nil;
    while not bool and not polyzerop res and (null max!-unknowns
                      or null test!-prediction) do
      if k>degbd then <<
        factor!-trace <<
          prin2!* "We have overshot the degree bound for ";
          printvar car v >>;
        if !*overshoot then
          prin2t "Multivariate degree bound overshoot -> restart";
        bad!-case:= bool := t >>
      else
        if polyzerop(substres:=evaluate!-mod!-p(res,car v,cdr v))
         then <<
          k:=iadd1 k;
          res:=diff!-over!-k!-mod!-p(res,k,car v);
          correction!-factor:=
            times!-mod!-p(correction!-factor,growth!-factor) >>
      else begin
        multihen!-msg(growth!-factor,first!-time,k,kk,substres,zz);
        if null zz
          then <<kk := kk#+1; if first!-time then first!-time := nil>>;
        solve!-for!-corrections(substres,hat0s,x0s,x1,
                                cdr variable!-set);
            % Answers left in x1.
        if bad!-case then return (bool := t);
        if max!-unknowns then <<
          solve!-count:=iadd1 solve!-count;
          for i:=1:number!-of!-factors do
            putv(getv(correction!-vectors,i),solve!-count,getv(x1,i));
          if solve!-count=caar unknowns!-count!-list then
            test!-prediction:=t >>;
        if zz then
           for i:=1:number!-of!-factors do
             putv(frvec,i,plus!-mod!-p(getv(frvec,i),times!-mod!-p(
                  getv(x1,i),correction!-factor)));
        factor!-trace <<
          printstr "   Giving:";
          if null zz then
          printvec("     f(",number!-of!-factors,",1) = ",x1)
           else <<
          printvec("     a(",number!-of!-factors,",1) = ",x1);
          printstr "   New a's are now:";
          printvec("     a(",number!-of!-factors,") = ",frvec) >>>>;
         d:=times!-mod!-p(correction!-factor,
              if zz then form!-sum!-and!-product!-mod!-p(x1,fhatvec,
                number!-of!-factors)
               else terms!-done!-mod!-p(frvec,x1,correction!-factor));
        if degree!-in!-variable(d,car v)>degbd then <<
          factor!-trace <<
            prin2!* "We have overshot the degree bound for ";
            printvar car v >>;
          if !*overshoot then
            prin2t "Multivariate degree bound overshoot -> restart";
          bad!-case:=t;
          return (bool := t)>>;
        d:=diff!-k!-times!-mod!-p(d,k,car v);
        if null zz then
           for i:=1:number!-of!-factors do
             putv(frvec,i,
               plus!-mod!-p(getv(frvec,i),
                 times!-mod!-p(getv(x1,i),correction!-factor)));
        k:=iadd1 k;
        res:=diff!-over!-k!-mod!-p(difference!-mod!-p(res,d),k,car v);
        factor!-trace <<
           if null zz then <<printstr "   New factors are now:";
                printvec("     f(",number!-of!-factors,") = ",frvec)>>;
          prin2!* "   and residue = ";
          printsf res;
          printstr "-------------"
        >>;
        correction!-factor:=
          times!-mod!-p(correction!-factor,growth!-factor) end;
    if not polyzerop res and not bad!-case then <<
      if null zz or null soln!-matrices then
        soln!-matrices
           := construct!-soln!-matrices(predicted!-forms,cdr v);
      factor!-trace <<
        if null zz then <<
        printstr "We use the results from the Hensel growth to";
        printstr "produce a set of linear equations to solve";
        printstr "for coefficients in the relevant factors:" >>
         else <<
        printstr "The Hensel growth so far allows us to test some of";
        printstr "our predictions:" >>>>;
      bool := nil;
      while not bool and unknowns!-count!-list and
        (car (w:=car unknowns!-count!-list))=solve!-count do <<
        unknowns!-count!-list:=cdr unknowns!-count!-list;
        factor!-trace
          print!-linear!-system(cdr w,soln!-matrices,
            correction!-vectors,predicted!-forms,car v);
        w:=try!-prediction(soln!-matrices,correction!-vectors,
          predicted!-forms,car w,cdr w,poly!-remaining,car v,
          if zz then fvec else nil,
          if zz then fhatvec else nil);
        if car w='singular or car w='bad!-prediction then
          if one!-prediction!-failed then <<
            factor!-trace printstr "Predictions were no help.";
            max!-unknowns:=nil;
            bool := t>>
          else if null zz then one!-prediction!-failed:=cdr w
          else <<
            if previous!-prediction!-holds then <<
              predictions:=delasc(car v,predictions);
              previous!-prediction!-holds:=nil >>;
            one!-prediction!-failed:=cdr w >>
        else <<
          putv(prediction!-results,car w,cadr w);
          poly!-remaining:=caddr w >> >>;
      if null max!-unknowns then <<
        if zz and previous!-prediction!-holds then
          predictions:=delasc(car v,predictions);
        goto temploop >>;
      w:=length unknowns!-count!-list;
      if w>1 or (w=1 and one!-prediction!-failed) then <<
        test!-prediction:=nil;
        goto temploop >>;
      if w=1 or one!-prediction!-failed then <<
        w:=if one!-prediction!-failed then one!-prediction!-failed
           else cdar unknowns!-count!-list;
        putv(prediction!-results,w,
             if null zz then poly!-remaining
              else quotfail!-mod!-p(poly!-remaining,
                                    getv(fhatvec,w)))>>;
      for i:=1:number!-of!-factors do
          putv(frvec,i,getv(prediction!-results,i));
      if (not previous!-prediction!-holds or null zz)
         and not one!-prediction!-failed then
        predictions:=
          (car v .
            list(soln!-matrices,predicted!-forms,max!-unknowns,
              number!-of!-unknowns))
          . predictions >>;
      multihen!-exit(first!-time,frvec,zz)
   end;

symbolic
   procedure multihen!-msg(growth!-factor,first!-time,k,kk,substres,zz);
        factor!-trace <<
          prin2!* "Hensel Step "; printstr (kk:=kk #+ 1);
          prin2!* "-------------";
          if kk>10 then printstr "-" else terpri!*(t);
          prin2!* "Next corrections are for (";
          prinsf growth!-factor;
          if not (k=1) then <<
            prin2!* ") ** ";
            prin2!* k >> else prin2!* '!);
          printstr ". To find these we solve:";
          if zz then prin2!* "     sum over i [ a(i,1)*fhat(i,0) ] = "
           else prin2!* "     sum over i [ f(i,1)*fhat(i,0) ] = ";
          prinsf substres;
          prin2!* " mod ";
          prin2!* hensel!-growth!-size;
          if zz then printstr " for a(i,1). "
           else printstr " for f(i,1), ";
          if null zz and first!-time then <<
            prin2!*
               "       where fhat(i,0) = product over j [ f(j,0) ]";
            prin2!* " / f(i,0) mod ";
            printstr hensel!-growth!-size >>;
          terpri!*(nil)
        >>;

symbolic procedure multihen!-exit(first!-time,frvec,zz);
      factor!-trace <<
      if not bad!-case then
        if first!-time then
          if zz then printstr "But these a's are already correct."
           else printstr "Therefore these factors are already correct."
        else <<
          if zz then <<printstr "Correct a's are:";
                     printvec("  a(",number!-of!-factors,") = ",frvec)>>
           else <<printstr "Correct factors are:";
                 printvec("  f(",number!-of!-factors,") = ",frvec) >>>>;
      terpri!*(nil);
      printstr "**************************************************";
      terpri!*(nil)>>;

symbolic procedure find!-msg1(best!-factors,growth!-factor,poly);
    factor!-trace <<
      printstr "Want f(i) s.t.";
      prin2!* "  product over i [ f(i) ] = ";
      prinsf poly;
      prin2!* " mod ";
      printstr hensel!-growth!-size;
      terpri!*(nil);
      printstr "We know f(i) as follows:";
      printvec("  f(",number!-of!-factors,") = ",best!-factors);
      prin2!* " and we shall put in powers of ";
      prinsf growth!-factor;
      printstr " to find them fully."
    >>;

symbolic procedure find!-msg2(v,variable!-set);
    factor!-trace <<
      prin2!*
         "First solve the problem in one less variable by putting ";
      prinvar car v; prin2!* "="; printstr cdr v;
      if cdr variable!-set then <<
        prin2!* "and growing wrt ";
        printvar caadr variable!-set
        >>;
      terpri!*(nil)
    >>;

symbolic procedure find!-msg3(best!-factors,v);
    factor!-trace <<
      prin2!* "After putting back any knowledge of ";
      prinvar car v;
      printstr ", we have the";
      printstr "factors so far as:";
      printvec("  f(",number!-of!-factors,") = ",best!-factors);
      printstr "Subtracting the product of these from the polynomial";
      prin2!* "and differentiating wrt "; prinvar car v;
      printstr " gives a residue:"
    >>;

symbolic procedure find!-msg4(predicted!-forms,v);
      factor!-trace <<
        printstr "To help reduce the number of Hensel steps we try";
        prin2!* "predicting how many terms each factor will have wrt ";
        prinvar car v; printstr ".";
        printstr
          "Predictions are based on the bivariate factors :";
        printvec("     f(",number!-of!-factors,") = ",predicted!-forms)
        >>;

symbolic procedure find!-msg5;
      factor!-trace <<
        terpri!*(nil);
        printstr "We predict :";
        for each w in number!-of!-unknowns do <<
          prin2!* car w;
          prin2!* " terms in f("; prin2!* cdr w; printstr '!) >>;
        if (caar number!-of!-unknowns)=1 then <<
          prin2!* "Since we predict only one term for f(";
          prin2!* cdar number!-of!-unknowns;
          printstr "), we can try";
          printstr "dividing it out now:" >>
        else <<
          prin2!* "So we shall do at least ";
          prin2!* isub1 caar number!-of!-unknowns;
          prin2!* " Hensel step";
          if (caar number!-of!-unknowns)=2 then printstr "."
          else printstr "s." >>;
        terpri!*(nil) >>;

symbolic procedure solve!-for!-corrections(c,fhatvec,fvec,resvec,vset);
% ....;
  if null vset then
    for i:=1:number!-of!-factors do
      putv(resvec,i,
        remainder!-mod!-p(
          times!-mod!-p(c,getv(alphavec,i)),
          getv(fvec,i)))
  else (lambda factor!-level; begin
    scalar residue,growth!-factor,f0s,fhat0s,v,
      degbd,first!-time,redc,
      predicted!-forms,max!-unknowns,solve!-count,number!-of!-unknowns,
      correction!-vectors,soln!-matrices,w,previous!-prediction!-holds,
      unknowns!-count!-list,poly!-remaining,
      prediction!-results,one!-prediction!-failed;
    v:=car vset;
    degbd:=get!-degree!-bound car v;
    first!-time:=t;
    growth!-factor:=make!-growth!-factor v;
    poly!-remaining:=c;
    prediction!-results:=mkvect number!-of!-factors;
    redc:=evaluate!-mod!-p(c,car v,cdr v);
    solve!-msg1(c,fvec,v);
    solve!-for!-corrections(redc,
      fhat0s:=reduce!-vec!-by!-one!-var!-mod!-p(
        fhatvec,v,number!-of!-factors),
      f0s:=reduce!-vec!-by!-one!-var!-mod!-p(
        fvec,v,number!-of!-factors),
      resvec,
      cdr vset); % Results left in RESVEC.
    if bad!-case then return;
    solve!-msg2(resvec,v);
    residue:=diff!-over!-k!-mod!-p(difference!-mod!-p(c,
          form!-sum!-and!-product!-mod!-p(resvec,fhatvec,
            number!-of!-factors)),1,car v);
    factor!-trace <<
      printsf residue;
      prin2!* " Now we shall put in the powers of ";
      prinsf growth!-factor;
      printstr " to find the a's fully."
    >>;
    if not polyzerop residue and not zerop cdr v then <<
      w:=atsoc(car v,predictions);
      if w then <<
        previous!-prediction!-holds:=t;
        factor!-trace <<
          printstr
             "We shall use the previous prediction for the form of";
          prin2!* "polynomials wrt "; printvar car v >>;
        w:=cdr w;
        soln!-matrices:=car w;
        predicted!-forms:=cadr w;
        max!-unknowns:=caddr w;
        number!-of!-unknowns:=cadr cddr w >>
      else <<
        factor!-trace <<
     printstr
        "We shall use a new prediction for the form of polynomials ";
        prin2!* "wrt "; printvar car v >>;
        predicted!-forms:=mkvect number!-of!-factors;
        for i:=1:number!-of!-factors do
          putv(predicted!-forms,i,getv(fvec,i));
            % Make a copy of the factors in a vector we shall overwrite.
        make!-predicted!-forms(predicted!-forms,car v);
            % Sets max!-unknowns and number!-of!-unknowns.
        >>;
      solve!-msg3();
      unknowns!-count!-list:=number!-of!-unknowns;
      while unknowns!-count!-list and
         (car (w:=car unknowns!-count!-list))=1 do
        begin scalar i,r,wr,fi;
          unknowns!-count!-list:=cdr unknowns!-count!-list;
          i:=cdr w;
          w:=quotient!-mod!-p(
            wr:=difference!-mod!-p(poly!-remaining,
              times!-mod!-p(r:=getv(resvec,i),getv(fhatvec,i))),
            fi:=getv(fvec,i));
          if didntgo w or not polyzerop
            difference!-mod!-p(wr,times!-mod!-p(w,fi)) then
            if one!-prediction!-failed then <<
              factor!-trace printstr "Predictions are no good.";
              max!-unknowns:=nil >>
            else <<
              factor!-trace <<
                prin2!* "Guess for a(";
                prin2!* i;
                printstr ") was bad." >>;
              one!-prediction!-failed:=i >>
          else <<
            putv(prediction!-results,i,r);
            factor!-trace <<
              prin2!* "Prediction for a("; prin2!* i;
              prin2!* ") worked: ";
              printsf r >>;
            poly!-remaining:=wr >>
        end;
      w:=length unknowns!-count!-list;
      if w=1 and not one!-prediction!-failed then <<
        putv(resvec,cdar unknowns!-count!-list,
          quotfail!-mod!-p(poly!-remaining,getv(fhatvec,
            cdar unknowns!-count!-list)));
        go to exit >>
      else if w=0 and one!-prediction!-failed and max!-unknowns then <<
        putv(resvec,one!-prediction!-failed,
          quotfail!-mod!-p(poly!-remaining,getv(fhatvec,
            one!-prediction!-failed)));
        go to exit >>;
      solve!-count:=1;
      if max!-unknowns then
        correction!-vectors:=
           make!-correction!-vectors(resvec,max!-unknowns) >>;
    if not polyzerop residue then first!-time:=nil;
    return multihen1(list(residue,
                            growth!-factor,
                            first!-time,
                            fhat0s,
                            f0s,
                            vset,
                            solve!-count,
                            correction!-vectors,
                            unknowns!-count!-list,
                            resvec,
                            v,
                            degbd,
                            soln!-matrices,
                            predicted!-forms,
                            poly!-remaining,
                            prediction!-results,
                            fvec,
                            previous!-prediction!-holds,
                            one!-prediction!-failed),
                            t);
exit:
      multihen!-exit(first!-time,resvec,t);
  end) (factor!-level+1);

symbolic procedure solve!-msg1(c,fvec,v);
    factor!-trace <<
      printstr "Want a(i) s.t.";
      prin2!* "(*)  sum over i [ a(i)*fhat(i) ] = ";
      prinsf c;
      prin2!* " mod ";
      printstr hensel!-growth!-size;
      prin2!* "    where fhat(i) = product over j [ f(j) ]";
      prin2!* " / f(i) mod ";
      printstr hensel!-growth!-size;
      printstr "    and";
      printvec("      f(",number!-of!-factors,") = ",fvec);
      terpri!*(nil);
      prin2!*
         "First solve the problem in one less variable by putting ";
      prinvar car v; prin2!* '!=; printstr cdr v;
      terpri!*(nil)
    >>;

symbolic procedure solve!-msg2(resvec,v);
    factor!-trace <<
      printstr "Giving:";
      printvec("  a(",number!-of!-factors,",0) = ",resvec);
      printstr "Subtracting the contributions these give in (*) from";
      prin2!* "the R.H.S. of (*) ";
      prin2!* "and differentiating wrt "; prinvar car v;
      printstr " gives a residue:"
    >>;

symbolic procedure solve!-msg3;
      factor!-trace <<
        terpri!*(nil);
        printstr "We predict :";
        for each w in number!-of!-unknowns do <<
          prin2!* car w;
          prin2!* " terms in a("; prin2!* cdr w; printstr '!) >>;
        if (caar number!-of!-unknowns)=1 then <<
          prin2!* "Since we predict only one term for a(";
          prin2!* cdar number!-of!-unknowns;
          printstr "), we can test it right away:" >>
        else <<
          prin2!* "So we shall do at least ";
          prin2!* isub1 caar number!-of!-unknowns;
          prin2!* " Hensel step";
          if (caar number!-of!-unknowns)=2 then printstr "."
          else printstr "s." >>;
        terpri!*(nil) >>;

endmodule;

end;
