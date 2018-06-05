public class Row {

	private int startingPoint;
	private int rowNumber;
	private int colNumber;
	private int imageId;

	// This var was initially used in case of Poison pill termination
	private String message;

	public Row(String poisonPill){
		this.message = poisonPill;
	}

	public Row(int startingPoint, int rowNumber, int colNumber, int imageId, String message){
		this.startingPoint = startingPoint;
		this.rowNumber = rowNumber;
		this.colNumber = colNumber;
		this.imageId = imageId;
		this.message = message;
	}

	public String getMessage(){
		return this.message;
	}

	public int getStartingPoint(){
		return startingPoint;
	}

	public int getRowNumber(){
		return rowNumber;
	}

	public int getColNumber(){
		return colNumber;
	}

	public int getImageId(){
		return imageId;
	}

	public String toString(){
		return "startingPoint: " + startingPoint + " rowNumber: " + rowNumber + " colNumber: " + colNumber + " imageId: " + imageId
				+ " message: " + message;
	}

}
