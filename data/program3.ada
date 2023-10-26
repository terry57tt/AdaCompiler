with Ada.Text_IO;
use Ada.Text_IO;

procedure unDebut is
   function aireRectangle(larg : Integer; long : Integer) return Integer is
      aire: Integer;
   begin
      aire := larg * long;
      return aire;
   end aireRectangle;

   function perimetreRectangle(larg : Integer; long : Integer) return Integer is
      p : Integer;
   begin
      p := larg * 2 + long * 2;
      return p;
   end perimetreRectangle;

   -- VARIABLES
   choix : Integer;
   valeur : Integer; -- Vous devez déclarer 'valeur'

begin
   choix := 2;
   if choix = 1 then
      valeur := perimetreRectangle(2, 3);
      Put("Périmètre : ");
      Put(valeur);
   else
      valeur := aireRectangle(2, 3);
      Put("Aire : ");
      Put(valeur);
   end if;
end unDebut;
