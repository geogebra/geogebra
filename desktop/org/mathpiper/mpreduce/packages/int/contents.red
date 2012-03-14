module contents;

% Authors: Mary Ann Moore and Arthur C. Norman.

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


fluid '(content indexlist sqfr varlist zlist);   % clogflag

exports contents,contentsmv,dfnumr,difflogs,factorlistlist, % multsqfree
        multup,sqfree,sqmerge;

imports int!-fac,fquotf,gcdf,interr,!*multf,partialdiff,quotf,ordop,
        addf,negf,domainp,difff,mksp,negsq,invsq,addsq,!*multsq,diffsq;


comment we assume no power substitution is necessary in this module;

symbolic procedure contents(p,v);
% Find the contents of the polynomial p wrt variable v;
% Note that v may not be the main variable of p;
    if domainp(p) then p
    else if v=mvar p then contentsmv(p,v,nil)
    else if ordop(v,mvar p) then p
    else contentsmv(makemainvar(p,v),v,nil);

symbolic procedure contentsmv(p,v,sofar);
% Find contents of polynomial P;
% V is main variable of P;
% SOFAR is partial result;
    if sofar=1 then 1
    else if domainp p then gcdf(p,sofar)
    else if not(v=mvar p) then gcdf(p,sofar)
    else contentsmv(red p,v,gcdf(lc p,sofar));

symbolic procedure makemainvar(p,v);
% Bring v up to be the main variable in polynomial p.
% Note that the reconstructed p must be used with care since
% it does not conform to the normal REDUCE ordering rules.
    if domainp p then p
    else if v=mvar p then p
    else mergeadd(mulcoeffsby(makemainvar(lc p,v),lpow p,v),
      makemainvar(red p,v),v);

symbolic procedure mulcoeffsby(p,pow,v);
% Multiply each coefficient in p by the standard power pow;
    if null p then nil
    else if domainp p or not(v=mvar p) then ((pow .* p) .+ nil)
    else (lpow p .* ((pow .* lc p) .+ nil)) .+ mulcoeffsby(red p,pow,v);

symbolic procedure mergeadd(a,b,v);
% Add polynomials a and b given that they have same main variable v;
    if domainp a or not(v=mvar a) then
      if domainp b or not(v=mvar b) then addf(a,b)
      else lt b .+ mergeadd(a,red b,v)
    else if domainp b or not(v=mvar b) then
      lt a .+ mergeadd(red a,b,v)
    else (lambda xc;
      if xc=0 then (lpow a .* addf(lc a,lc b)) .+
            mergeadd(red a,red b,v)
      else if xc>0 then lt a .+ mergeadd(red a,b,v)
      else lt b .+ mergeadd(a,red b,v))
        (tdeg lt a-tdeg lt b);

symbolic procedure sqfree(p,vl);
    if (null vl) or (domainp p) then
        <<content:=p; nil>>
    else begin    scalar w,v,dp,gg,pg,dpg,p1,w1;
        w:=contents(p,car vl); % content of p ;
        p:=quotf(p,w); % make p primitive;
        w:=sqfree(w,cdr vl); % process content by recursion;
        if p=1 then return w;
        v:=car vl; % pick out variable from list;
        while not (p=1) do <<
            dp:=partialdiff(p,v);
            gg:=gcdf(p,dp);
            pg:=quotf(p,gg);
            dpg:=negf partialdiff(pg,v);
            p1:=gcdf(pg,addf(quotf(dp,gg),dpg));
            w1:=p1.w1;
            p:=gg>>;
        return sqmerge(reverse w1,w,t)
        end;

symbolic procedure sqmerge(w1,w,simplew1);
% w and w1 are lists of factors of each power. if simplew1 is true
% then w1 contains only single factors for each power. ;
    if null w1 then w
    else if null w then if car w1=1 then nil.sqmerge(cdr w1,w,simplew1)
          else (if simplew1 then list car w1 else car w1).
sqmerge(cdr w1,w,simplew1)
    else if car w1=1 then (car w).sqmerge(cdr w1,cdr w,simplew1) else
        append(if simplew1 then list car w1 else car w1,car w).
        sqmerge(cdr w1,cdr w,simplew1);

symbolic procedure multup l;
% l is a list of s.f.'s. result is s.f. for product of elements of l;
   begin         scalar res;
      res:=1;
      for each j in l do res := multf(res,j);
%     while not null l do <<
%        res:=multf(res,car l);
%        l:=cdr l >>;
      return res
   end;

