package dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import service.MyBatisConnector;
import vo.GoodVO;
import vo.MemberVO;

public class GoodDAO {

	static GoodDAO single = null;
	SqlSessionFactory factory = null;

	public static GoodDAO getInstance() {

		if (single == null) {
			single = new GoodDAO();
		}
		
		return single;
	}

	public GoodDAO() {
		factory = MyBatisConnector.getInstance().getSqlSessionFactory();
	}
	
	public GoodVO checkStoreGood(GoodVO vo) {
		
		SqlSession sqlSession = factory.openSession();
		GoodVO gVO = sqlSession.selectOne("good.check_good", vo);
		sqlSession.close();

		return gVO; 
	}
	
	public int insertStoreGood(GoodVO vo) {
		
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.insert("good.insert_StoreGood", vo);
		sqlSession.close();   
 
		return result;
	}
	
	public int removeStoreGood(GoodVO vo) {
		
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.delete("good.remove_StoreGood", vo);
		sqlSession.close();  

		return result;
	}

}
