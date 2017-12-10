import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TraceAnalyzer {

    public void analyzeFile(String inputFilePath, String outputFilePath) throws Exception {
        // read file in
        Stream<String> inStream = readFile(inputFilePath);
        // calculate graph of traces
        List<Trace> mappedList = calculate(inStream);
        // write final results
        writeResult(mappedList, outputFilePath);
    }

    protected Stream<String> readFile(String filePath) throws Exception {
        return Files.lines(Paths.get(filePath));
    }

    protected List<Trace> calculate(Stream<String> inStream) {
        // let's convert lines to objects
        Stream<TraceElement> traceElement = inStream.parallel().map(x -> convertLineToTraceElement(x));
        // let's group them by id, then we can work on them in parallel independent
        Map<String, List<TraceElement>> elements = traceElement
                .collect(Collectors.groupingBy(TraceElement::getId));
        // time to calc the graph
        return calculateGraph(elements);
    }

    protected void writeResult(List<Trace> mappedList, String outputFilePath) throws Exception {
        mappedList.stream().parallel().forEach(x -> writeTraceLine(outputFilePath, x));
    }

    private TraceElement convertLineToTraceElement(String aLine) {
        String[] parts = aLine.split(" ");
        String start = parts[0];
        String end = parts[1];
        String traceid = parts[2];
        String service = parts[3];
        String communication = parts[4];
        String[] communicationParticipants = communication.split("->");
        String callerSpan = communicationParticipants[0];
        String span = communicationParticipants[1];
        TraceElement aTraceElement = new TraceElement(start, end, service, span, callerSpan);
        aTraceElement.setId(traceid);
        return aTraceElement;
    }



    private List<Trace> calculateGraph(Map<String, List<TraceElement>> elements) {

        List<Trace> mappedList = elements.entrySet().stream().parallel().map(entry -> calculateGraph(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        return mappedList;
    }

    private Trace calculateGraph(String aKey, List<TraceElement> elementsToMap) {
        TraceElement root = null;
        for (TraceElement anElement : elementsToMap) {
            if (anElement.getCaller().equalsIgnoreCase("null")) {
                root = anElement;
                break;
            }
        }
        elementsToMap.remove(root);
        Trace aTrace = new Trace(aKey, mapNodes(root, elementsToMap));
        return aTrace;

    }

    private TraceElement mapNodes(TraceElement origin, List<TraceElement> nodes) {

        if (nodes.isEmpty()) {
            return origin;
        }
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getCaller().equalsIgnoreCase(origin.getSpan())) {
                // that's a child
                // so add it as child and execute function on child
                if (i == 0) {
                    List<TraceElement> reducedList = nodes.subList(1, nodes.size());
                    TraceElement subTree = mapNodes(nodes.get(i), reducedList);
                    origin.addCall(subTree);
                } else if (i == (nodes.size() - 1)) {
                    List<TraceElement> reducedList = nodes.subList(0, nodes.size() - 1);
                    TraceElement subTree = mapNodes(nodes.get(i), reducedList);
                    origin.addCall(subTree);
                } else {
                    List<TraceElement> reducedListBefore = nodes.subList(0, i);
                    List<TraceElement> reducedListAfter = nodes.subList(i + 1, nodes.size());
                    List<TraceElement> reducedList = new ArrayList<TraceElement>(reducedListBefore);
                    reducedList.addAll(reducedListAfter);
                    TraceElement subTree = mapNodes(nodes.get(i), reducedList);
                    origin.addCall(subTree);
                }
            }
        }
        return origin;
    }

    private void writeTraceLine(String outputFilePath, Trace aTrace) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            FileWriter fw = new FileWriter(outputFilePath, true);
            System.out.println(mapper.writeValueAsString(aTrace));
            fw.write(mapper.writeValueAsString(aTrace) + "\n");
            fw.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }
}
