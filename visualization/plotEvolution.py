import csv
import re
from pylab import *

def find_year(filename):
	matcher = re.search("topics-(\d+)to(\d+).csv", filename)
	return int(matcher.group(1))

data = csv.reader(open("../output/packed_data.csv"))

header = data.next()
years  = map(find_year, header[1:])

for line in data:
	topic = line[0]
	fraction_of_papers = map(float, line[1:])

	figure()
	plot(years, fraction_of_papers, linewidth=1.0, color = 'black')
	axis([min(years), max(years), 0, .18])
	xlabel('Year')
	ylabel('Fraction of papers')
	#title(topic)
	grid(True)
	savefig("../output/topic evolution/"+topic+".pdf")