import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class FirstPage {

	public static boolean validate_username_unique(Connection conn, int uname) {
		ResultSet rs;
		Statement stmt;
		try {
			stmt = conn.createStatement();
			String query = "select cust_id from customer";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int uname_db = rs.getInt(1);
				if (uname_db == (uname)) {
					return false;
				}
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return true;
	}

	public static void add_city(Connection conn, int ins, int uname) {
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rt = null;
			int trip = 0;
			// String inSql = "insert into trip(city_id) values('"+ins+"') where
			// cust_id='"+uname+"'";
			// int res = stmt.executeUpdate(inSql);
			int res = 1;
			Scanner sc = new Scanner(System.in);
			if (res == 1) {
				System.out.println("Enter number of customers : ");
				int no_of_people = sc.nextInt();
				System.out.println("Enter the start date of your trip : ");
				String date_in = sc.next();
				System.out.println("Enter the end date of your trip : ");
				String date_out = sc.next();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
				java.util.Date date_i = null;
				Date date_o = null;
				try {
					date_i = simpleDateFormat.parse(date_in);
					date_o = simpleDateFormat.parse(date_out);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					System.out.println("Please enter a valid date!!!");
					e.printStackTrace();
				}
				String s_i = simpleDateFormat.format(date_i);
				String s_o = simpleDateFormat.format(date_o);
				int Ongoing = 0;
				if (s_i.equals(simpleDateFormat.format(new Date()))) {
					Ongoing = 1;
				} else {
					Ongoing = 0;
				}
				int cnt = 0;
				String tripId = "select count(*) from trip where cust_id = '" + uname + "'";
				rt = stmt.executeQuery(tripId);
				while (rt.next()) {
					cnt = rt.getInt(1);
				}
				trip = cnt + 1;
				String date_no = "insert into trip(trip_id,cust_id,start_date,end_date,people_travelling,"
						+ "Cancelled,Refund,Ongoing,city_id,total_cost) values('"+trip+"','" + uname + "','" + s_i + "','" + s_o
						+ "','" + no_of_people + "',0,0,'" + Ongoing + "','" + ins + "',0)";

				int res_sec = stmt.executeUpdate(date_no);
				if (res_sec == 1) {
					FirstPage.modeOfTransportation(conn, s_i, s_o, uname, ins, no_of_people, trip);
				}
			} else {
				System.out.println("City value not added.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	////////////////

	public static void return_journey_reservation(Connection conn, String start_date, String end_date, int uname,
			int ins, int n, int trip) {
		Scanner sc = new Scanner(System.in);
		Statement stmt;
		ResultSet rs;
		int city_name = 0;
		System.out.println("Enter the mode of Transportation you will prefer to use : ");
		System.out.println("0 - > Train");
		System.out.println("1 - > Flight");
		System.out.println("2 - > Bus");

		int choice = sc.nextInt();

		switch (choice) {
		case 0:

			String train_query_1 = "select city_id from customer where cust_id='" + uname + "'";
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(train_query_1);
				while (rs.next()) {
					city_name = rs.getInt(1);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String train_query_2 = "select train_name,no_of_stops,no_of_seats,fare,train_id from train where source='"
					+ ins + "' and destination='" + city_name + "' and " + " arrival_date='" + end_date
					+ "' and  no_of_seats>='" + n + "'";
			rs = null;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(train_query_2);
				while (rs.next()) {
					System.out.println("Train Name   : " + rs.getString(1));
					System.out.println("No. of Stops : " + rs.getInt(2));
					System.out.println("No. of Seats : " + rs.getInt(3));
					System.out.println("Fare         : " + rs.getInt(4));
					System.out.println("Train id     : " + rs.getInt(5));
				}
				System.out.println("Enter a train_id for the trip : ");
				int choic = sc.nextInt();
				System.out.println("Enter the type of seat : ");
				String seat = sc.next();
				String fetch_fare = "select fare,no_of_seats from train where train_id = '" + choic + "'";
				rs = stmt.executeQuery(fetch_fare);
				int single_ticket = 0, no_seats = 0;
				while (rs.next()) {
					single_ticket = rs.getInt(1);
					no_seats = rs.getInt(1);
				}
				/////////////////////////////////////////////////////////////////////////////////////////
				String insert_train = "insert into train_reservation(seat_number,train_id,trip_id,cust_id,train_status,total_fare,seat_type) values('"
						+ (no_seats - n) + "','" + choic + "','" + trip + "','" + uname + "',1,'" + (single_ticket) * n
						+ "','" + seat + "')";
				/////////////////////////////////////////////////////////////////////////////////////////
				int tr_res = stmt.executeUpdate(insert_train);
				if (tr_res == 1) {
					int temp = no_seats - n;
					String update = "update train set no_of_seats='" + temp + "' where train_id = '" + choic + "'";
					int result = stmt.executeUpdate(update);
					if (result == 1) {
						System.out.println("Return train reservation done");
					}

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/////////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////////

			//////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////

			break;
		case 1:

			String flight_query_1 = "select city_id from customer where cust_id='" + uname + "'";
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(flight_query_1);
				while (rs.next()) {
					city_name = rs.getInt(1);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String flight_query_2 = "select flight_service,no_of_seats,fare,flight_id from flight where source='" + ins
					+ "' and destination='" + city_name + "' and " + " arrival_date='" + end_date
					+ "' and  no_of_seats>='" + n + "'";
			rs = null;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(flight_query_2);
				while (rs.next()) {
					System.out.println("Flight Name  : " + rs.getString(1));
					System.out.println("No. of Seats : " + rs.getInt(2));
					System.out.println("Fare         : " + rs.getInt(3));
					System.out.println("Flight_id    :" + rs.getInt(4));
				}

				///////////////////////////////////////////////

				System.out.println("Enter a flight_id for the trip : ");
				int choic = sc.nextInt();
				System.out.println("Enter the type of seat : ");
				String seat = sc.next();
				String fetch_fare = "select fare,no_of_seats from flight where flight_id = " + choic;
				rs = stmt.executeQuery(fetch_fare);
				int single_ticket = 0, no_seats = 0;
				while (rs.next()) {
					single_ticket = rs.getInt(1);
					no_seats = rs.getInt(2);

				}

				String insert_flight = "insert into flight_reservation(seat_number,flight_id,trip_id,cust_id,flight_status,total_fare,seat_type) values('"
						+ (no_seats - n) + "','" + choic + "','" + trip + "','" + uname + "',1,'" + (single_ticket) * n
						+ "','" + seat + "')";

				int tr_flight = stmt.executeUpdate(insert_flight);
				if (tr_flight == 1) {
					int temp = no_seats - n;
					String update = "update flight set no_of_seats='" + temp + "' where flight_id = '" + choic + "'";
					int result = stmt.executeUpdate(update);
					if (result == 1) {
						System.out.println("Return flight reservation done");
					}
				}

				//////////////////////////////////////////////////
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 2:

			String bus_query_1 = "select city_id from customer where cust_id='" + uname + "'";
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(bus_query_1);
				while (rs.next()) {
					city_name = rs.getInt(1);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String bus_query_2 = "select service_provider,no_of_stops,no_of_seats,fare,bus_id from bus where source='"
					+ ins + "' and destination='" + city_name + "' and " + " arrival_date='" + end_date
					+ "' and  no_of_seats>='" + n + "'";
			rs = null;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(bus_query_2);
				while (rs.next()) {
					System.out.println("Bus Name     : " + rs.getString(1));
					System.out.println("No. of Stops :" + rs.getInt(2));
					System.out.println("No. of Seats : " + rs.getInt(3));
					System.out.println("Fare         : " + rs.getInt(4));
					System.out.println("Bus_id    	 :" + rs.getInt(5));
				}
				//////////////////////////////////////////////////////////

				System.out.println("Enter a bus_id for the trip : ");
				int choic = sc.nextInt();

				String fetch_fare = "select fare,no_of_seats from bus where bus_id = " + choic;
				rs = stmt.executeQuery(fetch_fare);
				int single_ticket = 0, no_seats = 0;
				while (rs.next()) {
					single_ticket = rs.getInt(1);
					no_seats = rs.getInt(2);
				}

				String insert_bus = "insert into bus_reservation(seat_number,bus_id,trip_id,cust_id,bus_status,total_fare) values('"
						+ (no_seats - n) + "','" + choic + "','" + trip + "','" + uname + "',1,'" + (single_ticket) * n
						+ "')";

				int tr_bus = stmt.executeUpdate(insert_bus);
				if (tr_bus == 1) {
					int temp = no_seats - n;
					String update = "update bus set no_of_seats='" + temp + "' where bus_id = '" + choic + "'";
					int result = stmt.executeUpdate(update);
					if (result == 1) {
						System.out.println("Return bus reservation done");
					}
				}

				////////////////////////////////////////////////////////

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		}
	}

	///////////////

	public static void modeOfTransportation(Connection conn, String start_date, String end_date, int uname, int ins,
			int n, int trip) {

		Scanner sc = new Scanner(System.in);
		Statement stmt;
		ResultSet rs;
		Date arrive = null;
		int city_name = 0;
		System.out.println("Enter the mode of Transportation you will prefer to use : ");
		System.out.println("0 - > Train");
		System.out.println("1 - > Flight");
		System.out.println("2 - > Bus");

		int choice = sc.nextInt();

		switch (choice) {
		case 0:

			String train_query_1 = "select city_id from customer where cust_id='" + uname + "'";
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(train_query_1);
				while (rs.next()) {
					city_name = rs.getInt(1);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String train_query_2 = "select train_name,no_of_stops,no_of_seats,fare,train_id from train where source='"
					+ city_name + "' and destination='" + ins + "' and " + " departure_date='" + start_date
					+ "' and  no_of_seats>='" + n + "'";
			rs = null;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(train_query_2);
				while (rs.next()) {
					System.out.println("Train Name   : " + rs.getString(1));
					System.out.println("No. of Stops : " + rs.getInt(2));
					System.out.println("No. of Seats : " + rs.getInt(3));
					System.out.println("Fare         : " + rs.getInt(4));
					System.out.println("Train id     : " + rs.getInt(5));
				}
				System.out.println("Enter a train_id for the trip : ");
				int choic = sc.nextInt();
				System.out.println("Enter the type of seat : ");
				String seat = sc.next();
				String fetch_fare = "select fare,no_of_seats,arrival_date from train where train_id = '" + choic + "'";
				rs = stmt.executeQuery(fetch_fare);
				int single_ticket = 0, no_seats = 0;

				while (rs.next()) {
					single_ticket = rs.getInt(1);
					no_seats = rs.getInt(2);
					arrive = rs.getDate(3);
				}
				/////////////////////////////////////////////////////////////////////////////////////////
				String insert_train = "insert into train_reservation(seat_number,train_id,trip_id,cust_id,train_status,total_fare,seat_type) values('"
						+ (no_seats - n) + "','" + choic + "','" + trip + "','" + uname + "',1,'" + (single_ticket) * n
						+ "','" + seat + "')";
				/////////////////////////////////////////////////////////////////////////////////////////
				int tr_res = stmt.executeUpdate(insert_train);
				if (tr_res == 1) {
					int temp = no_seats - n;
					String update = "update train set no_of_seats='" + temp + "' where train_id = '" + choic + "'";
					int result = stmt.executeUpdate(update);
					if (result == 1) {
						System.out.println("Train reservation done");
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/////////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////////

			return_journey_reservation(conn, start_date, end_date, uname, ins, n, trip);

			FirstPage.hotelRegistration(conn, uname, true, false, false, arrive, n, ins);

			//////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////

			break;
		case 1:

			String flight_query_1 = "select city_id from customer where cust_id='" + uname + "'";
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(flight_query_1);
				while (rs.next()) {
					city_name = rs.getInt(1);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String flight_query_2 = "select flight_service,no_of_seats,fare,flight_id from flight where source='"
					+ city_name + "' and destination='" + ins + "' and " + " departure_date='" + start_date
					+ "' and  no_of_seats>='" + n + "'";
			rs = null;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(flight_query_2);
				while (rs.next()) {
					System.out.println("Flight Name  : " + rs.getString(1));
					System.out.println("No. of Seats : " + rs.getInt(2));
					System.out.println("Fare         : " + rs.getInt(3));
					System.out.println("Flight_id    :" + rs.getInt(4));
				}

				///////////////////////////////////////////////

				System.out.println("Enter a flight_id for the trip : ");
				int choic = sc.nextInt();
				System.out.println("Enter the type of seat : ");
				String seat = sc.next();
				String fetch_fare = "select fare,arrival_date,no_of_seats from flight where flight_id = '" + choic
						+ "'";
				rs = stmt.executeQuery(fetch_fare);
				int single_ticket = 0, no_seats = 0;

				while (rs.next()) {
					single_ticket = rs.getInt(1);
					arrive = rs.getDate(2);
					no_seats = rs.getInt(3);
				}

				String insert_flight = "insert into flight_reservation(seat_number,flight_id,trip_id,cust_id,flight_status,total_fare,seat_type) values('"
						+ (no_seats - n) + "','" + choic + "','" + trip + "','" + uname + "',1,'" + (single_ticket) * n
						+ "','" + seat + "')";

				int tr_flight = stmt.executeUpdate(insert_flight);
				if (tr_flight == 1) {
					int temp = no_seats - n;
					String update = "update flight set no_of_seats='" + temp + "' where flight_id = '" + choic + "'";
					int result = stmt.executeUpdate(update);
					if (result == 1) {
						System.out.println("Flight reservation done");
					}
				}

				//////////////////////////////////////////////////
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return_journey_reservation(conn, start_date, end_date, uname, ins, n, trip);
			FirstPage.hotelRegistration(conn, uname, false, true, false, arrive, n, ins);

			break;
		case 2:

			String bus_query_1 = "select city_id from customer where cust_id='" + uname + "'";
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(bus_query_1);
				while (rs.next()) {
					city_name = rs.getInt(1);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String bus_query_2 = "select service_provider,no_of_stops,no_of_seats,fare,bus_id from bus where source='"
					+ city_name + "' and destination='" + ins + "' and " + " departure_date='" + start_date
					+ "' and  no_of_seats>='" + n + "'";
			rs = null;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(bus_query_2);
				while (rs.next()) {
					System.out.println("Bus Name     : " + rs.getString(1));
					System.out.println("No. of Stops :" + rs.getInt(2));
					System.out.println("No. of Seats : " + rs.getInt(3));
					System.out.println("Fare         : " + rs.getInt(4));
					System.out.println("Bus_id    	 :" + rs.getInt(5));
				}
				//////////////////////////////////////////////////////////

				System.out.println("Enter a bus_id for the trip : ");
				int choic = sc.nextInt();

				String fetch_fare = "select fare,arrival_date,no_of_seats from bus where bus_id = '" + choic + "'";
				rs = stmt.executeQuery(fetch_fare);
				int single_ticket = 0, no_seats = 0;

				while (rs.next()) {
					single_ticket = rs.getInt(1);
					arrive = rs.getDate(2);
					no_seats = rs.getInt(3);
				}

				String insert_bus = "insert into bus_reservation(seat_number,bus_id,trip_id,cust_id,bus_status,total_fare) values('"
						+ (no_seats - n) + "','" + choic + "','" + trip + "','" + uname + "',1,'" + (single_ticket) * n
						+ "')";

				int tr_bus = stmt.executeUpdate(insert_bus);
				if (tr_bus == 1) {
					int temp = no_seats - n;
					String update = "update bus set no_of_seats='" + temp + "' where bus_id = '" + choic + "'";
					int result = stmt.executeUpdate(update);
					if (result == 1) {
						System.out.println("Bus reservation done");
					}

				}

				////////////////////////////////////////////////////////

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return_journey_reservation(conn, start_date, end_date, uname, ins, n, trip);
			FirstPage.hotelRegistration(conn, uname, false, false, true, arrive, n, ins);

			break;
		}

	}

	public static void hotelRegistration(Connection conn, int uname, boolean b, boolean c, boolean d, Date arrive,
			int n, int city_id) {

		Statement stmt;
		ResultSet rs;

		System.out.println("Hotel Prices : ");

		System.out.println();
		System.out.println("Guest House - Rs. 800/room(dual sharing)");
		System.out.println();
		System.out.println("Inn  - Rs.1000/room(dual sharing)");
		System.out.println();
		System.out.println("Villa - Rs.2000/room(dual sharing)");
		System.out.println();
		System.out.println("Resort - Rs.3000/room(dual sharing)");
		System.out.println();
		System.out.println("Price for an extra person will be additional");
		System.out.println();
		System.out.println("Actual Prices will vary from city to city and season too!!!");
		System.out.println();
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter a hotel type : ");
		String type = sc.next();
		System.out.println();
		System.out.println("Hotels : No_of_rooms : Stars");
		String show_hotels = "select hotel_id,name,no_of_rooms,stars from hotel where hotel_type='" + type
				+ "' and no_of_rooms>='" + n + "' and city_id='" + city_id + "'";

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(show_hotels);
			while (rs.next()) {
				System.out.println(rs.getInt(1) + " : " + rs.getString(2) + "  :  " + rs.getInt(3) + "  :  " + rs.getInt(4));
			}
			int rooms_number = n / 2;
			int amt = 0;
			System.out.println("Enter hotel Id : ");
			int id = sc.nextInt();
			switch (type) {
			case "Resort":
				amt = 3000;
				break;
			case "Villa":
				amt = 2000;
				break;
			case "Guest House":
				amt = 800;
				break;
			case "Inn":
				amt = 1000;
				break;
			}
			int cost = rooms_number * amt;
			
			//String insertH = "insert into hotel_reservation values()";
			
			System.out.println("Your Hotel booking is confirmed.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// inn,villa,guest_house,resort;
	}

	public static void schedule(Connection conn) {
		String city_list = "select city_id,city_name from city";
		Statement stmt;
		ResultSet rs;

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(city_list);
			while (rs.next()) {
				int c_id;

				c_id = rs.getInt(1);
				String c_name = rs.getString(2);

				System.out.println(c_id + ". " + c_name);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static boolean validate(int uname, String passw, Connection conn) {
		Statement stmt;
		ResultSet rs;
		try {
			stmt = conn.createStatement();
			String checkQuery = "select cust_id,password from customer";
			rs = stmt.executeQuery(checkQuery);
			while (rs.next()) {
				int uname_db = rs.getInt(1);
				String pass = rs.getString(2);
				if (uname_db == (uname) && pass.equals(passw)) {
					return true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static String SignUpalldetails(Connection conn) {

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter your First Name : ");
		String fname = sc.next();
		System.out.println("Enter your Last Name : ");
		String lname = sc.next();
		String name = fname + " " + lname;
		FirstPage.schedule(conn);
		System.out.println("Enter the city id you live in : ");
		int city_name = sc.nextInt();
		// sc.next();
		System.out.println("Enter your State : ");
		String state_name = sc.nextLine();
		sc.next();
		System.out.println("Enter Pincode : ");
		int pin = sc.nextInt();
		System.out.println("Enter your age : ");
		int age = sc.nextInt();
		System.out.println("Enter your Contact Number : ");
		String contact_1 = sc.next();
		System.out.println("Enter your alternate Contact Number or else enter 0 : ");
		String contact_2 = sc.next();
		int uid = 0;
		System.out.println("Enter your Preferred Mode of Transpotation.\n 0 -> Train \n" + " 1 -> Flight \n 2 -> Bus");
		int pref = sc.nextInt();
		System.out.println("Enter an user pin : ");
		uid = sc.nextInt();
		while (!validate_username_unique(conn, uid)) {
			System.out.println("Enter an userid : ");
			uid = sc.nextInt();
		}
		System.out.println("Enter password : ");
		String pass = sc.next();
		int status = 0;

		String insertSignupdetails = "insert into customer(cust_id,age,l_name,f_name,login_status,"
				+ "city_id,pincode,state,password,mode_of_transportation) " + "values('" + uid + "','" + age + "','"
				+ lname + "','" + fname + "','" + status + "','" + city_name + "','" + pin + "','" + state_name + "','"
				+ pass + "','" + pref + "')";
		try {
			Statement stmt = conn.createStatement();
			int res = stmt.executeUpdate(insertSignupdetails);
			if (res == 1) {
				return name;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}

	public static void recomm_low_budget(Connection conn) {
		Scanner sc = new Scanner(System.in);
		PreparedStatement pstmt;
		ResultSet rs;
		System.out.println("Enter Budget : ");
		int budget = sc.nextInt();
		String budget_query = "select sum(total_cost)/count(*) as cost,city_name from trip natural join city group by city_name having"
				+ " sum(total_cost)/count(*) <='" + budget + "' and sum(total_cost)/count(*) > 0";
		try {
			pstmt = conn.prepareStatement(budget_query);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				System.out.println("Expected Total Cost : " + rs.getInt(1));
				System.out.println("Destination Name : " + rs.getString(2));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void listCities_most_tourist_places(Connection conn) {
		Statement stmt;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String findCities = "select city_name,count(tourist_spot) from city natural join tourist_spot group by city_name order by count(tourist_spot) desc";
			rs = stmt.executeQuery(findCities);
			System.out.println("City" + "      :      " + " No_of_Tourist_Spots");
			while (rs.next()) {
				System.out.println(rs.getString(1) + " : " + rs.getInt(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void listHotels_according_to_stars(Connection conn) {
		Scanner sc = new Scanner(System.in);
		Statement stmt;
		ResultSet rs = null;
		FirstPage.schedule(conn);
		System.out.println("Enter a city id : ");
		int c_name = sc.nextInt();
		try {
			stmt = conn.createStatement();
			String findHotels = "select name,stars from hotel where city_id='" + c_name + "' order by stars";
			rs = stmt.executeQuery(findHotels);
			System.out.println("Hotel " + "      :      " + " No_of_Stars");
			while (rs.next()) {
				System.out.println(rs.getString(1) + " : " + rs.getInt(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("Log In or Sign Up?\n1. Login\n2.Signup");
		String full_name;
		String first_name = null, last_name = null;
		Statement stmt;
		ResultSet rs;
		Scanner sc = new Scanner(System.in);
		int res = sc.nextInt();
		int uname;
		Connection conn = DBConnection.getConnection();
		try {
			conn.setSchema("dbms");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (conn != null) {
			if (res == 1) {
				System.out.println("Enter your details to Login!!!");
				System.out.println("Enter your userid : ");
				uname = sc.nextInt();
				System.out.println("Enter password : ");
				String pass = sc.next();
				if (FirstPage.validate(uname, pass, conn)) {
					String getName = "select f_name,l_name from customer where cust_id='" + uname + "'";
					try {
						stmt = conn.createStatement();
						rs = stmt.executeQuery(getName);
						while (rs.next()) {
							first_name = rs.getString(1);
							last_name = rs.getString(2);
						}
						full_name = first_name + last_name;
						System.out.println("Welcome Mr. " + full_name);
						//String insert = "insert into trip(trip_id,cust_id) values('"++"','"+uname+"')";
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} else {
				full_name = FirstPage.SignUpalldetails(conn);
				System.out.println("You have successfully signed in.\nEnter details to login!!!!");
				System.out.println("Enter your userid : ");
				uname = sc.nextInt();
				System.out.println("Enter password : ");
				String pass = sc.next();
				if (FirstPage.validate(uname, pass, conn)) {
					String getName = "select f_name,l_name from customer where cust_id='" + uname + "'";
					try {
						stmt = conn.createStatement();

						rs = stmt.executeQuery(getName);
						while (rs.next()) {
							first_name = rs.getString(1);
							last_name = rs.getString(2);
						}
						full_name = first_name + last_name;
						System.out.println("Welcome Mr. " + full_name);
						String login = "update customer set login_status=1 where cust_id='" + uname + "'";
						int response = stmt.executeUpdate(login);

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

			//////////////////////
			while (true) {
				System.out.println();
				System.out.println();
				System.out.println("Enter your choice : ");
				System.out.println("\n1. Schedule a trip. ");
				System.out.println("2.  List the trips where budget is below the entered budget. ");
				System.out.println("3.  List the cities according to the number of tourist places. ");
				System.out.println("4.  List of hotels in a particular city ranked according to stars. ");
				System.out.println("5.  List the number of restaurants according to cities. ");
				System.out.println("6.  List the restaurants in particular city. ");
				System.out.println("7.  List your past and future trips. ");
				System.out.println("8.  List the most visited cities. ");
				System.out.println("9.  List tourist spots of a particular city. ");
				System.out.println("10. Exit. ");

				int choice = sc.nextInt();
				switch (choice) {
				case 1:
					FirstPage.schedule(conn);
					System.out.println("Enter a city id you want to travel : ");
					int city_to_travel = sc.nextInt();
					FirstPage.add_city(conn, city_to_travel, uname);
					break;
				case 2:
					FirstPage.recomm_low_budget(conn);
					break;
				case 3:
					FirstPage.listCities_most_tourist_places(conn);
					break;
				case 4:
					FirstPage.listHotels_according_to_stars(conn);
					break;
				case 5:
					FirstPage.noOfRestAcctoCity(conn);
					break;
				case 6:
					FirstPage.RestinParticularCity(conn);
					break;
				case 7:
					FirstPage.ListmyTrips(conn, uname);
					break;
				case 8:
					FirstPage.MostVisitedCities(conn);
					break;
				case 9:
					FirstPage.ListTouristSpots(conn);

					break;
				case 10:
					System.exit(0);
					Statement stmt1;
					try {
						stmt1 = conn.createStatement();
						String login = "update customer set login_status=1 where cust_id='" + uname + "'";
						int response = stmt1.executeUpdate(login);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				default:
					System.out.println("Enter a valid choice!!!");
					break;
				}

			}
			/////////////////////

		}
	}

	public static void ListTouristSpots(Connection conn) {

		FirstPage.schedule(conn);
		Statement stmt;
		ResultSet rs;
		Scanner sc = new Scanner(System.in);
		System.out.println();
		try {
			stmt = conn.createStatement();
			System.out.println("Enter a city_id : ");
			int c_id = sc.nextInt();

			String query = "select place_name,timings from tourist_spot where city_id = '" + c_id + "'";

			rs = stmt.executeQuery(query);
			while (rs.next()) {
				System.out.println(rs.getString(1) + "  :  " + rs.getString(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void MostVisitedCities(Connection conn) {

		// FirstPage.schedule(conn);
		Statement stmt;
		ResultSet rs;
		Scanner sc = new Scanner(System.in);
		System.out.println();
		try {
			stmt = conn.createStatement();
			// System.out.println("Enter a city_id : ");
			// int c_id = sc.nextInt();

			String query = "select city_name,count(*) from city natural join trip group by(city_name)";

			rs = stmt.executeQuery(query);
			while (rs.next()) {
				System.out.println(rs.getString(1) + "  :  " + rs.getInt(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void ListmyTrips(Connection conn, int uname) {

		Statement stmt;
		ResultSet rs;
		try {
			stmt = conn.createStatement();
			String query = "select trip_id,city_name from city natural join trip where cust_id = '" + uname + "'";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				System.out.println(rs.getString(1) + "  :  " + rs.getString(2));
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public static void RestinParticularCity(Connection conn) {

		FirstPage.schedule(conn);
		Statement stmt;
		ResultSet rs;
		Scanner sc = new Scanner(System.in);
		System.out.println();
		try {
			stmt = conn.createStatement();
			System.out.println("Enter a city_id : ");
			int c_id = sc.nextInt();

			String query = "select restaurant_name,cost_per_person from restaurant where city_id = '" + c_id + "'";

			rs = stmt.executeQuery(query);
			while (rs.next()) {
				System.out.println(rs.getString(1) + "  :  " + rs.getInt(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void noOfRestAcctoCity(Connection conn) {

		FirstPage.schedule(conn);
		Statement stmt;
		ResultSet rs;
		Scanner sc = new Scanner(System.in);
		System.out.println();
		try {
			stmt = conn.createStatement();

			// System.out.println("Enter maximum cost of person : ");
			// int cost = sc.nextInt();

			String query = "select city_name,count(*) from city natural join restaurant group by(city_name)";

			rs = stmt.executeQuery(query);
			while (rs.next()) {
				System.out.println(rs.getString(1) + "  :  " + rs.getInt(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
