import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class Server {

    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public void startServer() {

        try (ServerSocket serverSocket = new ServerSocket(1991)) {
            System.out.println("Listening on port: 1991");
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Found client....");
                setupStreams(clientSocket);
                chatWithClient();
                closeAll();
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    public void setupStreams(Socket clientSocket) throws IOException {
        outputStream = new DataOutputStream(clientSocket.getOutputStream());
        outputStream.flush();
        inputStream = new DataInputStream(clientSocket.getInputStream());
    }

    public void closeAll() throws IOException {
        inputStream.close();
        outputStream.close();
    }

    public void chatWithClient() throws IOException, NoSuchAlgorithmException {

        String askLogin = "Type 'login' to log in or 'create' to create account.";
        outputStream.writeUTF(askLogin);
        outputStream.writeBoolean(true);
        outputStream.flush();
        String response = inputStream.readUTF();

        if (response.equalsIgnoreCase("login")) {
            userLogin();
        } else if (response.equalsIgnoreCase("create")) {
            createAccount();
        } else {
            chatWithClient();
        }
    }

    public void userLogin() throws IOException, NoSuchAlgorithmException {

        String askUsername = "Please enter your username:";
        outputStream.writeUTF(askUsername);
        outputStream.writeBoolean(true);
        outputStream.flush();
        String userName = inputStream.readUTF();

        if (UserData.checkUser(userName)) {
            String askPassword = "Please enter your password:";
            outputStream.writeUTF(askPassword);
            outputStream.writeBoolean(true);
            outputStream.flush();
            String password = inputStream.readUTF();

            if (UserData.checkLogin(userName, password)) {
                outputStream.writeUTF("Login Successful! Type 'quit' to exit.");
                outputStream.writeBoolean(true);
                outputStream.flush();
                System.out.println("Program complete.");

            } else {
                outputStream.writeUTF("Login failed. Please try again.");
                outputStream.writeBoolean(false);
                outputStream.flush();
                userLogin();

            }
        } else {
            outputStream.writeUTF("Username invalid. Press enter to try again or type 'create' to create a new account.");
            outputStream.writeBoolean(true);
            outputStream.flush();
            String response = inputStream.readUTF();

            if (response.equalsIgnoreCase("create")) {
                createAccount();
            } else {
                userLogin();
            }
        }
    }

    public void createAccount() throws IOException, NoSuchAlgorithmException {

        String askUsername = "Please enter you username:";
        outputStream.writeUTF(askUsername);
        outputStream.writeBoolean(true);
        outputStream.flush();
        String userName = inputStream.readUTF();

        if (!UserData.checkUser(userName)) {
            String askPassword = "Please enter your password:";
            outputStream.writeUTF(askPassword);
            outputStream.writeBoolean(true);
            outputStream.flush();

            String password = inputStream.readUTF();
            UserData.createUser(userName, password);
            outputStream.writeUTF("Account created!");
            outputStream.writeBoolean(false);
            outputStream.flush();
            chatWithClient();

        } else {
            outputStream.writeUTF("That username already exists. Please try again.");
            outputStream.writeBoolean(false);
            outputStream.flush();
            createAccount();
        }
    }
}
