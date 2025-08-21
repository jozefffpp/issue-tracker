package com.gohealth.issue_tracker.command;

import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(
        name = "IssueTrackerApplication",
        mixinStandardHelpOptions = true,
        subcommands = {CreateIssueCommand.class, UpdateIssueCommand.class, ListIssuesCommand.class},
        description = "CLI to track issues in Google Sheets")
public class MainCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("**************************************************");
        System.out.println("Welcome to Issue Tracker CLI!");
        System.out.println("Please use one of the following commands:");
        System.out.println("  create  - create a new issue");
        System.out.println("  update  - update an existing issue");
        System.out.println("  list    - list issues by status");

        CommandLine.usage(this, System.out);
    }
}
