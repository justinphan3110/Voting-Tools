//Author: Long Phan				ID: lnp26
package Main;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class Vote extends Application {
	private ArrayList<String> nameList = new ArrayList<>();			//The initial Name list
	private String[] namelist = new String[nameList.size()];
	Button voteButton, resultButton, cancelButton;
	private String current;
	private ChoiceBox<String> choice;
	int[] vote;
	private boolean flag = true;
	private TextInputDialog inputDialog;
	Optional<String> nameAnswer;
	public void start(Stage primaryStage){

		while(flag) {
		this.dialogBox();
		}
		
		namelist = nameList.toArray(namelist);					//convert ArrayList nameList into String array namelist
		 vote = new int[namelist.length];
		
		for(int i = 0; i < nameList.size(); i++) {			
			vote[i] = 0;
		}
		
		Label label = new Label("Select a candidate.");
		choice = new ChoiceBox<String>();
		choice.getItems().addAll(nameList);
		choice.setOnAction(this::processChoice);
		
		voteButton = new Button("Vote");
		resultButton = new Button("Results");
		HBox buttons = new HBox(voteButton, resultButton);
		buttons.setSpacing(20);
		Insets inset = new Insets(15,0,0,0);
		buttons.setPadding(inset);
		buttons.setAlignment(Pos.TOP_RIGHT);
		
		voteButton.setOnAction(this::processVoteButtonPush);
		resultButton.setOnAction(this::processResultButtonPush);
		
		
		VBox root = new VBox(label,choice,buttons);
		root.setPadding(new Insets(15,15,15,25));
		root.setSpacing(10);
		root.setStyle("-fx-background-color: skyblue");
		Scene scene = new Scene(root, 300,150);
		primaryStage.setTitle("Voting");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	
	}
	public void processChoice(ActionEvent event) {
		current = nameList.get(choice.getSelectionModel().getSelectedIndex());
	}
	public void processVoteButtonPush(ActionEvent event) {
		
		if(event.getSource() == voteButton) {
			for(int i = 0; i < namelist.length; i++) {
				if(namelist[i] == current) {
					vote[i]++;
					System.out.println("Vote " + namelist[i]);
				}
			}
			}
		}	
	public void processResultButtonPush(ActionEvent event) {
		
		if(event.getSource()== resultButton) {
			
		
		int mid = 0;
		String midName = "";
		boolean flag = true;
		while(flag) {
			flag = false;
		for (int i = 0; i < (namelist.length-1); i++) {
			if(vote[i] > vote[i+1]) {		
				//Arrange the result list from low to high
				mid = vote[i+1];								
				vote[i+1] =	vote[i];
				vote[i] = mid;
				//Arrange the name list according to the ordered result list
				midName = namelist[i+1];		
				namelist[i+1] = namelist[i];
				namelist[i] = midName;
				
				flag = true;
			}
		}
		}	
		System.out.println("===============================================");
		for (int i = 0; i < vote.length; i++) {
			System.out.println(vote[i] + "\t " + namelist[i]);			//Print out the whole voting result
		}
		System.out.println("===============================================");
		int voteLength = 0;
		String nameResult = "";
		if(vote.length != 0) {
		voteLength = vote.length -1;
		nameResult = namelist[voteLength];
		boolean flag2 = true;
		
		if(vote.length == 1) {
			System.out.println(namelist[0] + ": " + vote[0]);
		}
		else {
		if(vote[voteLength] != vote[voteLength-1]) {
			System.out.println(namelist[voteLength] + ", " + vote[voteLength]);
		}
		else {
			int i = 0;
			while(flag2 && i < voteLength) {
				flag2 = false;
				i++;
				if(vote[voteLength] == vote[voteLength-i]) {
					nameResult = nameResult + ", " + namelist[voteLength-i];
					flag2 = true;
					
				}
				
			}
				System.out.println(nameResult + ": " + vote[voteLength]);
		}
	}
		}
		else {
			System.out.println("No input");
		}
		}
		
	}
	public void addName(){
		Scanner scan = new Scanner(System.in);
		while(true) {
			System.out.println("Enter name: " );
			String ansName = scan.nextLine();
			if(ansName.equals("0")) {
				break;
			}
			nameList.add(ansName);
		}
	}
	
	public void dialogBox(){
			inputDialog = new TextInputDialog();
			inputDialog.setHeaderText(null);
			inputDialog.setTitle("Name");
			inputDialog.setContentText("Enter a name: ");
			nameAnswer = inputDialog.showAndWait();
			cancelButton = (Button) inputDialog.getDialogPane().lookupButton(ButtonType.CANCEL);
			inputDialog.getDialogPane().lookupButton(ButtonType.CANCEL).setDisable(true);
			
			if(nameAnswer.isPresent()) {
				if(nameAnswer.get().equals("quit")|| nameAnswer.get().equals("q")) {
					flag = false;
				}
				else {
				nameList.add(nameAnswer.get());
				System.out.println("Adding " + nameAnswer.get());
				}
			}
			else {
				flag = false;
		}
	}	
			
	

public static void main(String[] args) {
		launch(args);	
	}
}