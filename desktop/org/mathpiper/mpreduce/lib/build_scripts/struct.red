
% Author: Anthony C. Hearn.

% This code is designed to structure Lisp and REDUCE code.  The result
% should have the same execution behavior as the input.

% The next few bits are to make this code free-standing...

symbolic procedure lprim x; print x;

symbolic procedure no!-side!-effectp u;
   if atom u then numberp u or idp u and not(fluidp u or globalp u)
    else if car u eq 'quote then t
    else if flagp!*!*(car u,'nosideeffects)
     then no!-side!-effect!-listp u
    else nil;

symbolic procedure no!-side!-effect!-listp u;
   null u or no!-side!-effectp car u and no!-side!-effect!-listp cdr u;

flag('(car cdr caar cadr cdar cddr caaar caadr cadar caddr cdaar cdadr
       cddar cdddr cons),'nosideeffects);

% Currently code does not check for duplicate labels.

symbolic procedure structchk u;
   % Top level structuring function.
   begin scalar v;
      repeat <<v := copy u; u := structchk1 u>> until u = v;
      return u
   end;

symbolic procedure structchk1 u;
   begin scalar x;
   if atom u or car u eq 'quote then return u
    else if atom car u and (x := get(car u,'structfn))
     then return apply(x,list u)
    else if car u eq 'lambda
     then return list('lambda,cadr u,structchk1 caddr u)
    else if car u eq 'procedure
     then return list('procedure,cadr u,caddr u,cadddr u,
		      car cddddr u,structchk1 cadr cddddr u)
    else return for each x in u collect structchk1 x
 end;

put('cond,'structfn,'strcond);

put('rblock,'structfn,'blockchk);

put('prog,'structfn,'progchk);

put('progn,'structfn,'prognchk);

symbolic procedure strcond u;
   begin
      u := for each x in cdr u collect list(car x,structchk1 cadr x);
      if length u = 2 and eqcar(cadar u,'cond) and caadr u = 't
       then u := {mknot caar u,cadadr u} . cdadar u;
      return 'cond . u
   end;

symbolic procedure mknot u;
  if not atom u and car u memq '(not null) then cadr u else {'not,u};

fluid '(flg lablist);

symbolic procedure addlbl lbl;
   if atsoc(lbl,lablist) then nil
     else lablist := list(lbl,nil) . lablist;

symbolic procedure addblock lst;
   rplacd(cdr atsoc(getlbl caar lst,lablist),cdar lst . cdr lst);

symbolic procedure gochk u;
   if atom u or car u memq '(quote prog) then nil
    else if car u eq 'go then updlbl(cadr u,u)
    else <<gochk car u; gochk cdr u>>;

symbolic procedure updlbl(lbl,exp);
  begin
    scalar x;
    x := atsoc(lbl,lablist);
    if x then rplaca(cdr x,exp . cadr x)
    else lablist := list(lbl,list exp) . lablist
  end;
   

symbolic procedure transferp u;
   if atom u or not idp car u then nil
    else if flagp(car u,'transfer) then car u
    else if car u eq 'cond then condtranp cdr u
    else if car u memq '(prog2 progn) then transferp car reverse cdr u
    else nil;

flag('(go return rederr error errach),'transfer);

symbolic procedure condtranp u;
   % Determines if every branch of a COND is a transfer.
   if null u then nil
    else if null cdr u and caar u eq t then transferp cadar u
    else transferp cadar u and condtranp cdr u;

symbolic procedure progchk u; blockchk1(u,'prog);

symbolic procedure blockchk u; blockchk1(u,'rblock);

symbolic procedure blockchk1(u,v);
   begin scalar flg,lablist,laststat,vars,top,x,z;
      % Format of element of LABLIST is (label,list of references,body).
      vars := cadr u;
      % Define independent blocks.
      u := cddr u;
      if null u then lprie "empty block";
      % First make sure that block does not 'fall through'.
      x := u;
      while cdr x do x := cdr x;
%     if not transferp car x then rplacd(x,list '(return nil));
      % Now look for first label.
      while u and not labelp car u do
	 <<top := car u . top; gochk car u;  u := cdr u>>;
	   % Should that be structchk1 car u?
      if null u then <<top := reversip top; go to ret>>
       else if null top or not transferp car top
	  then <<top := list('go,getlbl car u) . top; gochk car top>>;
      top := reversip top;
      top := list nil . nil . top . car reverse top;   % lablist format.
      while u do
	if labelp car u
	       then <<addlbl getlbl car u;
		 if null laststat or transferp laststat
		   then <<laststat := nil;
			  x := list car u; u := cdr u;
			  while u and not transferp laststat do
			   <<if labelp car u
			       then u := list('go,getlbl car u) . u;
				gochk car u;
				laststat := car u;
			     x := car u . x;
			     u := cdr u>>;
			  addblock(reversip x . laststat);
			  x := nil>>>>
		 else rederr list("unreachable statement",car u);
      % Merging of blocks.
      lablist := reversip lablist;   % To make final order correct.
    a:
      flg := nil;
      % Removal of (cond ... (pi (go lab)) ...) ... (go lab)).
      for each x in (top . lablist)
	 do if cdr x and cddr x and eqcar(cdddr x,'go)
	      then condgochk(caddr x,cdddr x);
      % Replacement of singly referenced labels by PROGN.
      x := nil;
      while lablist do
	<<z := length cadar lablist;
	   if z=0 or z=1 and cdddar lablist=caadar lablist
	    then lprim list("unreferenced block at label",caar lablist)
	 else if z=1
	  then <<flg := t; lprim list("label",caar lablist,"removed");
		rplacw(caadar lablist,prognchk1 caddar lablist)>>
	 else x := car lablist . x; lablist := cdr lablist>>;
      lablist := reversip x;
      % WHILE/REPEAT insertion.
      for each z in lablist do
	if cdddr z = caadr z
	   and eqcar(caaddr z,'cond)
	   and null cddr caaddr z
	   and transferp cadadr caaddr z
	   and notranp cdaddr z
	 then <<flg := t;
		rplaca(cdr z,!&deleq(cdddr z,cadr z));
		rplaca(cddr z,list(whilechk(mknull caadr caaddr z,
		  cdr reverse cdaddr z),cadadr caaddr z));
		rplacd(cddr z,nil)>>;
      % Superfluous PROGN expansion.
      if flg then for each y in top . lablist do
	<<z := caddr y;
	  while z do
	     if eqcar(car z,'progn) then rplacw(z,nconc(cdar z,cdr z))
	      else z := cdr z;
	      if cdr y and cddr y and eqcar(cdddr y,'progn)
		then rplacd(cddr y,car reverse cdddr y)>>;
      if flg then go to a;
      top := caddr top;   % Retrieve true expression.
      x := top;
      % Pick up remaining labels.
      while x do
	<<while cdr x do x := cdr x;
	  if eqcar(car x,'go) and (z := atsoc(cadar x,lablist))
	    then <<rplacw(x,if cdadr z then mklbl car z . caddr z
			     else <<lprim list("label",caar lablist,
					       "removed"); caddr z>>);
		   lablist := delete(z,lablist)>>
	   else if lablist
	    then <<rplacd(x,mklbl caar lablist . caddar lablist);
				lablist := cdr lablist>>
	 else x := cdr x>>;
 ret: top := miscchk structchk1 top;
      if null vars and eqcar(car top,'return) then return cadar top
       else return v . vars . top;
   end;

symbolic procedure miscchk u;
   % Check for miscellaneous constructs.
   begin scalar v,w;  % x
      v := u;
%     x := copy u;
      while v do if eqcar(car v,'setq) and
	 ((w := setqchk(car v,cdr v)) neq v) then rplacw(v,w)
	  else if cdr v and eqcar(car v,'cond) and null cddar v
	     and eqcar(cadr cadar v,'return)
	  % Next line should be generalized to (...) ... (return ...).
	     and eqcar(cadr v,'return)
	   then rplacw(v,{'return,
			   {'cond,{caadar v,cadr cadr cadar v},
			     {'t,cadr cadr v}}} . cddr v)
	 else v := cdr v;
%     return if u = x then u else miscchk u
      return u
   end;

symbolic procedure setqchk(u,v);
   % Determine if setq in u is necessary.
   begin scalar x,y,z;
      x := cadr u; y := caddr u;
      if not no!-side!-effectp y then return u . v;
  a:  if null v then return u . reversip z
%      else if eqcar(car v,'return) and not smemq(x,cdar v)
%       then return nconc(reversip z,v)
       else if eqcar(car v,'return) and used!-oncep(x,cadar v)
	then <<lprim list("assignment for",x,"removed");
	       return nconc(reversip z,substq(x,y,car v) . cdr v)>>
       else if not smemq(x,car v)
	then <<z := car v . z; v := cdr v; go to a>>
       else return u . nconc(reversip z,v)
   end;

symbolic procedure used!-oncep(u,v);
   % Determines if u is used at most once in v.
   if atom v then t
    else if car v eq 'quote then t
    else if u eq car v then not smemq(u,cdr v)
    else used!-oncep(u,cdr v);

symbolic procedure substq(u,v,w);
   % Substitute first occurrence of atom u in w by v.
   if atom w then if u eq w then v else w
    else if car w eq 'quote then w
    else if u eq car w then v . cdr w
    else if not atom car w then substq(u,v,car w) . substq(u,v,cdr w)
    else car w . substq(u,v,cdr w);

symbolic procedure labelp u;
   atom u or car u eq '!*label;

symbolic procedure getlbl u;
   if atom u then u else cadr u;

symbolic procedure mklbl u; list('!*label,u);

symbolic procedure notranp u;
   null smemqlp('(go return),cdr reverse u);

symbolic procedure !&deleq(u,v);
   if null v then nil else if u eq car v then cdr v
    else car v . !&deleq(u,cdr v);

symbolic procedure prognchk u; prognchk1 cdr u;

symbolic procedure prognchk1 u;
   if null cdr u or null cdr(u:= miscchk u) then car u else 'progn . u;

symbolic procedure mknull u;
   if not atom u and car u memq '(null not) then cadr u
    else list('null,u);

symbolic procedure condgochk(u,v);
   if null u then nil
    else <<condgochk(cdr u,v);
	   if eqcar(car u,'cond) then cgchk1(cdar u,u,v)>>;

symbolic procedure cgchk1(u,v,w);
   if null u then nil
    else if not transferp cadar u then nil
	% We could look for following (T transfer) here.
    else begin scalar x,y,z;
	cgchk1(cdr u,v,w);
	x := cadar u;
	if x=w
	    or eqcar(x,'progn) and (x := car reverse x)=w
		and (y := reverse cdr reverse cdadar u)
	then <<flg := t;
	z := atsoc(cadr w,lablist);
	rplaca(cdr z,!&deleq(x,cadr z));
	rplaca(car u,mknull caar u);
	z := reverse cdr reverse cdr v;
	if cdr u then <<z := ('cond . cdr u) . z; rplacd(u,nil)>>;
	if y then rplacd(u,list list(t,prognchk1 y));
	rplaca(cdar u,prognchk1 z);
	rplacd(v,list w)>>
   else nil
   end;

% The following routines transform MAPs into FOR EACH statements
% were possible;

symbolic procedure mapox u; mapsox(u,'on,'do);

symbolic procedure mapcox u; mapsox(u,'in,'do);

symbolic procedure maplistox u; mapsox(u,'on,'collect);

symbolic procedure mapcarox u; mapsox(u,'in,'collect);

symbolic procedure mapconox u; mapsox(u,'on,'conc);

symbolic procedure mapcanox u; mapsox(u,'in,'conc);

symbolic procedure mapsox(u,v,w);
   begin scalar x,y,z;
      x := cadr u;
      y := caddr u;
      if not eqcar(y,'function)
	then rederr list("syntax error in map expression",u);
      y := cadr y;
      if atom y then <<z := 'x; y := list(y,z)>>
       else if not(car y eq 'lambda) or null cadr y or cdadr y
	then rederr list("syntax error in map expression",u)
       else <<z := caadr y; y := caddr y>>;
      return list('foreach,z,v,x,w,y)
   end;

put('map,'structfn,'mapox);

put('mapc,'structfn,'mapcox);

put('maplist,'structfn,'maplistox);

put('mapcar,'structfn,'mapcarox);

put('mapcan,'structfn,'mapcanox);

put('mapcon,'structfn,'mapconox);

symbolic procedure whilechk(u,v);
   begin scalar w;
      % Note that V is in reversed order.
      return if idp(u) and car v = list('setq,u,list('cdr,u))
	and not((w := caronly(u,cdr v,'j)) eq '!*failed!*)
	then list('progn,list('foreach,'j,'in,u,'do,prognchk1 reversip w),
		  list('setq,u,nil))
       else list('while,u,prognchk1 reversip v)
   end;

symbolic procedure caronly(u,v,w);
   begin scalar x;
      return if not smemq(u,v) then v
	  else if atom v then if u eq v then '!*failed!* else v
    else if not idp car v
       or not(eqcar(cdr v,u) and cdr v and null cddr v
		and (x := get(car v,'carfn)))
     then cmerge(caronly(u,car v,w),caronly(u,cdr v,w))
    else if car v eq 'car then w
    else list(x,w)
   end;

deflist('((car t) (caar car) (cdar cdr) (caaar caar) (cadar cadr)
	  (cdaar cdar) (cddar cddr) (caaaar caaar) (caadar caadr)
	  (cadaar cadar) (caddar caddr) (cdaaar cdaar) (cdadar cdadr)
	  (cddaar cddar) (cdddar cdddr)),
	'carfn);

symbolic procedure cmerge(u,v);
   if u eq '!*failed!* or v eq '!*failed!* then '!*failed!* else u . v;


end;
