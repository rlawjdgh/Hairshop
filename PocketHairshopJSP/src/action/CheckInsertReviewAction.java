package action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ReviewDAO;
import vo.ReviewVO;

/**
 * Servlet implementation class CheckMemberAction
 */
@WebServlet("/checkInsertReview.do") 
public class CheckInsertReviewAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		request.setCharacterEncoding("utf-8"); 
		
		String reservation_idx = request.getParameter("reservation_idx");
		  
		if(reservation_idx != null) {
			
			ReviewDAO dao = ReviewDAO.getInstance();
			ReviewVO vo = dao.checkReview(Integer.parseInt(reservation_idx));
			
			if(vo == null) {
				response.getWriter().println(String.format("[{'result':'noWrite'}]")); 
			} else { 
				response.getWriter().println(String.format("[{'result':'yesWrite'}]")); 
			}
		}
	}
} 
