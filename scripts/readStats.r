# ============= It generates averages, STDs, medians and IQRs for a folder with several results (tested instances) ================
getFolderRMSEs <- function(path, last.iteration=NA, n.executions=NA) {
  pastas <- list.dirs(path, full.names=TRUE, recursive=F)
  n <- last.iteration
  out_file_str <- paste(path,"/stats.csv", sep="")
  file_conn <- file(out_file_str, "w")

  write("problem,tr_mean,ts_mean,tr_std,ts_std,tr_median,ts_median,tr_IQR,ts_IQR", file=file_conn, append=TRUE)

  for (i in pastas) {
    str <- getRMSE(i, last.iteration, n.executions)
    write(str, file=file_conn, append=TRUE)
  }

  close(file_conn)
}

# Given a path to a folder with GSGP output print on the screen the mean/SD or median/IQR 
getRMSE <- function(path, last.iteration=NA, n.executions=NA) {
  tr <- read.table(paste(path,"/trFitness.csv",sep=""), sep=",", header=F)
  ts <- read.table(paste(path,"/tsFitness.csv",sep=""), sep=",", header=F)

  if (is.na(last.iteration)) {
    n <- dim(tr)[2]
  }

  if (is.na(n.executions)) {
    n.executions <- dim(tr)[1]
  }

  tr_median <- median(tr[1:n.executions,n])
  ts_median <- median(ts[1:n.executions,n])
  tr_IQR <- IQR(tr[1:n.executions,n])
  ts_IQR <- IQR(ts[1:n.executions,n])
  tr_mean <- mean(tr[1:n.executions,n])
  ts_mean <- mean(ts[1:n.executions,n])
  tr_std <- sd(tr[1:n.executions,n])
  ts_std <- sd(ts[1:n.executions,n])

  return(paste(basename(path),",",tr_mean,",",ts_mean,",",tr_std,",",ts_std,",",tr_median,",",ts_median,",",tr_IQR,",",ts_IQR, sep=""))
}

printHelp <- function() {
  print("Usage: Rscript readStats.r <instances-results-path>")
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

getFolderRMSEs(path=opt_args$path)