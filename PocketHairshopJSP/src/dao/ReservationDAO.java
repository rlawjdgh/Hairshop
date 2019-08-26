package dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import vo.ReservationVO;

public class ReservationDAO {

	static ReservationDAO single = null;
	SqlSessionFactory factory = null;

	public static ReservationDAO getInstance() {

		if (single == null) {
			single = new ReservationDAO();
		}
		
		return single;
	}
	
	public int insertReservation(ReservationVO vo) {
		
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.insert("reservation.insert_reservation", vo);
		sqlSession.close(); 

		return result;
	}

}
