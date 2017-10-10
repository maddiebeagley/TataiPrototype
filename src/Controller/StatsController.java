package Controller;

/**
 * This class is the controller for the stats class. This shows the user what are their
 * current scores for the application and writes/overrides all information to a hidden file
 * in the top level directory.
 * @author Maddie Beagley and Emilie Pearce
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class StatsController {

	double _averageMark;
	int _numberOfTests;
	int _highScore;

	@FXML private Label averageScore;
	@FXML private Label highScore;
	@FXML private Label testsTaken;
	@FXML private Button hardButton;
	@FXML private Button mediumButton;

	/**
	 * Sets the values of the labels that display the user's results with their 
	 * average high score, highest score and number of tests they have taken.
	 */
	public void initialize() {
		try {
			//finds previous average score and converts it to an integer
			String averageScoreString = Files.readAllLines(Paths.get(".easyResults.txt")).get(0);
			averageScore.setText(averageScoreString);

			//finds previous high score and converts it to an integer
			String highScoreString = Files.readAllLines(Paths.get(".easyResults.txt")).get(1);
			highScore.setText(highScoreString);

			//finds previous number of tests run and converts to an integer
			String numOfTestsString = Files.readAllLines(Paths.get(".easyResults.txt")).get(2);
			testsTaken.setText(numOfTestsString);
			
		} catch (Exception e) {

		}
	}

	/**
	 * Method takes an action event on the return to menu button.  The main menu scene is
	 * displayed and the level view is taken away
	 * @param event
	 */
	public void returnMainMenu(ActionEvent event) {
		System.out.println("Event triggering return to main menu");
		//main scene should be reloaded.

		// Get the main stage to display the scene in
		Stage stageEventBelongsTo = (Stage) ((Node)event.getSource()).getScene().getWindow();

		Scene mainMenuScene = null;
		try {
			mainMenuScene = new Scene(FXMLLoader.load(getClass().getResource("/View/MainMenu.fxml")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//mainMenuScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stageEventBelongsTo.setScene(mainMenuScene);
	}
	
	public void displayResults(ActionEvent e) {
		String filename;
		
		if (e.getSource().equals(hardButton)) {
			filename = ".hardResults.txt";
		} else if (e.getSource().equals(mediumButton)) {
			filename = ".mediumResults.txt";
		} else {
			filename = ".easyResults.txt";
		}
		
		try {
			//finds previous average score and converts it to an integer
			String averageScoreString = Files.readAllLines(Paths.get(filename)).get(0);
			averageScore.setText(averageScoreString);

			//finds previous high score and converts it to an integer
			String highScoreString = Files.readAllLines(Paths.get(filename)).get(1);
			highScore.setText(highScoreString);

			//finds previous number of tests run and converts to an integer
			String numOfTestsString = Files.readAllLines(Paths.get(filename)).get(2);
			testsTaken.setText(numOfTestsString);
			
		} catch (Exception exception) {

		}
		
	}




}
