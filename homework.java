import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class homework {
	static long start;
	static int targetLevel=3;
	static double timeLeft;
	static double timeLimit;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		FileReader fr = null;
		start = System.nanoTime();
		// the width and height of the square board
		int n;
		// the number of fruit types
		int p;
		// the remaining time in seconds
		double t;
		char[][] board = null;
		try {
			fr = new FileReader("input.txt");
			br = new BufferedReader(fr);

			n = Integer.parseInt(br.readLine());
			p = Integer.parseInt(br.readLine());
			t = Double.parseDouble(br.readLine());
			timeLeft = t;
			timeLimit = t/5;
			if(timeLeft>=10 && n<12){
				targetLevel = 4;
			}
			if(timeLeft>=30 && n<14) {
				targetLevel =4;
			}
			if(timeLeft<10 && n>15){
				targetLevel =2;
			}
			if(timeLeft<30 && n>20) {
				targetLevel = 2;
			}
			//System.out.println(timeLimit);
			board = new char[n][n];
			int alpha = Integer.MIN_VALUE;
			int beta = Integer.MAX_VALUE;
			for (int i = 0; i < n; i++) {
				String line = br.readLine();
				for (int j = 0; j < n; j++) {
					board[i][j] = line.charAt(j);
				}
			}
			//int[] result = maxValue(board,alpha,beta,0,targetDeep);
			int res = Integer.MIN_VALUE;
			int resX = -1;
			int resY = -1;
			char[][] flag = copyMatrix(board);
			for(int i=0;i<board.length;i++){
				for(int j=0;j<board.length;j++){
					if(board[i][j] != '*' && flag[i][j]-'0'<=10){
						char[][] nextBoard = copyMatrix(board);
						int points = sink(nextBoard,nextBoard[i][j],i,j);
						points = points*points;
						sink(flag,flag[i][j],i,j);
						fallingDown(nextBoard);
						int tempScore = points+minValue(nextBoard,alpha,beta,1,start);
						if(t<5&&n>10) {
							tempScore = points;
						}
						if(tempScore>res){
							res = tempScore;
							resX = i;
							resY = j;
						}
					}
				}
			}


			sink(board,board[resX][resY],resX,resY);
			fallingDown(board);
			for(int i=0;i<board.length;i++){
				for(int j=0;j<board.length;j++){
					if(board[i][j] - 0>57) {
						board[i][j] = (char)(board[i][j]-11);
					}
				}
			}
			printToFile(board,resX,resY);
			System.out.println(targetLevel);
			System.out.println("execute time:"+ (System.nanoTime()-start)/1000000000+"s");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// int[value, X, Y ]
	private static int maxValue(char[][] board, int alpha, int beta, int level, long start){
		//if(level>=3) return new int[]{0,-1,-1};
		double pastTime = (System.nanoTime()-start)/1000000000;
		if(pastTime > timeLimit){
			timeLeft = timeLeft - pastTime;
			timeLimit = pastTime + timeLeft/5;
			targetLevel--;
			System.out.println(timeLimit);
		}
		if(level>=targetLevel) return eval(board,true);
		int decisionX = -1;
		int decisionY = -1;
		int points = -1;
		char[][] flag = new char[board.length][board.length];
		for(int i=0;i<board.length;i++){
			for(int j=0;j<board.length;j++){
				flag[i][j] = board[i][j];
			}
		}

		for(int i=0;i<board.length;i++){
			for(int j=0;j<board.length;j++){
				if(board[i][j] != '*' && flag[i][j] - '0' <= 10){
					char[][] nextBoard = copyMatrix(board);
					points = sink(nextBoard,nextBoard[i][j],i,j);
					points = points*points;
					sink(flag,flag[i][j],i,j);
					fallingDown(nextBoard);

					int val = minValue(nextBoard,alpha,beta,level+1, start);
					int curVal = val+points;

					if(curVal>alpha) {
						alpha = curVal;
						decisionX = i;
						decisionY = j;
					}
					if(alpha>=beta){
						return beta;
					}
				}
			}
		}
		if(points == -1) {return 0;}
		return alpha;
	}

	private static int minValue(char[][] board, int alpha, int beta,int level,long start){
		double pastTime = (System.nanoTime()-start)/1000000000;
		if(pastTime > timeLimit){
			timeLeft = timeLeft - pastTime;
			timeLimit = pastTime + timeLeft/5;
			targetLevel--;
		}
		if(level>=targetLevel) return eval(board,false);
		int points = -1;
		char[][] flag = new char[board.length][board.length];
		for(int i=0;i<board.length;i++){
			for(int j=0;j<board.length;j++){
				flag[i][j] = board[i][j];
			}
		}

		for(int i=0;i<board.length;i++){
			for(int j=0;j<board.length;j++){
				if(board[i][j] != '*' && flag[i][j] - '0' <=10){
					char[][] nextBoard = copyMatrix(board);
					points = sink(nextBoard,nextBoard[i][j],i,j);
					sink(flag,flag[i][j],i,j);
					fallingDown(nextBoard);
					int val = maxValue(nextBoard,alpha,beta,level+1,start);
					int curVal = val-points;
					if(curVal < beta){
						beta = curVal;
					}
					if(beta<=alpha){
						return alpha;
					}
				}
			}
		}
		if(points == -1) {return 0;}
		return beta;
	}

	// return the most points for the remaining board
	private static int eval(char[][] board, boolean isMax){
		ArrayList<Integer> list = new ArrayList<>();
		char[][] flag = new char[board.length][board.length];
		for(int i=0;i<board.length;i++){
			for(int j=0;j<board.length;j++){
				flag[i][j] = board[i][j];
			}
		}
		int count =0;
		for(int i=0;i<board.length;i++){
			for(int j=0;j<board.length;j++){
				if(board[i][j] != '*' && flag[i][j] - 0 <= 57) {
					int points = sink(board,board[i][j],i,j);
					sink(flag,flag[i][j],i,j);
					list.add(points);
				}
			}
		}
		int sum = 0;
		Collections.sort(list);
		for(int i=list.size()-1;i>=0;i--){
			if(isMax){
				if((i+1-list.size())%2==0){
					sum+=list.get(i)*list.get(i);
				}
				else{
					sum-=list.get(i)*list.get(i);
				}
			}
			else{
				if((i+1-list.size())%2==0){
					sum-=list.get(i)*list.get(i);
				}
				else{
					sum+=list.get(i)*list.get(i);
				}
			}
		}
		//System.out.println(sum);
		return sum;
	}

	private static char[][] fallingDown(char[][] board) {
		for(int i=0;i<board.length;i++){
			int p1 = board.length - 1;
			for(int p2 = board.length-1; p2>=0; p2--){
				if(board[p2][i] == '*' || board[p2][i]-0 >57) {
					continue;
				}
				board[p1][i] = board[p2][i];
				p1--;
			}
			for(int index=p1;p1>=0;p1--){
				board[p1][i]='*';
			}
		}
		return board;
	}

	private static char[][] copyMatrix(char[][] board){
		char[][] temp = new char[board.length][board.length];
		for(int i=0;i<board.length;i++){
			for(int j=0;j<board.length;j++){
				temp[i][j] = board[i][j];
			}
		}
		return temp;
	}

	private static int sink(char[][] board, char fruit, int i, int j){
		if(i<0||j<0||i>=board.length||j>=board.length||board[i][j]!=fruit) return 0;
		board[i][j] = (char)(board[i][j]+20);
		int result =  1 + sink(board, fruit, i+1, j)
			+ sink(board, fruit, i-1, j)
			+ sink(board, fruit, i, j-1)
			+ sink(board, fruit, i, j+1);
		return result;
	}

	public static void printToFile(char[][] nursey, int row, int col) {
		int length = nursey.length;
		try {
			PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
			writer.print((char)(col+'A'));
			writer.println(row+1);
			for (int i = 0; i < length; i++) {
				for (int j = 0; j < length; j++) {
					if(nursey[i][j]-'0'>57){
						writer.print('*');
					}
					else{
						writer.print(nursey[i][j]);
					}
				}
				writer.println();
			}
			writer.close();
			System.out.println("finish");
		} catch (IOException e) {
			System.out.println("fail when write into the file");
		}
	}
}
