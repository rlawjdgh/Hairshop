package dao;


import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import service.MyBatisConnector;
import vo.MemberVO;
import vo.StoreVO;

public class StoreDAO {
	
	static StoreDAO single = null;
	SqlSessionFactory factory = null;

	public static StoreDAO getInstance() {

		if (single == null) {
			single = new StoreDAO();
		}
		
		return single;
	} 

	public StoreDAO() {
		factory = MyBatisConnector.getInstance().getSqlSessionFactory();
	}
	 
	public int insertStore(StoreVO vo) {
		 
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.insert("store.insert_store", vo);
		sqlSession.close();
 
		return result;
	}
	
	public StoreVO selectOneStore_nickNameIdx(int nickName_idx) {
		
		SqlSession sqlSession = factory.openSession();
		StoreVO vo = sqlSession.selectOne("store.select_idx", nickName_idx);
		sqlSession.close();
		 
		return vo;
	}
	
	public int updatePhoto1(StoreVO vo) { 
		
		SqlSession sqlSession = factory.openSession( true );
		int result = sqlSession.update("store.update_photo1", vo); 
		sqlSession.close();
		
		return result;
		
	}
	
	public int updatePhoto2(StoreVO vo) {
		
		SqlSession sqlSession = factory.openSession( true );
		int result = sqlSession.update("store.update_photo2", vo); 
		sqlSession.close();
		
		return result; 
	}
	
	public StoreVO getStoreInfo(int nickName_idx) {
		
		SqlSession sqlSession = factory.openSession();
		StoreVO vo = sqlSession.selectOne("store.store_info", nickName_idx);
		sqlSession.close();
  
		return vo;			
	}
	
	public int changeStore(StoreVO vo) {
		
		SqlSession sqlSession = factory.openSession( true );
		int result = sqlSession.update("store.change_store", vo); 
		sqlSession.close();
		
		return result;
	}

	public List<StoreVO> getStoreAll() {
		
		SqlSession sqlSession = factory.openSession();
		List<StoreVO> list = sqlSession.selectList("store.get_storeAll");
		sqlSession.close(); 
		 
		return list;
	}
	
}
