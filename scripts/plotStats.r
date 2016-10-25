# Plot graphic with data generated.
plotGraphic <- function(path, last.iteration=NA, n.executions=NA) {
  
  file <- read.table(paste(path,"/stats.csv",sep=""), sep=",", header=F)

  if (is.na(last.iteration)) {
    n <- dim(file)[2]
    #print(n);
  }

  if (is.na(n.executions)) {
    n.executions <- dim(file)[1]
    #print(n.executions);
  }

  problem <- rep(file[2:n.executions,1])
  mean <- file[2:n.executions,2:3]
  std <- file[2:n.executions,4:5]
  median <- file[2:n.executions,6:7]
  iqr <- file[2:n.executions,8:9]

  plot("graphicMean.jpg", mean, (n.executions - 1), 2, list(problem, c("tr_mean", "ts_mean")), problem, "Mean Graphic of the Problems")
  plot("graphicStandardDeviation.jpg", std, (n.executions - 1), 2, list(problem, c("tr_std", "ts_std")), problem, "Standard Deviation Graphic of the Problems")
  plot("graphicMedian.jpg", median, (n.executions - 1), 2, list(problem, c("tr_median", "ts_median")), problem, "Median Graphic of the Problems")
  plot("graphicIQR.jpg", iqr, (n.executions - 1), 2, list(problem, c("tr_IQR", "ts_IQR")), problem, "IQR Graphic of the Problems")

}

plot <- function(name, data, row, column, dimnames, problem, title){

  jpeg(filename = name)

  vector <- unlist(data, use.names = FALSE)

  #if(row == 1){
     color <- c(1:row)
     #dimnamesAux = list(unlist(dimnames[2], use.names = FALSE), unlist(dimnames[1], use.names = FALSE))
     dataGraphic<- matrix(as.numeric(paste(vector)), nrow=row, ncol=column, dimnames=dimnames, problem)
     barplot(dataGraphic, beside=TRUE, col=color, main=title ,ylab="valor", xlab="medidas")
     legend("topleft", legend=unlist(dimnames[1], use.names = FALSE), fill=color, bty="n")

  #barplot(data, beside = TRUE, legend = rownames(problem))

  

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