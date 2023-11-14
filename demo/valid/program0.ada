with Ada.Text_IO;
use Ada.Text_IO;

procedure AfficherDateHeure is

    now : Ada.Calendar.Time;

begin
    now := Ada.Calendar.Clock;
    -- Affiche l'heure
    WriteLn(Ada.Calendar.Second(now));
end AfficherDateHeure;