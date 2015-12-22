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
	
	public Board(String mapName) {
		this.blocks = new ArrayList<>();
		
		try {
			this.loadMap(mapName);
		} catch (IOException e) {
			System.out.println("Map failed to load");
		}
		
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
		this.boardData[x][y] = '#';
		this.blocks.remove(new Point(x,y));
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

}
