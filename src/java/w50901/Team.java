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
                red, red, red
//                yellow, yellow, yellow,
//                yellow, yellow, yellow
        );
        for (String[] team : arsTeam)

            new Thread(() -> {
                try {
                    Krislet.main(team);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
    }
}
