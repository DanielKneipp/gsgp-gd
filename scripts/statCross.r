calcMeamStd <- function(file, n_gens, out_file_str) {
  data <- list()

  con = file(file, "r")
  while(TRUE) {
    line <- readLines(con, n=1)
    if(length(line) == 0) {
      break
    }
    if(line == "") {
      next
    }

    line_data <- as.integer(unlist(strsplit(line, ",")))
    generation <- line_data[2]
    line_data <- line_data[-c(1, 2)] # Removing the first and second positions (first pos: denotes the repetition - not used info)

    if(generation > length(data)) {
      data[[generation]] <- line_data
    } else {
      data[[generation]] <- c(data[[generation]], line_data)
    }
  }

  file_conn <- file(out_file_str, "w")
  write("generation,mean,std", file=file_conn, append=TRUE)

  for(gen in 1:n_gens) {
    m <- mean(data[[gen]])
    s <- sd(data[[gen]])
    out_str <- paste(gen,",",m,",",s, sep="")
    write(out_str, file=file_conn, append=TRUE)
  }
}

printHelp <- function() {
  print("Usage: Rscript statCross.r -f <crossover-data-file> -n <number-of-generations> -o <output-file>")
}

library("optparse")

option_list <- list(
  make_option(c("-f", "--file"), type="character", default=NULL,
              help="Results path", metavar="character"),
  make_option(c("-n", "--num_gens"), type="integer", default=NULL,
              help="Number of generations", metavar="integer"),
  make_option(c("-o", "--output_file"), type="character", default=NULL,
              help="Output file", metavar="character")
              )

opt_parser <- OptionParser(option_list=option_list)
opt_args <- parse_args(opt_parser)

if (is.null(opt_args$file)) {
  print_help(opt_parser)
  stop("At least one argument must be supplied (results path)", call.=FALSE)
}

if (is.null(opt_args$num_gens)) {
  print_help(opt_parser)
  stop("The number of generations must be passed", call.=FALSE)
}

if (is.null(opt_args$output_file)) {
  print_help(opt_parser)
  stop("The output file must be passed", call.=FALSE)
}

calcMeamStd(file=opt_args$file, n_gens=opt_args$num_gens, out_file_str=opt_args$output_file)