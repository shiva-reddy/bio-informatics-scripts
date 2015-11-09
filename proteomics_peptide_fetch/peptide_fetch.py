import urllib2, urllib
import httplib
import base64
import os
import pickle
import shutil
import lxml
from subprocess import call
from lxml import etree

try:
    import ssl
except ImportError:
    print "error: no ssl support"

def zip_and_delete_folder(dir_name):
	shutil.make_archive(dir_name, format="zip", root_dir=dir_name)
	shutil.rmtree(dir_name)

def main():
	USERNAME = "athri"
	PASSWORD = "sr33Amma"
	source_file=open("human_acc.txt","r")
	count=pickle.load(open("status.p","rb"))
	accession_id=source_file.readline().strip()
	skip=count
	while skip:
		accession_id=source_file.readline().strip()
		skip=skip-1

	while accession_id != "":
		
		print "accession_id:- "+accession_id
		print "Count:- "+str(count)
		
		p = accession(USERNAME, PASSWORD,accession_id)
		
		download_dir=get_path(count)
		file_name=download_dir+"/"+str(count)+".xml"
		print file_name
		
		p.connectAndRetrieve(download_dir)
		pickle.dump(count,open("status.p","wb"))

		count=count+1
		if count %100 ==0:
			zip_and_delete_folder(download_dir)	

		accession_id=source_file.readline().strip()

def get_path(count):
	return str(count/100)

def add_to_output_file(accession_id,sequence):
	accession_id=accession_id.split('/')[1][:-4]
	print "Computed sequence is "+sequence
	output_file=open("accession_sequence.txt","a")
	output_file.write(accession_id+"\t"+sequence+"\n")
	output_file.close()

def add_to_coverage_file(accession_id,sequence):
	accession_id=accession_id.split('/')[1][:-4]
	coverage=str((len(sequence)-sequence.count('!'))/float(len(sequence)))
	print "Coverage of the sequence is "+coverage
	output_file=open("accession_coverage.txt","a")
	output_file.write(accession_id+"\t"+coverage+"\n")
	output_file.close()

def compute_sequence(entry_list):
	min_val=entry_list[0][0]
	seq=['!']*(int(entry_list[-1:][0][1])-int(min_val))
	for entry in entry_list:
		r_start=int(entry[0])-int(min_val)
		r_end=int(entry[1])-int(min_val)
		seq[r_start:r_end]=entry[2]
	seq=''.join(seq)
	return seq

def xparse(file):
	tree=etree.parse(file.name)
	root=tree.getroot()
	entry_list=[]
	all_entries=root.findall('{http://www.w3.org/2005/Atom}entry')
	for entry in all_entries:
		START_POSITION=entry[5][0][0].text
		END_POSITION=entry[5][0][1].text
		PEPTIDE_SEQUENCE=entry[5][0][2].text
		entry_tup=(START_POSITION,END_POSITION,PEPTIDE_SEQUENCE)
		entry_list.append(entry_tup)
	if len(entry_list):
		sequence=compute_sequence(entry_list)
		add_to_output_file(file.name,sequence)
		add_to_coverage_file(file.name,sequence)
		return entry_list
	else:
		return False

class accession():

	def __init__(self, username, password,accession_id):
		self.accession_id=accession_id
		self.default_headers = { "Authorization" : "Basic %s" % base64.encodestring( "%s:%s" % ( username, password) ).rstrip('\n') }
		self.port = 443
		self.host = 'www.proteomicsdb.org'
		self.url="https://www.proteomicsdb.org/proteomicsdb/logic/api/proteinpeptideresult.xsodata/InputParams(PROTEINFILTER='"+accession_id+"')/Results?$select=ENTRY_NAME,PROTEIN_NAME,UNIQUE_IDENTIFIER,CHROMOSOME_NAME,GENE_NAME,STRAND,PEPTIDE_SEQUENCE,START_POSITION,END_POSITION&$filter=PEPTIDE_MASS%20gt%201000%20&$format=xml"
		#print self.url

	def connectAndRetrieve(self,directory):
		if not os.path.exists(directory):
			print "Created "+directory+" directory"
			os.makedirs(directory)
		hconn = httplib.HTTPSConnection( "%s:%d" % (self.host,self.port) )
		hconn.request("GET", self.url, headers = self.default_headers)
		#print self.url
		resp = hconn.getresponse()
		#print resp.status, resp.reason
		if resp.status == 200:
			print "Api connection successfully established..."
		print "Downloading "+self.accession_id+" ",
		body = resp.read()
		print "...Done"
		write_file=open(directory+"/"+self.accession_id+".xml","w")
		write_file.write(body)
		write_file.close()
		print "Parsing "+self.accession_id
		parse_result=xparse(write_file)

		if parse_result == False:
			os.remove(write_file.name)
			current_list=pickle.load(open("invalid_prot.p","rb"))
			current_list.append(self.accession_id)
			pickle.dump(current_list,open("invalid_prot.p","wb"))
			print "Dumped "+self.accession_id

		else:
			current_list=pickle.load(open("valid_prot.p","rb"))
			current_list.append(self.accession_id)
			pickle.dump(current_list,open("valid_prot.p","wb"))
			print "Added "+self.accession_id+" sequence"

		hconn.close()
    
if __name__ == "__main__":
	main()


