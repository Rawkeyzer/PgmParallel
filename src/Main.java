import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Main {

	private static final int PROCESSOR_AMOUNT = 4;
	private static volatile long totalTime;
	private static final String POISON_PILL = "TERMINATE";
	private static ArrayList<int[][]> images = new ArrayList<>();

	public static void main(String[] args) throws FileNotFoundException, InterruptedException {

		//Grap all pgm files from dir
		File dir = new File("images\\");
		File[] fileNames = dir.listFiles();

		Pgm pgm = new Pgm();
		BlockingQueue<Row> queue = new LinkedBlockingQueue<>();

		//Convert all files to 2D arrays that contains pgm image and add it to a list
		for (File file : fileNames) {
			images.add(pgm.readPGMFile(file));
		}

		Producer producer = new Producer(queue);

		//Starting producer to start adding Rows to queue
		new Thread(producer).start();

		int numberOfTasks = (images.size() - 1) * PROCESSOR_AMOUNT;

		CountDownLatch countDownLatch = new CountDownLatch(numberOfTasks);
		ExecutorService executor = Executors.newFixedThreadPool(PROCESSOR_AMOUNT);
		Consumer consumer = new Consumer(queue, countDownLatch);

		for (int i = 1; i <= PROCESSOR_AMOUNT; i++) {
			executor.submit(consumer);
		}

		countDownLatch.await();
		System.out.println("Stopped: " + countDownLatch.getCount());
		executor.shutdown();

		// Create and convert pgm file for each image and save it in results folder
		int id = 0;
		for (int[][] image : images) {
			pgm.createFile(image, "results\\" + id + "Inverse.pgm");
			id++;
		}

		System.out.println("Time taken: " + totalTime);
	}

	// Producer class
	static class Producer implements Runnable {

		private BlockingQueue<Row> queue;

		public Producer(BlockingQueue<Row> queue) {
			this.queue = queue;
		}

		@Override
		public void run() {

			System.out.println("Producer has started!");

			// For each image divide in rows and create row object
			for (int i = 0; i < images.size(); i++) {

				// Index information to divide image into rows
				int lastRow = images.get(i).length;
				int lastCol = images.get(i)[0].length;

				int div = lastRow / PROCESSOR_AMOUNT;

				int rowNumber = 0;
				int colNumber = lastCol / PROCESSOR_AMOUNT;

				// Create new row for each block of image and add it to the queue
				for (int j = 0; j < PROCESSOR_AMOUNT; j++) {
					try {
						Row row = new Row(rowNumber, rowNumber + div, lastCol, i, "");
						rowNumber = rowNumber + div;
						queue.put(row);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println("Producer has finished!");
		}
	}

	//Consumer class
	static class Consumer implements Runnable {

		private final int MAXVAL = 255;
		private BlockingQueue<Row> queue;
		private CountDownLatch countDownLatch;

		public Consumer(BlockingQueue<Row> queue, CountDownLatch countDownLatch) {
			this.queue = queue;
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void run() {

			long threadId = Thread.currentThread().getId();

			System.out.println("Consumer " + threadId + " has started");

			// Infinite loop - will automatically break out of loop when countdown latch reaches zero
			while (true) {
				if (countDownLatch.getCount() == 0) {
					break;
				}
				try {
					Row row;
					row = queue.take();
					inverseRow(row, threadId);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					countDownLatch.countDown();
				}
			}
			System.out.println("Consumer " + threadId + " has terminated");
		}

		private synchronized void inverseRow(Row row, long threadId) {

			int image[][] = images.get(row.getImageId());

			System.out.println("INVERSING: " + row.getImageId() + " By consumer: " + threadId);

			// START MEASURING TIME
			long startTime = System.currentTimeMillis();

			// Loop over current row/block until inversed completely
			for (int i = row.getStartingPoint(); i < row.getRowNumber(); ++i) {
				for (int j = 0; j < row.getColNumber(); ++j) {
					image[i][j] = MAXVAL - image[i][j];
				}
			}

			// END MEASURING TIME
			long endTime = System.currentTimeMillis();
			totalTime += endTime - startTime;
		}

	}


}
