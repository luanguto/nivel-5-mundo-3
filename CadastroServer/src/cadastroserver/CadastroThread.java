package cadastroserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import cadastroserver.controller.ProdutosJpaController;
import cadastroserver.controller.UsuariosJpaController;
import cadastroserver.model.Usuarios;

public class CadastroThread extends Thread {
    private final ProdutosJpaController ctrl;
    private final UsuariosJpaController ctrlUsu;
    private final Socket socket;

    public CadastroThread(ProdutosJpaController ctrl, UsuariosJpaController ctrlUsu, Socket socket) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {

            // Recebe login e senha
            String login = (String) entrada.readObject();
            String senha = (String) entrada.readObject();

            // Verifica o login
            Usuarios usuario = ctrlUsu.findUsuario(login, senha);
            if (usuario == null) {
                saida.writeObject("Falha no login");
                return; // Termina a execução se o login falhar
            }

           
            while (!socket.isClosed()) {
                String comando = (String) entrada.readObject();

                
                if ("L".equals(comando)) {
                    
                    //ctrl.findProdutos();
                   
                    saida.writeObject("Lista de Produtos: ...");
                } else {
                    saida.writeObject("Comando desconhecido");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro na thread de comunicação: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar o socket: " + e.getMessage());
            }
        }
    }

   
}
