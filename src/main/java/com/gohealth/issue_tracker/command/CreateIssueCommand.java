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

    @Autowired
    private IssueService issueService;

    @Override
    public void run() {
        Issue issue = issueService.createIssue(description, parentId);
        System.out.println("Created issue: " + issue.getId());
    }
}