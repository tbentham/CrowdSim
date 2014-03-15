package NewDijkstra;

import WorldRepresentation.Path;
import WorldRepresentation.World;
import org.junit.Before;
import org.junit.Test;

import javax.vecmath.Point2d;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import static org.junit.Assert.fail;

public class aStarTest {

	Integer goalNode;
	Integer startNode;
	int sideLength;
	World w;
	int[][] densityMap;
	FastDijkstra fd;
    AStar aStar;
	
	@Before
	public void setUp() throws Exception {
    	goalNode = 6806;
    	startNode = 5324;
    	sideLength = 100;
    	w = new World(100);
    	w.addWall(32.7, 16.3, 33.7, 56.7);
        w.addWall(32.3, 16.5, 45.9, 16.3);
        w.addWall(55.2, 16.0, 61.3, 15.9);
        w.addWall(33.0, 16.6, 42.5, 16.3);
        w.addWall(47.5, 16.1, 51.8, 16.0);
        w.addWall(56.4, 15.9, 91.3, 15.2);
        w.addWall(96.0, 15.4, 95.9, 59.3);
//        for(int i = 0; i < 15; i++ ) {
//            densityMap[i][3] = 9;
//            densityMap[i][4] = 8;
//        }

    	w.setUp();
    	densityMap = new int[sideLength][sideLength]; 

        aStar = new AStar(sideLength * sideLength, w.getNodeArray(), (ArrayList) w.getEdges(), sideLength);
	}

    @Test
    public void aStarTest() throws Exception {
        try {
            readDensityFromFile();
            printDensityArray(densityMap);
            System.out.println("");
            w.printFloorPlan();
            double min = Double.POSITIVE_INFINITY;
            double dist;
            for (int i = 0; i < w.getWalls().size(); i++) {
                dist = w.getWalls().get(i).distance(new Point2d(startNode / sideLength, startNode % sideLength));
                if (dist < min) {
                    min = dist;
                }
            }
            System.out.println(min);
            min = Double.POSITIVE_INFINITY;
            for (int i = 0; i < w.getWalls().size(); i++) {
                dist = w.getWalls().get(i).distance(new Point2d(50, 0));
                if (dist < min) {
                    min = dist;
                }
            }
            System.out.println(min);
            Path path = aStar.getPath(5324, 6806, densityMap);
            path.printPath();
            System.out.println("");
            printPathOnMap(w.getFloorPlan(), path);
            System.out.println("");
            printDensityArray(densityMap);
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Exception");
        }
    }
    @Test
    public void whichNodes() throws Exception {
        int[][] success = new int[sideLength][sideLength];
        readDensityFromFile();
        printDensityArray(densityMap);
        System.out.println("");
        w.printFloorPlan();
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < sideLength; j++) {
                try {
                    aStar.getPath(i * sideLength + j, goalNode, densityMap);
                    success[i][j] = 1;
                }
                catch (Exception e) {
                    success[i][j] = 0;
                }
            }
            System.out.println(i);
        }
        printDensityArray(success);
    }



    private void printDensityArray(int[][] array) {
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                if (array[j][i] == 0) {
                    System.out.print('\267');
                }
                else {
                    System.out.print(array[j][i]);
                }
            }
            System.out.println();
        }
    }

    private void printPathOnMap(int[][] floorPlan, Path p) {
        char[][] newFloorPlan = new char[sideLength][sideLength];
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                newFloorPlan[i][j] = Integer.toString(floorPlan[i][j]).charAt(0);
            }
        }
        for (int q = 0; q < p.getNodes().size(); q++) {
            int x = p.getNodes().get(q).x;
            int y = p.getNodes().get(q).y;
            newFloorPlan[x][y] = 'P';
        }
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                if (newFloorPlan[j][i] == '0') {
                    System.out.print('\267');
                }
                else {
                    System.out.print(newFloorPlan[j][i]);
                }
            }
            System.out.println();
        }
    }

    private void readDensityFromFile() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("density.txt"));
        int count = 0;
        try {
            String line = br.readLine();
            while (line != null) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                for (int i = 0; i < line.length() - 1; i++) {
                    densityMap[count][i] = Character.getNumericValue(line.charAt(i));
                }
                count++;
            }
        } finally {
            br.close();
        }
    }

}
