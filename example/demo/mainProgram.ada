with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
        Number : Integer;
        Result : Boolean;
        iter: Integer := 1;

    -- Function that calculate abs val
    function abs_val(X: Integer) return Integer is
    begin
        if (x < 0) then
            return x * (-1);
        end if;
        return x;
    end abs_val;

     -- Function that do the sqrt
     function Integer_Sqrt(X : Integer; Guess : Integer; Epsilon : Integer) return Integer is
            New_Guess : Integer := (Guess + X / Guess) / 2;  -- Integer division
        begin
            if abs_val(New_Guess - Guess) <= Epsilon then
                return New_Guess;
            else
                return Integer_Sqrt(X, New_Guess, Epsilon);
            end if;
     end Integer_Sqrt;

    -- Function to check if a number is prime
    function Is_Prime(Num : Integer) return Boolean is
        A : Integer;
    begin
        if Num <= 1 then
            return False;
        end if;

        A := Integer_Sqrt(Num, 1, 1);

        for I in 2 .. A loop
            if Num REM I = 0 then
                a:= I;
                return False;
            end if;
        end loop;

        return True;
    end Is_Prime;


-- Main procedure
begin
    while iter <= 15  loop
        Number := iter;
        Result := Is_Prime(Number);
        if Result = true then
            put(1);
        else
            put(0);
        end if;
        iter := iter + 1;
    end loop;

end Main;