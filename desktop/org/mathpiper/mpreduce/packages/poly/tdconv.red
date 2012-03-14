module tdconv; % Procedures for conversion of internal & external
               % expressions defined with total degree ordering.

% Authors: Shuichi Moritsugu <y31046@tansei.cc.u-tokyo.ac.jp>
%          and Eiichi Goto.

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

symbolic procedure setunion(l1,l2);
   % Union of two sets.
      if null l2 then l1
        else if member(car l2,l1) then setunion(l1,cdr l2)
        else setunion(append(l1,car l2 . nil),cdr l2);

symbolic procedure searchtm term;
   % Search for variables in a term.
      if domainp term then nil
        else caar term . searchpl cdr term;

symbolic procedure searchpl poly;
   % Search for variables in a polynomial.
      if domainp poly then nil
        else setunion(searchtm car poly,searchpl cdr poly);

symbolic procedure qsort l;
   % Quick sort of variables with lexicographic ordering.
      begin scalar a,l1,l2,ll;
            if null l then return nil;
            a:=car l; ll:=cdr l;
            loop : if null ll then go to exit;
   % We need ORDOP rather than ORDERP in next line to be consistent
   % with the way that REDUCE orders expressions.
                   if ordop(a,car ll) then l2:=car ll . l2
                                       else l1:=car ll . l1;
                   ll:=cdr ll; go to loop;
            exit : return append(qsort l1,a . qsort l2);
      end;

symbolic procedure mapins(ep,cfl);
   % Insert of exponent into coefficient list.
      if null cfl then nil
        else ((ep . caar cfl) . cdar cfl) . mapins(ep,cdr cfl);

symbolic procedure mkzl n;
   % Making of zero-list (length = n-1).
      if n=1 then nil else 0 . mkzl(n-1);

symbolic procedure sq2sstm(sqtm,vd);
   % Transformation of term from sq to ss.
      begin scalar ep,cf,cfl;
            if caar sqtm=caar vd
              then <<cf:=cdr sqtm; ep:=cdar sqtm;
                     if domainp cf
                       then return ((ep . mkzl cdr vd) . cf) . nil
                       else cfl:=sq2sscfpl(cf,cdar vd . sub1 cdr vd)>>
              else <<cfl:=sq2sscfpl(sqtm . nil,cdar vd . sub1 cdr vd);
                     ep:=0>>;
            return mapins(ep,cfl);
      end;

symbolic procedure sq2sscfpl(cfpl,vd);
   % Transformation of coefficient polynomial from sq to ss.
      if null cfpl then nil
        else if domainp cfpl then (mkzl(cdr vd+1) . cfpl) . nil
        else append(sq2sstm(car cfpl,vd),sq2sscfpl(cdr cfpl,vd));

symbolic procedure sq2sspl(sqpl,vd);
   % Transformation of polynomial from sq to ss.
      if domainp sqpl then sqpl
        else append(sq2sstm(car sqpl,vd),sq2sspl(cdr sqpl,vd));

symbolic procedure sdlist nm;
   % Classification of ss by the degree of main variable.
      begin scalar anslist,partlist,n,rnm;
            rnm:=nm;
            init : n:=caaar rnm; partlist:= car rnm . nil;
            loop : rnm:=cdr rnm;
                   if null rnm
                     then <<anslist:=append(anslist,partlist . nil);
                            go to exit>>;
                   if domainp rnm
                     then <<anslist:=append(append(anslist,
                                                   partlist . nil),
                                            rnm);
                            go to exit>>;
                   if n=caaar rnm
                     then <<partlist:=append(partlist,car rnm . nil);
                            go to loop>>
                     else <<anslist:=append(anslist,partlist . nil);
                            go to init>>;
            exit : return anslist;
      end;

symbolic procedure univsdl2sq(var,sdl);
   % Transformation from univariate ss to sq.
      if domainp sdl then sdl
        else if zerop caaaar sdl then cdaar sdl
        else ((var . caaaar sdl) . cdaar sdl) . univsdl2sq(var,cdr sdl);

symbolic procedure mapdel sdl;
   % Deletion of the exponent of main variable from ss.
      if null sdl then nil
        else (cdaar sdl . cdar sdl) . mapdel cdr sdl;

