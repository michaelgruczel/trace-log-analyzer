
import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

public class TraceElement {

    String service;
    String start;
    String end;
    String span;
    String caller;
    String id;
    List<TraceElement> calls = new ArrayList<>();

    public TraceElement() {

    }

    public TraceElement(String start, String end, String service, String span, String caller) {
        this.start = start;
        this.end = end;
        this.service = service;
        this.span = span;
        this.caller = caller;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public List<TraceElement> getCalls() {
        return calls;
    }

    public void setCalls(List<TraceElement> calls) {
        this.calls = calls;
    }

    public void addCall(TraceElement call) {
        if(calls != null && !calls.isEmpty()) {
            calls.add(call);
        } else {
            calls = new ArrayList<>();
            calls.add(call);
        }
    }


    public String getSpan() {
        return span;
    }

    public void setSpan(String span) {
        this.span = span;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    @Transient
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
