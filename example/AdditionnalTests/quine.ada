with Ada.Text_IO; use Ada.Text_IO;

procedure Quine is
	type str;
	type u is access str;
	type str is record
		value : integer;
		tail : u;
	end record;
	procedure puts(s : u) is begin
		if s /= null then
			put(character'val(s.value)); 
			puts(s.tail);
		end if;
	end;
	procedure print_int(n : Integer) is
		c : integer := n rem 10;
	begin
		if n > 9 then print_int(n / 10); end if;
		put(character'val(48 + C));
	end;
	function o(c : integer; s : u) return u is
		result : u := new str;
	begin
		result.value := c; result.tail := s; return result;
	end;
	procedure def(s : u; n : character) is
		k : integer := 0;
		w : u := s;
	begin
		put(n);put(':');put('u');put(':');put('=');
		while w /= null loop
			put('o');put('(');print_int(w.value);put(',');
			put(')');
			k := k+1;
			w := w.tail;
		end loop;
		put('n');put('u');put('l');put('l');
		for i in 1..k loop put(')'); end loop;
		put(';');
		new_line;
	end;
begin
	puts(s);
	def(s, 's');
	def(e, 'e');
	puts(e);
	new_line;
end Quine;
