with Ada.Text_IO;
use Ada.Text_IO;

procedure Main is
    a : Integer;
begin
   a := 5;
   if 5 <= 5 then            -- true
      if false then         -- false
         a := 1;
      else
         if false then      -- false
            a := 9;
         elsif (a=-12) or ((6<=(a+1)) or (true=false)) then -- true
            a := 10;        -- 'a' prend la valeur de 10
         else
            a := 11;
         end if;
      end if;
   end if;
   Put(a);    -- la valeur de 'a' se retrouve en R0
end Main;



