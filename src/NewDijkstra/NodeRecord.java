package NewDijkstra;

public class NodeRecord {

    public Integer node;
    public Integer predecessor;

    public NodeRecord(Integer node) {
        this.node = node;
        predecessor = null;
    }
}
