with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
    a : Integer;
begin
   if ((1>=2) or (1=1)) and (2<3) then
      a := 1;
   else
      a := 2;
   end if;
end Main;
