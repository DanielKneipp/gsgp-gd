# Plot graphic with data generated.
plotGraphic <- function(path, last.iteration=NA, n.executions=NA) {
  
  file <- read.table(paste(path,"/stats.csv",sep=""), sep=",", header=F)

  if (is.na(last.iteration)) {
    n <- dim(file)[2]
  }

  if (is.na(n.executions)) {
    n.executions <- dim(file)[1]
  }

  problem <- rep(file[2:n.executions,1])
  mean <- file[2:n.executions,2:3]
  std <- file[2:n.executions,4:5]
  median <- file[2:n.executions,6:7]
  iqr <- file[2:n.executions,8:9]

  plot("graphicMean.jpg", mean, (n.executions - 1), 2, list(c("tr_mean", "ts_mean"), problem), problem, "Mean Graphic of the Problems")
  plot("graphicStandardDeviation.jpg", std, (n.executions - 1), 2, list(c("tr_std", "ts_std"), problem), problem, "Standard Deviation Graphic of the Problems")
  plot("graphicMedian.jpg", median, (n.executions - 1), 2, list(c("tr_median", "ts_median"), problem), problem, "Median Graphic of the Problems")
  plot("graphicIQR.jpg", iqr, (n.executions - 1), 2, list(c("tr_IQR", "ts_IQR"), problem), problem, "IQR Graphic of the Problems")

}

plot <- function(name, data, row, column, dimnames, problem, title){

  jpeg(filename = name)

  vector <- unlist(data, use.names = FALSE)

  color <- c(1:column)
  dataGraphic<- matrix(as.numeric(paste(vector)), nrow=column, ncol=row, dimnames=dimnames, problem)
  barplot(dataGraphic, beside=TRUE, col=color, main=title ,ylab="valor", xlab="medidas")
  legend("topleft", legend=unlist(dimnames[1], use.names = FALSE), fill=color, bty="n")

}

library("optparse")

option_list <- list(
  make_option(c("-p", "--path"), type="character", default=NULL,
              help="Results path", metavar="character")
              )

opt_parser <- OptionParser(option_list=option_list)
opt_args <- parse_args(opt_parser)

if (is.null(opt_args$path)) {
  print_help(opt_parser)
  stop("At least one argument must be supplied (results path)", call.=FALSE)
}

plotGraphic(path=opt_args$path)