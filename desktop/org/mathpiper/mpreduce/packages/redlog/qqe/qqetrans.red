% ----------------------------------------------------------------------
% $Id: qqetrans.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2005-2009 Andreas Dolzmann and Thomas Sturm
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
   fluid '(qqe_trans_rcsid!* qqe_trans_copyright!*);
   qqe_trans_rcsid!* :=
      "$Id: qqetrans.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   qqe_trans_copyright!* := "Copyright (c) 2005-2009 A. Dolzmann and T. Sturm"
>>;

module qqetrans;
% Queue quantor elimination translation module. Submodule of [qqe].

exports qqe_la2lth;

procedure qqe_la2lth(u);
   % qqe language with ladds and radds to language with only heads and
   % tails. [u] is a S-expression, it is supposed to be a formula.
   % Function recursively transforms a formula into an equivalent one 
   % with only heads and tails. Function calls for subroutines for
   % atomic formula or for non atomic formula are performed.
   begin scalar f;
      % non atomic formula
      if qqe_debug!* then prin2t":::entering qqe_la2lth";
      if atom u then return u;
      f := rl_simpl(u,nil,-1);
      if atom f then return f;
      if qqe_debug!* then prin2t{"simplificated formula is", f};
      if rl_cxp car f then return qqe_la2lth1 f
      % atomic formula
      else return qqe_la2lth!-at f;
   end;

procedure qqe_la2lth1(u);
   % qqe language with ladds and radds to language with only heads and
   % tails. [u] is a S-expression, it is supposed to be a formula.
   % Function recursively transforms a formula into an equivalent one 
   % with only heads and tails. Function calls for subroutines for
   % atomic formula or for non atomic formula are performed.
   car u . for each x in cdr u collect 
      if qqe_qadd!-insidef x  then qqe_la2lth x
      else x;

procedure qqe_la2lth!-at(u);
   % qqe language with ladds and radds to language with only heads and
   % tails for atomic formulas. [u] is a S-expression, it is supposed to 
   % be an atomic formula. The most outer ladd or radd is transformed 
   % into equivalent form without ladd, radd. 
   begin scalar lhs, rhs, op, x, prepu;
      
      prepu := rl_prepat u;
      lhs := qqe_arg2l prepu;
      rhs := qqe_arg2r prepu;
      op := qqe_op prepu;
      x := nil;

      if qqe_qadd!-inside lhs then 
      <<
         u := qqe_la2lth!-at1 prepu;
         x := t;
      >>
      else if qqe_qadd!-inside rhs then 
      <<
         u := qqe_la2lth!-at1 qqe_mk2(op,rhs,lhs); 
         x := t;
      >>;

      if x then u := qqe_la2lth u;
      
      return u;
   end;

procedure qqe_la2lth!-at1(u);
   % qqe language with ladds and radds to language with only heads and
   % tails for atomic formulas. [u] is a S-expression, it is supposed to 
   % be an atomic formula in lisp prefix.
   if (qqe_qopaddp caadr u) then qqe_la2lth!-addout u
   else qqe_la2lth!-addin u;
      
procedure qqe_la2lth!-addout(u);
   % qqe language with ladds and radds to language with only heads and
   % tails with add is most outer function of the lhs term of an atomic 
   % formula. [u] is a S-expression, it is supposed to be a atomic 
   % formula in lisp prefix.
   begin scalar op, lhs, rhs, rhsadd, lhsadd, opadd;
      op := qqe_op u;
      lhs := qqe_arg2l u;
      rhs := qqe_arg2r u; 
      lhsadd := qqe_arg2l lhs;
      rhsadd := qqe_arg2r lhs;
      opadd := qqe_op lhs;

      if opadd = 'ladd then
      <<
         if op eq 'qequal then
            return {'and, qqe_mk2('qneq,rhs,'qepsilon),
               rl_simpat(qqe_mk2('equal,{'lhead,copy rhs},lhsadd)),
               qqe_mk2('qequal,{'rtail,rhs},rhsadd)}
         else return {'or, qqe_mk2('qequal,rhs,'qepsilon),
            {'and, rl_simpat(qqe_mk2('neq,{'lhead, copy rhs},lhsadd)),
               qqe_mk2('qneq,copy rhs,'qepsilon)},
            qqe_mk2('qneq,{'rtail,copy rhs},rhsadd)};
      >>
      else if opadd = 'radd then
      <<
         if op eq 'qequal then
            return {'and, qqe_mk2('qneq, rhs, 'qepsilon),
               rl_simpat(qqe_mk2('equal,{'rhead,rhs},lhsadd)),
               qqe_mk2('qequal,{'ltail,copy rhs},rhsadd)}
         else return {'or, qqe_mk2('qequal,rhs,'qepsilon),
            {'and, rl_simpat(qqe_mk2('neq,{'rhead,copy rhs},lhsadd)),
               qqe_mk2('qneq,rhs,'qepsilon)},
            qqe_mk2('qneq,{'ltail,copy rhs},rhsadd)};
      >>
            
   end;


