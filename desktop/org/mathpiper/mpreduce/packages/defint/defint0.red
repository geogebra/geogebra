module defint0;  % Rules for definite integration.

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


global '(unknown_tst product_tst transform_tst transform_lst);

transform_lst := '();

fluid '(!*precise);

global '(spec_cond);

symbolic smacro procedure mynumberp(n);

begin; if numberp n then t

else if listp n and car n = 'quotient and (numberp cadr n or
mynumberp cadr n) and (numberp caddr n or mynumberp caddr n) then 't

else if listp n and car n = 'sqrt and (numberp cadr n or cadr n = 'pi)
         then t else nil;
end;

symbolic operator mynumberp;


put('intgggg,'simpfn,'simpintgggg);

% put('defint,'psopfn,'new_defint);

symbolic procedure new_defint(lst);
   begin scalar var,result,n1,n2,n3,n4,!*precise;
      if eqcar(car lst,'times)
        then return new_defint append(cdar lst,cdr lst);
      unknown_tst := nil;
      var := nth(lst,length lst);
      if length lst = 2 and listp car lst then
              lst := test_prod(lst,var);
      transform_tst := reval algebraic(transform_tst);
      if transform_tst neq t then lst := hyperbolic_test(lst);
      for each i in lst do specfn_test(i);
      if length lst = 5 then
         <<n1 := car lst;
           n2 := cadr lst;
           n3 := caddr lst;
           n4 := cadddr lst;
           result := reval algebraic defint2(n1,n2,n3,n4,var)>>
      else if length lst = 4 then
         <<n1 := car lst;
           n2 := cadr lst;
           n3 := caddr lst;
           result := reval algebraic defint2(n1,n2,n3,var)>>
      else if length lst = 3 then
         <<n1 := car lst;
           n2 := cadr lst;
           result := reval algebraic defint2(n1,n2,var)>>
      else if length lst = 2 then
         <<n1 := car lst;
           result := reval algebraic defint2(n1,var)>>;
      algebraic(transform_tst := nil);
      if pairp result then <<for each i in result do test_unknown(i);
             % Tidy up result by ensuring that just unknown is returned
             % and not multiples of it.
            if unknown_tst then return 'UNKNOWN else return result>>
       else return result
   end;

symbolic procedure specfn_test(n);

begin;
if listp n and car n = 'times then
<< if listp caddr n and (car caddr n = 'm_gegenbauerp or
                                             car caddr n = 'm_jacobip)
    then off exp; >>;
end;

symbolic procedure test_prod(lst,var);

begin scalar temp,ls;

temp := caar lst;

if temp = 'times then
   << if listp caddar lst then

% test for special cases of Meijer G-functions of compoud functions

      << if car caddar lst neq 'm_chebyshevt and
                  car caddar lst neq 'm_chebyshevu and
            car caddar lst neq 'm_gegenbauerp and
            car caddar lst neq 'm_jacobip then
            ls := append(cdar lst,{var})

%else returned without change
         else ls := lst;>>
      else ls := append(cdar lst,{var});
   >>

else if temp = 'minus and caadar lst = 'times then
<< if length cadar lst = 3 then
      ls := {{'minus,car cdadar lst},cadr cdadar lst,var}
   else if length cadar lst = 4 then
      ls := {{'minus,car cdadar lst},cadr cdadar lst,
                                                                      caddr cdadar lst,var}>>
else ls := lst;
return ls;
end;

symbolic procedure test_unknown(n);

% A procedure to test for unknown as the result of the integration
% process

if pairp n then << for each i in n do test_unknown(i)>>
else if n = 'unknown then unknown_tst := 't;

algebraic<<
heaviside_rules :=

{ heaviside(~x) => 1 when numberp x and x >= 0,
  heaviside(~x) => 0 when numberp x and x < 0 };

let heaviside_rules;

operator defint2,defint_choose;

SHARE MELLINCOEF$

defint2_rules:=

{ defint2(~n,cos((~x*~~A)/~~C)-cos((~x*~~B)/~~D),~x) =>
                defint2(-2,n,sin((A/C+B/D)*x/2),sin((A/C-B/D)*x/2),x),
  defint2(cos((~x*~~A)/~~C)-cos((~x*~~B)/~~D),~x) =>
                defint2(-2,sin((A/C+B/D)*x/2),sin((A/C-B/D)*x/2),x),

  defint2(~b,~f1,~f2,~x) => b*defint2(f1,f2,x) when freeof (b,x),

  defint2(~~b*~f1,~~c*~f2,~x) => b*c*defint2(f1,f2,x)
         when freeof (b,x) and freeof (c,x) and not(b = 1 and c = 1),

  defint2(~b/~f1,~c/~f2,~x) => c*b*defint2(1/f1,1/f2,x)
         when freeof (b,x) and freeof (c,x) and not(b = 1 and c = 1),

  defint2(~~b*~f1,~c/~f2,~x) => c*b*defint2(f1,1/f2,x)
         when freeof (b,x) and freeof (c,x) and not(b = 1 and c = 1),

  defint2(~b/~f1,~~c*~f2,~x) => c*b*defint2(1/f1,f2,x)
         when freeof (b,x) and freeof (c,x) and not(b = 1 and c = 1),

  defint2(~f1/~~b,~~c*~f2,~x) => c/b*defint2(f1,f2,x)
         when freeof (b,x) and freeof (c,x) and not(b = 1 and c = 1),

  defint2(~b/~f1,~x) => b*defint2(1/f1,x)
        when freeof (b,x) and not(b = 1),

  defint2(~~b*~f1,~x) => b*defint2(f1,x)
        when freeof (b,x) and not(b = 1),

  defint2(~f1/~~b,~x) => 1/b*defint2(f1,x)
        when freeof (b,x) and not(b = 1),

  defint2((~f2+ ~~f1)/~~f3,~x) => defint2(f2/f3,x) + defint2(f1/f3,x)
                        when not(f1=0),
  defint2(-~f1,~x) =>  - defint2(f1,x),
  defint2((~f2+ ~~f1)/~~f3,~n,~x) =>
                 defint2(f2/f3,n,x) + defint2(f1/f3,n,x)
                when not(f1=0),
  defint2(-~f1,~n,~x) =>  - defint2(f1,n,x),
  defint2(~n,(~f2+ ~~f1)/~~f3,~x) =>
                 defint2(n,f2/f3,x) + defint2(n,f1/f3,x)
                when not(f1=0),
  defint2(~n,-~f1,~x) =>  - defint2(n,f1,x),
  defint2(~n,(~f2+ ~~f1)/~~f3,~nn,~x) =>
                 defint2(n,f2/f3,nn,x) + defint2(n,f1/f3,nn,x)
                when not(f1=0),
  defint2(~n,-~f1,~nn,~x) =>  - defint2(n,f1,nn,x),
  defint2(~n,~nn,(~f2+ ~~f1)/~~f3,~x) =>
                 defint2(n,nn,f2/f3,x) + defint2(n,nn,f1/f3,x)
                when not(f1=0),
  defint2(~n,~nn,-~f1,~x) =>  - defint2(n,nn,f1,x),

  defint2(~n,~x^~a,~f1,~f2,~x) =>
                            n*intgggg(defint_choose(f1,x),defint_choose(f2,x),a,x)
   when numberp n ,

  defint2(~n,~x,~f1,~f2,~x) =>
                 n*intgggg(defint_choose(f1,x),defint_choose(f2,x),1,x)
   when numberp n ,
  defint2(~n,1/~x^~~a,~f1,~f2,~x) =>
                            n*intgggg(defint_choose(f1,x),defint_choose(f2,x),-a,x)
   when numberp n ,
  defint2(~n,1/~x,~f1,~f2,~x) =>
                           n*intgggg(defint_choose(f1,x),defint_choose(f2,x),-1,x)
   when numberp n ,
  defint2(~n,sqrt(~x),~f1,~f2,~x) =>
                           n*intgggg(defint_choose(f1,x),defint_choose(f2,x),1/2,x)
   when numberp n ,
  defint2(~n,sqrt(~x)*~x,~f1,~f2,~x) =>
                           n*intgggg(defint_choose(f1,x),defint_choose(f2,x),3/2,x)
   when numberp n ,
  defint2(~n,sqrt(~x)*~x^~a,~f1,~f2,~x) =>
              n*intgggg(defint_choose(f1,x),defint_choose(f2,x),1/2+a,x)
   when numberp n ,
  defint2(~n,1/sqrt(~x),~f1,~f2,~x) =>
               n*intgggg(defint_choose(f1,x),defint_choose(f2,x),-1/2,x)
   when numberp n ,
  defint2(~n,1/(sqrt(~x)*~x),~f1,~f2,~x) =>
               n*intgggg(defint_choose(f1,x),defint_choose(f2,x),-3/2,x)
   when numberp n ,
  defint2(~n,1/(sqrt(~x)*~x^~a),~f1,~f2,~x) =>
             n*intgggg(defint_choose(f1,x),defint_choose(f2,x),-1/2-a,x)
   when numberp n ,

  defint2(~n,1/~x,~f1,~x) => n*intgggg(defint_choose(f1,x),0,-1,x)
   when numberp n ,
  defint2(~n,1/~x^(~a),~f1,~x) => n*intgggg(defint_choose(f1,x),0,-a,x)
   when numberp n ,
  defint2(~n,1/sqrt(~x),~f1,~x) =>
                 n*intgggg(defint_choose(f1,x),0,-1/2,x) when numberp n,
  defint2(~n,1/(sqrt(~x)*~x),~f1,~x) =>
                            n*intgggg(defint_choose(f1,x),0,-3/2,x)
   when numberp n ,
  defint2(~n,1/(sqrt(~x)*~x^~a),~f1,~x) =>
                            n*intgggg(defint_choose(f1,x),0,-1/2-a,x)
   when numberp n ,
  defint2(~n,~x**(~a),~f1,~x) => n*intgggg(defint_choose(f1,x),0,a,x)
   when numberp n ,
  defint2(~n,~x,~f1,~x) => n*intgggg(defint_choose(f1,x),0,1,x)
   when numberp n ,
  defint2(~n,sqrt(~x),~f1,~x) => n*intgggg(defint_choose(f1,x),0,1/2,x)
    when numberp n ,
 defint2(~n,sqrt(~x)*~x,~f1,~x) =>
    n*intgggg(defint_choose(f1,x),0,3/2,x)
   when numberp n ,
  defint2(~n,sqrt(~x)*~x^~a,~f1,~x) =>
                           n*intgggg(defint_choose(f1,x),0,1/2+a,x)
   when numberp n ,

  defint2(~~b*~x^~~a/~~c,~f1,~f2,~x) =>
        b/c*intgggg(defint_choose(f1,x),defint_choose(f2,x),a,x)
                        when freeof(b,x) and freeof (c,x),
  defint2(~b/(~~c*~x^~~a),~f1,~f2,~x) =>
        b/c*intgggg(defint_choose(f1,x),defint_choose(f2,x),-a,x)
                        when freeof(b,x) and freeof(c,x),
  defint2(sqrt(~x),~f1,~f2,~x) =>
                     intgggg(defint_choose(f1,x),defint_choose(f2,x),1/2,x),
  defint2(sqrt(~x)*~x^~~a,~f1,~f2,~x) =>
               intgggg(defint_choose(f1,x),defint_choose(f2,x),1/2+a,x),
  defint2(~b/(~~c*sqrt(~x)),~f1,~f2,~x) =>
            b/c*intgggg(defint_choose(f1,x),defint_choose(f2,x),-1/2,x),
  defint2(1/(sqrt(~x)*~x^~~a),~f1,~f2,~x) =>
      intgggg(defint_choose(f1,x),defint_choose(f2,x),-1/2-a,x),

  defint2(1/~x^(~~a),~f1,~x) => intgggg(defint_choose(f1,x),0,-a,x),
  defint2(1/sqrt(~x),~f1,~x) => intgggg(defint_choose(f1,x),0,-1/2,x),
  defint2(1/(sqrt(~x)*~x^~~a),~f1,~x) =>
                             intgggg(defint_choose(f1,x),0,-1/2-a,x),
  defint2(~x**(~~a),~f1,~x) => intgggg(defint_choose(f1,x),0,a,x),
  defint2(sqrt(~x),~f1,~x) => intgggg(defint_choose(f1,x),0,1/2,x),
  defint2(sqrt(~x)*~x^~~a,~f1,~x) =>
                 intgggg(defint_choose(f1,x),0,1/2+a,x),

  defint2(~b,~f1,~x) => b*defint2(f1,x) when freeof(b,x),
  defint2(~f1,~f2,~x) =>
        intgggg(defint_choose(f1,x),defint_choose(f2,x),0,x),

  defint2(~n,~f1,~x) => n*intgggg(defint_choose(f1,x),0,0,x),

  defint2(~f1,~x) => intgggg(defint_choose(f1,x),0,0,x),

  defint2((~f1-~f2)/~f3,~f4,~x) =>
                                               defint2(f1/f3,f4,x) - defint2(f2/f3,f4,x),
  defint2(-~b,~f1,~f2,~x) => -b*defint2(f1,f2,x) when freeof(b,x)

};

let defint2_rules;
>>;
endmodule;
end;


