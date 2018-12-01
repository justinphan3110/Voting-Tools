
//Author: Long Phan				ID: lnp26
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CustomVoting extends Application {
	// Variable that will be used in this Custom Voting
	private List<Candidate> nameList = new ArrayList<>(); // The initial Name list
	private Candidate[] candidateList;
	Button voteButton, resultButton, saveButton, cancelButton; // Button to use

	// ChoiceBox setting
	private Candidate current; // The current value of the choice box
	private ChoiceBox<String> choice; // ChoiceBox

	// DialogBox setting
	private boolean finishAddingFlag = true;
	private String text = ""; // text appear at expanable tabs
	private TextInputDialog inputDialog, customVote; // TextDialog and its answer
	Optional<String> nameAnswer;
	private String ansObjective; // The type of vote for the custom

	// CustomVoteBox Setting
	Optional<String> customAnswer;
	boolean InputFlag = true;
	boolean retryFlag = true; // Check if user want to retry the setting input of error

	public void start(Stage primaryStage) {
		while (retryFlag) {
			TypeOfVote(); // The type of this poll
		}
		if (InputFlag) {
			while (finishAddingFlag) { // Typing the member of the poll until the finish
				this.addVoteObjective();
			}
			if (!nameList.isEmpty()) // Only run ChoiceBox when NameList has elements
				this.choiceBox(primaryStage);
		} else
			System.out.println("No input");
	}

	// Box
	public void choiceBox(Stage primaryStage) {
		Label label = new Label("Select a " + ansObjective);
		choice = new ChoiceBox<String>();
		for (Candidate temp : nameList) {
			choice.getItems().add(temp.getName());
		}
		;
		choice.setOnAction(this::processChoice);

		voteButton = new Button("Vote");
		resultButton = new Button("Results");
		saveButton = new Button("Save");
		HBox buttons = new HBox(voteButton, resultButton, saveButton);
		buttons.setSpacing(20);
		Insets inset = new Insets(15, 0, 0, 0);
		buttons.setPadding(inset);
		buttons.setAlignment(Pos.TOP_RIGHT);

		voteButton.setOnAction(this::processVoteButtonPush);
		resultButton.setOnAction(this::processResultButtonPush);
		saveButton.setOnAction(this::processSaveButtonPush);

		VBox root = new VBox(label, choice, buttons);
		root.setPadding(new Insets(15, 15, 15, 25));
		root.setSpacing(10);
		root.setStyle("-fx-background-color: skyblue");
		Scene scene = new Scene(root, 300, 150);
		primaryStage.setTitle("Voting " + ansObjective.toUpperCase());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void addVoteObjective() {
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
		textArea.setWrapText(true);
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
				} else {
					if (this.contains(nameList, nameAnswer.get())) {
						System.out.println("Already contains " + nameAnswer.get());
						text += "Already contains " + nameAnswer.get() + " \n";
					} else {
						nameList.add(new Candidate(nameAnswer.get()));
						System.out.println("Adding " + nameAnswer.get());
						text += "Adding " + nameAnswer.get() + "\n";
					}
				}
			}
		} else { // when there is no input
			finishAddingFlag = false;
		}
	}

	public void TypeOfVote() {
		customVote = new TextInputDialog();
		customVote.setTitle("Setting");
		customVote.setHeaderText(null);
		customVote.setContentText("The type of Vote (i.e: Name)");
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

	public void saveDialog() {
		TextInputDialog saveResult = new TextInputDialog();
		saveResult.setTitle("Saving");
		saveResult.setHeaderText(null);
		saveResult.setContentText("Enter a file name: ");
		Optional<String> fileName = saveResult.showAndWait();
		if (fileName.isPresent()) {
			try (BufferedWriter resultFile = new BufferedWriter(new FileWriter(fileName.get()))) {
				this.orderedResult();
				for (Candidate candidate : candidateList) {
					resultFile.write(candidate.getName() + ", " + candidate.getCount() + " \r\n");
				}
				this.dataSavedDialog();
			} catch (IOException e) {
				this.errorSavingDialog();
			}
		}
	}

	public void dataSavedDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Saved");
		alert.setHeaderText(null);
		alert.setContentText("Data saved");
		alert.showAndWait();
	}

	public void errorSavingDialog() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText("Saved failed: Invalid name");
		alert.showAndWait();
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
		if (choice.get() == endButton) {
			retryFlag = false;
			InputFlag = false;
		}
	}

	public void resultDialog(String contentText) {
		Alert result = new Alert(AlertType.INFORMATION);
		result.setTitle("Result");
		result.setHeaderText("The Result of your " + this.ansObjective + " poll.");
		result.setContentText(contentText);
		Label labelResult = new Label("The result are:");
		TextArea textArea = new TextArea(this.printWholeResult());
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
			current.addVote();
			System.out.println("Vote " + current.getName());
		}
	}

	public void processResultButtonPush(ActionEvent event) {
		if (event.getSource() == resultButton) {
			orderedResult();
		}
		this.resultDialog(this.findWinner());
	}

	public void processSaveButtonPush(ActionEvent event) {
		if (event.getSource() == saveButton) {
			this.saveDialog();
		}
	}

	// Extra method to print
	public void orderedResult() {
		candidateList = new Candidate[nameList.size()];
		int i = 0;
		for (Candidate a : nameList) {
			candidateList[i] = a;
			i++;
		}
		this.insertionSort(nameList); // Sort in descending order
		// Finding the winner
	}

	public String findWinner() {
		String finalResult = "";
		if (candidateList[0].compareTo(candidateList[1]) > 0) {
			finalResult = candidateList[0].getName() + ", ";
		} else {
			for (Candidate candidate : candidateList) {
				if (candidate.compareTo(candidateList[0]) == 0) {
					finalResult += candidate.getName() + ", ";
				}
			}
		}
		return finalResult += candidateList[0].getCount() + " vote";
	}

	public <T> void insertionSort(List<Candidate> list) {
		for (int i = 1; i < list.size(); i++) {
			Comparable<T> temp = (Comparable<T>) list.get(i);
			int position = i;
			while (position > 0 && temp.compareTo((T) list.get(position)) > 0) {
				list.set(position, list.get(position - 1));
				position--;
			}
			list.set(position, (Candidate) temp);
		}
	}

	public String printWholeResult() {
		String result = "";
		for (Candidate a : candidateList) {
			result += a.getName() + ", " + a.getCount() + "\n";
		}
		return result;
	}

	private boolean isNullOrEmpty(String text) {
		String textTrim = text.trim();
		if (text.isEmpty() || textTrim.length() == 0) {
			return true;
		}
		return false;
	}

	public boolean contains(List<Candidate> list, String nameCheck) {
		for (Candidate a : list) {
			if (a.getName().toLowerCase().equals(nameCheck.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		launch(args);
	}
}