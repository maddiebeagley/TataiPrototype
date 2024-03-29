package Controller;

import java.io.BufferedReader;
import Model.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.nio.file.Paths;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * This class is the controller for a level ie. a test environment. The controller handles recording, playing 
 * recording back to the user, checking if their pronunciation was correct and then moving on to the next
 * question in the test.
 * @author Maddie Beagley and Emilie Pearce
 *
 */

public class LevelController {

	@FXML private Label numberToTest;
	@FXML private Label progressLabel;
	@FXML private Button backButton;
	@FXML private Button recordButton;
	@FXML private Button checkButton;
	@FXML private Button listenButton;
	@FXML private Label firstChance = new Label();
	@FXML private Label secondChance = new Label();
	@FXML private Label feedbackMessage;
	@FXML private AnchorPane helpWindow;
	@FXML private ProgressBar recordingProgress;
	@FXML private Circle circle1, circle2, circle3, circle4, 
	circle5, circle6, circle7, circle8, circle9, circle10;

	private TestType type;
	private int progress = 1;
	private Question _currentQuestion;
	private Test _test;
	private MediaPlayer _player;
	private final String RECORDINGFILEPATH = "RecordingDir/foo.wav";
	private Difficulty _difficulty;
	private int chances = 2;
	private Color red = Color.web("ef473a");
	private Color green = Color.web("56ab2f");
	private Color white = Color.web("ffffff");
	private String blueProgressBar = "-fx-accent: blue;";
	private String orangeProgressBar = "-fx-accent: orange;";
	private List<Circle> progressCircles;
	private Circle[] chanceCircles;


	/**
	 * Method is custom constructor for LevelController so parameters can be passed into it.
	 * the difficulty is set and a new test is made
	 * @param diff Difficulty of the test user wants to run (enum)
	 */
	public LevelController(Difficulty diff, TestType testType) {
		_difficulty = diff;
		type = testType;
		_test = new Test(_difficulty);
		System.out.println(_difficulty.toString() + " & " + type.toString());
		//generates a new directory
		File recordingDir = new File("RecordingDir/");
		if(!recordingDir.exists()) {
			recordingDir.mkdir();
		}
	}

	/**
	 * Makes the various parts of the level scene invisible until they are needed further on
	 * in the code.
	 */
	public void initialize() {

		addNewQuestionToTest();
		checkButton.setDisable(true);
		recordButton.setDisable(false);
		listenButton.setDisable(true);
		feedbackMessage.setVisible(false);
		recordingProgress.setVisible(false);

		progressCircles = new ArrayList<Circle>(Arrays.asList(circle1, circle2,
				circle3, circle4, circle5, circle6, circle7, circle8, circle9, circle10));
	}

	/**
	 * For now just having a play around - this method is called when the make random number
	 * button is clicked and will show the number and the word of that number in maori.
	 * Learning how to use events.
	 * @param event
	 */
	public void addNewQuestionToTest() {

		if (type == type.EQUATION) {
			_currentQuestion = Equation.create(_test.getdifficulty());
		} else {
			_currentQuestion = new Practice();
		}

		_test.addTestQuestion(_currentQuestion);
		numberToTest.setText(_currentQuestion.getDisplayString());

	}

	/**
	 * Uses a bash command to take a new recording. This functionality will be run in a 
	 * backgroud thread. Buttons (except return to main menu) will be disabled during the
	 * recording process and reenabled after. A new media player storing the current 
	 * recording will be instantiated once this recording has been taken.
	 * @param e
	 */
	public void takeRecording(ActionEvent e) {
		recordingProgressBar(blueProgressBar);
		String cmd = "arecord -d 4 -r 22050 -c 1 -i -t wav -f s16_LE  " + RECORDINGFILEPATH;

		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);

