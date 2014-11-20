r = open("testcase11.cnf","r")
w = open("new.cnf","w")
for line in r.readlines():
	w.write(line[:-2]+"\n")
