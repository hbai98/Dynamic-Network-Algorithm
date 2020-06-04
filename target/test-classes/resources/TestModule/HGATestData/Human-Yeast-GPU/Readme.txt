Yeast and Human Netwoks are from authors of MI-GRAAL.
(Reference:Kuchaiev, Oleksii, and Nata¡¦sa Pr¡¦zulj1. "Integrative network alignment reveals large regions of global network similarity in yeast and human." Bioinformatics 27.10 (2011): 1390-1396.)

Yeast network, named "YeastNet.txt", consists of 2390 nodes and 16127 edges.
HumanSub network, named "HumanSub.txt", consists of 4461 nodes and 11128 edges. It is the largest connected subnetwork extracted from the "HumanNet.txt".
The file "table.Yeast-Human.txt" is the homologous table between Yeast and Human, obtained by the matlab program "GetTable.m" from YEAST-uniprot-20120518.fasta and Human-20120513.fasta.
==========MIPS files==============
The MIPS file ("MIPS.txt") contains all proteins in YeastNet.txt,and 'Yeast-2390' is the network name.
Each MIPS file in "subnets" directory contains proteins in corresponding yeast sub-network. They are obtained by "subnets/getdata.py".
Each sub-directory in subnets contains 50 sub-networks which have the same number of nodes. For example, the sub-directory "100" includes 50 sub-networks, and each of them has 100 nodes.