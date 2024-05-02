with Ada.Text_IO; use Ada.Text_IO;
procedure Test is
    a : Integer;
begin
   for I in 1 .. 10 loop a:=I; end loop;
   for I in reverse 1 .. 10 loop a:=I; end loop;
end;