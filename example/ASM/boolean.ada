with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
    a : Integer;

   function fn(a: Integer) return Integer is
   begin
      return 1;
   end Return_Int;

begin
   a := 1;
   if ((1=a)) and (2<fn(10)) then
      a := 1;
   else
      a := 2;
   end if;
end Main;
