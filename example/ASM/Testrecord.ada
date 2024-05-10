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
   test := Create_S('a', Create_R('x', 'y'), 'z').D.A;
   V := Create_R('2', '6');

   if V = Create_R('8', '8') then
    put('p');
   end if;

   test := Create_T(Create_S('2', Create_R('b', 'c'), 'd'), 'e').G.D.A;
   test := Create_T(Create_S('a', Create_R('b', 'c'), 'd'), 'e').G.D.B;
end Record_Test;
