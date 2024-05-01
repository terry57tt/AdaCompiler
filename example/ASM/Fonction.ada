with Ada.Text_IO; use Ada.Text_IO;

procedure Main is

   -- Déclaration d'une procédure simple
   procedure Simple_Procedure is
   begin
      null; -- Corps vide
   end Simple_Procedure;

   -- Déclaration d'une fonction qui retourne un Integer
   function Return_Int return Integer is
   begin
      return 0; -- Retourne simplement 0
   end Return_Int;

   -- Déclaration d'une procédure avec un paramètre
   procedure Procedure_With_Param (Param : Integer) is
   begin
      null; -- Corps vide
   end Procedure_With_Param;

   -- Déclaration d'une fonction imbriquée appelant une autre fonction
   function Nested_Function return Integer is
   begin
      return Return_Int; -- Appel d'une autre fonction
   end Nested_Function;

   -- Déclaration d'une procédure complexe appelant plusieurs procédures
   procedure Complex_Procedure is
   begin
      Simple_Procedure;  -- Appel de procédure
      Procedure_With_Param(5);  -- Appel de procédure avec un argument
   end Complex_Procedure;

begin
   -- Appel des fonctions et procédures dans le corps principal du programme
   Simple_Procedure;
   Put_Line("Return_Int: " & Integer'Image(Return_Int));
   Procedure_With_Param(10);
   Put_Line("Nested_Function: " & Integer'Image(Nested_Function));
   Complex_Procedure;
end Main;