symbolic procedure mulvsdl2sq(vd,sdl);
   % Transformation from multivariate ss to sq.
      if domainp sdl then sdl
        else if zerop caaaar sdl
               then if domainp cdr sdl and cdr sdl
                      then append(sdl2sq(cdar vd . sub1 cdr vd,
                                         sdlist mapdel car sdl),
                                  cdr sdl)
                      else sdl2sq(cdar vd . sub1 cdr vd,
                                  sdlist mapdel car sdl)
               else ((caar vd . caaaar sdl)
                      . sdl2sq(cdar vd . sub1 cdr vd,
                               sdlist mapdel car sdl))
                    . mulvsdl2sq(vd,cdr sdl);

symbolic procedure sdl2sq(vd,sdl);
   % Transformation from classified ss to sq.
      if cdr vd=1 then univsdl2sq(caar vd,sdl)
                   else mulvsdl2sq(vd,sdl);

symbolic procedure termorder1(term1,term2);
   % Comparison of ordering between two terms (purely lexicographic
   % ordering).
      if null term1 then 0
        else if zerop term1 and zerop term2 then 0
        else if zerop term1 then -1
        else if zerop term2 then 1
        else if car term1<car term2 then -1
        else if car term1>car term2 then 1
        else termorder1(cdr term1,cdr term2);

symbolic procedure listsum l;
   % Total degree.
      if null l then 0 else car l+listsum cdr l;

symbolic procedure termorder(term1,term2);
   % Comparison of ordering between two terms (total degree and
   % lexicographic ordering).
      begin scalar s1,s2;
            if null term1 then 0
              else if zerop term1 and zerop term2 then 0
              else if zerop term1 then -1
              else if zerop term2 then 1;
              s1:=listsum term1; s2:=listsum term2;
              return if s1=s2 then termorder1(term1,term2)
                       else if s1<s2 then -1 else 1;
      end;

symbolic procedure xxsort l;
   sort(l,function(lambda (a, b); termorder(car a,car b)<0));


% symbolic procedure xxsort l;
%  %Sort of terms with present ordering.
%     begin scalar a,l1,l2,ll;
%           if null l then return nil;
%           a:=car l; ll:=cdr l;
%           loop : if null ll then go to exit;
%                  if termorder(car a,caar ll)<0
%                    then l1:=car ll . l1
%                    else l2:=car ll . l2;
%                  ll:=cdr ll; go to loop;
%           exit : return append(xxsort l1,a . xxsort l2);
%     end;

symbolic procedure lxsort l;
   sort(l,function(lambda (a, b); termorder1(car a,car b)<0));

% symbolic procedure lxsort l;
%  % Sort of terms with purely lexicographic ordering.
%     begin scalar a,l1,l2,ll;
%           if null l then return nil;
%           a:=car l; ll:=cdr l;
%           loop : if null ll then go to exit;
%                  if termorder1(car a,caar ll)<0
%                    then l1:=car ll . l1
%                    else l2:=car ll . l2;
%                  ll:=cdr ll; go to loop;
%           exit : return append(lxsort l1,a . lxsort l2);
%     end;

symbolic procedure delet(a,l);
   %Deletion from list.
      if null a then l
        else if null l or a=l then nil
        else if a=car l then cdr l
        else car l . delet(a,cdr l);

symbolic procedure lx2xx ss;
   % Transformation from lex. to another normal ordering.
      begin scalar nm,ct;
            if domainp ss or domainp car ss then return ss;
            nm:=cadr ss; ct:=cdr lastnondomain nm;
            return car ss . (append(xxsort delet(ct,nm),ct) . cddr ss);
      end;

symbolic procedure lastnondomain u;
   % Return the last non-domain pair of the list u.
   if domainp u then errach list("non-domain",u)
    else if domainp cdr u then u
    else lastnondomain cdr u;

symbolic procedure xx2lx ss;
   % Transformation from normal ordering to lex.
      begin scalar nm,ct;
            if domainp ss or domainp car ss then return ss;
            nm:=cadr ss; ct:=cdr lastnondomain nm;
            return car ss . (append(lxsort delet(ct,nm),ct) . cddr ss);
      end;

symbolic procedure sf2ss f;
   % Transformation from sf to ss (with denominator 1).
      begin scalar vl,vd;
            if domainp f then return f;
            vl:=searchpl f; vd:=qsort vl . length vl;
            return lx2xx(vd . (sq2sspl(f,vd) . 1));
      end;

symbolic procedure ss2sf s;
   % Transformation from ss to sf (neglecting the denominator).
      if domainp s then s
        else sdl2sq(car s , sdlist cadr xx2lx s );

endmodule;

end;
