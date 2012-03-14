% Demonstrations file for the REDUCE implementation of Turtle Graphics

% Caroline Cotter, ZIB,Berlin, 1998.

load_package turtle;

on rounded;

on demo;

%1) Draw 36 rays of length 100
%(Taken from MapleTech - Maple in Mathematics and the Sciences,
% Special Issue 1994)

draw {for i:=1:36 collect
                  {setheading(i*10), forward 100, back 100} };


%2) Draw a "fan" of 36 straight spikes.

draw {for i:=1:36 collect 
                  {setheading(i*10), forward 100, back 95} };


%3) Draw a "fan" of 36 curved rays.

draw {for i:=1:36 collect
           {setheading(i*10), forward 20, turnleft 20, forward 20, turnleft 20,
            forward 20, turnleft 20, forward 20, turnleft 20, forward 20, 
            back 20, turnright 20, back 20, turnright 20, back 20, 
            turnright 20, back 20, turnright 20, back 18} };


%4) Draw 12 regular polygons with 12 sides of length 40,each polygon
% forming an angle 
% of 360/n degrees with the previous one.
%(Taken from MapleTech - Maple in Mathematics and the Sciences,
% Special Issue 1994)

draw {for i:=1:12 collect 
                  {turnleft(30), for j:=1:12 collect 
                                             {forward 40, turnleft(30)}} };


%5) A "peak" pattern - an example of a recursive procedure.

<<
procedure peak(r);
begin;
      return for i:=0:r collect 
              {move{x_coord+5,y_coord-10}, move{x_coord+10,y_coord+60},
               move{x_coord+10,y_coord-60}, move{x_coord+5,y_coord+10}};
end;

draw {home(), peak(3)} >>;

%This procedure can then be part of a longer chain of commands:

draw {home(), move{5,50}, peak(3), move{x_coord+10,-100}, 
      peak(2), move{x_coord+10,0}};


%6) Write a recursive procedure which draws "trees" such that every branch
% is half the
% length of the previous branch.
%(Taken from MapleTech - Maple in Mathematics and the Sciences,
% Special Issue 1994)

<<
procedure tree(a,b);   %Here: a is the start length, b is the number of levels
begin;
      return if fixp b and b>0            %checking b is a positive integer
                then {turnleft(45), forward a, tree(a/2,b-1),
                      back a, turnright(90), forward a, tree(a/2,b-1),
                      back a, turnleft(45)}
             else {x_coord,y_coord};           %default: Turtle stays still
end;


draw {home(), tree(130,7)} >>;


%This can be rotated so that the tree grows upwards:

draw {home(), setheading(90), tree(130,7)};


%7) A 36-point star.

draw {home(), for i:=1:36 collect 
                    {turnleft(10), forward 100, turnleft(10), back 100} };


%8) Draw 100 equilateral triangles with the leading points equally spaced
% on a circular path.

draw {home(), for i:=1:100 collect
           {forward 150, turnright(60), back(150), 
            turnright(60), forward 150, setheading(i*3.6)} };


%9) Two or more graphs can be drawn together (this is easier if the graphs
% are named).Here we show graphs 4 and 8 on top of one another:

<<
gr4:={home(), for i:=1:12 collect 
                    {turnleft(30), for j:=1:12 collect 
                                       {forward 40, turnleft(30)}} }$

gr8:={home(), for i:=1:100 collect
                      {forward 150, turnright(60), back(150), 
                       turnright(60), forward 150, setheading(i*3.6)} }$

plot(gr4,gr8) >>;

off rounded;

end;
