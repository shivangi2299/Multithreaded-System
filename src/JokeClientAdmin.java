import java.io.*;
import java.net.*;
import java.util.Scanner;

public class JokeClientAdmin {
    private static String[] mode = {"Joke","Proverb"};
    public static void main(String argv[]) {
        JokeClientAdmin cc = new JokeClientAdmin(argv);
        cc.run(argv);
    }
    public JokeClientAdmin(String argv[]) {
    }

    public void run(String argv[]) {

        String server_1 = "";
        String server_2 = "";
        int iPort1 = 4545;

        if (argv.length < 1)
            server_1 = "localhost";
        else {
            if (argv.length == 2) {
                server_1 = argv[0];
                server_2 = argv[1];
            } else {
                if (argv.length == 1)
                    server_1 = argv[0];
                else {
                    System.out.print("Cannot process more than 2 command line arguments");
                    System.exit(0);
                }
            }
        }

        String input = "";
        Scanner consoleIn = new Scanner(System.in);
        System.out.println("This the the JokeServer's Client Admin");

        if (argv.length < 2) {
            do {
                System.out.println("Please press ENTER to toggle JokeServer's mode, " +
                        "SHUTDOWN to shutdown JokeServer and quit to exit\n");
                System.out.flush();
                input = consoleIn.nextLine();
                if (!input.equalsIgnoreCase("quit")) {
                    if (!input.equalsIgnoreCase("shutdown"))
                        primary(server_1, 0);
                    else
                        primary(server_1, 1);

                }
            } while (input.indexOf("quit") < 0);
            System.out.println("Shutting Down JokeServer's AdminClient");
        }
        else if (argv.length == 2)
            settleTwoServer(server_1, server_2, consoleIn);
    }

    void settleTwoServer(String server_1, String server_2, Scanner consoleIn){
        String input = "";
        System.out.print("Which server do you want to connect to? 1. "+ server_1 +"(at port 5050), 2. "
                +server_2+"(at port 5051) \nType the number and press ENTER\n");
        System.out.flush ();
        input = consoleIn.nextLine();
        if(input.equalsIgnoreCase("1") ) {
            do {
                System.out.println("Please choose from the following list and enter the number");
                System.out.println("1.  Toggle Primary JokeServer's mode\n2. Shutdown Primary JokeServer\n" +
                        "3. Quit\n4. Go back to server Selection");
                System.out.flush ();
                input = consoleIn.nextLine();
                switch (input) {
                    case "1":
                        primary(server_1, 0);
                        break;
                    case "2":
                        primary(server_1, 1);
                        break;
                    case "3":
                        System.out.println("**Shutting down Admin Client**");
                        break;
                    case "4":
                        settleTwoServer(server_1, server_2, consoleIn);
                        break;
                    default:
                        System.out.println("Not an valid input");
                }
            }while(input.equalsIgnoreCase("3"));
        }
        if(input.equalsIgnoreCase("2") ) {
            do {
                System.out.println("Please choose from the following list and enter the number");
                System.out.println("1.  Toggle Secondary JokeServer's mode\n2. Shutdown Secondary JokeServer\n" +
                        "3. Quit\n4. Go back to Server Selection");
                System.out.flush ();
                input = consoleIn.nextLine();
                switch (input) {
                    case "1":
                        secondary(server_2, 0);
                        break;
                    case "2":
                        secondary(server_2, 1);
                        break;
                    case "3":
                        System.out.println("**Shutting down Admin Client**");
                        break;
                    case "4":
                        settleTwoServer(server_1, server_2, consoleIn);
                        break;
                    default:
                        System.out.println("Not an valid input");
                }
            }while(input.indexOf("3") < 0);
        }
    }

    void primary(String serverName, int iServerClosestat){

        try{
            JokeData obj = new JokeData();

            Socket socket = new Socket(serverName, 5050);
            System.out.println("\nWe have successfully connected to the JokeServer at port 5050");

            OutputStream OutputStream = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(OutputStream);
            obj.iServerClosestat = iServerClosestat;
            oos.writeObject(obj);

            if(iServerClosestat == 0) {
                InputStream InStream = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(InStream);
                JokeData InObject = (JokeData) ois.readObject();
                System.out.println("JokeServer's mode is successfully toggled");
                System.out.println("JokeServer's mode is set to " + mode[InObject.PrimaryAdMode]);
            }
            System.out.println("\nClosing the connection to the server.\n");
            socket.close();
        } catch (ConnectException CE){
            System.out.println("\nDarn it! Unable to connect to JokeServer! It it even running yet?\n");
            CE.printStackTrace();
        } catch (UnknownHostException UH){
            System.out.println("\nUnknown Host problem.\n");
            UH.printStackTrace();
        } catch(ClassNotFoundException CNF){
            CNF.printStackTrace();
        } catch (IOException IOE){
            IOE.printStackTrace();
        }
    }

    void secondary(String serverName, int iServerClosestat){
        String sPre = "<S2> ";
        try{
            JokeData obj = new JokeData();

            Socket socket = new Socket(serverName, 5051);
            System.out.println(sPre + " We have successfully connected to the Secondary JokeServer at port 5051");

            OutputStream OutputStream = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(OutputStream);
            obj.iServerClosestat = iServerClosestat;
            oos.writeObject(obj);

            if(iServerClosestat == 0) {
                InputStream InStream = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(InStream);
                JokeData InObject = (JokeData) ois.readObject();
                System.out.println(sPre + " JokeServer's mode is successfully toggled");
                System.out.println(sPre + " JokeServer's mode is set to " + mode[InObject.SecondAdMode]);
            }
            System.out.println(sPre + " Closing the connection to the server.\n");
            socket.close();
        } catch (ConnectException CE){
            System.out.println("\nDarn it! Unable to connect to JokeServer! It it even running yet?\n");
            CE.printStackTrace();
        } catch (UnknownHostException UH){
            System.out.println("\nUnknown Host problem.\n");
            UH.printStackTrace();
        } catch(ClassNotFoundException CNF){
            CNF.printStackTrace();
        } catch (IOException IOE){
            IOE.printStackTrace();
        }
    }
}