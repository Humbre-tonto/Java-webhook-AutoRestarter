package com.internaldev;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class SshRemoteCommandExecutor implements RemoteCommandExecutor {

    private final String remoteHost;
    private final String user;
    private final String privateKeyContent;

    public SshRemoteCommandExecutor(String remoteHost, String user, String privateKeyContent) {
        this.remoteHost = remoteHost;
        this.user = user;
        this.privateKeyContent = privateKeyContent;
    }

    @Override
    public void executeCommand(String scriptPath) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;

        try {
            byte[] keyBytes = privateKeyContent.getBytes(StandardCharsets.UTF_8);
            jsch.addIdentity("server17_memory_key", keyBytes, null, null);

            // Connect
            session = jsch.getSession(user, remoteHost, 22);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no"); // Ignore known_hosts for simplicity
            session.setConfig(config);
            session.connect(5000); // 5s timeout

            // Execute Command (Load Profile + Run Script)
            String command = "source ~/.bash_profile; nohup " + scriptPath + " > /dev/null 2>&1 &";

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.connect();

            System.out.println("✅ Command Sent: " + command);

        } catch (JSchException e) {
            System.err.println("❌ SSH Failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
    }
}
