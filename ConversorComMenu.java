import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CurrencyConverterComMenu {

    private static final String API_KEY = "SUA_API_KEY_AQUI";  // Substitua com sua chave
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    public static double getExchangeRate(String baseCurrency, String targetCurrency) throws Exception {
        String urlString = BASE_URL + API_KEY + "/latest/" + baseCurrency;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Erro na requisição: Código HTTP " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder jsonOutput = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            jsonOutput.append(line);
        }
        conn.disconnect();

        JSONObject jsonObject = new JSONObject(jsonOutput.toString());
        JSONObject rates = jsonObject.getJSONObject("conversion_rates");

        if (!rates.has(targetCurrency)) {
            throw new IllegalArgumentException("Moeda de destino inválida: " + targetCurrency);
        }

        return rates.getDouble(targetCurrency);
    }

    public static void mostrarMenu() {
        System.out.println("\n=== MENU CONVERSOR DE MOEDAS ===");
        System.out.println("1. Converter moeda");
        System.out.println("2. Instruções");
        System.out.println("3. Sair");
        System.out.print("Escolha uma opção: ");
    }

    public static void mostrarInstrucoes() {
        System.out.println("\nInstruções:");
        System.out.println("- Digite os códigos de moeda no padrão ISO (ex: USD, EUR, BRL).");
        System.out.println("- Informe o valor numérico que deseja converter.");
        System.out.println("- É necessário conexão com a internet.");
    }

    public static void converterMoeda(Scanner scanner) {
        try {
            System.out.print("Moeda de origem (ex: USD): ");
            String baseCurrency = scanner.next().toUpperCase();

            System.out.print("Moeda de destino (ex: BRL): ");
            String targetCurrency = scanner.next().toUpperCase();

            System.out.print("Valor a ser convertido: ");
            double amount = scanner.nextDouble();

            double rate = getExchangeRate(baseCurrency, targetCurrency);
            double converted = amount * rate;

            System.out.printf("Resultado: %.2f %s = %.2f %s%n", amount, baseCurrency, converted, targetCurrency);

        } catch (IllegalArgumentException e) {
            System.err.println("Erro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao converter: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean sair = false;

        while (!sair) {
            mostrarMenu();
            String opcao = scanner.next();

            switch (opcao) {
                case "1":
                    converterMoeda(scanner);
                    break;
                case "2":
                    mostrarInstrucoes();
                    break;
                case "3":
                    sair = true;
                    System.out.println("Saindo... Obrigado por usar o conversor!");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }

        scanner.close();
    }
}
