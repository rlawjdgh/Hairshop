package dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import service.MyBatisConnector;
import vo.ReservationVO;
import vo.StaffVO;

public class ReservationDAO {

	static ReservationDAO single = null;
	SqlSessionFactory factory = null;

	public static ReservationDAO getInstance() {

		if (single == null) {
			single = new ReservationDAO();
		}
		
		return single;
	}
	
	public ReservationDAO() {
		factory = MyBatisConnector.getInstance().getSqlSessionFactory();
	}
	 
	public int insertReservation(ReservationVO vo) {
		
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.insert("reservation.insert_reservation", vo);
		sqlSession.close(); 
 
		return result; 
	}  
	
	public List<ReservationVO> selectReservation(int store_idx) {
			
		SqlSession sqlSession = factory.openSession(); 
		List<ReservationVO> list = sqlSession.selectList("reservation.get_reservation", store_idx);
		sqlSession.close(); 
		   
		return list;
	}
	
	public List<ReservationVO> getReservationTime(String cal_day) {
		
		SqlSession sqlSession = factory.openSession(); 
		List<ReservationVO> list = sqlSession.selectList("reservation.get_reservationTime", cal_day);
		sqlSession.close(); 
		   
		return list;
	}
	
	public int reservationComplete(int reservation_idx) {
		
		SqlSession sqlSession = factory.openSession( true );
		int result = sqlSession.update("reservation.reservation_complete", reservation_idx); 
		sqlSession.close();
		
		return result;
	} 
	
	public List<ReservationVO> getMyReservation(int login_idx) {
		
		SqlSession sqlSession = factory.openSession(); 
		List<ReservationVO> list = sqlSession.selectList("reservation.get_Myreservation", login_idx);
		sqlSession.close();  
		   
		return list;
	}

}
