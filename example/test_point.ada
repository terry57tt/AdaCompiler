with Ada.Text_IO; use Ada.Text_IO;

procedure Record_Test is

   type R is record
      A, B: Character;
   end record;

   type S is record
      C: Character;
      D: R;
      E: Character;
   end record;

   type T is record
         G: S;
         H: Character;
      end record;

   function Create_R (A_Char, B_Char : Character) return R is
      Result : R;
   begin
      Result.A := A_Char;
      Result.B := B_Char;
      return Result;
   end Create_R;

   function Create_S (C_Char : Character; D_Struct : R; E_Char : Character) return S is
      Result : S;
   begin
      Result.C := C_Char;
      Result.D := D_Struct;
      Result.E := E_Char;
      return Result;
   end Create_S;

   function Create_T (G_Struct : S; H_Char : Character) return T is
         Result : T;
      begin
         Result.G := G_Struct;
         Result.H := H_Char;
         return Result;
      end Create_T;

   V: R := Create_R('1', '2');
   X: S := Create_S('0', V, '3');
   test: Character;

begin
   -- Tests sans affichage

   -- Test valide: Accès à un champ d'une structure retournée par une fonction (devrait réussir)
   test := Create_S('a', Create_R('x', 'y'), 'z').D.G;

   -- Test invalide: Accès à un champ inexistant (devrait échouer)
   test := Create_S('a', Create_R('x', 'y'), 'z').Z;

   -- Test valide: Affectation d'une structure à une autre du même type (devrait réussir)
   V := Create_R('2', '6');

   -- Test invalide: Affectation entre types incompatibles (devrait échouer)
   V := Create_S('a', Create_R('x', 'y'), 'z');

   -- Test valide: Comparaison de deux structures de même type (devrait réussir)
   if V = Create_R('8', '8') then
    put('p');
   end if;

   -- Test invalide: Tentative de comparaison entre types incompatibles (devrait échouer)
   if X = V then
    put('p');
   end if;

   -- Test valide: Imbrication profonde avec fonction
   test := Create_T(Create_S('2', Create_R('b', 'c'), 'd'), 'e').G.D.A;

   -- Test valide: Imbrication profonde avec fonction
   test := Create_T(Create_S('a', Create_R('b', 'c'), 'd'), 'e').G.D.B;

   -- Test invalide: Imbrication profonde avec fonction
   test := Create_T(Create_S('a', Create_R('b', 'c'), 'd'), 'e').G.D.H;
end Record_Test;
