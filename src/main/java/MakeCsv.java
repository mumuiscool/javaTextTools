import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MakeCsv {
    private static int ix = 0;
    private static PrintStream outer = System.out;
    private static BinsCenter binsCenter = null;
    private static Map<Integer, String> groups = new HashMap<Integer, String>();

    private static void init(){
        groups.put(1,"KJH");
        groups.put(2,"KJH");
        groups.put(3,"KJH");
        groups.put(4,"KJH");
        groups.put(5,"KJH");
        groups.put(6,"KJH");
        groups.put(7,"KJH");
        groups.put(8,"KJH");

    }
    
    public static void main(String[] args) throws IOException {
        init();

        int rowCnt = 1024, colCnt = 4, textLen = 20;;
        try {
            rowCnt = Integer.parseInt(args[0]);
            textLen = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            System.err.println("args error.");
        }

        binsCenter = new BinsCenter(1024*rowCnt);

        //out csv Header str,integer double
        out("a,b,c,d");

        for(int i = 0; i<rowCnt; ++i ){

            String f1Value = getText(textLen);
            String f2Value = getInteger();
            String f3Value = getDouble();
            String f4Value = getGroupText();

            out(f1Value);
            out(",");
            out(f2Value);
            out(",");
            out(f3Value);
            out(",");
            out(f4Value);
            out("\r\n");
        }

    }

    public static String getText(int len){
        String rStr = "";
        //String.format("%02X",d)
        StringBuilder sb = new StringBuilder();
        Integer[] tmpDatas = binsCenter.takeMany(len);
        for( int i = 0; i < len; ++i){
            sb.append(String.format("%02X", tmpDatas[i]));
        }
        rStr = sb.toString();
        return rStr;
    }
    public static String getInteger(){
        String rStr = "";
        //String.format("%02X",d)
        Integer tmp = binsCenter.takeWhenHas();
        rStr = tmp != null ? tmp.toString(): "null";
        return rStr;
    }
    public static String getDouble(){
        String rStr = "";
        Integer tmp = binsCenter.takeWhenHas();
        Double tmpDb = tmp.doubleValue();
        tmpDb = tmpDb * Math.PI;

        return tmpDb.toString();
    }
    public static String getGroupText(){
        String rStr = "";
        Integer tmp = binsCenter.takeWhenHas();
        tmp = tmp & 8;
        rStr = groups.get(tmp);
        return rStr;
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
        public Integer[] takeMany(int needCnt){
            Integer[] r = new Integer[needCnt];

            for(int i = 0; i < needCnt; ++i){
                Integer tmpData = takeWhenHas();
                r[i] = tmpData != null ? tmpData : -1;
            }

            return r;
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
