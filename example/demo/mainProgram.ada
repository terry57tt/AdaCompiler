with Ada.Text_IO; use Ada.Text_IO;

procedure Main is
        Number : Integer;
        Result : Boolean;


    -- Function to calculate the integer square root using binary search
    function Integer_Sqrt(X : Integer) return Integer is
        Low : Integer := 0;
        High : Integer := X;
        Mid : Integer;
    begin
        if X < 0 then
            return -1; -- Return -1 for negative numbers
        elsif X < 2 then
            return X; -- Return X for 0 or 1
        else
            while Low < High loop
                Mid := (Low + High + 1) / 2; -- Adjusting midpoint towards high
                if Mid * Mid <= X then
                    Low := Mid;
                else
                    High := Mid - 1;
                end if;
            end loop;
            return Low;
        end if;
    end Integer_Sqrt;


    -- Function to check if a number is prime
    function Is_Prime(Num : Integer) return Boolean is
        A : Integer;
    begin
        if Num <= 1 then
            return False;
        end if;

        A := Integer_Sqrt(Num);

        for I in 2 .. A loop
            if Num REM I = 0 then
                return False;
            end if;
        end loop;

        return True;
    end Is_Prime;


-- Main procedure
begin
    for iter in 1..15 loop
        Number := iter;
        Result := Is_Prime(Number);
        if Result = true then
            put(1);
        else
            put(0);
        end if;
    end loop;

end Main;