
ReadResult=fopen(BlastResult);
WriteTable=fopen(Table,'w');
while 1
    tline=fgetl(ReadResult);
    if tline==-1
        break;
    end
    if isempty(tline)||length(tline)<7
        continue;
    end
    if strcmp(tline(1:7),'Query= ')
        Pname1=tline(8:end);
        for i=1:4
            tline=fgetl(ReadResult);
        end
        if isempty(tline)
            continue;
        end
        if strcmp(tline(1:9),'Sequences')
            fprintf(WriteTable,'%s',Pname1);
            fprintf(WriteTable,'%s',' ');
            %disp(Pname1);
            tline=fgetl(ReadResult);
            while 1
                tline=fgetl(ReadResult);
                if isempty(tline)
                    fprintf(WriteTable,'\n');
                    break;
                end
                Place=3;
                while 1
                    if strcmp(tline(Place),' ')
                        break;
                    end
                    Place=Place+1;
                end
                Pname2=tline(3:Place);
                Evalue=tline(79:end);
                Evalue=str2num(Evalue);
                Evalue=Evalue/10;
                if Evalue==0
                    Evalue=1;
                else
                    Evalue=1/(1-1/log10(Evalue)); 
                    Evalue=round(Evalue*1000)/1000;
                end
                Evalue=num2str(Evalue);
                fprintf(WriteTable,'%s',Pname2);
                %fprintf(WriteTable,'%s',' ');
                fprintf(WriteTable,'%s',Evalue);
                fprintf(WriteTable,'%s',' ');
                %disp(Pname2);
                %disp(Evalue);
            end
        end
    end
end
fclose(ReadResult);
fclose(WriteTable);