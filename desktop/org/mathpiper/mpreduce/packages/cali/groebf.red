module groebf;

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


Comment

                ##############################
                ###                        ###
                ###   GROEBNER FACTORIZER  ###
                ###                        ###
                ##############################

The Groebner algorithm with factorization and constraint lists.

New in version 2.2 :

        syntax for groebfactor
        listgroebfactor!*
        extendedgroebfactor!*

There are two versions of the extended groebner factorizer.
One needs the lex. term order, the other supports arbitrary ones (the
default). Switch between both versions via switch lexefgb.

Internal data structure

        result::={dpmat, constraint list }

        extendedresult::=
                {dpmat, constraint list, (dimension | indepvarset) }

        problem::={dpmat, constraint list, pair list, easydim}

        aggregate::=
        { (list of problems) , (list of results) }

For a system with constraints m=(b,c) V(m)=V(b,c) denotes the zero set
V(b)\setminus D(c).

The Groebner algorithm supports only the classical reduction
principle.

end Comment;

% --- The side effect switching lexefgb on or off :

put('lexefgb,'simpfg,'((t (put 'cali 'efgb 'lex))
        (nil (remprop 'cali 'efgb))));


symbolic procedure groebf!=problemsort(a,b);
% Sorted by ascending easydim to force depth first search.
   (nth(a,4)<nth(b,4))
   or (nth(a,4)=nth(b,4)) and (length second a<= length second b);

symbolic procedure groebf!=resultsort(a,b);
% Sort extendedresults by descending true dimension, assuming the
% third part being the dimension.
   third a > third b;

