package Tools;

public class Stopwatch {
    public long start; // 开始时间
    public Stopwatch(){
        start = System.currentTimeMillis();
    }
    public void start(){
        start = System.currentTimeMillis();
    }
    public double elapsedTime(){
        long now = System.currentTimeMillis();
        return (now - start)/1000.0;
    }
}
