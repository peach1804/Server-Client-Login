import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private String message;
    private String response;
    private boolean goNext;
    private Scanner sc = new Scanner(System.in);

    public void connect() {

        try {
            socket = new Socket("localhost", 1991);
            setupStreams(socket);
            tryLogin();
            closeAll();

        } catch (IOException ioe) {
            System.out.println(ioe);
            System.exit(1);
        }

    }

    public void setupStreams(Socket socket) throws IOException {
        outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.flush();
        inputStream = new DataInputStream(socket.getInputStream());
    }

    public void closeAll() throws IOException {
        socket.close();
        inputStream.close();
        outputStream.close();
    }

    public void tryLogin() throws IOException {

        while (true) {
            message = inputStream.readUTF();
            System.out.println(message);
            goNext = inputStream.readBoolean();

            if (goNext) {
                response = sc.nextLine();
                outputStream.writeUTF(response);
                outputStream.flush();

                if (response.equalsIgnoreCase("quit")) {
                    System.exit(0);
                }
            } else {
                tryLogin();
            }
        }
    }
}
