import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * Author: Gustavo Carbone
 * Date: 05/23/2017 
 */

public class RgivenZ {
	
	private double[][] matrix;
	private static int r = 50;
	private static int z = 4;
	
	public RgivenZ() {
		matrix = new double[r][z];
		
		Scanner col = null;
		int i = 0;
		int j = 0;
		
		try {
			col = new Scanner(new File("hw5_probRgivenZ_init.txt"));			
		} catch( FileNotFoundException a) {
			System.out.println("File not found");
		} 
		
		while(col.hasNextLine()) {
			Scanner row = new Scanner(col.nextLine());
			while(row.hasNext()) {
				matrix[i][j % z] = Double.parseDouble(row.next());
				j++;
			}
			i++;
		}
	}
	
	public double getProb(int r, int z) {
		return matrix[r][z];
	}
	
}
