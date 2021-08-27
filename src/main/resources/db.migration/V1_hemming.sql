create or replace function hemming(IN hash1 bigint, IN hash2 bigint)
returns real as $$
    declare
        hashesXor bigint := hash1 # hash2;
        numberOfOne bigint := 0;
        resultVar real := 0.0;
        delta bigint := 0;
    begin
        if (hashesXor < 0) then
            hashesXor := ~hashesXor;
            delta := 64;
        end if;
        while(hashesXor > 0)
            loop
                numberOfOne := numberOfOne + (hashesXor & 1);
                hashesXor := hashesXor >> 1;
            end loop;
        numberOfOne := abs(numberOfOne - delta);
        resultVar := 1 - cast(numberOfOne as double precision)/64.0;
        return resultVar;
    end; $$
language plpgsql;