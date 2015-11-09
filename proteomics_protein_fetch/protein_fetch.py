import urllib2, urllib
import httplib;
import json
import base64
import os
from subprocess import call

try:
    import ssl
except ImportError:
    print "error: no ssl support"
        
def main():
	print "Enter mysql username and password. "
	print "User:-", 
	m_user=raw_input()
	print "Password:-"
	m_pass=getpass.getpass()

	print "Enter your proteomics user name:- "
	USERNAME = raw_input()
	print "Enter your proteomics password:-"
	PASSWORD = getpass.getpass()

	source_file=open(argv[1],"r")
	tissue_id=source_file.readline().strip()
	
	prev=""
	count=0
	while tissue_id != "":
		if tissue_id != prev:
			count=count+1
			print "Tissue ID:- "+tissue_id
			print "Count:- "+str(count)
			p = tissue(USERNAME, PASSWORD,tissue_id)
			p.connectAndRetrieve(m_user,m_pass)

			status_file=open("status.txt","a")
			status_file.write(tissue_id)
			status_file.write(str(count)+"\n")
			status_file.close()
		prev=tissue_id
		tissue_id=source_file.readline().strip()


class tissue():

	def __init__(self, username, password,tissue_id):
		self.tissue_id=tissue_id
		self.default_headers = { "Authorization" : "Basic %s" % base64.encodestring( "%s:%s" % ( username, password) ).rstrip('\n') }
		self.port = 443
		self.host = 'www.proteomicsdb.org'
		self.url="https://www.proteomicsdb.org/proteomicsdb/logic/api/proteinspertissue.xsodata/InputParams(TISSUE_ID='"+tissue_id+"',CALCULATION_METHOD=0,SWISSPROT_ONLY=1,NO_ISOFORM=1)/Results?$select=ENTRY_NAME,UNIQUE_IDENTIFIER,DATABASE,PROTEIN_DESCRIPTION,PEPTIDES,SAMPLE_NAME,SAMPLE_DESCRIPTION,UNNORMALIZED_EXPRESSION,NORMALIZED_EXPRESSION&$format=xml"
		#print self.url

	def connectAndRetrieve(self,m_user,m_pass):
		file_name="temp.xml"
		hconn = httplib.HTTPSConnection( "%s:%d" % (self.host,self.port) )
		hconn.request("GET", self.url, headers = self.default_headers)
		#print self.url
		resp = hconn.getresponse()
		#print resp.status, resp.reason
		if resp.status == 200:
			print "Api connection successfully established..."
		print "Downloading "+self.tissue_id
		body = resp.read()
		#soup=BeautifulSoup(body)
		print "...Done"
		write_file=open(file_name,"w")
		write_file.write(body)
		write_file.close()
		print "Parsing "+self.tissue_id
		#call(["java","-cp",".:/home/prashanth/proteomics_shiva/mysql-connector-java-5.0.8-bin.jar","proteinParser","x","root","A!pink@elephant#"])
		call(["java","-cp",".:/home/prashanth/proteomics_shiva/mysql-connector-java-5.0.8-bin.jar","proteinParser",self.tissue_id,m_user,m_pass])
		hconn.close()
    
if __name__ == "__main__":
	main()


