import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
 

public class PuzzleDriver extends Application{
	
	public static final int canvasSize = 600;
	public static int fieldSize;
	public static final Color boardColor = Color.DARKSEAGREEN;
	public static final Color stoneColor = Color.DODGERBLUE;
	public static final Color playerColor = Color.DARKCYAN;
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("PuzzleGame");
		
		
		//Init layers
		Group root = new Group();
		Canvas boardCanvas = new Canvas(canvasSize, canvasSize);
		Canvas playerCanvas = new Canvas(canvasSize, canvasSize);
		
		//Init graphics
		GraphicsContext boardGC = boardCanvas.getGraphicsContext2D();
		GraphicsContext playerGC = playerCanvas.getGraphicsContext2D();
		
		Board board = new Board("simplePuzzle.txt");
		
		loadBoard("simplePuzzle.txt", playerGC, boardGC);
		
		
		//Add canvas to group and show stage
		root.getChildren().addAll(boardCanvas, playerCanvas);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	
		
		
	}
	
	public void loadBoard(String mapName, GraphicsContext playerGC, GraphicsContext boardGC) {
		Board board = new Board(mapName);
		fieldSize = canvasSize / board.getSize();
		
		drawBoard(board, boardGC);
		drawPlayer(board, playerGC);
	}
	
	public void drawBoard(Board board, GraphicsContext gc) {
		
		gc.setStroke(Color.DARKSLATEGRAY);
		
		for (int x = 0; x < board.getSize(); x++) {
			for (int y = 0; y < board.getSize(); y++) {
				
				//Set the drawing color depending on if it's a stone or normal field. 
				if (board.getData()[x][y] == 'X') {
					gc.setFill(stoneColor);
				} else {
					gc.setFill(boardColor);
				}
				
				gc.strokeRect(x*fieldSize, y*fieldSize, fieldSize, fieldSize);
				gc.fillRect(x*fieldSize, y*fieldSize, fieldSize, fieldSize);
				
			}
		}
	}
	
	public void drawPlayer(Board board, GraphicsContext gc) {
		gc.setFill(playerColor);
		
		//Draw player as oval. The (fieldSize/12 and 6) is so the player doesn't fill the entire field. 
		gc.fillOval(board.getUserPosition().x*fieldSize+(fieldSize/12), board.getUserPosition().y*fieldSize+(fieldSize/12), 
				fieldSize-(fieldSize/6), fieldSize-(fieldSize/6));
		gc.applyEffect(new DropShadow((fieldSize/12), 0, 0, Color.DARKSLATEGRAY));
	}
	
	public void movePlayer(Board board, GraphicsContext gc, int direction) {
		
	}
}
