#!/usr/bin/python2.4
# --------------------------------------------------------
# Transforms conll08 corpus into the beast format
# --------------------------------------------------------
# Script: conll082beast
# --------------------------------------------------------
# Ivan Vladimir Meza-Ruiz
# 2008/Edinburgh
# --------------------------------------------------------

# * Program arguments control

import sys
import os
import re 
import getopt
from corpus import *
from features import *
import random


def usage():
    print '''\
Usage: extractPennTreeBank.py [options] 
   OUTPUT FILES
   -s|--splits    num        Number of splits [0]  
   -o|--output    filename   Filename of the the data file [outstd]
   -u|--utterance filename   Filename of the the utterances file [outstd]

   INFO TO EXTRACT
   -c|--column            Column to extract [Charniak]

   UTTERANCES
   -b|--begin  num        Starts printing from num line [0]
   -t|--total  num        Total number of utterance to transform [0]
   -r|--randomize         Randomize the corpus [off]

   -v|--verbose           Verbose output

   -h|--help              Prints this message
'''



try:                                
    opts, args = getopt.getopt(sys.argv[1:], "vhrs:o:b:t:u:c:",\
            ["verbose","splits=","output=","begin=","total=","utterance=","column=","randomize","help"])
except getopt.GetoptError:           
    usage()                          
    sys.exit(2)                     

if len(args)!=1:
    usage()
    sys.exit(1)

# Option initialization
filename=args[0]

output = None
utterances   = None
ini=0
total=0
strict = False
randomize = False
nsplits    = 0
open_fmt_file= None
verbose = False
col=7

for opt,val in opts:
    if opt in ("-h","--help"):
        usage()
        sys.exit(0)
    elif opt in ("-s","--splits"):
        nsplits=int(val)
    elif opt in ("-o","--output"):
        output=val;
    elif opt in ("-u","--utterance"):
        utterances=val;
    elif opt in ("-b","--begin"):
        ini=int(val)
    elif opt in ("-c","--column"):
        col=int(val)
    elif opt in ("-t","--total"):
        total=int(val)
    elif opt in ("-s","--strict"):
        strict=True
    elif opt in ("-r","--randomize"):
        randomize=True
    elif opt in ("-v","--verbose"):
        verbose=True


# -------------------------------------------------------------
# Main section
# Prepare splits

size=0
for line in open(filename):
    line=line.strip()
    if len(line) == 0:
        size+=1

sntcs=range(size)

if randomize:
    random.seed()
    sntcs=random(sntcs)

if nsplits==0:    
    splits=[(sntcs,output,utterances)]
else:
    ln=len(sntcs)/nsplits
    splits=[]
    for s in range(nsplits):
        train=[]
        test=[]
        for i in range(len(sntcs)):
            if s*ln<=i and i<(s+1)*ln:
                test.append(i)
            else:
                train.append(i)
        splits.append((train,output+".train."+str(s),utterances+".train."+str(s)))
        splits.append((test,output+".test."+str(s),utterances+".train."+str(s)))

    
    for i in range(len(splits)/2):
        if (len(splits[2*i][0])+len(splits[2*i+1][0]))!=len(sntcs):
            print >> 'Error during spliting'
            sys.exit()

if verbose:
    i=0;
    for s in splits:
        print >> sys.stderr, "Split",i
        print >> sys.stderr, " Starts:",s[0][:10]
        print >> sys.stderr, " Ends:",s[0][-10:]
        i+=1


def par(s):
 s=s.replace("(","-lrb-")
 s=s.replace(")","-rrb-")
 return s

if verbose:
    print >> sys.stderr, "Total of splits: ",len(splits)
nsplit=0
for split,outputn,uttsn in splits:
# loops for splits
    nsplit+=1
    if verbose:
        print >> sys.stderr, "Entering split:",nsplit,"[",outputn,']'
    if outputn == None:
        output = sys.stdout
    else:
        output = open(outputn,"w")

    if uttsn == None:
        utts = sys.stdout
    else:
        utts = open(uttsn,"w")


    num=0
    num_total=0
    id=0
    lines=[]
    for line in open(filename):
    # Main loop for sentences
        line=line.strip()
        if not len(line) == 0:
            bits=line.split()
            lines.append((bits[0],bits[1],bits[col]))
            continue
        else:
            cs=lines
            lines=[]

        # If sntc in the split
        if verbose:
            if id%(corpus.size()/ 50) == 0:
                print >> sys.stderr, ".",
 
        if not id in split:
            id+=1
            continue
        id+=1
       
        if num < ini:
            continue
        num+=1

        if total>0 and num_total>=total:
            break
        num_total+=1

        # Actully doing something
        print >> output, " ".join([ c.replace("*"," (%s %s) ")%(par(b),par(a)) for (a,b,c) in cs ])
        print >> output
        print >> utts, " ".join([ a for (a,b,c) in cs ])
        print >> utts


    if verbose:
        print >> sys.stderr, ""


