package NewDijkstra;

import WorldRepresentation.World;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class FastDijkstraTest {

    private Integer startNode;
    private World w;
    private FastDijkstra fastDijkstra;

    @Before
    public void setUp() {
        startNode = 2688;
        w = new World(100, 1);
        w.addWall(35.7, 65.2, 35.5, 16.2, 0);
        w.addWall(35.5, 16.2, 39.5, 16.3, 0);
        w.addWall(42.2, 16.6, 45.3, 16.6, 0);
        w.addWall(48.6, 16.6, 72.0, 16.7, 0);
        w.addWall(72.0, 16.7, 99.6, 104.1, 0);
        w.addWall(35.2, 65.3, 2.5, 14.6, 0);
        w.addWall(7.6, 12.7, 0, 16.1, 0);
        w.setUp();
        fastDijkstra = new FastDijkstra();
    }

    @After
    public void tearDown() {
        w = null;
        fastDijkstra = null;
    }

    @Test
    public void pathFindsConnectedPaths() {
        fastDijkstra.pathFind(startNode, 10000, w);
        NodeRecord nodeRecord = (NodeRecord) fastDijkstra.getNodes().get(0).getData();
        while (nodeRecord.predecessor != null) {
            Integer predValue = nodeRecord.predecessor;
            Integer nodeValue = nodeRecord.node;
            boolean found = false;
            for (Connection connection : fastDijkstra.getConnections().get(nodeValue)) {
                Integer fromNode = ((NodeRecord) connection.getFrom().getData()).node;
                Integer toNode = ((NodeRecord) connection.getTo().getData()).node;
                if (fromNode.equals(nodeValue) && toNode.equals(predValue)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
            nodeRecord = (NodeRecord) fastDijkstra.getNodes().get(predValue).getData();
        }
        assertTrue(nodeRecord.node.equals(startNode));
    }
}
