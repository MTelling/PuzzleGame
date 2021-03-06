import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.animation.FillTransition;
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
	
	public enum direction {
		UP, DOWN, RIGHT, LEFT, NONE;
	}
	
	public static final int CANVAS_SIZE = 800;
	public static final Color BOARD_COLOR = Color.DARKSEAGREEN;
	public static final Color STONE_COLOR = Color.DODGERBLUE;
	public static final Color TOUCHED_COLOR = Color.DARKGREEN;
	public static final Color PLAYER_COLOR = Color.DARKCYAN;
	public static final Color END_COLOR = Color.DARKRED;
	public static ArrayList<String> levelList;
	public static int fieldSize;
	public static int moveSpeed; //Lower is faster
	public static direction moveWay = direction.NONE;
	public static boolean animationInProgress = false;
	public static Group root;
	public static Board board;
	public static Circle player;
	public static int currentLevel = 0;
	
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("PuzzleGame");
		
		//TODO: Add score
		//TODO: Add highscore
		//TODO: Add more maps
		//TODO: Make mapMaker
		//TODO: Make the blocks gradually fade into the marked color. Can be done with a javafx timer. 
		//TODO: Change all styling to CSS
		//TODO: Find out how to avoid having so many public javaFX units. 
		//TODO: Change board to panes
		//TDOO: Reset when enter is pressed. 
				
		
		//Load maps.
		levelList = getMapList("");
		
		//Init layers
		root = new Group();
		Canvas boardCanvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
		Pane playerPane = new Pane();
		boardCanvas.setLayoutY(40);
		playerPane.setLayoutY(40);
		
		//Init graphics, player and board
		GraphicsContext boardGC = boardCanvas.getGraphicsContext2D();
		player = new Circle();
		board = new Board(levelList.get(currentLevel));
		loadBoard(boardGC);
		
		//Init controls
		Pane controls = controls(playerPane, boardGC);
		
		//Add canvas and player to group and show stage
		playerPane.getChildren().add(player);
		root.getChildren().addAll(boardCanvas, playerPane, controls);
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		
		//Add event handler for scene. This listens for keyboard and decides which way to move. 
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case UP: moveWay = direction.UP ; break;
				case DOWN: moveWay = direction.DOWN ; break;
				case RIGHT: moveWay = direction.RIGHT; break;
				case LEFT: moveWay = direction.LEFT; break;
				case ENTER: resetBoard(boardGC); break;
				default: break;
				}
				
				if (!animationInProgress && moveWay != direction.NONE && !board.isWon() && !board.isLost()) {
					movePlayer(boardGC);
					
					//Remove the intro. 
					for (int i = root.getChildren().size() - 1; i >= 0 ; i--) {
						if (root.getChildren().get(i).getId() == "introPane") {
							root.getChildren().remove(i);
						}
					}
				}
				
			}
			
		});
		
		//add intro. 
		root.getChildren().add(new AttentionPane(
				"Rules:\n"
				+ "You can move left, right, up and down with the arrow buttons.\n"
				+ "You have to visit all stones, but you can only stay at a stone\n"
				+ "once. Second time you visit the stone you will just pass over.\n"
				+ "If there is a red stone, you have to end there.\n"
				+ "Good luck!",
				"introPane",
				20, //font size 20
				0, //No shadow
				true //Closeable
				));
		
		//Load stylesheet. 
	    File styleSheet = new File("stylesheet.css");
	    scene.getStylesheets().clear();
	    scene.getStylesheets().add("file:///" + styleSheet.getAbsolutePath().replace("\\", "/"));
	    
		primaryStage.setResizable(false);
		primaryStage.setMaxHeight(CANVAS_SIZE+500);
		primaryStage.setMaxWidth(CANVAS_SIZE-5);
		primaryStage.show();
	
		
	}
	
	public static void loadBoard(GraphicsContext gc) {
		fieldSize = CANVAS_SIZE / board.getSize();
		moveSpeed = 1500 / board.getSize();
		//Load board
		drawBoard(gc);
		
		//Draw player at position
		player.setRadius((fieldSize-(fieldSize/12))/2);
		player.setFill(PLAYER_COLOR);
		player.setCenterX(board.getUserPosition().x*fieldSize+(fieldSize/2));
		player.setCenterY(board.getUserPosition().y*fieldSize+(fieldSize/2));
		
		DropShadow playerShadow = new DropShadow();
		playerShadow.setRadius(fieldSize/12);
		player.setEffect(playerShadow);
	}
	
	public static void drawBoard(GraphicsContext gc) {
		gc.setStroke(Color.DARKSLATEGRAY);
		
		for (int x = 0; x < board.getSize(); x++) {
			for (int y = 0; y < board.getSize(); y++) {
				
				//Set the drawing color depending on if it's a stone or normal field. 
				if (board.getData()[x][y] == 'X') {
					gc.setFill(STONE_COLOR);
				} else if (board.getData()[x][y] == 'M') {
					gc.setFill(TOUCHED_COLOR);
				} else if (board.getData()[x][y] == 'E') {
					gc.setFill(END_COLOR);
				} else {
					gc.setFill(BOARD_COLOR);
				}
				
								
				//Draw rectangle and borders.
				gc.strokeRect(x*fieldSize, y*fieldSize, fieldSize, fieldSize);
				gc.fillRect(x*fieldSize, y*fieldSize, fieldSize, fieldSize);
				
			}
		}
		
		if (board.isWon()) {
			System.out.println("The board is won!");
		}
	}
	
	//Moves the player to a new position. This method overloads movePlayer(Point, GraphicsContext).
	//This is the one that is used if the player just needs to move to new location. 
	public void movePlayer(GraphicsContext boardGC) {		
		Point oldUserPosition = new Point(board.getUserPosition().x, board.getUserPosition().y);
		
		//The board model takes care of getting the next position for the player. 
		if (board.calculateRoute(moveWay)) {
			movePlayer(oldUserPosition, boardGC);
		} else { //No move has been made. Flash red to tell the move is invalid. 
			FillTransition flash = new FillTransition(new Duration(300), player, PLAYER_COLOR, Color.rgb(255, 50, 50));
			flash.setCycleCount(2);
			flash.setAutoReverse(true);
			flash.play();
		}
		
		
		//reset direction. 
		moveWay = direction.NONE;
		
	}
	
	//Move the player to the new position in the board model from the old position. 
	public static void movePlayer(Point oldPosition, GraphicsContext boardGC) {
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
			drawBoard(boardGC);
			if (board.isWon()) {
				AttentionPane won = new AttentionPane("You have won!", "won", 66, 16, false);
				root.getChildren().add(won);
			} else if (board.isLost()) {
				root.getChildren().add(new AttentionPane("You have to end on that block!", "lostPane", 40, 5, false));
			}
			animationInProgress = false;
		});
		playerMove.play();
	}
	
	
	public FlowPane controls(Pane playerPane, GraphicsContext boardGC) {
		//TODO: Comment in this.
		FlowPane controls = new FlowPane();
		controls.setPadding(new Insets(5,5,5,5));
		controls.setVgap(5);
		controls.setHgap(10);
		controls.setStyle("-fx-background-color: #2f4f4f;");
		controls.setPrefSize(CANVAS_SIZE, 40);
		
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
				resetBoard(boardGC);
			}
		});
		
		//Shows and unshows levelSelector. 
		levelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				//If there was no levelSelector to remove, then show one. 
				if (!resetPanels()){
					root.getChildren().add(levelSelector(playerPane, boardGC));
				}
			}
			
		});
		
		return controls;
	}
	
	public static void resetBoard(GraphicsContext boardGC) {
		//A bug occurs if the reset is pressed while the player is moving. So don't allow this to happen. 
		if (!animationInProgress) {
			try {
				//Saves the current position to be able to make the transition back to start. 
				Point currentPosition = new Point(board.getUserPosition().x, board.getUserPosition().y);
				//Resets board
				board = new Board(levelList.get(currentLevel));
				loadBoard(boardGC);
				
				//Moves player to the startposition from the current position
				movePlayer(currentPosition, boardGC);
				
				//removes all panels
				resetPanels();
				
			} catch (IOException e) {
				System.out.println("Failed during reset");
				e.printStackTrace();
			}
		}
	}
		
	
	
	//Checks if the topmost panel is levelSelector or lostPane. If it is remove it. 
	public static  boolean resetPanels() {
		boolean levelSelectorOnTop = false;
		for (int i = root.getChildren().size() - 1; i >= 0 ; i--) {
			//The !board.isLost() is to ensure that you can't just make the levelSelector disappear and return to a lost game.
			if (root.getChildren().get(i).getId() == "levelSelector" && !board.isLost()) {
				root.getChildren().remove(i);
				levelSelectorOnTop = true;
			} else if (root.getChildren().get(i).getId() == "lostPane") {
				root.getChildren().remove(i);
			} else if (root.getChildren().get(i).getId() == "won") {
				root.getChildren().remove(i);
			}
		}
				
		return levelSelectorOnTop;
		
	}
	
	public FlowPane levelSelector(Pane playerPane, GraphicsContext boardGC) {
		
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
	    for (int i = 0; i < levelList.size(); i++) {
	    	levels.add(new StackPane());
	    	levels.get(i).setId(""+i);
	    	levels.get(i).setPrefSize(144, 144);
	    	//Marks the level that is currently being played with lightgrey. 
	    	if (i == currentLevel) {
	    		levels.get(i).setStyle("-fx-background-color: lightgrey; -fx-background-radius: 10px;");
	    	} else {
	    		levels.get(i).setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
	    	}
	    	
	    	
	    	
	    	//Set what to do when mouse enters. 
	    	levels.get(i).setOnMouseEntered(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					StackPane pane = (StackPane) event.getSource();
					//Nothing should happen to the current level pane. 
					if (!pane.getId().equals(""+currentLevel)){
						pane.setStyle("-fx-background-color: grey; -fx-background-radius: 10px;");
			        	root.setCursor(Cursor.HAND); //Change cursor to hand
					}
				}
	    		
			});
	    	
	    	//Set what to do when mouse leaves. 
	    	levels.get(i).setOnMouseExited(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					StackPane pane = (StackPane) event.getSource();
					//Nothing should happen to the current level pane. 
					if (!pane.getId().equals(""+currentLevel)){
						pane.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
			        	root.setCursor(Cursor.DEFAULT); //Change cursor to default
					}
				}
	    		
	    	});
	    	
	    	//Set what to do when mouse clicks. 
	    	levels.get(i).setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					StackPane pane = (StackPane) event.getSource();
					//Nothing should happen to the current level pane. 
					if (!pane.getId().equals(""+currentLevel)){
						int index = levelSelector.getChildren().indexOf(pane);
						System.out.println("New level selected: " + index);
						changeLevel(playerPane, index, boardGC);
					}
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

	
	public void changeLevel(Pane playerPane, int levelNumber, GraphicsContext boardGC){
		try {
			board = new Board(levelList.get(levelNumber));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currentLevel = levelNumber;
		
		//In order to be able to change level, the player must be removed entirely then recreated. 
		//Other wise there will be problems with the resizing and transition animation. 
		playerPane.getChildren().remove(0);
		player = new Circle();
		playerPane.getChildren().add(player);
		
		//Load the new
		loadBoard(boardGC);
		
		//Remove the latest element which should be the levelSelector. 
		root.getChildren().remove(root.getChildren().size() - 1);
		
		//If the game is won the "won" pane should be removed.
		resetPanels();
	}
	
	
	//Gets all textfiles in directory and return path to them as arrayList. 
	private static ArrayList<String> getMapList(String directory) {
		ArrayList<String> mapList = new ArrayList<>();
		try {
			Files.walk(Paths.get("" + directory)).forEach(filePath -> {
			    if (Files.isRegularFile(filePath)) {
			    	String str = ""+filePath;
			        if (str.endsWith(".txt")) {
			        	mapList.add(str);
			        }

			    }
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Couldn't get mapList");
			e.printStackTrace();
		}
		
		return mapList;
	}

}
