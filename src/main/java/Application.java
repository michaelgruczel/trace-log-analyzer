

public class Application {

    public static void main(String args[]) throws Exception {

        if(args.length != 2) {
            printFileFormatHelp();
        }  else {
            TraceAnalyzer traceAnalyzer = new TraceAnalyzer();
            traceAnalyzer.analyzeFile(args[0], args[1]);
        }
    }

    private static void printFileFormatHelp() {
        System.out.println("Usage: java -jar trace-log-analyzer-all-cli.jar <PATH TO LOGFILE> <PATH TO OUTFILE>");
        System.out.println("File format:");
        System.out.println("[start-timestamp] [end-timestamp] [trace] [service-name] [caller-span]->[span]");
    }
}
