lisp;

linelength 60;

symbolic procedure test_wrap x;
  begin
    scalar a, b;
    for i := 1:60 do <<
      terpri();
      prin2 "Test at offset "; print i;
      for j := 1:i-1 do prin2 ".";
      prin2 "<";
      a := posn();
      prin1 x;
      b := posn();
      prin2 ">";
      terpri();
      if b > 60 then print list('overflowed, a, b)
      else print list("positions", a, b) >>;
    print "done"
  end;

test_wrap 'a;
test_wrap 'abcdefghijklmnopqr;
test_wrap '!+;
test_wrap '!+!-!+!-!+!-!+!-!+!-!+!-!+!-!+!-!+!-;
% The next line has tabs on it
test_wrap '!	!	;
test_wrap !$eol!$;
test_wrap 2;
test_wrap 1234567890123456789;
test_wrap "a";
test_wrap "abcdefghijklmnopqr";
% The next line is to test double-quotes within strings
test_wrap "a""c""e""g""h""j""l""n""p""r";
test_wrap '(a);
test_wrap '(a b c d e f g h i j k l m n o p q r);
test_wrap '(a . x);
test_wrap '(a b c d e f g h i j k l m n o p q r . x);
test_wrap '((((a b c))) . x);
test_wrap '((((a b))));
% I could perhaps try almost all special characters at the start and
% within names...
test_wrap '!_abc!_def;
test_wrap '!+abc!+def;
test_wrap '!&abc!&def;
test_wrap '!!abc!!def;
test_wrap '!-abc!-def;

end;
