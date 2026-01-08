package com.internaldev;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class WebhookListener {

    // --- CONFIGURATION ---
    private static final int PORT = 9090;
    private static final String REMOTE_HOST = "192.168.4.17";
    private static final String USER = "internaldev";
    private static final String PRIVATE_KEY_CONTENT = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIJKQIBAAKCAgEArdLyorrmW3bceIEvwA3oQGcQXf8uAKQ33hi3HCIl3hrAxBoM\n" +
            "qANNbf/Z/D5HPvaM8cC4i1W8p6cv6J4Wme3fXgcLn9wpL767AROT93mrNJXJZzd7\n" +
            "fN0sksdNFztv26YQ2iB2XEgOeZensxYXDzfU1olhyhBQgdjiRXCsvcvbWnbwzpsP\n" +
            "S+66SrGII6fX63LjzjQ75FSXkqFngnjlTx/zY4ITDsbpDh9Aw4JY7z91Ncvw65mB\n" +
            "nCPd2dVTo8VZ+AniMVF5stiN9Qs1nCR+o2ujGEzDQvnNJc9t7jHk5BO228rEga2v\n" +
            "3po/R/rZARfMRxADtblTWosreIoJ9HUy3y/3C3WqVRJJp3FR6cq6FvKIoyHuXBqa\n" +
            "Um+fiJmqi4HXVn7Gz2OPfDi0UXcV4gV0X5NjcAQ9vNsX8SnbvvjgumL25sM1kbGW\n" +
            "xfJNY3O7siieZKax9lf+7KG0c6QOvDRQohWvz/6R0wLqC6VbcDxoqagWy54JJ+MW\n" +
            "gig9MtTokzSNrTe38Cs5o6XO/qPqC4xVwRRo0cxFBvqPcaQmEzeB1cwB/0Y1OrwR\n" +
            "+1XA9SOqbgP2hYxnUdM6mmDmghbrINAHj1kT6p7Jn5qEjj0rmEwJVdUHVJ+JE157\n" +
            "a3hca0acK1A7G6OsWY3JI3PeKHxjySq5/Abj9Zj9EvapM6O4wUMdH5KeOucCAwEA\n" +
            "AQKCAgBXk4+Rs266KGGeCSCAt8ELKaISaa3IvvQy3urqrr27cGkspB86Syeowx4V\n" +
            "u17z9w4+AyY3dUPuiApYPcYPWcyPOCeALFFH7+WR/kYvikpa3Riy+z4pQ9ngPa+2\n" +
            "g8iwtrOI4pAfoGJCmsjwLrIBhdSjHmZ5gsy9DwO5jrJJkbr7ki9zOKZ1UFrlpRp9\n" +
            "NGRSCPmDe8etG32eb9mR381Qlz1uJivp5wemiNJ9bl/WcndgWlcQrK+jdmKhDJzu\n" +
            "R1Ez6e7fgDg7hmXHTxV8jNqNBX8PkCVUyjy0zxSIzPSQgOpBRKDK4TJZigdWAalx\n" +
            "pobSStxDo5K+bJ3DRnELjDzMPANQGwgLX4n6w7G4d0CSIQmhKtwxXNLGFNykgDKw\n" +
            "KfzJwPXGPk650xmhyxGw5kuu2SRgcNIrIpElu0KfO2DArqCyBFZf4QaB+2De2+Cr\n" +
            "a319H4VZlU20qVWkGx/NaYU4tYBoMwd3u4D7DL7v8JMz2K5W69J8s3mwNy19L7Y6\n" +
            "bK3bn6KGPF86cppebBdHe31cuTcueA667GRb5qVD2NOIf1mUnGR/dHcHlRuPQy4d\n" +
            "1p/H32Iqaab/0l2tl/qCBClvzEenTyNZ6PNNsnP97fRgjsT78jzdeliPxvxhLaeu\n" +
            "b9zUOc0UEb/VyWxbLte4f5p/dSQshy8dZw4gcJRjQ+W4K6OoyQKCAQEA207fXx7U\n" +
            "M4X0eifqeivj6PSj7CWcAXIcMi05FoPCn/xduayRkDeGSib+GoVf51W6oiT+9c7p\n" +
            "gB6xwZeX/e/LXEaWTDghbgWUxn/Mrc2W9gzYZomZPDTUeTz7K0mlN7o0FNhGh0KN\n" +
            "iGKWjB1Uc6EGQn9Jm81wIdnll8F2u+da/Uz2xy6BYBXUSD9Obe+ilHh3PD4WSrdG\n" +
            "KCxPmgsurZM93vcQFVpyXJWp9jpmpO3dwSdMxrRjSeoFhjtOyloX8b5AZH4hsP3K\n" +
            "OdIELjs9IC32zB0sE5khRPe3mwBb6Q7h6DZ6zXyl5XV9yE9DjaUtMdPcKDd7JJCv\n" +
            "9EOb1T/RHmmpYwKCAQEAyuf1YaAE0zf7KyV81Y57STcnRJcJUGBpsSCLn8zGuZJf\n" +
            "NehnocUNsRjB4bPvNLFRY8g34pm60Zwou1Q8gRFd/klnpkqP4PpUIgnLIijV1qWm\n" +
            "sAEZYS95cz/n9gzuwDG+ltq36oqfndI2FhVyCCpCOlog2NLmXkOSunyCvE0YlnYv\n" +
            "qdNd/PY2mL+PJQN1ihqcVJUb8MhEFbnvyqdHHumoeizS9r2k5iJWALrwcIUgt8wW\n" +
            "yhmlCqWu3L55TFIIxtV+Yq74vg+Scwc04KtdyRiUb+N1a8G/w8NJ3QUyj6RWBrev\n" +
            "8mxj7iSm3TYPKrMcSeW7LuOAAHaIVO8AySZVHmshrQKCAQEAzCLHoxfkOC1pWseK\n" +
            "57uxAqetaeO8KlBYVJBpwj5VTGwxpeIvAInek2lmP7OEiKXw+nrrZEz8Zoirt2Lv\n" +
            "VKz/o14FCt94ppvFV1ugXuuuqEtep1tPao5WdJ/BwBJmGtBVKE4EoGTfr2LtdXDb\n" +
            "qmHpnL40+zSoOqpDnxWy+scol/0q/E6amKDZH4Ll80kLEZ5shoPYZiXgxQYx9laQ\n" +
            "VXtoLQHWU7oEF5AsS4kjAER1lRSKz68beLK//6l29r4T6a7I64HIxKIHufh2cb4s\n" +
            "cKa4QlipLXjtUfAvVLVp2C9FMsx7VIjBNyw9L9YwMEnfHrFxDXJPSsLvNbQ0+rPT\n" +
            "aLa7twKCAQEAtQt7WvxY0c4lgj4UwMg9IPrhQsYxPVKJe846Rv+EYZhE8+jGbwVQ\n" +
            "TjB07lqKmU2N/aZslvfsRZM8lMl4owFICNiebzYgYvF5mFV8tqIRC1ELBgg1CQ4/\n" +
            "I6Jl6plnfn3kPN4zNEc0XLFwKQxBjx+FsGMy48RqYLmSdmsai8GOqtIRfUpnlopp\n" +
            "9KkXpEMN4YgU7TOOlq1acrhRSVUwlFiQR1MveZGFTQJeEpC6cOuYYebsfq+bH4Du\n" +
            "IXCi8oJP6nocJxlQQOXMGm7Xs4+0QvZ3m84su1VJMfdSNK+qiZhsWdoUWA52q8pw\n" +
            "SPkvVz4xg45EEnZThBY/VulaniBh2p5kkQKCAQAssozSLk3tFzYTIV+X/yvs1BIF\n" +
            "0uLnbeyZtzK2a/98MRwlZCQWG53nk6C4wYJGfc8kFb0KSlvfFHcvVjM/GgxjYdHU\n" +
            "qTVvAng25qi1e1MUTqnXDkp5cX9rs3WxAEPHBZSH2E5yAEhxITzG8DqVI74Sttdq\n" +
            "kaJTbJl5IAu3fPGLrAnLeYCn9BtUNpLTPerBxoJGATXv08iJbVMQCt87Um/hI8zV\n" +
            "CkusSH9OcXWfCnHT2zXxP99FMg3Il5H1KrRtsI4rk4AIKn8l8BYKrUqUWBcIviWI\n" +
            "kYZU/tw762hCgbsnLPRBVPky/pvdcvDa2EbQzvzfI2L8y899mCG0ttRl6Xu5\n" +
            "-----END RSA PRIVATE KEY-----\n";

    private static final String JBOSS_SCRIPT = "/app/scripts/startJBoss.sh";
    private static final String MULE_SCRIPT  = "/app/scripts/startMule.sh";
    private static final String TOMCAT_SCRIPT = "/app/scripts/startTomcat.sh";
    private static final String SECONDARY_TOMCAT_SCRIPT = "/app/scripts/startSecondaryTomcat.sh";

    private final RemoteCommandExecutor commandExecutor;

    public WebhookListener(RemoteCommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public static void main(String[] args) throws IOException {
        RemoteCommandExecutor commandExecutor = new SshRemoteCommandExecutor(REMOTE_HOST, USER, PRIVATE_KEY_CONTENT);
        WebhookListener webhookListener = new WebhookListener(commandExecutor);
        webhookListener.start();
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/webhook", new WebhookHandler(this));
        server.setExecutor(null); // Default executor
        server.start();
        System.out.println("🚀 Auto-Restarter Listener started on port " + PORT);
    }

    static class WebhookHandler implements HttpHandler {
        private final WebhookListener listener;

        public WebhookHandler(WebhookListener listener) {
            this.listener = listener;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                try {
                    Gson gson = new Gson();
                    JsonObject payload = gson.fromJson(jsonBuilder.toString(), JsonObject.class);

                    String msg = payload.has("msg") && !payload.get("msg").isJsonNull()
                            ? payload.get("msg").getAsString()
                            : "";

                    String serviceName = "Unknown";
                    if (payload.has("monitor") && !payload.get("monitor").isJsonNull() && payload.get("monitor").isJsonObject()) {
                        JsonObject monitor = payload.getAsJsonObject("monitor");
                        if (monitor.has("name") && !monitor.get("name").isJsonNull()) {
                            serviceName = monitor.get("name").getAsString();
                        }
                    }

                    System.out.println("Received Alert: " + msg);

                    if (msg.toLowerCase().contains("down")) {
                        listener.handleDownEvent(serviceName);
                    } else {
                        System.out.println("Service is UP or Testing. No action taken.");
                    }

                    String response = "Received";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, 0);
                    exchange.getResponseBody().close();
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    public void handleDownEvent(String serviceName) {
        String scriptToRun = null;

        if (serviceName.toLowerCase().contains("jboss")) {
            scriptToRun = JBOSS_SCRIPT;
        } else if (serviceName.toLowerCase().contains("mule")) {
            scriptToRun = MULE_SCRIPT;
        } else if (serviceName.toLowerCase().contains("tomcat")) {
            scriptToRun = TOMCAT_SCRIPT + " && " + SECONDARY_TOMCAT_SCRIPT;
        }

        if (scriptToRun != null) {
            System.out.println("⚠️ " + serviceName + " is DOWN. Triggering restart...");
            try {
                commandExecutor.executeCommand(scriptToRun);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No script configured for service: " + serviceName);
        }
    }
}
