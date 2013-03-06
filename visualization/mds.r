#!/usr/bin/env Rscript

generate_mds <-function(file){
	similarity	= read.csv(file)
	distance	= 1 - similarity
	mds 		= cmdscale(distance)
	
	pdf(file=paste('../output/',file,'.pdf', sep =""))
	plot(mds, type='n', xlab="", ylab="")
	plot.window(xlim=c(-.8,.8),ylim=c(-.8,.8))
	text(mds, names(distance))
}

setwd("../data")
lapply(list.files(pattern = "*mds.csv"), generate_mds)