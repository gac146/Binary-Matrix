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
	private static final int z = 4;
	
	
	/**
	 * Execution of the program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		//-------------Question A------------//
		
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
		
		
		//--------Question E----------//
		RgivenZ probRgvZ = new RgivenZ();
		String[] strProbZ = load(z, "hw5_probZ_init.txt");
		double[][] probStudentsZ = new double[students][z];
		double[][] rGvnZ = new double[movies][z];
		double[] probZ = new double[z];
		
		//getting probabilities of P(Z=i) for all students
		for(int i=0; i < z; i++) {
			probZ[i] = Double.parseDouble(strProbZ[i]);
		}		
		//getting P(R=1 | Z=i)
		for(int i=0; i < movies; i++) {
			for(int j=0; j < z; j++) {
				rGvnZ[i][j] = probRgvZ.getProb(i, j);
			}
		}
		
		
		
		//--------EM algorithm---------//
		for(int counter=0; counter < 129; counter++) {
			double[][] tmp2dArr = new double[students][z];
			
			//printing log likelihood
			System.out.print("Iteration " + counter + " | ");
			System.out.println(logLikelihood(probZ, rGvnZ, matrixRatings));
			
			//calculating E-step
			for(int i=0; i < students; i++) {
				double denom = denominator(probZ, rGvnZ, i, matrixRatings);
				for(int j=0; j < z; j++) {
					double num = numerator(j, probZ[j], rGvnZ, i, matrixRatings);
					tmp2dArr[i][j] = num / denom;
					//System.out.println("denom = " + denom + " --- num = " + num);
				}
			}
			
			//updating probRgvZ
			probStudentsZ = tmp2dArr;
			
			//M-step
			//Updating P(Z=i)
			probZ = updatePzi(probStudentsZ);	
			rGvnZ = updateRgvZ(probStudentsZ, rGvnZ, matrixRatings);
		}	 
	}
	
	
	/**
	 * Updates P(Rj=1 | Z = i)
	 * 
	 * @param pZi
	 * @param rGvnZ
	 * @param ratings
	 * @return
	 */
	private static double[][] updateRgvZ(double[][] pZi, double[][] rGvnZ, Matrix ratings) {
		
		double[][] updRgvZ = new double[movies][z];
		double numFirstSum = 0;
		double numSecSum = 0;
		double denom = 0;
		
		//getting numerator
		for(int j=0; j < movies; j++) {
			for(int i=0; i < z; i++) {
				for(int t=0; t < students; t++) {
					if(ratings.getElement(t, j).equals("1")) numFirstSum += pZi[t][i];
					else if(ratings.getElement(t, j).equals("?")) numSecSum += (rGvnZ[j][i] * pZi[t][i]);
					denom += pZi[t][i];
				}
				
				//updating values
				updRgvZ[j][i] = (numFirstSum + numSecSum) / denom;
				numFirstSum = 0;
				numSecSum = 0;
				denom = 0;
			}
		}
		
		return updRgvZ;
	}
	
	/**
	 * Updates the P(Z=i) as root node
	 * 
	 * @param pZi
	 * @return returns updated array of P(Z=i)
	 */
	private static double[] updatePzi(double[][] pZi) {
		double[] updatedPzi = new double[z];
		double sumZi = 0;
		
		for(int zi=0; zi < z; zi++) {
			for(int t=0; t < students; t++) {
				sumZi += pZi[t][zi];
			}
			
			updatedPzi[zi] = (1.0 / students)*sumZi;
			sumZi = 0;
		}
		return updatedPzi;
	}
	
	/**
	 * Calculates denominator for Pi
	 * 
	 * @param probZ
	 * @param probRgvnZ
	 * @return
	 */
	private static double denominator(double[] probZ, double[][] probRgvnZ, int t, Matrix ratings) {
		double denom = 0;
		
		for(int zi=0; zi < probZ.length; zi++) {
			double tmp = 1;
			for(int row=0; row < probRgvnZ.length; row++) {
				if(ratings.getElement(t, row).equals("1") )  
					tmp *= probRgvnZ[row][zi];
				else if( ratings.getElement(t, row).equals("0"))
					tmp *= (1.0 - probRgvnZ[row][zi]);
			}
			tmp *= probZ[zi];
			denom += tmp;
			//System.out.println(denom + " -- " + zi);
		}
		
		return denom;	
	}
	
	/**
	 * Returns numerator for Pi
	 * 
	 * @param col
	 * @param zi
	 * @param matrix
	 * @return
	 */
	private static double numerator(double col, double zi, double[][] probRgvZ, int t, Matrix ratings) {
		double num = 1;
		
		for(int row=0; row < probRgvZ.length; row++) {
			if(ratings.getElement(t, row).equals("1"))
				num *= probRgvZ[row][(int)col];
			else if(ratings.getElement(t, row).equals("0"))
				num *= (1 - probRgvZ[row][(int)col]);
		}
		
		return (num * zi);		
	}
	
	/**
	 * Computes the log likelihood
	 * 
	 * @param probZ
	 * @param rGvnZ
	 * @param ratings
	 * @return
	 */
	private static double logLikelihood(double[] probZ, double[][] rGvnZ, Matrix ratings) {
		double logLk = 0;
		
		for(int t=0; t < students; t++) {
			double tmp = denominator(probZ, rGvnZ, t, ratings);
			logLk += Math.log(tmp);
		}
		
		return ( (1.0/students) * logLk);
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
