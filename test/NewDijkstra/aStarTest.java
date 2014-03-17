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
    	goalNode = 0;     // 6806?
    	startNode = 2688;
    	sideLength = 100;
    	w = new World(100);
    	w.addWall(35.7, 65.2, 35.5, 16.2);
        w.addWall(35.5, 16.2, 39.5, 16.3);
        w.addWall(42.2, 16.6, 45.3, 16.6);
        w.addWall(48.6, 16.6, 72.0, 16.7);
        w.addWall(72.0, 16.7, 99.6, 104.1);
        w.addWall(35.2, 65.3, 2.5, 14.6);
        w.addWall(7.6, 12.7, 0, 16.1);
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
            Path path = aStar.getPath(startNode, goalNode, densityMap);
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
                String [] parts = line.split(" ");
                for (int i = 0; i < parts.length - 1; i++) {
                    densityMap[count][i] = Integer.parseInt(parts[i]);
                }
                count++;
            }
        } finally {
            br.close();
        }
    }

}
