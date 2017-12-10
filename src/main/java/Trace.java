
public class Trace {
    String id;
    TraceElement root;

    public Trace() {

    }

    public Trace(String id, TraceElement root) {
        this.id = id;
        this.root = root;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TraceElement getRoot() {
        return root;
    }

    public void setRoot(TraceElement root) {
        this.root = root;
    }
}
