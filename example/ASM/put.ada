with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
    a : Integer := 1000;

    procedure coucou(x : Integer) is
    begin
        put(x);
        put(a);
        put(2);
    end coucou;
begin
   put(10);
   put(7);
   put('a');
   coucou(5);
   put('!');
   put('d');
   put('9');
end Main;