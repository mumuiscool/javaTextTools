import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MakeCsv {
    private static int ix = 0;
    private static PrintStream outer = System.out;
    
    public static void main(String[] args) throws IOException {
        int rowCnt = 1024, colCnt = 4;
        try {
            rowCnt = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        //out csv Header str,integer double
        out("a,b,c,d");

    }

    public static void out(String str){
        outer.print(str);
    }
    
    public static class BinsCenter implements Runnable{
        private final int readBufCnt;
        private BlockingQueue<Integer> datasQueue = null;

        public BinsCenter(int readBufCnt){
            this.readBufCnt = readBufCnt;
            datasQueue = new ArrayBlockingQueue<Integer>(readBufCnt);
        }
        public Integer takeWhenHas(){
            Integer rElement = null;

            if(!datasQueue.isEmpty()){
                rElement = this.take();
            }

            return rElement;
        }

        public Integer take(){
            try {
                return this.datasQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void run() {
            try {
                flushData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        public void flushData() throws IOException {
            // make 4 col
            InputStream in = System.in;
            int readed = -1;
            int readCnt = 0;
            //byte[] readBuf = new byte[readBufCnt];
            while((readed = in.read()) != -1){
                // Data
                try {
                    datasQueue.put(readed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                readCnt = readCnt + readed;

                if(readCnt >= readBufCnt){
                    readCnt = 0;
                }
            }
        }
    }
}
