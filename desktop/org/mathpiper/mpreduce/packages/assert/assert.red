% ----------------------------------------------------------------------
% $Id: assert.red 1855 2012-11-26 13:09:21Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2010 Thomas Sturm
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
%

lisp <<
   fluid '(assert_rcsid!* assert_copyright!*);
   assert_rcsid!* := "$Id: assert.red 1855 2012-11-26 13:09:21Z thomas-sturm $";
   assert_copyright!* := "(c) 2010 T. Sturm"
>>;

module assert;

create!-package('(assert assertcheckfn),nil);

global '(assert_functionl!* exlist !*comp);

fluid '(lispsystem!* !*msg assertstatistics!*);

switch assert,assertbreak,assertstatistics;

% The switch assert is a hook to make all stats introduced here return nil thus
% turning them into comments. Note that even when it is on, structs and
% assertions only modify property lists but do not change the behaviour of the
% system unless assert_install or assert_install_all is used. I thus switch it
% on by default for now.
on1 'assert;

off1 'assertbreak;
on1 'assertstatistics;

%% macro procedure assert_check(l);
%%    begin scalar f,origfn,progn,argl,w,w1,w2,w3,w4,w5,de,msg,code; integer n;
%%       f := cadr l;
%%       n := length cdr caddr l;
%%       if (w := get(f,'number!-of!-args)) and not eqn(w,n) then
%% 	 rederr {"bad number of args in ",l};
%%       origfn := get(f,'assert_origfn);
%%       if not origfn then  <<
%% 	 origfn := intern gensym();
%% 	 progn := {'copyd,mkquote origfn,mkquote f} . progn;
%%       	 progn := {'put,mkquote f,''assert_origfn,mkquote origfn} . progn
%%       >>;
%%       argl := for i := 1:n collect mkid('a,i);
%%       w1 := mkquote f;
%%       w2 := mkquote origfn;
%%       w3 := 'list . argl;
%%       w4 := 'list . for each fn in cdr caddr l collect mkquote fn;
%%       w5 := mkquote cadddr l;
%%       de := {'de,f,argl,{'assert_check1,w1,w2,w3,w4,w5}};
%%       progn := {{'lambda,'(!*comp),de},t} . progn;
%%       progn := 'progn . reversip progn;
%%       msg := {'list,mkquote f,"is not an expr procedure - ignoring assert"};
%%       code := {'cond,
%% 	 {{'not,{'eqcar,{'getd,mkquote f},''expr}},{'lprim,msg}},
%% 	 {t,progn}};
%%       return code
%%    end;

procedure assert_check1(fn,origfn,argl,argtypel,restype);
   % This is the wrapper code executed when an insertion is installed.
   % fn is the name of the original function; origfn is an identifier
   % having the original function as its function value; argl is the
   % list of arguments passed; argtypel is a list of types asserted for
   % the arguments in argl; restype is the type asserted for the result
   % of the function call. Depending on the swith !*assertstatistics,
   % there is statictical information added to the fluid
   % assertstatistics!*, which is output and deleted when calling
   % assert_analyze().
   begin scalar cfn,w,res,scargtypel,bad; integer n;
      if !*assertstatistics then <<
      	 w := atsoc(fn,assertstatistics!*);
      	 if w then
 	    cadr w := cadr w + 1
      	 else
 	    assertstatistics!* := (fn . {1,0,0}) . assertstatistics!*
      >>;
      scargtypel := argtypel;
      for each a in argl do <<
	 n := n + 1;
	 if (cfn := get(car scargtypel,'assert_checkfn))
 	    and not apply(cfn,{a})
	    and not(pairp a and flagp(car a,'assert_ignore))
 	 then <<
	    bad := t;
	    assert_error(fn,argtypel,restype,n,car scargtypel,a)
	 >>;
	 scargtypel := cdr scargtypel
      >>;
      res := apply(origfn,argl);
      if (cfn := get(restype,'assert_checkfn))
	 and not apply(cfn,{res})
	 and not(pairp res and flagp(car res,'assert_ignore))
      then <<
	 bad := t;
	 assert_error(fn,argtypel,restype,0,restype,res)
      >>;
      if !*assertstatistics and bad then <<
      	 w := cdr atsoc(fn,assertstatistics!*);
	 cadr w := cadr w + 1
      >>;
      return res
   end;

procedure assert_error(fn,argtypel,restype,typeno,type,arg);
   % Subroutine of assert_check1 called in case of an assertion
   % violation. fn is the name of the original function; argtypel is a
   % list of types asserted for the arguments of the function call;
   % restype is the type asserted for the result of the function call;
   % typeno is an integer denoting which argument has violated an
   % assertion, where 0 stands for the result; type is the asserted type
   % for arg; arg is the argument violating an assertion. Depending on
   % the switch !*assertbreak, either the computation is interrupted
   % with a rederr or computation continues and the error
   % message is printed as a warning. In the latter case lprim is used,
   % which is controlled by the switch !*msg.
   begin scalar w,msg,!*lower;
      if !*assertstatistics then <<
      	 w := cdr atsoc(fn,assertstatistics!*);
	 caddr w := caddr w + 1
      >>;
      msg := if eqn(typeno,0) then
%	 {"result of",fn,"invalid as",type,":",arg}
	 {"declaration",assert_format(fn,argtypel,restype),
	    "violated by result",arg}
      else
%	 {"argument",typeno,"of",fn,"invalid as",type,":",arg};
	 {"declaration",assert_format(fn,argtypel,restype),
	    "violated by",mkid('arg,typeno),arg};
      if !*assertbreak then
	 rederr msg
      else
	 lprim msg
   end;

procedure assert_format(fn,argtypel,restype);
   % fn is the original function name; argtypel is the list of types
   % asserted for the arguments; restype is the type asserted for the
   % result. Reconstructs the assertion as a identifier for printing in
   % diagnostic messages.
   begin scalar ass;
      ass := explode restype;
      ass := '!! . '!) . '!! . '! . '!! . '!- . '!! . '!> . '!! . '! . ass;
      for each a in reverse argtypel do
	 ass := '!! . '!, . nconc(explode a,ass);
      ass := cddr ass;
      ass := '!! . '!: . '!! . '! . '!! . '!( . ass;
      ass := nconc(explode fn,ass);
      return compress ass
   end;

procedure assert_structstat();
   % The parser for struct. Returns a form that stores the type
   % checking function on the property list of the type.
   begin scalar type,cfn;
      type := scan();
      scan();
      if flagp(cursym!*,'delim) then <<
	 if not !*assert then
	    return nil;
	 if !*msg then lprim {"struct",type,"is not checked"};
      	 return nil
      >>;
      if cursym!* neq 'checked then
	 rederr {"expecting 'checked by' in struct but found",cursym!*};
      if scan() neq 'by then
	 rederr {"expecting 'by' in struct but found",cursym!*};
      cfn := scan();
      if not flagp(scan(),'delim) then
	 rederr {"expecting end of struct but found",cursym!*};
      if not !*assert then
	 return nil;
      return {'put,mkquote type,''assert_checkfn,mkquote cfn}
   end;

put('struct,'stat,'assert_structstat);

operator assert_analyze;

procedure assert_analyze();
   % Print and delete the statistical information collected in the fluid
   % assertstatistics!*. This works in both algebraic and symbolic mode.
   begin scalar headline,footline; integer s1,s2,s3;
      assertstatistics!* := sort(assertstatistics!*,
	 function(lambda x,y; ordp(car y,car x)));
      for each pr in assertstatistics!* do <<
	 s1 := s1 + cadr pr;
	 s2 := s2 + caddr pr;
	 s3 := s3 + cadddr pr
      >>;
      headline := '(function . (!#calls  !#bad! calls !#assertion! violations));
      footline := 'SUM . {s1,s2,s3};
      assertstatistics!* := nil . headline . nil .
	 reversip(nil . footline . nil . assertstatistics!*);
      for each pr in assertstatistics!* do <<
	 if pr then <<
	    prin2 car pr;
	    for i := length explode2 car pr + length explode2 cadr pr : 23 do
 	       prin2 " ";
	    prin2 cadr pr;
	    for i := length explode2 caddr pr : 23 do prin2 " ";
	    prin2 caddr pr;
	    for i := length explode2 cadddr pr : 23 do prin2 " ";
	    prin2t cadddr pr
	 >> else <<
	    for i := 1:72 do prin2 "-";
	    terpri()
	 >>
      >>;
      assertstatistics!* := nil
   end;

%% procedure assert_stat();
%%    begin scalar fn,argtypel,restype;
%%       fn := scan();
%%       if scan() neq '!*colon!* then
%% 	 rederr {"expecting ':' in assert but found",cursym!*};
%%       argtypel := assert_stat1();
%%       if scan() neq 'difference or scan() neq 'greaterp then
%% 	 rederr {"expecting '->' in assert but found",cursym!*};
%%       restype := scan();
%%       if not flagp(scan(),'delim) then
%% 	 rederr {"expecting end of assert but found",cursym!*};
%%       if not !*assertcheck then
%% 	 return nil;
%%       return {'assert_check,fn,'list . argtypel,restype}
%%    end;

procedure assert_declarestat();
   % The parser for assert. Returns forms that define a suitable wrapper
   % function, store relevant information on the property list of the
   % original function, and add the original function to the global list
   % assert_functionl!*.
   begin scalar l,fnx,progn,assertfn,noassertfn,argl,w1,w2,w3,w4,w4,w5;
      integer i;
      l := assert_stat!-parse();
      if not !*assert then
 	    return nil;
      fnx := explode car l;
      assertfn := intern compress nconc(explode 'assert!:,fnx);
      noassertfn := intern compress nconc(explode 'noassert!:,fnx);
      argl := for each x in cadr l collect mkid('a,i := i + 1);
      w1 := mkquote car l;
      w2 := mkquote noassertfn;
      w3 := 'list . argl;
      w4 := 'list . for each fn in cadr l collect mkquote fn;
      w5 := mkquote caddr l;
      progn := {'de,assertfn,argl,{'assert_check1,w1,w2,w3,w4,w5}} . progn;
      progn := {'put,w1,''assert_assertfn,mkquote assertfn} . progn;
      progn := {'put,w1,''assert_noassertfn,w2} . progn;
      progn := {'put,w1,''assert_installed,nil} . progn;
      progn := {'cond,{
	 {'not,{'member,w1,'assert_functionl!*}},
	 {'setq,'assert_functionl!*,{'cons,w1,'assert_functionl!*}}}} . progn;
      return 'progn . reversip progn
   end;

procedure assert_stat!-parse();
   % Subroutine of assert_stat(). This is the actual parsing code.
   begin scalar fn,argtypel,restype;
      fn := scan();
      if scan() neq '!*colon!* then
	 rederr {"expecting ':' in assert but found",cursym!*};
      argtypel := assert_stat1();
      if scan() neq 'difference or scan() neq 'greaterp then
	 rederr {"expecting '->' in assert but found",cursym!*};
      restype := scan();
      if not flagp(scan(),'delim) then
	 rederr {"expecting end of assert but found",cursym!*};
      return {fn,argtypel,restype}
   end;

procedure assert_stat1();
   % Subroutine of assert_stat!-parse. Parses the tuple of argument
   % types left of the arrow.
   begin scalar argtypel;
      if scan() neq '!*lpar!* then
	 rederr {"expecting '(' in assert but found",cursym!*};
      if scan() eq '!*rpar!* then
	 return nil;
      repeat <<
	 argtypel := cursym!* . argtypel;
	 scan();
      	 if cursym!* neq '!*comma!* and cursym!* neq '!*rpar!* then
	    rederr {"expecting ',' or ')' in assert but found",cursym!*};
	 if cursym!* eq '!*comma!* then
	    scan()
      >> until cursym!* eq '!*rpar!*;
      return reversip argtypel
   end;

put('declare,'stat,'assert_declarestat);

procedure assert_install(fnl);
   % This is parsed as stat rlis, i.e., it takes a comma-separated list
   % fnl of arbirary length of arguments w/o parentesis. fnl is list of
   % identifiers that are functions for which an existing assertion is
   % installed.
   if !*assert then
      for each fn in fnl do assert_install1 fn;

put('assert_install,'stat,'rlis);

procedure assert_install1(fn);
   % fn is an identifier that is a single function for which an existing
   % assertion is installed.
   if get(fn,'assert_installed) then
      lprim {"assert already installed for",fn}
   else if not eqcar(getd fn,'expr) then
      lprim {fn,"is not an expr procedure - ignoring assert"}
   else <<
      copyd(get(fn,'assert_noassertfn),fn);
      copyd(fn,get(fn,'assert_assertfn));
      put(fn,'assert_installed,t)
   >>;

procedure assert_uninstall(fnl);
   % This is parsed as stat rlis, i.e., it takes a comma-separated list
   % fnl of arbirary length of arguments w/o parentesis. fnl is list of
   % identifiers that are functions for which an installed assertion is
   % uninstalled.
   if !*assert then
      for each fn in fnl do assert_uninstall1 fn;

put('assert_uninstall,'stat,'rlis);

procedure assert_uninstall1(fn);
   % fn is an identifier that is a single function for which an
   % installed assertion is uninstalled.
   if not get(fn,'assert_installed) then
      lprim {"assert not installed for",fn}
   else <<
      copyd(fn,get(fn,'assert_noassertfn));
      put(fn,'assert_installed,nil)
   >>;

procedure assert_install_all();
   % This is parsed as stat endstat, i.e., it takes no arguments but
   % also no empty pair of parenthesis. Installs assertions for the
   % functions in the global list assert_functionl!* of all functions
   % for which there are assertions defined.
   if !*assert then
      assert_install assert_functionl!*;

put('assert_install_all,'stat,'endstat);

procedure assert_uninstall_all();
   % This is parsed as stat endstat, i.e., it takes no arguments but
   % also no empty pair of parenthesis. Uninstalls assertions for the
   % functions in the global list assert_functionl!* of all functions
   % for which ther are assertions defined.
   if !*assert then
      assert_uninstall assert_functionl!*;

put('assert_uninstall_all,'stat,'endstat);

symbolic procedure formassert(u,vars,mode);
   if !*assert then
      assert_assert(cadr u,vars,mode);

put('assert, 'formfn, 'formassert);

procedure assert_assert(u, vars, mode);
   if mode eq 'symbolic then
      {'cond,
 	 {{'not, {'eval, u}},
 	    {'progn,
 	       {'backtrace},
	       {'cond,
 	       	  {'!*assertbreak,
 	       	     {'rederr, {'list, "failed assertion", mkquote u}}},
	       	  {t,
 	       	     {'lprim, {'list, "failed assertion", mkquote u}}}}}}};

endmodule;  % assert

end;  % of file
