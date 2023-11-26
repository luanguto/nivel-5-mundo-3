package cadastroclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import javax.persistence.*;

public class CadastroClient {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String host = "localhost";
        int port = 4321;

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {

           
            saida.writeObject("Loja"); 
            saida.writeObject("Loja"); 

            
            saida.writeObject("L");

            
            Object response = entrada.readObject();
            
            
            if (response instanceof List<?>) {
                List<?> entities = (List<?>) response;
                
             
                for (Object entity : entities) {
                    public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String senha;

  
    public Usuarios() {}

    // Getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
                    
                }
            }

         
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
