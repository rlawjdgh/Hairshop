package dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import service.MyBatisConnector;
import vo.MemberVO;

public class MemberDAO {

	static MemberDAO single = null;
	SqlSessionFactory factory = null;

	public static MemberDAO getInstance() {

		if (single == null) {
			single = new MemberDAO();
		}
		
		return single;
	}

	public MemberDAO() {
		factory = MyBatisConnector.getInstance().getSqlSessionFactory();
	}
	

	public int insertMember(MemberVO vo) {

		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.insert("member.insert_member", vo);
		sqlSession.close(); 

		return result;
	}
	
	public MemberVO selectMember(int idx) { 
		  
		SqlSession sqlSession = factory.openSession();
		MemberVO vo = sqlSession.selectOne("member.select_member", idx);
		sqlSession.close();

		return vo;
	}
	
	public List<MemberVO> findName(int idx) {
		
		SqlSession sqlSession = factory.openSession();
		List<MemberVO> list = sqlSession.selectList("member.select_findName", idx);
		sqlSession.close();
		
		return list; 
	}

}
