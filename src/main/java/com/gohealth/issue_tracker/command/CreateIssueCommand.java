package com.gohealth.issue_tracker.command;

import com.gohealth.issue_tracker.model.Issue;
import com.gohealth.issue_tracker.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(name = "create", description = "Create a new issue")
public class CreateIssueCommand implements Runnable {

    @Option(names = {"-d", "--description"}, required = true, description = "Issue description")
    private String description;

    @Option(names = {"-p", "--parentId"}, description = "Parent issue ID")
    private String parentId;

    private final IssueService issueService;

    @Autowired
    public CreateIssueCommand(IssueService issueService) {
        this.issueService = issueService;
    }

    @Override
    public void run() {
        // Let picocli handle the exception thrown by the service layer.
        // It will automatically translate the exception into a non-zero exit code.
        Issue issue = issueService.createIssue(description, parentId);

        // If the service returns null, throw a RuntimeException
        if (issue == null) {
            throw new RuntimeException("Failed to create issue. Please check your input and try again.");
        }

        System.out.println("Created issue: " + issue.getId());
    }
}