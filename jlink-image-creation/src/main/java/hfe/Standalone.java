package hfe;

import com.canoo.common.StringUtilities;
import com.ulcjava.environment.standalone.client.CustomizableStandaloneLauncher;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Standalone extends CustomizableStandaloneLauncher {

    private static String getMac() throws UnknownHostException, SocketException {
        InetAddress ip = InetAddress.getLocalHost();
        NetworkInterface network = NetworkInterface.getByInetAddress(ip);
        byte[] mac = network.getHardwareAddress();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        return sb.toString();
    }

    public static void main(String[] args) {

        String urlString = args.length == 0 ? "http://localhost:8080" : args[0];

        try {

            Desktop dd = java.awt.Desktop.getDesktop();
            dd.browse(new URI(urlString + "/login?mac=" + getMac()));

            URL url = new URL(urlString + "/good");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            Callable<String> call =  Executors.privilegedCallable(() -> {
                String id = "no";
                while(id.equals("no")) {
                    try (InputStream is = con.getInputStream()) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                            StringBuilder sb = new StringBuilder();
                            reader.lines().forEach(l -> sb.append(l).append("\n"));
                            id = sb.toString();
                        }
                    }
                    Thread.sleep(1000);
                }
                return id;

            });
            Future<String> ff = Executors.newSingleThreadExecutor().submit(call);
            String valuesFromServlet = ff.get(10, TimeUnit.SECONDS);

            String[] values = valuesFromServlet.split("\n");
            String sessionId = values[0];
            String codebase = values[1];

            try(FileWriter writer = new FileWriter("c:/Temp/ulc.log")) {
                writer.write(args.length + "\n");
                writer.write("start\n");
                String launchString = "url-string=" + codebase + "/hfe.ulc";
                launchString += StringUtilities.isEmpty(sessionId) ? "" : ";jsessionid=" + sessionId;
                writer.write(sessionId + "\n");
                writer.write(launchString + "\n");
                writer.flush();
                Standalone launcher = new Standalone();
                launcher.launch(new String[]{launchString});
            } catch (IOException e) {
                handle(e);
            }
        } catch(Exception e) {
            handle(e);
        }
    }

    private static void handle(Exception e) {
        JPanel contentPane = new JPanel();
        contentPane.add(new JTextArea("Exception : " + e.getMessage()));
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(1400, 800));
        frame.setContentPane(contentPane);
        frame.setVisible(true);
    }

}
