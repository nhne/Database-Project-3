import java.sql.*;
import java.util.Scanner;

public class SchoolInfo {
	public static void main(String args[]) {
		// Open a connection to DB
		Connection conn;
		try {
			Class.forName("com.tmax.tibero.jdbc.TbDriver");
			conn = DriverManager.getConnection("jdbc:tibero:thin:@localhost:8629:" + args[0], args[1], args[2]);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			System.out.println("Could not connect to the server.");
			return;
		}

		System.out.println("Welcome\n");

		// Log in process
		String userId = null, userName = null, userType = null;
		Scanner scan = new Scanner(System.in);
		while (userId == null) {
			// Get ID & name for log in
			System.out.println("Please sign in");
			System.out.print("ID: ");
			String id = scan.nextLine();
			System.out.print("Name: ");
			String name = scan.nextLine();

			try {
				// Query
				@SuppressWarnings({"SqlNoDataSourceInspection", "SqlDialectInspection"})
				String sql = "SELECT 'Student' AS user_type FROM student " +
						"WHERE id=" + id + " AND name='" + name + "' " +
						"UNION " +
						"SELECT 'Instructor' AS user_type FROM instructor " +
						"WHERE id=" + id + " AND name='" + name + "';";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					userType = rs.getString(1);
					userId = id;
					userName = name;
				} else {
					System.out.println("Wrong authentication.");
				}
				pstmt.close();
				rs.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// Set up menu
		UserMenu userMenu;
		if (userType.equals("student"))
			userMenu = new StudentMenu(conn);
		else
			userMenu = new InstructorMenu(conn);

		while (true) {
			// Show menu
			userMenu.showMenu();

			System.out.print(">> ");
			int num = scan.nextInt();
			scan.nextLine();

			// Exit if menu number is 0
			if (num == 0) break;

			userMenu.executeMenu(num, userId, userName);
		}



		// Close the connection
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
