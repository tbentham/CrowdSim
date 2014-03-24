package NewDijkstra;

public class NodeRecord implements Comparable<NodeRecord> {

    public Integer node;
    public Integer predecessor;
    public double value;

    public NodeRecord(Integer node) {
        this.node = node;
        predecessor = null;
        value = 0.0;
    }

    public int compareTo(NodeRecord other) {
        return Double.compare(value, other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        final NodeRecord other = (NodeRecord) o;
        return this.node.equals(other.node);
    }

}
