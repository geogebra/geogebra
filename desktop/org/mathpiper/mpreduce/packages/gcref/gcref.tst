on gcref;

symbolic procedure p1(x,y);
   p2(x) + y;

symbolic procedure p2(x);
   <<
      if x>0 then p2(x-1);
      p1(x-2,1);
      p3(x)
   >>;

off gcref;

end;
