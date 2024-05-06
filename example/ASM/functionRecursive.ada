with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
   a : Integer;


   function fonction_param(a: Integer) return Integer is
   begin
      if (a > 0) then
        return fonction_param(a - 1);
      end if;
      return -1;
   end Return_Int;

begin
   a := fonction_param(4);
end Main;