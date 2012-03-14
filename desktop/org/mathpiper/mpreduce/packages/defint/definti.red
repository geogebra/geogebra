module definti;

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


% A rule set to test for the validity of the seven cases for the
% integration of a single Meijer G-function.
%
% 'The Special Functions and their Approximations', Volume 1,
%  Y.L.Luke. Chapter 5.6 pages 158 & 159

algebraic <<

operator test_cases,case_1,case_2,case_3,case_4,case_5,case_6,case_7;

test_cases_rules :=

{test_cases(~m,~n,~p,~q,~delta,~xi,~eta,~test_1,~test_1a,~test_2) => 't

   when case_1(m,n,p,q,delta,xi,eta,test_1,test_1a,test_2) = 't
   or case_2(m,n,p,q,delta,xi,eta,test_1,test_1a,test_2) = 't
   or case_3(m,n,p,q,delta,xi,eta,test_1,test_1a,test_2) = 't
   or case_4(m,n,p,q,delta,xi,eta,test_1,test_1a,test_2) = 't
   or case_5(m,n,p,q,delta,xi,eta,test_1,test_1a,test_2) = 't
   or case_6(m,n,p,q,delta,xi,eta,test_1,test_1a,test_2) = 't
   or case_7(m,n,p,q,delta,xi,eta,test_1,test_1a,test_2) = 't
};

let test_cases_rules;

case_1_rules :=

