package com.gohealth.issue_tracker;

import com.gohealth.issue_tracker.command.MainCommand;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class IssueTrackerApplication implements CommandLineRunner {

    private final CommandLine.IFactory factory;

    public IssueTrackerApplication(CommandLine.IFactory factory) {
        this.factory = factory;
    }

    public static void main(String[] args) {
        SpringApplication.run(IssueTrackerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            CommandLine cmd = createCommandLine(factory.create(MainCommand.class));
            cmd.execute(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // extraction point for spying
    protected CommandLine createCommandLine(Object command) {
        return new CommandLine(command, factory);
    }
}


