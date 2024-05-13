with Ada.Text_IO; use Ada.Text_IO;
procedure Test is
    a : Integer;
    b : Integer;
begin
   for I in 1 .. 10 loop
     for J in reverse 1 .. 10 loop
        for K in 1 .. 10 loop
            b := I + J + K;
            put(b);
            a := 2;
        end loop;
     end loop;
   end loop;

end;