package hfe;

import com.canoo.common.StringUtilities;
import com.ulcjava.environment.standalone.client.CustomizableStandaloneLauncher;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Standalone extends CustomizableStandaloneLauncher {

    public static void main(String[] args) {


        try(FileWriter writer = new FileWriter("c:/Temp/ulc.log")) {
            writer.write("start\n");
            Properties ps = new Properties();
            writer.write("client.properties laden\n");
            try (InputStream is = Standalone.class.getResourceAsStream(("/client.properties"))) {
                ps.load(is);
            } catch (Exception e) {
                writer.write(e.getMessage() + "\n");
            }
            writer.write("client.properties geladen\n");
            writer.write(ps.toString() + "\n");
            String sessionId = ps.getProperty("jsessionid");
            String codebase = ps.getProperty("codebase");
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
