-- Importation des modules ADA nécessaires
with ADA.TEXT_IO; USE ADA.TEXT_IO;

-- Déclaration du programme principal
procedure Main is

    -- Déclarations multiples (DECLSTAR)
    type MyType; -- Test pour 'type IDENT;'
    type Pointer is access MyType; -- Test pour 'type IDENT is access IDENT;'
    
    -- Déclaration de type record (CHAMPSPLUS)
    type RecordType is record
        Field1, Field2 : Integer; -- Test pour 'CHAMPS'
    end record;

    -- Déclaration de variables
    Variable1, Variable2 : Integer := 0; -- Test pour 'IDENTPLUS, : TYPE EXPRINTERRO ;'

    -- Déclaration de sous-programme (PROCEDURE)
    procedure MyProcedure is
    begin
        -- Instructions pour MyProcedure
        Variable1 := Variable1 + 1; -- Test pour 'INSTR'
    end MyProcedure;

    -- Déclaration de fonction
    function MyFunction return Integer is
    begin
        -- Instructions pour MyFunction
        return Variable2; -- Test pour 'return EXPRINTERRO ;'
    end MyFunction;

    -- Test pour 'PARAMSINTERRO' et 'MODEINTERRO' pourrait être intégré dans une autre procédure ou fonction

begin
    -- Corps du programme principal (INSTRPLUS)
    MyProcedure; -- Test pour 'IDENT;'
    Variable1 := MyFunction; -- Test pour 'ACCES := EXPR ;'

    -- Test pour la boucle 'for'
    for I in reverse 1 .. 10 loop -- Test pour 'REVERSEINTERRO'
        Variable2 := Variable2 + I;
    end loop;

    -- Test pour la structure conditionnelle 'if'
    if Variable1 = 5 then -- Test pour 'EXPR'
        -- Quelques instructions
    elsif Variable1 = 10 then -- Test pour 'ELSIFSTAR'
        -- Quelques instructions
    else -- Test pour 'ELSEINTERRO'
        -- Quelques instructions
    end if;

end Main; -- Test pour 'IDENTINTERRO'
