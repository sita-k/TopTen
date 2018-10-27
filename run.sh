#!/bin/bash

pwd
#javac ./TopTen/src/TopTen.java
java -classpath ./TopTen/src/ TopTen input/H1B_FY_2015.csv output/top_10_occupations.txt output/top_10_states.txt
#java TopTen input/H1B_FY_2015.csv output/top_10_occupations.txt output/top_10_states.txt
