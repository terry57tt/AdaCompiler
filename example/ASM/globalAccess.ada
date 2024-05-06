with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
   global : Integer := 2;

   procedure Simple_Procedure is
     b : Integer := 1;
   begin
      b := global + 1;
   end Simple_Procedure;

   function fonction_param(a: Integer; b: Integer) return Integer is
     c : Integer;
   begin
      Simple_Procedure;
      global := global + 100;
      c := global + b;
      return 1000;
   end;

begin
   Simple_Procedure;
   global := global + fonction_param(10,12);
end Main;