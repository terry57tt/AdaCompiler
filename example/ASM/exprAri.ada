with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
   function getInteger return Integer is
   begin
      return 14;
   end getInteger;

    a: Integer;

begin
   a := ((((7 REM 3 * 10 + 5 - 3) / 2) * 20) REM getInteger() ) * 2;

end Main;
