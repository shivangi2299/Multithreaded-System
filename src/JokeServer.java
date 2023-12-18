import java.io.*;
import java.net.*;

class JokeData implements Serializable{
    String sClientUserName;
    int PrimaryAdMode = 0;
    int SecondAdMode = 0;
    int iServerClosestat = 0;
}

class toggleMode{
    public static int PrimaryMode = 0;
    public static int SecondMode = 0;
    public static String[] saMode = {"Joke","Proverb"};
    public int getPrimaryMode() {
        return PrimaryMode;
    }

    public void setPrimaryMode() {
        if(PrimaryMode == 0) {
            PrimaryMode = 1;
        }
        else {
            PrimaryMode = 0;
        }
    }
    public int getSecondMode() {
        return SecondMode;
    }

    public void setSecondMode() {
        if(SecondMode == 0) {
            SecondMode = 1;
        }
        else {
            SecondMode = 0;
        }
    }
}


class AdminLooperPrimary implements Runnable {
    public static boolean bCtrlSwitch = true;

    public void run() {
        int q_len = 6;
        int port = 5050;
        Socket sock;

        try {
            ServerSocket servsock = new ServerSocket(port, q_len);
            while (bCtrlSwitch) {
                sock = servsock.accept();
                new PrimaryAdWorker(sock).start();
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
}

class secondAdmin implements Runnable {
    public static boolean bCtrlSwitch = true;

    public void run() {
        int q_len = 6;
        int port = 5051;
        Socket sock;

        try {
            ServerSocket servsock = new ServerSocket(port, q_len);
            while (bCtrlSwitch) {
                sock = servsock.accept();
                new SecondAdWorker(sock).start();
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
}

class PrimaryAdWorker extends Thread {
    Socket sock;

    PrimaryAdWorker(Socket s) {
        sock = s;
    }

    public void run() {
        try {

            InputStream InStream = sock.getInputStream();
            ObjectInputStream ObjectIS = new ObjectInputStream(InStream);

            JokeData InObject = (JokeData) ObjectIS.readObject();

            OutputStream outStream = sock.getOutputStream();
            ObjectOutputStream objectOS = new ObjectOutputStream(outStream);

            if (InObject.iServerClosestat == 0) {

                System.out.println("Connection from Client Admin to Primary JokeServer at port: 5050");

                toggleMode objToggleMode = new toggleMode();
                objToggleMode.setPrimaryMode();
                InObject.PrimaryAdMode = objToggleMode.getPrimaryMode();
                System.out.println("\nJokeServer's mode is set to " + objToggleMode.saMode[objToggleMode.getPrimaryMode()]);

            }

            objectOS.writeObject(InObject);
            System.out.println("Here we are closing the client-socket connection of JokeServer and ClientAdmin......");
            sock.close();

            if(InObject.iServerClosestat == 1) {
                System.out.println("**Shutting down Primary JokeServer as requested by ClientAdmin**");
                System.exit(0);
            }
        } catch (ClassNotFoundException CNF) {
            CNF.printStackTrace();
        } catch (IOException x) {
            System.out.println("Server error.");
            x.printStackTrace();
        }
    }
}

class SecondAdWorker extends Thread {
    Socket sock;

    SecondAdWorker(Socket s) {
        sock = s;
    }

    public void run() {
        try {

            InputStream InStream = sock.getInputStream();
            ObjectInputStream ObjectIS = new ObjectInputStream(InStream);

            JokeData InObject = (JokeData) ObjectIS.readObject();

            OutputStream outStream = sock.getOutputStream();
            ObjectOutputStream objectOS = new ObjectOutputStream(outStream);

            if (InObject.iServerClosestat == 0) {
                System.out.println("Connection from Client Admin to Secondary JokeServer at port: 5051");

                toggleMode objToggleMode = new toggleMode();
                objToggleMode.setSecondMode();
                InObject.SecondAdMode = objToggleMode.getSecondMode();
                System.out.println("\nJokeServer's mode is set to " + objToggleMode.saMode[objToggleMode.getSecondMode()]);
            }

            objectOS.writeObject(InObject);
            System.out.println("Here we are closing the client-socket connection of JokeServer and ClientAdmin......");
            sock.close();

            if(InObject.iServerClosestat == 1) {
                System.out.println("**Shutting down Secondary JokeServer as requested by ClientAdmin**");
                System.exit(0);
            }
        } catch (ClassNotFoundException CNF) {
            CNF.printStackTrace();
        } catch (IOException x) {
            System.out.println("Server error.");
            x.printStackTrace();
        }
    }
}

class mainjokeWorker extends Thread {
    Socket sock;
    mainjokeWorker (Socket s) {sock = s;}

    public void run(){
        try{

            InputStream InStream = sock.getInputStream();
            ObjectInputStream ObjectIS = new ObjectInputStream(InStream);

            JokeData InObject = (JokeData) ObjectIS.readObject();

            OutputStream outStream = sock.getOutputStream();
            ObjectOutputStream objectOS = new ObjectOutputStream(outStream);

            System.out.println("\nConnection from the client at Primary JokeServer at port no 4545:");
            System.out.println("Client's Username: " + InObject.sClientUserName);

            toggleMode objToggleMode = new toggleMode();
            InObject.PrimaryAdMode = objToggleMode.getPrimaryMode();

            objectOS.writeObject(InObject);

            System.out.println("Here we are closing the client-socket connection......\n");
            sock.close();

        } catch(ClassNotFoundException CNF){
            CNF.printStackTrace();
        } catch (IOException x){
            System.out.println("Server error.");
            x.printStackTrace();
        }
    }
}

class SecondJokeWorker extends Thread {
    Socket sock;
    SecondJokeWorker (Socket s) {sock = s;}

    public void run(){
        try{

            InputStream InStream = sock.getInputStream();
            ObjectInputStream ObjectIS = new ObjectInputStream(InStream);

            JokeData InObject = (JokeData) ObjectIS.readObject();

            OutputStream outStream = sock.getOutputStream();
            ObjectOutputStream objectOS = new ObjectOutputStream(outStream);


            System.out.println("\nConnection from the client at Primary JokeServer at port no 4545:\n");
            System.out.println("Client's Username: " + InObject.sClientUserName);

            toggleMode objToggleMode = new toggleMode();
            InObject.SecondAdMode = objToggleMode.getSecondMode();

            objectOS.writeObject(InObject);

            System.out.println("Here we are closing the client-socket connection......");
            sock.close();

        } catch(ClassNotFoundException CNF){
            CNF.printStackTrace();
        } catch (IOException x){
            System.out.println("Server error.");
            x.printStackTrace();
        }
    }
}

public class JokeServer {
    public static void main(String[] args) throws Exception {

        int iServerPort1 = 4545;
        int iServerPort2 = 4546;
        int q_len = 6;
        Socket sock;

        if(args.length < 1){
            AdminLooperPrimary AL = new AdminLooperPrimary();
            Thread t = new Thread(AL);
            t.start();

            System.out.println
                    ("This is the Primary JokeServer, waiting for connections at port " + iServerPort1 + ".\n");

            ServerSocket servSock = new ServerSocket(iServerPort1, q_len);
            System.out.println("Here ServerSocket is waiting for connections..");

            while (true) {
                sock = servSock.accept();
                System.out.println("Connection from " + sock);
                new mainjokeWorker(sock).start();
            }
        }
        else{
            if(args[0].equalsIgnoreCase("secondary")){
                secondAdmin AL = new secondAdmin();
                Thread t = new Thread(AL);
                t.start();

                System.out.println
                        ("This is the Secondary Joke Server, waiting for connection at port " + iServerPort2 + ".\n");

                ServerSocket servSock = new ServerSocket(iServerPort2, q_len);

                while (true) {
                    sock = servSock.accept();
                    System.out.println("Connection from " + sock);
                    new SecondJokeWorker(sock).start();
                }
            }
            else
                System.out.println("Unable to process more than 2 arguments");
        }
    }
}
