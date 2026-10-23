package serverPackage;

import common.Operation;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    static String calculer(Operation op) {
        double x = op.getOperande1();
        double y = op.getOperande2();
        double r;
        switch (op.getOperateur()) {
            case '+': r = x + y; break;
            case '-': r = x - y; break;
            case '*': r = x * y; break;
            case '/':
                if (y == 0) return "Erreur : division par zéro";
                r = x / y; break;
            default: return "Erreur : opérateur invalide";
        }
        return (r == Math.rint(r) && Math.abs(r) < 1e15) ? String.valueOf((long) r) : String.valueOf(r);
    }

    public static void main(String[] args) {
        int port = 1234;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur en attente de connexion sur le port " + port + "...");
            try (Socket socket = serverSocket.accept();
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.flush();
                System.out.println("Client connecté : " + socket.getInetAddress());

                while (true) {
                    Object recu = in.readObject();
                    if (recu instanceof String && ((String) recu).equalsIgnoreCase("FIN")) break;

                    Operation op = (Operation) recu;
                    System.out.println("Objet reçu : " + op);
                    String resultat = calculer(op);
                    out.writeObject(resultat);
                    out.flush();
                    System.out.println("Résultat envoyé : " + resultat);
                }
                System.out.println("Fin de la communication. Fermeture des sockets.");
            } catch (EOFException e) {
                System.out.println("Le client a fermé la connexion.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }
}