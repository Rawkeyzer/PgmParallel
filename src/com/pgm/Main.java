package com.pgm;

import java.io.File;
import java.io.FileNotFoundException;

import javax.annotation.processing.Processor;

public class Main {

	private static final int PROCESSOR_AMOUNT = 2;
	private static final int MAXVAL = 255;
	private static int image[][];
	private static long totalTime;
	private static int id = 0;

	public static void main(String[] args) throws FileNotFoundException, InterruptedException {

		File dir = new File("images\\");
		File[] fileNames = dir.listFiles();

		for(File file : fileNames){
			invertFiles(file);
		}

		System.out.println("Time taken: " + totalTime);

	}

	static class Processor extends Thread {

		private int startingPoint;
		private int rowNumber;
		private int colNumber;
		private int id;

		public Processor(int id, int startingPoint, int endPointRow, int endPointCol) {
			this.id = id;
			this.startingPoint = startingPoint;
			this.rowNumber = endPointRow;
			this.colNumber = endPointCol;
		}

		public void run() {
			System.out.println("Thread " + id + " has started!");
			for (int i = startingPoint; i < rowNumber; ++i) {
				for (int j = 0; j < colNumber; ++j) {
					image[i][j] = MAXVAL - image[i][j];
				}
			}
			System.out.println("Thread " + id + " has finished.");
		}
	}

	public static void invertFiles(File filename) throws FileNotFoundException, InterruptedException {

		Pgm pgm = new Pgm();
		Processor[] processors = new Processor[PROCESSOR_AMOUNT];

		// Read image and assign to double array
		image = pgm.readPGMFile(filename);

		// Index information to pass to divide parts of array to different processors
		int lastRow = image.length;
		int lastCol = image[0].length;

		int div = lastRow / PROCESSOR_AMOUNT;

		int rowNumber = 0;
		int colNumber = lastCol / PROCESSOR_AMOUNT;

		// START MEASURING TIME
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < PROCESSOR_AMOUNT; i++) {
			if (i == PROCESSOR_AMOUNT - 1) {
				processors[i] = new Processor(id, rowNumber, lastRow, lastCol);
			} else {
				processors[i] = new Processor(id, rowNumber, rowNumber + div, lastCol);
				rowNumber = rowNumber + div;
			}
			processors[i].start();
			id++;
		}

		for (int i = 0; i < PROCESSOR_AMOUNT; i++) {
			processors[i].join();
		}

		// END MEASURING TIME
		long endTime = System.currentTimeMillis();

		int result[][] = image;

		totalTime += endTime - startTime;

		pgm.createFile(result, "results\\" + filename.getName() + "Inverse.pgm");
	}

}
