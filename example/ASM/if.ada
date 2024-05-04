with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
    a : Integer;
begin
   a := 5;
   if ((false=true and 5<8) or 18>=18) or (2=2 and not (1=1)) then
      Put(1);
   else
      Put(0);
   end if;
end Main;
