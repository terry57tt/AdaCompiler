with Ada.Text_IO ; Ada.Text_IO ;

unDebut is
    function aireRectangle(larg : integer; long : integer) return integer is
    aire: integer
        choix := 2;
        if choix = 1
            then valeur := perimetreRectangle(2, 3) ;
                put(valeur) ;
            else valeur := aireRectangale(2, 3) ;
                put(valeur) ;
        end if;
end unDebut ;