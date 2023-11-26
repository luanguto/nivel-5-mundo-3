package cadastroserver;

import cadastroserver.controller.ProdutosJpaController;
import cadastroserver.controller.UsuariosJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroServerPU");
        ProdutosJpaController produtoController = new ProdutosJpaController(emf);
        UsuariosJpaController usuarioController = new UsuariosJpaController(emf);

        final int port = 4321;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor iniciado na porta " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                CadastroThread thread = new CadastroThread(produtoController, usuarioController, clientSocket);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Erro ao fechar o servidor: " + e.getMessage());
                }
            }
        }
    }
}
