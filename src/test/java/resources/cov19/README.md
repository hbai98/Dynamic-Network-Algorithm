## files

There're two files that needs caution. 

* COV19_hosts_more_0.7_slice.txt  

  proteins: 227, edges: 504

* humanPPI_score_more_0.7_slice.txt

  proteins: 1496, edges: 73114

0.7 means scores indicating confidence of interactions in biomolecule networks, which rank from `0` - `1`. Score > 0.7 will filter out connections with high confidence. [[Website](https://www.string-db.org/cgi/info.pl?footer_active_subpage=scores)]

* After filtering, as some interactions on humanPPI has been deleted, CoV19_host protein number => `208`

Slicing has been done by python from jupyter lab. All nodes' IDs have been cut, during which '9606.ENSP00000' has been removed from the head of each string.

## Folder

Cytoscape folder contains network files that are supported by IO in Cytoscape. 

* cyto_networks.cys 

  networks in cytoscape

* humanPPI_vs_Host_DKernel_scores.csv  

  DKernel scores

* nCoV_host_info.txt

  host proteins vertex set in humanPPI_score_more_0.7

