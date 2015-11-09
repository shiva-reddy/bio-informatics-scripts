import pickle
import os
if os._exists("accession_sequence.txt"):
	os.remove("accession_sequence.txt")
if os._exists("accession_coverage.txt"):
	os.remove("accession_coverage.txt")	
status=0
pickle.dump(status,open("status.p","wb"))
l=[]
pickle.dump(l,open("valid_prot.p","wb"))
l=[]
pickle.dump(l,open("invalid_prot.p","wb"))