put('groebfactor,'psopfn,'intf!=groebfactor);
symbolic procedure intf!=groebfactor m;
  begin scalar bas,con;
  bas:=dpmat_from_a reval first m;
  if length m=1 then con:=nil
  else if length m=2 then
        con:=for each x in cdr reval second m collect dp_from_a x
  else rederr("Syntax : GROEBFACTOR(base list [,constraint list])");
  return makelist
        for each x in groebfactor!*(bas,con) collect dpmat_2a first x;
  end;

symbolic operator listgroebfactor;
symbolic procedure listgroebfactor l;
% l is a list of polynomial systems. We look for the union of the
% solution sets.
   if !*mode='algebraic then
        makelist for each x in listgroebfactor!*
                for each y in cdr reval l collect dpmat_from_a y
        collect dpmat_2a x
   else listgroebfactor!* l;

symbolic procedure listgroebfactor!* l;
% Proceed a whole list of dpmats at once.
   begin scalar gbs;
   gbs:=for each x in
        groebf!=preprocess(nil,for each x in l collect {x,nil})
                collect groebf!=initproblem x;
   gbs:=sort(gbs,function groebf!=problemsort);
   return for each x in groebf!=masterprocess(gbs,nil) collect first x;
   end;

symbolic procedure groebfactor!*(bas,poly);
% Returns a list l of results (b,c) such that
%       V(bas,poly) = \union { V(b,c) : (b,c) \in l }

   if dpmat_cols bas > 0 then
        rederr "GROEBFACTOR only for ideal bases"
   else if null !*noetherian then
        rederr "GROEBFACTOR only for noetherian term orders"
   else if dpmat_zero!? bas then list({bas,poly})
   else begin scalar gbs;
   if cali_trace() > 5 then
   << write"GROEBFACTOR the system "; dpmat_print bas >>;
   gbs:=for each x in groebf!=preprocess(nil,list {bas,poly}) collect
                groebf!=initproblem x;
   gbs:=sort(gbs,function groebf!=problemsort);
   return groebf!=masterprocess(gbs,nil);
   end;

put('extendedgroebfactor,'psopfn,'intf!=extendedgroebfactor);
symbolic procedure intf!=extendedgroebfactor m;
  begin scalar bas,con;
  bas:=dpmat_from_a reval first m;
  if length m=1 then con:=nil
  else if length m=2 then
        con:=for each x in cdr reval second m collect dp_from_a x
  else rederr
          "Syntax : EXTENDEDGROEBFACTOR(base list [,constraint list])";
  return makelist
        for each x in extendedgroebfactor!*(bas,con) collect
                makelist {first x,makelist second x,makelist third x};
  end;

symbolic procedure extendedgroebfactor!*(bas,poly);
% Returns a list l of extendedresults (b,c,vars) in prefix form such
% that      V(bas,poly) = \union { V(b,c) : (b,c) \in l }
% and b:<\prod c> is puredimensional with independent variable set vars.

   if dpmat_cols bas > 0 then
        rederr "EXTENDEDGROEBFACTOR only for ideal bases"
   else if null !*noetherian then
        rederr "EXTENDEDGROEBFACTOR only for noetherian term orders"
   else if dpmat_zero!? bas then
        list({dpmat_2a bas,nil,ring_names cali!=basering})
   else begin scalar gbs;
   if cali_trace() > 5 then
   << write"EXTENDEDGROEBFACTOR the system "; dpmat_print bas >>;
   gbs:=for each x in groebf!=preprocess(nil,list {bas,poly}) collect
                groebf!=initproblem x;
   return groebf!=extendedmasterprocess gbs;
   end;

symbolic procedure groebf!=extendedmasterprocess gbs;
% gbs is a list of problems to process. Returns a list of
% extendedresults in prefix form.
% If {m,con,vars} is such an extendedresult then m:<\prod con> is the
% (puredimensional) recontraction of m\tensor k(vars).
   begin scalar res,res1,u;
   while gbs or res do
     if gbs then
     % The hard postprocessing is done only at the end.
     << gbs:=sort(gbs,function groebf!=problemsort);
        % Convert results to extendedresults and sort them :
        res:=for each x in groebf!=masterprocess(gbs,res) collect
                if (length x=3) then x
                else {first x,second x,dim!* first x};
        res:=sort(res,function groebf!=resultsort);
        gbs:=nil
      >>
      else % Do the first (hard) postprocessing
      << % process result by result :
         u:=groebf!=postprocess2 car res; res:=cdr res;
         % Extract and preprocess new problems from u.
         % This needs descent by dimension of the results proceeded.
         gbs:=for each x in groebf!=preprocess(res,second u)
                        collect groebf!=initproblem x;
         % Extract extendedresults from u.
         % They may be non-GB wrt t h i s  term order, see above.
         res1:=nconc(first u,res1);
      >>;
   return res1;
   end;

% --------- Another version of the extended Groebner factorizer -------
put('extendedgroebfactor1,'psopfn,'intf!=extendedgroebfactor1);
symbolic procedure intf!=extendedgroebfactor1 m;
  begin scalar bas,con;
  bas:=dpmat_from_a reval first m;
  if length m=1 then con:=nil
  else if length m=2 then
        con:=for each x in cdr reval second m collect dp_from_a x
  else rederr
          "Syntax : EXTENDEDGROEBFACTOR1(base list [,constraint list])";
  return makelist
        for each x in extendedgroebfactor1!*(bas,con) collect
                makelist {first x,makelist second x,makelist third x};
  end;

symbolic procedure extendedgroebfactor1!*(bas,poly);
% Returns a list l of extendedresults (b,c,vars) in prefix form such
% that      V(bas,poly) = \union { V(b,c) : (b,c) \in l }
% and b:<\prod c> is puredimensional with independent variable set vars.

   if dpmat_cols bas > 0 then
        rederr "EXTENDEDGROEBFACTOR1 only for ideal bases"
   else if null !*noetherian then
        rederr "EXTENDEDGROEBFACTOR1 only for noetherian term orders"
   else if dpmat_zero!? bas then
        list({dpmat_2a bas,nil,ring_names cali!=basering})
   else begin scalar gbs;
   if cali_trace() > 5 then
   << write"EXTENDEDGROEBFACTOR1 the system "; dpmat_print bas >>;
   gbs:=for each x in groebf!=preprocess(nil,list {bas,poly}) collect
                groebf!=initproblem x;
   return for each x in groebf!=extendedmasterprocess1 gbs collect
                nth(x,4);
   end;

symbolic procedure groebf!=extendedmasterprocess1 gbs;
% Version that computes the retraction of each intermediate result
% to apply FGB shortcuts. gbs is a list of problems to process.
% Returns a list of extendedresults in prefix form.
% If {m,con,vars} is such an extendedresult then m:<\prod con> is the
% (puredimensional) recontraction of m\tensor k(vars).
% internally they are incorporated into res as
%       {dpmat, nil (since no constraints), dim, prefix form}.
   begin scalar res,u,v,p;
   while gbs or
        (p:=listtest(res,nil,function (lambda(x,y); length x<4))) do
     if gbs then
     % The hard postprocessing is done only at the end.
     << gbs:=sort(gbs,function groebf!=problemsort);
        % Convert results to extendedresults and sort them :
        res:=for each x in groebf!=masterprocess(gbs,res) collect
                if (length x>2) then x
                else {first x,second x,dim!* first x};
        res:=sort(res,function groebf!=resultsort);
        gbs:=nil
      >>
      else % Do the first (hard) postprocessing
      << % process result by result :
         u:=groebf!=postprocess2 p; res:=delete(p,res);
         % Extract extendedresults from u and convert them
         % with postprocess3 to quotient ideals.
         v:=for each x in first u collect
                {groebf!=postprocess3 x, nil, length third x,x};
         for each y in v do
           if not groebf!=redtest(res,y) then
                res:=merge({y},groebf!=sieve(res,y),
                        function groebf!=resultsort);
         % Extract and preprocess new problems from u.
         gbs:=for each x in groebf!=preprocess(res,second u) collect
                        groebf!=initproblem x;
       >>;
   return res;
   end;

% ------- end of the second version ------------------------

symbolic procedure groebf!=masterprocess(gbs,res);
% gbs = list of problems, res = list of results (since several times
% involved in the extendedmasterprocess).
% Returns a list of results already postprocessed with (the easy)
% groebf!=postpocess1 where the elements surviving from res may
% change only in the constraints part.
   begin scalar u,v;
   while gbs do
   << if cali_trace()>10 then
                print for each x in gbs collect nth(x,4);
      u:=groebf!=slave car gbs; gbs:=cdr gbs;
      if u then % u is an aggregate.
      << % postprocess the result part returning a list of aggregates.
         v:=for each x in second u collect groebf!=postprocess1(res,x);
         % split up into the problems u and results v
         u:=nconc(car u,for each x in v join car x);
         v:=for each x in v join second x;
         for each y in v do
           if cali_trace() > 5 then
           << write"partial result :"; terpri();
              dpmat_print car y ;
               prin2"constraints : ";
               for each x in second y do dp_print2 x;
           >>;
         for each y in v do
           if not groebf!=redtest(res,y) then
                res:=y . groebf!=sieve(res,y);
         for each x in u do
           if not groebf!=redtest(res,x) then
                gbs:=merge({x},groebf!=sieve(gbs,x),
                                function groebf!=problemsort);
         if cali_trace()>20 then
            << terpri(); write length gbs," remaining branches. ",
               length res," partial results"; terpri()
            >>;
       >>
      else % branch discarded
        if cali_trace()>20 then print"Branch discarded";
   >>;
   return res;
   end;

symbolic procedure groebf!=initproblem x;
% Converts a result into a problem.
  list(car x,second x, groeb_makepairlist(dpmat_list car x,t),
                easydim!* car x);

% The following two procedures make destructive changes
% on the cdr of some of the list elements.

symbolic procedure groebf!=redtest(a,c);
% Ex. u \in a : car u \submodule car c ?
% If so, update the constraints of u.
  begin scalar u;
  u:=listtest(a,c,function(lambda(x,y); submodulep!*(car x,car y)));
  if u then cdr u:=intersection(second u,second c).cddr u;
  return u;
  end;

symbolic procedure groebf!=sieve(a,c);
% Remove u \in a with car c \submodule car u
% and update the constraints of c.
  for each x in a join if not submodulep!*(car c,car x) then {x}
     else << cdr c:=intersection(second x,second c).cddr c; >>;

symbolic procedure groebf!=test(con,m);
% nil <=> ex. f \in con : f mod m = 0. m is a baslist.
  if null m then t
  else if dp_unit!? bas_dpoly first m then nil
  else if null con then t
  else begin scalar p; p:=t;
    while p and con do
    << p:=p and bas_dpoly car red_redpol(m,bas_make(0,car con));
       con:=cdr con
    >>;
    return p;
    end;

symbolic procedure groebf!=newcon(r,d);
% r=(m,c) is a result, d a list of polynomials. Returns the
% (slightly optimized) result list ( (m+(p),c+(q|q<p)) | p \in d ).
  begin scalar m,c,u;
    m:=first r; c:=second r;
    return for each p in d join
       if not member(p,c) then
       << u:={matsum!* {m, dpmat_from_dpoly(p)}, c}; c:=p.c; {u} >>;
    end;

symbolic procedure groebf!=preprocess(a1,b);
% Try to split (factor) each polynomial in each problem of the list b.
% Returns a list of results.
% a1 is a list of results already computed.

  begin scalar a,c,d,back,u;
    if cali_trace()>20 then prin2"preprocessing started";
    while b do
    << if cali_trace()>20 then
       << terpri(); write length a," ready. ";
          write length b," left."; terpri()
       >>;
       c:=car b; b:=cdr b;
       if not (null groebf!=test(second c,dpmat_list car c)
            or groebf!=redtest(a1,c)
            or groebf!=redtest(a,c)) then
       << d:=dpmat_list car c; back:=nil;
          while d and not back do
          << u:=((fctrf numr simp dp_2a bas_dpoly car d)
                        where !*factor=t);
             if (length u>2) or (cdadr u>1) then
             << back:=t;
                b:=append(groebf!=newcon(c,
                        for each y in cdr u collect
                                dp_from_a prepf car y),b);
             >>
             else d:=cdr d
          >>;
          if not back then
          << if cali_trace()>20 then
             << terpri(); write"Subproblem :"; dpmat_print car c >>;
             if not groebf!=redtest(a,c) then a:=c . groebf!=sieve(a,c);
          >>
       >>
    >>;
    if cali_trace()>20 then prin2"preprocessing finished...";
    return a;
    end;

symbolic procedure groebf!=slave c;
% Proceed upto the first splitting. Returns an aggregate.
  begin scalar be,back,p,u,v,a,b,gb,pl,nr,pol,con;

  back:=nil;
  gb:=bas_sort dpmat_list first c;
  con:=second c; pl:=third c; nr:=length gb;
  while pl and not back do
  << p:=car pl; pl:=cdr pl;
     if cali_trace() > 10 then groeb_printpair(p,pl);

     pol:=groeb_spol p;
     if cali_trace() > 70 then
     << terpri(); write"S.-pol : "; dp_print2 bas_dpoly pol >>;
     pol:=bas_dpoly car red_redpol(gb,pol);
     if cali_trace() > 70 then
     << terpri(); write"Reduced S.-pol. : "; dp_print2 pol >>;

     if pol then
     << if !*bcsimp then pol:=car dp_simp pol;
        if dp_unit!? pol then
        << if cali_trace()>20 then print "unit ideal";
           back:=t
        >>
        else
        << % -- factorize pol
           u:=((fctrf numr simp dp_2a pol) where !*factor=t);
           nr:=nr+1;
           if length cdr u=1 then % only one factor
           << pol:=dp_from_a prepf caadr u;
              be:=bas_make(nr,pol);
              u:=be.gb;
              if null groebf!=test(con,u) then
              << back:=t;
                 if cali_trace()>20 then print" zero constraint";
              >>
              else
              << if cali_trace()>20 then
                 << terpri(); write nr,". "; dp_print2 pol >>;
                 pl:=groeb_updatePL(pl,gb,be,t);
                 if cali_trace() > 30 then
                 << terpri(); groeb_printpairlist pl >>;
                 gb:=merge(list be,gb,function red_better);
              >>
           >>
           else % more than one factor
           << for each x in cdr u do
              << pol:=dp_from_a prepf car x;
                 be:=bas_make(nr,pol);
                 a:=be.gb;
                 if groebf!=test(con,a) then
                 << if cali_trace()>20 then
                    << terpri(); write nr; write". "; dp_print2 pol >>;
                    p:=groeb_updatePL(append(pl,nil),gb,be,t);
                    if cali_trace() > 30 then
                    << terpri(); groeb_printpairlist p >>;
                    b:=merge(list be,append(gb,nil),
                        function red_better);
                    b:=dpmat_make(length b,0,b,nil,nil);
                    v:={b,con,p}.v;
                 >>
                 else if cali_trace()>20 then print" zero constraint";
                 if not member(pol,con) then con:=pol . con;
              >>;
              if null v then
                << if cali_trace()>20 then print "Branch canceled";
                   back:=t
                >>
              else if length v=1 then
              << c:=car v; gb:=dpmat_list first c; con:=second c;
                 pl:=third c; v:=nil;
              >>
              else
              << back:=t;
                 if cali_trace()>20 then
                 << write" Branching into ",length v," parts ";
                    terpri();
                 >>;
              >>;
           >>;
        >>;
     >>;
  >>;
  if not back then % pl exhausted => new partial result.
      return
      {nil,list {groeb_mingb dpmat_make(length gb,0,gb,nil,t),con}}
  else if v then return
        {for each x in v collect
                {first x,second x,third x,easydim!* first x},
        nil}
  else return nil;
  end;

symbolic procedure groebf!=postprocess1(res,x);
% Easy postprocessing a result. Returns an aggregate.
% res is a list of results, already obtained.

  begin scalar p,r,v;

     % ---- interreduce and try factorization once more.

     if !*red_total then
     << v:=groebf!=preprocess(res,
                list {dpmat_make(dpmat_rows car x,0,
                   red_straight dpmat_list car x,nil,
                                dpmat_gbtag car x),
                        second x});
        if (length v=1) and dpmat_gbtag caar v then r:=v
        else p:=for each x in v collect groebf!=initproblem x;
     >>
     else r:={x};
     return {p,r};
     end;

symbolic procedure groebf!=postprocess2 m;
  (begin scalar d,vars,u,v,c1,m1,m1a,m2,p,con;
    con:=second m; d:=third m; m:=first m;
    v:=moid_goodindepvarset m;
    if neq(length v,d) then
                rederr"In POSTPROCESS2 the dimension is wrong";
    if null v then return
        {for each x in groebf!=zerosolve(m,con)
            collect {x,nil,nil},nil};

    % -- Prepare data for change to dimension zero :
    % Recompute gbases wrt. the elimination order for u and
    % take only those components for which v remains independent.

    vars:=ring_names(c1:=cali!=basering);
    u:=setdiff(vars,v);
    if get('cali,'efgb)='lex then setring!* ring_lp(c1,u)
    else setring!* ring_rlp(c1,u);
    m1:=for each u in groebfactor!*(dpmat_neworder(m,nil),
                for each x in con collect dp_neworder x) collect
        {first u,second u,dim!* first u};
    for each x in m1 do
      if (third x = d) and member(v,indepvarsets!* car x)
        then m1a := x . m1a
      else m2:=x.m2;
      % m1a : components with indepvarset v
      % m2  : components with v being dependent variables.

    % -- Change to dimension zero.

    m1:=for each x in m1a collect
        {dpmat_2a first x,for each p in second x collect dp_2a p};
    if get('cali,'efgb)='lex then
        setring!* ring_define(u,nil,'lex,for each x in u collect 1)
    else setring!* ring_define(u,degreeorder!* u,'revlex,
                                for each x in u collect 1);
    m1:=for each x in m1 collect
        {groeb_mingb dpmat_from_a first x,
                for each p in second x collect dp_from_a p};

    % Extract the lc's of the lifted Groebner bases and save them
    % for NewCon on the list m1a, since in the zerodimensional part
    % lc's are assumed to be invertible.
    m1a:=pair(m1a,for each x in m1 collect groebf!=elcbe first x);

    % Compute the zerodimensional TriangSets from m1 and their lists
    % of lc's and prepare them for lifting.
    m1:=for each x in m1 join groebf!=zerosolve(first x,second x);
    m1:=for each x in m1 collect {x,groebf!=elcbe dpmat_from_a x};

    % -- Lift all stuff back to c1.

    setring!* c1;

    % Extract the TriangSets as extendedresults in prefix form (!).
    m1:=for each x in m1 collect {first x,second x,v};

    % List of new problems found during recomputation of GB :
    m2:=for each x in m2 collect
        {dpmat_neworder(first x,nil),
         for each y in second x collect dp_neworder y};

    % List of new problems, derived from nonzero conditions for
    % lc's in dimension zero.
    m1a:=for each x in m1a join
        groebf!=newcon({dpmat_neworder(first car x,nil),
                 for each p in second car x collect dp_neworder p},
        for each p in cdr x collect dp_from_a p);

Comment The list of results :

m1 : The list of TriangSets wrt. v produced in this run. They are in
alg. prefix form to remember that they are Groebner bases only
wrt. the pure lex. term order.

m2 : Results (in prefix form), for which v is dependent.

m1a : Branches, where some of the critical lc's of the TriangSets
      vanish.

Both m2 and m1a should be returned in the pool of problems.

end comment;

    return {m1,nconc(m1a,m2)};
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

symbolic procedure groebf!=elcbe(m);
% Extract list of leading coefficients in algebraic prefix form
% from base elements of the dpmat m.
   for each x in dpmat_list m join
        if domainp dp_lc bas_dpoly x then {}
        else {bc_2a dp_lc bas_dpoly x};

symbolic procedure groebf!=postprocess3 u;
% Compute for the extendedresult u={m,con,vars} in prefix form
%       m:<\prod con>.
   matqquot!*(dpmat_from_a first u,
        groebf!=prod for each x in second u collect dp_from_a x);

symbolic procedure groebf!=prod l;
  begin scalar p; p:=dp_fi 1;
  l:=listminimize(for each x in l join dp_factor x,function equal);
  for each x in l do p:=dp_prod(x,p);
  return p;
  end;

symbolic procedure groebf!=zerosolve(m,con);
% Hook for the zerodimensional solver.
% Input : m = zerodimensional dpmat (not to be checked),
%       con = list of dpoly constraints.
% Output : a list of dpmats in prefix form.
  begin scalar u;
  % Look up the constraints, since during the change to dimension zero
  % some of them may trivialize :
  con:=for each x in con join if not dp_unit!? x then {x};
  % Factorized radical computation.
  u:=groebf_zeroprimes1(m,con);
  % Apply the zerosolver to each of these results.
  return for each x in u join
        if get('cali,'efgb)='lex then zerosolve!* x else zerosolve1!* x;
  end;

symbolic procedure groebf_zeroprimes1(m,con);
% Returns a list of gbases for the zerodimensional ideal m,
% incorporating as in the Groebner factorizer the factors of the
% univariate polynomials in m according to such variables, that don't
% appear as leading terms in m.
  begin scalar m1,m2,p,u,l;
  l:=list {m,con};
  for each x in ring_names cali!=basering do
  << m1:=m2:=nil;
     for each y in l do

        % The following checks, whether x is a leading term of first
        % y. Such x may be skipped, since embedding dimension may be
        % reduced. On the other hand, computing univariate polynomials
        % for them is often quite nasty.

        if not member(x,for each v in dpmat_list first y join
                {mo_linear dp_lmon bas_dpoly v}) then
     << p:=odim_up(x,first y); u:=dp_factor p;
        if (length u>1) or not equal(first u,p) then
                m1:=nconc(groebf!=newcon(y,u),m1)
        else m2:=y.m2;
     >>
        else m2:=y.m2;
     l:=groebf!=masterprocess(
        sort(for each x in m1 collect groebf!=initproblem x,
                function groebf!=problemsort),
        m2);
  >>;
  return for each x in l join
        if second x then {matqquot!*(first x,groebf!=prod second x)}
        % Here one can use the linear algebra quotient algorithm, since
        % first x is known to be zerodimensional radical.
        else {first x};
    end;

endmodule; % groebf

end;
