#!/usr/bin/env Rscript

require("stringr")

plot_trend <- function(trend, max_value){
	topic		<- trend[1,1]
	data_frame	<- trend[2:length(trend)]
	data_values	<- c(t(data_frame))
	labels		<- str_match(colnames(data_frame),".*(\\d{4})to\\d{4}\\.csv")[,2]
	pdf(file=paste("~/Documents/workspace/SEminer/output/topic evolution/",topic,".pdf",sep=""))
	plot(data_values,
		 axes	= FALSE,
		 ann	= FALSE,
		 ylim	= c(0, max_value),
		 type	= "o")
	axis(1, 1:length(labels), lab = labels)
	axis(2, las=1)
	title(main = topic, xlab = "Years", ylab="Fraction of papers")
	dev.off()
	}

data = read.csv("~/Documents/workspace/SEminer/output/packed_data.csv")
max_value = max(data[,2:length(data)])
for(i in seq(1,nrow(data))) plot_trend(data[i,], max_value)