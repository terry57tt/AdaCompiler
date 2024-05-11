with Ada.Text_IO; use Ada.Text_IO;

procedure Record_Test is

   type test is record
      A, B: integer;
   end record;

   type test2 is record
        A, B: integer;
   end record;

   type test3 is record
        C : integer;
        D : test2;
   end record;

   coucou : test;

   coucou2 : test3;

   function parametre_struct (coucou : test) return integer is
    begin
        return coucou.A + coucou.B;
    end parametre_struct;

    a : integer;

begin
   coucou.A := 1;
   coucou.B := 2;

   coucou2.C := 3;
   coucou2.D.A := 4;
   coucou2.D.B := 5;

   a := parametre_struct(coucou);
   put(a);
end Record_Test;
