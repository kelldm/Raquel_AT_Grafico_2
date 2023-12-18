package org.example;
import com.google.gson.Gson;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    static Gson gson = new Gson();
    static Timer timer = new Timer();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Controle de Acesso - AT JAVA");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 200);
        frame.setLayout(new FlowLayout());
        JLabel codigoLabel = new JLabel("Insira seu Código de Acesso: ");
        codigoLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField codigoTextField = new JTextField(30);

        codigoTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

                if (codigoTextField.getText().length() == 10) {
                    String resposta = sendRequestPost(codigoTextField.getText());
                    responseData respostaServer = gson.fromJson(resposta, responseData.class);

                    if ("1".equals(respostaServer.ACK)) {
                        frame.getContentPane().setBackground(Color.GREEN);
                        resetarCor(frame);

                    } else {
                        frame.getContentPane().setBackground(Color.RED);
                        resetarCor(frame);
                    }

                    SwingUtilities.invokeLater(() -> codigoTextField.setText(""));
                }

            }
            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                String resposta = sendRequestPost(codigoTextField.getText());
                responseData respostaServer = gson.fromJson(resposta, responseData.class);

                if ("1".equals(respostaServer.ACK)){
                    frame.getContentPane().setBackground(Color.GREEN);
                    resetarCor(frame);
                } else {
                    frame.getContentPane().setBackground(Color.RED);
                    resetarCor(frame);
                }
            }
        });

        frame.add(codigoLabel);
        frame.add(codigoTextField);

        frame.setVisible(true);
    }


    public static String sendRequestPost(String codigo) {
        try {
            String apiUrl = "http://localhost:8080/api/verifica";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = "{\"codigo\": \"" + codigo + "\"}";
            String jsonGson = gson.toJson(new requestData(codigo));

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonGson.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }

            reader.close();
            connection.disconnect();

            return response.toString();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private static class responseData {
        String ACK;

        responseData(String ACK){
            this.ACK = ACK;
        }

    }

    private static class requestData{
        String codigo;

        requestData(String codigo){
            this.codigo = codigo;
        }
    }



    private static void resetarCor(JFrame frame) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                frame.getContentPane().setBackground(UIManager.getColor("Panel.background"));
            }
        }, 3000);
    }

}