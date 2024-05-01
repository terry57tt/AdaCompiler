with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
   a : Integer;

   procedure Simple_Procedure is
   begin
      a:=a+1;
   end Simple_Procedure;

   function fonction_param(a,b) return Integer is
   begin
      Simple_Procedure;
      return 0;
   end Return_Int;

begin
   Simple_Procedure;
   a := fonction_param(a,2);
end Main;