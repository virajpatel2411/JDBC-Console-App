import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Testing {
public static void main(String[] args) {
	Scanner sc = new Scanner(System.in);
	System.out.println("Enter a date : ");
	String date_in = sc.next();
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
	java.util.Date date = null;
	try {
		date = simpleDateFormat.parse(date_in);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	String s = simpleDateFormat.format(date);
	
}
}
