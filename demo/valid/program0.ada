with Ada.Text_IO;
use Ada.Text_IO;

procEDure AfficherDateHeure is

    now : Ada.Calendar.Time;

begiN
    now := Ada.Calendar.Clock;
    -- Affiche l'heure
    WriteLn(Ada.Calendar.Second(now));
END AfficherDateHeure;