import java.sql.*;

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
		if(num == 1){ // building Course Report
			try{
				String semester, year;
				int sh, sm, eh, em;
				semester = year = sh = sm = eh = em = "";
				
				//getting most recent semester
				String sql = "with t1 as (select course_id, year, semester, sec_id from Teaches"
						+ " where id=" + userId
						+ " and year=(select max(year) from Teaches"
						+ " where id=" + userId + ")),"
						+ " semfall as (select * from t1 where semester='Fall'),"
						+ " semsummer as (select * from t1 where semester='Summer'),"
						+ " semspring as (select * from t1 where semester='Spring')"
						+ " select * from semfall"
						+ " union"
						+ " select * from semsummer where not exists (select * from semfall)"
						+ " union"
						+ " select * from semspring where not exists"
						+ "((select * from semsummer) union (select * from semfall));";
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs0 = ps.executeQuery();
				
				// when data aren't exist
				if(!rs0.next()){
					System.out.println("no data found!");
					rs0.close();
					ps.close();
					return;
				}
				year = rs0.getString(2);
				semester = rs0.getString(3);
				
				System.out.println("Course report - " + year + " " + semester);
				do{
					String course_id = rs0.getString(1);
					String sec_id = rs0.getString(4);
					ResultSet rs;
					String resultString = course_id + " ";
					
					//getting course's title
					sql = "select title from Course where course_id='" + course_id + "';";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					rs.next();
					resultString += rs.getString(1) + " ";
					
					//getting other information
					sql = "select building, room_number, day, start_hr, start_min, end_hr, end_min"
							+ " from Section natural join time_slot"
							+ " where course_id='" + course_id + "'"
							+ " and sec_id='" + sec_id + "'"
							+ " and semester='" + semester + "'"
							+ " and year=" + year;
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					rs.next();
					// print in format "[building room_number] (day... sh:sm - eh:em)"
					resultString += "[" + rs.getString(1) + " " + rs.getString(2) + "] ";
					resultString += "(" + rs.getString(3);
					while(rs.next()){
						resultString += ", " + rs.getString(3);
						sh = rs.getString(4);
						sm = rs.getString(5);
						eh = rs.getString(6);
						em = rs.getString(7);
					}
					resultString += " " + sh + " : " + sm + " - ";
					resultString += eh + " : " + em + ")";
					//System.out.println(resultString);
					System.out.format("[%s %s] (%s %2d : %2d - %2d : %2d\n", building, room_number, days, sh, sm, eh, em);
				
					//Printing Student's data
					sql = "select ID, name, dept_name, grade from student natural join takes"
							+ " where course_id='" + course_id + "'"
							+ " and sec_id=" + sec_id 
							+ " and semester='" + semester + "'"
							+ " and year=" + year + ";";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					
					System.out.println("ID\tname\tdept_name\tgrade");
					while(rs.next()){
						resultString = "";
						for(int j = 0; j < 4; j++){
							resultString += rs.getString(j + 1) + "\t";
						}
						System.out.println(resultString);
					}
					rs.close();
				}while(rs0.next());
				ps.close();
				rs0.close();
			}catch(SQLException e){
				System.out.println(e);
			}
		}else if(num == 2){ // building Advisee Report
			try{
				String ID, name, dept_name, tot_cred;
				String sql = "select * from advisor, student"
						+ " where advisor.S_ID=student.ID"
						+ " and advisor.I_ID=" + userId + ";";
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				//if there are no data
				if(!rs.next()){
					System.out.println("no advisor data found!");
					ps.close();
					rs.close();
					return;
				}
				System.out.println("ID\tname\tdept_name\ttot_cred");
				do{
					ID = rs.getString(3);
					name = rs.getString(4);
					dept_name = rs.getString(5);
					tot_cred = rs.getString(6);
					System.out.format("%s\t%s\t%s\t%s\n", ID, name, dept_name, tot_cred);
				}while(rs.next());
				ps.close();
				rs.close();
			}catch(SQLException e){
				System.out.println(e);
			}
		}else{
			System.out.println("something gone wrong");
		}

	}
}
