package com.zimmem.jenkins.plugins.remotessh;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.jvnet.hudson.plugins.Messages;
import org.jvnet.hudson.plugins.SSHSite;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class RemoteSSHBuilder extends Builder {

    private String command;
    private String hostname;
    private String port;
    private String username;
    private String password;

    @DataBoundConstructor
    public RemoteSSHBuilder(String hostname, String username, String password, String port, String command) {
        this.setHostname(hostname);
        this.setUsername(username);
        this.setPort(port);
        this.setPassword(password);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
                                                                                                throws InterruptedException,
                                                                                                IOException {
        SSHSite site = new SSHSite(hostname, port, username, password);
        if (site != null && command != null && command.trim().length() > 0) {
            EnvVars env = build.getEnvironment(listener);
            String renderCommand = env.expand(command);
            listener.getLogger().printf("executing script:%n%s%n", renderCommand);
            return site.executeCommand(listener.getLogger(), renderCommand) == 0;
        }
        return true;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.SSH_JobConfigDisplayName();
        }

        @Override
        public Builder newInstance(StaplerRequest req, JSONObject formData)
                                                                           throws hudson.model.Descriptor.FormException {
            return req.bindJSON(clazz, formData);
        }

    }
}
