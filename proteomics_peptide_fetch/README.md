
Proteome peptide sequence fetcher
=================================


The objective is to generate the sequences and calculate coverage for all 
proteins for which data is available in proteomics. 
The input file is '*human_acc.txt*' which is a list of all protein accessions found 
in human uniprot db. 

To run it:-
python initial_settings.py
python peptide_fetch.py 

The following are the output files:-

1.*accession_coverage.txt*:- This file consists a list of all accession_ids for which proteomics has data followed by their corresponding protein coverage.

2.*accession_coverage.txt*:- This file consists a list of all accession_ids for which proteomics has data followed by their corresponding sequence.

3.*invalid_prot.p*:- This pickle file consists of a python list of all protein accessions
which return an error result on proteomics.

4.*valid_prot.p*:- This pickle file consists of a python list of all protein accessions
which return a positive result on proteomics.

5.*status.p*:- This is a status file which contains the count of the number of accessions
from *human_acc.txt* that have been hit.

To convert the pickle files to text files:-
**python pickle_to_text.py filename**
