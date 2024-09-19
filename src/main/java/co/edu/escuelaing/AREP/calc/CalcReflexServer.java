package co.edu.escuelaing.AREP.calc;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;

public class CalcReflexServer {
    public static void main(String[] args) throws IOException, URISyntaxException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ServerSocket serverSocket = null;
        boolean running = true;
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 36000.");
            System.exit(1);
        }

        while(running){
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir 36000 ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            boolean isFirstLine = true;
            String firstLine = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if (isFirstLine){
                    firstLine = inputLine;
                    isFirstLine = false;
                }
                if (!in.ready()) {break; }
            }
            URI reqURL = getRequestURL(firstLine);
            if (reqURL.getPath().startsWith("/compreFlex")){
                String query = reqURL.getQuery().split("=")[1];
                String command = query.substring(0,query.lastIndexOf("("));
                String[] values =query.substring(query.lastIndexOf("(")+1,query.lastIndexOf(")")).split(",");
                System.out.println(command);
                System.out.println(values[0]);
                System.out.println(values[1]);
                System.out.println(query);
                String response = computeMathCommand(command, values[0], values[1]);
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + response;
            }else {
                outputLine = getDefaultResponse();
            }

            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
    private static URI getRequestURL(String firstLine) throws URISyntaxException {
        String url = firstLine.split(" ")[1];
        return new URI(url);
    }

    private static String getDefaultResponse(){
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta charset=\"UTF-8\">\n"
                + "<title>Title of the document</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<h1>Method Not Found</h1>\n"
                + "</body>\n"
                + "</html>\n";
    }

    public static String computeMathCommand(String command,String value1,String value2) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class c = Math.class;
        Class[] parameterTypes = {int.class,int.class};
        Method m = c.getDeclaredMethod(command, parameterTypes);
        Object[] params = {Integer.parseInt(value1), Integer.parseInt(value2)};
        m.invoke(null,params).toString();

        return m.invoke(null,params).toString();
    }

}