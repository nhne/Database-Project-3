import java.sql.Connection;

class InstructorMenu implements UserMenu {
	private Connection conn;
	InstructorMenu(Connection conn) {
		this.conn = conn;
	}
	@Override
	public void showMenu() {
		System.out.println("Please select instructor menu\n" +
				"1) Course report\n" +
				"2) Advisee report\n" +
				"0) Exit");
	}

	@Override
	public void executeMenu(int num, String userId, String userName) {

	}
}
