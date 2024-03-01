with Ada.Text_IO; use Ada.Text_IO;

procedure Pascal is

   type Node;
   type List is access Node;
   type Node is record
      Value: Integer;
      Next: List;
   end record;

   function Get(L: List; I: Integer) return Integer is
   begin
      if I = 0 then return L.Value; end if;
      return Get(L.next, I - 1);
   end;
   procedure Set(L: List; I, V: Integer) is
   begin
      if I = 0 then
         L.Value := v;
      else
         Set(L.next, I - 1, V);
      end if;
   end;

begin
   for i in 0 .. N-1 loop
      Set(R, I, 0);
      compute_row(i);
      print_row(i);
   end loop;
end Pascalito;

--  Local Variables:
--  compile-command: "gnatmake pascal.adb && ./pascal"
--  End:
