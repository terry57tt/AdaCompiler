with Ada.Text_IO;
with Ada.Numerics.Discrete_Random;

procedure Simulate_Solar_System is

  type Planet is (Mercury, Venus, Earth, Mars, Jupiter, Saturn, Uranus, Neptune);

  type State is record
    position : Float;
    velocity : Float;
  end record;

  package Rand is
    new Ada.Numerics.Discrete_Random(Positive);

  use Rand;

  function Random_Planet return Planet is
  begin
    return Planet'(Random(1, 8));
  end Random_Planet;

  function Random_Position return Float is
  begin
    return Random(-1000.0, 1000.0);
  end Random_Position;

  function Random_Velocity return Float is
  begin
    return Random(-100.0, 100.0);
  end Random_Velocity;

  procedure Initialize(s : in out State) is
  begin
    s.position := Random_Position;
    s.velocity := Random_Velocity;
  end Initialize;

  procedure Update(s : in out State) is
  begin
    s.position := s.position + s.velocity;
  end Update;

  procedure Print(s : in State) is
  begin
    pragma Assert(s.planet in Planet);

    write(s.planet, ': ');
    write(s.position, ', ');
    write(s.velocity);
    write_line;
  end Print;

  planets : array(1..8) of State;

begin
  for i in 1..8 loop
    Initialize(planets(i));
  end loop;

  for i in 1..1000 loop
    for j in 1..8 loop
      Update(planets(j));
    end loop;

    for j in 1..8 loop
      Print(planets(j));
    end loop;
  end loop;
end Simulate_Solar_System;
