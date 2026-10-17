package serverPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {


    static String calculer(String a, String op, String b) {
        try {
            double x = Double.parseDouble(a);
            double y = Double.parseDouble(b);
            double r;
            switch (op) {
                case "+": r = x + y; break;
                case "-": r = x - y; break;
                case "*": r = x * y; break;
                case "/":
                    if (y == 0) return "Erreur : division par zéro";
                    r = x / y; break;
                default: return "Erreur : opérateur invalide";
            }
            return (r == Math.rint(r) && Math.abs(r) < 1e15) ? String.valueOf((long) r) : String.valueOf(r);
        } catch (NumberFormatException e) {
            return "Erreur : opérande invalide";
        }
    }

    public static void main(String[] args) {
        int port = 1234;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur en attente de connexion sur le port " + port + "...");
            try (Socket socket = serverSocket.accept();
                 DataInputStream in = new DataInputStream(socket.getInputStream());
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

                System.out.println("Client connecté : " + socket.getInetAddress());
                while (true) {
                    String a = in.readUTF();
                    if (a.equalsIgnoreCase("FIN")) break;
                    String op = in.readUTF();
                    String b = in.readUTF();
                    System.out.println("Reçu : " + a + " " + op + " " + b);
                    String resultat = calculer(a, op, b);
                    out.writeUTF(resultat);
                    out.flush();
                    System.out.println("Résultat envoyé : " + resultat);
                }
                System.out.println("Fin de la communication. Fermeture des sockets.");
            } catch (EOFException e) {
                System.out.println("Le client a fermé la connexion.");
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }
}