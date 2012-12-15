module myvector;
% This is a simple vector package for vectors arbitrary dimension. The
% methods are very similar to those in avector.
% Author: Simon Weitzhofer

create!-package('(myvector),'(contrib myvector));

global '(myvector!-loaded!*);

myvector!-loaded!* := t;


% The following procedure checks for a myvector
symbolic procedure myvecp u;
begin scalar x;
%debug%write("myvecp called with parameter ",u," # ");terpri();
return
  if null u or numberp u then nil
  else if atom u then (get(u,'rtype)='!!myvec or vectorp u)
  else if (atom(x:=car u) and get(x,'rtype)='!!myvec)
    then t
  else if flagp(x,'myvectorfn) then t
  else if (flagp(x,'varithop) or flagp(x,'myvectormapping))
    then hasonemyvector cdr u
  else nil;
end;

flag('(myvecp),'boolean);

% The following procedure checks to ensure that the arg list contains
% at least one vector
symbolic procedure hasonemyvector u;
  <<%debug%write("hasonemyvector entered with argument ", u);terpri();
  if null u then nil
    else (myvecp car u) or (hasonemyvector cdr u)>>;

% The following procedure checks that the arg list consists entirely
% of vectors
symbolic procedure areallmyvectors u;
<<%debug%write("areallmyvectors called with parameter ",u," # ");terpri();
  if null u then nil
  else if null cdr u then myvecp car u
  else (myvecp car u) and (areallmyvectors cdr u)>>;

% Here we redefine getrtype1 and getrtype2 to handle vectors.

remflag('(getrtype1 getrtype2),'lose);  % We must use these definitions.

symbolic procedure getrtype1 u;
<<%debug%write("getrtype1 called with parameter ",u," # ");terpri();
   if vectorp u then '!!myvec else nil>>;

symbolic procedure getrtype2 u;
  begin scalar x;
%debug%write("getrtype2 called with parameter ",u," # ");terpri();
    return if myvecp u then '!!myvec
      else if (x:=get(car u,'rtype)) and (x:=get(x,'rtypefn))
	then apply1(x, cdr u)
      else if x:=get(car u,'rtypefn) then apply1(x,cdr u)
      else nil
  end;

symbolic procedure myvec u;
  begin scalar y;
  %debug%write("myvec called with parameter ",u," # ");terpri();
  for each x in u do
  << if not atom x then write("Cannot declare ",x," as a vector")
    else
    << y := gettype x;
      if y memq '(array procedure matrix operator) then
	write("*** Object ",x," has already been declared as ",y)
      else makenewmyvector(x)
    >>;
  >>;
  return nil
  end;

deflist('((myvec rlis)),'stat);

symbolic procedure makenewmyvector (u);
  begin
%debug%write("makenewmyvector called with parameter ",u," # ");terpri();
%   write("Declaring ",U," a vector");terpri();
    put(u,'rtype,'!!myvec);
    put(u,'avalue,list('myvector,mkvect(2)));
    return nil
  end;

% Vector function declarations : these are the routines that link
% our new data type into the REDUCE system.

  put('!!myvec,'letfn,'myveclet);           % Assignment routine
  put('!!myvec,'name,'!!myvec);          % Our name for the data type
  put('!!myvec,'evfn,'!*myvecsm!*);         % Evaluation function
  put('!!myvec,'prifn,'myvecpri!*);         % Printing function
  flag('(!!myvec),'sprifn);

  symbolic procedure myvecpri!*(u,v,w);
<<%write("myvecpri!* called with parameters ",u," ,",v," and ",w," # ");terpri();
    myvecpri(u)>>;

% The following routine prints out a vector in a neat way (cf.
% the way in which matrices are printed !

  symbolic procedure myvecpri(u);
  begin scalar y,v,xx;
%write("myvecpri called with parameter ",u," # ");terpri();
  y:= if vectorp u then u else getmyavalue u;

    if null y then return nil;
%   if null x then xx := 'vec;
    xx := 'vec;
    writepri("(",'first);
    for i:=0:(upbv(y)-1) do
    << v := getv(y,i);
      writepri(mkquote v,nil);
      writepri(",",nil)>>;
    v := getv(y,upbv(y));
    writepri(mkquote v,nil);
    writepri(")",'last);
    terpri!* t;
  end;

symbolic procedure getmyavalue u;
   <<%debug%write("getmyavalue called with parameter ",u," # ");terpri();
   if numberp u then u else
  (if x then cadr x else nil) where x=get(u,'avalue)>>;

put('!!myvec,'setelemfn,'setmyvectorelement);

symbolic procedure indexedmyvectorp u;
<<%debug%write("indexedmyvectorp called with parameter ",u," # ");terpri();
  (myvecp car u) and (fixp cadr u) and (getdimension(car(u))>=cadr(u))>>;

% The following function sets one element of a vector object

symbolic procedure getdimension(v);
  <<%write("getdimension called with parameter ",v," # ");terpri();
  v:= aeval v;
  if (null v) then rerror(myvector,1,list("getdimension called on a null object"))
  else if (myvecp v) then upbv v
  else rerror(myvector,1,list(v,"typeerror in getdimension"))>>;

symbolic procedure setmyvectorelement(u,v);
  begin
%debug%write("setmyvectorelement called with parameters ",u," and ",v," # ");terpri();
  if (not indexedmyvectorp u) then rerror(myvector,3,list(u,"not a valid vector index"));
  putv(getmyavalue car u,cadr u,v);
  return nil
  end;

% If SETK is invoked with an vector atom as its first argument, then
% control will be passed to the routine VECLET
% assignment routine

symbolic procedure myveclet(u,v,utype,b,vtype);
  begin
%debug%write("myveclet called with parameters ",u," , ",v," , ",utype," , ",b," , ",vtype," # ");terpri();
%  if zerop v then return setmyvectortozero(u,utype);
  if not equal(vtype,'!!myvec)
    then rerror(myvector,4,"RHS is not a vector");
  if equal(utype,'!!myvec) then put(u,'avalue,list('!!myvec,v))
  else if utype memq '(array matrix) then
    rerror(myvector,5,list(u,"already defined as ",utype))
  else
  << % We force U to be a vector
     myvec u;
     write("*** ",u," re-defined as vector"); terpri();
     put(u,'avalue,list('myvec,v))
  >>;
  return v
  end;

% A quick and dirty way of declaring a null vector

symbolic procedure setmyvectortozero(u,utype);
  begin scalar x;
%debug%write("setmyvectortozero called with parameters ",u," and ",utype," # ");terpri();
  x := mkvect 2;
  for k:=0:2 do putv(x,k,aeval 0);
  return myveclet(u,x,utype,t,'!!myvec)
  end;

symbolic procedure getmyvectorindex(u);
<<%debug%write("getmyvectorindex called with parameters ",u," and ",flg," # ");terpri();
  if not(null u) and (getdimension car u >= cadr u) then cadr u
  else rerror(myvector,6,list(u,"not a valid vector index"))>>;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                      %
%                   VECTOR EVALUATION MODULE                           %
%                                                                      %
% This section contains the routines required to evaluate vector       %
% expressions. The main routine, VECSM!*, is mainly table-driven.      %
% If you wish to include your own routines then you should be          %
% aware of the mechanism used to invoke vector evaluation.             %
%                                                                      %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
symbolic procedure !*myvecsm!*(u,v);
<<%debug%write("!*myvecsm!* called with parameters ",u," and ",v," # ");terpri(); 
myvecsm!* u>>;

symbolic procedure myvecsimp!* u; 
<<%debug%write("myvecsimp!* called with parameter ",u," # ");terpri();
if myvecp u then myvecsm!* u else u>>;

symbolic procedure myvecsm!* u;
  begin scalar y,vecopr,vargs,v;
  %debug%write("myvecsm!* called with parameter ",u," # ");terpri();
  if atom u then v := (if vectorp u then u else getmyavalue u)
  else if vectorp u then v := u
  else if (atom(y:= car u) and get(y,'rtype)='!!myvec) then
    v := getv(getmyavalue y,getmyvectorindex(u))

% Those were the simple cases. Now for the tricky operators

% Separate the operator from its operands

  else <<
    vecopr := car u;
    vargs := for each j in cdr u collect myvecsimp!* j;

% Select a course of action dependent upon the operator

    if y := get(vecopr,'myvectorfunction) then
    % We must check (if the op is an arithmetic operator)
    % to ensure that there are vectors in the argument list
    << if (flagp(vecopr,'myvectorfn) or (flagp(vecopr,'varithop) and
       hasonemyvector vargs)) then v := apply(y,list vargs)
                            else v := aeval append(list(vecopr),vargs)
    >>
    else if flagp(vecopr,'myvectormapping) then
    << % Check that the argument is really a vector
      y := car vargs;
      v := if vectorp y then myvectorapply(vecopr,y,cdr vargs)
                             else scalarapply(vecopr,y,cdr vargs)
    >>
    else <<rerror(myvector,7,
                  list(vecopr,"is not a valid vector operator"))>>;
  >>;
  return v
 end;

symbolic procedure vectormapping u;
<<%debug%write("vectormapping called with parameter ",u," # ");terpri();
  flag(u,'myvectormapping)>>;

deflist('((vectormapping rlis)),'stat);

% We will allow the basic transcendental functions to be vector-valued.
% Then we can, for example, evaluate Sin of a vector.

vectormapping 'sin,'cos,'log,'exp,'tan,'asin,'atan,'sinh,'cosh,'tanh;
vectormapping 'quotient,'minus,'df,'int,'sqrt;

% We must put appropriate flags upon the arithmetic operators and
% vector operators too ...
 flag('(sub minus difference quotient plus times expt),'varithop);
 flag('(myvect cross dot perpendicular unitperpendicular unitvector vmod listtomyvect dim get xvcoord yvcoord zvcoord),'myvectorfn);

% We must now define the procedures to carry out vector algebra and
% calculus. They must be given a VECTORFUNCTION property

symbolic smacro procedure myvectorfn(oper,vfn);
  put(oper,'myvectorfunction,vfn);

% Scalar-vector multiplication
% TODO: Add control for size

myvectorfn('times,'myvectormultiply);
symbolic procedure myvectormultiply vargs;
  % This routine multiplies together a list made up of scalars,
  % 3x3 matrices and a vector. Note that the combinations
  % vector*vector and vector*matrix are illegal.
  begin scalar  lht,rht,lhtype,rhtype;
  %debug%write("myvectormultiply entered with argument ", vargs);terpri();
  lht := aeval car vargs;         % Begin with first multiplicand
  for each v in cdr vargs do
  << % Deal with each multiplicand in turn
    rht := if myvecp v then myvecsm!* v
                     else v;
    lhtype := !*typeof  lht;
    rhtype := !*typeof  rht;
    lht :=
      if not (lhtype='!!myvec or rhtype='!!myvec) then
        aeval list('times,lht,rht)

      else if lhtype='!!myvec then
        if null rhtype then myvectorapply('times,lht,list rht)
	else if (rhtype='!!myvec) then myvectordot(list(lht, rht))
	else myvectortimesmatrix(lht,rht)


      else if null lhtype then myvectorapply('times,rht,list lht)

      else matrixtimesmyvector(lht,rht)
  >>;
  return lht
  end;

% Multiplication of a vector by a quadratic matrix from the left
% TODO: Add control for size

symbolic procedure matrixtimesmyvector(m,v);
  begin scalar rows,myrow,x,n,rowunev,size;
%debug%write("matrixtimesvector entered with arguments ", m," and ", v);terpri();
  if atom m and idp m and null getmyavalue m then
    rerror(myvector,9,"Unset matrix in vector multiplication");
  rows := if idp m then cdr getmyavalue m else cdr m;
  if not eqn((n:=upbv(v))+1,length(car rows)) then
    rerror(myvector,10,"Vector/Matrix dimension mismatch");
%  if not (n=upbv(v)+1) then
%    rerror(myvector,11,"Vector/Matrix dimension mismatch");
  size := length(rows)-1;
  x := mkvect(size);
  for k:=0:size do
  <<
    %debug%write("  ",k);
    myrow := car rows;
    rowunev:=nil;
    for j:=0:n do
    <<rowunev:= list('times,car myrow,getv(v,j)). rowunev;
      myrow:=cdr myrow>>;
    rowunev:= 'plus . rowunev;
    putv(x,k,aeval rowunev);
    rows := cdr rows
  >>;
  return x
  end;

symbolic procedure myvectortimesmatrix(v,m);
  begin scalar rows,myrow,x,n,size;
%debug%write("matrixtimesvector entered with arguments ", m," and ", v);terpri();
  if atom m and idp m and null getmyavalue m then
    rerror(myvector,9,"Unset matrix in vector multiplication");
  rows := if idp m then cdr getmyavalue m else cdr m;
  if not eqn((n:=upbv(v))+1,length rows) then
    rerror(myvector,11,"Vector/Matrix dimension mismatch");
  
  size := length(car rows)-1;
  x := mkvect(size);
  for k:=0:n do
  <<myrow := car rows;
    for j:=0:size do
    <<putv(x,j,list('times,car myrow,getv(v,k)) . getv(x,j));
      myrow:=cdr myrow>>;
    rows := cdr rows
  >>;
  for j:=0:size do
    putv(x,j, aeval ('plus . getv(x,j)));
  return x
  end;


symbolic procedure !*typeof u;
<<%debug%write("!*typeof entered with argument ", u);terpri();
  getrtype u>>;

% Vector addition

myvectorfn('plus,'myvectorplus);
symbolic procedure myvectorplus vargs;
  % Add an arbitrarily-long list of vectors
  begin scalar x;
  %debug%write("myvectorplus entered with argument ", vargs);terpri();
  x := myvecsm!* car vargs;
  for each v in cdr vargs do x:=myvectoradd(x,if myvecp v then myvecsm!* v else v);
  return x
  end;

symbolic procedure myvectoradd(u,v);
  % Add two vectors or two scalars
  begin scalar x,uisvec,visvec,n;
  %debug%write("myvectoradd entered with arguments ", u," and ",v);terpri();
  uisvec := myvecp u; visvec := myvecp v;
  if uisvec and visvec then
    if not eqn(upbv(u),upbv(v)) then
      rerror(myvector,12,"Trying to add two vectors of different size")
    else
    <<x :=mkvect(n:=upbv(u));
      for k:=0:n do putv(x,k,aeval list('plus, getv(u,k), getv(v,k)));
      return x
    >>
  else if uisvec then
    <<x :=mkvect(n:=upbv(u));
      for k:=0:n do putv(x,k,aeval list('plus, getv(u,k), v));
      return x
    >>
  else if visvec then
    <<x :=mkvect(n:=upbv(v));
      for k:=0:n do putv(x,k,aeval list('plus, u, getv(v,k)));
      return x
    >>
  else 
    return aeval list('plus, u, v)
 end;

% Difference of two vectors or scalar and vector

myvectorfn('difference,'myvectordiff);
symbolic procedure myvectordiff vargs;
  % Vector - Vector
  begin scalar x,y;
  %debug%write("myvectordiff entered with argument ", vargs);terpri();
  x := myvecsm!* car vargs;
  y := myvecsm!* list('minus,cadr vargs);   % Negate the second operand
  return myvectoradd(x,y)
  end;


% General case of a quotient involving vectors

myvectorfn('quotient,'myvectorquot);
symbolic procedure myvectorquot vargs;
  % This code deals with the cases
  %
  % (1) Vector / scalar
  % (2) Vector / (scalar-valued vector expression)
  % (3) Scalar / (scalar-valued vector expression)
  %
  begin scalar vdivisor,vdividend;
%debug%write("myvectorquot entered with argument ", vargs);terpri();
  vdivisor := aeval cadr vargs;
  if myvecp vdivisor
    then rerror(myvector,14,"Attempt to divide by a vector");
  vdividend := aeval car vargs;
  if vectorp vdividend then return myvectorapply('quotient,
                                          vdividend, list vdivisor)
  else return aeval list('quotient, vdividend, vdivisor);
 end;

% Vector cross product

myvectorfn('cross,'myvectorcrossprod);
symbolic procedure myvectorcrossprod vargs;
  begin scalar x,y,u0,u1,u2,v0,v1,v2,w0,w1,w2;
%debug%write("myvectorcrossprod entered with argument ", vargs);terpri();
  x := myvecsm!* car vargs;
  y := myvecsm!* cadr vargs;
  if (eqn(upbv(x),2) and eqn(upbv(x),2)) then
  <<u0 := getv(x,0); u1 := getv(x,1); u2 := getv(x,2);
    v0 := getv(y,0); v1 := getv(y,1); v2 := getv(y,2);
    % Calculate each component of the cross product
    w0 := aeval list('difference,
		      list('times,u1,v2),
		      list('times,u2,v1));
    w1 := aeval list('difference,
		      list('times,u2,v0),
		      list('times,u0,v2));
    w2 := aeval list('difference,
		      list('times,u0,v1),
		      list('times,u1,v0));
    x := mkvect(2);
    putv(x,0,w0); putv(x,1,w1); putv(x,2,w2);
    return x
  >>
  else if (eqn(upbv(x),1) and eqn(upbv(x),1)) then
  <<u0 := getv(x,0); u1 := getv(x,1);
    v0 := getv(y,0); v1 := getv(y,1);
    w2:=aeval list('difference,
			list('times,getv(x,0),getv(y,1)),
			list('times,getv(x,1),getv(y,0)));
    return w2
  >>
  else
    rerror(myvector,15,"A problem in cross");
  end;

% Vector modulus

myvectorfn('vmod,'myvectormod);

% There are two definitions due to the existence of a bug in the REDUCE
% code for SQRT : in the IBM version of REDUCE 3.3 an attempt to take
% SQRT of 0 results in an error, so I have coded round it.

% The first version which follows is the succinct version which will
% work if SQRT(0) doesn't give an error.

% symbolic procedure vectormod u;
%   aeval list('sqrt,list('dot,car u,car u));

% This version is a little longer but it works even if SQRT(0) doesn't.

symbolic procedure myvectormod u;
  begin scalar v;
%debug%write("myvectormod entered with argument ", u);terpri();
  v := aeval list('dot, car u, car u);
  if zerop v then return 0
             else return aeval list('sqrt,v);
  end;

% Vector dot product

myvectorfn('dot,'myvectordot);
symbolic procedure myvectordot vargs;
  begin scalar x,y,n,u,v, unev;
%debug%write("vectordot entered with argument ", u);terpri();
  x := car vargs;
  y := cadr vargs;
  if not eqn(n:=(upbv x) , (upbv y)) then
    rerror(myvector,16,"The arguments of dot don't have the same length");

  unev:=for i:=0:n collect
  <<u := getv(x,i);
    v := getv(y,i);
    unev:=list('times,u,v)>>;
  unev := 'plus . unev;
  
  return aeval unev
  end;

% Component-wise assignment of a vector (AVEC)

myvectorfn('myvect,'myvectoravec);

symbolic procedure myvectoravec vargs;
  begin scalar x, vargstmp;
%debug%write("myvectoravec called with parameter ",vargs," # ");terpri();
 % Build a vector from the argument list
%  if not eqn(length(vargs),3) then
%            rerror(myvector,17,"Incorrect number of args in AVEC");
  
  x := mkvect(length(vargs)-1);
  vargstmp:=vargs;
  for i:=0:(length(vargs)-2) do
    <<putv(x,i,aeval car vargstmp);
      vargstmp:=cdr vargstmp>>;
    putv(x,length(vargs)-1,aeval car vargstmp);
  return x
  end;

myvectorfn('listtomyvect,'listtomyvector);

symbolic procedure listtomyvector vargs;
  begin scalar x, vargstmp;
%debug%write("listtomyvector called with parameter ",vargs," # ");terpri();
 % Build a vector from the argument list
%  if not eqn(length(vargs),3) then
%            rerror(myvector,17,"Incorrect number of args in AVEC");
  if length(vargs)=1 and atom car vargs then
  	return listtomyvector(list(getmyavalue car vargs));
  if length(car vargs)<2 then
  	rerror(myvector,21,"an error occured");
  x := mkvect(length(car vargs)-2);
  vargstmp:=car vargs;
  if not((car vargstmp)='list) then
    rerror(myvector,20,"argument for listtomyvect must be a list");
  vargstmp:=cdr vargstmp;
  for i:=0:(length(car vargs)-3) do
    <<putv(x,i,aeval car vargstmp);
      vargstmp:=cdr vargstmp>>;
    putv(x,length(car vargs)-2,aeval car vargstmp);
  return x
  end;
% Vector substitution - definition of SUB as a VECTORFN
% function.
myvectorfn('sub,'myvectorsub);

% Now we have to define mapping for SUB. It's made a little complicated
% by the fact that the argument list for SUB has the interesting bit
% i.e. the vector, at the end.

symbolic procedure myvectorsub vargs;
  begin scalar subslist,vexpr,x,n;
  vexpr := car reverse vargs;    % That was the easy part !
  % Now we need to get the rest of the list
  subslist := reverse cdr reverse vargs; % Dirty, but effective !
  n := ubpv(vexpr);
  x := mkvect(n);
  for k:=0:n do
    putv(x,k,aeval append('(sub),
                          append(subslist,list getv(vexpr,k))));
  return x
  end;

symbolic procedure myvectorapply(vecopr,v,args);
  begin scalar vv,x,y,n;
%debug%write("myvectorapply called with parameter ",vecopr," ,",v," and ",args," # ");terpri();
  n := upbv(v);
  x := mkvect(n);
  vv := if not vectorp v then myvecsm!* v
                         else v;
  for k:=0:n do
  << % Apply the operation to each component
    y := getv(vv,k);
    y := if null args then aeval list(vecopr,y)
                      else aeval append(list(vecopr,y),args);
    putv(x,k,y);
  >>;
  return x
  end;

% We need to define a dummy routine to apply a function to a scalar

symbolic procedure scalarapply(op,v,args);
<<%debug%write("scalarapply called with parameters ",op,", ",v," and ",args," # ");terpri();
  if null args then aeval list(op,v)
               else aeval append(list(op,v),args)>>;

myvectorfn('perpendicular,'myperpendicular);
symbolic procedure myperpendicular vargs;
  begin scalar x,u,v;
%debug%write("myperpenducular entered with argument ", vargs);terpri();
  x := car vargs;
  if not eqn(1,upbv x) then
    rerror(myvector,18,"perpendicular needs one vector of dimension 2");
  u := aeval list('minus,getv(x,1));
  v := getv(x,0);
  x := mkvect(1);
  putv(x,0,u); putv(x,1,v);
  return x
  end;

myvectorfn('unitperpendicular,'myunitperpendicular);
symbolic procedure myunitperpendicular vargs;
  begin scalar x,u,v,n,x0,x1;
%debug%write("myperpenducular entered with argument ", vargs);terpri();
  x := car vargs;
  if not eqn(1,upbv x) then
    rerror(myvector,19,"unitperpendicular needs one vector of dimension 2");
  x0 := getv(x,0);
  x1 := getv(x,1);
  n := list('sqrt,list('plus,list('times,x0,x0),list('times,x1,x1)));
  u := aeval list('minus,list('quotient,getv(x,1),n));
  v := aeval list('quotient, getv(x,0), n);
  x := mkvect(1);
  putv(x,0,u); putv(x,1,v);
  return x
  end;

myvectorfn('unitvector,'myunit);
symbolic procedure myunit vargs;
  begin scalar x,r,k,n,norm;
%debug%write("myunit entered with argument ", vargs);terpri();
  x := car vargs;
  %length of vector
  n := upbv x;
  %the vector to return
  r := mkvect(n);
  norm := myvectormod vargs;
  for k:=0:n do
    putv(r,k,aeval list('quotient, getv(x,k), norm));
  return r
  end;

myvectorfn('dim,'mydim);
symbolic procedure mydim vargs;
  1+upbv car vargs;
  
myvectorfn('xvcoord,'myxvcoord);
symbolic procedure myxvcoord vargs;
  getv(car vargs,0);

myvectorfn('yvcoord,'myyvcoord);
symbolic procedure myyvcoord vargs;
  getv(car vargs,1);
  
myvectorfn('zvcoord,'myzvcoord);
symbolic procedure myzvcoord vargs;
  getv(car vargs,2);

myvectorfn('get,'mygetelement);
symbolic procedure mygetelement vargs;
   getv(car vargs,cadr vargs);

endmodule;

symbolic procedure assgnpri(u,v,w);
   begin scalar x, tm;
   % U is expression being printed.
   % V is a list of expressions assigned to U.
   % W is an id that indicates if U is the first, only or last element
   %  in the current set (or NIL otherwise).
   % Returns NIL.
    testing!-width!* := overflowed!* := nil;
    if null u then u := 0;
    if !*nero and u=0 then return nil;
    % Special cases.  These tests need to be generalized.
    if !*TeX then return texpri(u,v,w)
     else if getd 'vecp and vecp u then return vecpri(u,'mat)
     else if getd 'myvecp and myvecp u then return myvecpri(u);
   % The following is a bit of a mess. "fancy" output using latex style
   % in CSL has real difficulty when given really large expressions,
   % including big matrices. To avoid that leading to malformed output
   % and crashes I detect the case where I am running under CSL, fancy
   % output mode is available and enabled and the expression to to
   % printed is "huge". In that case I temporarily switch back to
   % old fashioned output format.
    if memq('csl, lispsystem!*) and
       getd 'math!-display and
       math!-display 0 and
       outputhandler!* = 'fancy!-output and
       would!-be!-huge u then <<
       fmp!-switch nil;
       tm := t >>;
    if (x := getrtype u) and flagp(x,'sprifn) and null outputhandler!*
      then <<if null v then apply1(get(get(x,'tag),'prifn),u)
             else maprin list('setq,car v,u) >>
    else <<
      if w memq '(first only) then terpri!* t;
      v := evalvars v;
      if !*fort then <<
        fvarpri(u,v,w);
        if tm then fmp!-switch t;
        return nil>>;
      maprin if v then 'setq . aconc(v,u) else u;
      if null w or w eq 'first then <<
        if tm then fmp!-switch t;
        return nil >>
       else if not !*nat then prin2!* "$";
      terpri!*(not !*nat) >>;
    if tm then fmp!-switch t;
    return nil
   end;

end;