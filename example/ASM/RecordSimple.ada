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

   function parametre_struct(coucou : test) return integer is
        copie : test := coucou;
    begin
        copie.A := 1000;
        copie.B := 100;
        return copie.A + copie.B;
    end parametre_struct;

    a : integer;

    function parametre_struct2(coucou : test3) return integer is
        copie : test3 := coucou;
    begin
        return copie.C + copie.D.A + copie.D.B;
    end parametre_struct;

begin
   coucou.A := 1;
   coucou.B := 2;

   coucou2.C := 3;
   coucou2.D.A := 4;
   coucou2.D.B := 5;

   a := parametre_struct(coucou);
   put(a);
   a := parametre_struct(coucou);
   put(a);
   put(coucou.A);

   a := parametre_struct2(coucou2);
   put(a);
   a := parametre_struct2(coucou2);
   put(a);
end Record_Test;
