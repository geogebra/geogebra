module trcase;  % Driving routine for integration of transcendental fns.

% Authors: Mary Ann Moore and Arthur C. Norman.
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


fluid '(!*backtrace
        !*failhard
        !*nowarnings
        !*purerisch
        !*reverse
        !*trint
        badpart
        ccount
        cmap
        cmatrix
        content
        cval
        denbad
        denominator!*
        indexlist
        lhs!*
        loglist
        lorder
        orderofelim
        power!-list!*
        rhs!*
        sillieslist
        sqfr
        sqrtflag
        sqrtlist
        tanlist
        svar
        varlist
        xlogs
        zlist);

% !*reverse:       flag to re-order zlist.
% !*nowarnings:    flag to lose messages.

global '(!*number!*
         !*ratintspecial
         !*seplogs
         !*spsize!*
         !*statistics
         gensymcount);

switch failhard;

exports transcendentalcase;

imports backsubst4cs,countz,createcmap,createindices,df2q,dfnumr,
  difflogs,fsdf,factorlistlist,findsqrts,findtrialdivs,gcdf,mkvect,
  interr,logstosq,mergin,multbyarbpowers,!*multf, % multsqfree,
  printdf,printsq,quotf,rationalintegrate,putv,
  simpint1,solve!-for!-u,sqfree,sqmerge,sqrt2top,substinulist,trialdiv,
  mergein,negsq,addsq,f2df,mknill,pnth,invsq,multsq,domainp,mk!*sq,
  mksp,prettyprint;

% Note that SEPLOGS keeps logarithmic part of result together as a
% kernel form, but this can lead to quite messy results.

symbolic
   procedure transcendentalcase(integrand,svar,xlogs,zlist,varlist);
   begin scalar divlist,jhd!-content,content,prim,sqfr,dfu,indexlist,
%      JHD!-CONTENT is local, while CONTENT is free (set in SQFREE).
      sillieslist,originalorder,wrongway,power!-list!*,
      sqrtlist,tanlist,loglist,dflogs,eprim,dfun,unintegrand,
      sqrtflag,badpart,rhs!*,lhs!*,gcdq,cmap,cval,orderofelim,cmatrix;
      scalar ccount,denominator!*,result,denbad,temp;
        gensymcount:=0;
      integrand:=sqrt2top integrand; % Move the sqrts to the numerator.
      if !*trint then << printc "Extension variables z<i> are";
          print zlist>>;
%     if !*ratintspecial and null cdr zlist then
%           return rationalintegrate(integrand,svar);
     % *** now unnormalize integrand, maybe ***.
     begin scalar w,gg;
        gg:=1;
        foreach z in zlist do
           <<w := subs2 diffsq(simp z,svar);   % subs2q?
             gg := !*multf(gg,quotf(denr w,gcdf(denr w,gg)))>>;
        gg := quotf(gg,gcdf(gg,denr integrand));
        unintegrand := (!*multf(gg,numr integrand)
                        ./ !*multf(gg,denr integrand));  % multf?
        if !*trint then <<
                printc "After unnormalization the integrand is ";
                printsq unintegrand >>
      end;
      divlist := findtrialdivs zlist;
         % Also puts some things on loglist sometimes.
      sqrtlist := findsqrts zlist;
      divlist := trialdiv(denr unintegrand,divlist);
      % N.B. the next line also sets 'content' as a free variable.
      % Since SQFREE may be used later, we copy it into JHD!-CONTENT.
      prim := sqfree(cdr divlist,zlist);
      jhd!-content := content;
      printfactors(prim,nil);
      eprim := sqmerge(countz car divlist,prim,nil);
      printfactors(eprim,t);
%     if !*trint then <<terpri();
%         printsf denominator!*;
%         terpri();
%         printc "...content is:";
%         printsf jhd!-content>>;
      sqfr := for each u in eprim collect multup u;
%      sqfr := multsqfree eprim;
%     if !*trint then <<printc "...sqfr is:";
%         prettyprint sqfr>>;
      if !*reverse then zlist := reverse zlist; % Alter order function.
      indexlist := createindices zlist;
