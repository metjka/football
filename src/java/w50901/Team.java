package w50901;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Vector;


public class Team {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //Vector<Krislet> players=new

        String[] args2 = new String[args.length > 0 ? args.length - 1 : 0];
        int n = 4;
        if (args.length > 0) args2 = Arrays.copyOfRange(args, 1, args.length);
        if (args.length % 2 == 1)
            n = Integer.parseInt(args[0]);

        final String[] args3 = args2;

        for (int i = 0; i < n; i++)

            new Thread(new Runnable() {
                public void run() {
                    //String [] args2={"-team","Yellow"};
                    try {
                        Krislet.main(args3);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();
        ;

    }

}
