with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
    i: Integer;
    j: Integer;
    val: Integer;
begin
   i := 0;
   j := 0;
   val := 0;
   while i < 3 loop
        i := i+1;
        while j < 3 loop
           j := j+1;
           val := val + 1;
        end loop;
        j := 0;
   end loop;

end Main;