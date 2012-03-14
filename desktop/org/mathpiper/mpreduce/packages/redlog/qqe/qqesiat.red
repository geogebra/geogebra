% ----------------------------------------------------------------------
% $Id: qqesiat.red 81 2009-02-06 18:22:31Z thomas-sturm $
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
   fluid '(qqe_siat_rcsid!* qqe_siat_copyright!*);
   qqe_siat_rcsid!* :=
      "$Id: qqesiat.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   qqe_siat_copyright!* := "Copyright (c) 2005-2009 A. Dolzmann and T. Sturm"
>>;

module qqesiat;
% QQE simplify atomic formula. Submodule of [qqe].

procedure qqe_simplat1(f, sop);
   % QQE simplify atomic formula. [f] is an
   % atomic formula; [sop] is the boolean operator [f] occurs with or
   % [nil]. Returns a quantifier-free formula that is a
   % simplified equivalent of [f].
   <<
      if not (rel memq '(qequal qneq)) then qqe_simplbtat(f,sop)
      else if rel eq 'qequal then qqe_simplqequal(f,sop)
      else qqe_simplqneq(f,sop)
   >> where rel=qqe_op f;
   
procedure qqe_simplbtat(f, sop);
   % QQE simplify basic type atomic formula. [f] is a atomic formula
   % of basic type.
   begin scalar prepf, eta_lhs, eta_rhs, op;
      
      prepf := rl_prepat f;
      % prepf := f;
      op := qqe_op f;

      eta_lhs := qqe_eta!-in!-term qqe_arg2l prepf;
      eta_rhs := qqe_eta!-in!-term qqe_arg2r prepf;

      if eta_lhs and null eta_rhs then <<
         if op neq 'neq then return 'false
         else return 'true;
      >>
      else if eta_rhs and null eta_lhs then <<
         if op neq 'neq then return 'false
         else return 'true;
      >>
      else if eta_lhs and eta_rhs then << 
         if qqe_op f eq 'equal then return 'true
         else return 'false;
      >>
      else return rl_simpat prepf; 
   end;

procedure qqe_eta!-in!-term(term);
   % QQE eta in term. [term] is a term of basic type. Return [t] if
   % the simplified term contains a term (tail qepsilon), where tail
   % is ltail or rtail.
   begin scalar eta_in, x;
      eta_in := nil;
      if atom term then return nil
      else if qqe_op term memq '(lhead rhead) then 
         return qqe_eta!-in!-term1 term %!!!
      else <<
         x := cdr term;
         while x and null eta_in do
         <<
            % prin2t car x;
            if not atom car x then <<
               if qqe_op car x memq '(lhead rhead) then
                  eta_in := qqe_eta!-in!-term1(car x) 
               %else if qqe_op car x eq 'plus then
               %   car x := qqe_simplterm!-plus car x;
               else eta_in := qqe_eta!-in!-term car x;
            >>;
            
            x := cdr x;
         >>;
         return eta_in;
      >>;
               
   end;

procedure qqe_eta!-in!-term1(term);
   begin scalar arg;
      arg := qqe_simplterm cadr term;
      term := qqe_op term . arg;
      if arg eq 'qepsilon then return t
      else return nil;
   end;

procedure qqe_simplqequal(f, sop);
   % QQE simplify atomic formula with qequal. [f] is a atomic formula
   % with qequal. Returns simplified formula.
   begin scalar lhs,rhs, varlhs, varrhs, noal, noar, notl, notr, rhsnew,
      lhsnew;
      rhs := qqe_arg2r f;
      lhs := qqe_arg2l f;

      if rhs = lhs then return 'true;

      varlhs := qqe_qprefix!-var(lhs);
      varrhs := qqe_qprefix!-var(rhs);

      if (varlhs eq varrhs) or (varlhs eq 'qepsilon) or 
         (varrhs eq 'qepsilon)
      then <<
         noar := qqe_number!-of!-adds!-in!-qterm rhs;
         noal := qqe_number!-of!-adds!-in!-qterm lhs;
         notr := qqe_number!-of!-tails!-in!-qterm rhs;
         notl := qqe_number!-of!-tails!-in!-qterm lhs;

         if (varlhs eq varrhs) and ((noar>=notr) or (noal>=notl)) 
            and not((noar-notr) = (noal-notl)) then <<
               return 'false; >>
                          
         else if (rhs eq 'qepsilon) and (noal > notl) 
         then return 'false
            
         else if (lhs eq 'qepsilon) and (noar > notr) 
         then return 'false
         else if (rhs eq 'qepsilon) and (noar = 0 and noal = 0) then 
         <<
            
            if varlhs eq 'qepsilon then return 'true
            else lhsnew := qqe_simplterm lhs;
            return qqe_mk2('qequal,lhsnew,rhs);
         >>
         else if (lhs eq 'qepsilon) and (noar = 0 and noal = 0) then
         <<
            
            if varrhs eq 'qepsilon then return 'true
            else rhsnew := qqe_simplterm rhs;
            return qqe_mk2('qequal,lhs,rhsnew);
         >>
      >>;
      
      rhsnew := qqe_simplterm rhs;
      lhsnew := qqe_simplterm lhs;
      if (rhs = rhsnew) and (lhs = lhsnew) then 
         return qqe_mk2('qequal, lhsnew, rhsnew)
      else 
         return qqe_simplqequal(qqe_mk2('qequal, lhsnew, rhsnew),nil);

   end;

procedure qqe_simplterm(term);
   % QQE simplify term. [term] is a term of queue type. Returns
   % simplified term.
   begin scalar op;
      if atom term then return term;
      op := qqe_op term;

      if op memq '(ltail rtail) then return qqe_simplterm!-tail term
      else if op memq '(lhead rhead) 
      then return qqe_simplterm!-head term
      else if op memq '(ladd radd) 
      then return qqe_simplterm!-add term
      else return term;
   end;

procedure qqe_simplterm!-add(term);
   % QQE simplify term with leading operation ladd or radd. [term] is
   % a term with leading operation ladd or radd. Returns simplified term.
   begin scalar arg;
      arg := qqe_arg2r term;
      if atom arg then return term
      else
      <<
         if arg=argnew then return term  
            % !!!qqe_mk2(op,qqe_arg2l term, argnew)
         else return qqe_simplterm qqe_mk2(op,qqe_arg2l term, argnew)
      >> where argnew=qqe_simplterm arg, op=qqe_op term;
   end;

procedure qqe_simplterm!-tail(term);
   % QQE simplify term with leading operation ltail or rtail. [term]
   % is term with leading operation ltail or rtail. Returns simplified
   % term.
   begin scalar arg, op, oparg;
      arg := qqe_arg2l term;
      if arg eq 'qepsilon then return 'qepsilon
      else if atom arg then return term;
      op := qqe_op term;
      oparg := qqe_op arg;
      if oparg memq '(ladd radd) then
      <<
         if (arg2rarg eq 'qepsilon) then return 'qepsilon
         else if op eq 'ltail and oparg eq 'radd then 
            return arg2rarg
         else if op eq 'rtail and oparg eq 'ladd then
            return arg2rarg      
      >> where arg2rarg=qqe_arg2r arg;
      <<
         if argnew = arg then return term % !!!{op,argnew}
         else return qqe_simplterm {op,argnew}
      >> where argnew=qqe_simplterm arg;
   end;

procedure qqe_simplterm!-head(term);
   % QQE simplify term with leading operation lhead or rhead. [term]
   % is a term with leading lhead or rhead. Return simplified term.
   begin scalar arg;
      arg := qqe_arg2l term;
      if atom arg then return term
      else if (qqe_op arg memq '(ladd radd))
         and (qqe_arg2r arg eq 'qepsilon)
      then return qqe_arg2l arg
      else
      <<
         if argnew = arg then return term 
         else return qqe_simplterm {op,argnew}
      >> where argnew=qqe_simplterm arg, op=qqe_op term;
   end;

procedure qqe_number!-of!-adds!-in!-qterm(term);
   % QQE number of adds in qterm. Counts the number of ladds and
   % radds in a term [term] of type queue.
   if atom term then 0
   else if qqe_op term memq '(ladd radd) 
   then 1 + qqe_number!-of!-adds!-in!-qterm qqe_arg2r term
   else qqe_number!-of!-adds!-in!-qterm qqe_arg2l term;

procedure qqe_number!-of!-tails!-in!-qterm(term);
   % QQE number of adds in qterm. Counts the number of ltails and
   % rtails in a term [term] of type queue.
   if atom term then 0
   else if qqe_op term memq '(ladd radd) then
      qqe_number!-of!-tails!-in!-qterm qqe_arg2r term
   else 1 + qqe_number!-of!-tails!-in!-qterm qqe_arg2l term;

procedure qqe_simplqneq(f, sop);
   % QQE simplify atomic formula with qneq. [f] is a atomic formula
   % with qneq. Returns simplified formula.
   begin scalar op, lhs, rhs, negu;
      op := car f;
      lhs := cadr f;
      rhs := caddr f;
      negu := qqe_simplqequal(qqe_mk2('qequal, lhs, rhs),nil);
      if negu eq 'true then f := 'false
      else if negu eq 'false then f := 'true
      else f := qqe_mk2('qneq, qqe_arg2l negu, qqe_arg2r negu);
      
      return f;
   end;

procedure qqe_simpl_standardizeqterm(term);
   %TODO soon going to be obsolete
   begin scalar x;
      x := term;
      while not atom x do
      <<
         car x := 'ltail;
         x := car cdr x;
      >>;
      return term;
   end;

endmodule;  % [qqesiat]

end;  % of file


%% ::: TODO : Other simplifications

%% lhead^l_1(q) == 'qepsilon or/and lhead^l_2(q) == 'qepsilon <->
%% lhead^l_1(q) == 'qespilon with l_1 >/< l_2

%% equal and eta ... neq and eta

%% (qneq (ltail (rtail (rtail (rtail q)))) (ltail
%% (ltail (ladd x140 (ladd x130 (ladd x120 (ladd x110
%% (ladd x100 (ladd x90 (ladd x80 (ladd x70 (ladd x60
%% (ladd x50 (ladd x40 (ladd x30 (ladd x20 (ladd x17 qepsilon)))))))))))))))))
%%    a2:  or
%% qqe_simplat1 = (qneq (ltail (rtail (rtail (rtail q))))
%% (ltail (ltail (ladd x140 (ladd x130 (ladd x120 (ladd x110
%% (ladd x100 (ladd x90 (ladd x80 (ladd x70 (ladd x60
%% (ladd x50 (ladd x40 (ladd x30 (ladd x20 (ladd x17 qepsilon))))))))))))))))) 
%%    AND length q > 16 !!! (maybe as pre-simplification !) 