{  case_1(~m,~n,~p,~q,~delta,~xi,~eta,~test_1,~test_1a,~test_2) => 't
     when 1 <= n and n <= p and p < q
      and 1 <= m and m <= q
      and delta > 0 and eta neq 0
      and mylessp(abs(atan(impart eta/repart eta)),delta) = 't
      and test_1 = 't
      and transform_test2('tst1,nil) = 't

     or p >= 1 and 0 <= n and n <= p
      and 1 <= m and m <= q and q = p + 1
      and not (n = 0 and m = p + 1)
      and delta >0 and eta neq 0
      and mylessp(abs(atan(impart eta/repart eta)),delta) = 't
      and test_1 = 't
      and transform_test2('tst1,nil) = 't

     or p >= 1 and 0 <= n and n <= p
      and 0 <= m and m <= q and q = p
      and delta > 0 and eta neq 0
      and mylessp(abs(atan(impart eta/repart eta)),delta) = 't
      and not (arg_test1(abs(atan(impart eta/repart eta)),delta) = 't)
      and test_1 = 't
      and transform_test2('tst1,nil) = 't

};

let case_1_rules;

case_2_rules :=

{  case_2(~m,~n,~p,~q,~delta,~xi,~eta,~test_1,~test_1a,~test_2) => 't
     when n = 0 and 1 <= p + 1 and p + 1 <= m and m <= q
      and delta > 0
      and mylessp(abs(atan(impart eta/repart eta)),delta) = 't
      and test_1 = 't
      and transform_test2('tst1,nil) = 't
};

let case_2_rules;

case_3_rules :=

{  case_3(~m,~n,~p,~q,~delta,~xi,~eta,~test_1,~test_1a,~test_2)  => 't
     when 0 <= n and n <= p and p < q
      and 1 <= m and m <= q
      and delta > 0
      and arg_test2(abs(atan(impart eta/repart eta)),delta) = 't
      and test_1 = 't and test_2 = 't
      and transform_test2('tst1,'tst2) = 't

     or 0 <= n and n <= p and p <= q - 2
      and delta = 0
      and arg_test3a(atan(impart eta/repart eta),0) = 't
      and test_1 = 't and test_2 = 't
      and transform_test2('tst1,'tst2) = 't
};

let case_3_rules;

case_4_rules :=

{  case_4(~m,~n,~p,~q,~delta,~xi,~eta,~test_1,~test_1a,~test_2)  => 't

     when 0 <= n and n <= p
      and 1 <= m and m <= q and q = p + 2
      and eta neq 0
      and delta <= 0
      and arg_test(atan(impart eta/repart eta),delta) = 't
      and test_1a = 't and test_2 = 't
      and transform_test2('tst1,'tst2) = 't

     or 0 <= n and n <= p
      and 1 <= m and m <= q and q = p + 2
      and eta neq 0
      and delta >= 1
      and arg_test3(atan(impart eta/repart eta),delta) = 't
      and test_1a = 't and test_2 = 't
      and transform_test2('tst1,'tst2) = 't

     or test_1 = 't and test_2 = 't
      and 0 <= n and n <= p
      and 1 <= m and m <= q and q = p + 2
      and eta neq 0
      and delta >= 0
      and arg_test3a(atan(impart eta/repart eta),delta) = 't
      and test_1 = 't and test_2 = 't
      and transform_test2('tst1,'tst2) = 't
};

let case_4_rules;

case_5_rules :=

{  case_5(~m,~n,~p,~q,~delta,~xi,~eta,~test_1,~test_1a,~test_2)  => 't
     when p >= 1
      and 0 <= n and n <= p
      and 1 <= m and m <= q and q = p + 1
      and eta neq 0
      and arg_test4(atan(impart eta/repart eta),delta) = 't
      and test_1a = 't
      and transform_test2('tst1,nil) = 't

     or p >= 1
      and 0 <= n and n <= p
      and 1 <= m and m <= q and q = p + 1
      and eta neq 0
      and xi >= 2
      and arg_test5(atan(impart eta/repart eta),delta,xi) = 't
      and test_1a = 't
      and transform_test2('tst1,nil) = 't

     or p >= 1
      and 0 <= n and n <= p
      and 1 <= m and m <= q and q = p + 1
      and eta neq 0
      and xi >= 2
      and arg_test6(atan(impart eta/repart eta),delta,xi) = 't
      and test_1a = 't
      and transform_test2('tst1,nil) = 't

     or p >= 1
      and 1 <= n and n <= p
      and 1 <= m and m <= q and q = p + 1
      and eta neq 0
      and xi >= 1
      and arg_test6a(atan(impart eta/repart eta),delta,xi) = 't
      and test_1 = 't
      and transform_test2('tst1,nil) = 't
};

let case_5_rules;

case_6_rules :=

{  case_6(~m,~n,~p,~q,~delta,~xi,~eta,~test_1,~test_1a,~test_2)  => 't
     when p >= 1
      and 0 <= n and n <= p
      and 1 <= m and m <= q and q = p + 1
      and eta neq 0
      and xi <= 1
      and arg_test(atan(impart eta/repart eta),delta) = 't
      and test_1a = 't and test_2 = 't
      and transform_test2('tst1,'tst2) = 't

     or p >= 1
      and 0 <= n and n <= p
      and 1 <= m and m <= q and q = p + 1
      and eta neq 0
      and xi >= 2
      and arg_test7(atan(impart eta/repart eta),delta,xi) = 't
      and test_1a = 't and test_2 = 't
      and transform_test2('tst1,'tst2) = 't

     or p >= 1
      and 0 <= n and n <= p
      and 1 <= m and m <= q and q = p + 1
      and eta neq 0
      and xi <= 1
      and arg_test8(atan(impart eta/repart eta),delta) = 't
      and test_1a = 't and test_2 = 't
      and transform_test2('tst1,'tst2) = 't

     or p >= 1
      and 0 <= n and n <= p
      and 1 <= m and m <= q and q = p + 1
      and eta neq 0
      and xi >= 2
      and arg_test8a(atan(impart eta/repart eta),delta,xi) = 't
      and test_1a = 't and test_2 = 't
      and transform_test2('tst1,'tst2) = 't
};

let case_6_rules;

case_7_rules :=

{  case_7(~m,~n,~p,~q,~delta,~xi,~eta,~test_1,~test_1a,~test_2)  => 't
     when p >= 1
      and 0 <= n and n <= p
      and 1 <= m and m <= q and q = p
      and eta neq 0
      and arg_test9(atan(impart eta/repart eta),delta) = 't
      and test_1a = 't
      and transform_test2('tst1,nil) = 't

     or p >= 1
      and 0 <= n and n <= p
      and 1 <= m and m <= q and q = p
      and eta neq 0
      and delta >= 1
      and arg_test9a(atan(impart eta/repart eta),delta) = 't
      and not (arg_test1(abs(atan(impart eta/repart eta)),delta) = 't)
      and test_1 = 't
      and transform_test2('tst1,nil) = 't
};

let case_7_rules;

>>;

endmodule;
end;



