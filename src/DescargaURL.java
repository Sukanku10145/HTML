import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.SocketTimeoutException;

public class DescargaURL {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java DescargaURL <URL> <nombre_archivo_salida>");
            System.exit(1);
        }

        String urlString = args[0];
        String outputFileName = args[1];

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Configurar timeouts
            connection.setConnectTimeout(5000); // 5 segundos para conectarse
            connection.setReadTimeout(5000);    // 5 segundos para leer datos

            // Desactivar el seguimiento automático de redirecciones
            connection.setInstanceFollowRedirects(false);

            // Obtener el código de respuesta HTTP
            int responseCode = connection.getResponseCode();
            System.out.println("Código HTTP de respuesta: " + responseCode);

            // Manejar la respuesta
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Leer el contenido HTML
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine).append("\n");
                }
                in.close();

                // Escribir el contenido en un archivo
                try (PrintWriter out = new PrintWriter(new FileWriter(outputFileName))) {
                    out.println(content.toString());
                    System.out.println("Contenido descargado y guardado en: " + outputFileName);
                } catch (IOException e) {
                    System.err.println("Error al escribir el archivo: " + e.getMessage());
                }

            } else if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                // Manejar redirecciones
                String newUrl = connection.getHeaderField("Location");
                System.out.println("Redirección detectada. Nueva URL: " + newUrl);
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                System.out.println("Error 404: Recurso no encontrado.");
            } else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                System.out.println("Error 500: Error interno del servidor.");
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                System.out.println("Error 403: Acceso prohibido.");
            } else {
                System.out.println("Código de respuesta no manejado: " + responseCode);
            }

            // Cerrar la conexión
            connection.disconnect();

        } catch (SocketTimeoutException e) {
            System.err.println("Error: Timeout al intentar conectar con la URL.");
        } catch (MalformedURLException e) {
            System.err.println("URL mal formada: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de conexión o lectura: " + e.getMessage());
        }
    }
}