package com.gohealth.issue_tracker.service;

import com.gohealth.issue_tracker.model.Issue;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GoogleSheetsIssueService implements IssueService {

    private final Sheets sheetsService;
    private final String SHEET_NAME = "Sheet1";
    private final String SPREADSHEET_ID = "1tcWPle1hz2XBf7iZv2lcruIj4O7FRo6w5mUHUiu8FYU";
    private final String RANGE = "Sheet1!A:F"; // A: ID, B: Description, C: Parent ID, D: Status, E: Created at, F: Updated at
    private final Map<String, Issue> issues = new HashMap<>();

    public GoogleSheetsIssueService() throws GeneralSecurityException, IOException {
        sheetsService = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                getCredentials()
        ).setApplicationName("Issue Tracker").build();
    }

    // Constructor for testing (inject mock)
    public GoogleSheetsIssueService(Sheets sheetsService) {
        this.sheetsService = sheetsService;
    }

    private static Credential getCredentials() throws IOException {
        InputStream in = GoogleSheetsIssueService.class.getResourceAsStream("/credentials.json");
        if (in == null) {
            throw new IllegalStateException("credentials.json not found in resources folder!");
        }

        GoogleCredential credential = GoogleCredential.fromStream(in)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        return credential;
    }


    @Override
    public Issue createIssue(String description, String parentId) {
        try {
            // 1. Get all existing IDs from the sheet
            String range = SHEET_NAME + "!A:A"; // Column A = IDs
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();

            List<List<Object>> values = response.getValues();

            // 2. Determine the next ID
            String newId;
            if (values == null || values.size() <= 1) {
                // Only header row or empty sheet
                newId = "AD-1";
            } else {
                String lastId = values.get(values.size() - 1).get(0).toString();
                int lastNum = Integer.parseInt(lastId.replace("AD-", ""));
                newId = "AD-" + (lastNum + 1);
            }

            // 3. Create Issue object
            Issue issue = new Issue();
            issue.setId(newId);
            issue.setDescription(description);
            issue.setParentId(parentId);
            issue.setStatus("OPEN");
            issue.setCreatedAt(LocalDateTime.now());
            issue.setUpdatedAt(LocalDateTime.now());

            // 4. Append new row to the sheet
            List<Object> row = Arrays.asList(
                    issue.getId(),
                    issue.getDescription(),
                    issue.getParentId() != null ? issue.getParentId() : "",
                    issue.getStatus(),
                    issue.getCreatedAt().toString(),
                    issue.getUpdatedAt().toString()
            );

            ValueRange appendBody = new ValueRange().setValues(Collections.singletonList(row));
            sheetsService.spreadsheets().values()
                    .append(SPREADSHEET_ID, RANGE, appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .execute();

            return issue;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create issue", e);
        }
    }

    //create \
    //  -d "Databricks Job is failing on parsing DoB" \
    //  -p AD-2

    // java -jar target/issue-tracker-0.0.1-SNAPSHOT.jar create -d "Databricks new2"

    @Override
    public Issue updateIssue(String id, String status) {
        try {
            ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, RANGE).execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) return null;

            for (int i = 1; i < values.size(); i++) { // start at 1 to skip header
                List<Object> row = values.get(i);
                if (row.get(0).equals(id)) {
                    row.set(3, status); // Status column D (index 3)
                    row.set(5, LocalDateTime.now().toString()); // Updated at column F (index 5)

                    ValueRange body = new ValueRange().setValues(Collections.singletonList(row));
                    sheetsService.spreadsheets().values()
                            .update(SPREADSHEET_ID, "Sheet1!A" + (i + 1) + ":F" + (i + 1), body)
                            .setValueInputOption("RAW")
                            .execute();

                    Issue issue = new Issue();
                    issue.setId((String) row.get(0));
                    issue.setDescription((String) row.get(1));
                    issue.setParentId((String) row.get(2));
                    issue.setStatus((String) row.get(3));
                    issue.setCreatedAt(LocalDateTime.parse((String) row.get(4)));
                    issue.setUpdatedAt(LocalDateTime.parse((String) row.get(5)));
                    return issue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//java -jar target/issue-tracker-0.0.1-SNAPSHOT.jar update -i AD-5 -s IN_PROGRESS


    @Override
    public List<Issue> listIssues(String status) {
        List<Issue> issues = new ArrayList<>();
        try {
            ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, RANGE).execute();
            List<List<Object>> values = response.getValues();
            if (values != null && values.size() > 1) {
                for (int i = 1; i < values.size(); i++) { // skip header
                    List<Object> row = values.get(i);
                    if (row.get(3).toString().equalsIgnoreCase(status)) {
                        Issue issue = new Issue();
                        issue.setId((String) row.get(0));
                        issue.setDescription((String) row.get(1));
                        issue.setParentId((String) row.get(2));
                        issue.setStatus((String) row.get(3));
                        issue.setCreatedAt(LocalDateTime.parse((String) row.get(4)));
                        issue.setUpdatedAt(LocalDateTime.parse((String) row.get(5)));
                        issues.add(issue);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return issues;
    }

    //java -jar target/issue-tracker-0.0.1-SNAPSHOT.jar list -s CLOSED
}
