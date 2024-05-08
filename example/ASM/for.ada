with Ada.Text_IO; use Ada.Text_IO;
procedure Test is
    a : Integer := 0;
begin
   for I in 1 .. 11 loop
      for J in 1 .. 11 loop
         a := a + 1;
      end loop;
   end loop;
end;
