package action;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.MemberDAO;
import dao.ReviewDAO;
import vo.MemberVO;
import vo.ReviewVO;

/**
 * Servlet implementation class GetStoreCommentAction
 */
@WebServlet("/getStoreComment.do")
public class GetStoreCommentAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		int store_idx = Integer.parseInt(request.getParameter("store_idx")); 
		
		if(store_idx != 0) {
			
			ReviewDAO rDao = ReviewDAO.getInstance();
			MemberDAO mDao = MemberDAO.getInstance();
			List<ReviewVO> rList = rDao.getStoreComment(store_idx);
			
			String arr = "[";
			 
			for(int i = 0 ; i < rList.size(); i++) {
				 
				 List<MemberVO> mList = mDao.findName(rList.get(i).getLogin_idx()); 
				 
				 for(int j = 0; j < mList.size(); j++) {
					  
					 String str = String.format("{'review_idx':'%d', 'user_name':'%s', 'staff_name':'%s', 'context':'%s', 'rating':'%d', 'complete':'%d'}", rList.get(i).getReview_idx(), mList.get(j).getNickName(), rList.get(i).getStaff_name(), rList.get(i).getContext(), rList.get(i).getRating(), rList.get(i).getComplete());
						arr += str;
						 
						if(i != mList.size() -1) {  
							arr += ", ";    
						}	 
				 }
			}  
			
			arr += "]";
			
			response.setCharacterEncoding("UTF-8");  
			response.setContentType("text/html; charset=UTF-8"); 
			response.getWriter().println(arr);  		
		}
	}

}
