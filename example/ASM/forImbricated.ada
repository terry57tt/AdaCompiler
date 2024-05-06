with Ada.Text_IO; use Ada.Text_IO;
procedure Test is
    a : Integer;
begin
   for I in 1 .. 10 loop
    for J in reverse 1 .. 10 loop
        a := a + 1;
    end loop;
   end loop;

end;