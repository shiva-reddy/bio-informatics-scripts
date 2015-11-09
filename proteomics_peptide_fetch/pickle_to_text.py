import sys
import pickle
file_name=sys.argv[1][:-1]+"txt"
print file_name
id_list=pickle.load(open(sys.argv[1],"rb"))
write_file=open(file_name,"w")
for x in id_list:
	write_file.write(x+"\n")
write_file.close()