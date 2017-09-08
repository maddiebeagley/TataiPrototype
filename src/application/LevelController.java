package application;

import java.io.File;
import java.io.IOException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LevelController {

	@FXML
	private Spinner spinner;
	
	@FXML
	private Button readyButton;
	
	@FXML
	private Label numberToTest;
	
	@FXML
	private Label numberWord;
	
	@FXML
	private ProgressBar progressBar;
	
	@FXML
	private Label progressLabel;
	
	@FXML
	private Button backButton;
	
	@FXML
	private Button circleButton;
	
	@FXML
	private Button recordButton;
	
	@FXML
	private Button listenButton;
	
	@FXML
	private StackPane stackPane;
	
	@FXML
	private JFXDialog dialog;
	
	private int progress = 0;
	//will store all the data associated with the current level
	private Result _currentLevelResult;
	//will store all data associated with entire test
	private Test _test;
	
	private Difficulty _difficulty;
	
	private int chances = 2;
	
	/**
	 * Method is custom constructor for LevelController so parameters can be passed into it.
	 * the difficulty is set and a new test is made
	 * @param diff Difficulty of the test user wants to run (enum)
	 */
	public LevelController(Difficulty diff) {
		_difficulty = diff;
		_test = new Test(_difficulty);
	}

	/**
	 * For now just having a play around - this method is called when the make random number
	 * button is clicked and will show the number and the word of that number in maori.
	 * Learning how to use events.
	 * @param event
	 */
	public void updateLabels(ActionEvent event) {
		_currentLevelResult = new Result(_test._difficulty);	
		//sets labels that show a number and the maori word corresponding to it
		numberToTest.setText(Integer.toString(_currentLevelResult._numberInt));
		numberWord.setText(_currentLevelResult._numberWord);
	}

	public void takeRecording(ActionEvent e) {
		//part 2 of cbf using bash as do not want to work on VM
		System.out.println("In method for taking a recording");

		//to be uncommented when there is a valid file for a recording.

		/*
		File recording = new File("recording");
		_currentLevelResult.setRecording(recording);
		 */
	}

	public void playRecording() {
		/*
		//if recording has been set for a level...
		if (_currentLevelResult._recording == null) {
			System.out.printlnlnlnln("recording has not been properly initialised");
			//play recording (cbf using bash as do not want to work on VM)
		} else {
			System.out.printlnlnlnln("In method for hearing a recording (recording not null)");
		}
		*/
		
		System.out.println("In method for playing a recording");
	}

	/**
	 * Updates the state of the progress bar. Tracks how many rounds of the
	 * level have been made.
	 */
	private void updateProgressBar() {
		progress = _test.getNumberofRound();
		System.out.println("update bar progress = " + progress);
		progressLabel.setText("Round " + progress + "/10");
		progressBar.setProgress((double) progress / 10);
	}
	/**
	 * Called only when the user is advancing to the next level, updates
	 * progress bar.
	 */
	public void nextLevel(ActionEvent event) {
		System.out.println("entered next question");
		//stores result of previous test in test model
		_test.addTestResult(_currentLevelResult);
		//instantiates a new result for the next level of the test
		_currentLevelResult = new Result(_test._difficulty);
		System.out.println("next level progress = " + progress);
		progress += 0.1;
		progressLabel.setText("Round " + Math.round(progress * 10) + "/10");
		System.out.println("progress label Round " + Math.round(progress * 10) + "/10");
		progressBar.setProgress(progress);
	}
	
	/**
	 * Method takes user to a screen which displays their results, invoked
	 * when 10 rounds of the test have been completed or if user quits prematurely.
	 * @param event
	 */
	public void showResults(ActionEvent event) {
		System.out.println("in method that sets the scene to results");
		
		Stage stageEventBelongsTo = (Stage) ((Node)event.getSource()).getScene().getWindow();
		
		Scene easyScene = null;
		try {
			easyScene = new Scene(FXMLLoader.load(getClass().getResource("Results.fxml")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		stageEventBelongsTo.setScene(easyScene);
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
			mainMenuScene = new Scene(FXMLLoader.load(getClass().getResource("MainMenu.fxml")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		mainMenuScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stageEventBelongsTo.setScene(mainMenuScene);
	}
	
	/**
	 * Method take an event on the ready button to start the test.  It disabled the 'ready' button
	 * and enabled all the other control buttons so the user can submit/record/listen to recordings
	 * before the actual test has started
	 * @param event
	 */
	public void readyButtonAction(ActionEvent event){
		readyButton.setDisable(true);
		readyButton.setVisible(false);
		updateLabels(event);
		circleButton.setDisable(false);
		recordButton.setDisable(false);
		listenButton.setDisable(false);
	}
	
	/**
	 * Method checks if the recording the user wants tested if the correct pronunciation
	 * for the current number and displays the respective instructions and feedback.
	 * ****ISSUE WITH PROGRESS BAR
	 * I know there HEAPS of code duplication, but i was trailing this out to see if the idea
	 * worked - if we wanna use it then ill come back to it and fix it
	 * Checking audio part of method needs doing
	 * @param e
	 */
	public void checkReccordingForWord(ActionEvent e) {
		System.out.println("Checking reccording...");
		Boolean correct = true;
		// Bash commands to check if recording is correct
		JFXDialogLayout layout = new JFXDialogLayout();
		dialog.setDialogContainer(stackPane);
		if(correct == false) {
			chances--;
			if(!(chances == 0)) {
				layout.setBody(new Text ("Oops, got that one wrong"));
			}
			else {
				layout.setBody(new Text ("No more chances"));
			}
			JFXButton dialogButton = new JFXButton("Try again");
			dialogButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					dialog.close();
					System.out.println("chances = " + chances);
					if(chances == 0) {
						updateLabels(e);
						updateProgressBar();
						chances = 2;
					}
				}
			});
			layout.setActions(dialogButton);
			dialog.setContent(layout);
			dialog.show();
			
		}
		else if(correct == true) {
			layout.setBody(new Text ("Yay, got it right!"));
			JFXButton dialogButton = new JFXButton("Continue");
			dialogButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					dialog.close();
					updateLabels(e);
					updateProgressBar();
				}
			});
			layout.setActions(dialogButton);
			dialog.setContent(layout);
			dialog.show();
			
			chances = 2;
		}
	}
	
}