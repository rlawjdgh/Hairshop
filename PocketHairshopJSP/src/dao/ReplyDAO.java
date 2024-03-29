package dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import service.MyBatisConnector;
import vo.GoodVO;
import vo.MemberVO;
import vo.ReplyVO;
import vo.StaffVO;

public class ReplyDAO {

	static ReplyDAO single = null;
	SqlSessionFactory factory = null;

	public static ReplyDAO getInstance() {

		if (single == null) {
			single = new ReplyDAO();
		}
		
		return single;
	}

	public ReplyDAO() {
		factory = MyBatisConnector.getInstance().getSqlSessionFactory();
	}
	
	public int insertReply(ReplyVO vo) {
		
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.insert("reply.insert_reply", vo);
		sqlSession.close();  

		return result;
	} 
	
	public List<ReplyVO> getReply(int review_idx) {
		
		SqlSession sqlSession = factory.openSession(); 
		List<ReplyVO> list = sqlSession.selectList("reply.get_reply", review_idx);
		sqlSession.close();  
		 
		return list;
	}

}
