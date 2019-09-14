package action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ReplyDAO;
import vo.ReplyVO;

/**
 * Servlet implementation class GetReplyAction
 */
@WebServlet("/getReply.do")
public class GetReplyAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String review_idx = request.getParameter("review_idx");
		
		if(review_idx != null) {
			
			ReplyDAO dao = ReplyDAO.getInstance();
			List<ReplyVO> list = dao.getReply(Integer.parseInt(review_idx));
			
			String arr = "[";  
			 
			for(int i = 0; i < list.size(); i++) {
				
				String str = String.format("{'staff_name':'%s', 'context':'%s'}", list.get(i).getStaff_name(), list.get(i).getContext());
				arr += str;  
				
				if(i != list.size() -1) {   
					arr += ", ";  
				}
			}   
			
			arr += "]";   
			 
			response.setCharacterEncoding("UTF-8");  
			response.setContentType("text/html; charset=UTF-8"); 
			response.getWriter().println(arr);  
		}
	}
 
}
 