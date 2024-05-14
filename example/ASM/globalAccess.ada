with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
   global : Integer := 2;
   a : Integer := 1;

   procedure Simple_Procedure is
     b : Integer := 1;
   begin
      b := global + 1;
   end Simple_Procedure;

   function fonction_param(a: Integer; b: Integer) return Integer is
     c : Integer;

     procedure incrementerGlobal(b : integer) is
        begin
            global := global + b;
     end incrementerGlobal;

     b : Integer := 1;
   begin
      Simple_Procedure;
      incrementerGlobal(80);
      global := global + 100;
      c := global + b;
      return 1000;
   end;

begin
   Simple_Procedure;
   a := a + fonction_param(10,12);
   global := global + a;
   put(global);
end Main;