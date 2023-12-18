import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

public class JokeClient {
    private static int primaryjokecount = 0;
    private static int primaryproverbcount = 0;
    private static int secondjokecount = 0;
    private static int secondproverbcount = 0;

    private static int PrimaryMode = 0;
    private static ArrayList<String> mainjoke;
    private static ArrayList<String> mainproverb;
    private static ArrayList<String> secondjoke;
    private static ArrayList<String> secondproverb;

    JokeClient() {

        mainjoke = new ArrayList<String>();
        mainjoke.add("JA#What does a baby computer call his father? Data");
        mainjoke.add("JB#Why can you never trust spiders? Because they post stuff on the web.");
        mainjoke.add("JC#How many computer programmers does it take to change a light bulb? None, that’s a hardware issue ");
        mainjoke.add("JD#What is the best way to criticize your boss? Very quietly, so he cannot hear you.");

        secondjoke = new ArrayList<String>();
        secondjoke.add("JA#What does a baby computer call his father? Data");
        secondjoke.add("JB#Why can you never trust spiders? Because they post stuff on the web.");
        secondjoke.add("JC#How many computer programmers does it take to change a light bulb? None, that’s a hardware issue ");
        secondjoke.add("JD#What is the best way to criticize your boss? Very quietly, so he cannot hear you.");

        mainproverb = new ArrayList<String>();
        mainproverb.add("PA#An hour in the morning is worth two in the evening.");
        mainproverb.add("PB#Think of many things, do one.");
        mainproverb.add("PC#Tell me and I’ll forget, show me and I may remember, involve me and I’ll understand.");
        mainproverb.add("PD#Better an ounce of happiness than a pound of gold.");

        secondproverb = new ArrayList<String>();
        secondproverb.add("PA#An hour in the morning is worth two in the evening.");
        secondproverb.add("PB#Think of many things, do one.");
        secondproverb.add("PC#Tell me and I’ll forget, show me and I may remember, involve me and I’ll understand.");
        secondproverb.add("PD#Better an ounce of happiness than a pound of gold.");

    }

    public static void main(String argv[]) {
        JokeClient cc = new JokeClient();
        cc.run(argv);
    }

    public void run(String argv[]) {

        String serverName1 = "";
        String server_2 = "";
        int iPort1 = 4545;

        if (argv.length < 1)
            serverName1 = "localhost";
        else {
            if (argv.length == 2) {
                serverName1 = argv[0];
                server_2 = argv[1];
            } else {
                if (argv.length == 1)
                    serverName1 = argv[0];
                else {
                    System.out.print("Cannot process more than 2 command line arguments");
                    System.exit(0);
                }
            }
        }

        String input = "";
        Scanner consoleIn = new Scanner(System.in);
        System.out.print("Please enter your Username: ");
        System.out.flush();
        String sUserName = consoleIn.nextLine();
        System.out.println("Hello " + sUserName);

        if (argv.length < 2) {
            do {
                System.out.print("Please press enter to connect to server, or quit to end: ");
                System.out.flush();
                input = consoleIn.nextLine();
                if (input.indexOf("quit") < 0) {
                    primarysettleall(sUserName, serverName1, iPort1);
                }
            } while (input.indexOf("quit") < 0);
            System.out.println("Shutting down client: " + sUserName);
        } else {
            if (argv.length == 2) {
                settleTwoServer(sUserName, serverName1, server_2, consoleIn);
            }
        }
    }

    void settleTwoServer(String sUserName, String serverName1, String server_2, Scanner consoleIn) {
        String input = "";
        System.out.print("Which server do you want to connect to? 1. " + serverName1 + ", 2. " + server_2
                + "\nType the number and press ENTER\n");
        System.out.flush();
        input = consoleIn.nextLine();
        if (input.equalsIgnoreCase("1")) {
            do {
                System.out.print("Please press enter to connect to server: " + serverName1 + " or type BACK to go back to " +
                        "main menu to switch server or type QUIT to end\n");
                System.out.flush();
                input = consoleIn.nextLine();
                if (!input.equalsIgnoreCase("quit")) {
                    if (input.equalsIgnoreCase("back")) {
                        settleTwoServer(sUserName, serverName1, server_2, consoleIn);
                    } else
                        primarysettleall(sUserName, serverName1, 4545);
                }
            } while (!input.equalsIgnoreCase("quit"));
            System.out.println("Shutting down client: " + sUserName);
        }
        if (input.equalsIgnoreCase("2")) {
            do {
                System.out.print("Please press enter to connect to server: " + serverName1 + " or type BACK to go back to " +
                        "main menu to switch server or type QUIT to end  ");
                System.out.flush();
                input = consoleIn.nextLine();
                if (!input.equalsIgnoreCase("quit")) {
                    if (input.equalsIgnoreCase("back")) {
                        settleTwoServer(sUserName, serverName1, server_2, consoleIn);
                    } else
                        secondsettleall(sUserName, server_2, 4546);
                }
            } while (!input.equalsIgnoreCase("quit"));
            System.out.println("Shutting down client: " + sUserName);
        }
    }

