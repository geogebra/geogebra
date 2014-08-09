import zipfile
import os.path
import os

# find use of cast in this directory

cast = '(GeoPoint)'

dirs = os.listdir(".")
for file in dirs:
    name = file.split(".")
    if (name[1] == 'java'):
        #if (name[0][0:3] == 'Cmd'):
            #print name[0]
            fichier = open(file, "r")
            startProcess = False
            endProcess = False
            ouvrantes = 0
            fermantes = 0
            index = 0
            indices = []
            for ligne in fichier:
                index += 1
                if ('process' in ligne and 'GeoElement[]' in ligne):
                    startProcess = True
                if (startProcess and not endProcess):
                    if (cast in ligne):
                        #print ligne
                        indices.append(index)
                    ouvrantes += ligne.count('{')
                    fermantes += ligne.count('}')
                    if (ouvrantes == fermantes):
                        endProcess = True


            fichier.close()
            if (len(indices) > 0):
				s = name[0]+" - lines: "
				for l in indices:
					s = s +str(l)+", "
				print s