		//generates a new thread to execute the recording functionality
		Thread record = new Thread(() -> {
			try {
				listenButton.setDisable(true);					
				checkButton.setDisable(true);					
				recordButton.setDisable(true);

				pb.start().waitFor();
				//when recording has completed, run the onRecordComplete with the input 
				//being the recording file that has just been generated.
				System.out.println("recording ready to update");

				listenButton.setDisable(false);
				checkButton.setDisable(false);
				recordButton.setDisable(false);

				//instantiates a new media player with the new media recording set.
				_player = newMediaPlayer();

			} catch (InterruptedException ignored) { // if process is prematurely terminated
			} catch (IOException ioEvent) { //if process is incorrect (likely programmer error)
				throw new RuntimeException("Programmer messed up command...");
			}
		});
		//starts the thread running to take the recording.
		record.start();	
	}

	/**
	 * Uses the media player to play the current recording as long as that player has been
	 * correctly set. Nothing will play if the media player is set to null (or has not been set).
	 */
	public void playRecording() {	
		recordingProgressBar(orangeProgressBar);
		//if recording has been set for a level...
		if (_player == null) {
			System.out.println("recording/mediaplayer has not been properly initialised");
		} else {
			//for now just disabling all buttons so can't call listen while already listening.
			//if we have the time could be cool to 
			listenButton.setDisable(true);
			checkButton.setDisable(true);
			recordButton.setDisable(true);
			//plays media
			_player.play(); 
			//invokes a runnable that resets the mediaplayer and updates buttons
			_player.onEndOfMediaProperty();
		}
	}

	/**
	 * Creates a new media player which loads in the current media. player.setOnEndOfMedia(...)
	 * creates a runnable that should be executed each time player.onEndOfMediaProperty() method 
	 * called. Will be instantiated each time a new recording is taken.
	 * @return
	 */
	private MediaPlayer newMediaPlayer() {
		Media media = new Media(Paths.get(RECORDINGFILEPATH).toUri().toString());
		//generates a media player to play audio
		MediaPlayer player = new MediaPlayer(media);
		//sets a runnable that will be called when player.onEndOfMediaProperty() called
		player.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				//ensures media can be replayed.
				_player.stop();
				//reenables buttons for use
				recordButton.setDisable(false);
				checkButton.setDisable(false);
				listenButton.setDisable(false);
			}
		});
		return player;
	}

	/**
	 * Updates the state of the progress bar. Tracks how many rounds of the
	 * level have been made.
	 */
	private void updateProgressBar(Color color) {
		progress = _test.getNumberofRound();
		System.out.println("test round = " + _test.getNumberofRound());

		System.out.println("number of round: " + _test.getNumberofRound());
		Circle circle =	progressCircles.get(_test.getNumberofRound() - 1);

		circle.setFill(color);
		circle.setStroke(color);
	}

	/**
	 * Called only when the user is advancing to another question
	 */
	public void nextQuestion(ActionEvent event) {
		progress += 1; // Add 1 question to progress bar
		System.out.println("Progress = " + progress + " TestType = " + type.toString());
		if((progress == 11) && (type.equals(TestType.EQUATION))) {
			System.out.println("Results showing");
			showResults(event);
		}
		if((progress == 11) && (type.equals(TestType.PRACTICE))) {
			System.out.println("Restarting");
			clearAndStartAgain(event);
		}
		if(progress - 1 > 10) {
			throw new RuntimeException("Too many tests have been logged");
		}
		this.addNewQuestionToTest();
		progressLabel.setText("A tawhio noa " + progress + "/10");

		listenButton.setDisable(true);
		_player = null;
	}

	private void clearAndStartAgain(ActionEvent e) {
		// Get the main stage to display the scene in
		Stage stageEventBelongsTo = (Stage) ((Node)e.getSource()).getScene().getWindow();

		AnchorPane statsScene = null;
		try {
			System.out.println("Entering practice mode");
			LevelController controller = new LevelController(Difficulty.HARD, TestType.PRACTICE);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Level.fxml"));
			loader.setController(controller);
			statsScene = loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Scene scene = new Scene(statsScene);
		stageEventBelongsTo.setScene(scene);

	}

	/**
	 * Method takes user to a screen which displays their results, invoked
	 * when 10 rounds of the test have been completed or if user quits prematurely.
	 * @param event
	 */
	public void showResults(ActionEvent event) {
		Stage stageEventBelongsTo = (Stage) ((Node)event.getSource()).getScene().getWindow();
		AnchorPane resultsScene = null;
		ResultsController controller = null;
		try {
			controller = new ResultsController(_test, type);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Results.fxml"));
			loader.setController(controller);
			resultsScene = loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Scene scene = new Scene(resultsScene);
		stageEventBelongsTo.setScene(scene);
	}

	/**
	 * Method takes an action event on the return to menu button.  The main menu scene is
	 * displayed and the level view is taken away
	 * @param event
	 */
	public void backButtonEvent(ActionEvent event) {
		Stage stageEventBelongsTo = (Stage) ((Node)event.getSource()).getScene().getWindow();
		Button eventButton = (Button)event.getSource();

		try {
			Stage stage = new Stage(); 
			AnchorPane root;
			ExitPopupController popupController = new ExitPopupController(stageEventBelongsTo, "/View/MainMenu.fxml");
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ExitPopup.fxml"));
			loader.setController(popupController);
			root = (AnchorPane)loader.load();

			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(eventButton.getScene().getWindow());
			stage.showAndWait();
		} catch (IOException e1) {
			System.out.println("exception thrown");
			e1.printStackTrace();
		}

	}

	/**
	 * Method checks if the recording the user wants tested if the correct pronunciation
	 * for the current number and displays the respective instructions and feedback.
	 * @param e
	 */
	public void checkRecording(ActionEvent e) {
		Boolean correct = this.checkRecordingForWord();
		if(correct) {		
			_currentQuestion.setPass(true);
			chances = 2;

			feedbackMessage(true);
			updateProgressBar(green);

			PauseTransition delay = new PauseTransition(Duration.seconds(3));
			delay.setOnFinished( event -> this.nextQuestion(e) );
			delay.play();
			//checkButton.setDisable(true);
			//listenButton.setDisable(true);
		}
		else {
			chances--;

			if(chances == 0) { // If they have no more chances left
				_currentQuestion.setPass(false);
				updateProgressBar(red);
				chances = 2;
				feedbackMessage(false);
				PauseTransition delay = new PauseTransition(Duration.seconds(3));
				delay.setOnFinished( event -> this.nextQuestion(e) );
				delay.play();
				//checkButton.setDisable(true);
				//listenButton.setDisable(true);
			} else { // If they have one more chance left
				feedbackMessage(false);
				//checkButton.setDisable(true);
				//listenButton.setDisable(true);
			}

		}

	}

	/**
	 * This method handles showing the user their feedback based on what they pronounced
	 * @param b
	 */
	private void feedbackMessage(boolean b) {
		if(b) {
			feedbackMessage.setText("I tika koe i te whakautu!");
			feedbackMessage.setStyle("-fx-background-color: linear-gradient(to right, #56ab2f, #a8e063);");
		}
		else if((!b) && (chances == 1)) {
			feedbackMessage.setText("Kati ... tamata ano");
			feedbackMessage.setStyle("-fx-background-color: linear-gradient(to right, #ff8008, #ffc837);");
		}
		else {
			feedbackMessage.setText("Kaore, he he. \nKo te whakautu he " + _currentQuestion.getAnswerInt());
			feedbackMessage.setStyle("-fx-background-color: linear-gradient(to right, #cb2d3e, #ef473a);");
		}
		feedbackMessage.setVisible(true);
		PauseTransition delay = new PauseTransition(Duration.seconds(3));
		delay.setOnFinished( event -> feedbackMessage.setVisible(false) );
		delay.play();
	}

	/**
	 * Method checks if the recording that is currently in the recording directory is the
	 * word that is the current test that it is on.  Uses back commands to run the wav file
	 * through HTK and reads from the recout.lmf file to see if all parts of the word have
	 * been picked up in the analysis
	 * @return boolean if the recording is the correct number of now=t
	 */
	private boolean checkRecordingForWord() {
		ArrayList<String> output = new ArrayList<String>();
		System.out.println("Checking recording HTK bash");
		String cmd = "HVite -H HMMs/hmm15/macros -H HMMs/hmm15/hmmdefs -C user/configLR  "
				+ "-w user/wordNetworkNum -o SWT -l '*' -i recout.mlf -p 0.0 -s 5.0  "
				+ "user/dictionaryD user/tiedList " + RECORDINGFILEPATH;
		ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			System.out.println("Starting process");
			Process process = processBuilder.start();
			process.waitFor();
			FileReader in = new FileReader("recout.mlf");
			BufferedReader br = new BufferedReader(in);
			String line = null;
			while((line = br.readLine()) != null) {
				if((!(line.contains("#!MLF!#"))) && (!(line.contains("\"*/foo.rec\""))) && (!(line.contains("."))) && (!(line.contains("sil")))) {
					System.out.println("old line = " + line);
					String newLine = line.replaceAll("aa", "ā");
					System.out.println("new line = " + newLine);
					output.add(newLine);
				}
			}
			br.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		String numberWord = _currentQuestion.getAnswerString();
		String[] split = numberWord.split("\\s+");
		List<String> list = Arrays.asList(split);
		for(String s:split) {
			if(!(output.contains(s))) {
				System.out.println("word not there, exiting FALSE");
				return false;
			}
		}
		System.out.println("word there, exiting TRUE");
		return true;
	}

	public void recordingProgressBar(String progressStyle) {
		recordingProgress.setStyle(progressStyle);
		recordingProgress.setVisible(true);
		Task<Void> task = new Task<Void>(){
			@Override
			public Void call(){
				for (int i = 1; i <= 100; i++)    {
					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					updateProgress(i , 100);
				}
				recordingProgress.setVisible(false);
				return null;                
			}
		};
		recordingProgress.progressProperty().bind(task.progressProperty());
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
	}

	public void showInstructions() {
		helpWindow.setVisible(true);
	}

	public void hideInstructions() {
		helpWindow.setVisible(false);
	}



}