    void primarysettleall(String sUserName, String serverName, int iPortNo) {

        try {
            JokeData objJokeData = new JokeData();
            objJokeData.sClientUserName = sUserName;

            Socket socket = new Socket(serverName, iPortNo);
            System.out.println("\nConnection with JokeServer at \"" + serverName + "\" using port " + iPortNo + " is successful");

            OutputStream OutputStream = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(OutputStream);

            oos.writeObject(objJokeData);
            System.out.println("We have sent the serialized values to the JokeServer's server socket");

            InputStream InStream = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(InStream);
            JokeData InObject = (JokeData) ois.readObject();

            PrimaryMode = InObject.PrimaryAdMode;
            primarysettleall(PrimaryMode, sUserName, iPortNo);

            System.out.println("Closing the connection to the server.\n");
            socket.close();
        } catch (
                ConnectException CE) {
            System.out.println("\nDarn it! Unable to connect to JokeServer! It it even running yet?\n");
            CE.printStackTrace();
        } catch (UnknownHostException UH) {
            System.out.println("\nUnknownHostException problem.\n");
            UH.printStackTrace();
        } catch (
                ClassNotFoundException CNF) {
            CNF.printStackTrace();
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }
    }

    void secondsettleall(String sUserName, String serverName, int iPortNo) {
        String sPre = "<S2> ";

        try {
            JokeData objJokeData = new JokeData();
            objJokeData.sClientUserName = sUserName;

            Socket socket = new Socket(serverName, iPortNo);
            System.out.println(sPre + "Connection with JokeServer at \"" + serverName + "\" is successful");

            OutputStream OutputStream = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(OutputStream);

            oos.writeObject(objJokeData);
            System.out.println(sPre + "We have sent the serialized values to the JokeServer's server socket");

            InputStream InStream = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(InStream);
            JokeData InObject = (JokeData) ois.readObject();

            PrimaryMode = InObject.SecondAdMode;
            primarysettleall(PrimaryMode, sUserName, iPortNo);

            System.out.println(sPre + "Closing the connection to the server.\n");
            socket.close();
        } catch (
                ConnectException CE) {
            System.out.println("\nDarn it! Unable to connect to JokeServer! It it even running yet?\n");
            CE.printStackTrace();
        } catch (UnknownHostException UH) {
            System.out.println("\nUnknownHostException problem.\n");
            UH.printStackTrace();
        } catch (
                ClassNotFoundException CNF) {
            CNF.printStackTrace();
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }
    }

    void primarysettleall(int PrimaryMode, String sUserName, int iPort) {
        String[] saTemp = {};
        String sPre = "<S2> ";
        if (iPort == 4545) {
            if (PrimaryMode == 0) {

                saTemp = mainjoke.get(primaryjokecount).split("#");

                System.out.println(saTemp[0] + " " + sUserName
                        + ": " + saTemp[1]);
                primaryjokecount++;
                if (primaryjokecount == 4) {
                    System.out.println("***JOKE CYCLE IS COMPLETED****");
                    primaryjokecount = 0;
                    Collections.shuffle(mainjoke);
                }
            } else {
                if (PrimaryMode == 1) {
                    saTemp = mainproverb.get(primaryproverbcount).split("#");
                    System.out.println(saTemp[0] + " " + sUserName
                            + ": " + saTemp[1]);
                    primaryproverbcount++;
                    if (primaryproverbcount == 4) {
                        System.out.println("***PROVERB CYCLE IS COMPLETED****");
                        primaryproverbcount = 0;
                        Collections.shuffle(mainproverb);
                    }
                } else
                    System.out.println("Unable to detect valid Server's mode");
            }
        } else if (iPort == 4546) {
            if (PrimaryMode == 0) {
                saTemp = secondjoke.get(secondjokecount).split("#");

                System.out.println(sPre + saTemp[0] + " " + sUserName
                        + ": " + saTemp[1]);
                secondjokecount++;
                if (secondjokecount == 4) {
                    System.out.println(sPre + "***JOKE CYCLE IS COMPLETED****");
                    secondjokecount = 0;
                    Collections.shuffle(secondjoke);
                }
            } else {
                if (PrimaryMode == 1) {
                    saTemp = secondproverb.get(secondproverbcount).split("#");
                    System.out.println(sPre + saTemp[0] + " " + sUserName
                            + ": " + saTemp[1]);
                    secondproverbcount++;
                    if (secondproverbcount == 4) {
                        System.out.println(sPre + "***PROVERB CYCLE IS COMPLETED****");
                        secondproverbcount = 0;
                        Collections.shuffle(secondproverb);
                    }
                } else
                    System.out.println(sPre + "Unable to detect valid Server's mode");
            }
        }
    }
}