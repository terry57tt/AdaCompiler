with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
    i: Integer;
begin
    i:= 0;
   while i < 5 loop
        i := i+1;
   end loop;
   while i > 4 loop
        i := i-1;
   end loop;
end Main;