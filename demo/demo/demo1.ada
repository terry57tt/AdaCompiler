with Ada.Text_IO ; use Ada.Text_IO ;
procedure ifProgram is
begin
    if Variable > 0 then
         x := 1;
    elsif Variable < 0 then
        x := 2;
        if Variable < 0 then
            x := 4;
        end if;
    elsif Variable < 0 then
            x := 2;
    else
       x := (46 * 5 + 3 rem 7);
    end if;

    while x < 10 loop
           y := z * 2;
        end loop;
end unDebut ;