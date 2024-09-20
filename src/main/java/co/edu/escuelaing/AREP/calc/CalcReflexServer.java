package co.edu.escuelaing.AREP.calc;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import java.util.Arrays;

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
                System.out.println(query);
                String response;
                if (command.equals("bbl")){
                    response = Arrays.toString(bubbleSort(values));
                }else {
                    response = computeMathCommand(command, values );
                }
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + "'{\"result\":"+response+"}'";
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

    /**
     * Extrae la URI de la peticion que se envio.
     * @param firstLine primera linea de la peticion que se envio.
     * @return URI con la informacion de la peticion.
     * @throws URISyntaxException Si la cadena es invalida.
     */
    private static URI getRequestURL(String firstLine) throws URISyntaxException {
        String url = firstLine.split(" ")[1];
        return new URI(url);
    }

    /**
     * Respuesta por defecto de la operacion en Calculadora.
     * @return Respuesta HTTP por defecto.
     */
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

    /**
     * Funcion para ejecutar la operacion deseada del usuario.
     * @param command operacion a ejecutar.
     * @param values valores con los que se va a ejecutar la operacion.
     * @return Resultado de la operacion. En caso de no poder ejecutar la operacion se informa del error.
     */
    public static String computeMathCommand(String command,String[] values)  {
        Class<?> c = Math.class;
        Class[] parameterTypes;
        Object[] params;
        if (values.length < 1 || values.length > 3) {
            return "Values exceed";
        }else if (values.length == 3){
            parameterTypes = new Class[]{double.class, double.class,double.class};
            params = new Object[]{Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2])};
        } else if (values.length == 2) {
            parameterTypes = new Class[]{double.class, double.class};
            params = new Object[]{Double.parseDouble(values[0]), Double.parseDouble(values[1])};
        } else {
            parameterTypes = new Class[]{double.class};
            params = new Object[]{Double.parseDouble(values[0])};
        }
        Method m = null;
        try {
            m = c.getDeclaredMethod(command, parameterTypes);
        } catch (NoSuchMethodException e) {
            return "Method not Found";
        }
        try {
            return m.invoke(null,params).toString();
        } catch (IllegalAccessException e) {
            return e.getCause().toString();
        } catch (InvocationTargetException e) {
            return e.getCause().toString();
        }
    }

    /**
     * Algoritmo de ordenamiento bubble sort. Devuuelve es resultado con los valores ordenados.
     * @param arr Arreglo de valores a ordenar.
     * @return Arreglo con los valores ordenados.
     */
    public static String[] bubbleSort(String[] arr) {
        int i, j, temp;
        int n = arr.length;
        boolean swapped;
        for (i = 0; i < n - 1; i++) {
            swapped = false;
            for (j = 0; j < n - i - 1; j++) {
                if (Integer.parseInt(arr[j])> Integer.parseInt(arr[j + 1])) {
                    temp = Integer.parseInt(arr[j]);
                    arr[j] = arr[j + 1];
                    arr[j + 1] = String.valueOf(temp);
                    swapped = true;
                }
            }
            if (!swapped)
                break;
        }
        return arr;
    }

}