package dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import service.MyBatisConnector;
import vo.GoodVO;
import vo.MemberVO;
import vo.ProductVO;

public class ProductDAO {

	static ProductDAO single = null;
	SqlSessionFactory factory = null;

	public static ProductDAO getInstance() { 

		if (single == null) {
			single = new ProductDAO();
		}
		
		return single;
	}

	public ProductDAO() {
		factory = MyBatisConnector.getInstance().getSqlSessionFactory();
	}
	
	public int addStoreProduct(ProductVO vo) {
		
		SqlSession sqlSession = factory.openSession(true);
		int result = sqlSession.insert("product.add_product", vo);
		sqlSession.close();  

		return result;  
	}
	
	public List<ProductVO> getBuyProduct(int login_idx) {
		
		SqlSession sqlSession = factory.openSession(true);
		List<ProductVO> list = sqlSession.selectList("product.get_BuyProduct", login_idx);
		sqlSession.close();  

		return list; 
	}

}
