with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
    a : Integer;
begin
   if 1=2 or 2=2 and 12<=5 then
      a:= 1;
   elsif 2=2 then
      a:= 2;
   elsif 3=3 then
      a:= 3;
   elsif 4=4 then
      a:= 4;
   else
      a:=5;
   end if;
end Main;