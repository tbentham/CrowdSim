package WorldRepresentation;

import java.util.ArrayList;

// This class is the synchronisation point for all the threads, this class is run when a barrier is hit
public class ChunkSync implements Runnable {

    int num;
    private ArrayList<LayoutChunk> chunks;
    private int stopped;
    long start;

    public ChunkSync() {
        this.num = 0;
        start = System.currentTimeMillis();
    }

    // Simply records the number of barriers that have been hit
    public void run() {
        System.out.println("I have hit the barrier:" + num);
        System.out.println("Time elapsed" + (System.currentTimeMillis() - start));
        num++;

        for (LayoutChunk c : chunks) {

            if (!c.finished) {
                return;
            }
        }

        for (LayoutChunk c : chunks) {

            this.stopped = c.i;
            c.i = Integer.MAX_VALUE - 1;
        }


    }

    public void addChunks(ArrayList<LayoutChunk> chunks) {
        this.chunks = chunks;
    }

    public int getStopped() {
        return this.stopped;
    }
}
