This project aims to download all protein information per tissue, available 
in proteomicsDB. This is accomplished by first fetching all tissue ids available 
in proteomicsDB. 
The file generated is the input for protein_fetch.py, which parses each xml using the java script proteinParser.java .

Description:-

1.protein_fetch.py:- Takes a list of tissueIds as input and fetches protein information 
from proteomicsDB.

2.proteinParser.java:- Xml parsing helper for protein_fetch.py.

3.getTissueIds.java :- Parsing the xml returned by proteomicsDB with all tissue ids and corresponding tissue names.

