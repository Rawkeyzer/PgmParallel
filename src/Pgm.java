import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Pgm {
	private static final int MAXVAL = 255;

//	public int[][] invert(int[][] image){
//		int width = image[0].length;
//		int height = image.length;
//		int[][] result = new int[height][width];
//		for (int i = 0; i < height; ++i)
//		{
//			for (int j = 0; j < width; ++j)
//			{
//				result[i][j] = MAXVAL - image[i][j];
//			}
//		}
//		return result;
//	}

	public int[][] readPGMFile(File filename) throws FileNotFoundException{
		Scanner scanner = new Scanner(filename);
		scanner.nextLine(); // magic number
		scanner.nextLine(); //discard comment

		int width = scanner.nextInt();
		int height = scanner.nextInt();
		int max = scanner.nextInt();

		int[][] image = new int[height][width];

		for (int i = 0; i < height; ++i)
		{
			for (int j = 0; j < width; ++j)
			{
				// normalize to 255
				int value = scanner.nextInt();
				value = (int) Math.round( ((double) value) / max * MAXVAL);
				image[i][j] = value;
			}
		}
		return image;
	}

	public void createFile(int[][] image, String filename) throws FileNotFoundException
	{
		PrintWriter pw = new PrintWriter(filename);

		int width = image[0].length;
		int height = image.length;

		// magic number, width, height, and maxval
		pw.println("P2");
		pw.println(width + " " + height);
		pw.println(MAXVAL);

		// print out the data, limiting the line lengths to 70 characters
		int lineLength = 0;
		for (int i = 0; i < height; ++i)
		{
			for (int j = 0; j < width; ++j)
			{
				int value = image[i][j];

				// if we are going over 70 characters on a line,
				// start a new line
				String stringValue = "" + value;
				int currentLength = stringValue.length() + 1;
				if (currentLength + lineLength > 70)
				{
					pw.println();
					lineLength = 0;
				}
				lineLength += currentLength;
				pw.print(value + " ");
			}
		}
		pw.close();
	}
}