import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
 

public class PuzzleDriver extends Application{
	
	public static final int canvasSize = 800;
	public static int fieldSize;
	public static final Color boardColor = Color.DARKSEAGREEN;
	public static final Color stoneColor = Color.DODGERBLUE;
	public static final Color touchedColor = Color.DARKGREEN;
	public static final Color playerColor = Color.DARKCYAN;
	public static final Color endColor = Color.DARKRED;
	public static int moveSpeed; //Lower is faster
	public static int direction = -1;
	public static boolean animationInProgress = false;
	public static Group root;
	public static StackPane won;
	public static Board board;
	public static Circle player;
	public static GraphicsContext boardGC;
	public static final String[] levelList = {"winTest.txt", "simplePuzzle.txt", "puzzle1.txt", "insane.txt"};
	public static int currentLevel = 2;
	
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("PuzzleGame");

		
		//Init layers
		root = new Group();
		Canvas boardCanvas = new Canvas(canvasSize, canvasSize);
		Pane playerPane = new Pane();
		boardCanvas.setLayoutY(40);
		playerPane.setLayoutY(40);
		
		//Init graphics
		boardGC = boardCanvas.getGraphicsContext2D();
		player = new Circle();

		//Init board and player
		board = new Board(levelList[2]);
		loadBoard();
		
		//Init controls
		Pane controls = controls(playerPane);
		
		//Add canvas and player to group and show stage
		playerPane.getChildren().add(player);
		root.getChildren().addAll(boardCanvas, playerPane, controls);
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		
		
		//TODO: Make this into a method.
		//Winning pane
		won = new StackPane();
		won.setStyle("-fx-background-color: rgba(100, 100, 100, 0.8); -fx-background-radius: 10;");
		won.setPrefSize(canvasSize - canvasSize/10, canvasSize - canvasSize/10);
		won.setLayoutX(canvasSize/20);
		won.setLayoutY(canvasSize/20 + canvasSize/30);
		
		Label wonLabel = new Label("You have won!");
		wonLabel.setStyle("-fx-text-fill: whitesmoke; -fx-font-style: italic; -fx-font-weight: bold; -fx-padding: 0 0 20 0;");
		wonLabel.setFont(new Font(canvasSize/12));
		DropShadow labelShadow = new DropShadow(canvasSize/50, Color.WHITESMOKE);
		wonLabel.setEffect(labelShadow);
		won.getChildren().add(wonLabel);
		
		
		
