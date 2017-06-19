package w50901;//
//	File:			w50901.Brain.java
//	Author:		Krzysztof Langner
//	Date:			1997/04/28
//
//    Modified by:	Paul Marlow

//    Modified by:      Edgar Acosta
//    Date:             March 4, 2008

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Brain extends Thread implements SensorInput {
    //===========================================================================
    // Private members
    private SendCommand krislet;            // robot which is controled by this brain


    //---------------------------------------------------------------------------
    // This is main brain function used to make decision
    // In each cycle we decide which command to issue based on
    // current situation. the rules are:
    //
    //	1. If you don't know where is ball then turn right and wait for new info
    //
    //	2. If ball is too far to kick it then
    //		2.1. If we are directed towards the ball then go to the ball
    //		2.2. else turn to the ball
    //
    //	3. If we dont know where is opponent goal then turn wait 
    //				and wait for new info
    //
    //	4. Kick ball
    //
    //	To ensure that we don't send commands to often after each cycle
    //	we waits one simulator steps. (This of course should be done better)

    // ***************  Improvements ******************
    // Allways know where the goal is.
    // Move to a place on my side on a kick_off
    // ************************************************
    private Memory memory;                // place where all information is stored


    //===========================================================================
    // Here are suporting functions for implement logic


    //===========================================================================
    // Implementation of w50901.SensorInput Interface
    private char m_side;
    volatile private boolean m_timeOver;
    private String m_playMode;
    private String m_team;
    private int m_number;

    //---------------------------------------------------------------------------
    // This constructor:
    // - stores connection to krislet
    // - starts thread for this object
    public Brain(SendCommand krislet,
                 String team,
                 char side,
                 int number,
                 String playMode) {
        m_timeOver = false;
        this.krislet = krislet;
        memory = new Memory();
        m_team = team;//LG
        m_side = side;
        m_number = number;//LG
        m_playMode = playMode;
        start();
    }

    public void run() {

        // first put it somewhere on my side
        if (Pattern.matches("^before_kick_off.*", m_playMode))
            krislet.move(-Math.random() * 52.5, 34 - Math.random() * 68.0);
        while (!m_timeOver) {

            // We know where is ball and we can kick it
            // so look for goal
            GoalInfo gates = null;
            if (m_side == 'l') {
                gates = (GoalInfo) memory.getObject("goal r");
            } else {
                gates = (GoalInfo) memory.getObject("goal l");
            }
            BallInfo ball = (BallInfo) memory.getObject("ball");

            List<PlayerInfo> players = memory.getAllObjects("player").stream()
                    .map(objectInfo -> ((PlayerInfo) objectInfo))
                    .collect(Collectors.toList());

            List<ObjectInfo> team = players.stream()
                    .filter(playerInfo -> Objects.equals(playerInfo.getTeamName(), this.m_team))
                    .collect(Collectors.toList());

//            System.out.print("\n" + m_team + " " + m_number + " see:");
            for (ObjectInfo o : players) {
                PlayerInfo pl = (PlayerInfo) o;
//                System.out.print(pl.getTeamName() + " " + pl.getTeamNumber() + ",");
            }

            if (ball == null) {
                // If you don't know where is ball then find it
                krislet.turn(40);
                memory.waitForNewInfo();
            } else if (ball.distance > 1.0) {
                // If ball is too far then
                // turn to ball or
                // if we have correct direction then go to ball
                if (ball.m_direction != 0) {
                    krislet.turn(ball.m_direction);
                } else {

                    krislet.dash(100 * ball.distance);

                }
            } else {


                if (gates == null) {
                    krislet.turn(40);
                    memory.waitForNewInfo();
                } else {

                    if (gates.distance <= 30) {
                        krislet.kick(100, gates.m_direction);
                    } else if (gates.distance < 45) {
                        krislet.kick(30, gates.m_direction);
                    } else if (gates.distance < 60) {
                        //pass
                        Optional<ObjectInfo> first = team.stream()
                                .findFirst();
                        if (first.isPresent()) {
                            if (first.get().distance > 20) {

                                krislet.kick(40, first.get().m_direction);
                            } else {
                                krislet.kick(30, gates.m_direction);
                            }
                        } else {
                            krislet.kick(30, gates.m_direction);
                        }

                    }
                }
            }
            // sleep one step to ensure that we will not send
            // two commands in one cycle.
            try {
                Thread.sleep(2 * SoccerParams.simulator_step);
            } catch (Exception e) {
            }
        }
        krislet.bye();
    }

    //---------------------------------------------------------------------------
    // This function sends see information
    public void see(VisualInfo info) {
        memory.store(info);
    }

    //---------------------------------------------------------------------------
    // This function receives hear information from player
    public void hear(int time, int direction, String message) {

    }

    //---------------------------------------------------------------------------
    // This function receives hear information from referee
    public void hear(int time, String message) {
        if (message.compareTo("time_over") == 0)
            m_timeOver = true;

    }

}
