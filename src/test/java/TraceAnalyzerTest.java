import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Stream<String> expectedOutPut = getLinesAsStream(new File(getClass().getClassLoader().getResource(resultfile).getPath()).getAbsolutePath());
        Map<String, Trace> expectedElements = new HashMap<>();
        expectedOutPut.forEach(anObject -> convertAndAddElement(expectedElements, anObject));
        // let's convert booth lists to maps, because it is faster to compare
        Map<String, Trace> realElements = new HashMap<>();
        result.forEach(anObject -> addElement(realElements, anObject));

        Assert.assertEquals(expectedElements.size(), result.size());
        for (String traceId : expectedElements.keySet()) {
            Trace realTrace = realElements.get(traceId);
            Trace expectedTrace = expectedElements.get(traceId);
            Assert.assertTrue(realTrace != null);
            Assert.assertEquals(realTrace.getRoot().getService(), expectedTrace.getRoot().getService());
            Assert.assertEquals(realTrace.getRoot().getSpan(), expectedTrace.getRoot().getSpan());
            Assert.assertEquals(realTrace.getRoot().getStart(), expectedTrace.getRoot().getStart());
            Assert.assertEquals(realTrace.getRoot().getEnd(), expectedTrace.getRoot().getEnd());
            compareCalls(realTrace.getRoot().getCalls(), expectedTrace.getRoot().getCalls());

        }
    }

    private void convertAndAddElement(Map<String, Trace> expectedElements, String anObject) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Trace obj = mapper.readValue(anObject, Trace.class);
            addElement(expectedElements, obj);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    private void addElement(Map<String, Trace> expectedElements, Trace anObject) {
        expectedElements.put(anObject.getId(), anObject);

    }

    private List<Trace> whenFileIsAnalyzed(String inputPath) throws Exception {
        Stream<String> in = testee.readFile(inputPath);
        return testee.calculate(in);

    }

    private String givenInputFile(String file) {
        testee = new TraceAnalyzer();
        return new File(getClass().getClassLoader().getResource(file).getPath()).getAbsolutePath();
    }

    private void compareCalls(List<TraceElement> callsReal, List<TraceElement> callsExpected) {

        // same amount of children
        if (callsReal != null && callsExpected != null) {
            Assert.assertEquals(callsReal.size(), callsExpected.size());
            for (TraceElement anExpectedElement : callsExpected) {
                boolean foundSameElement = false;
                for (TraceElement aRealCall : callsReal) {
                    if (anExpectedElement.getSpan().equalsIgnoreCase(aRealCall.getSpan())) {
                        foundSameElement = true;
                        compareCalls(aRealCall.getCalls(), anExpectedElement.getCalls());
                    }
                }
                Assert.assertTrue(foundSameElement);
            }

        } else {
            Assert.assertTrue(callsReal == null && callsExpected == null);
        }
    }

    Stream<String> getLinesAsStream(String filePath) throws Exception {
        return Files.lines(Paths.get(filePath));
    }


}