symbolic procedure diflist(l,cl,x,rl);
% Differentiates l (list of s.f.'s) wrt x to produce the sum of
% terms for the derivative of numr of 1st part of answer.  cl is
% coefficient list (s.f.'s) & rl is list of derivatives we have
% dealt with so far.  Result is s.q.;
   if null l then nil ./ 1
   else begin    scalar temp;
      temp:=!*multf(multup rl,multup cdr l);
      temp:=!*multsq(difff(car l,x),!*f2q temp);
      temp:=!*multsq(temp,(car cl) ./ 1);
      return addsq(temp,diflist(cdr l,cdr cl,x,(car l).rl))
   end;

%symbolic procedure multsqfree w;
%% W is list of sqfree factors. result is product of each list in w
%% to give one polynomial for each sqfree power.
%   if null w then nil
%    else (multup car w) . multsqfree cdr w;

symbolic procedure l2lsf l;
% L is a list of kernels. result is a list of same members as s.f.'s;
   if null l then nil
   else ((mksp(car l,1) .* 1) .+ nil).l2lsf cdr l;

symbolic procedure dfnumr(x,dl);
% Gives the derivative of the numr of the 1st part of answer.
% dl is list of any exponential or 1+tan**2 that occur in integrand
% denr. these are divided out from result before handing it back.
% result is s.q., ready for printing.
   begin         scalar temp1,temp2,coeflist,qlist,count;
      if not null sqfr then <<
      count:=0;
      qlist:=cdr sqfr;
      coeflist:=nil;
      while not null qlist do <<
         count:=count+1;
         coeflist:=count.coeflist;
         qlist:=cdr qlist >>;
      coeflist:=reverse coeflist >>;
      temp1:=!*multsq(diflist(l2lsf zlist,l2lsf indexlist,x,nil),
                      !*f2q multup sqfr);
      if not null sqfr and not null cdr sqfr then <<
          temp2:=!*multsq(diflist(cdr sqfr,coeflist,x,nil),
                          !*f2q multup l2lsf zlist);
      temp2:=!*multsq(temp2,(car sqfr) ./ 1) >>
      else temp2:=nil ./ 1;
      temp1:=addsq(temp1,negsq temp2);
      temp2:=cdr temp1;
      temp1:=car temp1;
      qlist:=nil;
      while not null dl do <<
         if not(car dl member qlist) then qlist:=(car dl).qlist;
         dl:=cdr dl >>;
      while not null qlist do <<
         temp1:=quotf(temp1,car qlist);
         qlist:=cdr qlist >>;
      return temp1 ./ temp2
   end;

symbolic procedure difflogs(ll,denm1,x);
% LL is list of log terms (with coeffts), den is common denominator
% over which they are to be put.  Result is s.q. for derivative of all
% these wrt x.
   if null ll then nil ./ 1
   else begin    scalar temp,qu,cvar,logoratan,arg;
      logoratan:=caar ll;
      cvar:=cadar ll;
      arg:=cddar ll;
      temp:=!*multsq(cvar ./ 1,diffsq(arg,x));
      if logoratan='iden then qu:=1 ./ 1
        else if logoratan='log then qu:=arg
        else if logoratan='atan then qu:=addsq(1 ./ 1,!*multsq(arg,arg))
        else interr "Logoratan=? in difflogs";
%Note call to special division routine;
      qu:=fquotf(!*multf(!*multf(denm1,numr temp),
                denr qu),numr qu);
                        %*MUST* GO EXACTLY;
     temp:=!*multsq(!*invsq (denr temp ./ 1),qu);
                 %result of fquotf is a s.q;
      return !*addsq(temp,difflogs(cdr ll,denm1,x))
   end;

symbolic procedure factorlistlist w;
% W is list of lists of sqfree factors in s.f.  Result is list of log
% terms required for integral answer. the arguments for each log fn
% are in s.q.
    begin scalar res,x,y;
        while not null w do <<
            x:=car w;
            while not null x do <<
                y:=facbypp(car x,varlist);
                while not null y do <<
                    res:=append(int!-fac car y,res);
                    y:=cdr y >>;
                x:=cdr x >>;
            w:=cdr w >>;
        return res
    end;

symbolic procedure facbypp(p,vl);
% Use contents/primitive parts to try to factor p.
    if null vl then list p
    else begin scalar princilap!-part,co;
        co:=contents(p,car vl);
        vl:=cdr vl;
        if co=1 then return facbypp(p,vl); %this var no help.
        princilap!-part:=quotf(p,co); %primitive part.
        if princilap!-part=1 then return facbypp(p,vl); % again no help
        return nconc(facbypp(princilap!-part,vl),facbypp(co,vl))
    end;

endmodule;

end;
