with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
begin
   if 1=2 or 2=2 and 12<=5 then
      Put_Line("1");
   else
      Put_Line("2");
   end if;
end Main;
