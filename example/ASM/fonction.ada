with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
   a : Integer;

   procedure Simple_Procedure is
     b : Integer := 1;
   begin
      b:=b+1;
   end Simple_Procedure;

   function fonction_param(a: Integer; b: Integer) return Integer is
     c : Integer;
   begin
      Simple_Procedure;
      return 1000;
   end Return_Int;

begin
   Simple_Procedure;
   a := fonction_param(10,12);
end Main;