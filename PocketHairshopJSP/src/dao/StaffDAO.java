package dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import service.MyBatisConnector;
import vo.StaffVO;
import vo.SurgeryVO;

public class StaffDAO {

	static StaffDAO single = null;
	SqlSessionFactory factory = null;

	public static StaffDAO getInstance() {

		if (single == null) {
			single = new StaffDAO();
		}
		
		return single;
	}

	public StaffDAO() {
		factory = MyBatisConnector.getInstance().getSqlSessionFactory();
	}
	
	public int insertStaff(StaffVO vo) {
		
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.insert("staff.insert_staff", vo);
		sqlSession.close();  

		return result;
	}
	
	public int updatePhoto(StaffVO vo) {
		
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.update("staff.update_photo", vo);
		sqlSession.close();   
  
		return result;
	}
	
	public List<StaffVO> getItemStaff(int nickName_idx) {
		
		SqlSession sqlSession = factory.openSession(); 
		List<StaffVO> list = sqlSession.selectList("staff.get_staff",nickName_idx);
		sqlSession.close(); 
		 
		return list;
	} 
	
	public int removeItemStaff(int staff_idx) {
		
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.delete("staff.delete_staff", staff_idx);
		sqlSession.close();  
		 
		return result;
	}
	 
	public List<StaffVO> findStaffName(int nickName_idx) { 
		
		SqlSession sqlSession = factory.openSession(); 
		List<StaffVO> list = sqlSession.selectList("staff.find_staffName",nickName_idx);
		sqlSession.close(); 
		 
		return list; 
	}

}
