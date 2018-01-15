package pt.ismai.atgest.Api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class Send {
    public static String post(String protocol, String host, int port, String path, String params){
        try {
            URL url = new URL(protocol, host, port, path);
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setAllowUserInteraction(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(params);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();

            StringBuilder sb = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line = in.readLine()) != null){
                sb.append(line);
            }
            in.close();
            return sb.toString();
        }
        catch(Exception e){
            return new String(e.getMessage());
        }
    }

    public static String get(String protocol, String host, int port, String path){
        try {
            URL url = new URL(protocol, host, port, path);
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            conn.setAllowUserInteraction(true);
            conn.connect();

            StringBuilder sb = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line = in.readLine()) != null){
                sb.append(line);
            }
            in.close();
            return sb.toString();
        }
        catch(Exception e){
            return new String(e.getMessage());
        }
    }
}
