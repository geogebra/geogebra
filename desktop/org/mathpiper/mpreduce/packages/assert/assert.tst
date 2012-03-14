symbolic;

struct any;
struct number checked by numberp;
struct sf checked by sfpx;
struct sq checked by sqp;

declare hugo: (number,any) -> number;

procedure hugo(x1,x2);
   x2;

assert_install hugo;

hugo(0,0);
hugo('x,0);
hugo(0,'x);

declare addf: (sf,sf) -> sf;
declare addsq: (sq,sq) -> sq;

assert_install addf,addsq;

addsq(simp 'x,numr simp 'x);

algebraic;

assert_analyze();

assert_uninstall_all;

end;
