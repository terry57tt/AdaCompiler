with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
   function getInteger(c : integer) return Integer is
   begin
      return c;
   end getInteger;

    a: Integer;
    b: Integer;

begin
   -- a := ((((7 REM 3 * 10 + 5 - 3) / 2) * 20) REM getInteger(11) ) * 2; -- ca vaut 20
   b:= 7 * 2 + 3 * 2 + (-6) * 3; --
end Main;
