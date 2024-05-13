with Ada.Text_IO; use Ada.Text_IO;
procedure Test is
    a : Integer := 0;
begin
   for I in reverse 1 .. 11 loop
      put(I);
   end loop;
end;