%     if !*trint then << printc "...indices are:";
%         prettyprint indexlist>>;
      dfu:=dfnumr(svar,car divlist);
%     if !*trint then << terpri();
%         printc "************ Derivative of u is:";
%         printsq dfu>>;
      loglist := append(loglist,factorlistlist prim); %%% nconc?
      loglist := mergein(xlogs,loglist);
      loglist := mergein(tanlist,loglist);
      cmap := createcmap();
      ccount := length cmap;
      if !*trint then <<printc "Loglist "; print loglist>>;
      dflogs := difflogs(loglist,denr unintegrand,svar);
      if !*trint
        then <<printc "************ 'Derivative' of logs is:";
               printsq dflogs>>;
      dflogs := addsq((numr unintegrand) ./ 1,negsq dflogs);
      % Put everything in reduction eqn over common denominator.
      gcdq := gcdf(denr dflogs,denr dfu);
      dfun := !*multf(numr dfu,denbad:=quotf(denr dflogs,gcdq));
      denbad := !*multf(denr dfu,denbad);
      denbad := !*multf(denr unintegrand,denbad);
      dflogs := !*multf(numr dflogs,quotf(denr dfu,gcdq));
      dfu := dfun;
      % Now DFU and DFLOGS are S.F.s.
      rhs!* := multbyarbpowers f2df dfu;
      if checkdffail(rhs!*,svar)
        then <<if !*trint then printsq checkdffail(rhs!*,svar);
      interr "Simplification fails on above expression">>;
      if !*trint then <<
          printc "Distributed Form of Numerator is:";
          printdf rhs!*>>;
      lhs!* := f2df dflogs;
%     if checkdffail(lhs!*,svar) then interr "Simplification failure";
      if !*trint then << printc "Distributed Form of integrand is:";
          printdf lhs!*;
          terpri()>>;
      cval := mkvect(ccount);
      for i := 0:ccount do putv(cval,i,nil ./ 1);
      power!-list!* := tansfrom(rhs!*,zlist,indexlist,0);
      lorder:=maxorder(power!-list!*,zlist,0);
      originalorder := for each x in lorder collect x;
           % Must copy as it is overwritten.
        if !*trint then <<
                printc "Maximum order for variables determined as ";
                print lorder >>;
        if !*statistics then << !*number!*:=0;
                !*spsize!*:=1;
                foreach xx in lorder do
                   !*spsize!*:=!*spsize!* * (xx+1) >>;
                % That calculates the largest U that can appear.
      dfun:=solve!-for!-u(rhs!*,lhs!*,nil);
      backsubst4cs(nil,orderofelim,cmatrix);
%      if !*trint then if ccount neq 0 then printvecsq cval;
        if !*statistics then << prin2 !*number!*; prin2 " used out of ";
                printc !*spsize!* >>;
      badpart:=substinulist badpart;
                 %substitute for c<i> still in badpart.
      dfun:=df2q substinulist dfun;
      result:= !*multsq(dfun,!*invsq(denominator!* ./ 1));
      result:= !*multsq(result,!*invsq(jhd!-content ./ 1));
      dflogs:=logstosq();
      if not null numr dflogs then <<
          if !*seplogs and (not domainp numr result) then <<
              result:=mk!*sq result;
              result:=(mksp(result,1) .* 1) .+ nil;
              result:=result ./ 1 >>;
          result:=addsq(result,dflogs)>>;
      if !*trint
        then  << %% prettyprint result;
          terpri();
          printc
          "*****************************************************";
          printc
           "************ THE INTEGRAL IS : **********************";
          printc
           "*****************************************************";
          terpri();
          printsq result;
          terpri()>>;
      if badpart then begin scalar n,oorder;
          if !*trint
            then printc "plus a part which has not been integrated";
          lhs!*:=badpart;
          lorder:=maxorder(power!-list!*,zlist,0);
          oorder:=originalorder;
          n:=length lorder;
          while lorder do <<
                if car lorder > car originalorder then
                        wrongway:=t;
                if car lorder=car originalorder then n:= n-1;
                lorder:=cdr lorder;
                originalorder:=cdr originalorder >>;
