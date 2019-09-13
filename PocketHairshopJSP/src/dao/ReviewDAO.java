package dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import service.MyBatisConnector;
import vo.MemberVO;
import vo.ReservationVO;
import vo.ReviewVO;
import vo.StaffVO;

public class ReviewDAO {

	static ReviewDAO single = null;
	SqlSessionFactory factory = null;

	public static ReviewDAO getInstance() {

		if (single == null) {
			single = new ReviewDAO();
		}
		
		return single;
	}
	
	public ReviewDAO() {
		factory = MyBatisConnector.getInstance().getSqlSessionFactory();
	}
	
	public int insertReview(ReviewVO vo) {
		
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.insert("review.insert_review", vo);
		sqlSession.close();  

		return result;
	}
	
	public ReviewVO checkReview(int reservation_idx) {
		 
		SqlSession sqlSession = factory.openSession(); 
		ReviewVO vo = sqlSession.selectOne("review.select_review", reservation_idx);
		sqlSession.close();

		return vo;
	}
	
	public List<ReviewVO> getStoreComment(int store_idx) {
		
		SqlSession sqlSession = factory.openSession();  
		List<ReviewVO> list = sqlSession.selectList("review.get_StoreComment", store_idx);
		sqlSession.close(); 
		 
		return list;  
	}
	
	public int updateReviewComplete(int review_idx) {
		
		SqlSession sqlSession = factory.openSession( true );
		int result = sqlSession.update("review.update_reviewComplete", review_idx); 
		sqlSession.close();
		
		return result;
	}
	
	public String findStaffName(int review_idx) {
		 
		SqlSession sqlSession = factory.openSession(); 
		String result = sqlSession.selectOne("review.find_staffName", review_idx); 
		sqlSession.close();
		
		return result;
	}

}
