with Ada.Text_IO;
with Ada.Integer_Text_IO;

procedure Labyrinthe is

  type Cellule is (Mur, Vide);
  type Grille is array (1..L, 1..H) of Cellule;

  L, H : Integer;
  grille : Grille;

begin

  -- Demande de la taille du labyrinthe
  Ada.Text_IO.Put(demander);
  Ada.Integer_Text_IO.Get(L);
  Ada.Text_IO.Put(demander);
  Ada.Integer_Text_IO.Get(H);

  -- Initialisation de la grille
  for i in 1..L loop
    for j in 1..H loop
      grille(i, j) := Vide;
    end loop;
  end loop;

  -- Génération du labyrinthe
  for i in 1..L loop
    for j in 1..H loop
      if (i = 1 or i = L) or (j = 1 or j = H) then
        grille(i, j) := Mur;
      else
        -- Génération d'un mur aléatoire
        if random(2) = 1 then
          grille(i, j) := Mur;
        end if;
      end if;
    end loop;
  end loop;

  -- Affichage du labyrinthe
  for i in 1..L loop begin
    for j in 1..H loop begin
      Ada.Text_IO.Put(grille(i, j) . character);
    end loop;
    Ada.Text_IO.New_Line;
  end loop;

end Labyrinthe;
