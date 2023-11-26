import java.io.*;
import java.net.*;

public class CadastroServer {
    private static final int PORT = 1433;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciado na porta " + PORT);

        try {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                
                String login = in.readLine();
                String senha = in.readLine();

              
                if (isValidLogin(login, senha)) {
                    out.println("OK");
                    /
                    if ("L".equals(in.readLine())) {
                        out.println("Produto 1, Produto 2, Produto 3");
                    }
                } else {
                    out.println("Falha na autenticação");
                }
            } catch (IOException e) {
                System.out.println("Erro ao tratar cliente: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Não foi possível fechar o socket");
                }
            }
        }

   
        private boolean isValidLogin(String login, String senha) {
            
            return "usuario".equals(login) && "senha123".equals(senha);
        }
    }
}
