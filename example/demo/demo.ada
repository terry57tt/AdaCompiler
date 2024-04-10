with Ada.Text_IO; use Ada.Text_IO;

procedure Pascal is
   N: integer := 4;
   index: Integer := 0;
   car: character;
   type List is record
      Value: Integer;
      Next: List;
   end record;
   R : List;


   function Get(L: in List; I: in out Integer) return Integer is
       ai2: Integer;
       c : character;
   begin
      ai1 := 0;
      c := 'c';
      L.value := 'a';
      I := 3;
      I := 'a';

      if I = 0 then return L.val; end if;
      return Get(L, I + I);
   end;

   procedure Set(L: in out List; I, V: Integer) is
    ab1: Integer;
   begin
      ab1 := 'c';
      set1 := 3;
      L := d;
      I := 3;
      V := 'a';
      ab1 := 2 + 3 + 4*(2+3/0);
      ab1 := 2;
      if I = 0 then
         L.Value := v;
      else
         Set(L, I - 1, V, 6);
      end if;
   end;

begin
   for i in 0 .. N-1 loop
      car := Set(R, i, 'z');
      N := Get(R, R.val);
      i := 3;
      compute_row(i);
   end loop;
   while index < N loop
      index := index + 1;
   end loop;
end Pascalito;

