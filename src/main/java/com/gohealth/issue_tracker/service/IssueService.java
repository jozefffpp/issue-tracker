package com.gohealth.issue_tracker.service;

import com.gohealth.issue_tracker.model.Issue;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IssueService {
    Issue createIssue(String description, String parentId);
    Issue updateIssue(String id, String status);
    List<Issue> listIssues(String status);
}
