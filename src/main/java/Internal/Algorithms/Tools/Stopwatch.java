package Internal.Algorithms.Tools;

public class Stopwatch {
    public long start; // 开始时间
    public Stopwatch(){
        start = System.currentTimeMillis();
    }
    public void start(){
        start = System.currentTimeMillis();
    }
    // second
    public double elapsedTime(){
        long now = System.currentTimeMillis();
        return (now - start)/1000.0;
    }
    public void outElapsedBySecond(){
        System.out.println("Time:"+elapsedTime()+"second");
    }
    public void outElapsedByMiniSecond(){
        System.out.println("Time:"+elapsedTime()*1000+"ms");
    }

}
