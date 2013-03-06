#!/usr/bin/python

import csv
import sys
from collections import defaultdict

packed_data = defaultdict(lambda : defaultdict(lambda: 0))

file_list = sys.argv[1:]
non_empty_files = []
totals    = {}
for file_name in file_list:
	csv_file = open(file_name, 'rb')
	data	 = csv.reader(csv_file)
	total = 0
	for entry in data:
		topic = entry[0]
		count = float(entry[1])
		packed_data[topic][file_name] = count
		total = total + count
	if total != 0:
		totals[file_name] = total
		non_empty_files.append(file_name)
	csv_file.close()

output_file = open('packed_data.csv','w')
writer		= csv.writer(output_file)
writer.writerow(["Topic"]+non_empty_files)
for topic in packed_data.keys():
	writer.writerow([topic] + [packed_data[topic][file_name]/float(totals[file_name]) for file_name in non_empty_files])
output_file.close()
