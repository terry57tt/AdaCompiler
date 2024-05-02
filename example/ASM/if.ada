with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
begin
   if ((false=true and 5<8) or 5>=18) or (2=2 and not 1=1) then
      Put_Line("1");
   else
      Put_Line("2");
   end if;
end Main;
