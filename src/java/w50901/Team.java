package w50901;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class Team {

    public static void main(String[] args) {
        String[] red = {"-team", "Red"};
        String[] yellow = {"-team", "Yellow"};
        List<String[]> arsTeam = Arrays.asList(
                red, red, red,
                yellow, yellow, yellow
        );
        for (String[] a : arsTeam)

            new Thread(() -> {
                try {
                    Krislet.main(a);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }).start();
        ;

    }

}
