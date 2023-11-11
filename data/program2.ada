with Ada.Text_IO;

procedure Sum_Of_Integers is
   Total : Integer := 0;
begin
   for I in 1..10 loop
      Total := Total + I;
   end loop;

   Ada.Text_IO.Put("La somme des entiers de 1 à 10 est : ");
   Ada.Text_IO.Put(Integer'Image(Total));
   Ada.Text_IO.New_Line;
end Sum_Of_Integers;
