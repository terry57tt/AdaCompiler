with Ada.Text_IO;
use Ada.Text_IO;

procedure Main is
    a : Integer;
begin
   a := 5;
   if 1 = 1 then
      if false then
         a := 5;
         Put("5");
      elsif false then
         a := 6;
         Put("6");
      else
         if false then
                  a := 9;
                  Put("5");
               elsif true then
                  a := 10;
                  Put("6");
               else
                  a := 11;
                  Put("7");
               end if;
      end if;
      a := 20;
      Put("4");
   else

      a := 3;
      Put("3");
   end if;
end Main;