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
 * Servlet implementation class InsertReviewAction
 */
@WebServlet("/insertReview.do")
public class InsertReviewAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String resultStr = "";
		String login_idx = request.getParameter("login_idx");
		String reservation_idx = request.getParameter("reservation_idx");
		String store_idx = request.getParameter("store_idx");
		String staff_name = request.getParameter("staff_name");
		String context = request.getParameter("context");
		String ratingNum = request.getParameter("ratingNum");
		
		if(login_idx != null && reservation_idx != null && store_idx != null && store_idx != null && staff_name != null && context != null && ratingNum != null) {
			
			ReviewDAO dao = ReviewDAO.getInstance(); 	   
			ReviewVO vo2 = new ReviewVO();		
			 
			vo2.setLogin_idx(Integer.parseInt(login_idx));
			vo2.setReservation_idx(Integer.parseInt(reservation_idx));
			vo2.setStore_idx(Integer.parseInt(store_idx));
			vo2.setStaff_name(staff_name);
			vo2.setContext(context);
			vo2.setRating(Integer.parseInt(ratingNum));
			 
			int result = dao.insertReview(vo2);
			
			if( result >= 1 ) {
				resultStr = "success"; 
			}else {
				resultStr = "fail";
			}
			
			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
		}  
	}
}