%%          if n=0 then wrongway:=t;      % Nothing changed
          if !*trint and wrongway then printc "Went wrong way";
          dfun:=df2q badpart;
%%          if !*trint
%%            then <<printsq dfun; printc "Denbad = "; printsf denbad>>;
          dfun:= !*multsq(dfun,invsq(denbad ./ 1));
          badpart := dfun;      %%% *****
          if wrongway then <<
                if !*trint then printc "Resetting....";
                result:=nil ./ 1;
                dfun := integrand; badpart:=dfun >>;
          if rootcheckp(unintegrand,svar) then
                return simpint1(integrand . svar.nil) . (nil ./ 1)
          else if !*purerisch or allowedfns zlist then
              << badpart := dfun;
                 dfun := nil ./ 1 >>    % JPff
           else << !*purerisch:=t;
                if !*trint
                  then <<printc "   Applying transformations ...";
                         printsq dfun>>;
              % We do not want any rules for tan at this point. In fact,
              % this may not be enough.
              temp := get('tan,'opmtch);
              remprop('tan,'opmtch);
              denbad:=transform(dfun,svar);
              if denbad=dfun
                then <<dfun:=nil ./ 1;
                       badpart:=denbad;
                       put('tan,'opmtch,temp)>>
             else <<denbad:=errorset!*(list('integratesq,mkquote denbad,
                                      mkquote svar,mkquote xlogs, nil),
                                      !*backtrace);
                    put('tan,'opmtch,temp);
           %%% JPF fix for distributive version
                     if not atom denbad then <<
                        denbad:=car denbad;     % As from an errorset
                        dfun:=untan car denbad;

                        if (dfun neq '(nil . 1)) then
                            badpart:=untan cdr denbad;
           % There is still a chance that we went the wrong way.
           % Decide that it is better if there is no bad part
                        if car badpart and not(badpart=denbad) then <<
                          wrongway:=nil;
                          lhs!*:=f2df car badpart;
                          lorder:=maxorder(power!-list!*,zlist,0);
                          n:=length lorder;
                          while lorder do <<
                                if car lorder > car oorder then
                                        wrongway:=t;
                                if car lorder=car oorder then n:= n-1;
                                lorder:=cdr lorder;
                                oorder:=cdr oorder >>;
                          if wrongway or (n=0) then <<
                               if !*trint then printc "Still backwards";
                               dfun := nil ./ 1;
                               badpart := integrand>>>>>>
                     else <<badpart := dfun; dfun := nil ./ 1 >>>>>>;
          if !*failhard then rerror(int,9,"FAILHARD switch set");
          if !*seplogs and not domainp result then <<
                result:=mk!*sq result;
                if not numberp result
                  then result:=(mksp(result,1) .* 1) .+ nil;
                result:=result ./ 1>>;
          result:=addsq(result,dfun) end
         else badpart:=nil ./ 1;
      return (sqrt2top result . badpart)                % JPff
   end;

symbolic procedure checkdffail(u,v);
   % Sometimes simplification fails and this gives the integrator the
   % idea that something is a constant when it is not.  Check for this.
   if null u then nil
    else if depends(lc u,v) then lc u
    else checkdffail(red u,v);

symbolic procedure printfactors(w,prdenom);
    % W is a list of factors to each power. If PRDENOM is true
    % this prints denominator of answer, else prints square-free
    % decomposition.
    begin         scalar i,wx;
        i:=1;
        if prdenom then <<
            denominator!* := 1;
            if !*trint
              then printc "Denominator of 1st part of answer is:";
            if not null w then w:=cdr w >>;
loopx:  if w=nil then return;
        if !*trint then <<prin2 "Factors of multiplicity ";
                          prin2 i;
                          prin2t ":";
                          terpri()>>;
        wx:=car w;
        while not null wx do <<
            if !*trint then printsf car wx;
            for j:=1 : i do
                denominator!*:= !*multf(car wx,denominator!*);
            wx:=cdr wx >>;
        i:=i+1;
        w:=cdr w;
        go to loopx
    end;

% unfluid '(dfun svar xlogs);

endmodule;

end;
