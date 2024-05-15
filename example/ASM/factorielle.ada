with Ada.Text_IO; use Ada.Text_IO;

procedure Factorial is
    global : integer := 1;

   function Factorial (N : Integer) return Integer is
   begin
      global := global * N;
      if N = 1 then
         return 1;
      else
         return N * Factorial(N - 1);
      end if;
   end Factorial;

   N : Integer;
   Result : Integer;

begin
   N := 5;
   Result := Factorial(N);
   Put(Result);
   put(global);
end Factorial;
