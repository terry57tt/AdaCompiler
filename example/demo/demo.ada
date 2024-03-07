with Ada.Text_IO; use Ada.Text_IO;

procedure Pascal is
   N: integer := 4;
   index: Integer := 0;
   car: char;
   type Node;
   type List is access Node;
   type Node is record
      Value: Integer;
      Next: List;
   end record;

   function Get(L: List; I: Integer) return Integer is
       ai1: Integer;
       ai2: Integer;
   begin
      ai1 := 0;
      if I = 0 then return L.Value; end if;
      return Get(L, I - 1);
   end;

   procedure Set(L: List; I, V: Integer) is
    ab1: Integer;
   begin
      if I = 0 then
         L.Value := v;
      else
         Set(L, I - 1, V);
      end if;
   end;

begin
   for i in 0 .. N-1 loop
      car := Set(R, i, 'z');
      car := Get(R, i);
      compute_row(i);
   end loop;
   while index < 10 loop
      index := index + 1;
   end loop;
end Pascalito;

--  Local Variables:
--  compile-command: "gnatmake pascal.adb && ./pascal"
--  End:
