with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
   function getInteger(c : integer) return Integer is
   begin
      return c;
   end getInteger;

    a: Integer;
    b: Integer;

begin
   a := 6 + 8 * 5 ; -- ca vaut 46
   b:= 7 * 2 + 3 * 2 + 6 * 3; -- donc 14 + 6 + 18 = 38
end Main;
