#Testing scipt for biopython.Takes 'uniprot fasta file' and protein_accession_number as an argument, and prints the sequence of the protein.
#This program can be modified to suit specific needs.

from Bio import SeqIO
import sys
def main():
	fasta_file_name=sys.argv[1]
	fasta_sequences = SeqIO.parse(open(fasta_file_name),'fasta')
	request=str(sys.argv[2])
	for fasta in fasta_sequences:
		if request == fasta.id[3:9]:
			print str(fasta.seq)
			break
if __name__ == "__main__":
    main()	
