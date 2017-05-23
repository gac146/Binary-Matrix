import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/*
 * Author: Gustavo Carbone
 * Date: 05/22/2017
 */

public class MoviesRecommender {
	
	private static final int movies = 50;
	private static final int students = 258;
	
	
	/**
	 * Execution of the program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		//Loading proper data for part "A" of this problem
		Matrix matrixRatings = new Matrix(students, movies, "hw5_movieRatings.txt");
		String[] movieTitles = load(movies, "hw5_movieTitles.txt");
		
		//Data structures to get and save the ratio of (# recommd movies / # students who saw the movie)
		double[] ratios = new double[movies];
		Map<Double, String> popularity = new HashMap<>(); 
		int[] movTotalViews = getMovViews(matrixRatings);
		int[] movRecommend = getMovRecommend(matrixRatings);
		
		//getting ratios
		for(int i=0; i < movies; i++) {
			ratios[i] = (double)movRecommend[i] / (double)movTotalViews[i];
		}
		
		//mapping ratios to movies
		for(int i=0; i < ratios.length; i++) {
			popularity.put(ratios[i], movieTitles[i]);
		}
		
		//sorting and printing ratios
		Arrays.sort(ratios);
		for(int i=0; i < ratios.length; i++) {
			System.out.println(popularity.get(ratios[i]));
		}
		
	 
	}
	
	/**
	 * Gets the number of positive ratings for all movies
	 * 
	 * @param ratings - matrix containing data for movie ratings
	 * @return integer array with number of positive ratings for each movie
	 */
	private static int[] getMovRecommend(Matrix ratings) {
		int posRatng = 0;
		int[] totRatng = new int[movies];
		
		for(int cols=0; cols < movies; cols++) {
			posRatng = 0;			
			for(int rows=0; rows < students; rows++) {
				String temp = ratings.getElement(rows, cols);
				if(temp.equals("1")) posRatng++;
			}
			totRatng[cols] = posRatng;
		}
		
		return totRatng;
	}
	
	/**
	 * Gets the number of total views of all movies
	 * 
	 * @param ratings - matrix containing data for movie ratings
	 * @return integer array with number of movie views
	 */
	private static int[] getMovViews(Matrix ratings) {
		int views = 0;
		int[] totViews = new int[movies];
		
		for(int cols=0; cols < movies; cols++) {
			views = 0;			
			for(int rows=0; rows < students; rows++) {
				String temp = ratings.getElement(rows, cols);
				if(temp.equals("1") || temp.equals("0")) views++;
			}
			totViews[cols] = views;
		}
		
		return totViews;
	}
	
	/**
	 * Loads a file into a string array. It loads line by line, not word by word.
	 * 
	 * @param size - Size of the array that will hold the file
	 * @param file - Name of the file to read from
	 * @return array with line by line file
	 */
	private static String[] load(int size, String file) {
		String[] titles = new String[size];
		Scanner reader = null;
		int counter = 0;
		
		try {
			reader = new Scanner(new File(file));
		} catch(FileNotFoundException e) {
			System.out.println("File not found");
		}
		
		while(reader.hasNextLine()) {
			titles[counter] = reader.nextLine();
			counter++;
		}
		
		return titles;
	}
}
