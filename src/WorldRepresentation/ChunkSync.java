package WorldRepresentation;

public class ChunkSync implements Runnable{
	
	int num;
	
	public ChunkSync(){
		this.num = 0;
	}
	
	public void run(){
		//Do inter-chunk communication here.
		System.out.println("I have hit the barrier:" + num);
		num++;
	}

}