procedure qqe_la2lth!-addin(u);
   % qqe language with ladds and radds to language with only heads and
   % tails with add is an inside function of the lhs term of an atomic 
   % formula. [u] is a S-expression, it is supposed to be a atomic 
   % formula in lisp prefix.
   begin scalar lhs, preadd, preaddop, opadd, at_type;
      at_type := qqe_op u;
      lhs := qqe_arg2l u;

      qqe_reset!-qadd!-location();
      qqe_qadd!-inside lhs;  %% should become obsolete
                            %% at the moment still needed

      % first if lhs has more then one argument, we should locate the
      % "right" subterm of lhs!!! compare example: %% (greaterp
      % ((((lhead q) . 1) . 1) (( %% (rhead (radd x10 (radd x9 (radd
      % x8 (radd x7 (ladd x6 %% (ladd x5 (ladd x4 (ladd x3 (ladd x2
      % (ladd x1 (ltail %% (ltail (ltail q)))))))))))))) . 1) . 1)
      % (((rhead m) . 1) . -1) %% ((x . 1) . 1)) nil)

      preadd := qqe_qadd!-inside!-relocate lhs;
      preaddop := qqe_op preadd;

      if qqe_debug!* then 
         prin2t{"preadd=",preadd,"preaddop=",preaddop, 
            "at_type = ", at_type};
      % pause;

      opadd := qqe_op qqe_arg2l preadd;
      
      if opadd = 'ladd then
      <<
         if preaddop = 'lhead then
            u := qqe_la2lth!-addin!-laddlhead u
         else if preaddop = 'ltail then
            u := qqe_la2lth!-addin!-laddltail(u,at_type)
         else if preaddop = 'rhead then
            u := qqe_la2lth!-addin!-laddrhead(u,at_type)
         else if preaddop = 'rtail then
            u := qqe_la2lth!-addin!-laddrtail(u,at_type);
         % else rederr("qqe_la2lth expected something else");
      >>
      else if opadd = 'radd then
      <<
         if preaddop = 'lhead then
            u := qqe_la2lth!-addin!-raddlhead(u,at_type)
         else if preaddop = 'ltail then
            u := qqe_la2lth!-addin!-raddltail(u,at_type)
         else if preaddop = 'rhead then
            u := qqe_la2lth!-addin!-raddrhead u
         else if preaddop = 'rtail then
            u := qqe_la2lth!-addin!-raddrtail(u,at_type);
         % else rederr("qqe_la2lth expected something else");
      >>;
      % else rederr("qqe_la2lth expected something else");
      % prin2t{"end with u=", u}; pause;
      return u;
   end;
      

procedure qqe_la2lth!-addin!-laddlhead(u);
   % qqe language with ladds and radds to language with only heads and
   % tails with add is an inside function of the lhs term of an atomic 
   % formula. [u] is a S-expression, it is supposed to be a atomic 
   % formula. This is a subroutine for the case the the predecessor
   % function to radd or ladd is lhead. 
   begin scalar lhs, lhsadd, prepreadd;
      lhs := qqe_arg2l u;
       
      lhsadd := cadr cadr qqe_qadd!-inside!-relocate lhs;;

      prepreadd := qqe_qadd!-inside!-relocate!-2up lhs;
       
      cadr prepreadd := lhsadd;

      return rl_simpat u;
   end;

