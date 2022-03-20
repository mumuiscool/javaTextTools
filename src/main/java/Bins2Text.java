import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class Bins2Text {
    public static void main(String[] args) throws IOException {
        int readBufCnt = 1024;
        try {
            readBufCnt = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        byte[] buf = new byte[readBufCnt];
        InputStream in = System.in;
        PrintStream out = System.out;
        int readed = -1;
        int readCnt = 0;
        while((readed = in.read()) != -1){
            out.printf("%02X", readed);
            ++readCnt;

            if(readCnt >= readBufCnt){
                out.flush();

                readCnt = 0;
            }
        }
    }
}
