package com.gohealth.issue_tracker.junit.command.command;

import com.gohealth.issue_tracker.command.MainCommand;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainCommandTest {

    @Test
    void testRunPrintsWelcomeAndUsage() {
        // Arrange
        MainCommand mainCommand = new MainCommand();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Act
        mainCommand.run();

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Welcome to Issue Tracker CLI!"));
        assertTrue(output.contains("Please use one of the following commands:"));
        assertTrue(output.contains("create  - create a new issue"));
        assertTrue(output.contains("update  - update an existing issue"));
        assertTrue(output.contains("list    - list issues by status"));

        // Reset System.out
        System.setOut(originalOut);
    }
}