procedure qqe_la2lth!-addin!-laddrhead(u,op);
   % qqe language with ladds and radds to language with only heads and
   % tails with add is an inside function of the lhs term of an atomic 
   % formula. [u] is a S-expression, it is supposed to be a atomic 
   % formula. This is a subroutine for the case the the predecessor
   % function to radd or ladd is rhead. 
   begin scalar list, cu, prepreadd, preadd, preadd_cu,
         lhsadd, rhsadd;
      preadd := qqe_qadd!-inside!-relocate cadr u;
      cu := copy u;
      lhsadd := cadr cadr preadd;
      if atom cddr cadr preadd then rhsadd := cddr cadr preadd 
      else rhsadd := caddr cadr preadd;
          
      if cadr u neq preadd then
      <<
         prepreadd := qqe_qadd!-inside!-relocate!-2up cadr u;
         preadd_cu := qqe_qadd!-inside!-relocate cadr cu;
         cadr preadd_cu := copy rhsadd;
         cadr prepreadd := lhsadd;
      >>
      else
      <<
         cadr u := lhsadd;
         cadr cu := {'rhead, copy rhsadd};
      >>;
      
      if op neq 'neq then
         u := {'or, {'and,rl_simpat cu, 
            qqe_mk2('qneq,copy rhsadd,'qepsilon)},
            {'and,rl_simpat u,qqe_mk2('qequal,copy rhsadd,'qepsilon)}}
      else % op eq 'neq 
         u := {'and, {'or,rl_simpat cu, 
            qqe_mk2('qequal,copy rhsadd,'qepsilon)},
            {'or,rl_simpat u,qqe_mk2('qneq,copy rhsadd,'qepsilon)}};
      
      return u;
   end;

procedure qqe_la2lth!-addin!-laddltail(u,op);
   % qqe language with ladds and radds to language with only heads and
   % tails with add is an inside function of the lhs term of an atomic 
   % formula. [u] is a S-expression, it is supposed to be a atomic 
   % formula. This is a subroutine for the case the the predecessor
   % function to radd or ladd is ltail. 
   begin scalar cu, lhsadd, rhsadd, prepreadd, preadd, preadd_cu;

      preadd := qqe_qadd!-inside!-relocate cadr u;
      
      lhsadd := cadr cadr preadd;
      if atom cddr cadr preadd then rhsadd := cddr cadr preadd 
      else rhsadd := caddr cadr preadd;
      cu := copy u;
      % prin2t{"cu=",cu, preadd, rhsadd,lhsadd};
      if cadr u neq preadd then
      <<
         % prin2t "here";
         preadd_cu := qqe_qadd!-inside!-relocate cadr cu;
         prepreadd := qqe_qadd!-inside!-relocate!-2up cadr u;
         car preadd_cu := 'ladd;         
         cdr preadd_cu := copy lhsadd . {{'ltail, copy rhsadd}};
         cadr prepreadd := 'qepsilon;
      >>
      else
      <<
         % prin2t "here2";
         cadr u := 'qepsilon;
         cadr cu := qqe_mk2('ladd, copy lhsadd,{'ltail, copy rhsadd});
      >>;
      
      if op eq 'qequal then
      u := {'or, {'and,rl_simpat cu, 
         qqe_mk2('qneq,copy rhsadd,'qepsilon)},
         {'and,rl_simpat u, qqe_mk2('qequal, copy rhsadd, 'qepsilon)}}
      else % op eq 'qneq
         u := {'and, {'or,rl_simpat cu, 
            qqe_mk2('qequal,copy rhsadd,'qepsilon)},
         {'or,rl_simpat u, qqe_mk2('qneq,copy rhsadd, 'qepsilon)}};
      return u;
      
   end;

procedure qqe_la2lth!-addin!-laddrtail(u,op);
   % qqe language with ladds and radds to language with only heads and
   % tails with add is an inside function of the lhs term of an atomic 
   % formula. [u] is a S-expression, it is supposed to be a atomic 
   % formula. This is a subroutine for the case the the predecessor
   % function to radd or ladd is rtail. 
   begin scalar list, cu, preadd, prepreadd, preadd_cu, prepreadd_cu, 
         lhsadd, rhsadd;
      preadd := qqe_qadd!-inside!-relocate cadr u;
      lhsadd := cadr cadr preadd;
      if atom cddr cadr preadd then rhsadd := cddr cadr preadd 
      else rhsadd := caddr cadr preadd;
      cu := copy u;
      
      if cadr u neq preadd then
      <<
         preadd_cu := qqe_qadd!-inside!-relocate cadr cu;
         prepreadd := qqe_qadd!-inside!-relocate!-2up cadr u;
         prepreadd_cu := qqe_qadd!-inside!-relocate!-2up cadr cu;
         cadr prepreadd_cu := copy rhsadd;
         cadr prepreadd := 'qepsilon;
      >>
      else
      <<
         cadr u := 'qepsilon;
         cadr cu := copy rhsadd;
      >>;
      
      if op eq 'qequal then
         u := {'or, {'and,rl_simpat cu, 
            qqe_mk2('qneq,copy rhsadd,'qepsilon)},
            {'and,rl_simpat u, qqe_mk2('qequal, copy rhsadd, 
               'qepsilon)}}
      else % op eq 'qneq
         u := {'and, {'or,rl_simpat cu, 
            qqe_mk2('qequal,copy rhsadd,'qepsilon)},
            {'or,rl_simpat u, qqe_mk2('qneq, copy rhsadd, 'qepsilon)}};
      return u;
      
   end;

procedure qqe_la2lth!-addin!-raddlhead(u,op);
   % qqe language with ladds and radds to language with only heads and
   % tails with add is an inside function of the lhs term of an atomic 
   % formula. [u] is a S-expression, it is supposed to be a atomic 
   % formula. This is a subroutine for the case the the predecessor
   % function to radd or ladd is lhead. 
   begin scalar list, cu, preadd, prepreadd,  preadd_cu, 
         lhsadd, rhsadd;
      preadd := qqe_qadd!-inside!-relocate cadr u;
      lhsadd := cadr cadr preadd;
      if atom cddr cadr preadd then rhsadd := cddr cadr preadd 
      else rhsadd := caddr cadr preadd;
      cu := copy u;
            
      if cadr u neq preadd then
      <<
         preadd_cu := qqe_qadd!-inside!-relocate cadr cu;
         prepreadd := qqe_qadd!-inside!-relocate!-2up cadr u;
         cadr preadd_cu := copy rhsadd;
         cadr prepreadd := lhsadd;
      >>
      else
      <<
         cadr u := lhsadd;
         cadr cu := {'lhead, copy rhsadd};
      >>;
      
      if op neq 'neq then
         u := {'or, {'and,rl_simpat cu, 
            qqe_mk2('qneq,copy rhsadd,'qepsilon)},
            {'and,rl_simpat u, qqe_mk2('qequal,copy rhsadd, 
               'qepsilon)}}
      else % op eq 'neq
         u := {'and, {'or,rl_simpat cu, 
            qqe_mk2('qequal,copy rhsadd,'qepsilon)},
            {'or,rl_simpat u, qqe_mk2('qneq,copy rhsadd, 'qepsilon)}};
      return u;
   end;

procedure qqe_la2lth!-addin!-raddrhead(u);
   % qqe language with ladds and radds to language with only heads and
   % tails with add is an inside function of the lhs term of an atomic 
   % formula. [u] is a S-expression, it is supposed to be a atomic 
   % formula. This is a subroutine for the case the the predecessor
   % function to radd or ladd is rhead. 
   qqe_la2lth!-addin!-laddlhead u;

procedure qqe_la2lth!-addin!-raddltail(u,op);
   % qqe language with ladds and radds to language with only heads and
   % tails with add is an inside function of the lhs term of an atomic 
   % formula. [u] is a S-expression, it is supposed to be a atomic 
   % formula. This is a subroutine for the case the the predecessor
   % function to radd or ladd is rtail. 
   qqe_la2lth!-addin!-laddrtail(u,op);

procedure qqe_la2lth!-addin!-raddrtail(u,op);
   % qqe language with ladds and radds to language with only heads and
   % tails with add is an inside function of the lhs term of an atomic 
   % formula. [u] is a S-expression, it is supposed to be a atomic 
   % formula. This is a subroutine for the case the the predecessor
   % function to radd or ladd is rtail. 
   begin scalar list, cu, preadd_cu, preadd, prepreadd, lhsadd, rhsadd;
      preadd := qqe_qadd!-inside!-relocate cadr u;
      lhsadd := cadr cadr preadd;
      if atom cddr cadr preadd then rhsadd := cddr cadr preadd 
      else rhsadd := caddr cadr preadd;
      cu := copy u;
            
      if cadr u neq preadd then
      <<
         preadd_cu := qqe_qadd!-inside!-relocate cadr cu;
         prepreadd := qqe_qadd!-inside!-relocate!-2up cadr u;
         car preadd_cu := 'radd;
         cdr preadd_cu := copy lhsadd . {{'rtail, copy rhsadd}};
         cadr prepreadd := 'qepsilon;
      >>
      else
      <<
         cadr u := 'qepsilon;
         cadr cu := qqe_mk2('radd, copy lhsadd, {'rtail, copy rhsadd});
      >>;
      
      if op eq 'qequal then
         u := {'or, {'and,rl_simpat cu,
            qqe_mk2('qneq,copy rhsadd,'qepsilon)},
            {'and,rl_simpat u, qqe_mk2('qequal, copy rhsadd, 
               'qepsilon)}}
      else
         u := {'and, {'or,rl_simpat cu,
            qqe_mk2('qequal,copy rhsadd,'qepsilon)},
            {'or,rl_simpat u, qqe_mk2('qneq, copy rhsadd, 
               'qepsilon)}};
      
      return u;
      
   end;

% --------------------------------------------------------------------
%                qadd location

procedure qqe_reset!-qadd!-location();
   qqe_qadd!-location!* := nil;

procedure qqe_qadd!-inside!-at(u);
   % qqe queue add inside atomic formula. [u] is a S-expression,
   % it is supposed to be a atomic formula. Function 
   % checks if a ladd or radd is within the lhs or rhs of u and returns 
   % [t], if so, and [nil] if not.
   begin scalar lhs, rhs,prepu;
      
      prepu := rl_prepat u;
      lhs := qqe_arg2l prepu;

      rhs := qqe_arg2r prepu;

      if qqe_qadd!-inside lhs or qqe_qadd!-inside rhs then return t
      else return nil;
   end;

procedure qqe_qadd!-insidef(f);
   % QQE queue add inside of f. [f] is a term. procedure checks
   % recursivly if there is an appearance of [radd] or [ladd] in
   % [f]. If so, then it returns [t], else [nil].
   begin scalar p,x;
      
      if rl_cxp qqe_op f then 
      <<
         
         x := cdr f;
         while x and null p do
         <<
            p := qqe_qadd!-insidef car x or p;
            x := cdr x;
         >>;
         return p;
      >>
      else return qqe_qadd!-inside!-at f;
   end;

procedure qqe_qadd!-inside(u);
   % qqe queue add inside atomic. [u] is a S-expression,
   % it is supposed to be a term in lisp prefix. Function checks if
   % a ladd or radd is within u and returns [t],
   % if so, and [nil] if not.
   begin scalar op, notyet, x, preop, qadd_location_before;

      if null u or atom u then return nil;
      op := qqe_op u;
      qqe_qadd!-location!* := 'a . qqe_qadd!-location!*;

      if pairp u and not qqe_qopaddp op then 
         <<
            notyet := t;
            preop := op;
            x := cdr u;
            qadd_location_before := qqe_qadd!-location!*;

            while x and notyet do
               <<
                  qqe_qadd!-location!* := 'd . qadd_location_before;
                  qadd_location_before := qqe_qadd!-location!*;
                  if not atom x and pairp car x and 
                     qqe_qadd!-inside car x 
                  then notyet := nil;
                  preop := x;
                  x := cdr x;
               >>;
            % prin2t
            
            if not notyet then return preop
            else return nil;
         >>
      % else if atom op then return nil
      else if pairp u and qqe_qopaddp op then return t
      else return nil;
   end;

procedure qqe_qadd!-inside!-relocate(u);
   % qqe queue add inside relocate the first appearance of a ladd or
   % radd. [u] is a S-expression, it is supposed to be a term. 
   % qqe_qadd!-inside supposed to be executed before.
   % Function returns a term beginning with the first function in u,
   % whose argument the most outside ladd, radd of u is.
   % For example: u := lhead(ltail(radd(lhead(q),p))), then
   % ltail(radd(lhead(q),p)) is being returned. 
   % Function returns [nil] if there is no ladd, radd in u.
   begin scalar pos, rq, list;
      
      list := reverse qqe_qadd!-location!*;
      % pos := cdr qqe_qadd!-location!*;
      pos := cdr list;
      rq := u;

      while cddr pos do
      <<
         if car pos = 'a then rq := car rq
         else rq := cdr rq; % car pos = 'd
         pos := cdr pos;
      >>;
      
      return if atom car rq then rq else car rq;
     
   end;

procedure qqe_qadd!-inside!-relocate!-2up(u);
   % qqe queue add inside relocate the first appearance of a ladd or
   % radd. [u] is a S-expression, it is supposed to be a term.
   % qqe_qadd!-inside supposed to be executed before.  Function
   % returns a S-expression beginning with the first function f in u,
   % whose argument q := qqe_qadd!-inside!-relocate u is, if f is
   % unary or q is the first argument, otherwise it returns the last
   % argument of f left to q.  For example: u := (plus
   % (lhead(ltail(radd(lhead(q),p)))) y), then
   % lhead(ltail(radd(lhead(q),p))) is being returned.  Another
   % example: u := (plus y (lhead(ltail(radd(lhead(q),p))))), then (y
   % (lhead(ltail(radd(lhead(q),p))))) is returned. Function returns
   % [nil] if there is no ladd, radd in u.
   begin scalar pos_loc, pos_u;
      if length qqe_qadd!-location!* < 5 then return u;
      % prin2t qqe_qadd!-location!*;
      pos_loc := cdr reverse cddddr qqe_qadd!-location!*;
      % prin2t pos_loc;
      pos_u := u;
      for each x in pos_loc do
      <<
         if x eq 'a then pos_u := car pos_u
         else pos_u := cdr pos_u;
      >>;
      % return if atom car pos_u then pos_u else car pos_u;
      return pos_u;
   end;

endmodule;  % [qqetrans]

end;  % of file
