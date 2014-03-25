package WorldRepresentation;

import java.util.ArrayList;

public class ChunkSync implements Runnable {

    int num;
    private ArrayList<LayoutChunk> chunks;
    private int stopped;

    public ChunkSync() {
        this.num = 0;
    }

    public void run() {
        //Do inter-chunk communication here.
        System.out.println("I have hit the barrier:" + num);
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
