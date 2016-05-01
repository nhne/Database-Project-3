import java.sql.Connection;

class StudentMenu implements UserMenu {
	private Connection conn;
	StudentMenu(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void showMenu() {
		System.out.println("Please select student menu\n" +
				"1) Student report\n" +
				"2) View time table\n" +
				"0) Exit");
	}

	@Override
	public void executeMenu(int num, String userId, String userName) {

	}
}
