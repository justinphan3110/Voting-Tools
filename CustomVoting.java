//Author: Long Phan				ID: lnp26
package Main;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.Printer;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class CustomVoting extends Application {
	// Variable that will be used in this Custom Voting
	private List<String> nameList = new ArrayList<>(); // The initial Name list
	private String[] namelist = new String[nameList.size()];
	private int[] vote; // The result list of voting
	Button voteButton, resultButton, cancelButton; // Button to use

	// ChoiceBox setting
	private String current; // The current value of the choice box
	private ChoiceBox<String> choice; // ChoiceBox

	// DialogBox setting
	private boolean finishAddingFlag = true;
	private String text = ""; //text appear at expanable tabs
	private TextInputDialog inputDialog, customVote; // TextDialog and its answer
	Optional<String> nameAnswer;
	private String finalResult = "";
	private String ansObjective; // The type of vote for the custom

	// CustomVoteBox Setting
	Optional<String> customAnswer;
	boolean InputFlag = true;		
	boolean retryFlag = true;				//Check if user want to retry the setting input of error
	
	public void start(Stage primaryStage) {
		while(retryFlag) {
		customVoteDialog(); // The type of this poll
		}
		if (InputFlag) {
			while (finishAddingFlag) { // Typing the member of the poll
				this.dialogBox();
			}
		if(!nameList.isEmpty())			//Only run ChoiceBox when NameList has elements
			this.choiceBox(primaryStage);
		} else
			System.out.println("No input");
	}

	//Box
	public void choiceBox(Stage primaryStage) {

		namelist = nameList.toArray(namelist); // convert ArrayList nameList into String array namelist
		vote = new int[namelist.length]; // vote list will always equal the nameList

		for (int i = 0; i < nameList.size(); i++) {
			vote[i] = 0;
		}

		Label label = new Label("Select a " + ansObjective);
		choice = new ChoiceBox<String>();
		choice.getItems().addAll(nameList);
		choice.setOnAction(this::processChoice);

		voteButton = new Button("Vote");
		resultButton = new Button("Results");
		HBox buttons = new HBox(voteButton, resultButton);
		buttons.setSpacing(20);
		Insets inset = new Insets(15, 0, 0, 0);
		buttons.setPadding(inset);
		buttons.setAlignment(Pos.TOP_RIGHT);

		voteButton.setOnAction(this::processVoteButtonPush);
		resultButton.setOnAction(this::processResultButtonPush);

		VBox root = new VBox(label, choice, buttons);
		root.setPadding(new Insets(15, 15, 15, 25));
		root.setSpacing(10);
		root.setStyle("-fx-background-color: skyblue");
		Scene scene = new Scene(root, 300, 150);
		primaryStage.setTitle("Voting " + ansObjective.toUpperCase());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void dialogBox() {
		ButtonType finishButton = new ButtonType("Finish", ButtonBar.ButtonData.CANCEL_CLOSE);
		ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
		inputDialog = new TextInputDialog();
		inputDialog.getDialogPane().getButtonTypes().setAll(addButton, finishButton);

		
		inputDialog.setHeaderText(null);
		inputDialog.setTitle(ansObjective);
		inputDialog.setContentText("Enter a " + ansObjective + "(q to quit): ");
		Label label = new Label("History Add");
		
		TextArea textArea = new TextArea(text);
		textArea.setEditable(false);
		textArea.setWrapText(false);
		textArea.setMaxHeight(Double.MAX_VALUE);
		textArea.setMaxWidth(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		inputDialog.getDialogPane().setExpandableContent(expContent);
		nameAnswer = inputDialog.showAndWait();
		if (nameAnswer.isPresent()) {
			if (nameAnswer.get().equals("quit") || nameAnswer.get().equals("q")) {
				finishAddingFlag = false;
			} else {
				if (this.isNullOrEmpty(nameAnswer.get())) {
					System.out.println("Please type correct input.");
					text += "Please type correct input.\n";
				} else if (nameList.contains(nameAnswer.get())) {
					System.out.println("The list already contains " + nameAnswer.get());
					text += "The list already contains " + nameAnswer.get() + "\n";
				} else {
					nameList.add(nameAnswer.get());
					System.out.println("Adding " + nameAnswer.get());
					text += "Adding " + nameAnswer.get() + "\n";
					
				}
			}
		} else {
			finishAddingFlag = false;
		}		
	}
	
	
	public void customVoteDialog() {
		customVote = new TextInputDialog();
		customVote.setTitle("Setting");
		customVote.setHeaderText(null);
		customVote.setContentText("The type of Vote");
		customAnswer = customVote.showAndWait();
		if (customAnswer.isPresent())
			if (!this.isNullOrEmpty(customAnswer.get())) {
				ansObjective = customAnswer.get();
				retryFlag = false;
			} else {
				this.noInputErrorDialog();
				
		}
		else {
			this.noInputErrorDialog();
		}
	}
	
	public void noInputErrorDialog() {
		ButtonType retryButton = new ButtonType("Retry", ButtonBar.ButtonData.OK_DONE);
		
		ButtonType endButton = new ButtonType("End", ButtonBar.ButtonData.CANCEL_CLOSE);
		
		Alert error = new Alert(AlertType.ERROR);
		error.getDialogPane().getButtonTypes().setAll(retryButton, endButton);
		error.setTitle("No input!");
		error.setHeaderText("No input!");
		error.setContentText("Choose " + retryButton.getText() + " or " + endButton.getText());
		Optional<ButtonType> choice = error.showAndWait();
		if(choice.get() == endButton) {
			retryFlag = false;
			InputFlag = false;
		}
}

	
	public void resultDialog(String contentText) {
		Alert result = new Alert(AlertType.INFORMATION);
		result.setTitle("Result");
		result.setHeaderText("The Result of your " + this.ansObjective + " poll.");
		result.setContentText(contentText);

		StringWriter write = new StringWriter();
		PrintWriter print = new PrintWriter(write);

		Label labelResult = new Label("The result are:");

		TextArea textArea = new TextArea(this.printResult());
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxHeight(Double.MAX_VALUE);
		textArea.setMaxWidth(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(labelResult, 0, 0);
		expContent.add(textArea, 0, 1);
		result.getDialogPane().setExpandableContent(expContent);

		result.showAndWait();

	}

	// The Button in the Application
	public void processChoice(ActionEvent event) {
		current = nameList.get(choice.getSelectionModel().getSelectedIndex());
	}

	public void processVoteButtonPush(ActionEvent event) {

		if (event.getSource() == voteButton) {
			for (int i = 0; i < namelist.length; i++) {
				if (namelist[i] == current) {
					vote[i]++;
					System.out.println("Vote " + namelist[i]);
				}
			}
		}
	}

	public void processResultButtonPush(ActionEvent event) {
		if (event.getSource() == resultButton) {
			orderedResult();
		}
		this.resultDialog(finalResult);
	}
	
	//Extra method to print
	public void orderedResult() {
		int mid = 0;
		String midName = "";
		boolean flag = true;
		while (flag) {
			flag = false;
			for (int i = 0; i < (namelist.length - 1); i++) {
				if (vote[i] > vote[i + 1]) {
					// Arrange the result list from low to high
					mid = vote[i + 1];
					vote[i + 1] = vote[i];
					vote[i] = mid;
					// Arrange the name list according to the ordered result list
					midName = namelist[i + 1];
					namelist[i + 1] = namelist[i];
					namelist[i] = midName;

					flag = true;
				}
			}
		}
		int voteLength = 0;
		String nameResult = "";
		if (vote.length != 0) {
			voteLength = vote.length - 1;
			nameResult = namelist[voteLength];
			boolean flag2 = true;

			if (vote.length == 1) {
				finalResult = namelist[0] + ": " + vote[0];
			} else {
				if (vote[voteLength] != vote[voteLength - 1]) {
					finalResult = namelist[voteLength] + ", " + vote[voteLength];
				} else {
					int i = 0;
					while (flag2 && i < voteLength) {
						flag2 = false;
						i++;
						if (vote[voteLength] == vote[voteLength - i]) {
							nameResult = nameResult + ", " + namelist[voteLength - i];
							flag2 = true;

						}

					}
					finalResult = nameResult + ": " + vote[voteLength];
				}
			}
		} else {
			finalResult = "No input";

		}
	}

	public String printResult() {
		String resultFinal = "";
		System.out.println("===============================================");
		for (int i = 0; i < vote.length; i++) {
			System.out.println(vote[i] + "\t " + namelist[i]); // Print out the whole voting result
			resultFinal += vote[i] + "\t " + namelist[i] + "\n";
		}
		System.out.println("===============================================");
		// Return String for the TextArea in result Dialog
		return "===============================================\n" + resultFinal
				+ "===============================================";
	}
	
	private boolean isNullOrEmpty(String text) {
		String textTrim = text.trim();
		if (text.isEmpty() || textTrim.length() == 0) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		launch(args);
	}
}