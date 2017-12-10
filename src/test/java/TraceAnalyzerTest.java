import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

public class TraceAnalyzerTest {

    private TraceAnalyzer testee;


    @Test
    public void test_small_sample() throws Exception {

        String inputPath = givenInputFile("small-log.txt");
        List<Trace> res = whenFileIsAnalyzed(inputPath);
        thenResultEqualToResultFile(res, "small-traces.txt");
    }

    @Test
    public void test_medium_sample() throws Exception {

        String inputPath = givenInputFile("medium-log.txt");
        List<Trace> res = whenFileIsAnalyzed(inputPath);
        thenResultEqualToResultFile(res, "medium-traces.txt");
    }

    private void thenResultEqualToResultFile(List<Trace> result, String resultfile) throws Exception {
        //(
    }

    private List<Trace> whenFileIsAnalyzed(String inputPath) throws Exception {
        Stream<String> in = testee.readFile(inputPath);
        return testee.calculate(in);

    }

    private String givenInputFile(String file) {
        testee = new TraceAnalyzer();
        return new File(getClass().getClassLoader().getResource(file).getPath()).getAbsolutePath();
    }


}
