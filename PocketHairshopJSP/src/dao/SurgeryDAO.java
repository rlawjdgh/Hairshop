package dao;


import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import service.MyBatisConnector;
import vo.StoreVO;
import vo.SurgeryVO;

public class SurgeryDAO {
	
	static SurgeryDAO single = null;
	SqlSessionFactory factory = null;

	public static SurgeryDAO getInstance() {

		if (single == null) {
			single = new SurgeryDAO();
		}
		
		return single;
	} 

	public SurgeryDAO() {
		factory = MyBatisConnector.getInstance().getSqlSessionFactory();
	}
	
	public int insertItemSurgery(SurgeryVO vo) {
		
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.insert("surgery.insert_surgery", vo);
		sqlSession.close(); 
 
		return result;
	}
	
	public List<SurgeryVO> getItemSurgery(SurgeryVO vo) {
		
		SqlSession sqlSession = factory.openSession(); 
		List<SurgeryVO> list = sqlSession.selectList("surgery.get_surgery",vo);
		sqlSession.close(); 
		
		return list;
	} 
	
	public int removeItemSurgery(int surgery_idx) {
		
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.delete("surgery.delete_surgery", surgery_idx);
		sqlSession.close();  
		 
		return result;
			
	}
	
	public int updatePhoto(SurgeryVO vo) { 
		
		SqlSession sqlSession = factory.openSession( true );
		int result = sqlSession.update("surgery.update_photo", vo); 
		sqlSession.close();
		
		return result; 
		
	}
	
}
