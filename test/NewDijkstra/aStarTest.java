
package NewDijkstra;

import WorldRepresentation.FloorConnection;
import WorldRepresentation.Path;
import WorldRepresentation.World;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;

public class aStarTest {

    private Integer startNode;
    private int sideLength;
    private World w;
    private int[][][] densityMap;
    private AStar aStar;
    private ArrayList<FloorConnection> floorConnections;

    @Before
    public void setUp() throws Exception {
        floorConnections = new ArrayList<FloorConnection>();
        floorConnections.add(new FloorConnection(25.0, 25.0, 0, 1));
        floorConnections.add(new FloorConnection(50.0, 50.0, 0, 1));
        startNode = 12688;
        sideLength = 100;
        w = new World(100, 2);
        w.addFloorConnections(floorConnections);
        w.addWall(35.7, 65.2, 35.5, 16.2, 0);
        w.addWall(35.5, 16.2, 39.5, 16.3, 0);
        w.addWall(42.2, 16.6, 45.3, 16.6, 0);
        w.addWall(48.6, 16.6, 72.0, 16.7, 0);
        w.addWall(72.0, 16.7, 99.6, 104.1, 0);
        w.addWall(35.2, 65.3, 2.5, 14.6, 0);
        w.addWall(7.6, 12.7, 0, 16.1, 0);
        densityMap = new int[sideLength][sideLength][2];
        for (int i = 0; i < 15; i++) {
            densityMap[i][3][0] = 9;
            densityMap[i][4][0] = 8;
        }

        w.setUp();

        aStar = new AStar(2 * sideLength * sideLength, w.getEdges(), sideLength);
    }

    @After
    public void tearDown() {
        aStar = null;
        floorConnections = null;
        w = null;
        densityMap = null;
    }

    @Test
    public void aStarReturnsValidConnectedPath() throws Exception {
        Path path = aStar.getPath(startNode, 0, 0, 0, densityMap, floorConnections);
        ArrayList<aConnection> connections = aStar.getConnections().get(startNode);
        boolean found = false;
        for (aConnection ac : connections) {
            Integer nodeValue = (path.getNodes().get(0).getZ() * sideLength * sideLength) +
                    path.getNodes().get(0).getX() * sideLength + path.getNodes().get(0).getY();
            if (ac.getFrom().equals(startNode) && ac.getTo().equals(nodeValue)) {
                found = true;
                break;
            }
        }
        assertTrue(found);

        for (int i = 0; i < path.getNodes().size() - 1; i++) {
            Node node = path.getNodes().get(i);
            Node toNode = path.getNodes().get(i + 1);
            Integer nodeValue = node.getZ() * (sideLength * sideLength) +
                    node.getX() * sideLength + node.getY();
            Integer toNodeValue = toNode.getZ() * (sideLength * sideLength) +
                    toNode.getX() * sideLength + toNode.getY();
            connections = aStar.getConnections().get(nodeValue);
            found = false;
            for (aConnection ac : connections) {
                if (ac.getFrom().equals(nodeValue) && ac.getTo().equals(toNodeValue)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }
}
