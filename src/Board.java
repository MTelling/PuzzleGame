import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Board {
	
	private char[][] boardData;
	private ArrayList<Point> blocks;
	private Point userPosition;
	private int boardSize;
	private Point endPosition;
	private boolean isLost;
	
	public Board(String mapName) throws IOException {
		this.blocks = new ArrayList<>();
		this.isLost = false;
		
		try {
			this.loadMap(mapName);
		} catch (IOException e) {
			System.out.println("The map couldn't be loaded. Trying simplePuzzle instead.");
			this.loadMap("simplePuzzle.txt");
		}
		
	}
	
	public void calculateRoute(int direction) {
		int horizontal = 0, vertical = 0;
		
		switch (direction) {
		case 0: vertical = -1; break; //Go up
		case 1: vertical = 1; break; //Go down
		case 2: horizontal = 1; break; //Go right
		case 3: horizontal = -1; break; //Go left
		default: break;
		}
		
		
		boolean outOfBounds = false;
		
		do {
			
			//if the next move will put it out of bounds, don't try to make it. 
			if ((this.userPosition.x == 0 && horizontal == -1) || (this.userPosition.x == this.boardSize - 1 && horizontal == 1)) {
				outOfBounds = true;
			} else if ((this.userPosition.y == 0 && vertical == -1) || (this.userPosition.y == this.boardSize - 1 && vertical == 1)) {
				outOfBounds = true;
			} else {
				this.userPosition.translate(horizontal, vertical);
			}
			
		} while(!this.blocks.contains(this.userPosition) && !outOfBounds);
		
		this.blockTouched(this.userPosition.x, this.userPosition.y);
	}
	
	
	public void loadMap(String mapName) throws IOException {
		File file = new File(mapName);
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		try {
			//Read gridsize and load variables
			int tmpBoardSize = Integer.parseInt(br.readLine());
			char[][] tmpBoardData = new char[tmpBoardSize][tmpBoardSize];
			ArrayList<Point> tmpBlocks = new ArrayList<>();
			Point tmpUserPosition = new Point(-1, -1);
			Point tmpEndPosition = new Point(-1, -1);
			
			//Load mapdata
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				for (int j = 0; j < line.length(); j++) {
					char c = line.charAt(j);
					tmpBoardData[i][j] = c;
					if (c == 'O') {
						tmpUserPosition = new Point(i,j);
					} else if (c == 'X') {
						tmpBlocks.add(new Point(i, j));
					} else if (c == 'E') {
						tmpEndPosition = new Point(i,j);
						tmpBlocks.add(new Point(i, j));
					}
					
				}
				
				i++;
			}
			
			if (i != tmpBoardSize) {
				throw new IOException();
			}
			
			if(tmpUserPosition.equals(new Point(-1,-1))) {
				throw new IOException();
			}
			
			if(!tmpEndPosition.equals(new Point(-1,-1))) {
				this.endPosition = tmpEndPosition;
			}
			
			this.userPosition = tmpUserPosition;
			this.blocks = tmpBlocks;
			this.boardSize = tmpBoardSize;
			this.boardData = tmpBoardData;
		} catch (Exception e) {
			throw new IOException();
		} finally {
			br.close();
		}
	}
	
	public String toString() {
		String board = "";
		for (int i = 0; i < this.boardSize; i++) {
			for (char c: this.boardData[i]) {	
				board += c + "\t";
			}
			board = board.trim();
			board += "\n";
		}
		board += "\n";
		for (Point p: blocks) {
			board += "(" + p.x + ", " + p.y + ")   ";
		}
		board += "\n";
		board += "User position: (" + this.userPosition.x + ", " + this.userPosition.y + ")";
		return board;
	}
	
	
	public void blockTouched(int x, int y) {
		if (this.boardData[x][y] == 'X') {
			this.boardData[x][y] = 'M';
			this.blocks.remove(new Point(x,y));
		} else if (this.boardData[x][y] == 'E') {
			//This takes care of what happens when the end is touched. 
			if (this.blocks.size() == 1) {
				this.boardData[x][y] = 'M';
				this.blocks.remove(new Point(x,y));
			} else {
				System.out.println("You have to end at this position");
				this.isLost = true;
			}
		}
	}
	
	public int getSize() {
		return this.boardSize;
	}
	
	public char[][] getData() {
		return this.boardData;
	}
	
	public Point getUserPosition() {
		return this.userPosition;
	}
	
	public ArrayList<Point> getBlocks() {
		return blocks;
	}
	
	public Point getEndPosition() {
		return this.endPosition;
	}
	
	public void setUserPosition(Point newPosition) {
		this.userPosition = newPosition;
	}
	
	public boolean isWon() {
		if (this.blocks.size() == 0) return true;
		else return false;
	}
	
	public boolean isLost() {
		return this.isLost;
	}

}
