import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * Author: Gustavo Carbone
 * Date: 05/22/2017
 */

public class Matrix {
	
	private String[][] matrix;
	
	/**
	 * Constructor - loading the matrix with all nodes from i = 1 to n = 23
	 */
	public Matrix(int rows, int cols, String file) {
		matrix = new String[rows][cols];
		Scanner col = null;
		int i = 0;
		int j = 0;
		
		try {
			col = new Scanner(new File(file));			
		} catch( FileNotFoundException a) {
			System.out.println("File not found");
		} 
		
		while(col.hasNextLine()) {
			Scanner row = new Scanner(col.nextLine());
			while(row.hasNext()) {
				matrix[i][j % cols] = row.next();
				j++;
			}
			i++;
		}
	}
	
	/**
	 * Returns the value at the specified row x column
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public String getElement(int row, int col) {
		return matrix[row][col];
	}
}