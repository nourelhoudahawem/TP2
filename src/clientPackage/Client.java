package clientPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {


    private static final Pattern OPERATION = Pattern.compile(
            "^\\s*(-?\\d+(?:\\.\\d+)?)\\s*([+\\-*/])\\s*(-?\\d+(?:\\.\\d+)?)\\s*$");

    public static void main(String[] args) {
        String hote = "localhost";
        int port = 1234;
        try (Socket socket = new Socket(hote, port);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Connecté au serveur.");
            while (true) {
                System.out.print("Entrez une opération (ex. 55 * 25) ou FIN pour quitter : ");
                String ligne = sc.nextLine();

                if (ligne.trim().equalsIgnoreCase("FIN")) {
                    out.writeUTF("FIN");
                    out.flush();
                    break;
                }

                Matcher m = OPERATION.matcher(ligne);
                if (!m.matches()) {
                    System.out.println("Syntaxe incorrecte. Format attendu : <nombre> <+|-|*|/> <nombre>");
                    continue;
                }

                out.writeUTF(m.group(1));
                out.writeUTF(m.group(2));
                out.writeUTF(m.group(3));
                out.flush();

                String resultat = in.readUTF();
                System.out.println("Résultat : " + ligne.trim() + " = " + resultat);
            }
            System.out.println("Communication terminée. Fermeture du socket.");
        } catch (IOException e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }
}