		//Add event handler for scene. This listens for keyboard and decides which way to move. 
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case UP: direction = 0; break;
				case DOWN: direction = 1; break;
				case RIGHT: direction = 2; break;
				case LEFT: direction = 3; break;
				default: break;
				}
				if (!animationInProgress && direction != -1 && !board.isWon() && !board.isLost()) {
					movePlayer();
				}
				
			}
			
		});
		
		primaryStage.setResizable(false);
		primaryStage.setMaxHeight(canvasSize+500);
		primaryStage.setMaxWidth(canvasSize-5);
		primaryStage.show();
	
		
	}
	
	public void loadBoard() {
		fieldSize = canvasSize / board.getSize();
		moveSpeed = 1500 / board.getSize();
		//Load board
		drawBoard();
		
		//Draw player at position
		player.setRadius((fieldSize-(fieldSize/12))/2);
		player.setFill(playerColor);
		player.setCenterX(board.getUserPosition().x*fieldSize+(fieldSize/2));
		player.setCenterY(board.getUserPosition().y*fieldSize+(fieldSize/2));
		
		DropShadow playerShadow = new DropShadow();
		playerShadow.setRadius(fieldSize/12);
		player.setEffect(playerShadow);
	}
	
	public void drawBoard() {
		boardGC.setStroke(Color.DARKSLATEGRAY);
		
		for (int x = 0; x < board.getSize(); x++) {
			for (int y = 0; y < board.getSize(); y++) {
				
				//Set the drawing color depending on if it's a stone or normal field. 
				if (board.getData()[x][y] == 'X') {
					boardGC.setFill(stoneColor);
				} else if (board.getData()[x][y] == 'M') {
					boardGC.setFill(touchedColor);
				} else if (board.getData()[x][y] == 'E') {
					boardGC.setFill(endColor);
				} else {
					boardGC.setFill(boardColor);
				}
				
								
				//Draw rectangle and borders.
				boardGC.strokeRect(x*fieldSize, y*fieldSize, fieldSize, fieldSize);
				boardGC.fillRect(x*fieldSize, y*fieldSize, fieldSize, fieldSize);
				
			}
		}
		
		if (board.isWon()) {
			System.out.println("The board is won!");
		}
	}
	
	
	public void movePlayer() {		
		Point oldUserPosition = new Point(board.getUserPosition().x, board.getUserPosition().y);
		
		//The board model takes care of getting the next position for the player. 
		board.calculateRoute(direction);
		
		movePlayer(oldUserPosition);
		
		
		//reset direction. 
		direction = -1;
		
	}
	
	public void movePlayer(Point oldPosition) {
		animationInProgress = true;
		//This moves the player from last position to the new.
		TranslateTransition playerMove = new TranslateTransition(Duration.millis(
				Math.abs(((board.getUserPosition().x - oldPosition.x)+(board.getUserPosition().y - oldPosition.y))*moveSpeed)), player);
		playerMove.setByX((board.getUserPosition().x - oldPosition.x) * fieldSize);
		playerMove.setByY((board.getUserPosition().y - oldPosition.y) * fieldSize);
		playerMove.setCycleCount(0);
		
		//Make sure the destination field isn't marked before animation is finished.
		//Make sure that no other keyevents are handled before animation is finished. 
		playerMove.setOnFinished(e -> {
			drawBoard();
			if (board.isWon()) {
				root.getChildren().add(won);
			} else if (board.isLost()) {
				root.getChildren().add(lost());
			}
			animationInProgress = false;
		});
		playerMove.play();
	}
	
	public StackPane lost() {
		
		StackPane lost = new StackPane();
		lost.setId("lostPane");
		lost.setStyle("-fx-background-color: rgba(100, 100, 100, 0.8); -fx-background-radius: 10;");
		lost.setPrefSize(canvasSize - canvasSize/10, canvasSize - canvasSize/10);
		lost.setLayoutX(canvasSize/20);
		lost.setLayoutY(canvasSize/20 + canvasSize/30);
		
		Label lostLabel = new Label("You have to end on that block!");
		lostLabel.setStyle("-fx-text-fill: whitesmoke; -fx-font-style: italic; -fx-font-weight: bold; -fx-padding: 0 0 20 0;");
		lostLabel.setFont(new Font(canvasSize/20));
		DropShadow labelShadow = new DropShadow(canvasSize/50, Color.WHITESMOKE);
		lostLabel.setEffect(labelShadow);
		lost.getChildren().add(lostLabel);
		
		return lost;
	}
	
	public FlowPane controls(Pane playerPane) {
		//TODO: Comment in this.
		FlowPane controls = new FlowPane();
		controls.setPadding(new Insets(5,5,5,5));
		controls.setVgap(5);
		controls.setHgap(10);
		controls.setStyle("-fx-background-color: #2f4f4f;");
		controls.setPrefSize(canvasSize, 40);
		
		//Add buttons to control panel. 
		Button resetButton = new Button("Reset");
		resetButton.setPrefSize(100, 30);
		
		Button levelButton = new Button("Select Level");
		levelButton.setPrefSize(100, 30);
		
		controls.getChildren().addAll(resetButton, levelButton);
		
		//Reset the game when resetbutton is clicked. 
		resetButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				try 
				{
					Point oldPosition = new Point(board.getUserPosition().x, board.getUserPosition().y);
					board = new Board(levelList[currentLevel]);
					loadBoard();
					movePlayer(oldPosition);
					root.getChildren().remove(won);
					
					//removes topmost panel
					resetPanels();					
					
				} catch (IOException e) {
					System.out.println("Failed during reset");
					e.printStackTrace();
				}
			}
			
		});
		
		//Shows and unshows levelSelector. 
		levelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (!resetPanels()){
					//TODO: You can get back to the game if lost, by pressing select level twice. 
					root.getChildren().add(levelSelector(playerPane));
				}
			}
			
		});
		
		return controls;
	}
	
	//Checks if the topmost panel is levelSelector or lostPane. If it is remove it. 
	public boolean resetPanels() {
		
		//TODO: A bug occurs if the game is lost
		boolean levelSelectorOnTop = false;
		for (int i = 0; i < root.getChildren().size(); i++) {
			if (root.getChildren().get(i).getId() == "levelSelector" && !board.isLost()) {
				root.getChildren().remove(i);
				levelSelectorOnTop = true;
			}	else if (root.getChildren().get(i).getId() == "lostPane") {
				root.getChildren().remove(i);
			}
		}
				
		return levelSelectorOnTop;
		
	}
	
	public FlowPane levelSelector(Pane playerPane) {
		
		FlowPane levelSelector = new FlowPane();
		levelSelector.setId("levelSelector");
		levelSelector.setAlignment(Pos.CENTER_LEFT);
		levelSelector.setLayoutX(100);
		levelSelector.setLayoutY(100);
		levelSelector.setPadding(new Insets(10,10,10,10));
		levelSelector.setVgap(8);
		levelSelector.setHgap(8);
	    levelSelector.setPrefWrapLength(600); //Sets width.
	    levelSelector.setStyle("-fx-background-color: DAE6F3; -fx-background-radius: 10px;");
	    
	    ArrayList<StackPane> levels = new ArrayList<>();
	    for (int i = 0; i < levelList.length; i++) {
	    	levels.add(new StackPane());
	    	levels.get(i).setPrefSize(144, 144);
	    	levels.get(i).setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
	    	
	    	
	    	//Set what to do when mouse enters. 
	    	levels.get(i).setOnMouseEntered(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					StackPane pane = (StackPane) event.getSource();
					pane.setStyle("-fx-background-color: grey; -fx-background-radius: 10px;");
			        root.setCursor(Cursor.HAND); //Change cursor to hand
				}
	    		
			});
	    	
	    	//Set what to do when mouse leaves. 
	    	levels.get(i).setOnMouseExited(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					StackPane pane = (StackPane) event.getSource();
					pane.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
			        root.setCursor(Cursor.DEFAULT); //Change cursor to default

				}
	    		
	    	});
	    	
	    	//Set what to do when mouse clicks. 
	    	levels.get(i).setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					StackPane pane = (StackPane) event.getSource();
					int index = levelSelector.getChildren().indexOf(pane);
					System.out.println("New level selected: " + index);
					changeLevel(playerPane, index);
				}
	    		
	    	});
	    	
	    	//Makes labels for each level. 
	    	Label levelNumber = new Label("" + (i+1));
	    	levelNumber.setFont(new Font(50));
	    	levels.get(i).getChildren().add(levelNumber);
	    	
	    	levelSelector.getChildren().add(levels.get(i));
	    }
	    
		return levelSelector;
	}
	
	public void changeLevel(Pane playerPane, int levelNumber){
		try {
			board = new Board(levelList[levelNumber]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currentLevel = levelNumber;
		
		//In order to be able to reset, the player must be removed entirely then recreated. 
		playerPane.getChildren().remove(0);
		player = new Circle();
		playerPane.getChildren().add(player);
		
		//Load the new
		loadBoard();
		
		//Remove the latest element which should be the levelSelector. 
		root.getChildren().remove(root.getChildren().size() - 1);
		
		//If the game is won the "won" pane should be removed. 
		if (root.getChildren().contains(won)) {
			root.getChildren().remove(won);
		}
	}

}
