with Ada.Text_IO; use Ada.Text_IO;

procedure ifProgram is
    Variable : Integer;
    x : Integer;
    Variable1 : Integer;
    Variable4 : Integer;
begin
    -- Structure conditionnelle if-elsif-else
    if Variable > 0 then
        x := 1;
    elsif Variable < 0 then
        x := 2;
    else
        x := 3;
    end if;

    -- Boucle while
    while Variable < 10 loop
        -- Calcul dans la boucle
        Variable4 := Variable1 * 2;

        -- Incrémentation de Variable
        Variable := Variable + 1;
    end loop;
end ifProgram;