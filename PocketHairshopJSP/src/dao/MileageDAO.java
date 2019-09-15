package dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import service.MyBatisConnector;
import vo.MemberVO;
import vo.MileageVO;

public class MileageDAO {

	static MileageDAO single = null;
	SqlSessionFactory factory = null;

	public static MileageDAO getInstance() {

		if (single == null) {
			single = new MileageDAO();
		}
		
		return single;
	} 

	public MileageDAO() {
		factory = MyBatisConnector.getInstance().getSqlSessionFactory();
	}
	
	public MileageVO findUserPoint(MileageVO vo) {
		
		SqlSession sqlSession = factory.openSession();
		MileageVO vo2 = sqlSession.selectOne("mileage.select_userMileage", vo);
		sqlSession.close(); 
 
		return vo2; 
	} 
	
	public int insertPoint(MileageVO vo) {
		
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.insert("mileage.insert_mileage", vo);
		sqlSession.close(); 
 
		return result;  
	}
	
	public int updateUserPoint(MileageVO vo) {
		
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.update("mileage.update_userPoint", vo);
		sqlSession.close();    
  
		return result;
	}
	
	public int getMyPoint(int login_idx) {
		
		SqlSession sqlSession = factory.openSession();
		int point = sqlSession.selectOne("mileage.get_myPoint", login_idx);
		sqlSession.close(); 
 
		return point; 
	}
}
