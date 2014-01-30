//package WorldRepresentation;
//
//import Dijkstra.Edge;
//import Dijkstra.Vertex;
//import NewDijkstra.Connection;
//import NewDijkstra.FastDijkstra;
//import NewDijkstra.Node;
//import NewDijkstra.NodeRecord;
//import org.jgrapht.util.FibonacciHeap;
//import org.jgrapht.util.FibonacciHeapNode;
//import org.junit.Before;
//import org.junit.Test;
//
//import javax.vecmath.Point2d;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//
//import static junit.framework.Assert.assertNotNull;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//
//public class WorldTest {
//
//    World world;
//
//    @Before
//    public void setUp() {
//        world = new World(100);
//    }
//
//    @Test
//    public void testWorldConstructor() {
//        assertTrue(world.getSideLength() == 100);
//        assertNotNull(world.getWalls());
//        assertTrue(world.getWalls().size() == 0);
//        assertNotNull(world.getFloorPlan());
//        assertTrue(world.getFloorPlan().length == 100);
//        assertTrue(world.getNodeArray().length == 100);
//        assertNotNull(world.getNodes());
//        assertTrue(world.getNodes().size() == 0);
//        assertNotNull(world.getEdges());
//        assertTrue(world.getEdges().size() == 0);
//        assertFalse(world.isSetUp());
//        assertFalse(world.areRoutesComputed());
//    }
//
//    @Test
//    public void addWallAddsWall() throws Exception {
//        world.addWall(0, 0, 5, 5);
//        assertTrue(world.getWalls().size() == 1);
//        assertTrue(world.getWalls().get(0).getStartVector().x == 0);
//        assertTrue(world.getWalls().get(0).getStartVector().y == 0);
//        assertTrue(world.getWalls().get(0).getEndVector().x == 5);
//        assertTrue(world.getWalls().get(0).getEndVector().y == 5);
//    }
//
//    @Test
//    public void addWallAddsPointWall() throws Exception {
//        world.addWall(new Point2d(0, 0), new Point2d(5, 5));
//        assertTrue(world.getWalls().size() == 1);
//        assertTrue(world.getWalls().get(0).getStartVector().x == 0);
//        assertTrue(world.getWalls().get(0).getStartVector().y == 0);
//        assertTrue(world.getWalls().get(0).getEndVector().x == 5);
//        assertTrue(world.getWalls().get(0).getEndVector().y == 5);
//    }
//
//
//    @Test
//    public void pathAlgorithmTimeProfilingTest() throws Exception {
//        long startTime = System.currentTimeMillis();
//
//        ArrayList<Integer> sizesToTest = new ArrayList<>();
//        sizesToTest.add(5);
//        sizesToTest.add(10);
//        sizesToTest.add(25);
//        sizesToTest.add(50);
//        sizesToTest.add(100);
//        sizesToTest.add(250);
//
//        ArrayList<Long> endTimes = new ArrayList<>();
//        ArrayList<Integer> numbersOfEdges = new ArrayList<>();
//
//        for (Integer sz : sizesToTest) {
//            World w = new World(sz);
//            w.setUp();
//            w.computeDijsktraTowards(0, 0);
//            Path p = w.getPath(3, 3);
//            LinkedList<Node> vertices = p.getNodes();
//            endTimes.add(System.currentTimeMillis());
//            numbersOfEdges.add(w.getEdges().size());
//        }
//
//        for (int i = 0; i < sizesToTest.size(); i++) {
//            Integer sz = sizesToTest.get(i);
//            Long timeDiff = endTimes.get(i) - startTime;
//            System.out.format("%dx%d (%d) nodes and %d edges took %dms to compute paths using Old Algorithm\n",
//                    sz, sz, sz * sz, numbersOfEdges.get(i), timeDiff);
//        }
//    }
//
//    @Test
//    public void shitTest() throws Exception {
//        World w = new World(10);
//        w.setUp();
//        w.computeDijsktraTowards(0, 0);
//        Path p = w.getPath(5, 5);
//        System.out.println("");
//    }
//
//    @Test
//    public void newPathAlgorithmTimeProfilingTest() throws Exception {
//
//        long startTime = System.currentTimeMillis();
//
//        ArrayList<Integer> sizesToTest = new ArrayList<>();
//        // sizesToTest.add(5);
//        sizesToTest.add(10);
////        sizesToTest.add(25);
////        sizesToTest.add(50);
////        sizesToTest.add(100);
////        sizesToTest.add(250);
////        sizesToTest.add(500);
//        // sizesToTest.add(1000);
//
//        ArrayList<Long> endTimes = new ArrayList<>();
//        ArrayList<Integer> numbersOfEdges = new ArrayList<>();
//
//        for (Integer sz : sizesToTest) {
//            World w = new World(sz);
//            w.setUp();
//            FastDijkstra fastDijkstra = new FastDijkstra();
//            fastDijkstra.nodes = new ArrayList<FibonacciHeapNode>();
//            fastDijkstra.connections = new HashMap<Integer, ArrayList<Connection>>();
//
//            for (int i = 0; i < sz; i++) {
//                for (int j = 0; j < sz; j++) {
//                    FibonacciHeapNode newNode = new FibonacciHeapNode(new NodeRecord((i * sz) + j));
//                    fastDijkstra.nodes.add(newNode);
//                }
//            }
//
//            for (Edge e : w.getEdges()) {
//                Integer source = (int) Math.round((e.getSource().getX() * sz) + (e.getSource().getY()));
//                Integer destination = (int) Math.round((e.getDestination().getX() * sz) + (e.getDestination().getY()));
//                Double weight = e.getWeight();
//                Connection newConn = new Connection(weight, fastDijkstra.nodes.get(source), fastDijkstra.nodes.get(destination));
//
//                ArrayList<Connection> connections;
//                if (fastDijkstra.connections.containsKey(source)) {
//                    connections = fastDijkstra.connections.get(source);
//                }
//                else {
//                    connections = new ArrayList<Connection>();
//                }
//                connections.add(newConn);
//                fastDijkstra.connections.put(source, connections);
//            }
//
//            FibonacciHeap fibonacciHeap = fastDijkstra.pathFind(0, sz * sz.);
//            Double[] keys = fastDijkstra.keys;
//            endTimes.add(System.currentTimeMillis());
//            numbersOfEdges.add(w.getEdges().size());
//
//            // Note to self: Don't need to change to XY lookup, can just add nodes even when there are walls
//            // Since there wont be any edges for them
//
////            for (int i = 0; i < 100; i++) {
////                for (int j = 0; j < 100; j++) {
////                    System.out.print(roundToSignificantFigures(keys[(i * sz) + j], 3));
////                    System.out.print("\t");
////                }
////                System.out.println();
////            }
//
//        }
//
//        for (int i = 0; i < sizesToTest.size(); i++) {
//            Integer sz = sizesToTest.get(i);
//            Long timeDiff = endTimes.get(i) - startTime;
//            System.out.format("%dx%d (%d) nodes and %d edges took %dms to compute paths using New Algorithm\n",
//                    sz, sz, sz * sz, numbersOfEdges.get(i), timeDiff);
//        }
//    }
//
//    private static double roundToSignificantFigures(double num, int n) {
//        if(num == 0) {
//            return 0;
//        }
//
//        final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
//        final int power = n - (int) d;
//
//        final double magnitude = Math.pow(10, power);
//        final long shifted = Math.round(num*magnitude);
//        return shifted/magnitude;
//    }
//
//}
