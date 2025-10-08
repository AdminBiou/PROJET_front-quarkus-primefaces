package com.example;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Named
@SessionScoped
public class CalculBean implements Serializable {

    private double a;
    private double b;
    private double c;
    private String resultat;
    private List<Operation> historique = new ArrayList<>();

    // === Getters et Setters ===
    public double getA() { return a; }
    public void setA(double a) { this.a = a; }
    public double getB() { return b; }
    public void setB(double b) { this.b = b; }
    public double getC() { return c; }
    public void setC(double c) { this.c = c; }
    public String getResultat() { return resultat; }
    public List<Operation> getHistorique() { return historique; }

    // === Réinitialiser seulement les champs de saisie et le résultat ===
    public void effacer() {
        a = 0;
        b = 0;
        c = 0;
        resultat = "";
    }

    // === Appel de l'API pour effectuer le calcul ===
    public void calculer(String op) {
        try {

            // Construction de l'URL de base avec a et b
            String urlStr = "http://localhost:8081/calcul/" + op + "?a=" + a + "&b=" + b;
            if(op.equals("eq2")) {
                urlStr += "&c=" + c;
            }

            // Envoi de la requête HTTP GET à ton API REST
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            Scanner sc = new Scanner(con.getInputStream());
            if(sc.hasNextLine()) {
                resultat = sc.nextLine();   // Lecture du résultat renvoyé par l’API
            }
            sc.close();

            // Traduire le code en nom lisible
            String typeLisible;
            switch (op) {
                case "add": typeLisible = "Addition"; break;
                case "sub": typeLisible = "Soustraction"; break;
                case "mul": typeLisible = "Multiplication"; break;
                case "div": typeLisible = "Division"; break;
                case "eq1": typeLisible = "Équation du 1er degré"; break;
                case "eq2": typeLisible = "Équation du 2e degré"; break;
                default: typeLisible = op; break;
            }

            // Ajouter à l'historique avec l'heure formatée
            String inputStr = "a=" + a + ", b=" + b + (op.equals("eq2") ? ", c=" + c : "");

            // Ajouter dans l’historique : (nom lisible, valeurs, résultat)
            historique.add(new Operation(typeLisible, inputStr, resultat));

        } catch (Exception e) {
            resultat = "Erreur : " + e.getMessage();
        }
    }

    // === Classe interne pour stocker l'historique ===
    public static class Operation {
        private String type;
        private String input;
        private String result;
        private String createdAt;

        public Operation(String type, String input, String result) {
            this.type = type;
            this.input = input;
            this.result = result;

            // Heure formatée en HH:mm:ss
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd -- HH:mm:ss");
            this.createdAt = LocalDateTime.now().format(formatter);
        }

        public String getType() { return type; }
        public String getInput() { return input; }
        public String getResult() { return result; }
        public String getCreatedAt() { return createdAt; }
    }
}
