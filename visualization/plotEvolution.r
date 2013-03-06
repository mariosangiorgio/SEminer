#!/usr/bin/env Rscript
library(ggplot2)
library(reshape)

data <-read.table("../data/Geographical.csv", header=T, sep=",", check.names=FALSE)
colnames(data)[1] = "Year"
data.melted = melt(data, id.vars="Year")
p1 <- ggplot(data.melted, aes(x = Year, y = value, colour = variable)) + geom_line() + scale_x_continuous("Year") + scale_y_continuous("Fraction of papers") + scale_colour_discrete("Region") + theme_bw()
ggsave(p1, file = "plot.pdf", width = 6, height = 4)