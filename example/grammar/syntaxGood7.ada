with Ada.Text_IO; use Ada.Text_IO;

procedure Example is
    -- Déclaration d'une procédure
    procedure MyProcedure is
        Variable3 : Integer := 10; -- Déclaration et initialisation d'une variable locale à la procédure
    begin
        -- Affectation dans la procédure
        Variable3 := Variable3 + 5;
    end MyProcedure;

    -- Déclaration d'une fonction
    function MyFunction(Argument : Integer) return Integer is
        Variable2 : Integer := Argument * 2; -- Déclaration et initialisation d'une variable locale à la fonction
    begin
        return Variable2 + 3;
    end MyFunction;

    Variable : Integer := 5; -- Déclaration et initialisation d'une variable
    x : Integer;
    Variable1 : Integer := 3; -- Déclaration et initialisation d'une variable
    Variable4 : Integer;

begin
    -- Structure conditionnelle if-elsif-else avec des déclarations et affectations
    if Variable > 0 then
        x := 1;
        Variable4 := Variable * 2;
    elsif Variable < 0 then
        x := 2;
        Variable4 := Variable * 3;
    else
        x := 3;
        Variable4 := Variable + Variable1;
    end if;

    -- Expression booléenne complexe
    if (Variable + Variable1 * 2) / 3 = 5 and then Variable > 2 or else Variable1 < 5 then
        x:=2;
    end if;

    -- Boucle for 
    for i in 1..5 loop
        begin
            Variable := Variable + Variable5;
        end;
    end loop;

    -- Boucle while 
    begin
        while Variable6 < 5 loop
            -- Calcul dans la boucle
            Variable6 := Variable6 + 1;

            -- Affectation dans la boucle
            Variable4 := Variable4 + Variable6;
        end loop;
    end;

    -- Appel de la procédure
    MyProcedure;

    -- Appel de la fonction
    x := MyFunction(Variable);
end Example;