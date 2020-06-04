import os,sys
import random
nodes={}
#HumanSub.txt
#YeastNet.txt
file=open('YeastNet.txt','r')
lines=file.readlines()
for line  in lines:
	line=line.upper()
	line=line.strip()
	xx=line.split('	')
	if not nodes.has_key(xx[0]):
		nodes[xx[0]]={}
	if not nodes.has_key(xx[1]):
		nodes[xx[1]]={}

	nodes[xx[1]][xx[0]]=1
	nodes[xx[0]][xx[1]]=1
	pass
file.close();
print len(nodes)
if not os.path.exists('subnets'):
	os.mkdir('subnets')
for i in [100,200,300,400,500,600,700]:
	seed = random.randint(0, sys.maxint)
	random.seed(seed)
	if not os.path.exists(('subnets/'+str(i))):
		os.mkdir(('subnets/'+str(i)))
	for j in range(50):
		subnets={};
		print 'start %d=>%d'%(i,j)
		loop=0
		while len(subnets)<i:
			loop=loop+1
			if loop>i*10:
				print 'restart %d=>%d'%(i,j)
				subnets={}
			if len(subnets)==0:
				index=random.randint(0, len(nodes)-1)
				key=nodes.keys()[index]
				while len(nodes[key])<5:
					index=random.randint(0, len(nodes)-1)
					key=nodes.keys()[index]
				subnets[key]=1
				continue
			isnew=False
			loop2=0
			while isnew==False:
				loop2=loop2+1
				if loop2>i*10:
					print 'restart %d=>%d'%(i,j)
					subnets={}
					break
				index=random.randint(0, len(subnets)-1)
				key=subnets.keys()[index]
				index=random.randint(0, len(nodes[key])-1)
				key=nodes[key].keys()[index]
				if subnets.has_key(key):
					continue
				if len(nodes[key])<5:
					continue
				isnew=True
				#print 'find %d cur %d'%(index,len(subnets))
			if loop2>i*10:
				continue
			subnets[key]=1
			pass
		file=open(('subnets/'+str(i)+'/%d_sub.txt')%j,'w')
		for n in subnets.keys():
			file.write((('Yeast2390	%s	%s\n')%(n,n)))
		file.close()
