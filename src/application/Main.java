package application;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			
			Scene introScene = new Scene(FXMLLoader.load(getClass().getResource("IntroMenu.fxml")));
			Scene mainScene = new Scene(FXMLLoader.load(getClass().getResource("MainMenu.fxml")));
			Scene levelScene = new Scene(FXMLLoader.load(getClass().getResource("Level.fxml")));
			
			introScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setScene(introScene);
			primaryStage.setResizable(false);
			primaryStage.setHeight(400);
			primaryStage.setWidth(600);
			primaryStage.show();
			
			//changed the scene transition to average as looking attempt at a level menu - to be changed dw
			//wanted to fiddle around functionality of changing labels ect. Should generate a random number
			//when pressing the button and print appropriate maori word. 
			PauseTransition delay = new PauseTransition(Duration.seconds(2));
			delay.setOnFinished( event -> primaryStage.setScene(levelScene));
			delay.play();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
